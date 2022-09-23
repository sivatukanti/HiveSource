// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.log.Logger;

public abstract class IdleTimeout
{
    private static final Logger LOG;
    private final Scheduler _scheduler;
    private final AtomicReference<Scheduler.Task> _timeout;
    private volatile long _idleTimeout;
    private volatile long _idleTimestamp;
    private final Runnable _idleTask;
    
    public IdleTimeout(final Scheduler scheduler) {
        this._timeout = new AtomicReference<Scheduler.Task>();
        this._idleTimestamp = System.currentTimeMillis();
        this._idleTask = new Runnable() {
            @Override
            public void run() {
                final long idleLeft = IdleTimeout.this.checkIdleTimeout();
                if (idleLeft >= 0L) {
                    IdleTimeout.this.scheduleIdleTimeout((idleLeft > 0L) ? idleLeft : IdleTimeout.this.getIdleTimeout());
                }
            }
        };
        this._scheduler = scheduler;
    }
    
    public Scheduler getScheduler() {
        return this._scheduler;
    }
    
    public long getIdleTimestamp() {
        return this._idleTimestamp;
    }
    
    public long getIdleFor() {
        return System.currentTimeMillis() - this.getIdleTimestamp();
    }
    
    public long getIdleTimeout() {
        return this._idleTimeout;
    }
    
    public void setIdleTimeout(final long idleTimeout) {
        final long old = this._idleTimeout;
        this._idleTimeout = idleTimeout;
        if (old > 0L) {
            if (old <= idleTimeout) {
                return;
            }
            this.deactivate();
        }
        if (this.isOpen()) {
            this.activate();
        }
    }
    
    public void notIdle() {
        this._idleTimestamp = System.currentTimeMillis();
    }
    
    private void scheduleIdleTimeout(final long delay) {
        Scheduler.Task newTimeout = null;
        if (this.isOpen() && delay > 0L && this._scheduler != null) {
            newTimeout = this._scheduler.schedule(this._idleTask, delay, TimeUnit.MILLISECONDS);
        }
        final Scheduler.Task oldTimeout = this._timeout.getAndSet(newTimeout);
        if (oldTimeout != null) {
            oldTimeout.cancel();
        }
    }
    
    public void onOpen() {
        this.activate();
    }
    
    private void activate() {
        if (this._idleTimeout > 0L) {
            this._idleTask.run();
        }
    }
    
    public void onClose() {
        this.deactivate();
    }
    
    private void deactivate() {
        final Scheduler.Task oldTimeout = this._timeout.getAndSet(null);
        if (oldTimeout != null) {
            oldTimeout.cancel();
        }
    }
    
    protected long checkIdleTimeout() {
        if (this.isOpen()) {
            final long idleTimestamp = this.getIdleTimestamp();
            final long idleTimeout = this.getIdleTimeout();
            final long idleElapsed = System.currentTimeMillis() - idleTimestamp;
            final long idleLeft = idleTimeout - idleElapsed;
            if (IdleTimeout.LOG.isDebugEnabled()) {
                IdleTimeout.LOG.debug("{} idle timeout check, elapsed: {} ms, remaining: {} ms", this, idleElapsed, idleLeft);
            }
            if (idleTimestamp != 0L && idleTimeout > 0L && idleLeft <= 0L) {
                if (IdleTimeout.LOG.isDebugEnabled()) {
                    IdleTimeout.LOG.debug("{} idle timeout expired", this);
                }
                try {
                    this.onIdleExpired(new TimeoutException("Idle timeout expired: " + idleElapsed + "/" + idleTimeout + " ms"));
                }
                finally {
                    this.notIdle();
                }
            }
            return (idleLeft >= 0L) ? idleLeft : 0L;
        }
        return -1L;
    }
    
    protected abstract void onIdleExpired(final TimeoutException p0);
    
    public abstract boolean isOpen();
    
    static {
        LOG = Log.getLogger(IdleTimeout.class);
    }
}
