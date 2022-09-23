// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi.modules;

import java.util.Arrays;
import javax.servlet.http.HttpSessionBindingEvent;
import java.security.Principal;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.security.authentication.LoginCallbackImpl;
import org.eclipse.jetty.util.security.Credential;
import java.util.Set;
import javax.servlet.http.HttpSession;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import org.eclipse.jetty.security.authentication.DeferredAuthentication;
import java.util.Collection;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.security.Password;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.Subject;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.AuthException;
import java.util.Map;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.callback.CallbackHandler;
import org.eclipse.jetty.security.CrossContextPsuedoSession;
import org.eclipse.jetty.util.log.Logger;

public class FormAuthModule extends BaseAuthModule
{
    private static final Logger LOG;
    public static final String __J_URI = "org.eclipse.jetty.util.URI";
    public static final String __J_AUTHENTICATED = "org.eclipse.jetty.server.Auth";
    public static final String __J_SECURITY_CHECK = "/j_security_check";
    public static final String __J_USERNAME = "j_username";
    public static final String __J_PASSWORD = "j_password";
    public static final String LOGIN_PAGE_KEY = "org.eclipse.jetty.security.jaspi.modules.LoginPage";
    public static final String ERROR_PAGE_KEY = "org.eclipse.jetty.security.jaspi.modules.ErrorPage";
    public static final String SSO_SOURCE_KEY = "org.eclipse.jetty.security.jaspi.modules.SsoSource";
    private String _formErrorPage;
    private String _formErrorPath;
    private String _formLoginPage;
    private String _formLoginPath;
    private CrossContextPsuedoSession<UserInfo> ssoSource;
    
    public FormAuthModule() {
    }
    
    public FormAuthModule(final CallbackHandler callbackHandler, final String loginPage, final String errorPage) {
        super(callbackHandler);
        this.setLoginPage(loginPage);
        this.setErrorPage(errorPage);
    }
    
    public FormAuthModule(final CallbackHandler callbackHandler, final CrossContextPsuedoSession<UserInfo> ssoSource, final String loginPage, final String errorPage) {
        super(callbackHandler);
        this.ssoSource = ssoSource;
        this.setLoginPage(loginPage);
        this.setErrorPage(errorPage);
    }
    
    @Override
    public void initialize(final MessagePolicy requestPolicy, final MessagePolicy responsePolicy, final CallbackHandler handler, final Map options) throws AuthException {
        super.initialize(requestPolicy, responsePolicy, handler, options);
        this.setLoginPage(options.get("org.eclipse.jetty.security.jaspi.modules.LoginPage"));
        this.setErrorPage(options.get("org.eclipse.jetty.security.jaspi.modules.ErrorPage"));
        this.ssoSource = (CrossContextPsuedoSession<UserInfo>)options.get("org.eclipse.jetty.security.jaspi.modules.SsoSource");
    }
    
    private void setLoginPage(String path) {
        if (!path.startsWith("/")) {
            FormAuthModule.LOG.warn("form-login-page must start with /", new Object[0]);
            path = "/" + path;
        }
        this._formLoginPage = path;
        this._formLoginPath = path;
        if (this._formLoginPath.indexOf(63) > 0) {
            this._formLoginPath = this._formLoginPath.substring(0, this._formLoginPath.indexOf(63));
        }
    }
    
    private void setErrorPage(String path) {
        if (path == null || path.trim().length() == 0) {
            this._formErrorPath = null;
            this._formErrorPage = null;
        }
        else {
            if (!path.startsWith("/")) {
                FormAuthModule.LOG.warn("form-error-page must start with /", new Object[0]);
                path = "/" + path;
            }
            this._formErrorPage = path;
            this._formErrorPath = path;
            if (this._formErrorPath.indexOf(63) > 0) {
                this._formErrorPath = this._formErrorPath.substring(0, this._formErrorPath.indexOf(63));
            }
        }
    }
    
