// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public abstract class AbstractChannelSink implements ChannelSink
{
    protected AbstractChannelSink() {
    }
    
    public void exceptionCaught(final ChannelPipeline pipeline, final ChannelEvent event, final ChannelPipelineException cause) throws Exception {
        Throwable actualCause = cause.getCause();
        if (actualCause == null) {
            actualCause = cause;
        }
        if (this.isFireExceptionCaughtLater(event, actualCause)) {
            Channels.fireExceptionCaughtLater(event.getChannel(), actualCause);
        }
        else {
            Channels.fireExceptionCaught(event.getChannel(), actualCause);
        }
    }
    
    protected boolean isFireExceptionCaughtLater(final ChannelEvent event, final Throwable actualCause) {
        return false;
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
