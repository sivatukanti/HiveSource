// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.io.Closeable;
import org.eclipse.jetty.util.log.Log;
import java.nio.channels.CancelledKeyException;
import org.eclipse.jetty.util.thread.Scheduler;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.util.thread.Locker;
import org.eclipse.jetty.util.log.Logger;

public class SelectChannelEndPoint extends ChannelEndPoint implements ManagedSelector.SelectableEndPoint
{
    public static final Logger LOG;
    private final Locker _locker;
    private boolean _updatePending;
    private final AtomicBoolean _open;
    private final ManagedSelector _selector;
    private final SelectionKey _key;
    private int _currentInterestOps;
    private int _desiredInterestOps;
    private final Runnable _runUpdateKey;
    private final Runnable _runFillable;
    private final Runnable _runCompleteWrite;
    private final Runnable _runCompleteWriteFillable;
    
    public SelectChannelEndPoint(final SocketChannel channel, final ManagedSelector selector, final SelectionKey key, final Scheduler scheduler, final long idleTimeout) {
        super(scheduler, channel);
        this._locker = new Locker();
        this._open = new AtomicBoolean();
        this._runUpdateKey = new Runnable() {
            @Override
            public void run() {
                SelectChannelEndPoint.this.updateKey();
            }
            
            @Override
            public String toString() {
                return SelectChannelEndPoint.this.toString() + ":runUpdateKey";
            }
        };
        this._runFillable = new RunnableCloseable() {
            @Override
            public void run() {
                SelectChannelEndPoint.this.getFillInterest().fillable();
            }
            
            @Override
            public String toString() {
                return SelectChannelEndPoint.this.toString() + ":runFillable";
            }
        };
        this._runCompleteWrite = new RunnableCloseable() {
            @Override
            public void run() {
                SelectChannelEndPoint.this.getWriteFlusher().completeWrite();
            }
            
            @Override
            public String toString() {
                return SelectChannelEndPoint.this.toString() + ":runCompleteWrite";
            }
        };
        this._runCompleteWriteFillable = new RunnableCloseable() {
            @Override
            public void run() {
                SelectChannelEndPoint.this.getWriteFlusher().completeWrite();
                SelectChannelEndPoint.this.getFillInterest().fillable();
            }
            
            @Override
            public String toString() {
                return SelectChannelEndPoint.this.toString() + ":runFillableCompleteWrite";
            }
        };
        this._selector = selector;
        this._key = key;
        this.setIdleTimeout(idleTimeout);
    }
    
    @Override
    protected void needsFillInterest() {
        this.changeInterests(1);
    }
    
    @Override
    protected void onIncompleteFlush() {
        this.changeInterests(4);
    }
    
    @Override
    public Runnable onSelected() {
        final int readyOps = this._key.readyOps();
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        int oldInterestOps;
        int newInterestOps;
        try {
            this._updatePending = true;
            oldInterestOps = this._desiredInterestOps;
            newInterestOps = (oldInterestOps & ~readyOps);
            this._desiredInterestOps = newInterestOps;
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        boolean readable = (readyOps & 0x1) != 0x0;
        boolean writable = (readyOps & 0x4) != 0x0;
        if (SelectChannelEndPoint.LOG.isDebugEnabled()) {
            SelectChannelEndPoint.LOG.debug("onSelected {}->{} r={} w={} for {}", oldInterestOps, newInterestOps, readable, writable, this);
        }
        if (readable && this.getFillInterest().isCallbackNonBlocking()) {
            if (SelectChannelEndPoint.LOG.isDebugEnabled()) {
                SelectChannelEndPoint.LOG.debug("Direct readable run {}", this);
            }
            this._runFillable.run();
            readable = false;
        }
        if (writable && this.getWriteFlusher().isCallbackNonBlocking()) {
            if (SelectChannelEndPoint.LOG.isDebugEnabled()) {
                SelectChannelEndPoint.LOG.debug("Direct writable run {}", this);
            }
            this._runCompleteWrite.run();
            writable = false;
        }
        final Runnable task = readable ? (writable ? this._runCompleteWriteFillable : this._runFillable) : (writable ? this._runCompleteWrite : null);
        if (SelectChannelEndPoint.LOG.isDebugEnabled()) {
            SelectChannelEndPoint.LOG.debug("task {}", task);
        }
        return task;
    }
    
    @Override
    public void updateKey() {
        try {
            final Locker.Lock lock = this._locker.lock();
            Throwable x3 = null;
            int oldInterestOps;
            int newInterestOps;
            try {
                this._updatePending = false;
                oldInterestOps = this._currentInterestOps;
                newInterestOps = this._desiredInterestOps;
                if (oldInterestOps != newInterestOps) {
                    this._currentInterestOps = newInterestOps;
                    this._key.interestOps(newInterestOps);
                }
            }
            catch (Throwable t) {
                x3 = t;
                throw t;
            }
            finally {
                if (lock != null) {
                    $closeResource(x3, lock);
                }
            }
            if (SelectChannelEndPoint.LOG.isDebugEnabled()) {
                SelectChannelEndPoint.LOG.debug("Key interests updated {} -> {} on {}", oldInterestOps, newInterestOps, this);
            }
        }
        catch (CancelledKeyException x2) {
            SelectChannelEndPoint.LOG.debug("Ignoring key update for concurrently closed channel {}", this);
            this.close();
        }
        catch (Throwable x) {
            SelectChannelEndPoint.LOG.warn("Ignoring key update for " + this, x);
            this.close();
        }
    }
    
    private void changeInterests(final int operation) {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        boolean pending;
        int oldInterestOps;
        int newInterestOps;
        try {
            pending = this._updatePending;
            oldInterestOps = this._desiredInterestOps;
            newInterestOps = (oldInterestOps | operation);
            if (newInterestOps != oldInterestOps) {
                this._desiredInterestOps = newInterestOps;
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        if (SelectChannelEndPoint.LOG.isDebugEnabled()) {
            SelectChannelEndPoint.LOG.debug("changeInterests p={} {}->{} for {}", pending, oldInterestOps, newInterestOps, this);
        }
        if (!pending) {
            this._selector.submit(this._runUpdateKey);
        }
    }
    
    @Override
    public void close() {
        if (this._open.compareAndSet(true, false)) {
            super.close();
            this._selector.destroyEndPoint(this);
        }
    }
    
    @Override
    public boolean isOpen() {
        return this._open.get();
    }
    
    @Override
    public void onOpen() {
        if (this._open.compareAndSet(false, true)) {
            super.onOpen();
        }
    }
    
    @Override
    public String toString() {
        try {
            final boolean valid = this._key != null && this._key.isValid();
            final int keyInterests = valid ? this._key.interestOps() : -1;
            final int keyReadiness = valid ? this._key.readyOps() : -1;
            return String.format("%s{io=%d/%d,kio=%d,kro=%d}", super.toString(), this._currentInterestOps, this._desiredInterestOps, keyInterests, keyReadiness);
        }
        catch (Throwable x) {
            return String.format("%s{io=%s,kio=-2,kro=-2}", super.toString(), this._desiredInterestOps);
        }
    }
    
    private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable exception) {
                x0.addSuppressed(exception);
            }
        }
        else {
            x1.close();
        }
    }
    
    static {
        LOG = Log.getLogger(SelectChannelEndPoint.class);
    }
    
    private abstract class RunnableCloseable implements Runnable, Closeable
    {
        @Override
        public void close() {
            try {
                SelectChannelEndPoint.this.close();
            }
            catch (Throwable x) {
                SelectChannelEndPoint.LOG.warn(x);
            }
        }
    }
}
