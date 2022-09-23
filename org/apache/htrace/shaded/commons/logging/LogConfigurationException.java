// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.commons.logging;

public class LogConfigurationException extends RuntimeException
{
    protected Throwable cause;
    
    public LogConfigurationException() {
        this.cause = null;
    }
    
    public LogConfigurationException(final String message) {
        super(message);
        this.cause = null;
    }
    
    public LogConfigurationException(final Throwable cause) {
        this((cause == null) ? null : cause.toString(), cause);
    }
    
    public LogConfigurationException(final String message, final Throwable cause) {
        super(message + " (Caused by " + cause + ")");
        this.cause = null;
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}
