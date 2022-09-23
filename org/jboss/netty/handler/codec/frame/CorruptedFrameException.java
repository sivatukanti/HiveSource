// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.frame;

public class CorruptedFrameException extends Exception
{
    private static final long serialVersionUID = 3918052232492988408L;
    
    public CorruptedFrameException() {
    }
    
    public CorruptedFrameException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public CorruptedFrameException(final String message) {
        super(message);
    }
    
    public CorruptedFrameException(final Throwable cause) {
        super(cause);
    }
}
