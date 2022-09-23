// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.continuation;

import org.mortbay.log.Log;
import java.util.Iterator;
import javax.servlet.ServletResponseWrapper;
import java.util.ArrayList;
import java.util.List;
import org.mortbay.util.ajax.Continuation;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.mortbay.log.Logger;

public class Jetty6Continuation implements ContinuationFilter.FilteredContinuation
{
    private static final Logger LOG;
    private static final ContinuationThrowable __exception;
    private final ServletRequest _request;
    private ServletResponse _response;
    private final org.mortbay.util.ajax.Continuation _j6Continuation;
    private Throwable _retry;
    private int _timeout;
    private boolean _initial;
    private volatile boolean _completed;
    private volatile boolean _resumed;
    private volatile boolean _expired;
    private boolean _responseWrapped;
    private List<ContinuationListener> _listeners;
    
    public Jetty6Continuation(final ServletRequest request, final org.mortbay.util.ajax.Continuation continuation) {
        this._initial = true;
        this._completed = false;
        this._resumed = false;
        this._expired = false;
        this._responseWrapped = false;
        if (!ContinuationFilter._initialized) {
            Jetty6Continuation.LOG.warn("!ContinuationFilter installed", null, null);
            throw new IllegalStateException("!ContinuationFilter installed");
        }
        this._request = request;
        this._j6Continuation = continuation;
    }
    
    public void addContinuationListener(final ContinuationListener listener) {
        if (this._listeners == null) {
            this._listeners = new ArrayList<ContinuationListener>();
        }
        this._listeners.add(listener);
    }
    
    public void complete() {
        synchronized (this) {
            if (this._resumed) {
                throw new IllegalStateException();
            }
            this._completed = true;
            if (this._j6Continuation.isPending()) {
                this._j6Continuation.resume();
            }
        }
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
    
    public ServletResponse getServletResponse() {
        return this._response;
    }
    
    public boolean isExpired() {
        return this._expired;
    }
    
    public boolean isInitial() {
        return this._initial;
    }
    
    public boolean isResumed() {
        return this._resumed;
    }
    
    public boolean isSuspended() {
        return this._retry != null;
    }
    
    public void resume() {
        synchronized (this) {
            if (this._completed) {
                throw new IllegalStateException();
            }
            this._resumed = true;
            if (this._j6Continuation.isPending()) {
                this._j6Continuation.resume();
            }
        }
    }
    
    public void setTimeout(final long timeoutMs) {
        this._timeout = ((timeoutMs > 2147483647L) ? Integer.MAX_VALUE : ((int)timeoutMs));
    }
    
    public void suspend(final ServletResponse response) {
        try {
            this._response = response;
            this._responseWrapped = (this._response instanceof ServletResponseWrapper);
            this._resumed = false;
            this._expired = false;
            this._completed = false;
            this._j6Continuation.suspend(this._timeout);
        }
        catch (Throwable retry) {
            this._retry = retry;
        }
    }
    
    public void suspend() {
        try {
            this._response = null;
            this._responseWrapped = false;
            this._resumed = false;
            this._expired = false;
            this._completed = false;
            this._j6Continuation.suspend(this._timeout);
        }
        catch (Throwable retry) {
            this._retry = retry;
        }
    }
    
    public boolean isResponseWrapped() {
        return this._responseWrapped;
    }
    
    public void undispatch() {
        if (!this.isSuspended()) {
            throw new IllegalStateException("!suspended");
        }
        if (ContinuationFilter.__debug) {
            throw new ContinuationThrowable();
        }
        throw Jetty6Continuation.__exception;
    }
    
    public boolean enter(final ServletResponse response) {
        this._response = response;
        this._expired = !this._j6Continuation.isResumed();
        if (this._initial) {
            return true;
        }
        this._j6Continuation.reset();
        if (this._expired && this._listeners != null) {
            for (final ContinuationListener l : this._listeners) {
                l.onTimeout(this);
            }
        }
        return !this._completed;
    }
    
    public boolean exit() {
        this._initial = false;
        final Throwable th = this._retry;
        this._retry = null;
        if (th instanceof Error) {
            throw (Error)th;
        }
        if (th instanceof RuntimeException) {
            throw (RuntimeException)th;
        }
        if (this._listeners != null) {
            for (final ContinuationListener l : this._listeners) {
                l.onComplete(this);
            }
        }
        return true;
    }
    
    static {
        LOG = Log.getLogger(Jetty6Continuation.class.getName());
        __exception = new ContinuationThrowable();
    }
}
