// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline.security;

import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.token.delegation.web.DelegationTokenAuthenticationFilter;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TimelineAuthenticationFilter extends DelegationTokenAuthenticationFilter
{
    private static TimelineDelegationTokenSecretManagerService.TimelineDelegationTokenSecretManager secretManager;
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        filterConfig.getServletContext().setAttribute("hadoop.http.delegation-token-secret-manager", TimelineAuthenticationFilter.secretManager);
        super.init(filterConfig);
    }
    
    public static void setTimelineDelegationTokenSecretManager(final TimelineDelegationTokenSecretManagerService.TimelineDelegationTokenSecretManager secretManager) {
        TimelineAuthenticationFilter.secretManager = secretManager;
    }
}
