// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.jmx.ZKMBeanInfo;

public class RemotePeerBean implements RemotePeerMXBean, ZKMBeanInfo
{
    private QuorumPeer.QuorumServer peer;
    
    public RemotePeerBean(final QuorumPeer.QuorumServer peer) {
        this.peer = peer;
    }
    
    @Override
    public String getName() {
        return "replica." + this.peer.id;
    }
    
    @Override
    public boolean isHidden() {
        return false;
    }
    
    @Override
    public String getQuorumAddress() {
        return this.peer.addr.getHostName() + ":" + this.peer.addr.getPort();
    }
}
