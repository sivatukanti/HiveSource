// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ResourceOption;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class UpdateNodeResourceRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public static UpdateNodeResourceRequest newInstance(final Map<NodeId, ResourceOption> nodeResourceMap) {
        final UpdateNodeResourceRequest request = Records.newRecord(UpdateNodeResourceRequest.class);
        request.setNodeResourceMap(nodeResourceMap);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract Map<NodeId, ResourceOption> getNodeResourceMap();
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract void setNodeResourceMap(final Map<NodeId, ResourceOption> p0);
}
