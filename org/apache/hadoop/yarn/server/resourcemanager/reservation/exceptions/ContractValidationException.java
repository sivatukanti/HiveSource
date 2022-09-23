// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ContractValidationException extends PlanningException
{
    private static final long serialVersionUID = 1L;
    
    public ContractValidationException(final String message) {
        super(message);
    }
}
