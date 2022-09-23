// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.continuation;

import javax.servlet.ServletResponseWrapper;
import java.util.Iterator;
import java.util.ArrayList;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;

class FauxContinuation implements ContinuationFilter.FilteredContinuation
{
    private static final ContinuationThrowable __exception;
    private static final int __HANDLING = 1;
    private static final int __SUSPENDING = 2;
    private static final int __RESUMING = 3;
    private static final int __COMPLETING = 4;
    private static final int __SUSPENDED = 5;
    private static final int __UNSUSPENDING = 6;
    private static final int __COMPLETE = 7;
    private final ServletRequest _request;
    private ServletResponse _response;
    private int _state;
    private boolean _initial;
    private boolean _resumed;
    private boolean _timeout;
    private boolean _responseWrapped;
    private long _timeoutMs;
    private ArrayList<ContinuationListener> _listeners;
    
    FauxContinuation(final ServletRequest request) {
        this._state = 1;
        this._initial = true;
        this._resumed = false;
        this._timeout = false;
        this._responseWrapped = false;
        this._timeoutMs = 30000L;
        this._request = request;
    }
    
    public void onComplete() {
        if (this._listeners != null) {
            for (final ContinuationListener l : this._listeners) {
                l.onComplete(this);
            }
        }
    }
    
    public void onTimeout() {
        if (this._listeners != null) {
            for (final ContinuationListener l : this._listeners) {
                l.onTimeout(this);
            }
        }
    }
    
    public boolean isResponseWrapped() {
        return this._responseWrapped;
    }
    
    public boolean isInitial() {
        synchronized (this) {
            return this._initial;
        }
    }
    
    public boolean isResumed() {
        synchronized (this) {
            return this._resumed;
        }
    }
    
    public boolean isSuspended() {
        synchronized (this) {
            switch (this._state) {
                case 1: {
                    return false;
                }
                case 2:
                case 3:
                case 4:
                case 5: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
    }
    
    public boolean isExpired() {
        synchronized (this) {
            return this._timeout;
        }
    }
    
    public void setTimeout(final long timeoutMs) {
        this._timeoutMs = timeoutMs;
    }
    
    public void suspend(final ServletResponse response) {
        this._response = response;
        this._responseWrapped = (response instanceof ServletResponseWrapper);
        this.suspend();
    }
    
    public void suspend() {
        synchronized (this) {
            switch (this._state) {
                case 1: {
                    this._timeout = false;
                    this._resumed = false;
                    this._state = 2;
                }
                case 2:
                case 3: {}
                case 4:
                case 5:
                case 6: {
                    throw new IllegalStateException(this.getStatusString());
                }
                default: {
                    throw new IllegalStateException("" + this._state);
                }
            }
        }
    }
    
    public void resume() {
        synchronized (this) {
            switch (this._state) {
                case 1: {
                    this._resumed = true;
                }
                case 2: {
                    this._resumed = true;
                    this._state = 3;
                }
                case 3:
                case 4: {}
                case 5: {
                    this.fauxResume();
                    this._resumed = true;
                    this._state = 6;
                    break;
                }
                case 6: {
                    this._resumed = true;
                }
                default: {
                    throw new IllegalStateException(this.getStatusString());
                }
            }
        }
    }
    
    public void complete() {
        synchronized (this) {
            switch (this._state) {
                case 1: {
                    throw new IllegalStateException(this.getStatusString());
                }
                case 2: {
                    this._state = 4;
                    break;
                }
                case 3: {
                    break;
                }
                case 4: {}
                case 5: {
                    this._state = 4;
                    this.fauxResume();
                    break;
                }
                case 6: {}
                default: {
                    throw new IllegalStateException(this.getStatusString());
                }
            }
        }
    }
    
    public boolean enter(final ServletResponse response) {
        this._response = response;
        return true;
    }
    
    public ServletResponse getServletResponse() {
        return this._response;
    }
    
    void handling() {
        synchronized (this) {
            this._responseWrapped = false;
            switch (this._state) {
                case 1: {
                    throw new IllegalStateException(this.getStatusString());
                }
                case 2:
                case 3: {
                    throw new IllegalStateException(this.getStatusString());
                }
                case 4: {}
                case 5: {
                    this.fauxResume();
                }
                case 6: {
                    this._state = 1;
                }
                default: {
                    throw new IllegalStateException("" + this._state);
                }
            }
        }
    }
    
    public boolean exit() {
        synchronized (this) {
            switch (this._state) {
                case 1: {
                    this._state = 7;
                    this.onComplete();
                    return true;
                }
                case 2: {
                    this._initial = false;
                    this._state = 5;
                    this.fauxSuspend();
                    if (this._state == 5 || this._state == 4) {
                        this.onComplete();
                        return true;
                    }
                    this._initial = false;
                    this._state = 1;
                    return false;
                }
                case 3: {
                    this._initial = false;
                    this._state = 1;
                    return false;
                }
                case 4: {
                    this._initial = false;
                    this._state = 7;
                    this.onComplete();
                    return true;
                }
                default: {
                    throw new IllegalStateException(this.getStatusString());
                }
            }
        }
    }
    
    protected void expire() {
        synchronized (this) {
            this._timeout = true;
        }
        this.onTimeout();
        synchronized (this) {
            switch (this._state) {
                case 1: {}
                case 2: {
                    this._timeout = true;
                    this._state = 3;
                    this.fauxResume();
                }
                case 3: {}
                case 4: {}
                case 5: {
                    this._timeout = true;
                    this._state = 6;
                    break;
                }
                case 6: {
                    this._timeout = true;
                }
                default: {
                    throw new IllegalStateException(this.getStatusString());
                }
            }
        }
    }
    
    private void fauxSuspend() {
        long expire_at;
        long wait;
        for (expire_at = System.currentTimeMillis() + this._timeoutMs, wait = this._timeoutMs; this._timeoutMs > 0L && wait > 0L; wait = expire_at - System.currentTimeMillis()) {
            try {
                this.wait(wait);
            }
            catch (InterruptedException e) {
                break;
            }
        }
        if (this._timeoutMs > 0L && wait <= 0L) {
            this.expire();
        }
    }
    
    private void fauxResume() {
        this._timeoutMs = 0L;
        this.notifyAll();
    }
    
    @Override
    public String toString() {
        return this.getStatusString();
    }
    
    String getStatusString() {
        synchronized (this) {
            return ((this._state == 1) ? "HANDLING" : ((this._state == 2) ? "SUSPENDING" : ((this._state == 5) ? "SUSPENDED" : ((this._state == 3) ? "RESUMING" : ((this._state == 6) ? "UNSUSPENDING" : ((this._state == 4) ? "COMPLETING" : ("???" + this._state))))))) + (this._initial ? ",initial" : "") + (this._resumed ? ",resumed" : "") + (this._timeout ? ",timeout" : "");
        }
    }
    
    public void addContinuationListener(final ContinuationListener listener) {
        if (this._listeners == null) {
            this._listeners = new ArrayList<ContinuationListener>();
        }
        this._listeners.add(listener);
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
        throw FauxContinuation.__exception;
    }
    
    static {
        __exception = new ContinuationThrowable();
    }
}
