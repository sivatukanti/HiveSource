// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.timeout;

import org.jboss.netty.channel.ChannelException;

public class TimeoutException extends ChannelException
{
    private static final long serialVersionUID = 4673641882869672533L;
    
    public TimeoutException() {
    }
    
    public TimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public TimeoutException(final String message) {
        super(message);
    }
    
    public TimeoutException(final Throwable cause) {
        super(cause);
    }
}
