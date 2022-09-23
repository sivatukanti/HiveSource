// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class ChannelHandlerLifeCycleException extends RuntimeException
{
    private static final long serialVersionUID = 8764799996088850672L;
    
    public ChannelHandlerLifeCycleException() {
    }
    
    public ChannelHandlerLifeCycleException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ChannelHandlerLifeCycleException(final String message) {
        super(message);
    }
    
    public ChannelHandlerLifeCycleException(final Throwable cause) {
        super(cause);
    }
}
