// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface ChannelSink
{
    void eventSunk(final ChannelPipeline p0, final ChannelEvent p1) throws Exception;
    
    void exceptionCaught(final ChannelPipeline p0, final ChannelEvent p1, final ChannelPipelineException p2) throws Exception;
    
    ChannelFuture execute(final ChannelPipeline p0, final Runnable p1);
}
