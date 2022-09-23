// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.io.Serializable;

public abstract class AbstractConnectionStrategy implements ConnectionStrategy, Serializable
{
    private static final long serialVersionUID = 27805973487155497L;
    protected BoneCP pool;
    protected Lock terminationLock;
    
    public AbstractConnectionStrategy() {
        this.terminationLock = new ReentrantLock();
    }
    
    protected long preConnection() throws SQLException {
        long statsObtainTime = 0L;
        if (this.pool.poolShuttingDown) {
            throw new SQLException(this.pool.shutdownStackTrace);
        }
        if (this.pool.statisticsEnabled) {
            statsObtainTime = System.nanoTime();
            this.pool.statistics.incrementConnectionsRequested();
        }
        return statsObtainTime;
    }
    
    protected void postConnection(final ConnectionHandle handle, final long statsObtainTime) {
        handle.renewConnection();
        if (handle.getConnectionHook() != null) {
            handle.getConnectionHook().onCheckOut(handle);
        }
        if (this.pool.closeConnectionWatch) {
            this.pool.watchConnection(handle);
        }
        if (this.pool.statisticsEnabled) {
            this.pool.statistics.addCumulativeConnectionWaitTime(System.nanoTime() - statsObtainTime);
        }
    }
    
    public Connection getConnection() throws SQLException {
        final long statsObtainTime = this.preConnection();
        final ConnectionHandle result = (ConnectionHandle)this.getConnectionInternal();
        if (result != null) {
            this.postConnection(result, statsObtainTime);
        }
        return result;
    }
    
    protected abstract Connection getConnectionInternal() throws SQLException;
    
    public ConnectionHandle pollConnection() {
        return null;
    }
    
    public void cleanupConnection(final ConnectionHandle oldHandle, final ConnectionHandle newHandle) {
    }
}
