// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.io;

import javax.annotation.Nullable;
import java.io.File;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.regex.Pattern;
import org.apache.curator.shaded.com.google.common.annotations.Beta;
import java.io.FilenameFilter;

@Beta
public final class PatternFilenameFilter implements FilenameFilter
{
    private final Pattern pattern;
    
    public PatternFilenameFilter(final String patternStr) {
        this(Pattern.compile(patternStr));
    }
    
    public PatternFilenameFilter(final Pattern pattern) {
        this.pattern = Preconditions.checkNotNull(pattern);
    }
    
    @Override
    public boolean accept(@Nullable final File dir, final String fileName) {
        return this.pattern.matcher(fileName).matches();
    }
}
