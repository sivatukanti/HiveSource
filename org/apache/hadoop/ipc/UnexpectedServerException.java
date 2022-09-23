// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

public class UnexpectedServerException extends RpcException
{
    private static final long serialVersionUID = 1L;
    
    UnexpectedServerException(final String message) {
        super(message);
    }
    
    UnexpectedServerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
