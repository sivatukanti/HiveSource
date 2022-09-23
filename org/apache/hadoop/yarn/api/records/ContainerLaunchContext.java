// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class ContainerLaunchContext
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static ContainerLaunchContext newInstance(final Map<String, LocalResource> localResources, final Map<String, String> environment, final List<String> commands, final Map<String, ByteBuffer> serviceData, final ByteBuffer tokens, final Map<ApplicationAccessType, String> acls) {
        final ContainerLaunchContext container = Records.newRecord(ContainerLaunchContext.class);
        container.setLocalResources(localResources);
        container.setEnvironment(environment);
        container.setCommands(commands);
        container.setServiceData(serviceData);
        container.setTokens(tokens);
        container.setApplicationACLs(acls);
        return container;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ByteBuffer getTokens();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setTokens(final ByteBuffer p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Map<String, LocalResource> getLocalResources();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setLocalResources(final Map<String, LocalResource> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Map<String, ByteBuffer> getServiceData();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setServiceData(final Map<String, ByteBuffer> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Map<String, String> getEnvironment();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setEnvironment(final Map<String, String> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<String> getCommands();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setCommands(final List<String> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Map<ApplicationAccessType, String> getApplicationACLs();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setApplicationACLs(final Map<ApplicationAccessType, String> p0);
}
