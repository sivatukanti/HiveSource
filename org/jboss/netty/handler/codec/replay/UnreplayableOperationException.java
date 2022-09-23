// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.replay;

public class UnreplayableOperationException extends UnsupportedOperationException
{
    private static final long serialVersionUID = 8577363912862364021L;
    
    public UnreplayableOperationException() {
    }
    
    public UnreplayableOperationException(final String message) {
        super(message);
    }
    
    public UnreplayableOperationException(final Throwable cause) {
        super(cause);
    }
    
    public UnreplayableOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
