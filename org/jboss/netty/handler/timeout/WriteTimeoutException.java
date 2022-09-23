// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.timeout;

public class WriteTimeoutException extends TimeoutException
{
    private static final long serialVersionUID = -7746685254523245218L;
    
    public WriteTimeoutException() {
    }
    
    public WriteTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public WriteTimeoutException(final String message) {
        super(message);
    }
    
    public WriteTimeoutException(final Throwable cause) {
        super(cause);
    }
}
