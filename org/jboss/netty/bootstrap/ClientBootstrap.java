// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.bootstrap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineException;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFactory;

public class ClientBootstrap extends Bootstrap
{
    public ClientBootstrap() {
    }
    
    public ClientBootstrap(final ChannelFactory channelFactory) {
        super(channelFactory);
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
            throw new NullPointerException("remoteAddress");
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
    
    public ChannelFuture bind(final SocketAddress localAddress) {
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
        return ch.bind(localAddress);
    }
}
