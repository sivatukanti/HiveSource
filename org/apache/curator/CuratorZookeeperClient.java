// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator;

import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.WatchedEvent;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.ZooKeeper;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.utils.DefaultTracerDriver;
import org.slf4j.LoggerFactory;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.utils.ZookeeperFactory;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.utils.DefaultZookeeperFactory;
import org.apache.zookeeper.Watcher;
import org.apache.curator.drivers.TracerDriver;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import java.io.Closeable;

public class CuratorZookeeperClient implements Closeable
{
    private final Logger log;
    private final ConnectionState state;
    private final AtomicReference<RetryPolicy> retryPolicy;
    private final int connectionTimeoutMs;
    private final AtomicBoolean started;
    private final AtomicReference<TracerDriver> tracer;
    
    public CuratorZookeeperClient(final String connectString, final int sessionTimeoutMs, final int connectionTimeoutMs, final Watcher watcher, final RetryPolicy retryPolicy) {
        this(new DefaultZookeeperFactory(), new FixedEnsembleProvider(connectString), sessionTimeoutMs, connectionTimeoutMs, watcher, retryPolicy, false);
    }
    
    public CuratorZookeeperClient(final EnsembleProvider ensembleProvider, final int sessionTimeoutMs, final int connectionTimeoutMs, final Watcher watcher, final RetryPolicy retryPolicy) {
        this(new DefaultZookeeperFactory(), ensembleProvider, sessionTimeoutMs, connectionTimeoutMs, watcher, retryPolicy, false);
    }
    
    public CuratorZookeeperClient(final ZookeeperFactory zookeeperFactory, EnsembleProvider ensembleProvider, final int sessionTimeoutMs, final int connectionTimeoutMs, final Watcher watcher, RetryPolicy retryPolicy, final boolean canBeReadOnly) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.retryPolicy = new AtomicReference<RetryPolicy>();
        this.started = new AtomicBoolean(false);
        this.tracer = new AtomicReference<TracerDriver>(new DefaultTracerDriver());
        if (sessionTimeoutMs < connectionTimeoutMs) {
            this.log.warn(String.format("session timeout [%d] is less than connection timeout [%d]", sessionTimeoutMs, connectionTimeoutMs));
        }
        retryPolicy = Preconditions.checkNotNull(retryPolicy, (Object)"retryPolicy cannot be null");
        ensembleProvider = Preconditions.checkNotNull(ensembleProvider, (Object)"ensembleProvider cannot be null");
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.state = new ConnectionState(zookeeperFactory, ensembleProvider, sessionTimeoutMs, connectionTimeoutMs, watcher, this.tracer, canBeReadOnly);
        this.setRetryPolicy(retryPolicy);
    }
    
    public ZooKeeper getZooKeeper() throws Exception {
        Preconditions.checkState(this.started.get(), (Object)"Client is not started");
        return this.state.getZooKeeper();
    }
    
    public RetryLoop newRetryLoop() {
        return new RetryLoop(this.retryPolicy.get(), this.tracer);
    }
    
    public SessionFailRetryLoop newSessionFailRetryLoop(final SessionFailRetryLoop.Mode mode) {
        return new SessionFailRetryLoop(this, mode);
    }
    
    public boolean isConnected() {
        return this.state.isConnected();
    }
    
    public boolean blockUntilConnectedOrTimedOut() throws InterruptedException {
        Preconditions.checkState(this.started.get(), (Object)"Client is not started");
        this.log.debug("blockUntilConnectedOrTimedOut() start");
        final OperationTrace trace = this.startAdvancedTracer("blockUntilConnectedOrTimedOut");
        this.internalBlockUntilConnectedOrTimedOut();
        trace.commit();
        final boolean localIsConnected = this.state.isConnected();
        this.log.debug("blockUntilConnectedOrTimedOut() end. isConnected: " + localIsConnected);
        return localIsConnected;
    }
    
    public void start() throws Exception {
        this.log.debug("Starting");
        if (!this.started.compareAndSet(false, true)) {
            final IllegalStateException ise = new IllegalStateException("Already started");
            throw ise;
        }
        this.state.start();
    }
    
    @Override
    public void close() {
        this.log.debug("Closing");
        this.started.set(false);
        try {
            this.state.close();
        }
        catch (IOException e) {
            ThreadUtils.checkInterrupted(e);
            this.log.error("", e);
        }
    }
    
    public void setRetryPolicy(final RetryPolicy policy) {
        Preconditions.checkNotNull(policy, (Object)"policy cannot be null");
        this.retryPolicy.set(policy);
    }
    
    public RetryPolicy getRetryPolicy() {
        return this.retryPolicy.get();
    }
    
    public TimeTrace startTracer(final String name) {
        return new TimeTrace(name, this.tracer.get());
    }
    
    public OperationTrace startAdvancedTracer(final String name) {
        return new OperationTrace(name, this.tracer.get(), this.state.getSessionId());
    }
    
    public TracerDriver getTracerDriver() {
        return this.tracer.get();
    }
    
    public void setTracerDriver(final TracerDriver tracer) {
        this.tracer.set(tracer);
    }
    
    public String getCurrentConnectionString() {
        return this.state.getEnsembleProvider().getConnectionString();
    }
    
    public int getConnectionTimeoutMs() {
        return this.connectionTimeoutMs;
    }
    
    public long getInstanceIndex() {
        return this.state.getInstanceIndex();
    }
    
    void addParentWatcher(final Watcher watcher) {
        this.state.addParentWatcher(watcher);
    }
    
    void removeParentWatcher(final Watcher watcher) {
        this.state.removeParentWatcher(watcher);
    }
    
    void internalBlockUntilConnectedOrTimedOut() throws InterruptedException {
        long elapsed;
        for (long waitTimeMs = this.connectionTimeoutMs; !this.state.isConnected() && waitTimeMs > 0L; waitTimeMs -= elapsed) {
            final CountDownLatch latch = new CountDownLatch(1);
            final Watcher tempWatcher = new Watcher() {
                @Override
                public void process(final WatchedEvent event) {
                    latch.countDown();
                }
            };
            this.state.addParentWatcher(tempWatcher);
            final long startTimeMs = System.currentTimeMillis();
            try {
                latch.await(1L, TimeUnit.SECONDS);
            }
            finally {
                this.state.removeParentWatcher(tempWatcher);
            }
            elapsed = Math.max(1L, System.currentTimeMillis() - startTimeMs);
        }
    }
}
