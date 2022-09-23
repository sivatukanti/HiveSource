// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.authentication;

import org.eclipse.jetty.security.UserAuthentication;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.http.HttpServletRequestWrapper;
import org.eclipse.jetty.util.log.Log;
import javax.servlet.RequestDispatcher;
import org.eclipse.jetty.server.Response;
import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Authentication;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.UserIdentity;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.util.log.Logger;

public class FormAuthenticator extends LoginAuthenticator
{
    private static final Logger LOG;
    public static final String __FORM_LOGIN_PAGE = "org.eclipse.jetty.security.form_login_page";
    public static final String __FORM_ERROR_PAGE = "org.eclipse.jetty.security.form_error_page";
    public static final String __FORM_DISPATCH = "org.eclipse.jetty.security.dispatch";
    public static final String __J_URI = "org.eclipse.jetty.security.form_URI";
    public static final String __J_POST = "org.eclipse.jetty.security.form_POST";
    public static final String __J_METHOD = "org.eclipse.jetty.security.form_METHOD";
    public static final String __J_SECURITY_CHECK = "/j_security_check";
    public static final String __J_USERNAME = "j_username";
    public static final String __J_PASSWORD = "j_password";
    private String _formErrorPage;
    private String _formErrorPath;
    private String _formLoginPage;
    private String _formLoginPath;
    private boolean _dispatch;
    private boolean _alwaysSaveUri;
    
    public FormAuthenticator() {
    }
    
    public FormAuthenticator(final String login, final String error, final boolean dispatch) {
        this();
        if (login != null) {
            this.setLoginPage(login);
        }
        if (error != null) {
            this.setErrorPage(error);
        }
        this._dispatch = dispatch;
    }
    
    public void setAlwaysSaveUri(final boolean alwaysSave) {
        this._alwaysSaveUri = alwaysSave;
    }
    
    public boolean getAlwaysSaveUri() {
        return this._alwaysSaveUri;
    }
    
    @Override
    public void setConfiguration(final Authenticator.AuthConfiguration configuration) {
        super.setConfiguration(configuration);
        final String login = configuration.getInitParameter("org.eclipse.jetty.security.form_login_page");
        if (login != null) {
            this.setLoginPage(login);
        }
        final String error = configuration.getInitParameter("org.eclipse.jetty.security.form_error_page");
        if (error != null) {
            this.setErrorPage(error);
        }
        final String dispatch = configuration.getInitParameter("org.eclipse.jetty.security.dispatch");
        this._dispatch = ((dispatch == null) ? this._dispatch : Boolean.valueOf(dispatch));
    }
    
    @Override
    public String getAuthMethod() {
        return "FORM";
    }
    
