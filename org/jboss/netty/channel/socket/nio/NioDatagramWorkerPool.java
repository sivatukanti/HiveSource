// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.Executor;

public class NioDatagramWorkerPool extends AbstractNioWorkerPool<NioDatagramWorker>
{
    public NioDatagramWorkerPool(final Executor executor, final int workerCount) {
        super(executor, workerCount);
    }
    
    @Override
    protected NioDatagramWorker newWorker(final Executor executor) {
        return new NioDatagramWorker(executor);
    }
}
