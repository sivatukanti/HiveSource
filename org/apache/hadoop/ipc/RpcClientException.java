// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

public class RpcClientException extends RpcException
{
    private static final long serialVersionUID = 1L;
    
    RpcClientException(final String message) {
        super(message);
    }
    
    RpcClientException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
