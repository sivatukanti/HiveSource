// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.server;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.hive.metastore.RawStore;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

public class ThreadFactoryWithGarbageCleanup implements ThreadFactory
{
    private static Map<Long, RawStore> threadRawStoreMap;
    private final String namePrefix;
    
    public ThreadFactoryWithGarbageCleanup(final String threadPoolName) {
        this.namePrefix = threadPoolName;
    }
    
    @Override
    public Thread newThread(final Runnable runnable) {
        final Thread newThread = new ThreadWithGarbageCleanup(runnable);
        newThread.setName(this.namePrefix + ": Thread-" + newThread.getId());
        return newThread;
    }
    
    public static Map<Long, RawStore> getThreadRawStoreMap() {
        return ThreadFactoryWithGarbageCleanup.threadRawStoreMap;
    }
    
    static {
        ThreadFactoryWithGarbageCleanup.threadRawStoreMap = new ConcurrentHashMap<Long, RawStore>();
    }
}
