// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public abstract class CachingGetSpaceUsed implements Closeable, GetSpaceUsed
{
    static final Logger LOG;
    protected final AtomicLong used;
    private final AtomicBoolean running;
    private final long refreshInterval;
    private final long jitter;
    private final String dirPath;
    private Thread refreshUsed;
    
    public CachingGetSpaceUsed(final Builder builder) throws IOException {
        this(builder.getPath(), builder.getInterval(), builder.getJitter(), builder.getInitialUsed());
    }
    
    CachingGetSpaceUsed(final File path, final long interval, final long jitter, final long initialUsed) throws IOException {
        this.used = new AtomicLong();
        this.running = new AtomicBoolean(true);
        this.dirPath = path.getCanonicalPath();
        this.refreshInterval = interval;
        this.jitter = jitter;
        this.used.set(initialUsed);
    }
    
    void init() {
        if (this.used.get() < 0L) {
            this.used.set(0L);
            this.refresh();
        }
        if (this.refreshInterval > 0L) {
            (this.refreshUsed = new Thread(new RefreshThread(this), "refreshUsed-" + this.dirPath)).setDaemon(true);
            this.refreshUsed.start();
        }
        else {
            this.running.set(false);
            this.refreshUsed = null;
        }
    }
    
    protected abstract void refresh();
    
    @Override
    public long getUsed() throws IOException {
        return Math.max(this.used.get(), 0L);
    }
    
    public String getDirPath() {
        return this.dirPath;
    }
    
    public void incDfsUsed(final long value) {
        this.used.addAndGet(value);
    }
    
    boolean running() {
        return this.running.get();
    }
    
    long getRefreshInterval() {
        return this.refreshInterval;
    }
    
    protected void setUsed(final long usedValue) {
        this.used.set(usedValue);
    }
    
    @Override
    public void close() throws IOException {
        this.running.set(false);
        if (this.refreshUsed != null) {
            this.refreshUsed.interrupt();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(CachingGetSpaceUsed.class);
    }
    
    private static final class RefreshThread implements Runnable
    {
        final CachingGetSpaceUsed spaceUsed;
        
        RefreshThread(final CachingGetSpaceUsed spaceUsed) {
            this.spaceUsed = spaceUsed;
        }
        
        @Override
        public void run() {
            while (this.spaceUsed.running()) {
                try {
                    long refreshInterval = this.spaceUsed.refreshInterval;
                    if (this.spaceUsed.jitter > 0L) {
                        final long jitter = this.spaceUsed.jitter;
                        refreshInterval += ThreadLocalRandom.current().nextLong(-jitter, jitter);
                    }
                    refreshInterval = Math.max(refreshInterval, 1L);
                    Thread.sleep(refreshInterval);
                    this.spaceUsed.refresh();
                }
                catch (InterruptedException e) {
                    CachingGetSpaceUsed.LOG.warn("Thread Interrupted waiting to refresh disk information: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
