// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ServerChannel;
import java.util.concurrent.ThreadPoolExecutor;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;

public class NioServerSocketChannelFactory implements ServerSocketChannelFactory
{
    private final WorkerPool<NioWorker> workerPool;
    private final NioServerSocketPipelineSink sink;
    private final BossPool<NioServerBoss> bossPool;
    private boolean releasePools;
    
    public NioServerSocketChannelFactory() {
        this(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        this.releasePools = true;
    }
    
    public NioServerSocketChannelFactory(final Executor bossExecutor, final Executor workerExecutor) {
        this(bossExecutor, workerExecutor, getMaxThreads(workerExecutor));
    }
    
    public NioServerSocketChannelFactory(final Executor bossExecutor, final Executor workerExecutor, final int workerCount) {
        this(bossExecutor, 1, workerExecutor, workerCount);
    }
    
    public NioServerSocketChannelFactory(final Executor bossExecutor, final int bossCount, final Executor workerExecutor, final int workerCount) {
        this(bossExecutor, bossCount, new NioWorkerPool(workerExecutor, workerCount));
    }
    
    public NioServerSocketChannelFactory(final Executor bossExecutor, final WorkerPool<NioWorker> workerPool) {
        this(bossExecutor, 1, workerPool);
    }
    
    public NioServerSocketChannelFactory(final Executor bossExecutor, final int bossCount, final WorkerPool<NioWorker> workerPool) {
        this(new NioServerBossPool(bossExecutor, bossCount, null), workerPool);
    }
    
    public NioServerSocketChannelFactory(final BossPool<NioServerBoss> bossPool, final WorkerPool<NioWorker> workerPool) {
        if (bossPool == null) {
            throw new NullPointerException("bossExecutor");
        }
        if (workerPool == null) {
            throw new NullPointerException("workerPool");
        }
        this.bossPool = bossPool;
        this.workerPool = workerPool;
        this.sink = new NioServerSocketPipelineSink();
    }
    
    public ServerSocketChannel newChannel(final ChannelPipeline pipeline) {
        return new NioServerSocketChannel(this, pipeline, this.sink, this.bossPool.nextBoss(), this.workerPool);
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
    
    private static int getMaxThreads(final Executor executor) {
        if (executor instanceof ThreadPoolExecutor) {
            final int maxThreads = ((ThreadPoolExecutor)executor).getMaximumPoolSize();
            return Math.min(maxThreads, SelectorUtil.DEFAULT_IO_THREADS);
        }
        return SelectorUtil.DEFAULT_IO_THREADS;
    }
}
