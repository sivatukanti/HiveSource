// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.socket.Worker;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.ChannelRunnableWrapper;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.AbstractChannelSink;

public abstract class AbstractOioChannelSink extends AbstractChannelSink
{
    @Override
    public ChannelFuture execute(final ChannelPipeline pipeline, final Runnable task) {
        final Channel ch = pipeline.getChannel();
        if (ch instanceof AbstractOioChannel) {
            final AbstractOioChannel channel = (AbstractOioChannel)ch;
            final Worker worker = channel.worker;
            if (worker != null) {
                final ChannelRunnableWrapper wrapper = new ChannelRunnableWrapper(pipeline.getChannel(), task);
                channel.worker.executeInIoThread(wrapper);
                return wrapper;
            }
        }
        return super.execute(pipeline, task);
    }
    
    @Override
    protected boolean isFireExceptionCaughtLater(final ChannelEvent event, final Throwable actualCause) {
        final Channel channel = event.getChannel();
        boolean fireLater = false;
        if (channel instanceof AbstractOioChannel) {
            fireLater = !AbstractOioWorker.isIoThread((AbstractOioChannel)channel);
        }
        return fireLater;
    }
}
