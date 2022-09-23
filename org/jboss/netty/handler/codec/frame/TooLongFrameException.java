// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.frame;

public class TooLongFrameException extends Exception
{
    private static final long serialVersionUID = -1995801950698951640L;
    
    public TooLongFrameException() {
    }
    
    public TooLongFrameException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public TooLongFrameException(final String message) {
        super(message);
    }
    
    public TooLongFrameException(final Throwable cause) {
        super(cause);
    }
}
