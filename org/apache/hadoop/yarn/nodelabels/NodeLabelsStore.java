// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.nodelabels;

import java.util.Collection;
import java.io.IOException;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import java.io.Closeable;

public abstract class NodeLabelsStore implements Closeable
{
    protected final CommonNodeLabelsManager mgr;
    protected Configuration conf;
    
    public NodeLabelsStore(final CommonNodeLabelsManager mgr) {
        this.mgr = mgr;
    }
    
    public abstract void updateNodeToLabelsMappings(final Map<NodeId, Set<String>> p0) throws IOException;
    
    public abstract void storeNewClusterNodeLabels(final Set<String> p0) throws IOException;
    
    public abstract void removeClusterNodeLabels(final Collection<String> p0) throws IOException;
    
    public abstract void recover() throws IOException;
    
    public void init(final Configuration conf) throws Exception {
        this.conf = conf;
    }
    
    public CommonNodeLabelsManager getNodeLabelsManager() {
        return this.mgr;
    }
}
