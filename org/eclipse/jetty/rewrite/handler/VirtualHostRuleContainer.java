// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.util.LazyList;

public class VirtualHostRuleContainer extends RuleContainer
{
    private String[] _virtualHosts;
    
    public void setVirtualHosts(final String[] virtualHosts) {
        if (virtualHosts == null) {
            this._virtualHosts = virtualHosts;
        }
        else {
            this._virtualHosts = new String[virtualHosts.length];
            for (int i = 0; i < virtualHosts.length; ++i) {
                this._virtualHosts[i] = this.normalizeHostname(virtualHosts[i]);
            }
        }
    }
    
    public String[] getVirtualHosts() {
        return this._virtualHosts;
    }
    
    public void addVirtualHost(final String virtualHost) {
        this._virtualHosts = (String[])LazyList.addToArray((Object[])this._virtualHosts, (Object)virtualHost, (Class)String.class);
    }
    
    @Override
    public String matchAndApply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (this._virtualHosts != null && this._virtualHosts.length > 0) {
            final String requestHost = this.normalizeHostname(request.getServerName());
            for (final String ruleHost : this._virtualHosts) {
                if (ruleHost == null || ruleHost.equalsIgnoreCase(requestHost) || (ruleHost.startsWith("*.") && ruleHost.regionMatches(true, 2, requestHost, requestHost.indexOf(".") + 1, ruleHost.length() - 2))) {
                    return this.apply(target, request, response);
                }
            }
            return null;
        }
        return this.apply(target, request, response);
    }
    
    private String normalizeHostname(final String host) {
        if (host == null) {
            return null;
        }
        if (host.endsWith(".")) {
            return host.substring(0, host.length() - 1);
        }
        return host;
    }
}
