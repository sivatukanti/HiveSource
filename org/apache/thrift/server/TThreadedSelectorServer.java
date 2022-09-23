// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.server;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.thrift.transport.TTransportException;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.SelectorProvider;
import java.nio.channels.Selector;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.thrift.transport.TNonblockingTransport;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.io.IOException;
import java.util.Collection;
import org.apache.thrift.transport.TNonblockingServerTransport;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.Set;
import org.slf4j.Logger;

public class TThreadedSelectorServer extends AbstractNonblockingServer
{
    private static final Logger LOGGER;
    private volatile boolean stopped_;
    private AcceptThread acceptThread;
    private final Set<SelectorThread> selectorThreads;
    private final ExecutorService invoker;
    private final Args args;
    
    public TThreadedSelectorServer(final Args args) {
        super(args);
        this.stopped_ = false;
        this.selectorThreads = new HashSet<SelectorThread>();
        args.validate();
        this.invoker = ((args.executorService == null) ? createDefaultExecutor(args) : args.executorService);
        this.args = args;
    }
    
    @Override
    protected boolean startThreads() {
        try {
            for (int i = 0; i < this.args.selectorThreads; ++i) {
                this.selectorThreads.add(new SelectorThread(this.args.acceptQueueSizePerThread));
            }
            this.acceptThread = new AcceptThread((TNonblockingServerTransport)this.serverTransport_, this.createSelectorThreadLoadBalancer(this.selectorThreads));
            for (final SelectorThread thread : this.selectorThreads) {
                thread.start();
            }
            this.acceptThread.start();
            return true;
        }
        catch (IOException e) {
            TThreadedSelectorServer.LOGGER.error("Failed to start threads!", e);
            return false;
        }
    }
    
    @Override
    protected void waitForShutdown() {
        try {
            this.joinThreads();
        }
        catch (InterruptedException e) {
            TThreadedSelectorServer.LOGGER.error("Interrupted while joining threads!", e);
        }
        this.gracefullyShutdownInvokerPool();
    }
    
    protected void joinThreads() throws InterruptedException {
        this.acceptThread.join();
        for (final SelectorThread thread : this.selectorThreads) {
            thread.join();
        }
    }
    
    @Override
    public void stop() {
        this.stopped_ = true;
        this.stopListening();
        if (this.acceptThread != null) {
            this.acceptThread.wakeupSelector();
        }
        if (this.selectorThreads != null) {
            for (final SelectorThread thread : this.selectorThreads) {
                if (thread != null) {
                    thread.wakeupSelector();
                }
            }
        }
    }
    
    protected void gracefullyShutdownInvokerPool() {
        this.invoker.shutdown();
        long timeoutMS = this.args.stopTimeoutUnit.toMillis(this.args.stopTimeoutVal);
        long now = System.currentTimeMillis();
        while (timeoutMS >= 0L) {
            try {
                this.invoker.awaitTermination(timeoutMS, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException ix) {
                final long newnow = System.currentTimeMillis();
                timeoutMS -= newnow - now;
                now = newnow;
                continue;
            }
            break;
        }
    }
    
    @Override
    protected boolean requestInvoke(final FrameBuffer frameBuffer) {
        final Runnable invocation = this.getRunnable(frameBuffer);
        if (this.invoker != null) {
            try {
                this.invoker.execute(invocation);
                return true;
            }
            catch (RejectedExecutionException rx) {
                TThreadedSelectorServer.LOGGER.warn("ExecutorService rejected execution!", rx);
                return false;
            }
        }
        invocation.run();
        return true;
    }
    
    protected Runnable getRunnable(final FrameBuffer frameBuffer) {
        return new Invocation(frameBuffer);
    }
    
    protected static ExecutorService createDefaultExecutor(final Args options) {
        return (options.workerThreads > 0) ? Executors.newFixedThreadPool(options.workerThreads) : null;
    }
    
    private static BlockingQueue<TNonblockingTransport> createDefaultAcceptQueue(final int queueSize) {
        if (queueSize == 0) {
            return new LinkedBlockingQueue<TNonblockingTransport>();
        }
        return new ArrayBlockingQueue<TNonblockingTransport>(queueSize);
    }
    
    protected SelectorThreadLoadBalancer createSelectorThreadLoadBalancer(final Collection<? extends SelectorThread> threads) {
        return new SelectorThreadLoadBalancer(threads);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TThreadedSelectorServer.class.getName());
    }
    
    public static class Args extends AbstractNonblockingServerArgs<Args>
    {
        public int selectorThreads;
        private int workerThreads;
        private int stopTimeoutVal;
        private TimeUnit stopTimeoutUnit;
        private ExecutorService executorService;
        private int acceptQueueSizePerThread;
        private AcceptPolicy acceptPolicy;
        
        public Args(final TNonblockingServerTransport transport) {
            super(transport);
            this.selectorThreads = 2;
            this.workerThreads = 5;
            this.stopTimeoutVal = 60;
            this.stopTimeoutUnit = TimeUnit.SECONDS;
            this.executorService = null;
            this.acceptQueueSizePerThread = 4;
            this.acceptPolicy = AcceptPolicy.FAST_ACCEPT;
        }
        
