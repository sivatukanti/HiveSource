// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.NodeState;
import java.util.EnumSet;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetClusterNodesRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetClusterNodesRequest newInstance(final EnumSet<NodeState> states) {
        final GetClusterNodesRequest request = Records.newRecord(GetClusterNodesRequest.class);
        request.setNodeStates(states);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetClusterNodesRequest newInstance() {
        final GetClusterNodesRequest request = Records.newRecord(GetClusterNodesRequest.class);
        return request;
    }
    
    public abstract EnumSet<NodeState> getNodeStates();
    
    public abstract void setNodeStates(final EnumSet<NodeState> p0);
}
