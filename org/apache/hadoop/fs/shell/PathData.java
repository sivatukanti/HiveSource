// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.fs.LocalFileSystem;
import java.io.File;
import java.net.URISyntaxException;
import org.apache.hadoop.fs.RemoteIterator;
import java.util.Arrays;
import org.apache.hadoop.fs.PathIOException;
import org.apache.hadoop.fs.PathIsDirectoryException;
import org.apache.hadoop.fs.PathIsNotDirectoryException;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.PathNotFoundException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import java.net.URI;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class PathData implements Comparable<PathData>
{
    protected final URI uri;
    public final FileSystem fs;
    public final Path path;
    public FileStatus stat;
    public boolean exists;
    private boolean inferredSchemeFromPath;
    private static final Pattern potentialUri;
    private static final Pattern windowsNonUriAbsolutePath1;
    private static final Pattern windowsNonUriAbsolutePath2;
    
    public PathData(final String pathString, final Configuration conf) throws IOException {
        this(FileSystem.get(stringToUri(pathString), conf), pathString);
    }
    
    public PathData(final URI localPath, final Configuration conf) throws IOException {
        this(FileSystem.getLocal(conf), localPath.getPath());
    }
    
    private PathData(final FileSystem fs, final String pathString) throws IOException {
        this(fs, pathString, lookupStat(fs, pathString, true));
    }
    
    private static boolean checkIfSchemeInferredFromPath(final String pathString) throws IOException {
        if (!PathData.windowsNonUriAbsolutePath1.matcher(pathString).find()) {
            return PathData.windowsNonUriAbsolutePath2.matcher(pathString).find() || (PathData.potentialUri.matcher(pathString).find() && false);
        }
        if (pathString.indexOf(47) != -1) {
            throw new IOException("Invalid path string " + pathString);
        }
        return true;
    }
    
    private PathData(final FileSystem fs, final String pathString, final FileStatus stat) throws IOException {
        this.inferredSchemeFromPath = false;
        this.fs = fs;
        this.uri = stringToUri(pathString);
        this.path = fs.makeQualified(new Path(this.uri));
        this.setStat(stat);
        if (Path.WINDOWS) {
            this.inferredSchemeFromPath = checkIfSchemeInferredFromPath(pathString);
        }
    }
    
    private static FileStatus lookupStat(final FileSystem fs, final String pathString, final boolean ignoreFNF) throws IOException {
        FileStatus status = null;
        try {
            status = fs.getFileStatus(new Path(pathString));
        }
        catch (FileNotFoundException e) {
            if (!ignoreFNF) {
                throw new PathNotFoundException(pathString);
            }
        }
        return status;
    }
    
    private void setStat(final FileStatus stat) {
        this.stat = stat;
        this.exists = (stat != null);
    }
    
    public FileStatus refreshStatus() throws IOException {
        FileStatus status = null;
        try {
            status = lookupStat(this.fs, this.toString(), false);
        }
        finally {
            this.setStat(status);
        }
        return status;
    }
    
    private void checkIfExists(final FileTypeRequirement typeRequirement) throws PathIOException {
        if (!this.exists) {
            throw new PathNotFoundException(this.toString());
        }
        if (typeRequirement == FileTypeRequirement.SHOULD_BE_DIRECTORY && !this.stat.isDirectory()) {
            throw new PathIsNotDirectoryException(this.toString());
        }
        if (typeRequirement == FileTypeRequirement.SHOULD_NOT_BE_DIRECTORY && this.stat.isDirectory()) {
            throw new PathIsDirectoryException(this.toString());
        }
    }
    
    public PathData suffix(final String extension) throws IOException {
        return new PathData(this.fs, this + extension);
    }
    
    public boolean parentExists() throws IOException {
        return this.representsDirectory() ? this.fs.exists(this.path) : this.fs.exists(this.path.getParent());
    }
    
    public boolean representsDirectory() {
        final String uriPath = this.uri.getPath();
        final String name = uriPath.substring(uriPath.lastIndexOf("/") + 1);
        return name.isEmpty() || name.equals(".") || name.equals("..");
    }
    
    public PathData[] getDirectoryContents() throws IOException {
        this.checkIfExists(FileTypeRequirement.SHOULD_BE_DIRECTORY);
        final FileStatus[] stats = this.fs.listStatus(this.path);
        final PathData[] items = new PathData[stats.length];
        for (int i = 0; i < stats.length; ++i) {
            final String child = this.getStringForChildPath(stats[i].getPath());
            items[i] = new PathData(this.fs, child, stats[i]);
        }
        Arrays.sort(items);
        return items;
    }
    
    public RemoteIterator<PathData> getDirectoryContentsIterator() throws IOException {
        this.checkIfExists(FileTypeRequirement.SHOULD_BE_DIRECTORY);
        final RemoteIterator<FileStatus> stats = this.fs.listStatusIterator(this.path);
        return new RemoteIterator<PathData>() {
            @Override
            public boolean hasNext() throws IOException {
                return stats.hasNext();
            }
            
            @Override
            public PathData next() throws IOException {
                final FileStatus file = stats.next();
                final String child = PathData.this.getStringForChildPath(file.getPath());
                return new PathData(PathData.this.fs, child, file, null);
            }
        };
    }
    
    public PathData getPathDataForChild(final PathData child) throws IOException {
        this.checkIfExists(FileTypeRequirement.SHOULD_BE_DIRECTORY);
        return new PathData(this.fs, this.getStringForChildPath(child.path));
    }
    
    private String getStringForChildPath(final Path childPath) {
        final String basename = childPath.getName();
        if (".".equals(this.toString())) {
            return basename;
        }
        final String separator = this.uri.getPath().endsWith("/") ? "" : "/";
        return uriToString(this.uri, this.inferredSchemeFromPath) + separator + basename;
    }
    
    public static PathData[] expandAsGlob(String pattern, final Configuration conf) throws IOException {
        final Path globPath = new Path(pattern);
        final FileSystem fs = globPath.getFileSystem(conf);
        final FileStatus[] stats = fs.globStatus(globPath);
        PathData[] items = null;
        if (stats == null) {
            pattern = pattern.replaceAll("\\\\(.)", "$1");
            items = new PathData[] { new PathData(fs, pattern, null) };
        }
        else {
            final URI globUri = globPath.toUri();
            PathType globType;
            if (globUri.getScheme() != null) {
                globType = PathType.HAS_SCHEME;
            }
            else if (!globUri.getPath().isEmpty() && new Path(globUri.getPath()).isAbsolute()) {
                globType = PathType.SCHEMELESS_ABSOLUTE;
            }
            else {
                globType = PathType.RELATIVE;
            }
            items = new PathData[stats.length];
            int i = 0;
            for (final FileStatus stat : stats) {
                URI matchUri = stat.getPath().toUri();
                String globMatch = null;
                switch (globType) {
                    case HAS_SCHEME: {
                        if (globUri.getAuthority() == null) {
                            matchUri = removeAuthority(matchUri);
                        }
                        globMatch = uriToString(matchUri, false);
                        break;
                    }
                    case SCHEMELESS_ABSOLUTE: {
                        globMatch = matchUri.getPath();
                        break;
                    }
                    case RELATIVE: {
                        final URI cwdUri = fs.getWorkingDirectory().toUri();
                        globMatch = relativize(cwdUri, matchUri, stat.isDirectory());
                        break;
                    }
                }
                items[i++] = new PathData(fs, globMatch, stat);
            }
        }
        Arrays.sort(items);
        return items;
    }
    
    private static URI removeAuthority(URI uri) {
        try {
            uri = new URI(uri.getScheme(), "", uri.getPath(), uri.getQuery(), uri.getFragment());
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
        return uri;
    }
    
    private static String relativize(final URI cwdUri, final URI srcUri, final boolean isDir) {
        final String uriPath = srcUri.getPath();
        final String cwdPath = cwdUri.getPath();
        if (cwdPath.equals(uriPath)) {
            return ".";
        }
        int lastSep = findLongestDirPrefix(cwdPath, uriPath, isDir);
        final StringBuilder relPath = new StringBuilder();
        if (lastSep < uriPath.length()) {
            relPath.append(uriPath.substring(lastSep + 1));
        }
        if (lastSep < cwdPath.length()) {
            while (lastSep != -1) {
                if (relPath.length() != 0) {
                    relPath.insert(0, "/");
                }
                relPath.insert(0, "..");
                lastSep = cwdPath.indexOf("/", lastSep + 1);
            }
        }
        return relPath.toString();
    }
    
    private static int findLongestDirPrefix(String cwd, String path, final boolean isDir) {
        if (!cwd.endsWith("/")) {
            cwd += "/";
        }
        if (isDir && !path.endsWith("/")) {
            path += "/";
        }
        final int len = Math.min(cwd.length(), path.length());
        int lastSep = -1;
        for (int i = 0; i < len && cwd.charAt(i) == path.charAt(i); ++i) {
            if (cwd.charAt(i) == '/') {
                lastSep = i;
            }
        }
        return lastSep;
    }
    
    @Override
    public String toString() {
        return uriToString(this.uri, this.inferredSchemeFromPath);
    }
    
    private static String uriToString(final URI uri, final boolean inferredSchemeFromPath) {
        final String scheme = uri.getScheme();
        String decodedRemainder = uri.getSchemeSpecificPart();
        if (scheme == null || inferredSchemeFromPath) {
            if (Path.isWindowsAbsolutePath(decodedRemainder, true)) {
                decodedRemainder = decodedRemainder.substring(1);
            }
            return decodedRemainder;
        }
        final StringBuilder buffer = new StringBuilder();
        buffer.append(scheme);
        buffer.append(":");
        buffer.append(decodedRemainder);
        return buffer.toString();
    }
    
    public File toFile() {
        if (!(this.fs instanceof LocalFileSystem)) {
            throw new IllegalArgumentException("Not a local path: " + this.path);
        }
        return ((LocalFileSystem)this.fs).pathToFile(this.path);
    }
    
    private static String normalizeWindowsPath(String pathString) throws IOException {
        if (!Path.WINDOWS) {
            return pathString;
        }
        final boolean slashed = pathString.length() >= 1 && pathString.charAt(0) == '/';
        if (PathData.windowsNonUriAbsolutePath1.matcher(pathString).find()) {
            if (pathString.indexOf(47) != -1) {
                throw new IOException("Invalid path string " + pathString);
            }
            pathString = pathString.replace('\\', '/');
            return "file:" + (slashed ? "" : "/") + pathString;
        }
        else {
            if (PathData.windowsNonUriAbsolutePath2.matcher(pathString).find()) {
                return "file:" + (slashed ? "" : "/") + pathString;
            }
            if (pathString.indexOf(58) == -1 && pathString.indexOf(92) != -1) {
                pathString = pathString.replace('\\', '/');
            }
            return pathString;
        }
    }
    
    private static URI stringToUri(String pathString) throws IOException {
        String scheme = null;
        String authority = null;
        int start = 0;
        pathString = normalizeWindowsPath(pathString);
        final int colon = pathString.indexOf(58);
        final int slash = pathString.indexOf(47);
        if (colon > 0 && slash == colon + 1) {
            scheme = pathString.substring(0, colon);
            start = colon + 1;
        }
        if (pathString.startsWith("//", start) && pathString.length() - start > 2) {
            start += 2;
            final int nextSlash = pathString.indexOf(47, start);
            final int authEnd = (nextSlash > 0) ? nextSlash : pathString.length();
            authority = pathString.substring(start, authEnd);
            start = authEnd;
        }
        final String path = pathString.substring(start, pathString.length());
        try {
            return new URI(scheme, authority, path, null, null);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    @Override
    public int compareTo(final PathData o) {
        return this.path.compareTo(o.path);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof PathData && this.path.equals(((PathData)o).path);
    }
    
    @Override
    public int hashCode() {
        return this.path.hashCode();
    }
    
    static {
        potentialUri = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+-.]+:");
        windowsNonUriAbsolutePath1 = Pattern.compile("^/?[a-zA-Z]:\\\\");
        windowsNonUriAbsolutePath2 = Pattern.compile("^/?[a-zA-Z]:/");
    }
    
    protected enum FileTypeRequirement
    {
        SHOULD_NOT_BE_DIRECTORY, 
        SHOULD_BE_DIRECTORY;
    }
    
    protected enum PathType
    {
        HAS_SCHEME, 
        SCHEMELESS_ABSOLUTE, 
        RELATIVE;
    }
}
