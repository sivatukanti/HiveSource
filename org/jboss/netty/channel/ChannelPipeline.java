// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.util.Map;
import java.util.List;

public interface ChannelPipeline
{
    void addFirst(final String p0, final ChannelHandler p1);
    
    void addLast(final String p0, final ChannelHandler p1);
    
    void addBefore(final String p0, final String p1, final ChannelHandler p2);
    
    void addAfter(final String p0, final String p1, final ChannelHandler p2);
    
    void remove(final ChannelHandler p0);
    
    ChannelHandler remove(final String p0);
    
     <T extends ChannelHandler> T remove(final Class<T> p0);
    
    ChannelHandler removeFirst();
    
    ChannelHandler removeLast();
    
    void replace(final ChannelHandler p0, final String p1, final ChannelHandler p2);
    
    ChannelHandler replace(final String p0, final String p1, final ChannelHandler p2);
    
     <T extends ChannelHandler> T replace(final Class<T> p0, final String p1, final ChannelHandler p2);
    
    ChannelHandler getFirst();
    
    ChannelHandler getLast();
    
    ChannelHandler get(final String p0);
    
     <T extends ChannelHandler> T get(final Class<T> p0);
    
    ChannelHandlerContext getContext(final ChannelHandler p0);
    
    ChannelHandlerContext getContext(final String p0);
    
    ChannelHandlerContext getContext(final Class<? extends ChannelHandler> p0);
    
    void sendUpstream(final ChannelEvent p0);
    
    void sendDownstream(final ChannelEvent p0);
    
    ChannelFuture execute(final Runnable p0);
    
    Channel getChannel();
    
    ChannelSink getSink();
    
    void attach(final Channel p0, final ChannelSink p1);
    
    boolean isAttached();
    
    List<String> getNames();
    
    Map<String, ChannelHandler> toMap();
}
