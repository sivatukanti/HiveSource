// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi;

import java.io.IOException;
import org.eclipse.jetty.security.authentication.LoginCallback;
import org.eclipse.jetty.server.UserIdentity;
import javax.security.auth.Subject;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.callback.TrustStoreCallback;
import javax.security.auth.message.callback.SecretKeyCallback;
import javax.security.auth.message.callback.PrivateKeyCallback;
import javax.security.auth.message.callback.CertStoreCallback;
import org.eclipse.jetty.security.authentication.LoginCallbackImpl;
import org.eclipse.jetty.security.jaspi.callback.CredentialValidationCallback;
import java.security.Principal;
import java.util.Collection;
import javax.security.auth.message.callback.PasswordValidationCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import org.eclipse.jetty.security.LoginService;
import javax.security.auth.callback.CallbackHandler;

public class ServletCallbackHandler implements CallbackHandler
{
    private final LoginService _loginService;
    private final ThreadLocal<CallerPrincipalCallback> _callerPrincipals;
    private final ThreadLocal<GroupPrincipalCallback> _groupPrincipals;
    
    public ServletCallbackHandler(final LoginService loginService) {
        this._callerPrincipals = new ThreadLocal<CallerPrincipalCallback>();
        this._groupPrincipals = new ThreadLocal<GroupPrincipalCallback>();
        this._loginService = loginService;
    }
    
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (final Callback callback : callbacks) {
            if (callback instanceof CallerPrincipalCallback) {
                this._callerPrincipals.set((CallerPrincipalCallback)callback);
            }
            else if (callback instanceof GroupPrincipalCallback) {
                this._groupPrincipals.set((GroupPrincipalCallback)callback);
            }
            else if (callback instanceof PasswordValidationCallback) {
                final PasswordValidationCallback passwordValidationCallback = (PasswordValidationCallback)callback;
                final Subject subject = passwordValidationCallback.getSubject();
                final UserIdentity user = this._loginService.login(passwordValidationCallback.getUsername(), (Object)passwordValidationCallback.getPassword());
                if (user != null) {
                    passwordValidationCallback.setResult(true);
                    passwordValidationCallback.getSubject().getPrincipals().addAll(user.getSubject().getPrincipals());
                    passwordValidationCallback.getSubject().getPrivateCredentials().add(user);
                }
            }
            else if (callback instanceof CredentialValidationCallback) {
                final CredentialValidationCallback credentialValidationCallback = (CredentialValidationCallback)callback;
                final Subject subject = credentialValidationCallback.getSubject();
                final LoginCallback loginCallback = new LoginCallbackImpl(subject, credentialValidationCallback.getUsername(), credentialValidationCallback.getCredential());
                final UserIdentity user2 = this._loginService.login(credentialValidationCallback.getUsername(), (Object)credentialValidationCallback.getCredential());
                if (user2 != null) {
                    loginCallback.setUserPrincipal(user2.getUserPrincipal());
                    credentialValidationCallback.getSubject().getPrivateCredentials().add(loginCallback);
                    credentialValidationCallback.setResult(true);
                    credentialValidationCallback.getSubject().getPrincipals().addAll(user2.getSubject().getPrincipals());
                    credentialValidationCallback.getSubject().getPrivateCredentials().add(user2);
                }
            }
            else if (!(callback instanceof CertStoreCallback)) {
                if (!(callback instanceof PrivateKeyCallback)) {
                    if (!(callback instanceof SecretKeyCallback)) {
                        if (!(callback instanceof TrustStoreCallback)) {
                            throw new UnsupportedCallbackException(callback);
                        }
                    }
                }
            }
        }
    }
    
    public CallerPrincipalCallback getThreadCallerPrincipalCallback() {
        final CallerPrincipalCallback callerPrincipalCallback = this._callerPrincipals.get();
        this._callerPrincipals.remove();
        return callerPrincipalCallback;
    }
    
    public GroupPrincipalCallback getThreadGroupPrincipalCallback() {
        final GroupPrincipalCallback groupPrincipalCallback = this._groupPrincipals.get();
        this._groupPrincipals.remove();
        return groupPrincipalCallback;
    }
}
