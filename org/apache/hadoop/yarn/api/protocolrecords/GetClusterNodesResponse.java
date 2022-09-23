// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.NodeReport;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetClusterNodesResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static GetClusterNodesResponse newInstance(final List<NodeReport> nodeReports) {
        final GetClusterNodesResponse response = Records.newRecord(GetClusterNodesResponse.class);
        response.setNodeReports(nodeReports);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<NodeReport> getNodeReports();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setNodeReports(final List<NodeReport> p0);
}
