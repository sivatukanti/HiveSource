// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

public interface FairCallQueueMXBean
{
    int[] getQueueSizes();
    
    long[] getOverflowedCalls();
    
    int getRevision();
}
