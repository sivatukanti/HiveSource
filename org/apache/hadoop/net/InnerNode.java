// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public interface InnerNode extends Node
{
    boolean add(final Node p0);
    
    Node getLoc(final String p0);
    
    List<Node> getChildren();
    
    int getNumOfLeaves();
    
    boolean remove(final Node p0);
    
    Node getLeaf(final int p0, final Node p1);
    
    public interface Factory<N extends InnerNode>
    {
        N newInnerNode(final String p0);
    }
}
