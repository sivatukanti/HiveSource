// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import java.util.Set;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class NodeReport
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static NodeReport newInstance(final NodeId nodeId, final NodeState nodeState, final String httpAddress, final String rackName, final Resource used, final Resource capability, final int numContainers, final String healthReport, final long lastHealthReportTime) {
        return newInstance(nodeId, nodeState, httpAddress, rackName, used, capability, numContainers, healthReport, lastHealthReportTime, null);
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static NodeReport newInstance(final NodeId nodeId, final NodeState nodeState, final String httpAddress, final String rackName, final Resource used, final Resource capability, final int numContainers, final String healthReport, final long lastHealthReportTime, final Set<String> nodeLabels) {
        final NodeReport nodeReport = Records.newRecord(NodeReport.class);
        nodeReport.setNodeId(nodeId);
        nodeReport.setNodeState(nodeState);
        nodeReport.setHttpAddress(httpAddress);
        nodeReport.setRackName(rackName);
        nodeReport.setUsed(used);
        nodeReport.setCapability(capability);
        nodeReport.setNumContainers(numContainers);
        nodeReport.setHealthReport(healthReport);
        nodeReport.setLastHealthReportTime(lastHealthReportTime);
        nodeReport.setNodeLabels(nodeLabels);
        return nodeReport;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract NodeId getNodeId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNodeId(final NodeId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract NodeState getNodeState();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNodeState(final NodeState p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getHttpAddress();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setHttpAddress(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getRackName();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setRackName(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Resource getUsed();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setUsed(final Resource p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Resource getCapability();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setCapability(final Resource p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract int getNumContainers();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNumContainers(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getHealthReport();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setHealthReport(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract long getLastHealthReportTime();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setLastHealthReportTime(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Set<String> getNodeLabels();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNodeLabels(final Set<String> p0);
}
