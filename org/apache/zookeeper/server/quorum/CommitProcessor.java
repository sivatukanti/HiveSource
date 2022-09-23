// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.server.ZooKeeperServerListener;
import java.util.ArrayList;
import org.apache.zookeeper.server.Request;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.apache.zookeeper.server.RequestProcessor;
import org.apache.zookeeper.server.ZooKeeperCriticalThread;

public class CommitProcessor extends ZooKeeperCriticalThread implements RequestProcessor
{
    private static final Logger LOG;
    LinkedList<Request> queuedRequests;
    LinkedList<Request> committedRequests;
    RequestProcessor nextProcessor;
    ArrayList<Request> toProcess;
    boolean matchSyncs;
    volatile boolean finished;
    
    public CommitProcessor(final RequestProcessor nextProcessor, final String id, final boolean matchSyncs, final ZooKeeperServerListener listener) {
        super("CommitProcessor:" + id, listener);
        this.queuedRequests = new LinkedList<Request>();
        this.committedRequests = new LinkedList<Request>();
        this.toProcess = new ArrayList<Request>();
        this.finished = false;
        this.nextProcessor = nextProcessor;
        this.matchSyncs = matchSyncs;
    }
    
    @Override
    public void run() {
        try {
            Request nextPending = null;
            while (!this.finished) {
                for (int len = this.toProcess.size(), i = 0; i < len; ++i) {
                    this.nextProcessor.processRequest(this.toProcess.get(i));
                }
                this.toProcess.clear();
                synchronized (this) {
                    if ((this.queuedRequests.size() == 0 || nextPending != null) && this.committedRequests.size() == 0) {
                        this.wait();
                        continue;
                    }
                    if ((this.queuedRequests.size() == 0 || nextPending != null) && this.committedRequests.size() > 0) {
                        final Request r = this.committedRequests.remove();
                        if (nextPending != null && nextPending.sessionId == r.sessionId && nextPending.cxid == r.cxid) {
                            nextPending.hdr = r.hdr;
                            nextPending.txn = r.txn;
                            nextPending.zxid = r.zxid;
                            this.toProcess.add(nextPending);
                            nextPending = null;
                        }
                        else {
                            this.toProcess.add(r);
                        }
                    }
                }
                if (nextPending != null) {
                    continue;
                }
                synchronized (this) {
                    while (nextPending == null && this.queuedRequests.size() > 0) {
                        final Request request = this.queuedRequests.remove();
                        switch (request.type) {
                            case -11:
                            case -10:
                            case 1:
                            case 2:
                            case 5:
                            case 7:
                            case 14: {
                                nextPending = request;
                                continue;
                            }
                            case 9: {
                                if (this.matchSyncs) {
                                    nextPending = request;
                                    continue;
                                }
                                this.toProcess.add(request);
                                continue;
                            }
                            default: {
                                this.toProcess.add(request);
                                continue;
                            }
                        }
                    }
                }
            }
        }
        catch (InterruptedException e) {
            CommitProcessor.LOG.warn("Interrupted exception while waiting", e);
        }
        catch (Throwable e2) {
            CommitProcessor.LOG.error("Unexpected exception causing CommitProcessor to exit", e2);
        }
        CommitProcessor.LOG.info("CommitProcessor exited loop!");
    }
    
    public synchronized void commit(final Request request) {
        if (!this.finished) {
            if (request == null) {
                CommitProcessor.LOG.warn("Committed a null!", new Exception("committing a null! "));
                return;
            }
            if (CommitProcessor.LOG.isDebugEnabled()) {
                CommitProcessor.LOG.debug("Committing request:: " + request);
            }
            this.committedRequests.add(request);
            this.notifyAll();
        }
    }
    
    @Override
    public synchronized void processRequest(final Request request) {
        if (CommitProcessor.LOG.isDebugEnabled()) {
            CommitProcessor.LOG.debug("Processing request:: " + request);
        }
        if (!this.finished) {
            this.queuedRequests.add(request);
            this.notifyAll();
        }
    }
    
    @Override
    public void shutdown() {
        CommitProcessor.LOG.info("Shutting down");
        synchronized (this) {
            this.finished = true;
            this.queuedRequests.clear();
            this.notifyAll();
        }
        if (this.nextProcessor != null) {
            this.nextProcessor.shutdown();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(CommitProcessor.class);
    }
}
