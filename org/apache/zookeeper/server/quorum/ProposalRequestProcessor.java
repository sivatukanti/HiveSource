// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.server.Request;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.SyncRequestProcessor;
import org.slf4j.Logger;
import org.apache.zookeeper.server.RequestProcessor;

public class ProposalRequestProcessor implements RequestProcessor
{
    private static final Logger LOG;
    LeaderZooKeeperServer zks;
    RequestProcessor nextProcessor;
    SyncRequestProcessor syncProcessor;
    
    public ProposalRequestProcessor(final LeaderZooKeeperServer zks, final RequestProcessor nextProcessor) {
        this.zks = zks;
        this.nextProcessor = nextProcessor;
        final AckRequestProcessor ackProcessor = new AckRequestProcessor(zks.getLeader());
        this.syncProcessor = new SyncRequestProcessor(zks, ackProcessor);
    }
    
    public void initialize() {
        this.syncProcessor.start();
    }
    
    @Override
    public void processRequest(final Request request) throws RequestProcessorException {
        if (request instanceof LearnerSyncRequest) {
            this.zks.getLeader().processSync((LearnerSyncRequest)request);
        }
        else {
            this.nextProcessor.processRequest(request);
            if (request.hdr != null) {
                try {
                    this.zks.getLeader().propose(request);
                }
                catch (Leader.XidRolloverException e) {
                    throw new RequestProcessorException(e.getMessage(), e);
                }
                this.syncProcessor.processRequest(request);
            }
        }
    }
    
    @Override
    public void shutdown() {
        ProposalRequestProcessor.LOG.info("Shutting down");
        this.nextProcessor.shutdown();
        this.syncProcessor.shutdown();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ProposalRequestProcessor.class);
    }
}
