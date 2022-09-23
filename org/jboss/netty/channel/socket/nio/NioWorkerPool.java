// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import java.util.concurrent.Executor;
import org.jboss.netty.util.ThreadNameDeterminer;

public class NioWorkerPool extends AbstractNioWorkerPool<NioWorker>
{
    private final ThreadNameDeterminer determiner;
    
    public NioWorkerPool(final Executor workerExecutor, final int workerCount) {
        this(workerExecutor, workerCount, null);
    }
    
    public NioWorkerPool(final Executor workerExecutor, final int workerCount, final ThreadNameDeterminer determiner) {
        super(workerExecutor, workerCount, false);
        this.determiner = determiner;
        this.init();
    }
    
    @Override
    protected NioWorker newWorker(final Executor executor) {
        return new NioWorker(executor, this.determiner);
    }
}
