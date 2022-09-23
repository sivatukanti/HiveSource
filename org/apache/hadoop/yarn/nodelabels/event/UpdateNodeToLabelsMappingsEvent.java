// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.nodelabels.event;

import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;

public class UpdateNodeToLabelsMappingsEvent extends NodeLabelsStoreEvent
{
    private Map<NodeId, Set<String>> nodeToLabels;
    
    public UpdateNodeToLabelsMappingsEvent(final Map<NodeId, Set<String>> nodeToLabels) {
        super(NodeLabelsStoreEventType.STORE_NODE_TO_LABELS);
        this.nodeToLabels = nodeToLabels;
    }
    
    public Map<NodeId, Set<String>> getNodeToLabels() {
        return this.nodeToLabels;
    }
}
