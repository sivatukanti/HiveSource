// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.net.SocketAddress;

public interface Channel extends Comparable<Channel>
{
    public static final int OP_NONE = 0;
    public static final int OP_READ = 1;
    public static final int OP_WRITE = 4;
    public static final int OP_READ_WRITE = 5;
    
    Integer getId();
    
    ChannelFactory getFactory();
    
    Channel getParent();
    
    ChannelConfig getConfig();
    
    ChannelPipeline getPipeline();
    
    boolean isOpen();
    
    boolean isBound();
    
    boolean isConnected();
    
    SocketAddress getLocalAddress();
    
    SocketAddress getRemoteAddress();
    
    ChannelFuture write(final Object p0);
    
    ChannelFuture write(final Object p0, final SocketAddress p1);
    
    ChannelFuture bind(final SocketAddress p0);
    
    ChannelFuture connect(final SocketAddress p0);
    
    ChannelFuture disconnect();
    
    ChannelFuture unbind();
    
    ChannelFuture close();
    
    ChannelFuture getCloseFuture();
    
    int getInterestOps();
    
    boolean isReadable();
    
    boolean isWritable();
    
    ChannelFuture setInterestOps(final int p0);
    
    ChannelFuture setReadable(final boolean p0);
    
    boolean getUserDefinedWritability(final int p0);
    
    void setUserDefinedWritability(final int p0, final boolean p1);
    
    Object getAttachment();
    
    void setAttachment(final Object p0);
}
