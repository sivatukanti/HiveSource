// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.server.ZooKeeperServerMXBean;

public interface FollowerMXBean extends ZooKeeperServerMXBean
{
    String getQuorumAddress();
    
    String getLastQueuedZxid();
    
    int getPendingRevalidationCount();
    
    long getElectionTimeTaken();
}
