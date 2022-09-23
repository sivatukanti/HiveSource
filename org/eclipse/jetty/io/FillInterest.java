// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.nio.channels.ReadPendingException;
import org.eclipse.jetty.util.Callback;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.log.Logger;

public abstract class FillInterest
{
    private static final Logger LOG;
    private final AtomicReference<Callback> _interested;
    private Throwable _lastSet;
    
    protected FillInterest() {
        this._interested = new AtomicReference<Callback>(null);
    }
    
    public void register(final Callback callback) throws ReadPendingException {
        if (!this.tryRegister(callback)) {
            FillInterest.LOG.warn("Read pending for {} prevented {}", this._interested, callback);
            if (FillInterest.LOG.isDebugEnabled()) {
                FillInterest.LOG.warn("callback set at ", this._lastSet);
            }
            throw new ReadPendingException();
        }
    }
    
    public boolean tryRegister(final Callback callback) {
        if (callback == null) {
            throw new IllegalArgumentException();
        }
        if (!this._interested.compareAndSet(null, callback)) {
            return false;
        }
        if (FillInterest.LOG.isDebugEnabled()) {
            FillInterest.LOG.debug("{} register {}", this, callback);
            this._lastSet = new Throwable(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + ":" + Thread.currentThread().getName());
        }
        try {
            this.needsFillInterest();
        }
        catch (Throwable e) {
            this.onFail(e);
        }
        return true;
    }
    
    public void fillable() {
        final Callback callback = this._interested.get();
        if (FillInterest.LOG.isDebugEnabled()) {
            FillInterest.LOG.debug("{} fillable {}", this, callback);
        }
        if (callback != null && this._interested.compareAndSet(callback, null)) {
            callback.succeeded();
        }
        else if (FillInterest.LOG.isDebugEnabled()) {
            FillInterest.LOG.debug("{} lost race {}", this, callback);
        }
    }
    
    public boolean isInterested() {
        return this._interested.get() != null;
    }
    
    public boolean isCallbackNonBlocking() {
        final Callback callback = this._interested.get();
        return callback != null && callback.isNonBlocking();
    }
    
    public boolean onFail(final Throwable cause) {
        final Callback callback = this._interested.get();
        if (callback != null && this._interested.compareAndSet(callback, null)) {
            callback.failed(cause);
            return true;
        }
        return false;
    }
    
    public void onClose() {
        final Callback callback = this._interested.get();
        if (callback != null && this._interested.compareAndSet(callback, null)) {
            callback.failed(new ClosedChannelException());
        }
    }
    
    @Override
    public String toString() {
        return String.format("FillInterest@%x{%b,%s}", this.hashCode(), this._interested.get() != null, this._interested.get());
    }
    
    public String toStateString() {
        return (this._interested.get() == null) ? "-" : "FI";
    }
    
    protected abstract void needsFillInterest() throws IOException;
    
    static {
        LOG = Log.getLogger(FillInterest.class);
    }
}
