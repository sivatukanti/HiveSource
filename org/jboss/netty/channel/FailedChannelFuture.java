// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class FailedChannelFuture extends CompleteChannelFuture
{
    private final Throwable cause;
    
    public FailedChannelFuture(final Channel channel, final Throwable cause) {
        super(channel);
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    public boolean isSuccess() {
        return false;
    }
    
    public ChannelFuture sync() throws InterruptedException {
        this.rethrow();
        return this;
    }
    
    public ChannelFuture syncUninterruptibly() {
        this.rethrow();
        return this;
    }
    
    private void rethrow() {
        if (this.cause instanceof RuntimeException) {
            throw (RuntimeException)this.cause;
        }
        if (this.cause instanceof Error) {
            throw (Error)this.cause;
        }
        throw new ChannelException(this.cause);
    }
}
