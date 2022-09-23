// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.security.http;

import java.io.IOException;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.delegation.web.DelegationTokenAuthenticationFilter;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RMAuthenticationFilter extends DelegationTokenAuthenticationFilter
{
    private static AbstractDelegationTokenSecretManager<?> manager;
    public static final String AUTH_HANDLER_PROPERTY = "yarn.resourcemanager.authentication-handler";
    private static final String OLD_HEADER = "Hadoop-YARN-Auth-Delegation-Token";
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        filterConfig.getServletContext().setAttribute("hadoop.http.delegation-token-secret-manager", RMAuthenticationFilter.manager);
        super.init(filterConfig);
    }
    
    @Override
    public void doFilter(ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest)request;
        final String newHeader = req.getHeader("X-Hadoop-Delegation-Token");
        if (newHeader == null || newHeader.isEmpty()) {
            final String oldHeader = req.getHeader("Hadoop-YARN-Auth-Delegation-Token");
            if (oldHeader != null && !oldHeader.isEmpty()) {
                request = new HttpServletRequestWrapper(req) {
                    @Override
                    public String getHeader(final String name) {
                        if (name.equals("X-Hadoop-Delegation-Token")) {
                            return oldHeader;
                        }
                        return super.getHeader(name);
                    }
                };
            }
        }
        super.doFilter(request, response, filterChain);
    }
    
    public static void setDelegationTokenSecretManager(final AbstractDelegationTokenSecretManager<?> manager) {
        RMAuthenticationFilter.manager = manager;
    }
}
