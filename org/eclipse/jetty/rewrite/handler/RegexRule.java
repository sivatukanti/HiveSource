// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public abstract class RegexRule extends Rule
{
    protected Pattern _regex;
    
    public void setRegex(final String regex) {
        this._regex = Pattern.compile(regex);
    }
    
    public String getRegex() {
        return (this._regex == null) ? null : this._regex.pattern();
    }
    
    @Override
    public String matchAndApply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final Matcher matcher = this._regex.matcher(target);
        final boolean matches = matcher.matches();
        if (matches) {
            return this.apply(target, request, response, matcher);
        }
        return null;
    }
    
    protected abstract String apply(final String p0, final HttpServletRequest p1, final HttpServletResponse p2, final Matcher p3) throws IOException;
    
    @Override
    public String toString() {
        return super.toString() + "[" + this._regex + "]";
    }
}
