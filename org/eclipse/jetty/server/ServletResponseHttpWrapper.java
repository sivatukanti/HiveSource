// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.Collection;
import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletResponseWrapper;

public class ServletResponseHttpWrapper extends ServletResponseWrapper implements HttpServletResponse
{
    public ServletResponseHttpWrapper(final ServletResponse response) {
        super(response);
    }
    
    @Override
    public void addCookie(final Cookie cookie) {
    }
    
    @Override
    public boolean containsHeader(final String name) {
        return false;
    }
    
    @Override
    public String encodeURL(final String url) {
        return null;
    }
    
    @Override
    public String encodeRedirectURL(final String url) {
        return null;
    }
    
    @Override
    public String encodeUrl(final String url) {
        return null;
    }
    
    @Override
    public String encodeRedirectUrl(final String url) {
        return null;
    }
    
    @Override
    public void sendError(final int sc, final String msg) throws IOException {
    }
    
    @Override
    public void sendError(final int sc) throws IOException {
    }
    
    @Override
    public void sendRedirect(final String location) throws IOException {
    }
    
    @Override
    public void setDateHeader(final String name, final long date) {
    }
    
    @Override
    public void addDateHeader(final String name, final long date) {
    }
    
    @Override
    public void setHeader(final String name, final String value) {
    }
    
    @Override
    public void addHeader(final String name, final String value) {
    }
    
    @Override
    public void setIntHeader(final String name, final int value) {
    }
    
    @Override
    public void addIntHeader(final String name, final int value) {
    }
    
    @Override
    public void setStatus(final int sc) {
    }
    
    @Override
    public void setStatus(final int sc, final String sm) {
    }
    
    @Override
    public String getHeader(final String name) {
        return null;
    }
    
    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }
    
    @Override
    public Collection<String> getHeaders(final String name) {
        return null;
    }
    
    @Override
    public int getStatus() {
        return 0;
    }
}
