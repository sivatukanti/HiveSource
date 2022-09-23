// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.utils.PathUtils;
import java.util.concurrent.BlockingQueue;

public class QueueSafety<T>
{
    private final String lockPath;
    private final QueueConsumer<T> consumer;
    private final BlockingQueue<T> queue;
    
    public QueueSafety(final String lockPath, final QueueConsumer<T> consumer) {
        this.lockPath = PathUtils.validatePath(lockPath);
        this.consumer = consumer;
        this.queue = null;
    }
    
    QueueSafety(final String lockPath, final BlockingQueue<T> queue) {
        this.lockPath = PathUtils.validatePath(lockPath);
        this.consumer = null;
        this.queue = queue;
    }
    
    String getLockPath() {
        return this.lockPath;
    }
    
    QueueConsumer<T> getConsumer() {
        return this.consumer;
    }
    
    BlockingQueue<T> getQueue() {
        return this.queue;
    }
}
