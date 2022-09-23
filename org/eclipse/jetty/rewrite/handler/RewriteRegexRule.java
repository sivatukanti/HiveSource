// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import org.eclipse.jetty.server.Request;
import java.io.IOException;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RewriteRegexRule extends RegexRule implements ApplyURI
{
    private String _replacement;
    
    public RewriteRegexRule() {
        this._handling = false;
        this._terminating = false;
    }
    
    public void setReplacement(final String replacement) {
        this._replacement = replacement;
    }
    
    public String apply(String target, final HttpServletRequest request, final HttpServletResponse response, final Matcher matcher) throws IOException {
        target = this._replacement;
        for (int g = 1; g <= matcher.groupCount(); ++g) {
            final String group = Matcher.quoteReplacement(matcher.group(g));
            target = target.replaceAll("\\$" + g, group);
        }
        return target;
    }
    
    public void applyURI(final Request request, final String oldTarget, final String newTarget) throws IOException {
        final Matcher matcher = this._regex.matcher(request.getRequestURI());
        final boolean matches = matcher.matches();
        if (matches) {
            String uri = this._replacement;
            for (int g = 1; g <= matcher.groupCount(); ++g) {
                final String group = Matcher.quoteReplacement(matcher.group(g));
                uri = uri.replaceAll("\\$" + g, group);
            }
            request.setRequestURI(uri);
        }
        else {
            request.setRequestURI(newTarget);
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + "[" + this._replacement + "]";
    }
}
