// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.ZooKeeperServerBean;

public class FollowerBean extends ZooKeeperServerBean implements FollowerMXBean
{
    private final Follower follower;
    
    public FollowerBean(final Follower follower, final ZooKeeperServer zks) {
        super(zks);
        this.follower = follower;
    }
    
    @Override
    public String getName() {
        return "Follower";
    }
    
    @Override
    public String getQuorumAddress() {
        return this.follower.sock.toString();
    }
    
    @Override
    public String getLastQueuedZxid() {
        return "0x" + Long.toHexString(this.follower.getLastQueued());
    }
    
    @Override
    public int getPendingRevalidationCount() {
        return this.follower.getPendingRevalidationsCount();
    }
    
    @Override
    public long getElectionTimeTaken() {
        return this.follower.self.getElectionTimeTaken();
    }
}
