// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import java.util.concurrent.ScheduledExecutorService;

public class ConnectionTesterThread implements Runnable
{
    private long idleConnectionTestPeriodInMs;
    private long idleMaxAgeInMs;
    private ConnectionPartition partition;
    private ScheduledExecutorService scheduler;
    private BoneCP pool;
    private boolean lifoMode;
    private static final Logger logger;
    
    protected ConnectionTesterThread(final ConnectionPartition connectionPartition, final ScheduledExecutorService scheduler, final BoneCP pool, final long idleMaxAgeInMs, final long idleConnectionTestPeriodInMs, final boolean lifoMode) {
        this.partition = connectionPartition;
        this.scheduler = scheduler;
        this.idleMaxAgeInMs = idleMaxAgeInMs;
        this.idleConnectionTestPeriodInMs = idleConnectionTestPeriodInMs;
        this.pool = pool;
        this.lifoMode = lifoMode;
    }
    
    public void run() {
        ConnectionHandle connection = null;
        try {
            long nextCheckInMs = this.idleConnectionTestPeriodInMs;
            if (this.idleMaxAgeInMs > 0L) {
                if (this.idleConnectionTestPeriodInMs == 0L) {
                    nextCheckInMs = this.idleMaxAgeInMs;
                }
                else {
                    nextCheckInMs = Math.min(nextCheckInMs, this.idleMaxAgeInMs);
                }
            }
            final int partitionSize = this.partition.getAvailableConnections();
            final long currentTimeInMs = System.currentTimeMillis();
            for (int i = 0; i < partitionSize; ++i) {
                connection = this.partition.getFreeConnections().poll();
                if (connection != null) {
                    connection.setOriginatingPartition(this.partition);
                    if (connection.isPossiblyBroken() || (this.idleMaxAgeInMs > 0L && System.currentTimeMillis() - connection.getConnectionLastUsedInMs() > this.idleMaxAgeInMs)) {
                        this.closeConnection(connection);
                    }
                    else {
                        long tmp;
                        if (this.idleConnectionTestPeriodInMs > 0L && currentTimeInMs - connection.getConnectionLastUsedInMs() > this.idleConnectionTestPeriodInMs && currentTimeInMs - connection.getConnectionLastResetInMs() >= this.idleConnectionTestPeriodInMs) {
                            if (!this.pool.isConnectionHandleAlive(connection)) {
                                this.closeConnection(connection);
                                continue;
                            }
                            tmp = this.idleConnectionTestPeriodInMs;
                            if (this.idleMaxAgeInMs > 0L) {
                                tmp = Math.min(tmp, this.idleMaxAgeInMs);
                            }
                        }
                        else {
                            tmp = Math.abs(this.idleConnectionTestPeriodInMs - (currentTimeInMs - connection.getConnectionLastResetInMs()));
                            final long tmp2 = Math.abs(this.idleMaxAgeInMs - (currentTimeInMs - connection.getConnectionLastUsedInMs()));
                            if (this.idleMaxAgeInMs > 0L) {
                                tmp = Math.min(tmp, tmp2);
                            }
                        }
                        if (tmp < nextCheckInMs) {
                            nextCheckInMs = tmp;
                        }
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
            this.scheduler.schedule(this, nextCheckInMs, TimeUnit.MILLISECONDS);
        }
        catch (Throwable e) {
            if (this.scheduler.isShutdown()) {
                ConnectionTesterThread.logger.debug("Shutting down connection tester thread.");
            }
            else {
                ConnectionTesterThread.logger.error("Connection tester thread interrupted", e);
            }
        }
    }
    
    protected void closeConnection(final ConnectionHandle connection) {
        if (connection != null && !connection.isClosed()) {
            try {
                connection.internalClose();
            }
            catch (SQLException e) {
                ConnectionTesterThread.logger.error("Destroy connection exception", e);
            }
            finally {
                this.pool.postDestroyConnection(connection);
                connection.getOriginatingPartition().getPoolWatchThreadSignalQueue().offer(new Object());
            }
        }
    }
    
    static {
        logger = LoggerFactory.getLogger(ConnectionTesterThread.class);
    }
}
