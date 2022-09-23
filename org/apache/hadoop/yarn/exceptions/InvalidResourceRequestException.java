// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

public class InvalidResourceRequestException extends YarnException
{
    private static final long serialVersionUID = 13498237L;
    
    public InvalidResourceRequestException(final Throwable cause) {
        super(cause);
    }
    
    public InvalidResourceRequestException(final String message) {
        super(message);
    }
    
    public InvalidResourceRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
