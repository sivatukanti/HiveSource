// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.channel.socket.Worker;

public final class ShareableWorkerPool<E extends Worker> implements WorkerPool<E>
{
    private final WorkerPool<E> wrapped;
    
    public ShareableWorkerPool(final WorkerPool<E> wrapped) {
        this.wrapped = wrapped;
    }
    
    public E nextWorker() {
        return this.wrapped.nextWorker();
    }
    
    public void rebuildSelectors() {
        this.wrapped.rebuildSelectors();
    }
    
    public void destroy() {
        this.wrapped.shutdown();
        if (this.wrapped instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable)this.wrapped).releaseExternalResources();
        }
    }
    
    public void shutdown() {
    }
}
