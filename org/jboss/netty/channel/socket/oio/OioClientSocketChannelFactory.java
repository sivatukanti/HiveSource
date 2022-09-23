// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.internal.ExecutorUtil;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.SocketChannel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

public class OioClientSocketChannelFactory implements ClientSocketChannelFactory
{
    private final Executor workerExecutor;
    final OioClientSocketPipelineSink sink;
    private boolean shutdownExecutor;
    
    public OioClientSocketChannelFactory() {
        this(Executors.newCachedThreadPool());
        this.shutdownExecutor = true;
    }
    
    public OioClientSocketChannelFactory(final Executor workerExecutor) {
        this(workerExecutor, null);
    }
    
    public OioClientSocketChannelFactory(final Executor workerExecutor, final ThreadNameDeterminer determiner) {
        if (workerExecutor == null) {
            throw new NullPointerException("workerExecutor");
        }
        this.workerExecutor = workerExecutor;
        this.sink = new OioClientSocketPipelineSink(workerExecutor, determiner);
    }
    
    public SocketChannel newChannel(final ChannelPipeline pipeline) {
        return new OioClientSocketChannel(this, pipeline, this.sink);
    }
    
    public void shutdown() {
        if (this.shutdownExecutor) {
            ExecutorUtil.shutdownNow(this.workerExecutor);
        }
    }
    
    public void releaseExternalResources() {
        ExecutorUtil.shutdownNow(this.workerExecutor);
    }
}
