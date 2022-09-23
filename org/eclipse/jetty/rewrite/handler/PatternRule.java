// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import org.eclipse.jetty.http.PathMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public abstract class PatternRule extends Rule
{
    protected String _pattern;
    
    public String getPattern() {
        return this._pattern;
    }
    
    public void setPattern(final String pattern) {
        this._pattern = pattern;
    }
    
    @Override
    public String matchAndApply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (PathMap.match(this._pattern, target)) {
            return this.apply(target, request, response);
        }
        return null;
    }
    
    protected abstract String apply(final String p0, final HttpServletRequest p1, final HttpServletResponse p2) throws IOException;
    
    @Override
    public String toString() {
        return super.toString() + "[" + this._pattern + "]";
    }
}
