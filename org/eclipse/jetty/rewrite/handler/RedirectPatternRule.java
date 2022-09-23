// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RedirectPatternRule extends PatternRule
{
    private String _location;
    
    public RedirectPatternRule() {
        this._handling = true;
        this._terminating = true;
    }
    
    public void setLocation(final String value) {
        this._location = value;
    }
    
    public String apply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.sendRedirect(response.encodeRedirectURL(this._location));
        return target;
    }
    
    @Override
    public String toString() {
        return super.toString() + "[" + this._location + "]";
    }
}
