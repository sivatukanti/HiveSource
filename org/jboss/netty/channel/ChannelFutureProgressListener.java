// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface ChannelFutureProgressListener extends ChannelFutureListener
{
    void operationProgressed(final ChannelFuture p0, final long p1, final long p2, final long p3) throws Exception;
}
