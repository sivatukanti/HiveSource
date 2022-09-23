// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ApplicationNotFoundException extends YarnException
{
    private static final long serialVersionUID = 8694408L;
    
    public ApplicationNotFoundException(final Throwable cause) {
        super(cause);
    }
    
    public ApplicationNotFoundException(final String message) {
        super(message);
    }
    
    public ApplicationNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
