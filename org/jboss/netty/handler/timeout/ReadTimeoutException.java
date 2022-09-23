// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.timeout;

public class ReadTimeoutException extends TimeoutException
{
    private static final long serialVersionUID = -4596059237992273913L;
    
    public ReadTimeoutException() {
    }
    
    public ReadTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ReadTimeoutException(final String message) {
        super(message);
    }
    
    public ReadTimeoutException(final Throwable cause) {
        super(cause);
    }
}
