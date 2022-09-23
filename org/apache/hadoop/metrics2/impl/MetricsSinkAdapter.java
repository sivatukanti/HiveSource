// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import java.util.Iterator;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.metrics2.MetricsRecord;
import java.util.Random;
import org.apache.hadoop.metrics2.util.Contracts;
import com.google.common.base.Preconditions;
import org.apache.hadoop.metrics2.lib.MutableGaugeInt;
import org.apache.hadoop.metrics2.lib.MutableCounterInt;
import org.apache.hadoop.metrics2.lib.MutableStat;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.apache.hadoop.metrics2.MetricsFilter;
import org.apache.hadoop.metrics2.MetricsSink;
import org.slf4j.Logger;

class MetricsSinkAdapter implements SinkQueue.Consumer<MetricsBuffer>
{
    private static final Logger LOG;
    private final String name;
    private final String description;
    private final String context;
    private final MetricsSink sink;
    private final MetricsFilter sourceFilter;
    private final MetricsFilter recordFilter;
    private final MetricsFilter metricFilter;
    private final SinkQueue<MetricsBuffer> queue;
    private final Thread sinkThread;
    private volatile boolean stopping;
    private volatile boolean inError;
    private final int periodMs;
    private final int firstRetryDelay;
    private final int retryCount;
    private final long oobPutTimeout;
    private final float retryBackoff;
    private final MetricsRegistry registry;
    private final MutableStat latency;
    private final MutableCounterInt dropped;
    private final MutableGaugeInt qsize;
    
    MetricsSinkAdapter(final String name, final String description, final MetricsSink sink, final String context, final MetricsFilter sourceFilter, final MetricsFilter recordFilter, final MetricsFilter metricFilter, final int periodMs, final int queueCapacity, final int retryDelay, final float retryBackoff, final int retryCount) {
        this.stopping = false;
        this.inError = false;
        this.registry = new MetricsRegistry("sinkadapter");
        this.name = Preconditions.checkNotNull(name, (Object)"name");
        this.description = description;
        this.sink = Preconditions.checkNotNull(sink, (Object)"sink object");
        this.context = context;
        this.sourceFilter = sourceFilter;
        this.recordFilter = recordFilter;
        this.metricFilter = metricFilter;
        this.periodMs = Contracts.checkArg(periodMs, periodMs > 0, "period");
        this.firstRetryDelay = Contracts.checkArg(retryDelay, retryDelay > 0, "retry delay");
        this.retryBackoff = Contracts.checkArg(retryBackoff, retryBackoff > 1.0f, "retry backoff");
        this.oobPutTimeout = (long)(this.firstRetryDelay * Math.pow(retryBackoff, retryCount) * 1000.0);
        this.retryCount = retryCount;
        this.queue = new SinkQueue<MetricsBuffer>(Contracts.checkArg(queueCapacity, queueCapacity > 0, "queue capacity"));
        this.latency = this.registry.newRate("Sink_" + name, "Sink end to end latency", false);
        this.dropped = this.registry.newCounter("Sink_" + name + "Dropped", "Dropped updates per sink", 0);
        this.qsize = this.registry.newGauge("Sink_" + name + "Qsize", "Queue size", 0);
        (this.sinkThread = new Thread() {
            @Override
            public void run() {
                MetricsSinkAdapter.this.publishMetricsFromQueue();
            }
        }).setName(name);
        this.sinkThread.setDaemon(true);
    }
    
    boolean putMetrics(final MetricsBuffer buffer, final long logicalTimeMs) {
        if (logicalTimeMs % this.periodMs != 0L) {
            return true;
        }
        MetricsSinkAdapter.LOG.debug("enqueue, logicalTime=" + logicalTimeMs);
        if (this.queue.enqueue(buffer)) {
            this.refreshQueueSizeGauge();
            return true;
        }
        this.dropped.incr();
        return false;
    }
    
    public boolean putMetricsImmediate(final MetricsBuffer buffer) {
        final WaitableMetricsBuffer waitableBuffer = new WaitableMetricsBuffer(buffer);
        if (!this.queue.enqueue(waitableBuffer)) {
            MetricsSinkAdapter.LOG.warn(this.name + " has a full queue and can't consume the given metrics.");
            this.dropped.incr();
            return false;
        }
        this.refreshQueueSizeGauge();
        if (!waitableBuffer.waitTillNotified(this.oobPutTimeout)) {
            MetricsSinkAdapter.LOG.warn(this.name + " couldn't fulfill an immediate putMetrics request in time. Abandoning.");
            return false;
        }
        return true;
    }
    
