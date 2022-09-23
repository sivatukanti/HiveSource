// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ServerChannel;
import org.jboss.netty.util.internal.ExecutorUtil;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.ChannelSink;
import java.util.concurrent.Executor;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;

public class OioServerSocketChannelFactory implements ServerSocketChannelFactory
{
    final Executor bossExecutor;
    private final Executor workerExecutor;
    private final ChannelSink sink;
    private boolean shutdownExecutor;
    
    public OioServerSocketChannelFactory() {
        this(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        this.shutdownExecutor = true;
    }
    
    public OioServerSocketChannelFactory(final Executor bossExecutor, final Executor workerExecutor) {
        this(bossExecutor, workerExecutor, null);
    }
    
    public OioServerSocketChannelFactory(final Executor bossExecutor, final Executor workerExecutor, final ThreadNameDeterminer determiner) {
        if (bossExecutor == null) {
            throw new NullPointerException("bossExecutor");
        }
        if (workerExecutor == null) {
            throw new NullPointerException("workerExecutor");
        }
        this.bossExecutor = bossExecutor;
        this.workerExecutor = workerExecutor;
        this.sink = new OioServerSocketPipelineSink(workerExecutor, determiner);
    }
    
    public ServerSocketChannel newChannel(final ChannelPipeline pipeline) {
        return new OioServerSocketChannel(this, pipeline, this.sink);
    }
    
    public void shutdown() {
        if (this.shutdownExecutor) {
            ExecutorUtil.shutdownNow(this.bossExecutor);
            ExecutorUtil.shutdownNow(this.workerExecutor);
        }
    }
    
    public void releaseExternalResources() {
        ExecutorUtil.shutdownNow(this.bossExecutor);
        ExecutorUtil.shutdownNow(this.workerExecutor);
    }
}
