// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.logging.InternalLogger;

public abstract class CompleteChannelFuture implements ChannelFuture
{
    private static final InternalLogger logger;
    private final Channel channel;
    
    protected CompleteChannelFuture(final Channel channel) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        this.channel = channel;
    }
    
    public void addListener(final ChannelFutureListener listener) {
        try {
            listener.operationComplete(this);
        }
        catch (Throwable t) {
            if (CompleteChannelFuture.logger.isWarnEnabled()) {
                CompleteChannelFuture.logger.warn("An exception was thrown by " + ChannelFutureListener.class.getSimpleName() + '.', t);
            }
        }
    }
    
    public void removeListener(final ChannelFutureListener listener) {
    }
    
    public ChannelFuture await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return this;
    }
    
    public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return true;
    }
    
    public boolean await(final long timeoutMillis) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return true;
    }
    
    public ChannelFuture awaitUninterruptibly() {
        return this;
    }
    
    public boolean awaitUninterruptibly(final long timeout, final TimeUnit unit) {
        return true;
    }
    
    public boolean awaitUninterruptibly(final long timeoutMillis) {
        return true;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public boolean isDone() {
        return true;
    }
    
    public boolean setProgress(final long amount, final long current, final long total) {
        return false;
    }
    
    public boolean setFailure(final Throwable cause) {
        return false;
    }
    
    public boolean setSuccess() {
        return false;
    }
    
    public boolean cancel() {
        return false;
    }
    
    public boolean isCancelled() {
        return false;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(CompleteChannelFuture.class);
    }
}
