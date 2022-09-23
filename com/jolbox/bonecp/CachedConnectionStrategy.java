// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import com.google.common.util.concurrent.Uninterruptibles;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;
import com.google.common.base.FinalizableWeakReference;
import java.util.Iterator;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.AbstractMap;
import com.google.common.base.FinalizableReferenceQueue;
import java.lang.ref.Reference;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;

public class CachedConnectionStrategy extends AbstractConnectionStrategy
{
    private static final long serialVersionUID = -4725640468699097218L;
    private static final Logger logger;
    private volatile AtomicBoolean warnApp;
    protected final Map<ConnectionHandle, Reference<Thread>> threadFinalizableRefs;
    private FinalizableReferenceQueue finalizableRefQueue;
    private ConnectionStrategy fallbackStrategy;
    protected CachedConnectionStrategyThreadLocal<AbstractMap.SimpleEntry<ConnectionHandle, Boolean>> tlConnections;
    
    public CachedConnectionStrategy(final BoneCP pool, final ConnectionStrategy fallbackStrategy) {
        this.warnApp = new AtomicBoolean();
        this.threadFinalizableRefs = new ConcurrentHashMap<ConnectionHandle, Reference<Thread>>();
        this.finalizableRefQueue = new FinalizableReferenceQueue();
        this.pool = pool;
        this.fallbackStrategy = fallbackStrategy;
        this.tlConnections = new CachedConnectionStrategyThreadLocal<AbstractMap.SimpleEntry<ConnectionHandle, Boolean>>(this, this.fallbackStrategy);
    }
    
    protected synchronized void stealExistingAllocations() {
        for (final ConnectionHandle handle : this.threadFinalizableRefs.keySet()) {
            if (handle.logicallyClosed.compareAndSet(true, false)) {
                try {
                    this.pool.releaseConnection(handle);
                }
                catch (SQLException e) {
                    CachedConnectionStrategy.logger.error("Error releasing connection", e);
                }
            }
        }
        if (this.warnApp.compareAndSet(false, true)) {
            CachedConnectionStrategy.logger.warn("Cached strategy chosen, but more threads are requesting a connection than are configured. Switching permanently to default strategy.");
        }
        this.threadFinalizableRefs.clear();
    }
    
    protected void threadWatch(final ConnectionHandle c) {
        this.threadFinalizableRefs.put(c, new FinalizableWeakReference<Thread>(Thread.currentThread(), this.finalizableRefQueue) {
            public void finalizeReferent() {
                try {
                    if (!CachedConnectionStrategy.this.pool.poolShuttingDown) {
                        CachedConnectionStrategy.logger.debug("Monitored thread is dead, closing off allocated connection.");
                    }
                    c.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                CachedConnectionStrategy.this.threadFinalizableRefs.remove(c);
            }
        });
    }
    
    @Override
    protected Connection getConnectionInternal() throws SQLException {
        final AbstractMap.SimpleEntry<ConnectionHandle, Boolean> result = this.tlConnections.get();
        if (result == null) {
            this.pool.cachedPoolStrategy = false;
            this.pool.connectionStrategy = this.fallbackStrategy;
            this.stealExistingAllocations();
            return this.pool.connectionStrategy.getConnection();
        }
        return result.getKey();
    }
    
    @Override
    public ConnectionHandle pollConnection() {
        throw new UnsupportedOperationException();
    }
    
    public void terminateAllConnections() {
        for (final ConnectionHandle conn : this.threadFinalizableRefs.keySet()) {
            this.pool.destroyConnection(conn);
        }
        this.threadFinalizableRefs.clear();
        this.fallbackStrategy.terminateAllConnections();
    }
    
    @Override
    public void cleanupConnection(final ConnectionHandle oldHandle, final ConnectionHandle newHandle) {
        this.threadFinalizableRefs.remove(oldHandle);
        this.threadWatch(newHandle);
    }
    
    static {
        logger = LoggerFactory.getLogger(CachedConnectionStrategy.class);
    }
    
    protected class CachedConnectionStrategyThreadLocal<T> extends ThreadLocal<AbstractMap.SimpleEntry<ConnectionHandle, Boolean>>
    {
        private ConnectionStrategy fallbackStrategy;
        private CachedConnectionStrategy ccs;
        
        public CachedConnectionStrategyThreadLocal(final CachedConnectionStrategy ccs, final ConnectionStrategy fallbackStrategy) {
            this.fallbackStrategy = fallbackStrategy;
            this.ccs = ccs;
        }
        
        @Override
        protected AbstractMap.SimpleEntry<ConnectionHandle, Boolean> initialValue() {
            AbstractMap.SimpleEntry<ConnectionHandle, Boolean> result = null;
            ConnectionHandle c = null;
            for (int i = 0; i < 12; ++i) {
                c = (ConnectionHandle)this.fallbackStrategy.pollConnection();
                if (c != null) {
                    break;
                }
                Uninterruptibles.sleepUninterruptibly(100L, TimeUnit.MILLISECONDS);
            }
            if (c != null) {
                result = new AbstractMap.SimpleEntry<ConnectionHandle, Boolean>(c, false);
                this.ccs.threadWatch(c);
            }
            return result;
        }
        
        public AbstractMap.SimpleEntry<ConnectionHandle, Boolean> dumbGet() {
            return super.get();
        }
        
        @Override
        public AbstractMap.SimpleEntry<ConnectionHandle, Boolean> get() {
            AbstractMap.SimpleEntry<ConnectionHandle, Boolean> result = super.get();
            if (result == null || result.getValue()) {
                final ConnectionHandle fallbackConnection = (ConnectionHandle)this.fallbackStrategy.pollConnection();
                if (fallbackConnection == null) {
                    return null;
                }
                result = new AbstractMap.SimpleEntry<ConnectionHandle, Boolean>(fallbackConnection, false);
            }
            result.setValue(true);
            result.getKey().logicallyClosed.set(false);
            return result;
        }
    }
}
