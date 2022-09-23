// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class NodeHealthStatus
{
    @InterfaceAudience.Private
    public static NodeHealthStatus newInstance(final boolean isNodeHealthy, final String healthReport, final long lastHealthReport) {
        final NodeHealthStatus status = Records.newRecord(NodeHealthStatus.class);
        status.setIsNodeHealthy(isNodeHealthy);
        status.setHealthReport(healthReport);
        status.setLastHealthReportTime(lastHealthReport);
        return status;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract boolean getIsNodeHealthy();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setIsNodeHealthy(final boolean p0);
    
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
}
