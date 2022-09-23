// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.InvalidObjectException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.lang3.StringUtils;
import java.net.URISyntaxException;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.net.URI;
import java.util.regex.Pattern;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.avro.reflect.Stringable;
import java.io.ObjectInputValidation;
import java.io.Serializable;

@Stringable
@InterfaceAudience.Public
@InterfaceStability.Stable
public class Path implements Comparable, Serializable, ObjectInputValidation
{
    public static final String SEPARATOR = "/";
    public static final char SEPARATOR_CHAR = '/';
    public static final String CUR_DIR = ".";
    public static final boolean WINDOWS;
    private static final Pattern HAS_DRIVE_LETTER_SPECIFIER;
    private static final long serialVersionUID = 708623L;
    private URI uri;
    
    void checkNotSchemeWithRelative() {
        if (this.toUri().isAbsolute() && !this.isUriPathAbsolute()) {
            throw new HadoopIllegalArgumentException("Unsupported name: has scheme but relative path-part");
        }
    }
    
    void checkNotRelative() {
        if (!this.isAbsolute() && this.toUri().getScheme() == null) {
            throw new HadoopIllegalArgumentException("Path is relative");
        }
    }
    
    public static Path getPathWithoutSchemeAndAuthority(final Path path) {
        final Path newPath = path.isUriPathAbsolute() ? new Path(null, null, path.toUri().getPath()) : path;
        return newPath;
    }
    
    public Path(final String parent, final String child) {
        this(new Path(parent), new Path(child));
    }
    
    public Path(final Path parent, final String child) {
        this(parent, new Path(child));
    }
    
    public Path(final String parent, final Path child) {
        this(new Path(parent), child);
    }
    
    public Path(final Path parent, final Path child) {
        URI parentUri = parent.uri;
        final String parentPath = parentUri.getPath();
        if (!parentPath.equals("/") && !parentPath.isEmpty()) {
            try {
                parentUri = new URI(parentUri.getScheme(), parentUri.getAuthority(), parentUri.getPath() + "/", null, parentUri.getFragment());
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
        final URI resolved = parentUri.resolve(child.uri);
        this.initialize(resolved.getScheme(), resolved.getAuthority(), resolved.getPath(), resolved.getFragment());
    }
    
    private void checkPathArg(final String path) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException("Can not create a Path from a null string");
        }
        if (path.length() == 0) {
            throw new IllegalArgumentException("Can not create a Path from an empty string");
        }
    }
    
    public Path(String pathString) throws IllegalArgumentException {
        this.checkPathArg(pathString);
        if (hasWindowsDrive(pathString) && pathString.charAt(0) != '/') {
            pathString = "/" + pathString;
        }
        String scheme = null;
        String authority = null;
        int start = 0;
        final int colon = pathString.indexOf(58);
        final int slash = pathString.indexOf(47);
        if (colon != -1 && (slash == -1 || colon < slash)) {
            scheme = pathString.substring(0, colon);
            start = colon + 1;
        }
        if (pathString.startsWith("//", start) && pathString.length() - start > 2) {
            final int nextSlash = pathString.indexOf(47, start + 2);
            final int authEnd = (nextSlash > 0) ? nextSlash : pathString.length();
            authority = pathString.substring(start + 2, authEnd);
            start = authEnd;
        }
        final String path = pathString.substring(start, pathString.length());
        this.initialize(scheme, authority, path, null);
    }
    
    public Path(final URI aUri) {
        this.uri = aUri.normalize();
    }
    
    public Path(final String scheme, final String authority, String path) {
        this.checkPathArg(path);
        if (hasWindowsDrive(path) && path.charAt(0) != '/') {
            path = "/" + path;
        }
        if (!Path.WINDOWS && path.charAt(0) != '/') {
            path = "./" + path;
        }
        this.initialize(scheme, authority, path, null);
    }
    
