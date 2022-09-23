// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.server;

import parquet.org.apache.thrift.protocol.TProtocol;
import parquet.org.apache.thrift.TProcessor;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.transport.TServerTransport;
import parquet.org.slf4j.LoggerFactory;
import parquet.org.apache.thrift.transport.TTransport;
import parquet.org.apache.thrift.transport.TTransportException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import parquet.org.slf4j.Logger;

public class TThreadPoolServer extends TServer
{
    private static final Logger LOGGER;
    private ExecutorService executorService_;
    private volatile boolean stopped_;
    private final TimeUnit stopTimeoutUnit;
    private final long stopTimeoutVal;
    
    public TThreadPoolServer(final Args args) {
        super(args);
        final SynchronousQueue<Runnable> executorQueue = new SynchronousQueue<Runnable>();
        this.stopTimeoutUnit = args.stopTimeoutUnit;
        this.stopTimeoutVal = args.stopTimeoutVal;
        this.executorService_ = new ThreadPoolExecutor(args.minWorkerThreads, args.maxWorkerThreads, 60L, TimeUnit.SECONDS, executorQueue);
    }
    
    @Override
    public void serve() {
        try {
            this.serverTransport_.listen();
        }
        catch (TTransportException ttx) {
            TThreadPoolServer.LOGGER.error("Error occurred during listening.", ttx);
            return;
        }
        this.stopped_ = false;
        this.setServing(true);
        while (!this.stopped_) {
            int failureCount = 0;
            try {
                final TTransport client = this.serverTransport_.accept();
                final WorkerProcess wp = new WorkerProcess(client);
                this.executorService_.execute(wp);
            }
            catch (TTransportException ttx2) {
                if (this.stopped_) {
                    continue;
                }
                ++failureCount;
                TThreadPoolServer.LOGGER.warn("Transport error occurred during acceptance of message.", ttx2);
            }
        }
        this.executorService_.shutdown();
        long timeoutMS = this.stopTimeoutUnit.toMillis(this.stopTimeoutVal);
        long now = System.currentTimeMillis();
        while (timeoutMS >= 0L) {
            try {
                this.executorService_.awaitTermination(timeoutMS, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException ix) {
                final long newnow = System.currentTimeMillis();
                timeoutMS -= newnow - now;
                now = newnow;
                continue;
            }
            break;
        }
        this.setServing(false);
    }
    
    @Override
    public void stop() {
        this.stopped_ = true;
        this.serverTransport_.interrupt();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TThreadPoolServer.class.getName());
    }
    
    public static class Args extends AbstractServerArgs<Args>
    {
        public int minWorkerThreads;
        public int maxWorkerThreads;
        public int stopTimeoutVal;
        public TimeUnit stopTimeoutUnit;
        
        public Args(final TServerTransport transport) {
            super(transport);
            this.minWorkerThreads = 5;
            this.maxWorkerThreads = Integer.MAX_VALUE;
            this.stopTimeoutVal = 60;
            this.stopTimeoutUnit = TimeUnit.SECONDS;
        }
        
        public Args minWorkerThreads(final int n) {
            this.minWorkerThreads = n;
            return this;
        }
        
        public Args maxWorkerThreads(final int n) {
            this.maxWorkerThreads = n;
            return this;
        }
    }
    
    private class WorkerProcess implements Runnable
    {
        private TTransport client_;
        
        private WorkerProcess(final TTransport client) {
            this.client_ = client;
        }
        
        public void run() {
            TProcessor processor = null;
            TTransport inputTransport = null;
            TTransport outputTransport = null;
            TProtocol inputProtocol = null;
            TProtocol outputProtocol = null;
            try {
                processor = TThreadPoolServer.this.processorFactory_.getProcessor(this.client_);
                inputTransport = TThreadPoolServer.this.inputTransportFactory_.getTransport(this.client_);
                outputTransport = TThreadPoolServer.this.outputTransportFactory_.getTransport(this.client_);
                inputProtocol = TThreadPoolServer.this.inputProtocolFactory_.getProtocol(inputTransport);
                outputProtocol = TThreadPoolServer.this.outputProtocolFactory_.getProtocol(outputTransport);
                while (!TThreadPoolServer.this.stopped_ && processor.process(inputProtocol, outputProtocol)) {}
            }
            catch (TTransportException ttx) {}
            catch (TException tx) {
                TThreadPoolServer.LOGGER.error("Thrift error occurred during processing of message.", tx);
            }
            catch (Exception x) {
                TThreadPoolServer.LOGGER.error("Error occurred during processing of message.", x);
            }
            if (inputTransport != null) {
                inputTransport.close();
            }
            if (outputTransport != null) {
                outputTransport.close();
            }
        }
    }
}
