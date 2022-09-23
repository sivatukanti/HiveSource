// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.embedder;

public class CodecEmbedderException extends RuntimeException
{
    private static final long serialVersionUID = -6283302594160331474L;
    
    public CodecEmbedderException() {
    }
    
    public CodecEmbedderException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public CodecEmbedderException(final String message) {
        super(message);
    }
    
    public CodecEmbedderException(final Throwable cause) {
        super(cause);
    }
}
