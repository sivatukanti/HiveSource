// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import java.util.concurrent.ScheduledExecutorService;

public class ConnectionMaxAgeThread implements Runnable
{
    private long maxAgeInMs;
    private ConnectionPartition partition;
    private ScheduledExecutorService scheduler;
    private BoneCP pool;
    private boolean lifoMode;
    private static final Logger logger;
    
    protected ConnectionMaxAgeThread(final ConnectionPartition connectionPartition, final ScheduledExecutorService scheduler, final BoneCP pool, final long maxAgeInMs, final boolean lifoMode) {
        this.partition = connectionPartition;
        this.scheduler = scheduler;
        this.maxAgeInMs = maxAgeInMs;
        this.pool = pool;
        this.lifoMode = lifoMode;
    }
    
    public void run() {
        ConnectionHandle connection = null;
        long nextCheckInMs = this.maxAgeInMs;
        final int partitionSize = this.partition.getAvailableConnections();
        final long currentTime = System.currentTimeMillis();
        for (int i = 0; i < partitionSize; ++i) {
            try {
                connection = this.partition.getFreeConnections().poll();
                if (connection != null) {
                    connection.setOriginatingPartition(this.partition);
                    final long tmp = this.maxAgeInMs - (currentTime - connection.getConnectionCreationTimeInMs());
                    if (tmp < nextCheckInMs) {
                        nextCheckInMs = tmp;
                    }
                    if (connection.isExpired(currentTime)) {
                        this.closeConnection(connection);
                    }
                    else {
                        if (this.lifoMode) {
                            if (!connection.getOriginatingPartition().getFreeConnections().offer(connection)) {
                                connection.internalClose();
                            }
                        }
                        else {
                            this.pool.putConnectionBackInPartition(connection);
                        }
                        Thread.sleep(20L);
                    }
                }
            }
            catch (Throwable e) {
                if (this.scheduler.isShutdown()) {
                    ConnectionMaxAgeThread.logger.debug("Shutting down connection max age thread.");
                }
                else {
                    ConnectionMaxAgeThread.logger.error("Connection max age thread exception.", e);
                }
            }
        }
        if (!this.scheduler.isShutdown()) {
            this.scheduler.schedule(this, nextCheckInMs, TimeUnit.MILLISECONDS);
        }
    }
    
    protected void closeConnection(final ConnectionHandle connection) {
        if (connection != null) {
            try {
                connection.internalClose();
            }
            catch (Throwable t) {
                ConnectionMaxAgeThread.logger.error("Destroy connection exception", t);
            }
            finally {
                this.pool.postDestroyConnection(connection);
            }
        }
    }
    
    static {
        logger = LoggerFactory.getLogger(ConnectionTesterThread.class);
    }
}