    @Override
    public AuthStatus validateRequest(final MessageInfo messageInfo, final Subject clientSubject, final Subject serviceSubject) throws AuthException {
        final HttpServletRequest request = (HttpServletRequest)messageInfo.getRequestMessage();
        final HttpServletResponse response = (HttpServletResponse)messageInfo.getResponseMessage();
        String uri = request.getRequestURI();
        if (uri == null) {
            uri = "/";
        }
        boolean mandatory = this.isMandatory(messageInfo);
        mandatory |= this.isJSecurityCheck(uri);
        final HttpSession session = request.getSession(mandatory);
        if (!mandatory || this.isLoginOrErrorPage(URIUtil.addPaths(request.getServletPath(), request.getPathInfo()))) {
            return AuthStatus.SUCCESS;
        }
        try {
            if (this.isJSecurityCheck(uri)) {
                final String username = request.getParameter("j_username");
                final String password = request.getParameter("j_password");
                final boolean success = this.tryLogin(messageInfo, clientSubject, response, session, username, new Password(password));
                if (success) {
                    String nuri = null;
                    synchronized (session) {
                        nuri = (String)session.getAttribute("org.eclipse.jetty.util.URI");
                    }
                    if (nuri == null || nuri.length() == 0) {
                        nuri = request.getContextPath();
                        if (nuri.length() == 0) {
                            nuri = "/";
                        }
                    }
                    response.setContentLength(0);
                    response.sendRedirect(response.encodeRedirectURL(nuri));
                    return AuthStatus.SEND_CONTINUE;
                }
                if (FormAuthModule.LOG.isDebugEnabled()) {
                    FormAuthModule.LOG.debug("Form authentication FAILED for " + StringUtil.printable(username), new Object[0]);
                }
                if (this._formErrorPage == null) {
                    if (response != null) {
                        response.sendError(403);
                    }
                }
                else {
                    response.setContentLength(0);
                    response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getContextPath(), this._formErrorPage)));
                }
                return AuthStatus.SEND_FAILURE;
            }
            else {
                final FormCredential form_cred = (FormCredential)session.getAttribute("org.eclipse.jetty.server.Auth");
                if (form_cred != null) {
                    if (form_cred._subject == null) {
                        return AuthStatus.SEND_FAILURE;
                    }
                    final Set<Object> credentials = form_cred._subject.getPrivateCredentials();
                    if (credentials == null || credentials.isEmpty()) {
                        return AuthStatus.SEND_FAILURE;
                    }
                    clientSubject.getPrivateCredentials().addAll(credentials);
                    return AuthStatus.SUCCESS;
                }
                else {
                    if (this.ssoSource != null) {
                        final UserInfo userInfo = this.ssoSource.fetch(request);
                        if (userInfo != null) {
                            final boolean success = this.tryLogin(messageInfo, clientSubject, response, session, userInfo.getUserName(), new Password(new String(userInfo.getPassword())));
                            if (success) {
                                return AuthStatus.SUCCESS;
                            }
                        }
                    }
                    if (DeferredAuthentication.isDeferred(response)) {
                        return AuthStatus.SUCCESS;
                    }
                    final StringBuffer buf = request.getRequestURL();
                    if (request.getQueryString() != null) {
                        buf.append("?").append(request.getQueryString());
                    }
                    synchronized (session) {
                        session.setAttribute("org.eclipse.jetty.util.URI", buf.toString());
                    }
                    response.setContentLength(0);
                    response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getContextPath(), this._formLoginPage)));
                    return AuthStatus.SEND_CONTINUE;
                }
            }
        }
        catch (IOException e) {
            throw new AuthException(e.getMessage());
        }
        catch (UnsupportedCallbackException e2) {
            throw new AuthException(e2.getMessage());
        }
    }
    
    public boolean isJSecurityCheck(final String uri) {
        final int jsc = uri.indexOf("/j_security_check");
        if (jsc < 0) {
            return false;
        }
        final int e = jsc + "/j_security_check".length();
        if (e == uri.length()) {
            return true;
        }
        final char c = uri.charAt(e);
        return c == ';' || c == '#' || c == '/' || c == '?';
    }
    
    private boolean tryLogin(final MessageInfo messageInfo, final Subject clientSubject, final HttpServletResponse response, final HttpSession session, final String username, final Password password) throws AuthException, IOException, UnsupportedCallbackException {
        if (this.login(clientSubject, username, password, "FORM", messageInfo)) {
            final char[] pwdChars = password.toString().toCharArray();
            final Set<LoginCallbackImpl> loginCallbacks = clientSubject.getPrivateCredentials(LoginCallbackImpl.class);
            if (!loginCallbacks.isEmpty()) {
                final LoginCallbackImpl loginCallback = loginCallbacks.iterator().next();
                final FormCredential form_cred = new FormCredential(username, pwdChars, loginCallback.getUserPrincipal(), loginCallback.getSubject());
                session.setAttribute("org.eclipse.jetty.server.Auth", form_cred);
            }
            if (this.ssoSource != null) {
                final UserInfo userInfo = new UserInfo(username, pwdChars);
                this.ssoSource.store(userInfo, response);
            }
            return true;
        }
        return false;
    }
    
    public boolean isLoginOrErrorPage(final String pathInContext) {
        return pathInContext != null && (pathInContext.equals(this._formErrorPath) || pathInContext.equals(this._formLoginPath));
    }
    
    static {
        LOG = Log.getLogger(FormAuthModule.class);
    }
    
    private static class FormCredential implements Serializable, HttpSessionBindingListener
    {
        String _jUserName;
        char[] _jPassword;
        transient Principal _userPrincipal;
        transient Subject _subject;
        
        private FormCredential(final String _jUserName, final char[] _jPassword, final Principal _userPrincipal, final Subject subject) {
            this._jUserName = _jUserName;
            this._jPassword = _jPassword;
            this._userPrincipal = _userPrincipal;
            this._subject = subject;
        }
        
        public void valueBound(final HttpSessionBindingEvent event) {
        }
        
        public void valueUnbound(final HttpSessionBindingEvent event) {
            if (FormAuthModule.LOG.isDebugEnabled()) {
                FormAuthModule.LOG.debug("Logout " + this._jUserName, new Object[0]);
            }
        }
        
        @Override
        public int hashCode() {
            return this._jUserName.hashCode() + this._jPassword.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof FormCredential)) {
                return false;
            }
            final FormCredential fc = (FormCredential)o;
            return this._jUserName.equals(fc._jUserName) && Arrays.equals(this._jPassword, fc._jPassword);
        }
        
        @Override
        public String toString() {
            return "Cred[" + this._jUserName + "]";
        }
    }
}
