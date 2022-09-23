// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import org.eclipse.jetty.server.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ForwardedSchemeHeaderRule extends HeaderRule
{
    private String _scheme;
    
    public ForwardedSchemeHeaderRule() {
        this._scheme = "https";
    }
    
    public String getScheme() {
        return this._scheme;
    }
    
    public void setScheme(final String scheme) {
        this._scheme = scheme;
    }
    
    @Override
    protected String apply(final String target, final String value, final HttpServletRequest request, final HttpServletResponse response) {
        ((Request)request).setScheme(this._scheme);
        return target;
    }
}
