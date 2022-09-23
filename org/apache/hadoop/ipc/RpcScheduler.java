// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

public interface RpcScheduler
{
    int getPriorityLevel(final Schedulable p0);
    
    boolean shouldBackOff(final Schedulable p0);
    
    void addResponseTime(final String p0, final int p1, final int p2, final int p3);
    
    void stop();
}
