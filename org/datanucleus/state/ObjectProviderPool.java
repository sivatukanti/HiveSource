// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import java.util.Iterator;
import java.util.Set;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.NucleusLogger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class ObjectProviderPool
{
    private long maxIdle;
    private long expirationTime;
    private Map<ObjectProvider, Long> recyclableOps;
    private CleanUpThread cleaner;
    private Class opClass;
    
    public ObjectProviderPool(final int maxIdle, final boolean reaperThread, final Class opClass) {
        this.maxIdle = 100L;
        this.maxIdle = maxIdle;
        this.expirationTime = 30000L;
        this.recyclableOps = new ConcurrentHashMap<ObjectProvider, Long>();
        this.opClass = opClass;
        if (reaperThread) {
            (this.cleaner = new CleanUpThread(this, this.expirationTime * 2L)).start();
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug("Started pool of ObjectProviders (maxPool=" + maxIdle + ", reaperThread=" + reaperThread + ")");
        }
    }
    
    public void close() {
        if (this.cleaner != null) {
            this.cleaner.interrupt();
        }
    }
    
    protected ObjectProvider create(final ExecutionContext ec, final AbstractClassMetaData cmd) {
        return (ObjectProvider)ClassUtils.newInstance(this.opClass, ObjectProviderFactoryImpl.OBJECT_PROVIDER_CTR_ARG_CLASSES, new Object[] { ec, cmd });
    }
    
    public boolean validate(final ObjectProvider op) {
        return true;
    }
    
    public void expire(final ObjectProvider op) {
    }
    
    public synchronized ObjectProvider checkOut(final ExecutionContext ec, final AbstractClassMetaData cmd) {
        final long now = System.currentTimeMillis();
        if (this.recyclableOps.size() > 0) {
            final Set<ObjectProvider> ops = this.recyclableOps.keySet();
            for (ObjectProvider op : ops) {
                if (now - this.recyclableOps.get(op) > this.expirationTime) {
                    this.recyclableOps.remove(op);
                    this.expire(op);
                    op = null;
                }
                else {
                    if (this.validate(op)) {
                        this.recyclableOps.remove(op);
                        op.connect(ec, cmd);
                        return op;
                    }
                    this.recyclableOps.remove(op);
                    this.expire(op);
                    op = null;
                }
            }
        }
        ObjectProvider op = this.create(ec, cmd);
        return op;
    }
    
    public synchronized void cleanUp() {
        final long now = System.currentTimeMillis();
        final Set<ObjectProvider> ops = this.recyclableOps.keySet();
        for (ObjectProvider op : ops) {
            if (now - this.recyclableOps.get(op) > this.expirationTime) {
                this.recyclableOps.remove(op);
                this.expire(op);
                op = null;
            }
        }
        System.gc();
    }
    
    public synchronized void checkIn(final ObjectProvider op) {
        if (this.recyclableOps.size() < this.maxIdle) {
            this.recyclableOps.put(op, System.currentTimeMillis());
        }
    }
    
    class CleanUpThread extends Thread
    {
        private ObjectProviderPool pool;
        private long sleepTime;
        
        CleanUpThread(final ObjectProviderPool pool, final long sleepTime) {
            this.pool = pool;
            this.sleepTime = sleepTime;
        }
        
        @Override
        public void run() {
            boolean needsStopping = false;
            while (!needsStopping) {
                try {
                    Thread.sleep(this.sleepTime);
                }
                catch (InterruptedException e) {
                    needsStopping = true;
                }
                this.pool.cleanUp();
            }
        }
    }
}
