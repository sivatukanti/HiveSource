// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.Callback;
import java.util.concurrent.Executor;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;

public abstract class AbstractConnection implements Connection
{
    private static final Logger LOG;
    private final List<Listener> listeners;
    private final long _created;
    private final EndPoint _endPoint;
    private final Executor _executor;
    private final Callback _readCallback;
    private int _inputBufferSize;
    
    protected AbstractConnection(final EndPoint endp, final Executor executor) {
        this.listeners = new CopyOnWriteArrayList<Listener>();
        this._created = System.currentTimeMillis();
        this._inputBufferSize = 2048;
        if (executor == null) {
            throw new IllegalArgumentException("Executor must not be null!");
        }
        this._endPoint = endp;
        this._executor = executor;
        this._readCallback = new ReadCallback();
    }
    
    @Override
    public void addListener(final Listener listener) {
        this.listeners.add(listener);
    }
    
    @Override
    public void removeListener(final Listener listener) {
        this.listeners.remove(listener);
    }
    
    public int getInputBufferSize() {
        return this._inputBufferSize;
    }
    
    public void setInputBufferSize(final int inputBufferSize) {
        this._inputBufferSize = inputBufferSize;
    }
    
    protected Executor getExecutor() {
        return this._executor;
    }
    
    @Deprecated
    public boolean isDispatchIO() {
        return false;
    }
    
    protected void failedCallback(final Callback callback, final Throwable x) {
        if (callback.isNonBlocking()) {
            try {
                callback.failed(x);
            }
            catch (Exception e) {
                AbstractConnection.LOG.warn(e);
            }
        }
        else {
            try {
                this.getExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.failed(x);
                        }
                        catch (Exception e) {
                            AbstractConnection.LOG.warn(e);
                        }
                    }
                });
            }
            catch (RejectedExecutionException e2) {
                AbstractConnection.LOG.debug(e2);
                callback.failed(x);
            }
        }
    }
    
    public void fillInterested() {
        if (AbstractConnection.LOG.isDebugEnabled()) {
            AbstractConnection.LOG.debug("fillInterested {}", this);
        }
        this.getEndPoint().fillInterested(this._readCallback);
    }
    
    public void tryFillInterested() {
        this.tryFillInterested(this._readCallback);
    }
    
    public void tryFillInterested(final Callback callback) {
        this.getEndPoint().tryFillInterested(callback);
    }
    
    public boolean isFillInterested() {
        return this.getEndPoint().isFillInterested();
    }
    
    public abstract void onFillable();
    
    protected void onFillInterestedFailed(final Throwable cause) {
        if (AbstractConnection.LOG.isDebugEnabled()) {
            AbstractConnection.LOG.debug("{} onFillInterestedFailed {}", this, cause);
        }
        if (this._endPoint.isOpen()) {
            boolean close = true;
            if (cause instanceof TimeoutException) {
                close = this.onReadTimeout();
            }
            if (close) {
                if (this._endPoint.isOutputShutdown()) {
                    this._endPoint.close();
                }
                else {
                    this._endPoint.shutdownOutput();
                    this.fillInterested();
                }
            }
        }
    }
    
    protected boolean onReadTimeout() {
        return true;
    }
    
    @Override
    public void onOpen() {
        if (AbstractConnection.LOG.isDebugEnabled()) {
            AbstractConnection.LOG.debug("onOpen {}", this);
        }
        for (final Listener listener : this.listeners) {
            listener.onOpened(this);
        }
    }
    
    @Override
    public void onClose() {
        if (AbstractConnection.LOG.isDebugEnabled()) {
            AbstractConnection.LOG.debug("onClose {}", this);
        }
        for (final Listener listener : this.listeners) {
            listener.onClosed(this);
        }
    }
    
    @Override
    public EndPoint getEndPoint() {
        return this._endPoint;
    }
    
    @Override
    public void close() {
        this.getEndPoint().close();
    }
    
    @Override
    public boolean onIdleExpired() {
        return true;
    }
    
    @Override
    public int getMessagesIn() {
        return -1;
    }
    
    @Override
    public int getMessagesOut() {
        return -1;
    }
    
    @Override
    public long getBytesIn() {
        return -1L;
    }
    
    @Override
    public long getBytesOut() {
        return -1L;
    }
    
    @Override
    public long getCreatedTimeStamp() {
        return this._created;
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x[%s]", this.getClass().getSimpleName(), this.hashCode(), this._endPoint);
    }
    
    static {
        LOG = Log.getLogger(AbstractConnection.class);
    }
    
    private class ReadCallback implements Callback
    {
        @Override
        public void succeeded() {
            AbstractConnection.this.onFillable();
        }
        
        @Override
        public void failed(final Throwable x) {
            AbstractConnection.this.onFillInterestedFailed(x);
        }
        
        @Override
        public String toString() {
            return String.format("AC.ReadCB@%x{%s}", AbstractConnection.this.hashCode(), AbstractConnection.this);
        }
    }
}
