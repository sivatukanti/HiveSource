// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.ChannelPipeline;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.socket.InternetProtocolFamily;
import org.jboss.netty.channel.socket.DatagramChannelFactory;

public class NioDatagramChannelFactory implements DatagramChannelFactory
{
    private final NioDatagramPipelineSink sink;
    private final WorkerPool<NioDatagramWorker> workerPool;
    private final InternetProtocolFamily family;
    private boolean releasePool;
    
    public NioDatagramChannelFactory() {
        this((InternetProtocolFamily)null);
    }
    
    public NioDatagramChannelFactory(final InternetProtocolFamily family) {
        this.workerPool = new NioDatagramWorkerPool(Executors.newCachedThreadPool(), SelectorUtil.DEFAULT_IO_THREADS);
        this.family = family;
        this.sink = new NioDatagramPipelineSink(this.workerPool);
        this.releasePool = true;
    }
    
    public NioDatagramChannelFactory(final Executor workerExecutor) {
        this(workerExecutor, SelectorUtil.DEFAULT_IO_THREADS);
    }
    
    public NioDatagramChannelFactory(final Executor workerExecutor, final int workerCount) {
        this(new NioDatagramWorkerPool(workerExecutor, workerCount));
    }
    
    public NioDatagramChannelFactory(final WorkerPool<NioDatagramWorker> workerPool) {
        this(workerPool, null);
    }
    
    public NioDatagramChannelFactory(final Executor workerExecutor, final InternetProtocolFamily family) {
        this(workerExecutor, SelectorUtil.DEFAULT_IO_THREADS, family);
    }
    
    public NioDatagramChannelFactory(final Executor workerExecutor, final int workerCount, final InternetProtocolFamily family) {
        this(new NioDatagramWorkerPool(workerExecutor, workerCount), family);
    }
    
    public NioDatagramChannelFactory(final WorkerPool<NioDatagramWorker> workerPool, final InternetProtocolFamily family) {
        this.workerPool = workerPool;
        this.family = family;
        this.sink = new NioDatagramPipelineSink(workerPool);
    }
    
    public DatagramChannel newChannel(final ChannelPipeline pipeline) {
        return new NioDatagramChannel(this, pipeline, this.sink, this.sink.nextWorker(), this.family);
    }
    
    public void shutdown() {
        this.workerPool.shutdown();
        if (this.releasePool) {
            this.releasePool();
        }
    }
    
    public void releaseExternalResources() {
        this.workerPool.shutdown();
        this.releasePool();
    }
    
    private void releasePool() {
        if (this.workerPool instanceof ExternalResourceReleasable) {
            ((ExternalResourceReleasable)this.workerPool).releaseExternalResources();
        }
    }
}
