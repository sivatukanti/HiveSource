// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;

public class InterProcessSemaphoreMutex implements InterProcessLock
{
    private final InterProcessSemaphoreV2 semaphore;
    private volatile Lease lease;
    
    public InterProcessSemaphoreMutex(final CuratorFramework client, final String path) {
        this.semaphore = new InterProcessSemaphoreV2(client, path, 1);
    }
    
    @Override
    public void acquire() throws Exception {
        this.lease = this.semaphore.acquire();
    }
    
    @Override
    public boolean acquire(final long time, final TimeUnit unit) throws Exception {
        final Lease acquiredLease = this.semaphore.acquire(time, unit);
        if (acquiredLease == null) {
            return false;
        }
        this.lease = acquiredLease;
        return true;
    }
    
    @Override
    public void release() throws Exception {
        final Lease lease = this.lease;
        Preconditions.checkState(lease != null, (Object)"Not acquired");
        this.lease = null;
        lease.close();
    }
    
    @Override
    public boolean isAcquiredInThisProcess() {
        return this.lease != null;
    }
}
