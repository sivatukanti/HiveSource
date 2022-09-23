// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io;

public class RuntimeIOException extends RuntimeException
{
    public RuntimeIOException() {
    }
    
    public RuntimeIOException(final String message) {
        super(message);
    }
    
    public RuntimeIOException(final Throwable cause) {
        super(cause);
    }
    
    public RuntimeIOException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
