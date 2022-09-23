// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

public class InvalidApplicationMasterRequestException extends YarnException
{
    private static final long serialVersionUID = 1357686L;
    
    public InvalidApplicationMasterRequestException(final Throwable cause) {
        super(cause);
    }
    
    public InvalidApplicationMasterRequestException(final String message) {
        super(message);
    }
    
    public InvalidApplicationMasterRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
