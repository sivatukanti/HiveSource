// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ApplicationIdNotProvidedException extends YarnException
{
    private static final long serialVersionUID = 911754350L;
    
    public ApplicationIdNotProvidedException(final Throwable cause) {
        super(cause);
    }
    
    public ApplicationIdNotProvidedException(final String message) {
        super(message);
    }
    
    public ApplicationIdNotProvidedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
