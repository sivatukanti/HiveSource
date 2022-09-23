// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

public interface Election
{
    Vote lookForLeader() throws InterruptedException;
    
    void shutdown();
}
