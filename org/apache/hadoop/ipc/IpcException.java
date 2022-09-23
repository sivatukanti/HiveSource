// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.io.IOException;

public class IpcException extends IOException
{
    private static final long serialVersionUID = 1L;
    final String errMsg;
    
    public IpcException(final String err) {
        this.errMsg = err;
    }
}
