// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.net.URI;

public abstract class PatternMatcher
{
    public abstract void matched(final URI p0) throws Exception;
    
    public void match(final Pattern pattern, final URI[] uris, final boolean isNullInclusive) throws Exception {
        if (uris != null) {
            final String[] patterns = (String[])((pattern == null) ? null : pattern.pattern().split(","));
            final List<Pattern> subPatterns = new ArrayList<Pattern>();
            for (int i = 0; patterns != null && i < patterns.length; ++i) {
                subPatterns.add(Pattern.compile(patterns[i]));
            }
            if (subPatterns.isEmpty()) {
                subPatterns.add(pattern);
            }
            if (subPatterns.isEmpty()) {
                this.matchPatterns(null, uris, isNullInclusive);
            }
            else {
                for (final Pattern p : subPatterns) {
                    this.matchPatterns(p, uris, isNullInclusive);
                }
            }
        }
    }
    
    public void matchPatterns(final Pattern pattern, final URI[] uris, final boolean isNullInclusive) throws Exception {
        for (int i = 0; i < uris.length; ++i) {
            final URI uri = uris[i];
            final String s = uri.toString();
            if ((pattern == null && isNullInclusive) || (pattern != null && pattern.matcher(s).matches())) {
                this.matched(uris[i]);
            }
        }
    }
}
