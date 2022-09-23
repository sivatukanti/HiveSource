// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.server;

import parquet.org.apache.thrift.transport.TNonblockingServerTransport;
import parquet.org.slf4j.LoggerFactory;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import parquet.org.slf4j.Logger;

public class THsHaServer extends TNonblockingServer
{
    private static final Logger LOGGER;
    private ExecutorService invoker;
    
    public THsHaServer(final Args args) {
        super(args);
        this.invoker = ((args.executorService == null) ? createInvokerPool(args) : args.executorService);
    }
    
    @Override
    public void serve() {
        if (!this.startListening()) {
            return;
        }
        if (!this.startSelectorThread()) {
            return;
        }
        this.setServing(true);
        this.joinSelector();
        this.gracefullyShutdownInvokerPool();
        this.setServing(false);
        this.stopListening();
    }
    
    protected static ExecutorService createInvokerPool(final Args options) {
        final int workerThreads = options.workerThreads;
        final int stopTimeoutVal = options.stopTimeoutVal;
        final TimeUnit stopTimeoutUnit = options.stopTimeoutUnit;
        final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        final ExecutorService invoker = new ThreadPoolExecutor(workerThreads, workerThreads, stopTimeoutVal, stopTimeoutUnit, queue);
        return invoker;
    }
    
    protected void gracefullyShutdownInvokerPool() {
        this.invoker.shutdown();
        long timeoutMS = 10000L;
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
        try {
            final Runnable invocation = this.getRunnable(frameBuffer);
            this.invoker.execute(invocation);
            return true;
        }
        catch (RejectedExecutionException rx) {
            THsHaServer.LOGGER.warn("ExecutorService rejected execution!", rx);
            return false;
        }
    }
    
    protected Runnable getRunnable(final FrameBuffer frameBuffer) {
        return new Invocation(frameBuffer);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(THsHaServer.class.getName());
    }
    
    public static class Args extends AbstractNonblockingServerArgs<Args>
    {
        private int workerThreads;
        private int stopTimeoutVal;
        private TimeUnit stopTimeoutUnit;
        private ExecutorService executorService;
        
        public Args(final TNonblockingServerTransport transport) {
            super(transport);
            this.workerThreads = 5;
            this.stopTimeoutVal = 60;
            this.stopTimeoutUnit = TimeUnit.SECONDS;
            this.executorService = null;
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
    }
    
    private class Invocation implements Runnable
    {
        private final FrameBuffer frameBuffer;
        
        public Invocation(final FrameBuffer frameBuffer) {
            this.frameBuffer = frameBuffer;
        }
        
        public void run() {
            this.frameBuffer.invoke();
        }
    }
}
