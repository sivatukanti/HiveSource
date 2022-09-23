// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

public interface Recoverable
{
    void recover(final RMStateStore.RMState p0) throws Exception;
}
