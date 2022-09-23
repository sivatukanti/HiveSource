// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi;

import org.eclipse.jetty.util.log.Log;
import java.util.Set;
import java.security.Principal;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.AuthException;
import java.util.HashMap;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.config.RegistrationListener;
import javax.security.auth.message.config.AuthConfigFactory;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.Authenticator;
import javax.servlet.ServletContext;
import org.eclipse.jetty.server.Server;
import javax.security.auth.Subject;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.security.DefaultAuthenticatorFactory;

public class JaspiAuthenticatorFactory extends DefaultAuthenticatorFactory
{
    private static final Logger LOG;
    private static String MESSAGE_LAYER;
    private Subject _serviceSubject;
    private String _serverName;
    
    public Subject getServiceSubject() {
        return this._serviceSubject;
    }
    
    public void setServiceSubject(final Subject serviceSubject) {
        this._serviceSubject = serviceSubject;
    }
    
    public String getServerName() {
        return this._serverName;
    }
    
    public void setServerName(final String serverName) {
        this._serverName = serverName;
    }
    
    @Override
    public Authenticator getAuthenticator(final Server server, final ServletContext context, final Authenticator.AuthConfiguration configuration, final IdentityService identityService, final LoginService loginService) {
        Authenticator authenticator = null;
        try {
            final AuthConfigFactory authConfigFactory = AuthConfigFactory.getFactory();
            final RegistrationListener listener = new RegistrationListener() {
                public void notify(final String layer, final String appContext) {
                }
            };
            final Subject serviceSubject = this.findServiceSubject(server);
            final String serverName = this.findServerName(server, serviceSubject);
            final String appContext = serverName + " " + context.getContextPath();
            final AuthConfigProvider authConfigProvider = authConfigFactory.getConfigProvider(JaspiAuthenticatorFactory.MESSAGE_LAYER, appContext, listener);
            if (authConfigProvider != null) {
                final ServletCallbackHandler servletCallbackHandler = new ServletCallbackHandler(loginService);
                final ServerAuthConfig serverAuthConfig = authConfigProvider.getServerAuthConfig(JaspiAuthenticatorFactory.MESSAGE_LAYER, appContext, servletCallbackHandler);
                if (serverAuthConfig != null) {
                    final Map map = new HashMap();
                    for (final String key : configuration.getInitParameterNames()) {
                        map.put(key, configuration.getInitParameter(key));
                    }
                    authenticator = new JaspiAuthenticator(serverAuthConfig, map, servletCallbackHandler, serviceSubject, true, identityService);
                }
            }
        }
        catch (AuthException e) {
            JaspiAuthenticatorFactory.LOG.warn(e);
        }
        return authenticator;
    }
    
    protected Subject findServiceSubject(final Server server) {
        if (this._serviceSubject != null) {
            return this._serviceSubject;
        }
        final List subjects = server.getBeans((Class)Subject.class);
        if (subjects.size() > 0) {
            return subjects.get(0);
        }
        return null;
    }
    
    protected String findServerName(final Server server, final Subject subject) {
        if (this._serverName != null) {
            return this._serverName;
        }
        if (subject != null) {
            final Set<Principal> principals = subject.getPrincipals();
            if (principals != null && !principals.isEmpty()) {
                return principals.iterator().next().getName();
            }
        }
        return "server";
    }
    
    static {
        LOG = Log.getLogger(JaspiAuthenticatorFactory.class);
        JaspiAuthenticatorFactory.MESSAGE_LAYER = "HTTP";
    }
}
