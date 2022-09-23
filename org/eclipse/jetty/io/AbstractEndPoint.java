// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.BufferUtil;
import java.util.concurrent.TimeoutException;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.Callback;
import java.io.IOException;
import org.eclipse.jetty.util.thread.Scheduler;
import java.net.InetSocketAddress;
import org.eclipse.jetty.util.log.Logger;

public abstract class AbstractEndPoint extends IdleTimeout implements EndPoint
{
    private static final Logger LOG;
    private final long _created;
    private final InetSocketAddress _local;
    private final InetSocketAddress _remote;
    private volatile Connection _connection;
    private final FillInterest _fillInterest;
    private final WriteFlusher _writeFlusher;
    
    protected AbstractEndPoint(final Scheduler scheduler, final InetSocketAddress local, final InetSocketAddress remote) {
        super(scheduler);
        this._created = System.currentTimeMillis();
        this._fillInterest = new FillInterest() {
            @Override
            protected void needsFillInterest() throws IOException {
                AbstractEndPoint.this.needsFillInterest();
            }
        };
        this._writeFlusher = new WriteFlusher((EndPoint)this) {
            @Override
            protected void onIncompleteFlush() {
                AbstractEndPoint.this.onIncompleteFlush();
            }
        };
        this._local = local;
        this._remote = remote;
    }
    
    @Override
    public long getCreatedTimeStamp() {
        return this._created;
    }
    
    @Override
    public InetSocketAddress getLocalAddress() {
        return this._local;
    }
    
    @Override
    public InetSocketAddress getRemoteAddress() {
        return this._remote;
    }
    
    @Override
    public Connection getConnection() {
        return this._connection;
    }
    
    @Override
    public void setConnection(final Connection connection) {
        this._connection = connection;
    }
    
    @Override
    public boolean isOptimizedForDirectBuffers() {
        return false;
    }
    
    @Override
    public void onOpen() {
        if (AbstractEndPoint.LOG.isDebugEnabled()) {
            AbstractEndPoint.LOG.debug("onOpen {}", this);
        }
        super.onOpen();
    }
    
    @Override
    public void close() {
        this.onClose();
        this._writeFlusher.onClose();
        this._fillInterest.onClose();
    }
    
    protected void close(final Throwable failure) {
        this.onClose();
        this._writeFlusher.onFail(failure);
        this._fillInterest.onFail(failure);
    }
    
    @Override
    public void fillInterested(final Callback callback) {
        this.notIdle();
        this._fillInterest.register(callback);
    }
    
    @Override
    public boolean tryFillInterested(final Callback callback) {
        this.notIdle();
        return this._fillInterest.tryRegister(callback);
    }
    
    @Override
    public boolean isFillInterested() {
        return this._fillInterest.isInterested();
    }
    
    @Override
    public void write(final Callback callback, final ByteBuffer... buffers) throws IllegalStateException {
        this._writeFlusher.write(callback, buffers);
    }
    
    protected abstract void onIncompleteFlush();
    
    protected abstract void needsFillInterest() throws IOException;
    
    public FillInterest getFillInterest() {
        return this._fillInterest;
    }
    
    protected WriteFlusher getWriteFlusher() {
        return this._writeFlusher;
    }
    
    @Override
    protected void onIdleExpired(final TimeoutException timeout) {
        final Connection connection = this._connection;
        if (connection != null && !connection.onIdleExpired()) {
            return;
        }
        final boolean output_shutdown = this.isOutputShutdown();
        final boolean input_shutdown = this.isInputShutdown();
        final boolean fillFailed = this._fillInterest.onFail(timeout);
        final boolean writeFailed = this._writeFlusher.onFail(timeout);
        if (this.isOpen() && (output_shutdown || input_shutdown) && !fillFailed && !writeFailed) {
            this.close();
        }
        else {
            AbstractEndPoint.LOG.debug("Ignored idle endpoint {}", this);
        }
    }
    
    @Override
    public void upgrade(final Connection newConnection) {
        final Connection old_connection = this.getConnection();
        if (AbstractEndPoint.LOG.isDebugEnabled()) {
            AbstractEndPoint.LOG.debug("{} upgrading from {} to {}", this, old_connection, newConnection);
        }
        final ByteBuffer prefilled = (old_connection instanceof Connection.UpgradeFrom) ? ((Connection.UpgradeFrom)old_connection).onUpgradeFrom() : null;
        old_connection.onClose();
        old_connection.getEndPoint().setConnection(newConnection);
        if (newConnection instanceof Connection.UpgradeTo) {
            ((Connection.UpgradeTo)newConnection).onUpgradeTo(prefilled);
        }
        else if (BufferUtil.hasContent(prefilled)) {
            throw new IllegalStateException();
        }
        newConnection.onOpen();
    }
    
    @Override
    public String toString() {
        Class<?> c;
        String name;
        for (c = this.getClass(), name = c.getSimpleName(); name.length() == 0 && c.getSuperclass() != null; c = c.getSuperclass(), name = c.getSimpleName()) {}
        final Connection connection = this.getConnection();
        return String.format("%s@%x{%s<->%d,%s,%s,%s,%s,%s,%d/%d,%s@%x}", name, this.hashCode(), this.getRemoteAddress(), this.getLocalAddress().getPort(), this.isOpen() ? "Open" : "CLOSED", this.isInputShutdown() ? "ISHUT" : "in", this.isOutputShutdown() ? "OSHUT" : "out", this._fillInterest.toStateString(), this._writeFlusher.toStateString(), this.getIdleFor(), this.getIdleTimeout(), (connection == null) ? null : connection.getClass().getSimpleName(), (connection == null) ? 0 : connection.hashCode());
    }
    
    static {
        LOG = Log.getLogger(AbstractEndPoint.class);
    }
}
