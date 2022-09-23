// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.conf.Configuration;

public class DefaultRpcScheduler implements RpcScheduler
{
    @Override
    public int getPriorityLevel(final Schedulable obj) {
        return 0;
    }
    
    @Override
    public boolean shouldBackOff(final Schedulable obj) {
        return false;
    }
    
    @Override
    public void addResponseTime(final String name, final int priorityLevel, final int queueTime, final int processingTime) {
    }
    
    public DefaultRpcScheduler(final int priorityLevels, final String namespace, final Configuration conf) {
    }
    
    @Override
    public void stop() {
    }
}
