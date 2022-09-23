// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface LifeCycleAwareChannelHandler extends ChannelHandler
{
    void beforeAdd(final ChannelHandlerContext p0) throws Exception;
    
    void afterAdd(final ChannelHandlerContext p0) throws Exception;
    
    void beforeRemove(final ChannelHandlerContext p0) throws Exception;
    
    void afterRemove(final ChannelHandlerContext p0) throws Exception;
}