    void publishMetricsFromQueue() {
        int retryDelay = this.firstRetryDelay;
        int n = this.retryCount;
        final int minDelay = Math.min(500, retryDelay * 1000);
        final Random rng = new Random(System.nanoTime());
        while (!this.stopping) {
            try {
                this.queue.consumeAll(this);
                this.refreshQueueSizeGauge();
                retryDelay = this.firstRetryDelay;
                n = this.retryCount;
                this.inError = false;
            }
            catch (InterruptedException e3) {
                MetricsSinkAdapter.LOG.info(this.name + " thread interrupted.");
            }
            catch (Exception e) {
                if (n > 0) {
                    final int retryWindow = Math.max(0, 500 * retryDelay - minDelay);
                    final int awhile = rng.nextInt(retryWindow) + minDelay;
                    if (!this.inError) {
                        MetricsSinkAdapter.LOG.error("Got sink exception, retry in " + awhile + "ms", e);
                    }
                    retryDelay *= (int)this.retryBackoff;
                    try {
                        Thread.sleep(awhile);
                    }
                    catch (InterruptedException e2) {
                        MetricsSinkAdapter.LOG.info(this.name + " thread interrupted while waiting for retry", e2);
                    }
                    --n;
                }
                else {
                    if (!this.inError) {
                        MetricsSinkAdapter.LOG.error("Got sink exception and over retry limit, suppressing further error messages", e);
                    }
                    this.queue.clear();
                    this.refreshQueueSizeGauge();
                    this.inError = true;
                }
            }
        }
    }
    
    private void refreshQueueSizeGauge() {
        this.qsize.set(this.queue.size());
    }
    
    @Override
    public void consume(final MetricsBuffer buffer) {
        long ts = 0L;
        for (final MetricsBuffer.Entry entry : buffer) {
            if (this.sourceFilter == null || this.sourceFilter.accepts(entry.name())) {
                for (final MetricsRecordImpl record : entry.records()) {
                    if ((this.context == null || this.context.equals(record.context())) && (this.recordFilter == null || this.recordFilter.accepts(record))) {
                        if (MetricsSinkAdapter.LOG.isDebugEnabled()) {
                            MetricsSinkAdapter.LOG.debug("Pushing record " + entry.name() + "." + record.context() + "." + record.name() + " to " + this.name);
                        }
                        this.sink.putMetrics((this.metricFilter == null) ? record : new MetricsRecordFiltered(record, this.metricFilter));
                        if (ts != 0L) {
                            continue;
                        }
                        ts = record.timestamp();
                    }
                }
            }
        }
        if (ts > 0L) {
            this.sink.flush();
            this.latency.add(Time.now() - ts);
        }
        if (buffer instanceof WaitableMetricsBuffer) {
            ((WaitableMetricsBuffer)buffer).notifyAnyWaiters();
        }
        MetricsSinkAdapter.LOG.debug("Done");
    }
    
    void start() {
        this.sinkThread.start();
        MetricsSinkAdapter.LOG.info("Sink " + this.name + " started");
    }
    
    void stop() {
        this.stopping = true;
        this.sinkThread.interrupt();
        if (this.sink instanceof Closeable) {
            IOUtils.cleanupWithLogger(MetricsSinkAdapter.LOG, (Closeable)this.sink);
        }
        try {
            this.sinkThread.join();
        }
        catch (InterruptedException e) {
            MetricsSinkAdapter.LOG.warn("Stop interrupted", e);
        }
    }
    
    String name() {
        return this.name;
    }
    
    String description() {
        return this.description;
    }
    
    void snapshot(final MetricsRecordBuilder rb, final boolean all) {
        this.registry.snapshot(rb, all);
    }
    
    MetricsSink sink() {
        return this.sink;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MetricsSinkAdapter.class);
    }
    
    static class WaitableMetricsBuffer extends MetricsBuffer
    {
        private final Semaphore notificationSemaphore;
        
        public WaitableMetricsBuffer(final MetricsBuffer metricsBuffer) {
            super(metricsBuffer);
            this.notificationSemaphore = new Semaphore(0);
        }
        
        public boolean waitTillNotified(final long millisecondsToWait) {
            try {
                return this.notificationSemaphore.tryAcquire(millisecondsToWait, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                return false;
            }
        }
        
        public void notifyAnyWaiters() {
            this.notificationSemaphore.release();
        }
    }
}
