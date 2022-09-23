// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.servlet;

import org.mortbay.util.ajax.Continuation;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import org.mortbay.log.Log;
import javax.servlet.FilterConfig;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.Filter;

public class ThrottlingFilter implements Filter
{
    private int _maximum;
    private int _current;
    private long _queueTimeout;
    private long _queueSize;
    private final Object _lock;
    private final List _queue;
    
    public ThrottlingFilter() {
        this._current = 0;
        this._lock = new Object();
        this._queue = new LinkedList();
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this._maximum = this.getIntegerParameter(filterConfig, "maximum", 10);
        this._queueTimeout = this.getIntegerParameter(filterConfig, "block", 5000);
        this._queueSize = this.getIntegerParameter(filterConfig, "queue", 500);
        if (this._queueTimeout == -1L) {
            this._queueTimeout = 2147483647L;
        }
        Log.debug("Config{maximum:" + this._maximum + ", block:" + this._queueTimeout + ", queue:" + this._queueSize + "}", null, null);
    }
    
    private int getIntegerParameter(final FilterConfig filterConfig, final String name, final int defaultValue) throws ServletException {
        final String value = filterConfig.getInitParameter(name);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new ServletException("Parameter " + name + " must be a number (was " + value + " instead)");
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
    }
    
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final Continuation continuation = this.getContinuation(request);
        boolean accepted = false;
        try {
            accepted = this.acceptRequest();
            if (!accepted) {
                if (continuation.isPending()) {
                    Log.debug("Request {} / {} was already queued, rejecting", request.getRequestURI(), continuation);
                    this.dropFromQueue(continuation);
                    continuation.reset();
                }
                else if (this.queueRequest(request, response, continuation)) {
                    accepted = this.acceptRequest();
                }
            }
            if (accepted) {
                chain.doFilter(request, response);
            }
            else {
                this.rejectRequest(request, response);
            }
        }
        finally {
            if (accepted) {
                this.releaseRequest();
                this.popQueue();
            }
        }
    }
    
    private void dropFromQueue(final Continuation continuation) {
        this._queue.remove(continuation);
        continuation.reset();
    }
    
    protected void rejectRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.sendError(503, "Too many active connections to resource " + request.getRequestURI());
    }
    
    private void popQueue() {
        final Continuation continuation;
        synchronized (this._queue) {
            if (this._queue.isEmpty()) {
                return;
            }
            continuation = this._queue.remove(0);
        }
        Log.debug("Resuming continuation {}", continuation, null);
        continuation.resume();
    }
    
    private void releaseRequest() {
        synchronized (this._lock) {
            --this._current;
        }
    }
    
    private boolean acceptRequest() {
        synchronized (this._lock) {
            if (this._current < this._maximum) {
                ++this._current;
                return true;
            }
        }
        return false;
    }
    
    private boolean queueRequest(final HttpServletRequest request, final HttpServletResponse response, final Continuation continuation) throws IOException, ServletException {
        synchronized (this._queue) {
            if (this._queue.size() >= this._queueSize) {
                Log.debug("Queue is full, rejecting request {}", request.getRequestURI(), null);
                return false;
            }
            Log.debug("Queuing request {} / {}", request.getRequestURI(), continuation);
            this._queue.add(continuation);
        }
        continuation.suspend(this._queueTimeout);
        Log.debug("Resuming blocking continuation for request {}", request.getRequestURI(), null);
        return true;
    }
    
    private Continuation getContinuation(final ServletRequest request) {
        return (Continuation)request.getAttribute("org.mortbay.jetty.ajax.Continuation");
    }
    
    public void destroy() {
        this._queue.clear();
    }
}
