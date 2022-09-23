// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.util.Records;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;

public abstract class GetNodesToLabelsResponse
{
    public static GetNodesToLabelsResponse newInstance(final Map<NodeId, Set<String>> map) {
        final GetNodesToLabelsResponse response = Records.newRecord(GetNodesToLabelsResponse.class);
        response.setNodeToLabels(map);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract void setNodeToLabels(final Map<NodeId, Set<String>> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract Map<NodeId, Set<String>> getNodeToLabels();
}
