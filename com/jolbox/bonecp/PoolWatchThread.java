// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import java.sql.Connection;
import org.slf4j.Logger;

public class PoolWatchThread implements Runnable
{
    private ConnectionPartition partition;
    private BoneCP pool;
    private boolean signalled;
    private long acquireRetryDelayInMs;
    protected boolean lazyInit;
    private int poolAvailabilityThreshold;
    private static final Logger logger;
    
    public PoolWatchThread(final ConnectionPartition connectionPartition, final BoneCP pool) {
        this.acquireRetryDelayInMs = 1000L;
        this.partition = connectionPartition;
        this.pool = pool;
        this.lazyInit = this.pool.getConfig().isLazyInit();
        this.acquireRetryDelayInMs = this.pool.getConfig().getAcquireRetryDelayInMs();
        this.poolAvailabilityThreshold = this.pool.getConfig().getPoolAvailabilityThreshold();
    }
    
    public void run() {
        while (!this.signalled) {
            int maxNewConnections = 0;
            try {
                if (this.lazyInit) {
                    this.partition.getPoolWatchThreadSignalQueue().take();
                }
                for (maxNewConnections = this.partition.getMaxConnections() - this.partition.getCreatedConnections(); maxNewConnections == 0 || this.partition.getAvailableConnections() * 100 / this.partition.getMaxConnections() > this.poolAvailabilityThreshold; maxNewConnections = this.partition.getMaxConnections() - this.partition.getCreatedConnections()) {
                    if (maxNewConnections == 0) {
                        this.partition.setUnableToCreateMoreTransactions(true);
                    }
                    this.partition.getPoolWatchThreadSignalQueue().take();
                }
                if (maxNewConnections > 0 && !this.pool.poolShuttingDown) {
                    this.fillConnections(Math.min(maxNewConnections, this.partition.getAcquireIncrement()));
                    if (this.partition.getCreatedConnections() < this.partition.getMinConnections()) {
                        this.fillConnections(this.partition.getMinConnections() - this.partition.getCreatedConnections());
                    }
                }
                if (this.pool.poolShuttingDown) {
                    return;
                }
                continue;
            }
            catch (InterruptedException e) {
                PoolWatchThread.logger.debug("Terminating pool watch thread");
                return;
            }
            break;
        }
    }
    
    private void fillConnections(final int connectionsToCreate) throws InterruptedException {
        try {
            for (int i = 0; i < connectionsToCreate && !this.pool.poolShuttingDown; ++i) {
                this.partition.addFreeConnection(new ConnectionHandle(null, this.partition, this.pool, false));
            }
        }
        catch (Exception e) {
            PoolWatchThread.logger.error("Error in trying to obtain a connection. Retrying in " + this.acquireRetryDelayInMs + "ms", e);
            Thread.sleep(this.acquireRetryDelayInMs);
        }
    }
    
    static {
        logger = LoggerFactory.getLogger(PoolWatchThread.class);
    }
}
