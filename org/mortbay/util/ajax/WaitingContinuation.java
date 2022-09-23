// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util.ajax;

import org.mortbay.log.Log;

public class WaitingContinuation implements Continuation
{
    Object _mutex;
    Object _object;
    boolean _new;
    boolean _resumed;
    boolean _pending;
    
    public WaitingContinuation() {
        this._new = true;
        this._resumed = false;
        this._pending = false;
        this._mutex = this;
    }
    
    public WaitingContinuation(final Object mutex) {
        this._new = true;
        this._resumed = false;
        this._pending = false;
        this._mutex = ((mutex == null) ? this : mutex);
    }
    
    public void resume() {
        synchronized (this._mutex) {
            this._resumed = true;
            this._mutex.notify();
        }
    }
    
    public void reset() {
        synchronized (this._mutex) {
            this._resumed = false;
            this._pending = false;
            this._mutex.notify();
        }
    }
    
    public boolean isNew() {
        return this._new;
    }
    
    public boolean suspend(final long timeout) {
        synchronized (this._mutex) {
            this._new = false;
            this._pending = true;
            boolean result = false;
            try {
                if (!this._resumed && timeout >= 0L) {
                    if (timeout == 0L) {
                        this._mutex.wait();
                    }
                    else if (timeout > 0L) {
                        this._mutex.wait(timeout);
                    }
                }
            }
            catch (InterruptedException e) {
                Log.ignore(e);
            }
            finally {
                result = this._resumed;
                this._resumed = false;
                this._pending = false;
            }
            return result;
        }
    }
    
    public boolean isPending() {
        synchronized (this._mutex) {
            return this._pending;
        }
    }
    
    public boolean isResumed() {
        synchronized (this._mutex) {
            return this._resumed;
        }
    }
    
    public Object getObject() {
        return this._object;
    }
    
    public void setObject(final Object object) {
        this._object = object;
    }
    
    public Object getMutex() {
        return this._mutex;
    }
    
    public void setMutex(final Object mutex) {
        if (this._pending && mutex != this._mutex) {
            throw new IllegalStateException();
        }
        this._mutex = ((mutex == null) ? this : mutex);
    }
    
    public String toString() {
        synchronized (this) {
            return "WaitingContinuation@" + this.hashCode() + (this._new ? ",new" : "") + (this._pending ? ",pending" : "") + (this._resumed ? ",resumed" : "");
        }
    }
}
