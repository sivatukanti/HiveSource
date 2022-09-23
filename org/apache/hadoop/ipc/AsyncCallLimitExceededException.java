// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.io.IOException;

public class AsyncCallLimitExceededException extends IOException
{
    private static final long serialVersionUID = 1L;
    
    public AsyncCallLimitExceededException(final String message) {
        super(message);
    }
}
