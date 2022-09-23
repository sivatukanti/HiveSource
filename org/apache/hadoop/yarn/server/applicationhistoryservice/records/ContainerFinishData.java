// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ContainerFinishData
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ContainerFinishData newInstance(final ContainerId containerId, final long finishTime, final String diagnosticsInfo, final int containerExitCode, final ContainerState containerState) {
        final ContainerFinishData containerFD = Records.newRecord(ContainerFinishData.class);
        containerFD.setContainerId(containerId);
        containerFD.setFinishTime(finishTime);
        containerFD.setDiagnosticsInfo(diagnosticsInfo);
        containerFD.setContainerExitStatus(containerExitCode);
        containerFD.setContainerState(containerState);
        return containerFD;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ContainerId getContainerId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setContainerId(final ContainerId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getFinishTime();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setFinishTime(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getDiagnosticsInfo();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setDiagnosticsInfo(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract int getContainerExitStatus();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setContainerExitStatus(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ContainerState getContainerState();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setContainerState(final ContainerState p0);
}
