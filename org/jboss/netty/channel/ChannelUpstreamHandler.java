// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface ChannelUpstreamHandler extends ChannelHandler
{
    void handleUpstream(final ChannelHandlerContext p0, final ChannelEvent p1) throws Exception;
}
