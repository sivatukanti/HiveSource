// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import org.mortbay.jetty.Connector;
import org.mortbay.util.StringUtil;
import java.util.Map;
import javax.servlet.ServletException;
import java.io.IOException;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.log.Log;
import org.mortbay.util.LazyList;
import java.security.Principal;
import org.mortbay.jetty.servlet.PathMap;
import org.mortbay.jetty.handler.HandlerWrapper;

public class SecurityHandler extends HandlerWrapper
{
    private String _authMethod;
    private UserRealm _userRealm;
    private ConstraintMapping[] _constraintMappings;
    private PathMap _constraintMap;
    private Authenticator _authenticator;
    private NotChecked _notChecked;
    private boolean _checkWelcomeFiles;
    public static Principal __NO_USER;
    public static Principal __NOBODY;
    
    public SecurityHandler() {
        this._authMethod = "BASIC";
        this._constraintMap = new PathMap();
        this._notChecked = new NotChecked();
        this._checkWelcomeFiles = false;
    }
    
    public Authenticator getAuthenticator() {
        return this._authenticator;
    }
    
    public void setAuthenticator(final Authenticator authenticator) {
        this._authenticator = authenticator;
    }
    
    public UserRealm getUserRealm() {
        return this._userRealm;
    }
    
    public void setUserRealm(final UserRealm userRealm) {
        this._userRealm = userRealm;
    }
    
    public ConstraintMapping[] getConstraintMappings() {
        return this._constraintMappings;
    }
    
    public void setConstraintMappings(final ConstraintMapping[] constraintMappings) {
        this._constraintMappings = constraintMappings;
        if (this._constraintMappings != null) {
            this._constraintMappings = constraintMappings;
            this._constraintMap.clear();
            for (int i = 0; i < this._constraintMappings.length; ++i) {
                Object mappings = this._constraintMap.get(this._constraintMappings[i].getPathSpec());
                mappings = LazyList.add(mappings, this._constraintMappings[i]);
                this._constraintMap.put(this._constraintMappings[i].getPathSpec(), mappings);
            }
        }
    }
    
    public String getAuthMethod() {
        return this._authMethod;
    }
    
    public void setAuthMethod(final String method) {
        if (this.isStarted() && this._authMethod != null && !this._authMethod.equals(method)) {
            throw new IllegalStateException("Handler started");
        }
        this._authMethod = method;
    }
    
    public boolean hasConstraints() {
        return this._constraintMappings != null && this._constraintMappings.length > 0;
    }
    
    public boolean isCheckWelcomeFiles() {
        return this._checkWelcomeFiles;
    }
    
    public void setCheckWelcomeFiles(final boolean authenticateWelcomeFiles) {
        this._checkWelcomeFiles = authenticateWelcomeFiles;
    }
    
