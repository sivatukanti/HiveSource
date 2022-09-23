// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.server.ZooKeeperServerMXBean;

public interface LeaderMXBean extends ZooKeeperServerMXBean
{
    String getCurrentZxid();
    
    String followerInfo();
    
    long getElectionTimeTaken();
    
    int getLastProposalSize();
    
    int getMinProposalSize();
    
    int getMaxProposalSize();
    
    void resetProposalStatistics();
}
