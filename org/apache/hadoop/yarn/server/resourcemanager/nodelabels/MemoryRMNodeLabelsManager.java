// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.nodelabels;

import java.io.IOException;
import org.apache.hadoop.yarn.nodelabels.CommonNodeLabelsManager;
import org.apache.hadoop.yarn.nodelabels.NodeLabelsStore;
import org.apache.hadoop.conf.Configuration;
import java.util.Collection;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;

public class MemoryRMNodeLabelsManager extends RMNodeLabelsManager
{
    Map<NodeId, Set<String>> lastNodeToLabels;
    Collection<String> lastAddedlabels;
    Collection<String> lastRemovedlabels;
    
    public MemoryRMNodeLabelsManager() {
        this.lastNodeToLabels = null;
        this.lastAddedlabels = null;
        this.lastRemovedlabels = null;
    }
    
    public void initNodeLabelStore(final Configuration conf) {
        this.store = new NodeLabelsStore(this) {
            @Override
            public void recover() throws IOException {
            }
            
            @Override
            public void removeClusterNodeLabels(final Collection<String> labels) throws IOException {
            }
            
            @Override
            public void updateNodeToLabelsMappings(final Map<NodeId, Set<String>> nodeToLabels) throws IOException {
            }
            
            @Override
            public void storeNewClusterNodeLabels(final Set<String> label) throws IOException {
            }
            
            @Override
            public void close() throws IOException {
            }
        };
    }
    
    @Override
    protected void initDispatcher(final Configuration conf) {
        super.dispatcher = null;
    }
    
    @Override
    protected void startDispatcher() {
    }
    
    @Override
    protected void stopDispatcher() {
    }
}
