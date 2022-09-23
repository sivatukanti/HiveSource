// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import java.io.Closeable;
import org.apache.curator.utils.CloseableUtils;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import org.apache.curator.framework.api.TempGetDataBuilder;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorTempFramework;

public class CuratorTempFrameworkImpl implements CuratorTempFramework
{
    private final CuratorFrameworkFactory.Builder factory;
    private final long inactiveThresholdMs;
    private CuratorFrameworkImpl client;
    private ScheduledExecutorService cleanup;
    private long lastAccess;
    
    public CuratorTempFrameworkImpl(final CuratorFrameworkFactory.Builder factory, final long inactiveThresholdMs) {
        this.factory = factory;
        this.inactiveThresholdMs = inactiveThresholdMs;
    }
    
    @Override
    public void close() {
        this.closeClient();
    }
    
    @Override
    public CuratorTransaction inTransaction() throws Exception {
        this.openConnectionIfNeeded();
        return new CuratorTransactionImpl(this.client);
    }
    
    @Override
    public TempGetDataBuilder getData() throws Exception {
        this.openConnectionIfNeeded();
        return new TempGetDataBuilderImpl(this.client);
    }
    
    @VisibleForTesting
    CuratorFrameworkImpl getClient() {
        return this.client;
    }
    
    @VisibleForTesting
    ScheduledExecutorService getCleanup() {
        return this.cleanup;
    }
    
    @VisibleForTesting
    synchronized void updateLastAccess() {
        this.lastAccess = System.currentTimeMillis();
    }
    
    private synchronized void openConnectionIfNeeded() throws Exception {
        if (this.client == null) {
            (this.client = (CuratorFrameworkImpl)this.factory.build()).start();
        }
        if (this.cleanup == null) {
            ThreadFactory threadFactory = this.factory.getThreadFactory();
            if (threadFactory == null) {
                threadFactory = ThreadUtils.newGenericThreadFactory("CuratorTempFrameworkImpl");
            }
            this.cleanup = Executors.newScheduledThreadPool(1, threadFactory);
            final Runnable command = new Runnable() {
                @Override
                public void run() {
                    CuratorTempFrameworkImpl.this.checkInactive();
                }
            };
            this.cleanup.scheduleAtFixedRate(command, this.inactiveThresholdMs, this.inactiveThresholdMs, TimeUnit.MILLISECONDS);
        }
        this.updateLastAccess();
    }
    
    private synchronized void checkInactive() {
        final long elapsed = System.currentTimeMillis() - this.lastAccess;
        if (elapsed >= this.inactiveThresholdMs) {
            this.closeClient();
        }
    }
    
    private synchronized void closeClient() {
        if (this.cleanup != null) {
            this.cleanup.shutdownNow();
            this.cleanup = null;
        }
        if (this.client != null) {
            CloseableUtils.closeQuietly(this.client);
            this.client = null;
        }
    }
}
