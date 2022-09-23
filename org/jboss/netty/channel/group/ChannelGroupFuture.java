// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.group;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

public interface ChannelGroupFuture extends Iterable<ChannelFuture>
{
    ChannelGroup getGroup();
    
    ChannelFuture find(final Integer p0);
    
    ChannelFuture find(final Channel p0);
    
    boolean isDone();
    
    boolean isCompleteSuccess();
    
    boolean isPartialSuccess();
    
    boolean isCompleteFailure();
    
    boolean isPartialFailure();
    
    void addListener(final ChannelGroupFutureListener p0);
    
    void removeListener(final ChannelGroupFutureListener p0);
    
    ChannelGroupFuture await() throws InterruptedException;
    
    ChannelGroupFuture awaitUninterruptibly();
    
    boolean await(final long p0, final TimeUnit p1) throws InterruptedException;
    
    boolean await(final long p0) throws InterruptedException;
    
    boolean awaitUninterruptibly(final long p0, final TimeUnit p1);
    
    boolean awaitUninterruptibly(final long p0);
    
    Iterator<ChannelFuture> iterator();
}
