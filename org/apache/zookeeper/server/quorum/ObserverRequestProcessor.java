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

public class ObserverRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor
{
    private static final Logger LOG;
    ObserverZooKeeperServer zks;
    RequestProcessor nextProcessor;
    LinkedBlockingQueue<Request> queuedRequests;
    boolean finished;
    
    public ObserverRequestProcessor(final ObserverZooKeeperServer zks, final RequestProcessor nextProcessor) {
        super("ObserverRequestProcessor:" + zks.getServerId(), zks.getZooKeeperServerListener());
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
                if (ObserverRequestProcessor.LOG.isTraceEnabled()) {
                    ZooTrace.logRequest(ObserverRequestProcessor.LOG, 2L, 'F', request, "");
                }
                if (request == Request.requestOfDeath) {
                    break;
                }
                this.nextProcessor.processRequest(request);
                switch (request.type) {
                    case 9: {
                        this.zks.pendingSyncs.add(request);
                        this.zks.getObserver().request(request);
                        continue;
                    }
                    case -11:
                    case -10:
                    case 1:
                    case 2:
                    case 5:
                    case 7:
                    case 14: {
                        this.zks.getObserver().request(request);
                        continue;
                    }
                }
            }
        }
        catch (Exception e) {
            this.handleException(this.getName(), e);
        }
        ObserverRequestProcessor.LOG.info("ObserverRequestProcessor exited loop!");
    }
    
    @Override
    public void processRequest(final Request request) {
        if (!this.finished) {
            this.queuedRequests.add(request);
        }
    }
    
    @Override
    public void shutdown() {
        ObserverRequestProcessor.LOG.info("Shutting down");
        this.finished = true;
        this.queuedRequests.clear();
        this.queuedRequests.add(Request.requestOfDeath);
        this.nextProcessor.shutdown();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ObserverRequestProcessor.class);
    }
}
