// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.server;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.RawStore;
import java.util.Map;
import org.apache.commons.logging.Log;

public class ThreadWithGarbageCleanup extends Thread
{
    private static final Log LOG;
    Map<Long, RawStore> threadRawStoreMap;
    
    public ThreadWithGarbageCleanup(final Runnable runnable) {
        super(runnable);
        this.threadRawStoreMap = ThreadFactoryWithGarbageCleanup.getThreadRawStoreMap();
    }
    
    public void finalize() throws Throwable {
        this.cleanRawStore();
        super.finalize();
    }
    
    private void cleanRawStore() {
        final Long threadId = this.getId();
        final RawStore threadLocalRawStore = this.threadRawStoreMap.get(threadId);
        if (threadLocalRawStore != null) {
            ThreadWithGarbageCleanup.LOG.debug("RawStore: " + threadLocalRawStore + ", for the thread: " + this.getName() + " will be closed now.");
            threadLocalRawStore.shutdown();
            this.threadRawStoreMap.remove(threadId);
        }
    }
    
    public void cacheThreadLocalRawStore() {
        final Long threadId = this.getId();
        final RawStore threadLocalRawStore = HiveMetaStore.HMSHandler.getRawStore();
        if (threadLocalRawStore != null && !this.threadRawStoreMap.containsKey(threadId)) {
            ThreadWithGarbageCleanup.LOG.debug("Adding RawStore: " + threadLocalRawStore + ", for the thread: " + this.getName() + " to threadRawStoreMap for future cleanup.");
            this.threadRawStoreMap.put(threadId, threadLocalRawStore);
        }
    }
    
    static {
        LOG = LogFactory.getLog(ThreadWithGarbageCleanup.class);
    }
}
