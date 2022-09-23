// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.server.ZooKeeperServerBean;
import org.apache.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.jmx.MBeanRegistry;
import org.apache.zookeeper.server.DataTreeBean;
import org.apache.zookeeper.server.ServerCnxn;
import org.apache.zookeeper.server.SessionTracker;
import java.util.HashMap;
import java.io.IOException;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;

public abstract class LearnerZooKeeperServer extends QuorumZooKeeperServer
{
    public LearnerZooKeeperServer(final FileTxnSnapLog logFactory, final int tickTime, final int minSessionTimeout, final int maxSessionTimeout, final DataTreeBuilder treeBuilder, final ZKDatabase zkDb, final QuorumPeer self) throws IOException {
        super(logFactory, tickTime, minSessionTimeout, maxSessionTimeout, treeBuilder, zkDb, self);
    }
    
    public abstract Learner getLearner();
    
    protected HashMap<Long, Integer> getTouchSnapshot() {
        if (this.sessionTracker != null) {
            return ((LearnerSessionTracker)this.sessionTracker).snapshot();
        }
        return new HashMap<Long, Integer>();
    }
    
    @Override
    public long getServerId() {
        return this.self.getId();
    }
    
    public void createSessionTracker() {
        this.sessionTracker = new LearnerSessionTracker(this, this.getZKDatabase().getSessionWithTimeOuts(), this.self.getId(), this.getZooKeeperServerListener());
    }
    
    @Override
    protected void startSessionTracker() {
    }
    
    @Override
    protected void revalidateSession(final ServerCnxn cnxn, final long sessionId, final int sessionTimeout) throws IOException {
        this.getLearner().validateSession(cnxn, sessionId, sessionTimeout);
    }
    
    @Override
    protected void registerJMX() {
        try {
            this.jmxDataTreeBean = new DataTreeBean(this.getZKDatabase().getDataTree());
            MBeanRegistry.getInstance().register(this.jmxDataTreeBean, this.jmxServerBean);
        }
        catch (Exception e) {
            LearnerZooKeeperServer.LOG.warn("Failed to register with JMX", e);
            this.jmxDataTreeBean = null;
        }
    }
    
    public void registerJMX(final ZooKeeperServerBean serverBean, final LocalPeerBean localPeerBean) {
        if (this.self.jmxLeaderElectionBean != null) {
            try {
                MBeanRegistry.getInstance().unregister(this.self.jmxLeaderElectionBean);
            }
            catch (Exception e) {
                LearnerZooKeeperServer.LOG.warn("Failed to register with JMX", e);
            }
            this.self.jmxLeaderElectionBean = null;
        }
        try {
            this.jmxServerBean = serverBean;
            MBeanRegistry.getInstance().register(serverBean, localPeerBean);
        }
        catch (Exception e) {
            LearnerZooKeeperServer.LOG.warn("Failed to register with JMX", e);
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
            LearnerZooKeeperServer.LOG.warn("Failed to unregister with JMX", e);
        }
        this.jmxDataTreeBean = null;
    }
    
    protected void unregisterJMX(final Learner peer) {
        try {
            if (this.jmxServerBean != null) {
                MBeanRegistry.getInstance().unregister(this.jmxServerBean);
            }
        }
        catch (Exception e) {
            LearnerZooKeeperServer.LOG.warn("Failed to unregister with JMX", e);
        }
        this.jmxServerBean = null;
    }
}
