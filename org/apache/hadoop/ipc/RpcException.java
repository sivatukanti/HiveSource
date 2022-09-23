// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.io.IOException;

public class RpcException extends IOException
{
    private static final long serialVersionUID = 1L;
    
    RpcException(final String message) {
        super(message);
    }
    
    RpcException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
