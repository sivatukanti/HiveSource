// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.server.ZooTrace;
import org.apache.zookeeper.server.Request;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.apache.zookeeper.server.RequestProcessor;
import org.apache.zookeeper.server.ZooKeeperCriticalThread;

public class FollowerRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor
{
    private static final Logger LOG;
    FollowerZooKeeperServer zks;
    RequestProcessor nextProcessor;
    LinkedBlockingQueue<Request> queuedRequests;
    boolean finished;
    
    public FollowerRequestProcessor(final FollowerZooKeeperServer zks, final RequestProcessor nextProcessor) {
        super("FollowerRequestProcessor:" + zks.getServerId(), zks.getZooKeeperServerListener());
        this.queuedRequests = new LinkedBlockingQueue<Request>();
        this.finished = false;
        this.zks = zks;
        this.nextProcessor = nextProcessor;
    }
    
    @Override
    public void run() {
        try {
            while (!this.finished) {
                final Request request = this.queuedRequests.take();
                if (FollowerRequestProcessor.LOG.isTraceEnabled()) {
                    ZooTrace.logRequest(FollowerRequestProcessor.LOG, 2L, 'F', request, "");
                }
                if (request == Request.requestOfDeath) {
                    break;
                }
                this.nextProcessor.processRequest(request);
                switch (request.type) {
                    case 9: {
                        this.zks.pendingSyncs.add(request);
                        this.zks.getFollower().request(request);
                        continue;
                    }
                    case -11:
                    case -10:
                    case 1:
                    case 2:
                    case 5:
                    case 7:
                    case 14: {
                        this.zks.getFollower().request(request);
                        continue;
                    }
                }
            }
        }
        catch (Exception e) {
            this.handleException(this.getName(), e);
        }
        FollowerRequestProcessor.LOG.info("FollowerRequestProcessor exited loop!");
    }
    
    @Override
    public void processRequest(final Request request) {
        if (!this.finished) {
            this.queuedRequests.add(request);
        }
    }
    
    @Override
    public void shutdown() {
        FollowerRequestProcessor.LOG.info("Shutting down");
        this.finished = true;
        this.queuedRequests.clear();
        this.queuedRequests.add(Request.requestOfDeath);
        this.nextProcessor.shutdown();
    }
    
    static {
        LOG = LoggerFactory.getLogger(FollowerRequestProcessor.class);
    }
}
