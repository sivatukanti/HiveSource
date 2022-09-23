// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.component;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import org.eclipse.jetty.util.Uptime;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Abstract Implementation of LifeCycle")
public abstract class AbstractLifeCycle implements LifeCycle
{
    private static final Logger LOG;
    public static final String STOPPED = "STOPPED";
    public static final String FAILED = "FAILED";
    public static final String STARTING = "STARTING";
    public static final String STARTED = "STARTED";
    public static final String STOPPING = "STOPPING";
    public static final String RUNNING = "RUNNING";
    private final CopyOnWriteArrayList<Listener> _listeners;
    private final Object _lock;
    private final int __FAILED = -1;
    private final int __STOPPED = 0;
    private final int __STARTING = 1;
    private final int __STARTED = 2;
    private final int __STOPPING = 3;
    private volatile int _state;
    private long _stopTimeout;
    public static final Listener STOP_ON_FAILURE;
    
    public AbstractLifeCycle() {
        this._listeners = new CopyOnWriteArrayList<Listener>();
        this._lock = new Object();
        this._state = 0;
        this._stopTimeout = 30000L;
    }
    
    protected void doStart() throws Exception {
    }
    
    protected void doStop() throws Exception {
    }
    
    @Override
    public final void start() throws Exception {
        synchronized (this._lock) {
            try {
                if (this._state == 2 || this._state == 1) {
                    return;
                }
                this.setStarting();
                this.doStart();
                this.setStarted();
            }
            catch (Throwable e) {
                this.setFailed(e);
                throw e;
            }
        }
    }
    
    @Override
    public final void stop() throws Exception {
        synchronized (this._lock) {
            try {
                if (this._state == 3 || this._state == 0) {
                    return;
                }
                this.setStopping();
                this.doStop();
                this.setStopped();
            }
            catch (Throwable e) {
                this.setFailed(e);
                throw e;
            }
        }
    }
    
    @Override
    public boolean isRunning() {
        final int state = this._state;
        return state == 2 || state == 1;
    }
    
    @Override
    public boolean isStarted() {
        return this._state == 2;
    }
    
    @Override
    public boolean isStarting() {
        return this._state == 1;
    }
    
    @Override
    public boolean isStopping() {
        return this._state == 3;
    }
    
    @Override
    public boolean isStopped() {
        return this._state == 0;
    }
    
    @Override
    public boolean isFailed() {
        return this._state == -1;
    }
    
    @Override
    public void addLifeCycleListener(final Listener listener) {
        this._listeners.add(listener);
    }
    
    @Override
    public void removeLifeCycleListener(final Listener listener) {
        this._listeners.remove(listener);
    }
    
    @ManagedAttribute(value = "Lifecycle State for this instance", readonly = true)
    public String getState() {
        switch (this._state) {
            case -1: {
                return "FAILED";
            }
            case 1: {
                return "STARTING";
            }
            case 2: {
                return "STARTED";
            }
            case 3: {
                return "STOPPING";
            }
            case 0: {
                return "STOPPED";
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getState(final LifeCycle lc) {
        if (lc.isStarting()) {
            return "STARTING";
        }
        if (lc.isStarted()) {
            return "STARTED";
        }
        if (lc.isStopping()) {
            return "STOPPING";
        }
        if (lc.isStopped()) {
            return "STOPPED";
        }
        return "FAILED";
    }
    
    private void setStarted() {
        this._state = 2;
        if (AbstractLifeCycle.LOG.isDebugEnabled()) {
            AbstractLifeCycle.LOG.debug("STARTED @{}ms {}", Uptime.getUptime(), this);
        }
        for (final Listener listener : this._listeners) {
            listener.lifeCycleStarted(this);
        }
    }
    
    private void setStarting() {
        if (AbstractLifeCycle.LOG.isDebugEnabled()) {
            AbstractLifeCycle.LOG.debug("starting {}", this);
        }
        this._state = 1;
        for (final Listener listener : this._listeners) {
            listener.lifeCycleStarting(this);
        }
    }
    
    private void setStopping() {
        if (AbstractLifeCycle.LOG.isDebugEnabled()) {
            AbstractLifeCycle.LOG.debug("stopping {}", this);
        }
        this._state = 3;
        for (final Listener listener : this._listeners) {
            listener.lifeCycleStopping(this);
        }
    }
    
    private void setStopped() {
        this._state = 0;
        if (AbstractLifeCycle.LOG.isDebugEnabled()) {
            AbstractLifeCycle.LOG.debug("{} {}", "STOPPED", this);
        }
        for (final Listener listener : this._listeners) {
            listener.lifeCycleStopped(this);
        }
    }
    
    private void setFailed(final Throwable th) {
        this._state = -1;
        if (AbstractLifeCycle.LOG.isDebugEnabled()) {
            AbstractLifeCycle.LOG.warn("FAILED " + this + ": " + th, th);
        }
        for (final Listener listener : this._listeners) {
            listener.lifeCycleFailure(this, th);
        }
    }
    
    @ManagedAttribute("The stop timeout in milliseconds")
    public long getStopTimeout() {
        return this._stopTimeout;
    }
    
    public void setStopTimeout(final long stopTimeout) {
        this._stopTimeout = stopTimeout;
    }
    
    static {
        LOG = Log.getLogger(AbstractLifeCycle.class);
        STOP_ON_FAILURE = new AbstractLifeCycleListener() {
            @Override
            public void lifeCycleFailure(final LifeCycle lifecycle, final Throwable cause) {
                try {
                    lifecycle.stop();
                }
                catch (Exception e) {
                    cause.addSuppressed(e);
                }
            }
        };
    }
    
    public abstract static class AbstractLifeCycleListener implements Listener
    {
        @Override
        public void lifeCycleFailure(final LifeCycle event, final Throwable cause) {
        }
        
        @Override
        public void lifeCycleStarted(final LifeCycle event) {
        }
        
        @Override
        public void lifeCycleStarting(final LifeCycle event) {
        }
        
        @Override
        public void lifeCycleStopped(final LifeCycle event) {
        }
        
        @Override
        public void lifeCycleStopping(final LifeCycle event) {
        }
    }
}
