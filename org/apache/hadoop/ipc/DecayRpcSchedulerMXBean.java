// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

public interface DecayRpcSchedulerMXBean
{
    String getSchedulingDecisionSummary();
    
    String getCallVolumeSummary();
    
    int getUniqueIdentityCount();
    
    long getTotalCallVolume();
    
    double[] getAverageResponseTime();
    
    long[] getResponseTimeCountInLastWindow();
}
