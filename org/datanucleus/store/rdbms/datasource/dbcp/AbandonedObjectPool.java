// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp;

import java.util.Iterator;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.PoolableObjectFactory;
import java.util.List;
import org.datanucleus.store.rdbms.datasource.dbcp.pool.impl.GenericObjectPool;

public class AbandonedObjectPool extends GenericObjectPool
{
    private final AbandonedConfig config;
    private final List trace;
    
    public AbandonedObjectPool(final PoolableObjectFactory factory, final AbandonedConfig config) {
        super(factory);
        this.trace = new ArrayList();
        this.config = config;
    }
    
    @Override
    public Object borrowObject() throws Exception {
        if (this.config != null && this.config.getRemoveAbandoned() && this.getNumIdle() < 2 && this.getNumActive() > this.getMaxActive() - 3) {
            this.removeAbandoned();
        }
        final Object obj = super.borrowObject();
        if (obj instanceof AbandonedTrace) {
            ((AbandonedTrace)obj).setStackTrace();
        }
        if (obj != null && this.config != null && this.config.getRemoveAbandoned()) {
            synchronized (this.trace) {
                this.trace.add(obj);
            }
        }
        return obj;
    }
    
    @Override
    public void returnObject(final Object obj) throws Exception {
        if (this.config != null && this.config.getRemoveAbandoned()) {
            synchronized (this.trace) {
                final boolean foundObject = this.trace.remove(obj);
                if (!foundObject) {
                    return;
                }
            }
        }
        super.returnObject(obj);
    }
    
    @Override
    public void invalidateObject(final Object obj) throws Exception {
        if (this.config != null && this.config.getRemoveAbandoned()) {
            synchronized (this.trace) {
                final boolean foundObject = this.trace.remove(obj);
                if (!foundObject) {
                    return;
                }
            }
        }
        super.invalidateObject(obj);
    }
    
    private void removeAbandoned() {
        final long now = System.currentTimeMillis();
        final long timeout = now - this.config.getRemoveAbandonedTimeout() * 1000;
        final ArrayList remove = new ArrayList();
        synchronized (this.trace) {
            for (final AbandonedTrace pc : this.trace) {
                if (pc.getLastUsed() > timeout) {
                    continue;
                }
                if (pc.getLastUsed() <= 0L) {
                    continue;
                }
                remove.add(pc);
            }
        }
        for (final AbandonedTrace pc2 : remove) {
            if (this.config.getLogAbandoned()) {
                pc2.printStackTrace();
            }
            try {
                this.invalidateObject(pc2);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
