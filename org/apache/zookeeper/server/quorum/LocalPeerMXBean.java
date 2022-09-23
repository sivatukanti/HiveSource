// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

public interface LocalPeerMXBean extends ServerMXBean
{
    int getTickTime();
    
    int getMaxClientCnxnsPerHost();
    
    int getMinSessionTimeout();
    
    int getMaxSessionTimeout();
    
    int getInitLimit();
    
    int getSyncLimit();
    
    int getTick();
    
    String getState();
    
    String getQuorumAddress();
    
    int getElectionType();
}