        public Args selectorThreads(final int i) {
            this.selectorThreads = i;
            return this;
        }
        
        public int getSelectorThreads() {
            return this.selectorThreads;
        }
        
        public Args workerThreads(final int i) {
            this.workerThreads = i;
            return this;
        }
        
        public int getWorkerThreads() {
            return this.workerThreads;
        }
        
        public int getStopTimeoutVal() {
            return this.stopTimeoutVal;
        }
        
        public Args stopTimeoutVal(final int stopTimeoutVal) {
            this.stopTimeoutVal = stopTimeoutVal;
            return this;
        }
        
        public TimeUnit getStopTimeoutUnit() {
            return this.stopTimeoutUnit;
        }
        
        public Args stopTimeoutUnit(final TimeUnit stopTimeoutUnit) {
            this.stopTimeoutUnit = stopTimeoutUnit;
            return this;
        }
        
        public ExecutorService getExecutorService() {
            return this.executorService;
        }
        
        public Args executorService(final ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }
        
        public int getAcceptQueueSizePerThread() {
            return this.acceptQueueSizePerThread;
        }
        
        public Args acceptQueueSizePerThread(final int acceptQueueSizePerThread) {
            this.acceptQueueSizePerThread = acceptQueueSizePerThread;
            return this;
        }
        
        public AcceptPolicy getAcceptPolicy() {
            return this.acceptPolicy;
        }
        
        public Args acceptPolicy(final AcceptPolicy acceptPolicy) {
            this.acceptPolicy = acceptPolicy;
            return this;
        }
        
        public void validate() {
            if (this.selectorThreads <= 0) {
                throw new IllegalArgumentException("selectorThreads must be positive.");
            }
            if (this.workerThreads < 0) {
                throw new IllegalArgumentException("workerThreads must be non-negative.");
            }
            if (this.acceptQueueSizePerThread <= 0) {
                throw new IllegalArgumentException("acceptQueueSizePerThread must be positive.");
            }
        }
        
        public enum AcceptPolicy
        {
            FAIR_ACCEPT, 
            FAST_ACCEPT;
        }
    }
    
    protected class AcceptThread extends Thread
    {
        private final TNonblockingServerTransport serverTransport;
        private final Selector acceptSelector;
        private final SelectorThreadLoadBalancer threadChooser;
        
        public AcceptThread(final TNonblockingServerTransport serverTransport, final SelectorThreadLoadBalancer threadChooser) throws IOException {
            this.serverTransport = serverTransport;
            this.threadChooser = threadChooser;
            this.acceptSelector = SelectorProvider.provider().openSelector();
            this.serverTransport.registerSelector(this.acceptSelector);
        }
        
        @Override
        public void run() {
            try {
                if (TThreadedSelectorServer.this.eventHandler_ != null) {
                    TThreadedSelectorServer.this.eventHandler_.preServe();
                }
                while (!TThreadedSelectorServer.this.stopped_) {
                    this.select();
                }
            }
            catch (Throwable t) {
                TThreadedSelectorServer.LOGGER.error("run() exiting due to uncaught error", t);
            }
            finally {
                try {
                    this.acceptSelector.close();
                }
                catch (IOException e) {
                    TThreadedSelectorServer.LOGGER.error("Got an IOException while closing accept selector!", e);
                }
                TThreadedSelectorServer.this.stop();
            }
        }
        
        public void wakeupSelector() {
            this.acceptSelector.wakeup();
        }
        
        private void select() {
            try {
                this.acceptSelector.select();
                final Iterator<SelectionKey> selectedKeys = this.acceptSelector.selectedKeys().iterator();
                while (!TThreadedSelectorServer.this.stopped_ && selectedKeys.hasNext()) {
                    final SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        this.handleAccept();
                    }
                    else {
                        TThreadedSelectorServer.LOGGER.warn("Unexpected state in select! " + key.interestOps());
                    }
                }
            }
            catch (IOException e) {
                TThreadedSelectorServer.LOGGER.warn("Got an IOException while selecting!", e);
            }
        }
        
        private void handleAccept() {
            final TNonblockingTransport client = this.doAccept();
            if (client != null) {
                final SelectorThread targetThread = this.threadChooser.nextThread();
                if (TThreadedSelectorServer.this.args.acceptPolicy == Args.AcceptPolicy.FAST_ACCEPT || TThreadedSelectorServer.this.invoker == null) {
                    this.doAddAccept(targetThread, client);
                }
                else {
                    try {
                        TThreadedSelectorServer.this.invoker.submit(new Runnable() {
                            public void run() {
                                AcceptThread.this.doAddAccept(targetThread, client);
                            }
                        });
                    }
                    catch (RejectedExecutionException rx) {
                        TThreadedSelectorServer.LOGGER.warn("ExecutorService rejected accept registration!", rx);
                        client.close();
                    }
                }
            }
        }
        
