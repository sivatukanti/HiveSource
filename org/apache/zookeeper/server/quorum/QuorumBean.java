// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.jmx.ZKMBeanInfo;

public class QuorumBean implements QuorumMXBean, ZKMBeanInfo
{
    private final QuorumPeer peer;
    private final String name;
    
    public QuorumBean(final QuorumPeer peer) {
        this.peer = peer;
        this.name = "ReplicatedServer_id" + peer.getMyid();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean isHidden() {
        return false;
    }
    
    @Override
    public int getQuorumSize() {
        return this.peer.getQuorumSize();
    }
}
