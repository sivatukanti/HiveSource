// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

public final class OutOfScopeException extends RuntimeException
{
    public OutOfScopeException(final String message) {
        super(message);
    }
    
    public OutOfScopeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public OutOfScopeException(final Throwable cause) {
        super(cause);
    }
}
