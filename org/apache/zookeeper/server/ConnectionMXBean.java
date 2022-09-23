// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

public interface ConnectionMXBean
{
    String getSourceIP();
    
    String getSessionId();
    
    String getStartedTime();
    
    String[] getEphemeralNodes();
    
    long getPacketsReceived();
    
    long getPacketsSent();
    
    long getOutstandingRequests();
    
    int getSessionTimeout();
    
    void terminateSession();
    
    void terminateConnection();
    
    long getMinLatency();
    
    long getAvgLatency();
    
    long getMaxLatency();
    
    String getLastOperation();
    
    String getLastCxid();
    
    String getLastZxid();
    
    String getLastResponseTime();
    
    long getLastLatency();
    
    void resetCounters();
}
