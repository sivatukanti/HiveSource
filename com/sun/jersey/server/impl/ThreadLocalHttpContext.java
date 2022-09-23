// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl;

import java.util.Map;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.api.core.HttpContext;

public final class ThreadLocalHttpContext implements HttpContext
{
    private ThreadLocal<HttpContext> context;
    
    public ThreadLocalHttpContext() {
        this.context = new ThreadLocal<HttpContext>();
    }
    
    public void set(final HttpContext context) {
        this.context.set(context);
    }
    
    public HttpContext get() {
        return this.context.get();
    }
    
    @Override
    public ExtendedUriInfo getUriInfo() {
        try {
            return this.context.get().getUriInfo();
        }
        catch (NullPointerException ex) {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public HttpRequestContext getRequest() {
        try {
            return this.context.get().getRequest();
        }
        catch (NullPointerException ex) {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public HttpResponseContext getResponse() {
        try {
            return this.context.get().getResponse();
        }
        catch (NullPointerException ex) {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public Map<String, Object> getProperties() {
        try {
            return this.context.get().getProperties();
        }
        catch (NullPointerException ex) {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public boolean isTracingEnabled() {
        try {
            return this.context.get().isTracingEnabled();
        }
        catch (NullPointerException ex) {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public void trace(final String message) {
        try {
            this.context.get().trace(message);
        }
        catch (NullPointerException ex) {
            throw new IllegalStateException();
        }
    }
}
