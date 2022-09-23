// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.internal.ExecutorUtil;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.DatagramChannel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import org.jboss.netty.channel.socket.DatagramChannelFactory;

public class OioDatagramChannelFactory implements DatagramChannelFactory
{
    private final Executor workerExecutor;
    final OioDatagramPipelineSink sink;
    private boolean shutdownExecutor;
    
    public OioDatagramChannelFactory() {
        this(Executors.newCachedThreadPool());
        this.shutdownExecutor = true;
    }
    
    public OioDatagramChannelFactory(final Executor workerExecutor) {
        this(workerExecutor, null);
    }
    
    public OioDatagramChannelFactory(final Executor workerExecutor, final ThreadNameDeterminer determiner) {
        if (workerExecutor == null) {
            throw new NullPointerException("workerExecutor");
        }
        this.workerExecutor = workerExecutor;
        this.sink = new OioDatagramPipelineSink(workerExecutor, determiner);
    }
    
    public DatagramChannel newChannel(final ChannelPipeline pipeline) {
        return new OioDatagramChannel(this, pipeline, this.sink);
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
