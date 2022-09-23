// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.embedder;

import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineException;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.ChannelSink;
import java.util.LinkedList;
import org.jboss.netty.channel.ChannelHandler;
import java.util.Queue;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channel;

abstract class AbstractCodecEmbedder<E> implements CodecEmbedder<E>
{
    private final Channel channel;
    private final ChannelPipeline pipeline;
    private final EmbeddedChannelSink sink;
    final Queue<Object> productQueue;
    
    protected AbstractCodecEmbedder(final ChannelHandler... handlers) {
        this.sink = new EmbeddedChannelSink();
        this.productQueue = new LinkedList<Object>();
        this.pipeline = new EmbeddedChannelPipeline();
        this.configurePipeline(handlers);
        this.channel = new EmbeddedChannel(this.pipeline, this.sink);
        this.fireInitialEvents();
    }
    
    protected AbstractCodecEmbedder(final ChannelBufferFactory bufferFactory, final ChannelHandler... handlers) {
        this(handlers);
        this.getChannel().getConfig().setBufferFactory(bufferFactory);
    }
    
    private void fireInitialEvents() {
        Channels.fireChannelOpen(this.channel);
        Channels.fireChannelBound(this.channel, this.channel.getLocalAddress());
        Channels.fireChannelConnected(this.channel, this.channel.getRemoteAddress());
    }
    
    private void configurePipeline(final ChannelHandler... handlers) {
        if (handlers == null) {
            throw new NullPointerException("handlers");
        }
        if (handlers.length == 0) {
            throw new IllegalArgumentException("handlers should contain at least one " + ChannelHandler.class.getSimpleName() + '.');
        }
        for (int i = 0; i < handlers.length; ++i) {
            final ChannelHandler h = handlers[i];
            if (h == null) {
                throw new NullPointerException("handlers[" + i + ']');
            }
            this.pipeline.addLast(String.valueOf(i), handlers[i]);
        }
        this.pipeline.addLast("SINK", this.sink);
    }
    
    public boolean finish() {
        Channels.close(this.channel);
        Channels.fireChannelDisconnected(this.channel);
        Channels.fireChannelUnbound(this.channel);
        Channels.fireChannelClosed(this.channel);
        return !this.productQueue.isEmpty();
    }
    
    protected final Channel getChannel() {
        return this.channel;
    }
    
    protected final boolean isEmpty() {
        return this.productQueue.isEmpty();
    }
    
    public final E poll() {
        return (E)this.productQueue.poll();
    }
    
    public final E peek() {
        return (E)this.productQueue.peek();
    }
    
    public final Object[] pollAll() {
        final int size = this.size();
        final Object[] a = new Object[size];
        for (int i = 0; i < size; ++i) {
            final E product = this.poll();
            if (product == null) {
                throw new ConcurrentModificationException();
            }
            a[i] = product;
        }
        return a;
    }
    
    public final <T> T[] pollAll(T[] a) {
        if (a == null) {
            throw new NullPointerException("a");
        }
        final int size = this.size();
        if (a.length < size) {
            a = (T[])Array.newInstance(a.getClass().getComponentType(), size);
        }
        int i = 0;
        while (true) {
            final T product = this.poll();
            if (product == null) {
                break;
            }
            a[i] = product;
            ++i;
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
    
    public final int size() {
        return this.productQueue.size();
    }
    
    public ChannelPipeline getPipeline() {
        return this.pipeline;
    }
    
    private final class EmbeddedChannelSink implements ChannelSink, ChannelUpstreamHandler
    {
        EmbeddedChannelSink() {
        }
        
        public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) {
            this.handleEvent(e);
        }
        
        public void eventSunk(final ChannelPipeline pipeline, final ChannelEvent e) {
            this.handleEvent(e);
        }
        
        private void handleEvent(final ChannelEvent e) {
            if (e instanceof MessageEvent) {
                final boolean offered = AbstractCodecEmbedder.this.productQueue.offer(((MessageEvent)e).getMessage());
                assert offered;
            }
            else if (e instanceof ExceptionEvent) {
                throw new CodecEmbedderException(((ExceptionEvent)e).getCause());
            }
        }
        
        public void exceptionCaught(final ChannelPipeline pipeline, final ChannelEvent e, final ChannelPipelineException cause) throws Exception {
            Throwable actualCause = cause.getCause();
            if (actualCause == null) {
                actualCause = cause;
            }
            throw new CodecEmbedderException(actualCause);
        }
        
        public ChannelFuture execute(final ChannelPipeline pipeline, final Runnable task) {
            try {
                task.run();
                return Channels.succeededFuture(pipeline.getChannel());
            }
            catch (Throwable t) {
                return Channels.failedFuture(pipeline.getChannel(), t);
            }
        }
    }
    
    private static final class EmbeddedChannelPipeline extends DefaultChannelPipeline
    {
        EmbeddedChannelPipeline() {
        }
        
        @Override
        protected void notifyHandlerException(final ChannelEvent e, Throwable t) {
            while (t instanceof ChannelPipelineException && t.getCause() != null) {
                t = t.getCause();
            }
            if (t instanceof CodecEmbedderException) {
                throw (CodecEmbedderException)t;
            }
            throw new CodecEmbedderException(t);
        }
    }
}
