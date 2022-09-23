// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import java.io.File;
import org.slf4j.LoggerFactory;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import org.slf4j.Logger;

public class DatadirCleanupManager
{
    private static final Logger LOG;
    private PurgeTaskStatus purgeTaskStatus;
    private final String snapDir;
    private final String dataLogDir;
    private final int snapRetainCount;
    private final int purgeInterval;
    private Timer timer;
    
    public DatadirCleanupManager(final String snapDir, final String dataLogDir, final int snapRetainCount, final int purgeInterval) {
        this.purgeTaskStatus = PurgeTaskStatus.NOT_STARTED;
        this.snapDir = snapDir;
        this.dataLogDir = dataLogDir;
        this.snapRetainCount = snapRetainCount;
        this.purgeInterval = purgeInterval;
        DatadirCleanupManager.LOG.info("autopurge.snapRetainCount set to " + snapRetainCount);
        DatadirCleanupManager.LOG.info("autopurge.purgeInterval set to " + purgeInterval);
    }
    
    public void start() {
        if (PurgeTaskStatus.STARTED == this.purgeTaskStatus) {
            DatadirCleanupManager.LOG.warn("Purge task is already running.");
            return;
        }
        if (this.purgeInterval <= 0) {
            DatadirCleanupManager.LOG.info("Purge task is not scheduled.");
            return;
        }
        this.timer = new Timer("PurgeTask", true);
        final TimerTask task = new PurgeTask(this.dataLogDir, this.snapDir, this.snapRetainCount);
        this.timer.scheduleAtFixedRate(task, 0L, TimeUnit.HOURS.toMillis(this.purgeInterval));
        this.purgeTaskStatus = PurgeTaskStatus.STARTED;
    }
    
    public void shutdown() {
        if (PurgeTaskStatus.STARTED == this.purgeTaskStatus) {
            DatadirCleanupManager.LOG.info("Shutting down purge task.");
            this.timer.cancel();
            this.purgeTaskStatus = PurgeTaskStatus.COMPLETED;
        }
        else {
            DatadirCleanupManager.LOG.warn("Purge task not started. Ignoring shutdown!");
        }
    }
    
    public PurgeTaskStatus getPurgeTaskStatus() {
        return this.purgeTaskStatus;
    }
    
    public String getSnapDir() {
        return this.snapDir;
    }
    
    public String getDataLogDir() {
        return this.dataLogDir;
    }
    
    public int getPurgeInterval() {
        return this.purgeInterval;
    }
    
    public int getSnapRetainCount() {
        return this.snapRetainCount;
    }
    
    static {
        LOG = LoggerFactory.getLogger(DatadirCleanupManager.class);
    }
    
    public enum PurgeTaskStatus
    {
        NOT_STARTED, 
        STARTED, 
        COMPLETED;
    }
    
    static class PurgeTask extends TimerTask
    {
        private String logsDir;
        private String snapsDir;
        private int snapRetainCount;
        
        public PurgeTask(final String dataDir, final String snapDir, final int count) {
            this.logsDir = dataDir;
            this.snapsDir = snapDir;
            this.snapRetainCount = count;
        }
        
        @Override
        public void run() {
            DatadirCleanupManager.LOG.info("Purge task started.");
            try {
                PurgeTxnLog.purge(new File(this.logsDir), new File(this.snapsDir), this.snapRetainCount);
            }
            catch (Exception e) {
                DatadirCleanupManager.LOG.error("Error occurred while purging.", e);
            }
            DatadirCleanupManager.LOG.info("Purge task completed.");
        }
    }
}
