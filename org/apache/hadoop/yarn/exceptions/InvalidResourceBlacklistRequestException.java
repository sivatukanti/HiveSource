// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

public class InvalidResourceBlacklistRequestException extends YarnException
{
    private static final long serialVersionUID = 384957911L;
    
    public InvalidResourceBlacklistRequestException(final Throwable cause) {
        super(cause);
    }
    
    public InvalidResourceBlacklistRequestException(final String message) {
        super(message);
    }
    
    public InvalidResourceBlacklistRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
