// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import com.google.re2j.PatternSyntaxException;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class GlobFilter implements PathFilter
{
    private static final PathFilter DEFAULT_FILTER;
    private PathFilter userFilter;
    private GlobPattern pattern;
    
    public GlobFilter(final String filePattern) throws IOException {
        this.userFilter = GlobFilter.DEFAULT_FILTER;
        this.init(filePattern, GlobFilter.DEFAULT_FILTER);
    }
    
    public GlobFilter(final String filePattern, final PathFilter filter) throws IOException {
        this.userFilter = GlobFilter.DEFAULT_FILTER;
        this.init(filePattern, filter);
    }
    
    void init(final String filePattern, final PathFilter filter) throws IOException {
        try {
            this.userFilter = filter;
            this.pattern = new GlobPattern(filePattern);
        }
        catch (PatternSyntaxException e) {
            throw new IOException("Illegal file pattern: " + e.getMessage(), e);
        }
    }
    
    public boolean hasPattern() {
        return this.pattern.hasWildcard();
    }
    
    @Override
    public boolean accept(final Path path) {
        return this.pattern.matches(path.getName()) && this.userFilter.accept(path);
    }
    
    static {
        DEFAULT_FILTER = new PathFilter() {
            @Override
            public boolean accept(final Path file) {
                return true;
            }
        };
    }
}
