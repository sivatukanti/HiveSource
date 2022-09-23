// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;
import com.google.common.base.FinalizableReferenceQueue;
import com.google.common.base.FinalizableWeakReference;
import java.sql.Connection;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import java.io.Serializable;

public class ConnectionPartition implements Serializable
{
    private static final long serialVersionUID = -7864443421028454573L;
    private static final Logger logger;
    private BlockingQueue<ConnectionHandle> freeConnections;
    private final int acquireIncrement;
    private final int minConnections;
    private final int maxConnections;
    protected ReentrantReadWriteLock statsLock;
    private int createdConnections;
    private final String url;
    private final String username;
    private final String password;
    private volatile boolean unableToCreateMoreTransactions;
    private boolean disableTracking;
    private BlockingQueue<Object> poolWatchThreadSignalQueue;
    private long queryExecuteTimeLimitInNanoSeconds;
    private String poolName;
    protected BoneCP pool;
    
    protected BlockingQueue<Object> getPoolWatchThreadSignalQueue() {
        return this.poolWatchThreadSignalQueue;
    }
    
    protected void updateCreatedConnections(final int increment) {
        try {
            this.statsLock.writeLock().lock();
            this.createdConnections += increment;
        }
        finally {
            this.statsLock.writeLock().unlock();
        }
    }
    
    protected void addFreeConnection(final ConnectionHandle connectionHandle) throws SQLException {
        connectionHandle.setOriginatingPartition(this);
        this.updateCreatedConnections(1);
        if (!this.disableTracking) {
            this.trackConnectionFinalizer(connectionHandle);
        }
        if (!this.freeConnections.offer(connectionHandle)) {
            this.updateCreatedConnections(-1);
            if (!this.disableTracking) {
                this.pool.getFinalizableRefs().remove(connectionHandle.getInternalConnection());
            }
            connectionHandle.internalClose();
        }
    }
    
    protected void trackConnectionFinalizer(final ConnectionHandle connectionHandle) {
        if (!this.disableTracking) {
            Connection con = connectionHandle.getInternalConnection();
            if (con != null && con instanceof Proxy && Proxy.getInvocationHandler(con) instanceof MemorizeTransactionProxy) {
                try {
                    con = (Connection)Proxy.getInvocationHandler(con).invoke(con, ConnectionHandle.class.getMethod("getProxyTarget", (Class<?>[])new Class[0]), null);
                }
                catch (Throwable t) {
                    ConnectionPartition.logger.error("Error while attempting to track internal db connection", t);
                }
            }
            final Connection internalDBConnection = con;
            final BoneCP pool = connectionHandle.getPool();
            connectionHandle.getPool().getFinalizableRefs().put(internalDBConnection, new FinalizableWeakReference<ConnectionHandle>(connectionHandle, connectionHandle.getPool().getFinalizableRefQueue()) {
                public void finalizeReferent() {
                    try {
                        pool.getFinalizableRefs().remove(internalDBConnection);
                        if (internalDBConnection != null && !internalDBConnection.isClosed()) {
                            ConnectionPartition.logger.warn("BoneCP detected an unclosed connection " + ConnectionPartition.this.poolName + "and will now attempt to close it for you. " + "You should be closing this connection in your application - enable connectionWatch for additional debugging assistance or set disableConnectionTracking to true to disable this feature entirely.");
                            internalDBConnection.close();
                            ConnectionPartition.this.updateCreatedConnections(-1);
                        }
                    }
                    catch (Throwable t) {
                        ConnectionPartition.logger.error("Error while closing off internal db connection", t);
                    }
                }
            });
        }
    }
    
    protected BlockingQueue<ConnectionHandle> getFreeConnections() {
        return this.freeConnections;
    }
    
    protected void setFreeConnections(final BlockingQueue<ConnectionHandle> freeConnections) {
        this.freeConnections = freeConnections;
    }
    
    public ConnectionPartition(final BoneCP pool) {
        this.statsLock = new ReentrantReadWriteLock();
        this.createdConnections = 0;
        this.unableToCreateMoreTransactions = false;
        this.poolWatchThreadSignalQueue = new ArrayBlockingQueue<Object>(1);
        final BoneCPConfig config = pool.getConfig();
        this.minConnections = config.getMinConnectionsPerPartition();
        this.maxConnections = config.getMaxConnectionsPerPartition();
        this.acquireIncrement = config.getAcquireIncrement();
        this.url = config.getJdbcUrl();
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.poolName = ((config.getPoolName() != null) ? ("(in pool '" + config.getPoolName() + "') ") : "");
        this.pool = pool;
        this.disableTracking = config.isDisableConnectionTracking();
        this.queryExecuteTimeLimitInNanoSeconds = TimeUnit.NANOSECONDS.convert(config.getQueryExecuteTimeLimitInMs(), TimeUnit.MILLISECONDS);
    }
    
    protected int getAcquireIncrement() {
        return this.acquireIncrement;
    }
    
    protected int getMinConnections() {
        return this.minConnections;
    }
    
    protected int getMaxConnections() {
        return this.maxConnections;
    }
    
    protected int getCreatedConnections() {
        try {
            this.statsLock.readLock().lock();
            return this.createdConnections;
        }
        finally {
            this.statsLock.readLock().unlock();
        }
    }
    
    protected String getUrl() {
        return this.url;
    }
    
    protected String getUsername() {
        return this.username;
    }
    
    protected String getPassword() {
        return this.password;
    }
    
    protected boolean isUnableToCreateMoreTransactions() {
        return this.unableToCreateMoreTransactions;
    }
    
    protected void setUnableToCreateMoreTransactions(final boolean unableToCreateMoreTransactions) {
        this.unableToCreateMoreTransactions = unableToCreateMoreTransactions;
    }
    
    protected int getAvailableConnections() {
        return this.freeConnections.size();
    }
    
    public int getRemainingCapacity() {
        return this.freeConnections.remainingCapacity();
    }
    
    protected long getQueryExecuteTimeLimitinNanoSeconds() {
        return this.queryExecuteTimeLimitInNanoSeconds;
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("url", this.pool.getConfig().getJdbcUrl()).add("user", this.pool.getConfig().getUsername()).add("minConnections", this.getMinConnections()).add("maxConnections", this.getMaxConnections()).add("acquireIncrement", this.acquireIncrement).add("createdConnections", this.createdConnections).add("freeConnections", this.getFreeConnections()).toString();
    }
    
    static {
        logger = LoggerFactory.getLogger(ConnectionPartition.class);
    }
}
