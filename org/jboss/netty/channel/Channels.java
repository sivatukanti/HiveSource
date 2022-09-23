// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import org.jboss.netty.util.internal.ConversionUtil;

public final class Channels
{
    public static ChannelPipeline pipeline() {
        return new DefaultChannelPipeline();
    }
    
    public static ChannelPipeline pipeline(final ChannelHandler... handlers) {
        if (handlers == null) {
            throw new NullPointerException("handlers");
        }
        final ChannelPipeline newPipeline = pipeline();
        for (int i = 0; i < handlers.length; ++i) {
            final ChannelHandler h = handlers[i];
            if (h == null) {
                break;
            }
            newPipeline.addLast(ConversionUtil.toString(i), h);
        }
        return newPipeline;
    }
    
    public static ChannelPipeline pipeline(final ChannelPipeline pipeline) {
        final ChannelPipeline newPipeline = pipeline();
        for (final Map.Entry<String, ChannelHandler> e : pipeline.toMap().entrySet()) {
            newPipeline.addLast(e.getKey(), e.getValue());
        }
        return newPipeline;
    }
    
    public static ChannelPipelineFactory pipelineFactory(final ChannelPipeline pipeline) {
        return new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(pipeline);
            }
        };
    }
    
    public static ChannelFuture future(final Channel channel) {
        return future(channel, false);
    }
    
    public static ChannelFuture future(final Channel channel, final boolean cancellable) {
        return new DefaultChannelFuture(channel, cancellable);
    }
    
    public static ChannelFuture succeededFuture(final Channel channel) {
        if (channel instanceof AbstractChannel) {
            return ((AbstractChannel)channel).getSucceededFuture();
        }
        return new SucceededChannelFuture(channel);
    }
    
    public static ChannelFuture failedFuture(final Channel channel, final Throwable cause) {
        return new FailedChannelFuture(channel, cause);
    }
    
    public static void fireChannelOpen(final Channel channel) {
        if (channel.getParent() != null) {
            fireChildChannelStateChanged(channel.getParent(), channel);
        }
        channel.getPipeline().sendUpstream(new UpstreamChannelStateEvent(channel, ChannelState.OPEN, Boolean.TRUE));
    }
    
    public static void fireChannelOpen(final ChannelHandlerContext ctx) {
        ctx.sendUpstream(new UpstreamChannelStateEvent(ctx.getChannel(), ChannelState.OPEN, Boolean.TRUE));
    }
    
    public static void fireChannelBound(final Channel channel, final SocketAddress localAddress) {
        channel.getPipeline().sendUpstream(new UpstreamChannelStateEvent(channel, ChannelState.BOUND, localAddress));
    }
    
    public static void fireChannelBound(final ChannelHandlerContext ctx, final SocketAddress localAddress) {
        ctx.sendUpstream(new UpstreamChannelStateEvent(ctx.getChannel(), ChannelState.BOUND, localAddress));
    }
    
    public static void fireChannelConnected(final Channel channel, final SocketAddress remoteAddress) {
        channel.getPipeline().sendUpstream(new UpstreamChannelStateEvent(channel, ChannelState.CONNECTED, remoteAddress));
    }
    
    public static void fireChannelConnected(final ChannelHandlerContext ctx, final SocketAddress remoteAddress) {
        ctx.sendUpstream(new UpstreamChannelStateEvent(ctx.getChannel(), ChannelState.CONNECTED, remoteAddress));
    }
    
    public static void fireMessageReceived(final Channel channel, final Object message) {
        fireMessageReceived(channel, message, null);
    }
    
    public static void fireMessageReceived(final Channel channel, final Object message, final SocketAddress remoteAddress) {
        channel.getPipeline().sendUpstream(new UpstreamMessageEvent(channel, message, remoteAddress));
    }
    
    public static void fireMessageReceived(final ChannelHandlerContext ctx, final Object message) {
        ctx.sendUpstream(new UpstreamMessageEvent(ctx.getChannel(), message, null));
    }
    
    public static void fireMessageReceived(final ChannelHandlerContext ctx, final Object message, final SocketAddress remoteAddress) {
        ctx.sendUpstream(new UpstreamMessageEvent(ctx.getChannel(), message, remoteAddress));
    }
    
    public static ChannelFuture fireWriteCompleteLater(final Channel channel, final long amount) {
        return channel.getPipeline().execute(new Runnable() {
            public void run() {
                Channels.fireWriteComplete(channel, amount);
            }
        });
    }
    
    public static void fireWriteComplete(final Channel channel, final long amount) {
        if (amount == 0L) {
            return;
        }
        channel.getPipeline().sendUpstream(new DefaultWriteCompletionEvent(channel, amount));
    }
    
    public static void fireWriteComplete(final ChannelHandlerContext ctx, final long amount) {
        ctx.sendUpstream(new DefaultWriteCompletionEvent(ctx.getChannel(), amount));
    }
    
    public static ChannelFuture fireChannelInterestChangedLater(final Channel channel) {
        return channel.getPipeline().execute(new Runnable() {
            public void run() {
                Channels.fireChannelInterestChanged(channel);
            }
        });
    }
    
    public static void fireChannelInterestChanged(final Channel channel) {
        channel.getPipeline().sendUpstream(new UpstreamChannelStateEvent(channel, ChannelState.INTEREST_OPS, 1));
    }
    
    public static void fireChannelInterestChanged(final ChannelHandlerContext ctx) {
        ctx.sendUpstream(new UpstreamChannelStateEvent(ctx.getChannel(), ChannelState.INTEREST_OPS, 1));
    }
    
    public static ChannelFuture fireChannelDisconnectedLater(final Channel channel) {
        return channel.getPipeline().execute(new Runnable() {
            public void run() {
                Channels.fireChannelDisconnected(channel);
            }
        });
    }
    
    public static void fireChannelDisconnected(final Channel channel) {
        channel.getPipeline().sendUpstream(new UpstreamChannelStateEvent(channel, ChannelState.CONNECTED, null));
    }
    
    public static void fireChannelDisconnected(final ChannelHandlerContext ctx) {
        ctx.sendUpstream(new UpstreamChannelStateEvent(ctx.getChannel(), ChannelState.CONNECTED, null));
    }
    
    public static ChannelFuture fireChannelUnboundLater(final Channel channel) {
        return channel.getPipeline().execute(new Runnable() {
            public void run() {
                Channels.fireChannelUnbound(channel);
            }
        });
    }
    
    public static void fireChannelUnbound(final Channel channel) {
        channel.getPipeline().sendUpstream(new UpstreamChannelStateEvent(channel, ChannelState.BOUND, null));
    }
    
    public static void fireChannelUnbound(final ChannelHandlerContext ctx) {
        ctx.sendUpstream(new UpstreamChannelStateEvent(ctx.getChannel(), ChannelState.BOUND, null));
    }
    
    public static ChannelFuture fireChannelClosedLater(final Channel channel) {
        return channel.getPipeline().execute(new Runnable() {
            public void run() {
                Channels.fireChannelClosed(channel);
            }
        });
    }
    
    public static void fireChannelClosed(final Channel channel) {
        channel.getPipeline().sendUpstream(new UpstreamChannelStateEvent(channel, ChannelState.OPEN, Boolean.FALSE));
        if (channel.getParent() != null) {
            fireChildChannelStateChanged(channel.getParent(), channel);
        }
    }
    
    public static void fireChannelClosed(final ChannelHandlerContext ctx) {
        ctx.sendUpstream(new UpstreamChannelStateEvent(ctx.getChannel(), ChannelState.OPEN, Boolean.FALSE));
    }
    
    public static ChannelFuture fireExceptionCaughtLater(final Channel channel, final Throwable cause) {
        return channel.getPipeline().execute(new Runnable() {
            public void run() {
                Channels.fireExceptionCaught(channel, cause);
            }
        });
    }
    
    public static ChannelFuture fireExceptionCaughtLater(final ChannelHandlerContext ctx, final Throwable cause) {
        return ctx.getPipeline().execute(new Runnable() {
            public void run() {
                Channels.fireExceptionCaught(ctx, cause);
            }
        });
    }
    
    public static void fireExceptionCaught(final Channel channel, final Throwable cause) {
        channel.getPipeline().sendUpstream(new DefaultExceptionEvent(channel, cause));
    }
    
    public static void fireExceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        ctx.sendUpstream(new DefaultExceptionEvent(ctx.getChannel(), cause));
    }
    
    private static void fireChildChannelStateChanged(final Channel channel, final Channel childChannel) {
        channel.getPipeline().sendUpstream(new DefaultChildChannelStateEvent(channel, childChannel));
    }
    
    public static ChannelFuture bind(final Channel channel, final SocketAddress localAddress) {
        if (localAddress == null) {
            throw new NullPointerException("localAddress");
        }
        final ChannelFuture future = future(channel);
        channel.getPipeline().sendDownstream(new DownstreamChannelStateEvent(channel, future, ChannelState.BOUND, localAddress));
        return future;
    }
    
    public static void bind(final ChannelHandlerContext ctx, final ChannelFuture future, final SocketAddress localAddress) {
        if (localAddress == null) {
            throw new NullPointerException("localAddress");
        }
        ctx.sendDownstream(new DownstreamChannelStateEvent(ctx.getChannel(), future, ChannelState.BOUND, localAddress));
    }
    
    public static void unbind(final ChannelHandlerContext ctx, final ChannelFuture future) {
        ctx.sendDownstream(new DownstreamChannelStateEvent(ctx.getChannel(), future, ChannelState.BOUND, null));
    }
    
    public static ChannelFuture unbind(final Channel channel) {
        final ChannelFuture future = future(channel);
        channel.getPipeline().sendDownstream(new DownstreamChannelStateEvent(channel, future, ChannelState.BOUND, null));
        return future;
    }
    
    public static ChannelFuture connect(final Channel channel, final SocketAddress remoteAddress) {
        if (remoteAddress == null) {
            throw new NullPointerException("remoteAddress");
        }
        final ChannelFuture future = future(channel, true);
        channel.getPipeline().sendDownstream(new DownstreamChannelStateEvent(channel, future, ChannelState.CONNECTED, remoteAddress));
        return future;
    }
    
    public static void connect(final ChannelHandlerContext ctx, final ChannelFuture future, final SocketAddress remoteAddress) {
        if (remoteAddress == null) {
            throw new NullPointerException("remoteAddress");
        }
        ctx.sendDownstream(new DownstreamChannelStateEvent(ctx.getChannel(), future, ChannelState.CONNECTED, remoteAddress));
    }
    
    public static ChannelFuture write(final Channel channel, final Object message) {
        return write(channel, message, null);
    }
    
    public static void write(final ChannelHandlerContext ctx, final ChannelFuture future, final Object message) {
        write(ctx, future, message, null);
    }
    
    public static ChannelFuture write(final Channel channel, final Object message, final SocketAddress remoteAddress) {
        final ChannelFuture future = future(channel);
        channel.getPipeline().sendDownstream(new DownstreamMessageEvent(channel, future, message, remoteAddress));
        return future;
    }
    
    public static void write(final ChannelHandlerContext ctx, final ChannelFuture future, final Object message, final SocketAddress remoteAddress) {
        ctx.sendDownstream(new DownstreamMessageEvent(ctx.getChannel(), future, message, remoteAddress));
    }
    
    public static ChannelFuture setInterestOps(final Channel channel, int interestOps) {
        validateInterestOps(interestOps);
        interestOps = filterDownstreamInterestOps(interestOps);
        final ChannelFuture future = future(channel);
        channel.getPipeline().sendDownstream(new DownstreamChannelStateEvent(channel, future, ChannelState.INTEREST_OPS, interestOps));
        return future;
    }
    
    public static void setInterestOps(final ChannelHandlerContext ctx, final ChannelFuture future, int interestOps) {
        validateInterestOps(interestOps);
        interestOps = filterDownstreamInterestOps(interestOps);
        ctx.sendDownstream(new DownstreamChannelStateEvent(ctx.getChannel(), future, ChannelState.INTEREST_OPS, interestOps));
    }
    
    public static ChannelFuture disconnect(final Channel channel) {
        final ChannelFuture future = future(channel);
        channel.getPipeline().sendDownstream(new DownstreamChannelStateEvent(channel, future, ChannelState.CONNECTED, null));
        return future;
    }
    
    public static void disconnect(final ChannelHandlerContext ctx, final ChannelFuture future) {
        ctx.sendDownstream(new DownstreamChannelStateEvent(ctx.getChannel(), future, ChannelState.CONNECTED, null));
    }
    
    public static ChannelFuture close(final Channel channel) {
        final ChannelFuture future = channel.getCloseFuture();
        channel.getPipeline().sendDownstream(new DownstreamChannelStateEvent(channel, future, ChannelState.OPEN, Boolean.FALSE));
        return future;
    }
    
    public static void close(final ChannelHandlerContext ctx, final ChannelFuture future) {
        ctx.sendDownstream(new DownstreamChannelStateEvent(ctx.getChannel(), future, ChannelState.OPEN, Boolean.FALSE));
    }
    
    private static void validateInterestOps(final int interestOps) {
        switch (interestOps) {
            case 0:
            case 1:
            case 4:
            case 5: {}
            default: {
                throw new IllegalArgumentException("Invalid interestOps: " + interestOps);
            }
        }
    }
    
    private static int filterDownstreamInterestOps(final int interestOps) {
        return interestOps & 0xFFFFFFFB;
    }
    
    private Channels() {
    }
}
