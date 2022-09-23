// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi.modules;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import org.eclipse.jetty.security.authentication.LoginCallbackImpl;
import javax.security.auth.callback.Callback;
import org.eclipse.jetty.security.jaspi.callback.CredentialValidationCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.B64Code;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.Subject;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.AuthException;
import java.util.Map;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;

public class BaseAuthModule implements ServerAuthModule, ServerAuthContext
{
    private static final Class[] SUPPORTED_MESSAGE_TYPES;
    protected static final String LOGIN_SERVICE_KEY = "org.eclipse.jetty.security.jaspi.modules.LoginService";
    protected CallbackHandler callbackHandler;
    
    public Class[] getSupportedMessageTypes() {
        return BaseAuthModule.SUPPORTED_MESSAGE_TYPES;
    }
    
    public BaseAuthModule() {
    }
    
    public BaseAuthModule(final CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }
    
    public void initialize(final MessagePolicy requestPolicy, final MessagePolicy responsePolicy, final CallbackHandler handler, final Map options) throws AuthException {
        this.callbackHandler = handler;
    }
    
    public void cleanSubject(final MessageInfo messageInfo, final Subject subject) throws AuthException {
    }
    
    public AuthStatus secureResponse(final MessageInfo messageInfo, final Subject serviceSubject) throws AuthException {
        return AuthStatus.SEND_SUCCESS;
    }
    
    public AuthStatus validateRequest(final MessageInfo messageInfo, final Subject clientSubject, final Subject serviceSubject) throws AuthException {
        return AuthStatus.SEND_FAILURE;
    }
    
    protected boolean isMandatory(final MessageInfo messageInfo) {
        final String mandatory = messageInfo.getMap().get("javax.security.auth.message.MessagePolicy.isMandatory");
        return mandatory != null && Boolean.valueOf(mandatory);
    }
    
    protected boolean login(final Subject clientSubject, String credentials, final String authMethod, final MessageInfo messageInfo) throws IOException, UnsupportedCallbackException {
        credentials = credentials.substring(credentials.indexOf(32) + 1);
        credentials = B64Code.decode(credentials, "ISO-8859-1");
        final int i = credentials.indexOf(58);
        final String userName = credentials.substring(0, i);
        final String password = credentials.substring(i + 1);
        return this.login(clientSubject, userName, new Password(password), authMethod, messageInfo);
    }
    
    protected boolean login(final Subject clientSubject, final String username, final Credential credential, final String authMethod, final MessageInfo messageInfo) throws IOException, UnsupportedCallbackException {
        final CredentialValidationCallback credValidationCallback = new CredentialValidationCallback(clientSubject, username, credential);
        this.callbackHandler.handle(new Callback[] { credValidationCallback });
        if (credValidationCallback.getResult()) {
            final Set<LoginCallbackImpl> loginCallbacks = clientSubject.getPrivateCredentials(LoginCallbackImpl.class);
            if (!loginCallbacks.isEmpty()) {
                final LoginCallbackImpl loginCallback = loginCallbacks.iterator().next();
                final CallerPrincipalCallback callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, loginCallback.getUserPrincipal());
                final GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(clientSubject, loginCallback.getRoles());
                this.callbackHandler.handle(new Callback[] { callerPrincipalCallback, groupPrincipalCallback });
            }
            messageInfo.getMap().put("javax.servlet.http.authType", authMethod);
        }
        return credValidationCallback.getResult();
    }
    
    static {
        SUPPORTED_MESSAGE_TYPES = new Class[] { HttpServletRequest.class, HttpServletResponse.class };
    }
}
