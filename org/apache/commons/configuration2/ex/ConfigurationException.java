// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.ex;

public class ConfigurationException extends Exception
{
    private static final long serialVersionUID = -1316746661346991484L;
    
    public ConfigurationException() {
    }
    
    public ConfigurationException(final String message) {
        super(message);
    }
    
    public ConfigurationException(final Throwable cause) {
        super(cause);
    }
    
    public ConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
