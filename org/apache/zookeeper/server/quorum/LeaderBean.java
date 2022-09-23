// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import java.util.Iterator;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.ZooKeeperServerBean;

public class LeaderBean extends ZooKeeperServerBean implements LeaderMXBean
{
    private final Leader leader;
    
    public LeaderBean(final Leader leader, final ZooKeeperServer zks) {
        super(zks);
        this.leader = leader;
    }
    
    @Override
    public String getName() {
        return "Leader";
    }
    
    @Override
    public String getCurrentZxid() {
        return "0x" + Long.toHexString(this.zks.getZxid());
    }
    
    @Override
    public String followerInfo() {
        final StringBuilder sb = new StringBuilder();
        for (final LearnerHandler handler : this.leader.getLearners()) {
            sb.append(handler.toString()).append("\n");
        }
        return sb.toString();
    }
    
    @Override
    public long getElectionTimeTaken() {
        return this.leader.self.getElectionTimeTaken();
    }
    
    @Override
    public int getLastProposalSize() {
        return this.leader.getProposalStats().getLastProposalSize();
    }
    
    @Override
    public int getMinProposalSize() {
        return this.leader.getProposalStats().getMinProposalSize();
    }
    
    @Override
    public int getMaxProposalSize() {
        return this.leader.getProposalStats().getMaxProposalSize();
    }
    
    @Override
    public void resetProposalStatistics() {
        this.leader.getProposalStats().reset();
    }
}
