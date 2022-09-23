// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class DefaultExceptionEvent implements ExceptionEvent
{
    private final Channel channel;
    private final Throwable cause;
    
    public DefaultExceptionEvent(final Channel channel, final Throwable cause) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        this.channel = channel;
        this.cause = cause;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public ChannelFuture getFuture() {
        return Channels.succeededFuture(this.getChannel());
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    @Override
    public String toString() {
        return this.getChannel().toString() + " EXCEPTION: " + this.cause;
    }
}
