// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.authentication;

import javax.servlet.WriteListener;
import java.util.Collections;
import java.util.Collection;
import org.eclipse.jetty.util.IO;
import java.io.PrintWriter;
import java.util.Locale;
import java.io.IOException;
import javax.servlet.http.Cookie;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.ServerAuthException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.Authentication;

public class DeferredAuthentication implements Authentication.Deferred
{
    private static final Logger LOG;
    protected final LoginAuthenticator _authenticator;
    private Object _previousAssociation;
    static final HttpServletResponse __deferredResponse;
    private static ServletOutputStream __nullOut;
    
    public DeferredAuthentication(final LoginAuthenticator authenticator) {
        if (authenticator == null) {
            throw new NullPointerException("No Authenticator");
        }
        this._authenticator = authenticator;
    }
    
    @Override
    public Authentication authenticate(final ServletRequest request) {
        try {
            final Authentication authentication = this._authenticator.validateRequest(request, DeferredAuthentication.__deferredResponse, true);
            if (authentication != null && authentication instanceof Authentication.User && !(authentication instanceof Authentication.ResponseSent)) {
                final LoginService login_service = this._authenticator.getLoginService();
                final IdentityService identity_service = login_service.getIdentityService();
                if (identity_service != null) {
                    this._previousAssociation = identity_service.associate(((Authentication.User)authentication).getUserIdentity());
                }
                return authentication;
            }
        }
        catch (ServerAuthException e) {
            DeferredAuthentication.LOG.debug(e);
        }
        return this;
    }
    
    @Override
    public Authentication authenticate(final ServletRequest request, final ServletResponse response) {
        try {
            final LoginService login_service = this._authenticator.getLoginService();
            final IdentityService identity_service = login_service.getIdentityService();
            final Authentication authentication = this._authenticator.validateRequest(request, response, true);
            if (authentication instanceof Authentication.User && identity_service != null) {
                this._previousAssociation = identity_service.associate(((Authentication.User)authentication).getUserIdentity());
            }
            return authentication;
        }
        catch (ServerAuthException e) {
            DeferredAuthentication.LOG.debug(e);
            return this;
        }
    }
    
    @Override
    public Authentication login(final String username, final Object password, final ServletRequest request) {
        if (username == null) {
            return null;
        }
        final UserIdentity identity = this._authenticator.login(username, password, request);
        if (identity != null) {
            final IdentityService identity_service = this._authenticator.getLoginService().getIdentityService();
            final UserAuthentication authentication = new UserAuthentication("API", identity);
            if (identity_service != null) {
                this._previousAssociation = identity_service.associate(identity);
            }
            return authentication;
        }
        return null;
    }
    
    public Object getPreviousAssociation() {
        return this._previousAssociation;
    }
    
    public static boolean isDeferred(final HttpServletResponse response) {
        return response == DeferredAuthentication.__deferredResponse;
    }
    
    static {
        LOG = Log.getLogger(DeferredAuthentication.class);
        __deferredResponse = new HttpServletResponse() {
            @Override
            public void addCookie(final Cookie cookie) {
            }
            
            @Override
            public void addDateHeader(final String name, final long date) {
            }
            
            @Override
            public void addHeader(final String name, final String value) {
            }
            
            @Override
            public void addIntHeader(final String name, final int value) {
            }
            
            @Override
            public boolean containsHeader(final String name) {
                return false;
            }
            
            @Override
            public String encodeRedirectURL(final String url) {
                return null;
            }
            
            @Override
            public String encodeRedirectUrl(final String url) {
                return null;
            }
            
            @Override
            public String encodeURL(final String url) {
                return null;
            }
            
            @Override
            public String encodeUrl(final String url) {
                return null;
            }
            
            @Override
            public void sendError(final int sc) throws IOException {
            }
            
            @Override
            public void sendError(final int sc, final String msg) throws IOException {
            }
            
            @Override
            public void sendRedirect(final String location) throws IOException {
            }
            
            @Override
            public void setDateHeader(final String name, final long date) {
            }
            
            @Override
            public void setHeader(final String name, final String value) {
            }
            
            @Override
            public void setIntHeader(final String name, final int value) {
            }
            
            @Override
            public void setStatus(final int sc) {
            }
            
            @Override
            public void setStatus(final int sc, final String sm) {
            }
            
            @Override
            public void flushBuffer() throws IOException {
            }
            
            @Override
            public int getBufferSize() {
                return 1024;
            }
            
            @Override
            public String getCharacterEncoding() {
                return null;
            }
            
            @Override
            public String getContentType() {
                return null;
            }
            
            @Override
            public Locale getLocale() {
                return null;
            }
            
            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return DeferredAuthentication.__nullOut;
            }
            
            @Override
            public PrintWriter getWriter() throws IOException {
                return IO.getNullPrintWriter();
            }
            
            @Override
            public boolean isCommitted() {
                return true;
            }
            
            @Override
            public void reset() {
            }
            
            @Override
            public void resetBuffer() {
            }
            
            @Override
            public void setBufferSize(final int size) {
            }
            
            @Override
            public void setCharacterEncoding(final String charset) {
            }
            
            @Override
            public void setContentLength(final int len) {
            }
            
            @Override
            public void setContentLengthLong(final long len) {
            }
            
            @Override
            public void setContentType(final String type) {
            }
            
            @Override
            public void setLocale(final Locale loc) {
            }
            
            @Override
            public Collection<String> getHeaderNames() {
                return (Collection<String>)Collections.emptyList();
            }
            
            @Override
            public String getHeader(final String arg0) {
                return null;
            }
            
            @Override
            public Collection<String> getHeaders(final String arg0) {
                return (Collection<String>)Collections.emptyList();
            }
            
            @Override
            public int getStatus() {
                return 0;
            }
        };
        DeferredAuthentication.__nullOut = new ServletOutputStream() {
            @Override
            public void write(final int b) throws IOException {
            }
            
            @Override
            public void print(final String s) throws IOException {
            }
            
            @Override
            public void println(final String s) throws IOException {
            }
            
            @Override
            public void setWriteListener(final WriteListener writeListener) {
            }
            
            @Override
            public boolean isReady() {
                return false;
            }
        };
    }
}
