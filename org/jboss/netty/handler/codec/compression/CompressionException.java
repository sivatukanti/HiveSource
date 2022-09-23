// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.compression;

public class CompressionException extends RuntimeException
{
    private static final long serialVersionUID = 5603413481274811897L;
    
    public CompressionException() {
    }
    
    public CompressionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public CompressionException(final String message) {
        super(message);
    }
    
    public CompressionException(final Throwable cause) {
        super(cause);
    }
}
