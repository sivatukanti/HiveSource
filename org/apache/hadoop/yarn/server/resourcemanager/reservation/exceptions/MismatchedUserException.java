// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class MismatchedUserException extends PlanningException
{
    private static final long serialVersionUID = 8313222590561668413L;
    
    public MismatchedUserException(final String message) {
        super(message);
    }
    
    public MismatchedUserException(final Throwable cause) {
        super(cause);
    }
    
    public MismatchedUserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
