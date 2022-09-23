// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import javax.servlet.http.HttpUpgradeHandler;
import java.util.Collection;
import javax.servlet.http.Part;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Enumeration;
import javax.servlet.http.Cookie;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletRequestWrapper;

public class ServletRequestHttpWrapper extends ServletRequestWrapper implements HttpServletRequest
{
    public ServletRequestHttpWrapper(final ServletRequest request) {
        super(request);
    }
    
    @Override
    public String getAuthType() {
        return null;
    }
    
    @Override
    public Cookie[] getCookies() {
        return null;
    }
    
    @Override
    public long getDateHeader(final String name) {
        return 0L;
    }
    
    @Override
    public String getHeader(final String name) {
        return null;
    }
    
    @Override
    public Enumeration<String> getHeaders(final String name) {
        return null;
    }
    
    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }
    
    @Override
    public int getIntHeader(final String name) {
        return 0;
    }
    
    @Override
    public String getMethod() {
        return null;
    }
    
    @Override
    public String getPathInfo() {
        return null;
    }
    
    @Override
    public String getPathTranslated() {
        return null;
    }
    
    @Override
    public String getContextPath() {
        return null;
    }
    
    @Override
    public String getQueryString() {
        return null;
    }
    
    @Override
    public String getRemoteUser() {
        return null;
    }
    
    @Override
    public boolean isUserInRole(final String role) {
        return false;
    }
    
    @Override
    public Principal getUserPrincipal() {
        return null;
    }
    
    @Override
    public String getRequestedSessionId() {
        return null;
    }
    
    @Override
    public String getRequestURI() {
        return null;
    }
    
    @Override
    public StringBuffer getRequestURL() {
        return null;
    }
    
    @Override
    public String getServletPath() {
        return null;
    }
    
    @Override
    public HttpSession getSession(final boolean create) {
        return null;
    }
    
    @Override
    public HttpSession getSession() {
        return null;
    }
    
    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }
    
    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }
    
    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }
    
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }
    
    @Override
    public boolean authenticate(final HttpServletResponse response) throws IOException, ServletException {
        return false;
    }
    
    @Override
    public Part getPart(final String name) throws IOException, ServletException {
        return null;
    }
    
    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }
    
    @Override
    public void login(final String username, final String password) throws ServletException {
    }
    
    @Override
    public void logout() throws ServletException {
    }
    
    @Override
    public String changeSessionId() {
        return null;
    }
    
    @Override
    public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }
}
