// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi;

import javax.security.auth.message.callback.GroupPrincipalCallback;
import java.util.Iterator;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import java.util.Set;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.AuthException;
import org.eclipse.jetty.security.UserAuthentication;
import java.security.Principal;
import org.eclipse.jetty.server.UserIdentity;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.server.Authentication;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.security.authentication.DeferredAuthentication;
import org.eclipse.jetty.security.IdentityService;
import javax.security.auth.Subject;
import java.util.Map;
import javax.security.auth.message.config.ServerAuthConfig;
import org.eclipse.jetty.security.Authenticator;

public class JaspiAuthenticator implements Authenticator
{
    private final ServerAuthConfig _authConfig;
    private final Map _authProperties;
    private final ServletCallbackHandler _callbackHandler;
    private final Subject _serviceSubject;
    private final boolean _allowLazyAuthentication;
    private final IdentityService _identityService;
    private final DeferredAuthentication _deferred;
    
    public JaspiAuthenticator(final ServerAuthConfig authConfig, final Map authProperties, final ServletCallbackHandler callbackHandler, final Subject serviceSubject, final boolean allowLazyAuthentication, final IdentityService identityService) {
        if (callbackHandler == null) {
            throw new NullPointerException("No CallbackHandler");
        }
        if (authConfig == null) {
            throw new NullPointerException("No AuthConfig");
        }
        this._authConfig = authConfig;
        this._authProperties = authProperties;
        this._callbackHandler = callbackHandler;
        this._serviceSubject = serviceSubject;
        this._allowLazyAuthentication = allowLazyAuthentication;
        this._identityService = identityService;
        this._deferred = new DeferredAuthentication((Authenticator)this);
    }
    
    public void setConfiguration(final AuthConfiguration configuration) {
    }
    
    public String getAuthMethod() {
        return "JASPI";
    }
    
    public Authentication validateRequest(final ServletRequest request, final ServletResponse response, final boolean mandatory) throws ServerAuthException {
        final JaspiMessageInfo info = new JaspiMessageInfo(request, response, mandatory);
        request.setAttribute("org.eclipse.jetty.security.jaspi.info", info);
        Authentication a = this.validateRequest(info);
        if (this._allowLazyAuthentication && !info.isAuthMandatory() && a == Authentication.UNAUTHENTICATED) {
            a = this._deferred;
        }
        return a;
    }
    
    public boolean secureResponse(final ServletRequest req, final ServletResponse res, final boolean mandatory, final Authentication.User validatedUser) throws ServerAuthException {
        final JaspiMessageInfo info = (JaspiMessageInfo)req.getAttribute("org.eclipse.jetty.security.jaspi.info");
        if (info == null) {
            throw new NullPointerException("MessageInfo from request missing: " + req);
        }
        return this.secureResponse(info, validatedUser);
    }
    
    public Authentication validateRequest(final JaspiMessageInfo messageInfo) throws ServerAuthException {
        try {
            final String authContextId = this._authConfig.getAuthContextID(messageInfo);
            final ServerAuthContext authContext = this._authConfig.getAuthContext(authContextId, this._serviceSubject, this._authProperties);
            final Subject clientSubject = new Subject();
            final AuthStatus authStatus = authContext.validateRequest(messageInfo, clientSubject, this._serviceSubject);
            if (authStatus == AuthStatus.SEND_CONTINUE) {
                return Authentication.SEND_CONTINUE;
            }
            if (authStatus == AuthStatus.SEND_FAILURE) {
                return Authentication.SEND_FAILURE;
            }
            if (authStatus == AuthStatus.SUCCESS) {
                final Set<UserIdentity> ids = clientSubject.getPrivateCredentials(UserIdentity.class);
                UserIdentity userIdentity;
                if (ids.size() > 0) {
                    userIdentity = ids.iterator().next();
                }
                else {
                    final CallerPrincipalCallback principalCallback = this._callbackHandler.getThreadCallerPrincipalCallback();
                    if (principalCallback == null) {
                        return Authentication.UNAUTHENTICATED;
                    }
                    Principal principal = principalCallback.getPrincipal();
                    if (principal == null) {
                        final String principalName = principalCallback.getName();
                        final Set<Principal> principals = principalCallback.getSubject().getPrincipals();
                        for (final Principal p : principals) {
                            if (p.getName().equals(principalName)) {
                                principal = p;
                                break;
                            }
                        }
                        if (principal == null) {
                            return Authentication.UNAUTHENTICATED;
                        }
                    }
                    final GroupPrincipalCallback groupPrincipalCallback = this._callbackHandler.getThreadGroupPrincipalCallback();
                    final String[] groups = (String[])((groupPrincipalCallback == null) ? null : groupPrincipalCallback.getGroups());
                    userIdentity = this._identityService.newUserIdentity(clientSubject, principal, groups);
                }
                return new UserAuthentication(this.getAuthMethod(), userIdentity);
            }
            if (authStatus == AuthStatus.SEND_SUCCESS) {
                return Authentication.SEND_SUCCESS;
            }
            throw new NullPointerException("No AuthStatus returned");
        }
        catch (AuthException e) {
            throw new ServerAuthException(e);
        }
    }
    
    public boolean secureResponse(final JaspiMessageInfo messageInfo, final Authentication validatedUser) throws ServerAuthException {
        try {
            final String authContextId = this._authConfig.getAuthContextID(messageInfo);
            final ServerAuthContext authContext = this._authConfig.getAuthContext(authContextId, this._serviceSubject, this._authProperties);
            final AuthStatus status = authContext.secureResponse(messageInfo, this._serviceSubject);
            return AuthStatus.SEND_SUCCESS.equals(status);
        }
        catch (AuthException e) {
            throw new ServerAuthException(e);
        }
    }
}
