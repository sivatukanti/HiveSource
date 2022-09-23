// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.server;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TSaslTransportException;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.LoggerFactory;
import org.apache.thrift.transport.TTransport;
import java.util.concurrent.RejectedExecutionException;
import org.apache.thrift.transport.TTransportException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;

public class TThreadPoolServer extends TServer
{
    private static final Logger LOGGER;
    private ExecutorService executorService_;
    private volatile boolean stopped_;
    private final TimeUnit stopTimeoutUnit;
    private final long stopTimeoutVal;
    private final TimeUnit requestTimeoutUnit;
    private final long requestTimeout;
    private final long beBackoffSlotInMillis;
    private Random random;
    
    public TThreadPoolServer(final Args args) {
        super(args);
        this.stopped_ = false;
        this.random = new Random(System.currentTimeMillis());
        this.stopTimeoutUnit = args.stopTimeoutUnit;
        this.stopTimeoutVal = args.stopTimeoutVal;
        this.requestTimeoutUnit = args.requestTimeoutUnit;
        this.requestTimeout = args.requestTimeout;
        this.beBackoffSlotInMillis = args.beBackoffSlotLengthUnit.toMillis(args.beBackoffSlotLength);
        this.executorService_ = ((args.executorService != null) ? args.executorService : createDefaultExecutorService(args));
    }
    
    private static ExecutorService createDefaultExecutorService(final Args args) {
        final SynchronousQueue<Runnable> executorQueue = new SynchronousQueue<Runnable>();
        return new ThreadPoolExecutor(args.minWorkerThreads, args.maxWorkerThreads, 60L, TimeUnit.SECONDS, executorQueue);
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
        if (this.eventHandler_ != null) {
            this.eventHandler_.preServe();
        }
        this.stopped_ = false;
        this.setServing(true);
        int failureCount = 0;
    Label_0093_Outer:
        while (!this.stopped_) {
            try {
                final TTransport client = this.serverTransport_.accept();
                WorkerProcess wp = new WorkerProcess(client);
                int retryCount = 0;
                long remainTimeInMillis = this.requestTimeoutUnit.toMillis(this.requestTimeout);
                while (true) {
                    try {
                        this.executorService_.execute(wp);
                    }
                    catch (Throwable t) {
                        Label_0250: {
                            if (t instanceof RejectedExecutionException) {
                                ++retryCount;
                                Label_0327: {
                                    try {
                                        if (remainTimeInMillis > 0L) {
                                            long sleepTimeInMillis = (long)(this.random.nextDouble() * (1L << Math.min(retryCount, 20))) * this.beBackoffSlotInMillis;
                                            sleepTimeInMillis = Math.min(sleepTimeInMillis, remainTimeInMillis);
                                            TimeUnit.MILLISECONDS.sleep(sleepTimeInMillis);
                                            remainTimeInMillis -= sleepTimeInMillis;
                                            break Label_0327;
                                        }
                                        client.close();
                                        wp = null;
                                        TThreadPoolServer.LOGGER.warn("Task has been rejected by ExecutorService " + retryCount + " times till timedout, reason: " + t);
                                        continue Label_0093_Outer;
                                    }
                                    catch (InterruptedException e) {
                                        TThreadPoolServer.LOGGER.warn("Interrupted while waiting to place client on executor queue.");
                                        Thread.currentThread().interrupt();
                                        continue Label_0093_Outer;
                                    }
                                    break Label_0250;
                                }
                                continue;
                            }
                        }
                        if (t instanceof Error) {
                            TThreadPoolServer.LOGGER.error("ExecutorService threw error: " + t, t);
                            throw (Error)t;
                        }
                        TThreadPoolServer.LOGGER.warn("ExecutorService threw error: " + t, t);
                    }
                    break;
                }
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
        public ExecutorService executorService;
        public int stopTimeoutVal;
        public TimeUnit stopTimeoutUnit;
        public int requestTimeout;
        public TimeUnit requestTimeoutUnit;
        public int beBackoffSlotLength;
        public TimeUnit beBackoffSlotLengthUnit;
        
        public Args(final TServerTransport transport) {
            super(transport);
            this.minWorkerThreads = 5;
            this.maxWorkerThreads = Integer.MAX_VALUE;
            this.stopTimeoutVal = 60;
            this.stopTimeoutUnit = TimeUnit.SECONDS;
            this.requestTimeout = 20;
            this.requestTimeoutUnit = TimeUnit.SECONDS;
            this.beBackoffSlotLength = 100;
            this.beBackoffSlotLengthUnit = TimeUnit.MILLISECONDS;
        }
        
        public Args minWorkerThreads(final int n) {
            this.minWorkerThreads = n;
            return this;
        }
        
        public Args maxWorkerThreads(final int n) {
            this.maxWorkerThreads = n;
            return this;
        }
        
        public Args requestTimeout(final int n) {
            this.requestTimeout = n;
            return this;
        }
        
        public Args requestTimeoutUnit(final TimeUnit tu) {
            this.requestTimeoutUnit = tu;
            return this;
        }
        
        public Args beBackoffSlotLength(final int n) {
            this.beBackoffSlotLength = n;
            return this;
        }
        
        public Args beBackoffSlotLengthUnit(final TimeUnit tu) {
            this.beBackoffSlotLengthUnit = tu;
            return this;
        }
        
        public Args executorService(final ExecutorService executorService) {
            this.executorService = executorService;
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
            TServerEventHandler eventHandler = null;
            ServerContext connectionContext = null;
            try {
                processor = TThreadPoolServer.this.processorFactory_.getProcessor(this.client_);
                inputTransport = TThreadPoolServer.this.inputTransportFactory_.getTransport(this.client_);
                outputTransport = TThreadPoolServer.this.outputTransportFactory_.getTransport(this.client_);
                inputProtocol = TThreadPoolServer.this.inputProtocolFactory_.getProtocol(inputTransport);
                outputProtocol = TThreadPoolServer.this.outputProtocolFactory_.getProtocol(outputTransport);
                eventHandler = TThreadPoolServer.this.getEventHandler();
                if (eventHandler != null) {
                    connectionContext = eventHandler.createContext(inputProtocol, outputProtocol);
                }
                do {
                    if (eventHandler != null) {
                        eventHandler.processContext(connectionContext, inputTransport, outputTransport);
                    }
                } while (!TThreadPoolServer.this.stopped_ && processor.process(inputProtocol, outputProtocol));
            }
            catch (TSaslTransportException ttx) {}
            catch (TTransportException ttx2) {}
            catch (TException tx) {
                TThreadPoolServer.LOGGER.error("Thrift error occurred during processing of message.", tx);
            }
            catch (Exception x) {
                TThreadPoolServer.LOGGER.error("Error occurred during processing of message.", x);
            }
            if (eventHandler != null) {
                eventHandler.deleteContext(connectionContext, inputProtocol, outputProtocol);
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
