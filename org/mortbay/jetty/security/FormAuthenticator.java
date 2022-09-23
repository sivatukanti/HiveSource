// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.io.Serializable;
import java.io.IOException;
import javax.servlet.http.HttpSession;
import org.mortbay.util.URIUtil;
import org.mortbay.util.StringUtil;
import java.security.Principal;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Request;
import org.mortbay.log.Log;

public class FormAuthenticator implements Authenticator
{
    public static final String __J_URI = "org.mortbay.jetty.URI";
    public static final String __J_AUTHENTICATED = "org.mortbay.jetty.Auth";
    public static final String __J_SECURITY_CHECK = "/j_security_check";
    public static final String __J_USERNAME = "j_username";
    public static final String __J_PASSWORD = "j_password";
    private String _formErrorPage;
    private String _formErrorPath;
    private String _formLoginPage;
    private String _formLoginPath;
    
    public String getAuthMethod() {
        return "FORM";
    }
    
    public void setLoginPage(String path) {
        if (!path.startsWith("/")) {
            Log.warn("form-login-page must start with /");
            path = "/" + path;
        }
        this._formLoginPage = path;
        this._formLoginPath = path;
        if (this._formLoginPath.indexOf(63) > 0) {
            this._formLoginPath = this._formLoginPath.substring(0, this._formLoginPath.indexOf(63));
        }
    }
    
    public String getLoginPage() {
        return this._formLoginPage;
    }
    
    public void setErrorPage(String path) {
        if (path == null || path.trim().length() == 0) {
            this._formErrorPath = null;
            this._formErrorPage = null;
        }
        else {
            if (!path.startsWith("/")) {
                Log.warn("form-error-page must start with /");
                path = "/" + path;
            }
            this._formErrorPage = path;
            this._formErrorPath = path;
            if (this._formErrorPath != null && this._formErrorPath.indexOf(63) > 0) {
                this._formErrorPath = this._formErrorPath.substring(0, this._formErrorPath.indexOf(63));
            }
        }
    }
    
    public String getErrorPage() {
        return this._formErrorPage;
    }
    
