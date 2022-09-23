// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class PlanningException extends Exception
{
    private static final long serialVersionUID = -684069387367879218L;
    
    public PlanningException(final String message) {
        super(message);
    }
    
    public PlanningException(final Throwable cause) {
        super(cause);
    }
    
    public PlanningException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
