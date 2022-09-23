// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface ChannelDownstreamHandler extends ChannelHandler
{
    void handleDownstream(final ChannelHandlerContext p0, final ChannelEvent p1) throws Exception;
}
