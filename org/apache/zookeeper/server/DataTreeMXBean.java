// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

public interface DataTreeMXBean
{
    int getNodeCount();
    
    String getLastZxid();
    
    int getWatchCount();
    
    long approximateDataSize();
    
    int countEphemerals();
}
