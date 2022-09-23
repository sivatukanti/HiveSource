// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.ServerCnxn;
import org.apache.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.jmx.MBeanRegistry;
import org.apache.zookeeper.server.DataTreeBean;
import org.apache.zookeeper.server.SessionTracker;
import org.apache.zookeeper.server.SessionTrackerImpl;
import org.apache.zookeeper.server.PrepRequestProcessor;
import org.apache.zookeeper.server.RequestProcessor;
import org.apache.zookeeper.server.FinalRequestProcessor;
import java.io.IOException;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;

public class LeaderZooKeeperServer extends QuorumZooKeeperServer
{
    CommitProcessor commitProcessor;
    
    LeaderZooKeeperServer(final FileTxnSnapLog logFactory, final QuorumPeer self, final DataTreeBuilder treeBuilder, final ZKDatabase zkDb) throws IOException {
        super(logFactory, self.tickTime, self.minSessionTimeout, self.maxSessionTimeout, treeBuilder, zkDb, self);
    }
    
    public Leader getLeader() {
        return this.self.leader;
    }
    
    @Override
    protected void setupRequestProcessors() {
        final RequestProcessor finalProcessor = new FinalRequestProcessor(this);
        final RequestProcessor toBeAppliedProcessor = new Leader.ToBeAppliedRequestProcessor(finalProcessor, this.getLeader().toBeApplied);
        (this.commitProcessor = new CommitProcessor(toBeAppliedProcessor, Long.toString(this.getServerId()), false, this.getZooKeeperServerListener())).start();
        final ProposalRequestProcessor proposalProcessor = new ProposalRequestProcessor(this, this.commitProcessor);
        proposalProcessor.initialize();
        this.firstProcessor = new PrepRequestProcessor(this, proposalProcessor);
        ((PrepRequestProcessor)this.firstProcessor).start();
    }
    
    @Override
    public int getGlobalOutstandingLimit() {
        return super.getGlobalOutstandingLimit() / (this.self.getQuorumSize() - 1);
    }
    
    public void createSessionTracker() {
        this.sessionTracker = new SessionTrackerImpl(this, this.getZKDatabase().getSessionWithTimeOuts(), this.tickTime, this.self.getId(), this.getZooKeeperServerListener());
    }
    
    @Override
    protected void startSessionTracker() {
        ((SessionTrackerImpl)this.sessionTracker).start();
    }
    
    public boolean touch(final long sess, final int to) {
        return this.sessionTracker.touchSession(sess, to);
    }
    
    @Override
    protected void registerJMX() {
        try {
            this.jmxDataTreeBean = new DataTreeBean(this.getZKDatabase().getDataTree());
            MBeanRegistry.getInstance().register(this.jmxDataTreeBean, this.jmxServerBean);
        }
        catch (Exception e) {
            LeaderZooKeeperServer.LOG.warn("Failed to register with JMX", e);
            this.jmxDataTreeBean = null;
        }
    }
    
    public void registerJMX(final LeaderBean leaderBean, final LocalPeerBean localPeerBean) {
        if (this.self.jmxLeaderElectionBean != null) {
            try {
                MBeanRegistry.getInstance().unregister(this.self.jmxLeaderElectionBean);
            }
            catch (Exception e) {
                LeaderZooKeeperServer.LOG.warn("Failed to register with JMX", e);
            }
            this.self.jmxLeaderElectionBean = null;
        }
        try {
            this.jmxServerBean = leaderBean;
            MBeanRegistry.getInstance().register(leaderBean, localPeerBean);
        }
        catch (Exception e) {
            LeaderZooKeeperServer.LOG.warn("Failed to register with JMX", e);
            this.jmxServerBean = null;
        }
    }
    
    @Override
    protected void unregisterJMX() {
        try {
            if (this.jmxDataTreeBean != null) {
                MBeanRegistry.getInstance().unregister(this.jmxDataTreeBean);
            }
        }
        catch (Exception e) {
            LeaderZooKeeperServer.LOG.warn("Failed to unregister with JMX", e);
        }
        this.jmxDataTreeBean = null;
    }
    
    protected void unregisterJMX(final Leader leader) {
        try {
            if (this.jmxServerBean != null) {
                MBeanRegistry.getInstance().unregister(this.jmxServerBean);
            }
        }
        catch (Exception e) {
            LeaderZooKeeperServer.LOG.warn("Failed to unregister with JMX", e);
        }
        this.jmxServerBean = null;
    }
    
    @Override
    public String getState() {
        return "leader";
    }
    
    @Override
    public long getServerId() {
        return this.self.getId();
    }
    
    @Override
    protected void revalidateSession(final ServerCnxn cnxn, final long sessionId, final int sessionTimeout) throws IOException {
        super.revalidateSession(cnxn, sessionId, sessionTimeout);
        try {
            this.setOwner(sessionId, ServerCnxn.me);
        }
        catch (KeeperException.SessionExpiredException ex) {}
    }
}
