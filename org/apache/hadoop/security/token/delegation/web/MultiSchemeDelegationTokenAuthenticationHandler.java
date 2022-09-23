// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.io.IOException;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.Iterator;
import org.apache.hadoop.security.authentication.server.AuthenticationHandlerUtil;
import java.util.HashSet;
import com.google.common.base.Preconditions;
import java.util.Properties;
import java.util.Collection;
import org.apache.hadoop.security.authentication.server.AuthenticationHandler;
import org.apache.hadoop.security.authentication.server.MultiSchemeAuthenticationHandler;
import java.util.Set;
import com.google.common.base.Splitter;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.authentication.server.CompositeAuthenticationHandler;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class MultiSchemeDelegationTokenAuthenticationHandler extends DelegationTokenAuthenticationHandler implements CompositeAuthenticationHandler
{
    public static final String DELEGATION_TOKEN_SCHEMES_PROPERTY = "multi-scheme-auth-handler.delegation.schemes";
    private static final Splitter STR_SPLITTER;
    private Set<String> delegationAuthSchemes;
    
    public MultiSchemeDelegationTokenAuthenticationHandler() {
        super(new MultiSchemeAuthenticationHandler("multi-scheme-dt"));
        this.delegationAuthSchemes = null;
    }
    
    @Override
    public Collection<String> getTokenTypes() {
        return ((CompositeAuthenticationHandler)this.getAuthHandler()).getTokenTypes();
    }
    
    @Override
    public void init(final Properties config) throws ServletException {
        super.init(config);
        final String schemesProperty = Preconditions.checkNotNull(config.getProperty("multi-scheme-auth-handler.schemes"));
        final String delegationAuthSchemesProp = Preconditions.checkNotNull(config.getProperty("multi-scheme-auth-handler.delegation.schemes"));
        final Set<String> authSchemes = new HashSet<String>();
        for (final String scheme : MultiSchemeDelegationTokenAuthenticationHandler.STR_SPLITTER.split(schemesProperty)) {
            authSchemes.add(AuthenticationHandlerUtil.checkAuthScheme(scheme));
        }
        this.delegationAuthSchemes = new HashSet<String>();
        for (final String scheme : MultiSchemeDelegationTokenAuthenticationHandler.STR_SPLITTER.split(delegationAuthSchemesProp)) {
            this.delegationAuthSchemes.add(AuthenticationHandlerUtil.checkAuthScheme(scheme));
        }
        Preconditions.checkArgument(authSchemes.containsAll(this.delegationAuthSchemes));
    }
    
    @Override
    public AuthenticationToken authenticate(final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        final String authorization = request.getHeader("Authorization");
        if (this.isManagementOperation(request)) {
            boolean schemeConfigured = false;
            if (authorization != null) {
                for (final String scheme : this.delegationAuthSchemes) {
                    if (AuthenticationHandlerUtil.matchAuthScheme(scheme, authorization)) {
                        schemeConfigured = true;
                        break;
                    }
                }
            }
            if (!schemeConfigured) {
                response.setStatus(401);
                for (final String scheme : this.delegationAuthSchemes) {
                    response.addHeader("WWW-Authenticate", scheme);
                }
                return null;
            }
        }
        return super.authenticate(request, response);
    }
    
    static {
        STR_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();
    }
}
