// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Arrays;
import org.apache.htrace.core.TraceScope;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.htrace.core.Tracer;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class Globber
{
    public static final Logger LOG;
    private final FileSystem fs;
    private final FileContext fc;
    private final Path pathPattern;
    private final PathFilter filter;
    private final Tracer tracer;
    
    public Globber(final FileSystem fs, final Path pathPattern, final PathFilter filter) {
        this.fs = fs;
        this.fc = null;
        this.pathPattern = pathPattern;
        this.filter = filter;
        this.tracer = FsTracer.get(fs.getConf());
    }
    
    public Globber(final FileContext fc, final Path pathPattern, final PathFilter filter) {
        this.fs = null;
        this.fc = fc;
        this.pathPattern = pathPattern;
        this.filter = filter;
        this.tracer = fc.getTracer();
    }
    
    private FileStatus getFileStatus(final Path path) throws IOException {
        try {
            if (this.fs != null) {
                return this.fs.getFileStatus(path);
            }
            return this.fc.getFileStatus(path);
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }
    
    private FileStatus[] listStatus(final Path path) throws IOException {
        try {
            if (this.fs != null) {
                return this.fs.listStatus(path);
            }
            return this.fc.util().listStatus(path);
        }
        catch (FileNotFoundException e) {
            return new FileStatus[0];
        }
    }
    
    private Path fixRelativePart(final Path path) {
        if (this.fs != null) {
            return this.fs.fixRelativePart(path);
        }
        return this.fc.fixRelativePart(path);
    }
    
    private static String unescapePathComponent(final String name) {
        return name.replaceAll("\\\\(.)", "$1");
    }
    
    private static List<String> getPathComponents(final String path) throws IOException {
        final ArrayList<String> ret = new ArrayList<String>();
        for (final String component : path.split("/")) {
            if (!component.isEmpty()) {
                ret.add(component);
            }
        }
        return ret;
    }
    
    private String schemeFromPath(final Path path) throws IOException {
        String scheme = path.toUri().getScheme();
        if (scheme == null) {
            if (this.fs != null) {
                scheme = this.fs.getUri().getScheme();
            }
            else {
                scheme = this.fc.getFSofPath(this.fc.fixRelativePart(path)).getUri().getScheme();
            }
        }
        return scheme;
    }
    
    private String authorityFromPath(final Path path) throws IOException {
        String authority = path.toUri().getAuthority();
        if (authority == null) {
            if (this.fs != null) {
                authority = this.fs.getUri().getAuthority();
            }
            else {
                authority = this.fc.getFSofPath(this.fc.fixRelativePart(path)).getUri().getAuthority();
            }
        }
        return authority;
    }
    
    public FileStatus[] glob() throws IOException {
        final TraceScope scope = this.tracer.newScope("Globber#glob");
        scope.addKVAnnotation("pattern", this.pathPattern.toUri().getPath());
        try {
            return this.doGlob();
        }
        finally {
            scope.close();
        }
    }
    
    private FileStatus[] doGlob() throws IOException {
        final String scheme = this.schemeFromPath(this.pathPattern);
        final String authority = this.authorityFromPath(this.pathPattern);
        final String pathPatternString = this.pathPattern.toUri().getPath();
        final List<String> flattenedPatterns = GlobExpander.expand(pathPatternString);
        final ArrayList<FileStatus> results = new ArrayList<FileStatus>(flattenedPatterns.size());
        boolean sawWildcard = false;
        for (final String flatPattern : flattenedPatterns) {
            final Path absPattern = this.fixRelativePart(new Path(flatPattern.isEmpty() ? "." : flatPattern));
            final List<String> components = getPathComponents(absPattern.toUri().getPath());
            ArrayList<FileStatus> candidates = new ArrayList<FileStatus>(1);
            FileStatus rootPlaceholder;
            if (Path.WINDOWS && !components.isEmpty() && Path.isWindowsAbsolutePath(absPattern.toUri().getPath(), true)) {
                final String driveLetter = components.remove(0);
                rootPlaceholder = new FileStatus(0L, true, 0, 0L, 0L, new Path(scheme, authority, "/" + driveLetter + "/"));
            }
            else {
                rootPlaceholder = new FileStatus(0L, true, 0, 0L, 0L, new Path(scheme, authority, "/"));
            }
            candidates.add(rootPlaceholder);
            for (int componentIdx = 0; componentIdx < components.size(); ++componentIdx) {
                final ArrayList<FileStatus> newCandidates = new ArrayList<FileStatus>(candidates.size());
                final GlobFilter globFilter = new GlobFilter(components.get(componentIdx));
                final String component = unescapePathComponent(components.get(componentIdx));
                if (globFilter.hasPattern()) {
                    sawWildcard = true;
                }
                if (candidates.isEmpty() && sawWildcard) {
                    break;
                }
                if (componentIdx < components.size() - 1 && !globFilter.hasPattern()) {
                    for (final FileStatus candidate : candidates) {
                        candidate.setPath(new Path(candidate.getPath(), component));
                    }
                }
                else {
                    for (final FileStatus candidate : candidates) {
                        if (globFilter.hasPattern()) {
                            final FileStatus[] children = this.listStatus(candidate.getPath());
                            if (children.length == 1) {
                                final Path path = candidate.getPath();
                                final FileStatus status = this.getFileStatus(path);
                                if (status == null) {
                                    Globber.LOG.warn("File/directory {} not found: it may have been deleted. If this is an object store, this can be a sign of eventual consistency problems.", path);
                                    continue;
                                }
                                if (!status.isDirectory()) {
                                    continue;
                                }
                            }
                            for (final FileStatus child : children) {
                                if (componentIdx >= components.size() - 1 || child.isDirectory()) {
                                    child.setPath(new Path(candidate.getPath(), child.getPath().getName()));
                                    if (globFilter.accept(child.getPath())) {
                                        newCandidates.add(child);
                                    }
                                }
                            }
                        }
                        else {
                            final FileStatus childStatus = this.getFileStatus(new Path(candidate.getPath(), component));
                            if (childStatus == null) {
                                continue;
                            }
                            newCandidates.add(childStatus);
                        }
                    }
                    candidates = newCandidates;
                }
            }
            for (FileStatus status2 : candidates) {
                if (status2 == rootPlaceholder) {
                    status2 = this.getFileStatus(rootPlaceholder.getPath());
                    if (status2 == null) {
                        continue;
                    }
                }
                if (this.filter.accept(status2.getPath())) {
                    results.add(status2);
                }
            }
        }
        if (!sawWildcard && results.isEmpty() && flattenedPatterns.size() <= 1) {
            return null;
        }
        final FileStatus[] ret = results.toArray(new FileStatus[0]);
        Arrays.sort(ret);
        return ret;
    }
    
    static {
        LOG = LoggerFactory.getLogger(Globber.class.getName());
    }
}
