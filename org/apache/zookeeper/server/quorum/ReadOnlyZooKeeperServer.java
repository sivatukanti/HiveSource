// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.jmx.MBeanRegistry;
import org.apache.zookeeper.server.DataTreeBean;
import org.apache.zookeeper.server.ZooKeeperServerBean;
import org.apache.zookeeper.server.RequestProcessor;
import org.apache.zookeeper.server.PrepRequestProcessor;
import org.apache.zookeeper.server.FinalRequestProcessor;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;

public class ReadOnlyZooKeeperServer extends QuorumZooKeeperServer
{
    private volatile boolean shutdown;
    
    ReadOnlyZooKeeperServer(final FileTxnSnapLog logFactory, final QuorumPeer self, final DataTreeBuilder treeBuilder, final ZKDatabase zkDb) {
        super(logFactory, self.tickTime, self.minSessionTimeout, self.maxSessionTimeout, treeBuilder, zkDb, self);
        this.shutdown = false;
    }
    
    @Override
    protected void setupRequestProcessors() {
        final RequestProcessor finalProcessor = new FinalRequestProcessor(this);
        final RequestProcessor prepProcessor = new PrepRequestProcessor(this, finalProcessor);
        ((PrepRequestProcessor)prepProcessor).start();
        this.firstProcessor = new ReadOnlyRequestProcessor(this, prepProcessor);
        ((ReadOnlyRequestProcessor)this.firstProcessor).start();
    }
    
    @Override
    public synchronized void startup() {
        if (this.shutdown) {
            ReadOnlyZooKeeperServer.LOG.warn("Not starting Read-only server as startup follows shutdown!");
            return;
        }
        this.registerJMX(new ReadOnlyBean(this), this.self.jmxLocalPeerBean);
        super.startup();
        this.self.cnxnFactory.setZooKeeperServer(this);
        ReadOnlyZooKeeperServer.LOG.info("Read-only server started");
    }
    
    @Override
    protected void registerJMX() {
        try {
            this.jmxDataTreeBean = new DataTreeBean(this.getZKDatabase().getDataTree());
            MBeanRegistry.getInstance().register(this.jmxDataTreeBean, this.jmxServerBean);
        }
        catch (Exception e) {
            ReadOnlyZooKeeperServer.LOG.warn("Failed to register with JMX", e);
            this.jmxDataTreeBean = null;
        }
    }
    
    public void registerJMX(final ZooKeeperServerBean serverBean, final LocalPeerBean localPeerBean) {
        try {
            this.jmxServerBean = serverBean;
            MBeanRegistry.getInstance().register(serverBean, localPeerBean);
        }
        catch (Exception e) {
            ReadOnlyZooKeeperServer.LOG.warn("Failed to register with JMX", e);
            this.jmxServerBean = null;
        }
    }
    
    @Override
    protected void setState(final State state) {
        this.state = state;
    }
    
    @Override
    protected void unregisterJMX() {
        try {
            if (this.jmxDataTreeBean != null) {
                MBeanRegistry.getInstance().unregister(this.jmxDataTreeBean);
            }
        }
        catch (Exception e) {
            ReadOnlyZooKeeperServer.LOG.warn("Failed to unregister with JMX", e);
        }
        this.jmxDataTreeBean = null;
    }
    
    protected void unregisterJMX(final ZooKeeperServer zks) {
        try {
            if (this.jmxServerBean != null) {
                MBeanRegistry.getInstance().unregister(this.jmxServerBean);
            }
        }
        catch (Exception e) {
            ReadOnlyZooKeeperServer.LOG.warn("Failed to unregister with JMX", e);
        }
        this.jmxServerBean = null;
    }
    
    @Override
    public String getState() {
        return "read-only";
    }
    
    @Override
    public long getServerId() {
        return this.self.getId();
    }
    
    @Override
    public synchronized void shutdown() {
        if (!this.canShutdown()) {
            ReadOnlyZooKeeperServer.LOG.debug("ZooKeeper server is not running, so not proceeding to shutdown!");
            return;
        }
        this.shutdown = true;
        this.unregisterJMX(this);
        this.self.cnxnFactory.setZooKeeperServer(null);
        this.self.cnxnFactory.closeAll();
        super.shutdown();
    }
}
