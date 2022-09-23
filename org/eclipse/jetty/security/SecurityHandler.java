// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import org.eclipse.jetty.util.log.Log;
import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.security.authentication.DeferredAuthentication;
import javax.servlet.ServletResponse;
import org.eclipse.jetty.server.Authentication;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import org.eclipse.jetty.server.handler.ContextHandler;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.HashMap;
import java.security.Principal;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.handler.HandlerWrapper;

public abstract class SecurityHandler extends HandlerWrapper implements Authenticator.AuthConfiguration
{
    private static final Logger LOG;
    private boolean _checkWelcomeFiles;
    private Authenticator _authenticator;
    private Authenticator.Factory _authenticatorFactory;
    private String _realmName;
    private String _authMethod;
    private final Map<String, String> _initParameters;
    private LoginService _loginService;
    private IdentityService _identityService;
    private boolean _renewSession;
    public static final Principal __NO_USER;
    public static final Principal __NOBODY;
    
    protected SecurityHandler() {
        this._checkWelcomeFiles = false;
        this._authenticatorFactory = new DefaultAuthenticatorFactory();
        this._initParameters = new HashMap<String, String>();
        this._renewSession = true;
        this.addBean(this._authenticatorFactory);
    }
    
    @Override
    public IdentityService getIdentityService() {
        return this._identityService;
    }
    
    public void setIdentityService(final IdentityService identityService) {
        if (this.isStarted()) {
            throw new IllegalStateException("Started");
        }
        this.updateBean(this._identityService, identityService);
        this._identityService = identityService;
    }
    
    @Override
    public LoginService getLoginService() {
        return this._loginService;
    }
    
    public void setLoginService(final LoginService loginService) {
        if (this.isStarted()) {
            throw new IllegalStateException("Started");
        }
        this.updateBean(this._loginService, loginService);
        this._loginService = loginService;
    }
    
    public Authenticator getAuthenticator() {
        return this._authenticator;
    }
    
    public void setAuthenticator(final Authenticator authenticator) {
        if (this.isStarted()) {
            throw new IllegalStateException("Started");
        }
        this.updateBean(this._authenticator, authenticator);
        this._authenticator = authenticator;
        if (this._authenticator != null) {
            this._authMethod = this._authenticator.getAuthMethod();
        }
    }
    
    public Authenticator.Factory getAuthenticatorFactory() {
        return this._authenticatorFactory;
    }
    
    public void setAuthenticatorFactory(final Authenticator.Factory authenticatorFactory) {
        if (this.isRunning()) {
            throw new IllegalStateException("running");
        }
        this.updateBean(this._authenticatorFactory, authenticatorFactory);
        this._authenticatorFactory = authenticatorFactory;
    }
    
    @Override
    public String getRealmName() {
        return this._realmName;
    }
    
    public void setRealmName(final String realmName) {
        if (this.isRunning()) {
            throw new IllegalStateException("running");
        }
        this._realmName = realmName;
    }
    
    @Override
    public String getAuthMethod() {
        return this._authMethod;
    }
    
    public void setAuthMethod(final String authMethod) {
        if (this.isRunning()) {
            throw new IllegalStateException("running");
        }
        this._authMethod = authMethod;
    }
    
    public boolean isCheckWelcomeFiles() {
        return this._checkWelcomeFiles;
    }
    
    public void setCheckWelcomeFiles(final boolean authenticateWelcomeFiles) {
        if (this.isRunning()) {
            throw new IllegalStateException("running");
        }
        this._checkWelcomeFiles = authenticateWelcomeFiles;
    }
    
    @Override
    public String getInitParameter(final String key) {
        return this._initParameters.get(key);
    }
    
    @Override
    public Set<String> getInitParameterNames() {
        return this._initParameters.keySet();
    }
    
    public String setInitParameter(final String key, final String value) {
        if (this.isRunning()) {
            throw new IllegalStateException("running");
        }
        return this._initParameters.put(key, value);
    }
    
    protected LoginService findLoginService() throws Exception {
        final Collection<LoginService> list = this.getServer().getBeans(LoginService.class);
        LoginService service = null;
        final String realm = this.getRealmName();
        if (realm != null) {
            for (final LoginService s : list) {
                if (s.getName() != null && s.getName().equals(realm)) {
                    service = s;
                    break;
                }
            }
        }
        else if (list.size() == 1) {
            service = list.iterator().next();
        }
        return service;
    }
    
