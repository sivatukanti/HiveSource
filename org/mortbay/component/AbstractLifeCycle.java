// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.component;

import org.mortbay.util.LazyList;
import org.mortbay.log.Log;

public abstract class AbstractLifeCycle implements LifeCycle
{
    private Object _lock;
    private final int FAILED = -1;
    private final int STOPPED = 0;
    private final int STARTING = 1;
    private final int STARTED = 2;
    private final int STOPPING = 3;
    private volatile int _state;
    protected Listener[] _listeners;
    
    public AbstractLifeCycle() {
        this._lock = new Object();
        this._state = 0;
    }
    
    protected void doStart() throws Exception {
    }
    
    protected void doStop() throws Exception {
    }
    
    public final void start() throws Exception {
        synchronized (this._lock) {
            try {
                if (this._state == 2 || this._state == 1) {
                    return;
                }
                this.setStarting();
                this.doStart();
                Log.debug("started {}", this);
                this.setStarted();
            }
            catch (Exception e) {
                this.setFailed(e);
                throw e;
            }
            catch (Error e2) {
                this.setFailed(e2);
                throw e2;
            }
        }
    }
    
    public final void stop() throws Exception {
        synchronized (this._lock) {
            try {
                if (this._state == 3 || this._state == 0) {
                    return;
                }
                this.setStopping();
                this.doStop();
                Log.debug("stopped {}", this);
                this.setStopped();
            }
            catch (Exception e) {
                this.setFailed(e);
                throw e;
            }
            catch (Error e2) {
                this.setFailed(e2);
                throw e2;
            }
        }
    }
    
    public boolean isRunning() {
        return this._state == 2 || this._state == 1;
    }
    
    public boolean isStarted() {
        return this._state == 2;
    }
    
    public boolean isStarting() {
        return this._state == 1;
    }
    
    public boolean isStopping() {
        return this._state == 3;
    }
    
    public boolean isStopped() {
        return this._state == 0;
    }
    
    public boolean isFailed() {
        return this._state == -1;
    }
    
    public void addLifeCycleListener(final Listener listener) {
        this._listeners = (Listener[])LazyList.addToArray(this._listeners, listener, Listener.class);
    }
    
    public void removeLifeCycleListener(final Listener listener) {
        this._listeners = (Listener[])LazyList.removeFromArray(this._listeners, listener);
    }
    
    private void setStarted() {
        this._state = 2;
        if (this._listeners != null) {
            for (int i = 0; i < this._listeners.length; ++i) {
                this._listeners[i].lifeCycleStarted(this);
            }
        }
    }
    
    private void setStarting() {
        this._state = 1;
        if (this._listeners != null) {
            for (int i = 0; i < this._listeners.length; ++i) {
                this._listeners[i].lifeCycleStarting(this);
            }
        }
    }
    
    private void setStopping() {
        this._state = 3;
        if (this._listeners != null) {
            for (int i = 0; i < this._listeners.length; ++i) {
                this._listeners[i].lifeCycleStopping(this);
            }
        }
    }
    
    private void setStopped() {
        this._state = 0;
        if (this._listeners != null) {
            for (int i = 0; i < this._listeners.length; ++i) {
                this._listeners[i].lifeCycleStopped(this);
            }
        }
    }
    
    private void setFailed(final Throwable th) {
        Log.warn("failed " + this + ": " + th);
        Log.debug(th);
        this._state = -1;
        if (this._listeners != null) {
            for (int i = 0; i < this._listeners.length; ++i) {
                this._listeners[i].lifeCycleFailure(this, th);
            }
        }
    }
}
