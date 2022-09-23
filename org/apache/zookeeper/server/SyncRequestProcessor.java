// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.Flushable;
import java.util.Random;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;

public class SyncRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor
{
    private static final Logger LOG;
    private final ZooKeeperServer zks;
    private final LinkedBlockingQueue<Request> queuedRequests;
    private final RequestProcessor nextProcessor;
    private Thread snapInProcess;
    private volatile boolean running;
    private final LinkedList<Request> toFlush;
    private final Random r;
    private static int snapCount;
    private static int randRoll;
    private final Request requestOfDeath;
    
    public SyncRequestProcessor(final ZooKeeperServer zks, final RequestProcessor nextProcessor) {
        super("SyncThread:" + zks.getServerId(), zks.getZooKeeperServerListener());
        this.queuedRequests = new LinkedBlockingQueue<Request>();
        this.snapInProcess = null;
        this.toFlush = new LinkedList<Request>();
        this.r = new Random(System.nanoTime());
        this.requestOfDeath = Request.requestOfDeath;
        this.zks = zks;
        this.nextProcessor = nextProcessor;
        this.running = true;
    }
    
    public static void setSnapCount(final int count) {
        SyncRequestProcessor.snapCount = count;
        SyncRequestProcessor.randRoll = count;
    }
    
    public static int getSnapCount() {
        return SyncRequestProcessor.snapCount;
    }
    
    private static void setRandRoll(final int roll) {
        SyncRequestProcessor.randRoll = roll;
    }
    
    @Override
    public void run() {
        try {
            int logCount = 0;
            setRandRoll(this.r.nextInt(SyncRequestProcessor.snapCount / 2));
            while (true) {
                Request si = null;
                if (this.toFlush.isEmpty()) {
                    si = this.queuedRequests.take();
                }
                else {
                    si = this.queuedRequests.poll();
                    if (si == null) {
                        this.flush(this.toFlush);
                        continue;
                    }
                }
                if (si == this.requestOfDeath) {
                    break;
                }
                if (si == null) {
                    continue;
                }
                if (this.zks.getZKDatabase().append(si)) {
                    if (++logCount > SyncRequestProcessor.snapCount / 2 + SyncRequestProcessor.randRoll) {
                        setRandRoll(this.r.nextInt(SyncRequestProcessor.snapCount / 2));
                        this.zks.getZKDatabase().rollLog();
                        if (this.snapInProcess != null && this.snapInProcess.isAlive()) {
                            SyncRequestProcessor.LOG.warn("Too busy to snap, skipping");
                        }
                        else {
                            (this.snapInProcess = new ZooKeeperThread("Snapshot Thread") {
                                @Override
                                public void run() {
                                    try {
                                        SyncRequestProcessor.this.zks.takeSnapshot();
                                    }
                                    catch (Exception e) {
                                        SyncRequestProcessor.LOG.warn("Unexpected exception", e);
                                    }
                                }
                            }).start();
                        }
                        logCount = 0;
                    }
                }
                else if (this.toFlush.isEmpty()) {
                    if (this.nextProcessor == null) {
                        continue;
                    }
                    this.nextProcessor.processRequest(si);
                    if (this.nextProcessor instanceof Flushable) {
                        ((Flushable)this.nextProcessor).flush();
                        continue;
                    }
                    continue;
                }
                this.toFlush.add(si);
                if (this.toFlush.size() <= 1000) {
                    continue;
                }
                this.flush(this.toFlush);
            }
        }
        catch (Throwable t) {
            this.handleException(this.getName(), t);
            this.running = false;
        }
        SyncRequestProcessor.LOG.info("SyncRequestProcessor exited!");
    }
    
    private void flush(final LinkedList<Request> toFlush) throws IOException, RequestProcessorException {
        if (toFlush.isEmpty()) {
            return;
        }
        this.zks.getZKDatabase().commit();
        while (!toFlush.isEmpty()) {
            final Request i = toFlush.remove();
            if (this.nextProcessor != null) {
                this.nextProcessor.processRequest(i);
            }
        }
        if (this.nextProcessor != null && this.nextProcessor instanceof Flushable) {
            ((Flushable)this.nextProcessor).flush();
        }
    }
    
    @Override
    public void shutdown() {
        SyncRequestProcessor.LOG.info("Shutting down");
        this.queuedRequests.add(this.requestOfDeath);
        try {
            if (this.running) {
                this.join();
            }
            if (!this.toFlush.isEmpty()) {
                this.flush(this.toFlush);
            }
        }
        catch (InterruptedException e) {
            SyncRequestProcessor.LOG.warn("Interrupted while wating for " + this + " to finish");
        }
        catch (IOException e2) {
            SyncRequestProcessor.LOG.warn("Got IO exception during shutdown");
        }
        catch (RequestProcessorException e3) {
            SyncRequestProcessor.LOG.warn("Got request processor exception during shutdown");
        }
        if (this.nextProcessor != null) {
            this.nextProcessor.shutdown();
        }
    }
    
    @Override
    public void processRequest(final Request request) {
        this.queuedRequests.add(request);
    }
    
    static {
        LOG = LoggerFactory.getLogger(SyncRequestProcessor.class);
        SyncRequestProcessor.snapCount = ZooKeeperServer.getSnapCount();
    }
}
