// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface ChannelHandlerContext
{
    Channel getChannel();
    
    ChannelPipeline getPipeline();
    
    String getName();
    
    ChannelHandler getHandler();
    
    boolean canHandleUpstream();
    
    boolean canHandleDownstream();
    
    void sendUpstream(final ChannelEvent p0);
    
    void sendDownstream(final ChannelEvent p0);
    
    Object getAttachment();
    
    void setAttachment(final Object p0);
}
