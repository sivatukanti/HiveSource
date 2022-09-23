// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class StartContainerRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static StartContainerRequest newInstance(final ContainerLaunchContext context, final Token container) {
        final StartContainerRequest request = Records.newRecord(StartContainerRequest.class);
        request.setContainerLaunchContext(context);
        request.setContainerToken(container);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ContainerLaunchContext getContainerLaunchContext();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setContainerLaunchContext(final ContainerLaunchContext p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Token getContainerToken();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setContainerToken(final Token p0);
}
