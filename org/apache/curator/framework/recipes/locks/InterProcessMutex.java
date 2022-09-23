// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import java.util.concurrent.Executor;
import org.apache.curator.shaded.com.google.common.util.concurrent.MoreExecutors;
import java.util.Collection;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import java.util.concurrent.ConcurrentMap;

public class InterProcessMutex implements InterProcessLock, Revocable<InterProcessMutex>
{
    private final LockInternals internals;
    private final String basePath;
    private final ConcurrentMap<Thread, LockData> threadData;
    private static final String LOCK_NAME = "lock-";
    
    public InterProcessMutex(final CuratorFramework client, final String path) {
        this(client, path, new StandardLockInternalsDriver());
    }
    
    public InterProcessMutex(final CuratorFramework client, final String path, final LockInternalsDriver driver) {
        this(client, path, "lock-", 1, driver);
    }
    
    @Override
    public void acquire() throws Exception {
        if (!this.internalLock(-1L, null)) {
            throw new IOException("Lost connection while trying to acquire lock: " + this.basePath);
        }
    }
    
    @Override
    public boolean acquire(final long time, final TimeUnit unit) throws Exception {
        return this.internalLock(time, unit);
    }
    
    @Override
    public boolean isAcquiredInThisProcess() {
        return this.threadData.size() > 0;
    }
    
    @Override
    public void release() throws Exception {
        final Thread currentThread = Thread.currentThread();
        final LockData lockData = this.threadData.get(currentThread);
        if (lockData == null) {
            throw new IllegalMonitorStateException("You do not own the lock: " + this.basePath);
        }
        final int newLockCount = lockData.lockCount.decrementAndGet();
        if (newLockCount > 0) {
            return;
        }
        if (newLockCount < 0) {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + this.basePath);
        }
        try {
            this.internals.releaseLock(lockData.lockPath);
        }
        finally {
            this.threadData.remove(currentThread);
        }
    }
    
    public Collection<String> getParticipantNodes() throws Exception {
        return LockInternals.getParticipantNodes(this.internals.getClient(), this.basePath, this.internals.getLockName(), this.internals.getDriver());
    }
    
    @Override
    public void makeRevocable(final RevocationListener<InterProcessMutex> listener) {
        this.makeRevocable(listener, MoreExecutors.sameThreadExecutor());
    }
    
    @Override
    public void makeRevocable(final RevocationListener<InterProcessMutex> listener, final Executor executor) {
        this.internals.makeRevocable(new RevocationSpec(executor, new Runnable() {
            @Override
            public void run() {
                listener.revocationRequested(InterProcessMutex.this);
            }
        }));
    }
    
    InterProcessMutex(final CuratorFramework client, final String path, final String lockName, final int maxLeases, final LockInternalsDriver driver) {
        this.threadData = Maps.newConcurrentMap();
        this.basePath = PathUtils.validatePath(path);
        this.internals = new LockInternals(client, driver, path, lockName, maxLeases);
    }
    
    boolean isOwnedByCurrentThread() {
        final LockData lockData = this.threadData.get(Thread.currentThread());
        return lockData != null && lockData.lockCount.get() > 0;
    }
    
    protected byte[] getLockNodeBytes() {
        return null;
    }
    
    protected String getLockPath() {
        final LockData lockData = this.threadData.get(Thread.currentThread());
        return (lockData != null) ? lockData.lockPath : null;
    }
    
    private boolean internalLock(final long time, final TimeUnit unit) throws Exception {
        final Thread currentThread = Thread.currentThread();
        final LockData lockData = this.threadData.get(currentThread);
        if (lockData != null) {
            lockData.lockCount.incrementAndGet();
            return true;
        }
        final String lockPath = this.internals.attemptLock(time, unit, this.getLockNodeBytes());
        if (lockPath != null) {
            final LockData newLockData = new LockData(currentThread, lockPath);
            this.threadData.put(currentThread, newLockData);
            return true;
        }
        return false;
    }
    
    private static class LockData
    {
        final Thread owningThread;
        final String lockPath;
        final AtomicInteger lockCount;
        
        private LockData(final Thread owningThread, final String lockPath) {
            this.lockCount = new AtomicInteger(1);
            this.owningThread = owningThread;
            this.lockPath = lockPath;
        }
    }
}
