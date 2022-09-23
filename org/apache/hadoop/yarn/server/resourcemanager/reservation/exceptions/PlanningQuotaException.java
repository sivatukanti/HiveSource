// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class PlanningQuotaException extends PlanningException
{
    private static final long serialVersionUID = 8206629288380246166L;
    
    public PlanningQuotaException(final String message) {
        super(message);
    }
    
    public PlanningQuotaException(final Throwable cause) {
        super(cause);
    }
    
    public PlanningQuotaException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
