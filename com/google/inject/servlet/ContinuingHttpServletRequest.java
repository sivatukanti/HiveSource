// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.http.Cookie;
import java.io.IOException;
import javax.servlet.ServletInputStream;
import com.google.inject.OutOfScopeException;
import javax.servlet.http.HttpSession;
import com.google.inject.internal.util.$Maps;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import javax.servlet.http.HttpServletRequestWrapper;

class ContinuingHttpServletRequest extends HttpServletRequestWrapper
{
    private final Map<String, Object> attributes;
    
    public ContinuingHttpServletRequest(final HttpServletRequest request) {
        super(request);
        this.attributes = (Map<String, Object>)$Maps.newHashMap();
    }
    
    @Override
    public HttpSession getSession() {
        throw new OutOfScopeException("Cannot access the session in a continued request");
    }
    
    @Override
    public HttpSession getSession(final boolean create) {
        throw new UnsupportedOperationException("Cannot access the session in a continued request");
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Cannot access raw request on a continued request");
    }
    
    @Override
    public void setAttribute(final String name, final Object o) {
        this.attributes.put(name, o);
    }
    
    @Override
    public void removeAttribute(final String name) {
        this.attributes.remove(name);
    }
    
    @Override
    public Object getAttribute(final String name) {
        return this.attributes.get(name);
    }
    
    @Override
    public Cookie[] getCookies() {
        return super.getCookies().clone();
    }
}
