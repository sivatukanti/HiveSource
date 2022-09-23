// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api;

import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ApplicationInitializationContext
{
    private final String user;
    private final ApplicationId applicationId;
    private ByteBuffer appDataForService;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public ApplicationInitializationContext(final String user, final ApplicationId applicationId, final ByteBuffer appDataForService) {
        this.user = user;
        this.applicationId = applicationId;
        this.appDataForService = appDataForService;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public ApplicationId getApplicationId() {
        return this.applicationId;
    }
    
    public ByteBuffer getApplicationDataForService() {
        return this.appDataForService;
    }
}
