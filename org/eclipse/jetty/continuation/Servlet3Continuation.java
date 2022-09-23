// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.continuation;

import java.util.Iterator;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.DispatcherType;
import java.io.IOException;
import javax.servlet.AsyncEvent;
import java.util.ArrayList;
import javax.servlet.AsyncListener;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;

public class Servlet3Continuation implements Continuation
{
    private static final ContinuationThrowable __exception;
    private final ServletRequest _request;
    private ServletResponse _response;
    private AsyncContext _context;
    private List<AsyncListener> _listeners;
    private volatile boolean _initial;
    private volatile boolean _resumed;
    private volatile boolean _expired;
    private volatile boolean _responseWrapped;
    private long _timeoutMs;
    
    public Servlet3Continuation(final ServletRequest request) {
        this._listeners = new ArrayList<AsyncListener>();
        this._initial = true;
        this._resumed = false;
        this._expired = false;
        this._responseWrapped = false;
        this._timeoutMs = -1L;
        this._request = request;
        this._listeners.add(new AsyncListener() {
            public void onComplete(final AsyncEvent event) throws IOException {
            }
            
            public void onError(final AsyncEvent event) throws IOException {
            }
            
            public void onStartAsync(final AsyncEvent event) throws IOException {
                event.getAsyncContext().addListener(this);
            }
            
            public void onTimeout(final AsyncEvent event) throws IOException {
                Servlet3Continuation.this._initial = false;
                event.getAsyncContext().dispatch();
            }
        });
    }
    
    public void addContinuationListener(final ContinuationListener listener) {
        final AsyncListener wrapped = new AsyncListener() {
            public void onComplete(final AsyncEvent event) throws IOException {
                listener.onComplete(Servlet3Continuation.this);
            }
            
            public void onError(final AsyncEvent event) throws IOException {
                listener.onComplete(Servlet3Continuation.this);
            }
            
            public void onStartAsync(final AsyncEvent event) throws IOException {
                event.getAsyncContext().addListener(this);
            }
            
            public void onTimeout(final AsyncEvent event) throws IOException {
                Servlet3Continuation.this._expired = true;
                listener.onTimeout(Servlet3Continuation.this);
            }
        };
        if (this._context != null) {
            this._context.addListener(wrapped);
        }
        else {
            this._listeners.add(wrapped);
        }
    }
    
    public void complete() {
        final AsyncContext context = this._context;
        if (context == null) {
            throw new IllegalStateException();
        }
        this._context.complete();
    }
    
    public ServletResponse getServletResponse() {
        return this._response;
    }
    
    public boolean isExpired() {
        return this._expired;
    }
    
    public boolean isInitial() {
        return this._initial && this._request.getDispatcherType() != DispatcherType.ASYNC;
    }
    
    public boolean isResumed() {
        return this._resumed;
    }
    
    public boolean isSuspended() {
        return this._request.isAsyncStarted();
    }
    
    public void keepWrappers() {
        this._responseWrapped = true;
    }
    
    public void resume() {
        final AsyncContext context = this._context;
        if (context == null) {
            throw new IllegalStateException();
        }
        this._resumed = true;
        this._context.dispatch();
    }
    
    public void setTimeout(final long timeoutMs) {
        this._timeoutMs = timeoutMs;
        if (this._context != null) {
            this._context.setTimeout(timeoutMs);
        }
    }
    
    public void suspend(final ServletResponse response) {
        this._response = response;
        this._responseWrapped = (response instanceof ServletResponseWrapper);
        this._resumed = false;
        this._expired = false;
        (this._context = this._request.startAsync()).setTimeout(this._timeoutMs);
        for (final AsyncListener listener : this._listeners) {
            this._context.addListener(listener);
        }
        this._listeners.clear();
    }
    
    public void suspend() {
        this._resumed = false;
        this._expired = false;
        (this._context = this._request.startAsync()).setTimeout(this._timeoutMs);
        for (final AsyncListener listener : this._listeners) {
            this._context.addListener(listener);
        }
        this._listeners.clear();
    }
    
    public boolean isResponseWrapped() {
        return this._responseWrapped;
    }
    
    public Object getAttribute(final String name) {
        return this._request.getAttribute(name);
    }
    
    public void removeAttribute(final String name) {
        this._request.removeAttribute(name);
    }
    
    public void setAttribute(final String name, final Object attribute) {
        this._request.setAttribute(name, attribute);
    }
    
    public void undispatch() {
        if (!this.isSuspended()) {
            throw new IllegalStateException("!suspended");
        }
        if (ContinuationFilter.__debug) {
            throw new ContinuationThrowable();
        }
        throw Servlet3Continuation.__exception;
    }
    
    static {
        __exception = new ContinuationThrowable();
    }
}
