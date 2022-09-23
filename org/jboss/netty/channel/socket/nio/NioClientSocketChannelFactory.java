// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

public class NioClientSocketChannelFactory implements ClientSocketChannelFactory
{
    private static final int DEFAULT_BOSS_COUNT = 1;
    private final BossPool<NioClientBoss> bossPool;
    private final WorkerPool<NioWorker> workerPool;
    private final NioClientSocketPipelineSink sink;
    private boolean releasePools;
    
    public NioClientSocketChannelFactory() {
        this(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        this.releasePools = true;
    }
    
    public NioClientSocketChannelFactory(final Executor bossExecutor, final Executor workerExecutor) {
        this(bossExecutor, workerExecutor, 1, SelectorUtil.DEFAULT_IO_THREADS);
    }
    
    public NioClientSocketChannelFactory(final Executor bossExecutor, final Executor workerExecutor, final int workerCount) {
        this(bossExecutor, workerExecutor, 1, workerCount);
    }
    
    public NioClientSocketChannelFactory(final Executor bossExecutor, final Executor workerExecutor, final int bossCount, final int workerCount) {
        this(bossExecutor, bossCount, new NioWorkerPool(workerExecutor, workerCount));
    }
    
    public NioClientSocketChannelFactory(final Executor bossExecutor, final int bossCount, final WorkerPool<NioWorker> workerPool) {
        this(new NioClientBossPool(bossExecutor, bossCount), workerPool);
    }
    
    public NioClientSocketChannelFactory(final Executor bossExecutor, final int bossCount, final WorkerPool<NioWorker> workerPool, final Timer timer) {
        this(new NioClientBossPool(bossExecutor, bossCount, timer, null), workerPool);
    }
    
    public NioClientSocketChannelFactory(final BossPool<NioClientBoss> bossPool, final WorkerPool<NioWorker> workerPool) {
        if (bossPool == null) {
            throw new NullPointerException("bossPool");
        }
        if (workerPool == null) {
            throw new NullPointerException("workerPool");
        }
        this.bossPool = bossPool;
        this.workerPool = workerPool;
        this.sink = new NioClientSocketPipelineSink(bossPool);
    }
    
    public SocketChannel newChannel(final ChannelPipeline pipeline) {
        return new NioClientSocketChannel(this, pipeline, this.sink, this.workerPool.nextWorker());
    }
    
    public void shutdown() {
        this.bossPool.shutdown();
        this.workerPool.shutdown();
        if (this.releasePools) {
            this.releasePools();
        }
    }
    
    public void releaseExternalResources() {
        this.bossPool.shutdown();
        this.workerPool.shutdown();
        this.releasePools();
    }
    
    private void releasePools() {
        if (this.bossPool instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable)this.bossPool).releaseExternalResources();
        }
        if (this.workerPool instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable)this.workerPool).releaseExternalResources();
        }
    }
}
