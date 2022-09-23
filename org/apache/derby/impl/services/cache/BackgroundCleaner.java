// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.cache;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.derby.iapi.services.daemon.DaemonService;
import org.apache.derby.iapi.services.daemon.Serviceable;

final class BackgroundCleaner implements Serviceable
{
    private final DaemonService daemonService;
    private final int clientNumber;
    private final AtomicBoolean scheduled;
    private final ArrayBlockingQueue<CacheEntry> queue;
    private volatile boolean shrink;
    private final ConcurrentCache cacheManager;
    
    BackgroundCleaner(final ConcurrentCache cacheManager, final DaemonService daemonService, final int capacity) {
        this.scheduled = new AtomicBoolean();
        this.queue = new ArrayBlockingQueue<CacheEntry>(capacity);
        this.daemonService = daemonService;
        this.cacheManager = cacheManager;
        this.clientNumber = daemonService.subscribe(this, true);
    }
    
    boolean scheduleClean(final CacheEntry e) {
        final boolean offer = this.queue.offer(e);
        if (offer) {
            this.requestService();
        }
        return offer;
    }
    
    void scheduleShrink() {
        this.shrink = true;
        this.requestService();
    }
    
    private void requestService() {
        if (this.scheduled.compareAndSet(false, true)) {
            this.daemonService.serviceNow(this.clientNumber);
        }
    }
    
    void unsubscribe() {
        this.daemonService.unsubscribe(this.clientNumber);
    }
    
    public int performWork(final ContextManager contextManager) throws StandardException {
        this.scheduled.set(false);
        if (this.shrink) {
            this.shrink = false;
            this.cacheManager.getReplacementPolicy().doShrink();
        }
        final CacheEntry cacheEntry = this.queue.poll();
        if (cacheEntry != null) {
            try {
                this.cacheManager.cleanEntry(cacheEntry);
            }
            finally {
                if (!this.queue.isEmpty() || this.shrink) {
                    this.requestService();
                }
            }
        }
        return 1;
    }
    
    public boolean serviceASAP() {
        return true;
    }
    
    public boolean serviceImmediately() {
        return false;
    }
}
