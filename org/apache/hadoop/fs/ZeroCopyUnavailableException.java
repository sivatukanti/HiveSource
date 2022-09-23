// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;

public class ZeroCopyUnavailableException extends IOException
{
    private static final long serialVersionUID = 0L;
    
    public ZeroCopyUnavailableException(final String message) {
        super(message);
    }
    
    public ZeroCopyUnavailableException(final String message, final Exception e) {
        super(message, e);
    }
    
    public ZeroCopyUnavailableException(final Exception e) {
        super(e);
    }
}
