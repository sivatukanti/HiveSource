// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.data.Id;
import java.util.List;
import java.nio.ByteBuffer;
import org.apache.zookeeper.server.ServerCnxn;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import org.apache.zookeeper.server.RequestProcessor;
import org.apache.zookeeper.server.FinalRequestProcessor;
import java.io.IOException;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.zookeeper.server.Request;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.zookeeper.server.SyncRequestProcessor;
import org.slf4j.Logger;

public class FollowerZooKeeperServer extends LearnerZooKeeperServer
{
    private static final Logger LOG;
    CommitProcessor commitProcessor;
    SyncRequestProcessor syncProcessor;
    ConcurrentLinkedQueue<Request> pendingSyncs;
    LinkedBlockingQueue<Request> pendingTxns;
    
    FollowerZooKeeperServer(final FileTxnSnapLog logFactory, final QuorumPeer self, final DataTreeBuilder treeBuilder, final ZKDatabase zkDb) throws IOException {
        super(logFactory, self.tickTime, self.minSessionTimeout, self.maxSessionTimeout, treeBuilder, zkDb, self);
        this.pendingTxns = new LinkedBlockingQueue<Request>();
        this.pendingSyncs = new ConcurrentLinkedQueue<Request>();
    }
    
    public Follower getFollower() {
        return this.self.follower;
    }
    
    @Override
    protected void setupRequestProcessors() {
        final RequestProcessor finalProcessor = new FinalRequestProcessor(this);
        (this.commitProcessor = new CommitProcessor(finalProcessor, Long.toString(this.getServerId()), true, this.getZooKeeperServerListener())).start();
        this.firstProcessor = new FollowerRequestProcessor(this, this.commitProcessor);
        ((FollowerRequestProcessor)this.firstProcessor).start();
        (this.syncProcessor = new SyncRequestProcessor(this, new SendAckRequestProcessor(this.getFollower()))).start();
    }
    
    public void logRequest(final TxnHeader hdr, final Record txn) {
        final Request request = new Request(null, hdr.getClientId(), hdr.getCxid(), hdr.getType(), null, null);
        request.hdr = hdr;
        request.txn = txn;
        request.zxid = hdr.getZxid();
        if ((request.zxid & 0xFFFFFFFFL) != 0x0L) {
            this.pendingTxns.add(request);
        }
        this.syncProcessor.processRequest(request);
    }
    
    public void commit(final long zxid) {
        if (this.pendingTxns.size() == 0) {
            FollowerZooKeeperServer.LOG.warn("Committing " + Long.toHexString(zxid) + " without seeing txn");
            return;
        }
        final long firstElementZxid = this.pendingTxns.element().zxid;
        if (firstElementZxid != zxid) {
            FollowerZooKeeperServer.LOG.error("Committing zxid 0x" + Long.toHexString(zxid) + " but next pending txn 0x" + Long.toHexString(firstElementZxid));
            System.exit(12);
        }
        final Request request = this.pendingTxns.remove();
        this.commitProcessor.commit(request);
    }
    
    public synchronized void sync() {
        if (this.pendingSyncs.size() == 0) {
            FollowerZooKeeperServer.LOG.warn("Not expecting a sync.");
            return;
        }
        final Request r = this.pendingSyncs.remove();
        this.commitProcessor.commit(r);
    }
    
    @Override
    public int getGlobalOutstandingLimit() {
        return super.getGlobalOutstandingLimit() / (this.self.getQuorumSize() - 1);
    }
    
    @Override
    public void shutdown() {
        FollowerZooKeeperServer.LOG.info("Shutting down");
        try {
            super.shutdown();
        }
        catch (Exception e) {
            FollowerZooKeeperServer.LOG.warn("Ignoring unexpected exception during shutdown", e);
        }
        try {
            if (this.syncProcessor != null) {
                this.syncProcessor.shutdown();
            }
        }
        catch (Exception e) {
            FollowerZooKeeperServer.LOG.warn("Ignoring unexpected exception in syncprocessor shutdown", e);
        }
    }
    
    @Override
    public String getState() {
        return "follower";
    }
    
    @Override
    public Learner getLearner() {
        return this.getFollower();
    }
    
    static {
        LOG = LoggerFactory.getLogger(FollowerZooKeeperServer.class);
    }
}
