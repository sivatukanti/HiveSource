// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.bootstrap;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.ChannelPipelineException;
import java.net.SocketAddress;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;

public class ConnectionlessBootstrap extends Bootstrap
{
    public ConnectionlessBootstrap() {
    }
    
    public ConnectionlessBootstrap(final ChannelFactory channelFactory) {
        super(channelFactory);
    }
    
    public Channel bind() {
        final SocketAddress localAddress = (SocketAddress)this.getOption("localAddress");
        if (localAddress == null) {
            throw new IllegalStateException("localAddress option is not set.");
        }
        return this.bind(localAddress);
    }
    
    public Channel bind(final SocketAddress localAddress) {
        if (localAddress == null) {
            throw new NullPointerException("localAddress");
        }
        ChannelPipeline pipeline;
        try {
            pipeline = this.getPipelineFactory().getPipeline();
        }
        catch (Exception e) {
            throw new ChannelPipelineException("Failed to initialize a pipeline.", e);
        }
        final Channel ch = this.getFactory().newChannel(pipeline);
        boolean success = false;
        try {
            ch.getConfig().setOptions(this.getOptions());
            success = true;
        }
        finally {
            if (!success) {
                ch.close();
            }
        }
        final ChannelFuture future = ch.bind(localAddress);
        future.awaitUninterruptibly();
        if (!future.isSuccess()) {
            future.getChannel().close().awaitUninterruptibly();
            throw new ChannelException("Failed to bind to: " + localAddress, future.getCause());
        }
        return ch;
    }
    
    public ChannelFuture connect() {
        final SocketAddress remoteAddress = (SocketAddress)this.getOption("remoteAddress");
        if (remoteAddress == null) {
            throw new IllegalStateException("remoteAddress option is not set.");
        }
        return this.connect(remoteAddress);
    }
    
    public ChannelFuture connect(final SocketAddress remoteAddress) {
        if (remoteAddress == null) {
            throw new NullPointerException("remotedAddress");
        }
        final SocketAddress localAddress = (SocketAddress)this.getOption("localAddress");
        return this.connect(remoteAddress, localAddress);
    }
    
    public ChannelFuture connect(final SocketAddress remoteAddress, final SocketAddress localAddress) {
        if (remoteAddress == null) {
            throw new NullPointerException("remoteAddress");
        }
        ChannelPipeline pipeline;
        try {
            pipeline = this.getPipelineFactory().getPipeline();
        }
        catch (Exception e) {
            throw new ChannelPipelineException("Failed to initialize a pipeline.", e);
        }
        final Channel ch = this.getFactory().newChannel(pipeline);
        boolean success = false;
        try {
            ch.getConfig().setOptions(this.getOptions());
            success = true;
        }
        finally {
            if (!success) {
                ch.close();
            }
        }
        if (localAddress != null) {
            ch.bind(localAddress);
        }
        return ch.connect(remoteAddress);
    }
}
