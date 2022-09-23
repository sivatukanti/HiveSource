// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.server.RequestProcessor;
import org.apache.zookeeper.server.FinalRequestProcessor;
import java.io.IOException;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.Request;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.zookeeper.server.SyncRequestProcessor;
import org.slf4j.Logger;

public class ObserverZooKeeperServer extends LearnerZooKeeperServer
{
    private static final Logger LOG;
    private boolean syncRequestProcessorEnabled;
    private CommitProcessor commitProcessor;
    private SyncRequestProcessor syncProcessor;
    ConcurrentLinkedQueue<Request> pendingSyncs;
    
    ObserverZooKeeperServer(final FileTxnSnapLog logFactory, final QuorumPeer self, final DataTreeBuilder treeBuilder, final ZKDatabase zkDb) throws IOException {
        super(logFactory, self.tickTime, self.minSessionTimeout, self.maxSessionTimeout, treeBuilder, zkDb, self);
        this.syncRequestProcessorEnabled = this.self.getSyncEnabled();
        this.pendingSyncs = new ConcurrentLinkedQueue<Request>();
        ObserverZooKeeperServer.LOG.info("syncEnabled =" + this.syncRequestProcessorEnabled);
    }
    
    public Observer getObserver() {
        return this.self.observer;
    }
    
    @Override
    public Learner getLearner() {
        return this.self.observer;
    }
    
    public void commitRequest(final Request request) {
        if (this.syncRequestProcessorEnabled) {
            this.syncProcessor.processRequest(request);
        }
        this.commitProcessor.commit(request);
    }
    
    @Override
    protected void setupRequestProcessors() {
        final RequestProcessor finalProcessor = new FinalRequestProcessor(this);
        (this.commitProcessor = new CommitProcessor(finalProcessor, Long.toString(this.getServerId()), true, this.getZooKeeperServerListener())).start();
        this.firstProcessor = new ObserverRequestProcessor(this, this.commitProcessor);
        ((ObserverRequestProcessor)this.firstProcessor).start();
        if (this.syncRequestProcessorEnabled) {
            (this.syncProcessor = new SyncRequestProcessor(this, null)).start();
        }
    }
    
    public synchronized void sync() {
        if (this.pendingSyncs.size() == 0) {
            ObserverZooKeeperServer.LOG.warn("Not expecting a sync.");
            return;
        }
        final Request r = this.pendingSyncs.remove();
        this.commitProcessor.commit(r);
    }
    
    @Override
    public String getState() {
        return "observer";
    }
    
    @Override
    public synchronized void shutdown() {
        if (!this.canShutdown()) {
            ObserverZooKeeperServer.LOG.debug("ZooKeeper server is not running, so not proceeding to shutdown!");
            return;
        }
        super.shutdown();
        if (this.syncRequestProcessorEnabled && this.syncProcessor != null) {
            this.syncProcessor.shutdown();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ObserverZooKeeperServer.class);
    }
}
