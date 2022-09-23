// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.nodelabels.event;

import java.util.Set;

public class StoreNewClusterNodeLabels extends NodeLabelsStoreEvent
{
    private Set<String> labels;
    
    public StoreNewClusterNodeLabels(final Set<String> labels) {
        super(NodeLabelsStoreEventType.ADD_LABELS);
        this.labels = labels;
    }
    
    public Set<String> getLabels() {
        return this.labels;
    }
}