    private void setLoginPage(String path) {
        if (!path.startsWith("/")) {
            FormAuthenticator.LOG.warn("form-login-page must start with /", new Object[0]);
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
                FormAuthenticator.LOG.warn("form-error-page must start with /", new Object[0]);
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
    public UserIdentity login(final String username, final Object password, final ServletRequest request) {
        final UserIdentity user = super.login(username, password, request);
        if (user != null) {
            final HttpSession session = ((HttpServletRequest)request).getSession(true);
            final Authentication cached = new SessionAuthentication(this.getAuthMethod(), user, password);
            session.setAttribute("org.eclipse.jetty.security.UserIdentity", cached);
        }
        return user;
    }
    
    @Override
    public void prepareRequest(final ServletRequest request) {
        final HttpServletRequest httpRequest = (HttpServletRequest)request;
        final HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("org.eclipse.jetty.security.UserIdentity") == null) {
            return;
        }
        final String juri = (String)session.getAttribute("org.eclipse.jetty.security.form_URI");
        if (juri == null || juri.length() == 0) {
            return;
        }
        final String method = (String)session.getAttribute("org.eclipse.jetty.security.form_METHOD");
        if (method == null || method.length() == 0) {
            return;
        }
        final StringBuffer buf = httpRequest.getRequestURL();
        if (httpRequest.getQueryString() != null) {
            buf.append("?").append(httpRequest.getQueryString());
        }
        if (!juri.equals(buf.toString())) {
            return;
        }
        if (FormAuthenticator.LOG.isDebugEnabled()) {
            FormAuthenticator.LOG.debug("Restoring original method {} for {} with method {}", method, juri, httpRequest.getMethod());
        }
        final Request base_request = Request.getBaseRequest(request);
        base_request.setMethod(method);
    }
    
    @Override
    public Authentication validateRequest(final ServletRequest req, final ServletResponse res, boolean mandatory) throws ServerAuthException {
        final HttpServletRequest request = (HttpServletRequest)req;
        final HttpServletResponse response = (HttpServletResponse)res;
        final Request base_request = Request.getBaseRequest(request);
        final Response base_response = base_request.getResponse();
        String uri = request.getRequestURI();
        if (uri == null) {
            uri = "/";
        }
        mandatory |= this.isJSecurityCheck(uri);
        if (!mandatory) {
            return new DeferredAuthentication(this);
        }
        if (this.isLoginOrErrorPage(URIUtil.addPaths(request.getServletPath(), request.getPathInfo())) && !DeferredAuthentication.isDeferred(response)) {
            return new DeferredAuthentication(this);
        }
        HttpSession session = null;
        try {
            session = request.getSession(true);
        }
        catch (Exception e) {
            if (FormAuthenticator.LOG.isDebugEnabled()) {
                FormAuthenticator.LOG.debug(e);
            }
        }
        if (session == null) {
            return Authentication.UNAUTHENTICATED;
        }
        try {
            if (this.isJSecurityCheck(uri)) {
                final String username = request.getParameter("j_username");
                final String password = request.getParameter("j_password");
                final UserIdentity user = this.login(username, password, request);
                FormAuthenticator.LOG.debug("jsecuritycheck {} {}", username, user);
                session = request.getSession(true);
                if (user != null) {
                    String nuri;
                    final FormAuthentication form_auth;
                    synchronized (session) {
                        nuri = (String)session.getAttribute("org.eclipse.jetty.security.form_URI");
                        if (nuri == null || nuri.length() == 0) {
                            nuri = request.getContextPath();
                            if (nuri.length() == 0) {
                                nuri = "/";
                            }
                        }
                        form_auth = new FormAuthentication(this.getAuthMethod(), user);
                    }
                    FormAuthenticator.LOG.debug("authenticated {}->{}", form_auth, nuri);
                    response.setContentLength(0);
                    final int redirectCode = (base_request.getHttpVersion().getVersion() < HttpVersion.HTTP_1_1.getVersion()) ? 302 : 303;
                    base_response.sendRedirect(redirectCode, response.encodeRedirectURL(nuri));
                    return form_auth;
                }
                if (FormAuthenticator.LOG.isDebugEnabled()) {
                    FormAuthenticator.LOG.debug("Form authentication FAILED for " + StringUtil.printable(username), new Object[0]);
                }
                if (this._formErrorPage == null) {
                    FormAuthenticator.LOG.debug("auth failed {}->403", username);
                    if (response != null) {
                        response.sendError(403);
                    }
                }
                else if (this._dispatch) {
                    FormAuthenticator.LOG.debug("auth failed {}=={}", username, this._formErrorPage);
                    final RequestDispatcher dispatcher = request.getRequestDispatcher(this._formErrorPage);
                    response.setHeader(HttpHeader.CACHE_CONTROL.asString(), HttpHeaderValue.NO_CACHE.asString());
                    response.setDateHeader(HttpHeader.EXPIRES.asString(), 1L);
                    dispatcher.forward(new FormRequest(request), new FormResponse(response));
                }
                else {
                    FormAuthenticator.LOG.debug("auth failed {}->{}", username, this._formErrorPage);
                    final int redirectCode2 = (base_request.getHttpVersion().getVersion() < HttpVersion.HTTP_1_1.getVersion()) ? 302 : 303;
                    base_response.sendRedirect(redirectCode2, response.encodeRedirectURL(URIUtil.addPaths(request.getContextPath(), this._formErrorPage)));
                }
                return Authentication.SEND_FAILURE;
            }
            else {
                final Authentication authentication = (Authentication)session.getAttribute("org.eclipse.jetty.security.UserIdentity");
                if (authentication != null) {
                    if (!(authentication instanceof Authentication.User) || this._loginService == null || this._loginService.validate(((Authentication.User)authentication).getUserIdentity())) {
                        synchronized (session) {
                            final String j_uri = (String)session.getAttribute("org.eclipse.jetty.security.form_URI");
                            if (j_uri != null) {
                                FormAuthenticator.LOG.debug("auth retry {}->{}", authentication, j_uri);
                                final StringBuffer buf = request.getRequestURL();
                                if (request.getQueryString() != null) {
                                    buf.append("?").append(request.getQueryString());
                                }
                                if (j_uri.equals(buf.toString())) {
                                    final MultiMap<String> j_post = (MultiMap<String>)session.getAttribute("org.eclipse.jetty.security.form_POST");
                                    if (j_post != null) {
                                        FormAuthenticator.LOG.debug("auth rePOST {}->{}", authentication, j_uri);
                                        base_request.setContentParameters(j_post);
                                    }
                                    session.removeAttribute("org.eclipse.jetty.security.form_URI");
                                    session.removeAttribute("org.eclipse.jetty.security.form_METHOD");
                                    session.removeAttribute("org.eclipse.jetty.security.form_POST");
                                }
                            }
                        }
                        FormAuthenticator.LOG.debug("auth {}", authentication);
                        return authentication;
                    }
                    FormAuthenticator.LOG.debug("auth revoked {}", authentication);
                    session.removeAttribute("org.eclipse.jetty.security.UserIdentity");
                }
                if (DeferredAuthentication.isDeferred(response)) {
                    FormAuthenticator.LOG.debug("auth deferred {}", session.getId());
                    return Authentication.UNAUTHENTICATED;
                }
                synchronized (session) {
                    if (session.getAttribute("org.eclipse.jetty.security.form_URI") == null || this._alwaysSaveUri) {
                        final StringBuffer buf2 = request.getRequestURL();
                        if (request.getQueryString() != null) {
                            buf2.append("?").append(request.getQueryString());
                        }
                        session.setAttribute("org.eclipse.jetty.security.form_URI", buf2.toString());
                        session.setAttribute("org.eclipse.jetty.security.form_METHOD", request.getMethod());
                        if (MimeTypes.Type.FORM_ENCODED.is(req.getContentType()) && HttpMethod.POST.is(request.getMethod())) {
                            final MultiMap<String> formParameters = new MultiMap<String>();
                            base_request.extractFormParameters(formParameters);
                            session.setAttribute("org.eclipse.jetty.security.form_POST", formParameters);
                        }
                    }
                }
                if (this._dispatch) {
                    FormAuthenticator.LOG.debug("challenge {}=={}", session.getId(), this._formLoginPage);
                    final RequestDispatcher dispatcher2 = request.getRequestDispatcher(this._formLoginPage);
                    response.setHeader(HttpHeader.CACHE_CONTROL.asString(), HttpHeaderValue.NO_CACHE.asString());
                    response.setDateHeader(HttpHeader.EXPIRES.asString(), 1L);
                    dispatcher2.forward(new FormRequest(request), new FormResponse(response));
                }
                else {
                    FormAuthenticator.LOG.debug("challenge {}->{}", session.getId(), this._formLoginPage);
                    final int redirectCode3 = (base_request.getHttpVersion().getVersion() < HttpVersion.HTTP_1_1.getVersion()) ? 302 : 303;
                    base_response.sendRedirect(redirectCode3, response.encodeRedirectURL(URIUtil.addPaths(request.getContextPath(), this._formLoginPage)));
                }
                return Authentication.SEND_CONTINUE;
            }
        }
        catch (IOException | ServletException ex2) {
            final Exception ex;
            final Exception e = ex;
            throw new ServerAuthException(e);
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
    
    public boolean isLoginOrErrorPage(final String pathInContext) {
        return pathInContext != null && (pathInContext.equals(this._formErrorPath) || pathInContext.equals(this._formLoginPath));
    }
    
    @Override
    public boolean secureResponse(final ServletRequest req, final ServletResponse res, final boolean mandatory, final Authentication.User validatedUser) throws ServerAuthException {
        return true;
    }
    
    static {
        LOG = Log.getLogger(FormAuthenticator.class);
    }
    
    protected static class FormRequest extends HttpServletRequestWrapper
    {
        public FormRequest(final HttpServletRequest request) {
            super(request);
        }
        
        @Override
        public long getDateHeader(final String name) {
            if (name.toLowerCase(Locale.ENGLISH).startsWith("if-")) {
                return -1L;
            }
            return super.getDateHeader(name);
        }
        
        @Override
        public String getHeader(final String name) {
            if (name.toLowerCase(Locale.ENGLISH).startsWith("if-")) {
                return null;
            }
            return super.getHeader(name);
        }
        
        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(Collections.list(super.getHeaderNames()));
        }
        
        @Override
        public Enumeration<String> getHeaders(final String name) {
            if (name.toLowerCase(Locale.ENGLISH).startsWith("if-")) {
                return Collections.enumeration((Collection<String>)Collections.emptyList());
            }
            return super.getHeaders(name);
        }
    }
    
    protected static class FormResponse extends HttpServletResponseWrapper
    {
        public FormResponse(final HttpServletResponse response) {
            super(response);
        }
        
        @Override
        public void addDateHeader(final String name, final long date) {
            if (this.notIgnored(name)) {
                super.addDateHeader(name, date);
            }
        }
        
        @Override
        public void addHeader(final String name, final String value) {
            if (this.notIgnored(name)) {
                super.addHeader(name, value);
            }
        }
        
        @Override
        public void setDateHeader(final String name, final long date) {
            if (this.notIgnored(name)) {
                super.setDateHeader(name, date);
            }
        }
        
        @Override
        public void setHeader(final String name, final String value) {
            if (this.notIgnored(name)) {
                super.setHeader(name, value);
            }
        }
        
        private boolean notIgnored(final String name) {
            return !HttpHeader.CACHE_CONTROL.is(name) && !HttpHeader.PRAGMA.is(name) && !HttpHeader.ETAG.is(name) && !HttpHeader.EXPIRES.is(name) && !HttpHeader.LAST_MODIFIED.is(name) && !HttpHeader.AGE.is(name);
        }
    }
    
    public static class FormAuthentication extends UserAuthentication implements Authentication.ResponseSent
    {
        public FormAuthentication(final String method, final UserIdentity userIdentity) {
            super(method, userIdentity);
        }
        
        @Override
        public String toString() {
            return "Form" + super.toString();
        }
    }
}
