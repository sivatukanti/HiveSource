// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

public interface ZooKeeperServerMXBean
{
    String getClientPort();
    
    String getVersion();
    
    String getStartTime();
    
    long getMinRequestLatency();
    
    long getAvgRequestLatency();
    
    long getMaxRequestLatency();
    
    long getPacketsReceived();
    
    long getPacketsSent();
    
    long getFsyncThresholdExceedCount();
    
    long getOutstandingRequests();
    
    int getTickTime();
    
    void setTickTime(final int p0);
    
    int getMaxClientCnxnsPerHost();
    
    void setMaxClientCnxnsPerHost(final int p0);
    
    int getMinSessionTimeout();
    
    void setMinSessionTimeout(final int p0);
    
    int getMaxSessionTimeout();
    
    void setMaxSessionTimeout(final int p0);
    
    void resetStatistics();
    
    void resetLatency();
    
    void resetMaxLatency();
    
    void resetFsyncThresholdExceedCount();
    
    long getNumAliveConnections();
    
    int getJuteMaxBufferSize();
}
