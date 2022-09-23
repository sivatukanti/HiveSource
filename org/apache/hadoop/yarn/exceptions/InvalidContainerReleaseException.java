// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

public class InvalidContainerReleaseException extends YarnException
{
    private static final long serialVersionUID = 13498237L;
    
    public InvalidContainerReleaseException(final Throwable cause) {
        super(cause);
    }
    
    public InvalidContainerReleaseException(final String message) {
        super(message);
    }
    
    public InvalidContainerReleaseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
