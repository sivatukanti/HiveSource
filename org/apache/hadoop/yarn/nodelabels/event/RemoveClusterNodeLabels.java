// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.nodelabels.event;

import java.util.Collection;

public class RemoveClusterNodeLabels extends NodeLabelsStoreEvent
{
    private Collection<String> labels;
    
    public RemoveClusterNodeLabels(final Collection<String> labels) {
        super(NodeLabelsStoreEventType.REMOVE_LABELS);
        this.labels = labels;
    }
    
    public Collection<String> getLabels() {
        return this.labels;
    }
}
