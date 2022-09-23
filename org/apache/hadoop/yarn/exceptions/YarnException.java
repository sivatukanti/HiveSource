// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class YarnException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public YarnException() {
    }
    
    public YarnException(final String message) {
        super(message);
    }
    
    public YarnException(final Throwable cause) {
        super(cause);
    }
    
    public YarnException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
