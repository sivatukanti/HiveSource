// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.bootstrap;

import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import java.util.Iterator;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import java.util.HashMap;
import java.util.Map;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelException;
import java.net.SocketAddress;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandler;

public class ServerBootstrap extends Bootstrap
{
    private volatile ChannelHandler parentHandler;
    
    public ServerBootstrap() {
    }
    
    public ServerBootstrap(final ChannelFactory channelFactory) {
        super(channelFactory);
    }
    
    @Override
    public void setFactory(final ChannelFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        if (!(factory instanceof ServerChannelFactory)) {
            throw new IllegalArgumentException("factory must be a " + ServerChannelFactory.class.getSimpleName() + ": " + factory.getClass());
        }
        super.setFactory(factory);
    }
    
    public ChannelHandler getParentHandler() {
        return this.parentHandler;
    }
    
    public void setParentHandler(final ChannelHandler parentHandler) {
        this.parentHandler = parentHandler;
    }
    
    public Channel bind() {
        final SocketAddress localAddress = (SocketAddress)this.getOption("localAddress");
        if (localAddress == null) {
            throw new IllegalStateException("localAddress option is not set.");
        }
        return this.bind(localAddress);
    }
    
    public Channel bind(final SocketAddress localAddress) {
        final ChannelFuture future = this.bindAsync(localAddress);
        future.awaitUninterruptibly();
        if (!future.isSuccess()) {
            future.getChannel().close().awaitUninterruptibly();
            throw new ChannelException("Failed to bind to: " + localAddress, future.getCause());
        }
        return future.getChannel();
    }
    
    public ChannelFuture bindAsync() {
        final SocketAddress localAddress = (SocketAddress)this.getOption("localAddress");
        if (localAddress == null) {
            throw new IllegalStateException("localAddress option is not set.");
        }
        return this.bindAsync(localAddress);
    }
    
    public ChannelFuture bindAsync(final SocketAddress localAddress) {
        if (localAddress == null) {
            throw new NullPointerException("localAddress");
        }
        final Binder binder = new Binder(localAddress);
        final ChannelHandler parentHandler = this.getParentHandler();
        final ChannelPipeline bossPipeline = Channels.pipeline();
        bossPipeline.addLast("binder", binder);
        if (parentHandler != null) {
            bossPipeline.addLast("userHandler", parentHandler);
        }
        final Channel channel = this.getFactory().newChannel(bossPipeline);
        final ChannelFuture bfuture = new DefaultChannelFuture(channel, false);
        binder.bindFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    bfuture.setSuccess();
                }
                else {
                    bfuture.getChannel().close();
                    bfuture.setFailure(future.getCause());
                }
            }
        });
        return bfuture;
    }
    
    private final class Binder extends SimpleChannelUpstreamHandler
    {
        private final SocketAddress localAddress;
        private final Map<String, Object> childOptions;
        private final DefaultChannelFuture bindFuture;
        
        Binder(final SocketAddress localAddress) {
            this.childOptions = new HashMap<String, Object>();
            this.bindFuture = new DefaultChannelFuture(null, false);
            this.localAddress = localAddress;
        }
        
        @Override
        public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent evt) {
            try {
                evt.getChannel().getConfig().setPipelineFactory(ServerBootstrap.this.getPipelineFactory());
                final Map<String, Object> allOptions = ServerBootstrap.this.getOptions();
                final Map<String, Object> parentOptions = new HashMap<String, Object>();
                for (final Map.Entry<String, Object> e : allOptions.entrySet()) {
                    if (e.getKey().startsWith("child.")) {
                        this.childOptions.put(e.getKey().substring(6), e.getValue());
                    }
                    else {
                        if ("pipelineFactory".equals(e.getKey())) {
                            continue;
                        }
                        parentOptions.put(e.getKey(), e.getValue());
                    }
                }
                evt.getChannel().getConfig().setOptions(parentOptions);
            }
            finally {
                ctx.sendUpstream(evt);
            }
            evt.getChannel().bind(this.localAddress).addListener(new ChannelFutureListener() {
                public void operationComplete(final ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        Binder.this.bindFuture.setSuccess();
                    }
                    else {
                        Binder.this.bindFuture.setFailure(future.getCause());
                    }
                }
            });
        }
        
        @Override
        public void childChannelOpen(final ChannelHandlerContext ctx, final ChildChannelStateEvent e) throws Exception {
            try {
                e.getChildChannel().getConfig().setOptions(this.childOptions);
            }
            catch (Throwable t) {
                Channels.fireExceptionCaught(e.getChildChannel(), t);
            }
            ctx.sendUpstream(e);
        }
        
        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
            this.bindFuture.setFailure(e.getCause());
            ctx.sendUpstream(e);
        }
    }
}