        private TNonblockingTransport doAccept() {
            try {
                return (TNonblockingTransport)this.serverTransport.accept();
            }
            catch (TTransportException tte) {
                TThreadedSelectorServer.LOGGER.warn("Exception trying to accept!", tte);
                return null;
            }
        }
        
        private void doAddAccept(final SelectorThread thread, final TNonblockingTransport client) {
            if (!thread.addAcceptedConnection(client)) {
                client.close();
            }
        }
    }
    
    protected class SelectorThread extends AbstractSelectThread
    {
        private final BlockingQueue<TNonblockingTransport> acceptedQueue;
        
        public SelectorThread(final TThreadedSelectorServer tThreadedSelectorServer) throws IOException {
            this(new LinkedBlockingQueue<TNonblockingTransport>());
        }
        
        public SelectorThread(final TThreadedSelectorServer tThreadedSelectorServer, final int maxPendingAccepts) throws IOException {
            this(createDefaultAcceptQueue(maxPendingAccepts));
        }
        
        public SelectorThread(final BlockingQueue<TNonblockingTransport> acceptedQueue) throws IOException {
            this.acceptedQueue = acceptedQueue;
        }
        
        public boolean addAcceptedConnection(final TNonblockingTransport accepted) {
            try {
                this.acceptedQueue.put(accepted);
            }
            catch (InterruptedException e) {
                TThreadedSelectorServer.LOGGER.warn("Interrupted while adding accepted connection!", e);
                return false;
            }
            this.selector.wakeup();
            return true;
        }
        
        @Override
        public void run() {
            try {
                while (!TThreadedSelectorServer.this.stopped_) {
                    this.select();
                    this.processAcceptedConnections();
                    this.processInterestChanges();
                }
                for (final SelectionKey selectionKey : this.selector.keys()) {
                    this.cleanupSelectionKey(selectionKey);
                }
            }
            catch (Throwable t) {
                TThreadedSelectorServer.LOGGER.error("run() exiting due to uncaught error", t);
            }
            finally {
                try {
                    this.selector.close();
                }
                catch (IOException e) {
                    TThreadedSelectorServer.LOGGER.error("Got an IOException while closing selector!", e);
                }
                TThreadedSelectorServer.this.stop();
            }
        }
        
        private void select() {
            try {
                this.selector.select();
                final Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
                while (!TThreadedSelectorServer.this.stopped_ && selectedKeys.hasNext()) {
                    final SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();
                    if (!key.isValid()) {
                        this.cleanupSelectionKey(key);
                    }
                    else if (key.isReadable()) {
                        this.handleRead(key);
                    }
                    else if (key.isWritable()) {
                        this.handleWrite(key);
                    }
                    else {
                        TThreadedSelectorServer.LOGGER.warn("Unexpected state in select! " + key.interestOps());
                    }
                }
            }
            catch (IOException e) {
                TThreadedSelectorServer.LOGGER.warn("Got an IOException while selecting!", e);
            }
        }
        
        private void processAcceptedConnections() {
            while (!TThreadedSelectorServer.this.stopped_) {
                final TNonblockingTransport accepted = this.acceptedQueue.poll();
                if (accepted == null) {
                    break;
                }
                this.registerAccepted(accepted);
            }
        }
        
        protected FrameBuffer createFrameBuffer(final TNonblockingTransport trans, final SelectionKey selectionKey, final AbstractSelectThread selectThread) {
            return TThreadedSelectorServer.this.processorFactory_.isAsyncProcessor() ? new AsyncFrameBuffer(trans, selectionKey, selectThread) : new FrameBuffer(trans, selectionKey, selectThread);
        }
        
        private void registerAccepted(final TNonblockingTransport accepted) {
            SelectionKey clientKey = null;
            try {
                clientKey = accepted.registerSelector(this.selector, 1);
                final FrameBuffer frameBuffer = this.createFrameBuffer(accepted, clientKey, this);
                clientKey.attach(frameBuffer);
            }
            catch (IOException e) {
                TThreadedSelectorServer.LOGGER.warn("Failed to register accepted connection to selector!", e);
                if (clientKey != null) {
                    this.cleanupSelectionKey(clientKey);
                }
                accepted.close();
            }
        }
    }
    
    protected static class SelectorThreadLoadBalancer
    {
        private final Collection<? extends SelectorThread> threads;
        private Iterator<? extends SelectorThread> nextThreadIterator;
        
        public <T extends SelectorThread> SelectorThreadLoadBalancer(final Collection<T> threads) {
            if (threads.isEmpty()) {
                throw new IllegalArgumentException("At least one selector thread is required");
            }
            this.threads = (Collection<? extends SelectorThread>)Collections.unmodifiableList((List<?>)new ArrayList<Object>(threads));
            this.nextThreadIterator = this.threads.iterator();
        }
        
        public SelectorThread nextThread() {
            if (!this.nextThreadIterator.hasNext()) {
                this.nextThreadIterator = this.threads.iterator();
            }
            return (SelectorThread)this.nextThreadIterator.next();
        }
    }
}
