// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.queue;

import java.io.InterruptedIOException;

public class BlockingReadTimeoutException extends InterruptedIOException
{
    private static final long serialVersionUID = 356009226872649493L;
    
    public BlockingReadTimeoutException() {
    }
    
    public BlockingReadTimeoutException(final String message, final Throwable cause) {
        super(message);
        this.initCause(cause);
    }
    
    public BlockingReadTimeoutException(final String message) {
        super(message);
    }
    
    public BlockingReadTimeoutException(final Throwable cause) {
        this.initCause(cause);
    }
}
