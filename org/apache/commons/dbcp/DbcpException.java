// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp;

public class DbcpException extends RuntimeException
{
    private static final long serialVersionUID = 2477800549022838103L;
    protected Throwable cause;
    
    public DbcpException() {
        this.cause = null;
    }
    
    public DbcpException(final String message) {
        this(message, null);
    }
    
    public DbcpException(final String message, final Throwable cause) {
        super(message);
        this.cause = null;
        this.cause = cause;
    }
    
    public DbcpException(final Throwable cause) {
        super((cause == null) ? null : cause.toString());
        this.cause = null;
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
