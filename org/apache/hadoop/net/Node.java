// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public interface Node
{
    String getNetworkLocation();
    
    void setNetworkLocation(final String p0);
    
    String getName();
    
    Node getParent();
    
    void setParent(final Node p0);
    
    int getLevel();
    
    void setLevel(final int p0);
}
