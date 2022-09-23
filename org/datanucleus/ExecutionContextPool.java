// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import java.util.Iterator;
import java.util.Set;
import org.datanucleus.util.NucleusLogger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ExecutionContextPool
{
    private NucleusContext nucCtx;
    private long maxIdle;
    private long expirationTime;
    private Map<ExecutionContext, Long> recyclableECs;
    private CleanUpThread cleaner;
    
    public ExecutionContextPool(final NucleusContext nucCtx) {
        this.maxIdle = 20L;
        this.maxIdle = nucCtx.getPersistenceConfiguration().getIntProperty("datanucleus.executionContext.maxIdle");
        this.nucCtx = nucCtx;
        this.expirationTime = 30000L;
        this.recyclableECs = new ConcurrentHashMap<ExecutionContext, Long>();
        if (nucCtx.getPersistenceConfiguration().getBooleanProperty("datanucleus.executionContext.reaperThread")) {
            (this.cleaner = new CleanUpThread(this, this.expirationTime * 2L)).start();
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug("Started pool of ExecutionContext (maxPool=" + this.maxIdle + ", reaperThread=" + (this.cleaner != null) + ")");
        }
    }
    
    protected ExecutionContext create(final Object owner, final Map<String, Object> options) {
        if (this.nucCtx.getPersistenceConfiguration().getBooleanProperty("datanucleus.Multithreaded")) {
            return new ExecutionContextThreadedImpl(this.nucCtx, owner, options);
        }
        return new ExecutionContextImpl(this.nucCtx, owner, options);
    }
    
    public boolean validate(final ExecutionContext ec) {
        return true;
    }
    
    public void expire(final ExecutionContext ec) {
    }
    
    public synchronized ExecutionContext checkOut(final Object owner, final Map<String, Object> options) {
        final long now = System.currentTimeMillis();
        if (this.recyclableECs.size() > 0) {
            final Set<ExecutionContext> e = this.recyclableECs.keySet();
            for (ExecutionContext ec : e) {
                if (now - this.recyclableECs.get(ec) > this.expirationTime) {
                    this.recyclableECs.remove(ec);
                    this.expire(ec);
                    ec = null;
                }
                else {
                    if (this.validate(ec)) {
                        this.recyclableECs.remove(ec);
                        ec.initialise(owner, options);
                        return ec;
                    }
                    this.recyclableECs.remove(ec);
                    this.expire(ec);
                    ec = null;
                }
            }
        }
        ExecutionContext ec = this.create(owner, options);
        return ec;
    }
    
    public synchronized void cleanUp() {
        final long now = System.currentTimeMillis();
        final Set<ExecutionContext> e = this.recyclableECs.keySet();
        for (ExecutionContext ec : e) {
            if (now - this.recyclableECs.get(ec) > this.expirationTime) {
                this.recyclableECs.remove(ec);
                this.expire(ec);
                ec = null;
            }
        }
        System.gc();
    }
    
    public synchronized void checkIn(final ExecutionContext ec) {
        if (this.recyclableECs.size() < this.maxIdle) {
            this.recyclableECs.put(ec, System.currentTimeMillis());
        }
    }
    
    class CleanUpThread extends Thread
    {
        private ExecutionContextPool pool;
        private long sleepTime;
        
        CleanUpThread(final ExecutionContextPool pool, final long sleepTime) {
            this.pool = pool;
            this.sleepTime = sleepTime;
        }
        
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(this.sleepTime);
                }
                catch (InterruptedException ex) {}
                this.pool.cleanUp();
            }
        }
    }
}
