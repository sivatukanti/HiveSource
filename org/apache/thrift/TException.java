// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

public class TException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public TException() {
    }
    
    public TException(final String message) {
        super(message);
    }
    
    public TException(final Throwable cause) {
        super(cause);
    }
    
    public TException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
