// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.ftp;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FTPException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public FTPException(final String message) {
        super(message);
    }
    
    public FTPException(final Throwable t) {
        super(t);
    }
    
    public FTPException(final String message, final Throwable t) {
        super(message, t);
    }
}