    protected IdentityService findIdentityService() {
        return this.getServer().getBean(IdentityService.class);
    }
    
    @Override
    protected void doStart() throws Exception {
        final ContextHandler.Context context = ContextHandler.getCurrentContext();
        if (context != null) {
            final Enumeration<String> names = context.getInitParameterNames();
            while (names != null && names.hasMoreElements()) {
                final String name = names.nextElement();
                if (name.startsWith("org.eclipse.jetty.security.") && this.getInitParameter(name) == null) {
                    this.setInitParameter(name, context.getInitParameter(name));
                }
            }
        }
        if (this._loginService == null) {
            this.setLoginService(this.findLoginService());
            if (this._loginService != null) {
                this.unmanage(this._loginService);
            }
        }
        if (this._identityService == null) {
            if (this._loginService != null) {
                this.setIdentityService(this._loginService.getIdentityService());
            }
            if (this._identityService == null) {
                this.setIdentityService(this.findIdentityService());
            }
            if (this._identityService == null) {
                if (this._realmName != null) {
                    this.setIdentityService(new DefaultIdentityService());
                    this.manage(this._identityService);
                }
            }
            else {
                this.unmanage(this._identityService);
            }
        }
        if (this._loginService != null) {
            if (this._loginService.getIdentityService() == null) {
                this._loginService.setIdentityService(this._identityService);
            }
            else if (this._loginService.getIdentityService() != this._identityService) {
                throw new IllegalStateException("LoginService has different IdentityService to " + this);
            }
        }
        final Authenticator.Factory authenticatorFactory = this.getAuthenticatorFactory();
        if (this._authenticator == null && authenticatorFactory != null && this._identityService != null) {
            this.setAuthenticator(authenticatorFactory.getAuthenticator(this.getServer(), ContextHandler.getCurrentContext(), this, this._identityService, this._loginService));
        }
        if (this._authenticator != null) {
            this._authenticator.setConfiguration(this);
        }
        else if (this._realmName != null) {
            SecurityHandler.LOG.warn("No Authenticator for " + this, new Object[0]);
            throw new IllegalStateException("No Authenticator");
        }
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        if (!this.isManaged(this._identityService)) {
            this.removeBean(this._identityService);
            this._identityService = null;
        }
        if (!this.isManaged(this._loginService)) {
            this.removeBean(this._loginService);
            this._loginService = null;
        }
        super.doStop();
    }
    
