// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.ex;

public class ConfigurationRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -7838702245512140996L;
    
    public ConfigurationRuntimeException() {
    }
    
    public ConfigurationRuntimeException(final String message) {
        super(message);
    }
    
    public ConfigurationRuntimeException(final Throwable cause) {
        super(cause);
    }
    
    public ConfigurationRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
