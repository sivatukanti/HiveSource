// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ApplicationTerminationContext
{
    private final ApplicationId applicationId;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public ApplicationTerminationContext(final ApplicationId applicationId) {
        this.applicationId = applicationId;
    }
    
    public ApplicationId getApplicationId() {
        return this.applicationId;
    }
}
