// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.ex;

public class ConversionException extends ConfigurationRuntimeException
{
    private static final long serialVersionUID = -5167943099293540392L;
    
    public ConversionException() {
    }
    
    public ConversionException(final String message) {
        super(message);
    }
    
    public ConversionException(final Throwable cause) {
        super(cause);
    }
    
    public ConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
