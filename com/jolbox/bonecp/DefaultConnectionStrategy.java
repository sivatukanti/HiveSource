// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.sql.Connection;

public class DefaultConnectionStrategy extends AbstractConnectionStrategy
{
    private static final long serialVersionUID = 962520166486807512L;
    
    public DefaultConnectionStrategy(final BoneCP pool) {
        this.pool = pool;
    }
    
    @Override
    public ConnectionHandle pollConnection() {
        ConnectionHandle result = null;
        final int partition = (int)(Thread.currentThread().getId() % this.pool.partitionCount);
        ConnectionPartition connectionPartition = this.pool.partitions[partition];
        result = connectionPartition.getFreeConnections().poll();
        if (result == null) {
            for (int i = 0; i < this.pool.partitionCount; ++i) {
                if (i != partition) {
                    result = this.pool.partitions[i].getFreeConnections().poll();
                    if (result != null) {
                        connectionPartition = this.pool.partitions[i];
                        break;
                    }
                }
            }
        }
        if (!connectionPartition.isUnableToCreateMoreTransactions()) {
            this.pool.maybeSignalForMoreConnections(connectionPartition);
        }
        return result;
    }
    
    @Override
    protected Connection getConnectionInternal() throws SQLException {
        ConnectionHandle result = this.pollConnection();
        if (result == null) {
            final int partition = (int)(Thread.currentThread().getId() % this.pool.partitionCount);
            final ConnectionPartition connectionPartition = this.pool.partitions[partition];
            try {
                result = connectionPartition.getFreeConnections().poll(this.pool.connectionTimeoutInMs, TimeUnit.MILLISECONDS);
                if (result == null) {
                    if (this.pool.nullOnConnectionTimeout) {
                        return null;
                    }
                    throw new SQLException("Timed out waiting for a free available connection.", "08001");
                }
            }
            catch (InterruptedException e) {
                if (this.pool.nullOnConnectionTimeout) {
                    return null;
                }
                throw PoolUtil.generateSQLException(e.getMessage(), e);
            }
        }
        return result;
    }
    
    public void terminateAllConnections() {
        this.terminationLock.lock();
        try {
            for (int i = 0; i < this.pool.partitionCount; ++i) {
                this.pool.partitions[i].setUnableToCreateMoreTransactions(false);
                final List<ConnectionHandle> clist = new LinkedList<ConnectionHandle>();
                this.pool.partitions[i].getFreeConnections().drainTo(clist);
                for (final ConnectionHandle c : clist) {
                    this.pool.destroyConnection(c);
                }
            }
        }
        finally {
            this.terminationLock.unlock();
        }
    }
}
