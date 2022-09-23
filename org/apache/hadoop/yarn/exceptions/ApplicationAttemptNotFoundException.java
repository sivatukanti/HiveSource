// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ApplicationAttemptNotFoundException extends YarnException
{
    private static final long serialVersionUID = 8694508L;
    
    public ApplicationAttemptNotFoundException(final Throwable cause) {
        super(cause);
    }
    
    public ApplicationAttemptNotFoundException(final String message) {
        super(message);
    }
    
    public ApplicationAttemptNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