    public Principal authenticate(final UserRealm realm, final String pathInContext, final Request request, final Response response) throws IOException {
        String uri = pathInContext;
        final HttpSession session = request.getSession(response != null);
        if (session == null) {
            return null;
        }
        if (this.isJSecurityCheck(uri)) {
            final FormCredential form_cred = new FormCredential();
            form_cred.authenticate(realm, request.getParameter("j_username"), request.getParameter("j_password"), request);
            String nuri = (String)session.getAttribute("org.mortbay.jetty.URI");
            if (nuri == null || nuri.length() == 0) {
                nuri = request.getContextPath();
                if (nuri.length() == 0) {
                    nuri = "/";
                }
            }
            if (form_cred._userPrincipal != null) {
                if (Log.isDebugEnabled()) {
                    Log.debug("Form authentication OK for " + form_cred._jUserName);
                }
                session.removeAttribute("org.mortbay.jetty.URI");
                request.setAuthType("FORM");
                request.setUserPrincipal(form_cred._userPrincipal);
                session.setAttribute("org.mortbay.jetty.Auth", form_cred);
                if (realm instanceof SSORealm) {
                    ((SSORealm)realm).setSingleSignOn(request, response, form_cred._userPrincipal, new Password(form_cred._jPassword));
                }
                if (response != null) {
                    response.setContentLength(0);
                    response.sendRedirect(response.encodeRedirectURL(nuri));
                }
            }
            else {
                if (Log.isDebugEnabled()) {
                    Log.debug("Form authentication FAILED for " + StringUtil.printable(form_cred._jUserName));
                }
                if (response != null) {
                    if (this._formErrorPage == null) {
                        response.sendError(403);
                    }
                    else {
                        response.setContentLength(0);
                        response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getContextPath(), this._formErrorPage)));
                    }
                }
            }
            return null;
        }
        FormCredential form_cred = (FormCredential)session.getAttribute("org.mortbay.jetty.Auth");
        if (form_cred != null) {
            if (form_cred._userPrincipal == null) {
                form_cred.authenticate(realm, request);
                if (form_cred._userPrincipal != null && realm instanceof SSORealm) {
                    ((SSORealm)realm).setSingleSignOn(request, response, form_cred._userPrincipal, new Password(form_cred._jPassword));
                }
            }
            else if (!realm.reauthenticate(form_cred._userPrincipal)) {
                form_cred._userPrincipal = null;
            }
            if (form_cred._userPrincipal != null) {
                if (Log.isDebugEnabled()) {
                    Log.debug("FORM Authenticated for " + form_cred._userPrincipal.getName());
                }
                request.setAuthType("FORM");
                request.setUserPrincipal(form_cred._userPrincipal);
                return form_cred._userPrincipal;
            }
            session.setAttribute("org.mortbay.jetty.Auth", null);
        }
        else if (realm instanceof SSORealm) {
            final Credential cred = ((SSORealm)realm).getSingleSignOn(request, response);
            if (request.getUserPrincipal() != null) {
                form_cred = new FormCredential();
                form_cred._userPrincipal = request.getUserPrincipal();
                form_cred._jUserName = form_cred._userPrincipal.getName();
                if (cred != null) {
                    form_cred._jPassword = cred.toString();
                }
                if (Log.isDebugEnabled()) {
                    Log.debug("SSO for " + form_cred._userPrincipal);
                }
                request.setAuthType("FORM");
                session.setAttribute("org.mortbay.jetty.Auth", form_cred);
                return form_cred._userPrincipal;
            }
        }
        if (this.isLoginOrErrorPage(pathInContext)) {
            return SecurityHandler.__NOBODY;
        }
        if (response != null) {
            if (request.getQueryString() != null) {
                uri = uri + "?" + request.getQueryString();
            }
            session.setAttribute("org.mortbay.jetty.URI", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + URIUtil.addPaths(request.getContextPath(), uri));
            response.setContentLength(0);
            response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getContextPath(), this._formLoginPage)));
        }
        return null;
    }
    
    public boolean isLoginOrErrorPage(final String pathInContext) {
        return pathInContext != null && (pathInContext.equals(this._formErrorPath) || pathInContext.equals(this._formLoginPath));
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
    
    private static class FormCredential implements Serializable, HttpSessionBindingListener
    {
        String _jUserName;
        String _jPassword;
        transient Principal _userPrincipal;
        transient UserRealm _realm;
        
        void authenticate(final UserRealm realm, final String user, final String password, final Request request) {
            this._jUserName = user;
            this._jPassword = password;
            this._userPrincipal = realm.authenticate(user, password, request);
            if (this._userPrincipal != null) {
                this._realm = realm;
            }
            else {
                Log.warn("AUTH FAILURE: user {}", StringUtil.printable(user));
                request.setUserPrincipal(null);
            }
        }
        
        void authenticate(final UserRealm realm, final Request request) {
            this._userPrincipal = realm.authenticate(this._jUserName, this._jPassword, request);
            if (this._userPrincipal != null) {
                this._realm = realm;
            }
            else {
                Log.warn("AUTH FAILURE: user {}", StringUtil.printable(this._jUserName));
                request.setUserPrincipal(null);
            }
        }
        
        public void valueBound(final HttpSessionBindingEvent event) {
        }
        
        public void valueUnbound(final HttpSessionBindingEvent event) {
            if (Log.isDebugEnabled()) {
                Log.debug("Logout " + this._jUserName);
            }
            if (this._realm instanceof SSORealm) {
                ((SSORealm)this._realm).clearSingleSignOn(this._jUserName);
            }
            if (this._realm != null && this._userPrincipal != null) {
                this._realm.logout(this._userPrincipal);
            }
        }
        
        public int hashCode() {
            return this._jUserName.hashCode() + this._jPassword.hashCode();
        }
        
        public boolean equals(final Object o) {
            if (!(o instanceof FormCredential)) {
                return false;
            }
            final FormCredential fc = (FormCredential)o;
            return this._jUserName.equals(fc._jUserName) && this._jPassword.equals(fc._jPassword);
        }
        
        public String toString() {
            return "Cred[" + this._jUserName + "]";
        }
    }
}