    protected boolean checkSecurity(final Request request) {
        switch (request.getDispatcherType()) {
            case REQUEST:
            case ASYNC: {
                return true;
            }
            case FORWARD: {
                if (this.isCheckWelcomeFiles() && request.getAttribute("org.eclipse.jetty.server.welcome") != null) {
                    request.removeAttribute("org.eclipse.jetty.server.welcome");
                    return true;
                }
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean isSessionRenewedOnAuthentication() {
        return this._renewSession;
    }
    
    public void setSessionRenewedOnAuthentication(final boolean renew) {
        this._renewSession = renew;
    }
    
    @Override
    public void handle(final String pathInContext, final Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final Response base_response = baseRequest.getResponse();
        final Handler handler = this.getHandler();
        if (handler == null) {
            return;
        }
        final Authenticator authenticator = this._authenticator;
        if (this.checkSecurity(baseRequest)) {
            if (authenticator != null) {
                authenticator.prepareRequest(baseRequest);
            }
            final RoleInfo roleInfo = this.prepareConstraintInfo(pathInContext, baseRequest);
            if (!this.checkUserDataPermissions(pathInContext, baseRequest, base_response, roleInfo)) {
                if (!baseRequest.isHandled()) {
                    response.sendError(403);
                    baseRequest.setHandled(true);
                }
                return;
            }
            final boolean isAuthMandatory = this.isAuthMandatory(baseRequest, base_response, roleInfo);
            if (isAuthMandatory && authenticator == null) {
                SecurityHandler.LOG.warn("No authenticator for: " + roleInfo, new Object[0]);
                if (!baseRequest.isHandled()) {
                    response.sendError(403);
                    baseRequest.setHandled(true);
                }
                return;
            }
            Object previousIdentity = null;
            try {
                Authentication authentication = baseRequest.getAuthentication();
                if (authentication == null || authentication == Authentication.NOT_CHECKED) {
                    authentication = ((authenticator == null) ? Authentication.UNAUTHENTICATED : authenticator.validateRequest(request, response, isAuthMandatory));
                }
                if (authentication instanceof Authentication.Wrapped) {
                    request = ((Authentication.Wrapped)authentication).getHttpServletRequest();
                    response = ((Authentication.Wrapped)authentication).getHttpServletResponse();
                }
                if (authentication instanceof Authentication.ResponseSent) {
                    baseRequest.setHandled(true);
                }
                else if (authentication instanceof Authentication.User) {
                    final Authentication.User userAuth = (Authentication.User)authentication;
                    baseRequest.setAuthentication(authentication);
                    if (this._identityService != null) {
                        previousIdentity = this._identityService.associate(userAuth.getUserIdentity());
                    }
                    if (isAuthMandatory) {
                        final boolean authorized = this.checkWebResourcePermissions(pathInContext, baseRequest, base_response, roleInfo, userAuth.getUserIdentity());
                        if (!authorized) {
                            response.sendError(403, "!role");
                            baseRequest.setHandled(true);
                            return;
                        }
                    }
                    handler.handle(pathInContext, baseRequest, request, response);
                    if (authenticator != null) {
                        authenticator.secureResponse(request, response, isAuthMandatory, userAuth);
                    }
                }
                else if (authentication instanceof Authentication.Deferred) {
                    final DeferredAuthentication deferred = (DeferredAuthentication)authentication;
                    baseRequest.setAuthentication(authentication);
                    try {
                        handler.handle(pathInContext, baseRequest, request, response);
                    }
                    finally {
                        previousIdentity = deferred.getPreviousAssociation();
                    }
                    if (authenticator != null) {
                        final Authentication auth = baseRequest.getAuthentication();
                        if (auth instanceof Authentication.User) {
                            final Authentication.User userAuth2 = (Authentication.User)auth;
                            authenticator.secureResponse(request, response, isAuthMandatory, userAuth2);
                        }
                        else {
                            authenticator.secureResponse(request, response, isAuthMandatory, null);
                        }
                    }
                }
                else {
                    baseRequest.setAuthentication(authentication);
                    if (this._identityService != null) {
                        previousIdentity = this._identityService.associate(null);
                    }
                    handler.handle(pathInContext, baseRequest, request, response);
                    if (authenticator != null) {
                        authenticator.secureResponse(request, response, isAuthMandatory, null);
                    }
                }
            }
            catch (ServerAuthException e) {
                response.sendError(500, e.getMessage());
            }
            finally {
                if (this._identityService != null) {
                    this._identityService.disassociate(previousIdentity);
                }
            }
        }
        else {
            handler.handle(pathInContext, baseRequest, request, response);
        }
    }
    
    public static SecurityHandler getCurrentSecurityHandler() {
        final ContextHandler.Context context = ContextHandler.getCurrentContext();
        if (context == null) {
            return null;
        }
        return context.getContextHandler().getChildHandlerByClass(SecurityHandler.class);
    }
    
    public void logout(final Authentication.User user) {
        SecurityHandler.LOG.debug("logout {}", user);
        final LoginService login_service = this.getLoginService();
        if (login_service != null) {
            login_service.logout(user.getUserIdentity());
        }
        final IdentityService identity_service = this.getIdentityService();
        if (identity_service != null) {
            final Object previous = null;
            identity_service.disassociate(previous);
        }
    }
    
    protected abstract RoleInfo prepareConstraintInfo(final String p0, final Request p1);
    
    protected abstract boolean checkUserDataPermissions(final String p0, final Request p1, final Response p2, final RoleInfo p3) throws IOException;
    
    protected abstract boolean isAuthMandatory(final Request p0, final Response p1, final Object p2);
    
    protected abstract boolean checkWebResourcePermissions(final String p0, final Request p1, final Response p2, final Object p3, final UserIdentity p4) throws IOException;
    
    static {
        LOG = Log.getLogger(SecurityHandler.class);
        __NO_USER = new Principal() {
            @Override
            public String getName() {
                return null;
            }
            
            @Override
            public String toString() {
                return "No User";
            }
        };
        __NOBODY = new Principal() {
            @Override
            public String getName() {
                return "Nobody";
            }
            
            @Override
            public String toString() {
                return this.getName();
            }
        };
    }
    
    public class NotChecked implements Principal
    {
        @Override
        public String getName() {
            return null;
        }
        
        @Override
        public String toString() {
            return "NOT CHECKED";
        }
        
        public SecurityHandler getSecurityHandler() {
            return SecurityHandler.this;
        }
    }
}
