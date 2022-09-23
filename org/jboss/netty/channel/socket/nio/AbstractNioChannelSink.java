// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.ChannelRunnableWrapper;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.AbstractChannelSink;

public abstract class AbstractNioChannelSink extends AbstractChannelSink
{
    @Override
    public ChannelFuture execute(final ChannelPipeline pipeline, final Runnable task) {
        final Channel ch = pipeline.getChannel();
        if (ch instanceof AbstractNioChannel) {
            final AbstractNioChannel<?> channel = (AbstractNioChannel<?>)ch;
            final ChannelRunnableWrapper wrapper = new ChannelRunnableWrapper(pipeline.getChannel(), task);
            channel.worker.executeInIoThread(wrapper);
            return wrapper;
        }
        return super.execute(pipeline, task);
    }
    
    @Override
    protected boolean isFireExceptionCaughtLater(final ChannelEvent event, final Throwable actualCause) {
        final Channel channel = event.getChannel();
        boolean fireLater = false;
        if (channel instanceof AbstractNioChannel) {
            fireLater = !AbstractNioWorker.isIoThread((AbstractNioChannel<?>)channel);
        }
        return fireLater;
    }
}
