// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import java.util.Set;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class RemoveFromClusterNodeLabelsRequest
{
    public static RemoveFromClusterNodeLabelsRequest newInstance(final Set<String> labels) {
        final RemoveFromClusterNodeLabelsRequest request = Records.newRecord(RemoveFromClusterNodeLabelsRequest.class);
        request.setNodeLabels(labels);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract void setNodeLabels(final Set<String> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract Set<String> getNodeLabels();
}
