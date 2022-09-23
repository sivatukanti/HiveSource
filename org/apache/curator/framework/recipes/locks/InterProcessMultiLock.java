// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.Collection;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.apache.curator.framework.CuratorFramework;
import java.util.List;

public class InterProcessMultiLock implements InterProcessLock
{
    private final List<InterProcessLock> locks;
    
    public InterProcessMultiLock(final CuratorFramework client, final List<String> paths) {
        this(makeLocks(client, paths));
    }
    
    public InterProcessMultiLock(final List<InterProcessLock> locks) {
        this.locks = (List<InterProcessLock>)ImmutableList.copyOf((Collection<?>)locks);
    }
    
    private static List<InterProcessLock> makeLocks(final CuratorFramework client, final List<String> paths) {
        final ImmutableList.Builder<InterProcessLock> builder = ImmutableList.builder();
        for (final String path : paths) {
            final InterProcessLock lock = new InterProcessMutex(client, path);
            builder.add(lock);
        }
        return builder.build();
    }
    
    @Override
    public void acquire() throws Exception {
        this.acquire(-1L, null);
    }
    
    @Override
    public boolean acquire(final long time, final TimeUnit unit) throws Exception {
        Exception exception = null;
        final List<InterProcessLock> acquired = (List<InterProcessLock>)Lists.newArrayList();
        boolean success = true;
        for (final InterProcessLock lock : this.locks) {
            try {
                if (unit == null) {
                    lock.acquire();
                    acquired.add(lock);
                }
                else {
                    if (!lock.acquire(time, unit)) {
                        success = false;
                        break;
                    }
                    acquired.add(lock);
                }
            }
            catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                success = false;
                exception = e;
            }
        }
        if (!success) {
            for (final InterProcessLock lock : Lists.reverse(acquired)) {
                try {
                    lock.release();
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
        return success;
    }
    
    @Override
    public synchronized void release() throws Exception {
        Exception baseException = null;
        for (final InterProcessLock lock : Lists.reverse(this.locks)) {
            try {
                lock.release();
            }
            catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                if (baseException == null) {
                    baseException = e;
                }
                else {
                    baseException = new Exception(baseException);
                }
            }
        }
        if (baseException != null) {
            throw baseException;
        }
    }
    
    @Override
    public synchronized boolean isAcquiredInThisProcess() {
        for (final InterProcessLock lock : this.locks) {
            if (!lock.isAcquiredInThisProcess()) {
                return false;
            }
        }
        return true;
    }
}
