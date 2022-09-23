// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.ZooTrace;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.Request;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.apache.zookeeper.server.RequestProcessor;
import org.apache.zookeeper.server.ZooKeeperCriticalThread;

public class ReadOnlyRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor
{
    private static final Logger LOG;
    private LinkedBlockingQueue<Request> queuedRequests;
    private boolean finished;
    private RequestProcessor nextProcessor;
    private ZooKeeperServer zks;
    
    public ReadOnlyRequestProcessor(final ZooKeeperServer zks, final RequestProcessor nextProcessor) {
        super("ReadOnlyRequestProcessor:" + zks.getServerId(), zks.getZooKeeperServerListener());
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
                long traceMask = 2L;
                if (request.type == 11) {
                    traceMask = 8L;
                }
                if (ReadOnlyRequestProcessor.LOG.isTraceEnabled()) {
                    ZooTrace.logRequest(ReadOnlyRequestProcessor.LOG, traceMask, 'R', request, "");
                }
                if (Request.requestOfDeath == request) {
                    break;
                }
                switch (request.type) {
                    case 1:
                    case 2:
                    case 5:
                    case 7:
                    case 9:
                    case 13:
                    case 14: {
                        final ReplyHeader hdr = new ReplyHeader(request.cxid, this.zks.getZKDatabase().getDataTreeLastProcessedZxid(), KeeperException.Code.NOTREADONLY.intValue());
                        try {
                            request.cnxn.sendResponse(hdr, null, null);
                        }
                        catch (IOException e) {
                            ReadOnlyRequestProcessor.LOG.error("IO exception while sending response", e);
                        }
                        continue;
                    }
                    default: {
                        if (this.nextProcessor == null) {
                            continue;
                        }
                        this.nextProcessor.processRequest(request);
                        continue;
                    }
                }
            }
        }
        catch (RequestProcessorException e2) {
            if (e2.getCause() instanceof Leader.XidRolloverException) {
                ReadOnlyRequestProcessor.LOG.info(e2.getCause().getMessage());
            }
            this.handleException(this.getName(), e2);
        }
        catch (Exception e3) {
            this.handleException(this.getName(), e3);
        }
        ReadOnlyRequestProcessor.LOG.info("ReadOnlyRequestProcessor exited loop!");
    }
    
    @Override
    public void processRequest(final Request request) {
        if (!this.finished) {
            this.queuedRequests.add(request);
        }
    }
    
    @Override
    public void shutdown() {
        this.finished = true;
        this.queuedRequests.clear();
        this.queuedRequests.add(Request.requestOfDeath);
        this.nextProcessor.shutdown();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ReadOnlyRequestProcessor.class);
    }
}