    public void doStart() throws Exception {
        if (this._authenticator == null) {
            if ("BASIC".equalsIgnoreCase(this._authMethod)) {
                this._authenticator = new BasicAuthenticator();
            }
            else if ("DIGEST".equalsIgnoreCase(this._authMethod)) {
                this._authenticator = new DigestAuthenticator();
            }
            else if ("CLIENT_CERT".equalsIgnoreCase(this._authMethod)) {
                this._authenticator = new ClientCertAuthenticator();
            }
            else if ("FORM".equalsIgnoreCase(this._authMethod)) {
                this._authenticator = new FormAuthenticator();
            }
            else {
                Log.warn("Unknown Authentication method:" + this._authMethod);
            }
        }
        super.doStart();
    }
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
        final Response base_response = (Response)((response instanceof Response) ? response : HttpConnection.getCurrentConnection().getResponse());
        final UserRealm old_realm = base_request.getUserRealm();
        try {
            base_request.setUserRealm(this.getUserRealm());
            if (dispatch == 1 && !this.checkSecurityConstraints(target, base_request, base_response)) {
                base_request.setHandled(true);
                return;
            }
            if (dispatch == 2 && this._checkWelcomeFiles && request.getAttribute("org.mortbay.jetty.welcome") != null) {
                request.removeAttribute("org.mortbay.jetty.welcome");
                if (!this.checkSecurityConstraints(target, base_request, base_response)) {
                    base_request.setHandled(true);
                    return;
                }
            }
            if (this._authenticator instanceof FormAuthenticator && target.endsWith("/j_security_check")) {
                this._authenticator.authenticate(this.getUserRealm(), target, base_request, base_response);
                base_request.setHandled(true);
                return;
            }
            if (this.getHandler() != null) {
                this.getHandler().handle(target, request, response, dispatch);
            }
        }
        finally {
            if (this._userRealm != null && dispatch == 1) {
                this._userRealm.disassociate(base_request.getUserPrincipal());
            }
            base_request.setUserRealm(old_realm);
        }
    }
    
    public boolean checkSecurityConstraints(final String pathInContext, final Request request, final Response response) throws IOException {
        final Object mapping_entries = this._constraintMap.getLazyMatches(pathInContext);
        String pattern = null;
        Object constraints = null;
        if (mapping_entries != null) {
        Label_0164:
            for (int m = 0; m < LazyList.size(mapping_entries); ++m) {
                final Map.Entry entry = (Map.Entry)LazyList.get(mapping_entries, m);
                final Object mappings = entry.getValue();
                final String path_spec = entry.getKey();
                for (int c = 0; c < LazyList.size(mappings); ++c) {
                    final ConstraintMapping mapping = (ConstraintMapping)LazyList.get(mappings, c);
                    if (mapping.getMethod() == null || mapping.getMethod().equalsIgnoreCase(request.getMethod())) {
                        if (pattern != null && !pattern.equals(path_spec)) {
                            break Label_0164;
                        }
                        pattern = path_spec;
                        constraints = LazyList.add(constraints, mapping.getConstraint());
                    }
                }
            }
            return this.check(constraints, this._authenticator, this._userRealm, pathInContext, request, response);
        }
        request.setUserPrincipal(this._notChecked);
        return true;
    }
    
    private boolean check(final Object constraints, final Authenticator authenticator, final UserRealm realm, final String pathInContext, final Request request, final Response response) throws IOException {
        int dataConstraint = 0;
        Object roles = null;
        boolean unauthenticated = false;
        boolean forbidden = false;
        for (int c = 0; c < LazyList.size(constraints); ++c) {
            final Constraint sc = (Constraint)LazyList.get(constraints, c);
            if (dataConstraint > -1 && sc.hasDataConstraint()) {
                if (sc.getDataConstraint() > dataConstraint) {
                    dataConstraint = sc.getDataConstraint();
                }
            }
            else {
                dataConstraint = -1;
            }
            if (!unauthenticated && !forbidden) {
                if (sc.getAuthenticate()) {
                    if (sc.isAnyRole()) {
                        roles = "*";
                    }
                    else {
                        final String[] scr = sc.getRoles();
                        if (scr == null || scr.length == 0) {
                            forbidden = true;
                            break;
                        }
                        if (roles != "*") {
                            int r = scr.length;
                            while (r-- > 0) {
                                roles = LazyList.add(roles, scr[r]);
                            }
                        }
                    }
                }
                else {
                    unauthenticated = true;
                }
            }
        }
        if (forbidden && (!(authenticator instanceof FormAuthenticator) || !((FormAuthenticator)authenticator).isLoginOrErrorPage(pathInContext))) {
            response.sendError(403);
            return false;
        }
        if (dataConstraint > 0) {
            final HttpConnection connection = HttpConnection.getCurrentConnection();
            final Connector connector = connection.getConnector();
            switch (dataConstraint) {
                case 1: {
                    if (connector.isIntegral(request)) {
                        break;
                    }
                    if (connector.getConfidentialPort() > 0) {
                        String url = connector.getIntegralScheme() + "://" + request.getServerName() + ":" + connector.getIntegralPort() + request.getRequestURI();
                        if (request.getQueryString() != null) {
                            url = url + "?" + request.getQueryString();
                        }
                        response.setContentLength(0);
                        response.sendRedirect(response.encodeRedirectURL(url));
                    }
                    else {
                        response.sendError(403, null);
                    }
                    return false;
                }
                case 2: {
                    if (connector.isConfidential(request)) {
                        break;
                    }
                    if (connector.getConfidentialPort() > 0) {
                        String url = connector.getConfidentialScheme() + "://" + request.getServerName() + ":" + connector.getConfidentialPort() + request.getRequestURI();
                        if (request.getQueryString() != null) {
                            url = url + "?" + request.getQueryString();
                        }
                        response.setContentLength(0);
                        response.sendRedirect(response.encodeRedirectURL(url));
                    }
                    else {
                        response.sendError(403, null);
                    }
                    return false;
                }
                default: {
                    response.sendError(403, null);
                    return false;
                }
            }
        }
        if (!unauthenticated && roles != null) {
            if (realm == null) {
                Log.warn("Request " + request.getRequestURI() + " failed - no realm");
                response.sendError(500, "No realm");
                return false;
            }
            Principal user = null;
            if (request.getAuthType() != null && request.getRemoteUser() != null) {
                user = request.getUserPrincipal();
                if (user == null) {
                    user = realm.authenticate(request.getRemoteUser(), null, request);
                }
                if (user == null && authenticator != null) {
                    user = authenticator.authenticate(realm, pathInContext, request, response);
                }
            }
            else if (authenticator != null) {
                user = authenticator.authenticate(realm, pathInContext, request, response);
            }
            else {
                Log.warn("Mis-configured Authenticator for " + request.getRequestURI());
                response.sendError(500, "Configuration error");
            }
            if (user == null) {
                return false;
            }
            if (user == SecurityHandler.__NOBODY) {
                return true;
            }
            if (roles != "*") {
                boolean inRole = false;
                int r2 = LazyList.size(roles);
                while (r2-- > 0) {
                    if (realm.isUserInRole(user, (String)LazyList.get(roles, r2))) {
                        inRole = true;
                        break;
                    }
                }
                if (!inRole) {
                    Log.warn("AUTH FAILURE: incorrect role for " + StringUtil.printable(user.getName()));
                    response.sendError(403, "User not in required role");
                    return false;
                }
            }
        }
        else {
            request.setUserPrincipal(this._notChecked);
        }
        return true;
    }
    
    static {
        SecurityHandler.__NO_USER = new Principal() {
            public String getName() {
                return null;
            }
            
            public String toString() {
                return "No User";
            }
        };
        SecurityHandler.__NOBODY = new Principal() {
            public String getName() {
                return "Nobody";
            }
            
            public String toString() {
                return this.getName();
            }
        };
    }
    
    public class NotChecked implements Principal
    {
        public String getName() {
            return null;
        }
        
        public String toString() {
            return "NOT CHECKED";
        }
        
        public SecurityHandler getSecurityHandler() {
            return SecurityHandler.this;
        }
    }
}
