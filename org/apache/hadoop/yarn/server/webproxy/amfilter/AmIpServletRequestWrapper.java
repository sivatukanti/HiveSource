// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webproxy.amfilter;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class AmIpServletRequestWrapper extends HttpServletRequestWrapper
{
    private final AmIpPrincipal principal;
    
    public AmIpServletRequestWrapper(final HttpServletRequest request, final AmIpPrincipal principal) {
        super(request);
        this.principal = principal;
    }
    
    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }
    
    @Override
    public String getRemoteUser() {
        return this.principal.getName();
    }
    
    @Override
    public boolean isUserInRole(final String role) {
        return false;
    }
}
