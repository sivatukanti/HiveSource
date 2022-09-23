// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class ReplaceLabelsOnNodeRequest
{
    public static ReplaceLabelsOnNodeRequest newInstance(final Map<NodeId, Set<String>> map) {
        final ReplaceLabelsOnNodeRequest request = Records.newRecord(ReplaceLabelsOnNodeRequest.class);
        request.setNodeToLabels(map);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract void setNodeToLabels(final Map<NodeId, Set<String>> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract Map<NodeId, Set<String>> getNodeToLabels();
}
