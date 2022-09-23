// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import org.apache.hadoop.metrics2.util.SampleStat;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import java.lang.reflect.Method;
import com.google.common.collect.Sets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Set;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MutableRatesWithAggregation extends MutableMetric
{
    static final Logger LOG;
    private final Map<String, MutableRate> globalMetrics;
    private final Set<Class<?>> protocolCache;
    private final ConcurrentLinkedDeque<WeakReference<ConcurrentMap<String, ThreadSafeSampleStat>>> weakReferenceQueue;
    private final ThreadLocal<ConcurrentMap<String, ThreadSafeSampleStat>> threadLocalMetricsMap;
    
    public MutableRatesWithAggregation() {
        this.globalMetrics = new ConcurrentHashMap<String, MutableRate>();
        this.protocolCache = (Set<Class<?>>)Sets.newHashSet();
        this.weakReferenceQueue = new ConcurrentLinkedDeque<WeakReference<ConcurrentMap<String, ThreadSafeSampleStat>>>();
        this.threadLocalMetricsMap = new ThreadLocal<ConcurrentMap<String, ThreadSafeSampleStat>>();
    }
    
    public void init(final Class<?> protocol) {
        if (this.protocolCache.contains(protocol)) {
            return;
        }
        this.protocolCache.add(protocol);
        for (final Method method : protocol.getDeclaredMethods()) {
            final String name = method.getName();
            MutableRatesWithAggregation.LOG.debug(name);
            this.addMetricIfNotExists(name);
        }
    }
    
    public void add(final String name, final long elapsed) {
        ConcurrentMap<String, ThreadSafeSampleStat> localStats = this.threadLocalMetricsMap.get();
        if (localStats == null) {
            localStats = new ConcurrentHashMap<String, ThreadSafeSampleStat>();
            this.threadLocalMetricsMap.set(localStats);
            this.weakReferenceQueue.add(new WeakReference<ConcurrentMap<String, ThreadSafeSampleStat>>(localStats));
        }
        ThreadSafeSampleStat stat = localStats.get(name);
        if (stat == null) {
            stat = new ThreadSafeSampleStat();
            localStats.put(name, stat);
        }
        stat.add((double)elapsed);
    }
    
    @Override
    public synchronized void snapshot(final MetricsRecordBuilder rb, final boolean all) {
        final Iterator<WeakReference<ConcurrentMap<String, ThreadSafeSampleStat>>> iter = this.weakReferenceQueue.iterator();
        while (iter.hasNext()) {
            final ConcurrentMap<String, ThreadSafeSampleStat> map = iter.next().get();
            if (map == null) {
                iter.remove();
            }
            else {
                this.aggregateLocalStatesToGlobalMetrics(map);
            }
        }
        for (final MutableRate globalMetric : this.globalMetrics.values()) {
            globalMetric.snapshot(rb, all);
        }
    }
    
    synchronized void collectThreadLocalStates() {
        final ConcurrentMap<String, ThreadSafeSampleStat> localStats = this.threadLocalMetricsMap.get();
        if (localStats != null) {
            this.aggregateLocalStatesToGlobalMetrics(localStats);
        }
    }
    
    private void aggregateLocalStatesToGlobalMetrics(final ConcurrentMap<String, ThreadSafeSampleStat> localStats) {
        for (final Map.Entry<String, ThreadSafeSampleStat> entry : localStats.entrySet()) {
            final String name = entry.getKey();
            final MutableRate globalMetric = this.addMetricIfNotExists(name);
            entry.getValue().snapshotInto(globalMetric);
        }
    }
    
    Map<String, MutableRate> getGlobalMetrics() {
        return this.globalMetrics;
    }
    
    private synchronized MutableRate addMetricIfNotExists(final String name) {
        MutableRate metric = this.globalMetrics.get(name);
        if (metric == null) {
            metric = new MutableRate(name, name, false);
            this.globalMetrics.put(name, metric);
        }
        return metric;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MutableRatesWithAggregation.class);
    }
    
    private static class ThreadSafeSampleStat
    {
        private SampleStat stat;
        
        private ThreadSafeSampleStat() {
            this.stat = new SampleStat();
        }
        
        synchronized void add(final double x) {
            this.stat.add(x);
        }
        
        synchronized void snapshotInto(final MutableRate metric) {
            if (this.stat.numSamples() > 0L) {
                metric.add(this.stat.numSamples(), Math.round(this.stat.total()));
                this.stat.reset();
            }
        }
    }
}