    private void initialize(final String scheme, final String authority, final String path, final String fragment) {
        try {
            this.uri = new URI(scheme, authority, normalizePath(scheme, path), null, fragment).normalize();
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static Path mergePaths(final Path path1, final Path path2) {
        String path2Str = path2.toUri().getPath();
        path2Str = path2Str.substring(startPositionWithoutWindowsDrive(path2Str));
        return new Path(path1.toUri().getScheme(), path1.toUri().getAuthority(), path1.toUri().getPath() + path2Str);
    }
    
    private static String normalizePath(final String scheme, String path) {
        path = StringUtils.replace(path, "//", "/");
        if (Path.WINDOWS && (hasWindowsDrive(path) || scheme == null || scheme.isEmpty() || scheme.equals("file"))) {
            path = StringUtils.replace(path, "\\", "/");
        }
        final int minLength = startPositionWithoutWindowsDrive(path) + 1;
        if (path.length() > minLength && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
    
    private static boolean hasWindowsDrive(final String path) {
        return Path.WINDOWS && Path.HAS_DRIVE_LETTER_SPECIFIER.matcher(path).find();
    }
    
    private static int startPositionWithoutWindowsDrive(final String path) {
        if (hasWindowsDrive(path)) {
            return (path.charAt(0) == '/') ? 3 : 2;
        }
        return 0;
    }
    
    public static boolean isWindowsAbsolutePath(final String pathString, final boolean slashed) {
        final int start = startPositionWithoutWindowsDrive(pathString);
        return start > 0 && pathString.length() > start && (pathString.charAt(start) == '/' || pathString.charAt(start) == '\\');
    }
    
    public URI toUri() {
        return this.uri;
    }
    
    public FileSystem getFileSystem(final Configuration conf) throws IOException {
        return FileSystem.get(this.toUri(), conf);
    }
    
    public boolean isAbsoluteAndSchemeAuthorityNull() {
        return this.isUriPathAbsolute() && this.uri.getScheme() == null && this.uri.getAuthority() == null;
    }
    
    public boolean isUriPathAbsolute() {
        final int start = startPositionWithoutWindowsDrive(this.uri.getPath());
        return this.uri.getPath().startsWith("/", start);
    }
    
    public boolean isAbsolute() {
        return this.isUriPathAbsolute();
    }
    
    public boolean isRoot() {
        return this.getParent() == null;
    }
    
    public String getName() {
        final String path = this.uri.getPath();
        final int slash = path.lastIndexOf("/");
        return path.substring(slash + 1);
    }
    
    public Path getParent() {
        final String path = this.uri.getPath();
        final int lastSlash = path.lastIndexOf(47);
        final int start = startPositionWithoutWindowsDrive(path);
        if (path.length() == start || (lastSlash == start && path.length() == start + 1)) {
            return null;
        }
        String parent;
        if (lastSlash == -1) {
            parent = ".";
        }
        else {
            parent = path.substring(0, (lastSlash == start) ? (start + 1) : lastSlash);
        }
        return new Path(this.uri.getScheme(), this.uri.getAuthority(), parent);
    }
    
    public Path suffix(final String suffix) {
        return new Path(this.getParent(), this.getName() + suffix);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        if (this.uri.getScheme() != null) {
            buffer.append(this.uri.getScheme());
            buffer.append(":");
        }
        if (this.uri.getAuthority() != null) {
            buffer.append("//");
            buffer.append(this.uri.getAuthority());
        }
        if (this.uri.getPath() != null) {
            String path = this.uri.getPath();
            if (path.indexOf(47) == 0 && hasWindowsDrive(path) && this.uri.getScheme() == null && this.uri.getAuthority() == null) {
                path = path.substring(1);
            }
            buffer.append(path);
        }
        if (this.uri.getFragment() != null) {
            buffer.append("#");
            buffer.append(this.uri.getFragment());
        }
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Path)) {
            return false;
        }
        final Path that = (Path)o;
        return this.uri.equals(that.uri);
    }
    
    @Override
    public int hashCode() {
        return this.uri.hashCode();
    }
    
    @Override
    public int compareTo(final Object o) {
        final Path that = (Path)o;
        return this.uri.compareTo(that.uri);
    }
    
    public int depth() {
        final String path = this.uri.getPath();
        int depth = 0;
        for (int slash = (path.length() == 1 && path.charAt(0) == '/') ? -1 : 0; slash != -1; slash = path.indexOf("/", slash + 1)) {
            ++depth;
        }
        return depth;
    }
    
    @Deprecated
    public Path makeQualified(final FileSystem fs) {
        return this.makeQualified(fs.getUri(), fs.getWorkingDirectory());
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
    public Path makeQualified(final URI defaultUri, final Path workingDir) {
        Path path = this;
        if (!this.isAbsolute()) {
            path = new Path(workingDir, this);
        }
        final URI pathUri = path.toUri();
        String scheme = pathUri.getScheme();
        String authority = pathUri.getAuthority();
        final String fragment = pathUri.getFragment();
        if (scheme != null && (authority != null || defaultUri.getAuthority() == null)) {
            return path;
        }
        if (scheme == null) {
            scheme = defaultUri.getScheme();
        }
        if (authority == null) {
            authority = defaultUri.getAuthority();
            if (authority == null) {
                authority = "";
            }
        }
        URI newUri = null;
        try {
            newUri = new URI(scheme, authority, normalizePath(scheme, pathUri.getPath()), null, fragment);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        return new Path(newUri);
    }
    
    @Override
    public void validateObject() throws InvalidObjectException {
        if (this.uri == null) {
            throw new InvalidObjectException("No URI in deserialized Path");
        }
    }
    
    static {
        WINDOWS = System.getProperty("os.name").startsWith("Windows");
        HAS_DRIVE_LETTER_SPECIFIER = Pattern.compile("^/?[a-zA-Z]:");
    }
}
