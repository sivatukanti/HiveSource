// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

public class LocalPeerBean extends ServerBean implements LocalPeerMXBean
{
    private final QuorumPeer peer;
    
    public LocalPeerBean(final QuorumPeer peer) {
        this.peer = peer;
    }
    
    @Override
    public String getName() {
        return "replica." + this.peer.getId();
    }
    
    @Override
    public boolean isHidden() {
        return false;
    }
    
    @Override
    public int getTickTime() {
        return this.peer.getTickTime();
    }
    
    @Override
    public int getMaxClientCnxnsPerHost() {
        return this.peer.getMaxClientCnxnsPerHost();
    }
    
    @Override
    public int getMinSessionTimeout() {
        return this.peer.getMinSessionTimeout();
    }
    
    @Override
    public int getMaxSessionTimeout() {
        return this.peer.getMaxSessionTimeout();
    }
    
    @Override
    public int getInitLimit() {
        return this.peer.getInitLimit();
    }
    
    @Override
    public int getSyncLimit() {
        return this.peer.getSyncLimit();
    }
    
    @Override
    public int getTick() {
        return this.peer.getTick();
    }
    
    @Override
    public String getState() {
        return this.peer.getServerState();
    }
    
    @Override
    public String getQuorumAddress() {
        return this.peer.getQuorumAddress().toString();
    }
    
    @Override
    public int getElectionType() {
        return this.peer.getElectionType();
    }
}
