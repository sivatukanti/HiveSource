// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import java.util.regex.Matcher;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RedirectRegexRule extends RegexRule
{
    private String _replacement;
    
    public RedirectRegexRule() {
        this._handling = true;
        this._terminating = true;
    }
    
    public void setReplacement(final String replacement) {
        this._replacement = replacement;
    }
    
    @Override
    protected String apply(String target, final HttpServletRequest request, final HttpServletResponse response, final Matcher matcher) throws IOException {
        target = this._replacement;
        for (int g = 1; g <= matcher.groupCount(); ++g) {
            final String group = matcher.group(g);
            target = target.replaceAll("\\$" + g, group);
        }
        response.sendRedirect(response.encodeRedirectURL(target));
        return target;
    }
}
