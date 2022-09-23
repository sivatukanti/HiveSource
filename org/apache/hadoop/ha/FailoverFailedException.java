// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class FailoverFailedException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public FailoverFailedException(final String message) {
        super(message);
    }
    
    public FailoverFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
