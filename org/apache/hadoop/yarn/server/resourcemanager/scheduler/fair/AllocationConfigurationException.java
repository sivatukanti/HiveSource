// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class AllocationConfigurationException extends Exception
{
    private static final long serialVersionUID = 4046517047810854249L;
    
    public AllocationConfigurationException(final String message) {
        super(message);
    }
    
    public AllocationConfigurationException(final String message, final Throwable t) {
        super(message, t);
    }
}
