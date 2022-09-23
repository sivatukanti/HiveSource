// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class ContainerStatus
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ContainerStatus newInstance(final ContainerId containerId, final ContainerState containerState, final String diagnostics, final int exitStatus) {
        final ContainerStatus containerStatus = Records.newRecord(ContainerStatus.class);
        containerStatus.setState(containerState);
        containerStatus.setContainerId(containerId);
        containerStatus.setDiagnostics(diagnostics);
        containerStatus.setExitStatus(exitStatus);
        return containerStatus;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ContainerId getContainerId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setContainerId(final ContainerId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ContainerState getState();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setState(final ContainerState p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract int getExitStatus();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setExitStatus(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getDiagnostics();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setDiagnostics(final String p0);
}
