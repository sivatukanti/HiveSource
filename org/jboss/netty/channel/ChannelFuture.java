// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.util.concurrent.TimeUnit;

public interface ChannelFuture
{
    Channel getChannel();
    
    boolean isDone();
    
    boolean isCancelled();
    
    boolean isSuccess();
    
    Throwable getCause();
    
    boolean cancel();
    
    boolean setSuccess();
    
    boolean setFailure(final Throwable p0);
    
    boolean setProgress(final long p0, final long p1, final long p2);
    
    void addListener(final ChannelFutureListener p0);
    
    void removeListener(final ChannelFutureListener p0);
    
    ChannelFuture sync() throws InterruptedException;
    
    ChannelFuture syncUninterruptibly();
    
    ChannelFuture await() throws InterruptedException;
    
    ChannelFuture awaitUninterruptibly();
    
    boolean await(final long p0, final TimeUnit p1) throws InterruptedException;
    
    boolean await(final long p0) throws InterruptedException;
    
    boolean awaitUninterruptibly(final long p0, final TimeUnit p1);
    
    boolean awaitUninterruptibly(final long p0);
}
