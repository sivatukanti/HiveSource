// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ResourceOverCommitException extends PlanningException
{
    private static final long serialVersionUID = 7070699407526521032L;
    
    public ResourceOverCommitException(final String message) {
        super(message);
    }
    
    public ResourceOverCommitException(final Throwable cause) {
        super(cause);
    }
    
    public ResourceOverCommitException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
