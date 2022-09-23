// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.metrics2.util.MBeans;
import javax.management.ObjectName;
import java.lang.ref.WeakReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.metrics2.util.Metrics2Util;
import org.apache.hadoop.metrics2.lib.Interns;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.MetricsCollector;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.util.concurrent.TimeUnit;
import java.util.TimerTask;
import java.util.Timer;
import com.google.common.base.Preconditions;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import com.google.common.util.concurrent.AtomicDoubleArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.apache.hadoop.metrics2.MetricsSource;

public class DecayRpcScheduler implements RpcScheduler, DecayRpcSchedulerMXBean, MetricsSource
{
    public static final String IPC_SCHEDULER_DECAYSCHEDULER_PERIOD_KEY = "decay-scheduler.period-ms";
    public static final long IPC_SCHEDULER_DECAYSCHEDULER_PERIOD_DEFAULT = 5000L;
    @Deprecated
    public static final String IPC_FCQ_DECAYSCHEDULER_PERIOD_KEY = "faircallqueue.decay-scheduler.period-ms";
    public static final String IPC_SCHEDULER_DECAYSCHEDULER_FACTOR_KEY = "decay-scheduler.decay-factor";
    public static final double IPC_SCHEDULER_DECAYSCHEDULER_FACTOR_DEFAULT = 0.5;
    @Deprecated
    public static final String IPC_FCQ_DECAYSCHEDULER_FACTOR_KEY = "faircallqueue.decay-scheduler.decay-factor";
    public static final String IPC_DECAYSCHEDULER_THRESHOLDS_KEY = "decay-scheduler.thresholds";
    @Deprecated
    public static final String IPC_FCQ_DECAYSCHEDULER_THRESHOLDS_KEY = "faircallqueue.decay-scheduler.thresholds";
    public static final String DECAYSCHEDULER_UNKNOWN_IDENTITY = "IdentityProvider.Unknown";
    public static final String IPC_DECAYSCHEDULER_BACKOFF_RESPONSETIME_ENABLE_KEY = "decay-scheduler.backoff.responsetime.enable";
    public static final Boolean IPC_DECAYSCHEDULER_BACKOFF_RESPONSETIME_ENABLE_DEFAULT;
    public static final String IPC_DECAYSCHEDULER_BACKOFF_RESPONSETIME_THRESHOLDS_KEY = "decay-scheduler.backoff.responsetime.thresholds";
    public static final String DECAYSCHEDULER_METRICS_TOP_USER_COUNT = "decay-scheduler.metrics.top.user.count";
    public static final int DECAYSCHEDULER_METRICS_TOP_USER_COUNT_DEFAULT = 10;
    public static final Logger LOG;
    private static final ObjectWriter WRITER;
    private final ConcurrentHashMap<Object, List<AtomicLong>> callCounts;
    private final AtomicLong totalDecayedCallCount;
    private final AtomicLong totalRawCallCount;
    private final AtomicLongArray responseTimeCountInCurrWindow;
    private final AtomicLongArray responseTimeTotalInCurrWindow;
    private final AtomicDoubleArray responseTimeAvgInLastWindow;
    private final AtomicLongArray responseTimeCountInLastWindow;
    private final AtomicReference<Map<Object, Integer>> scheduleCacheRef;
    private final long decayPeriodMillis;
    private final double decayFactor;
    private final int numLevels;
    private final double[] thresholds;
    private final IdentityProvider identityProvider;
    private final boolean backOffByResponseTimeEnabled;
    private final long[] backOffResponseTimeThresholds;
    private final String namespace;
    private final int topUsersCount;
    private static final double PRECISION = 1.0E-4;
    private MetricsProxy metricsProxy;
    
    public DecayRpcScheduler(final int numLevels, final String ns, final Configuration conf) {
        this.callCounts = new ConcurrentHashMap<Object, List<AtomicLong>>();
        this.totalDecayedCallCount = new AtomicLong();
        this.totalRawCallCount = new AtomicLong();
        this.scheduleCacheRef = new AtomicReference<Map<Object, Integer>>();
        if (numLevels < 1) {
            throw new IllegalArgumentException("Number of Priority Levels must be at least 1");
        }
        this.numLevels = numLevels;
        this.namespace = ns;
        this.decayFactor = parseDecayFactor(ns, conf);
        this.decayPeriodMillis = parseDecayPeriodMillis(ns, conf);
        this.identityProvider = this.parseIdentityProvider(ns, conf);
        this.thresholds = parseThresholds(ns, conf, numLevels);
        this.backOffByResponseTimeEnabled = parseBackOffByResponseTimeEnabled(ns, conf);
        this.backOffResponseTimeThresholds = parseBackOffResponseTimeThreshold(ns, conf, numLevels);
        this.responseTimeTotalInCurrWindow = new AtomicLongArray(numLevels);
        this.responseTimeCountInCurrWindow = new AtomicLongArray(numLevels);
        this.responseTimeAvgInLastWindow = new AtomicDoubleArray(numLevels);
        this.responseTimeCountInLastWindow = new AtomicLongArray(numLevels);
        this.topUsersCount = conf.getInt("decay-scheduler.metrics.top.user.count", 10);
        Preconditions.checkArgument(this.topUsersCount > 0, (Object)"the number of top users for scheduler metrics must be at least 1");
        final Timer timer = new Timer();
        final DecayTask task = new DecayTask(this, timer);
        timer.scheduleAtFixedRate(task, this.decayPeriodMillis, this.decayPeriodMillis);
        this.metricsProxy = MetricsProxy.getInstance(ns, numLevels, this);
        this.recomputeScheduleCache();
    }
    
    private IdentityProvider parseIdentityProvider(final String ns, final Configuration conf) {
        final List<IdentityProvider> providers = conf.getInstances(ns + "." + "identity-provider.impl", IdentityProvider.class);
        if (providers.size() < 1) {
            DecayRpcScheduler.LOG.info("IdentityProvider not specified, defaulting to UserIdentityProvider");
            return new UserIdentityProvider();
        }
        return providers.get(0);
    }
    
    private static double parseDecayFactor(final String ns, final Configuration conf) {
        double factor = conf.getDouble(ns + "." + "faircallqueue.decay-scheduler.decay-factor", 0.0);
        if (factor == 0.0) {
            factor = conf.getDouble(ns + "." + "decay-scheduler.decay-factor", 0.5);
        }
        else if (factor > 0.0 && factor < 1.0) {
            DecayRpcScheduler.LOG.warn("faircallqueue.decay-scheduler.decay-factor is deprecated. Please use decay-scheduler.decay-factor.");
        }
        if (factor <= 0.0 || factor >= 1.0) {
            throw new IllegalArgumentException("Decay Factor must be between 0 and 1");
        }
        return factor;
    }
    
    private static long parseDecayPeriodMillis(final String ns, final Configuration conf) {
        long period = conf.getLong(ns + "." + "faircallqueue.decay-scheduler.period-ms", 0L);
        if (period == 0L) {
            period = conf.getLong(ns + "." + "decay-scheduler.period-ms", 5000L);
        }
        else if (period > 0L) {
            DecayRpcScheduler.LOG.warn("faircallqueue.decay-scheduler.period-ms is deprecated. Please use decay-scheduler.period-ms");
        }
        if (period <= 0L) {
            throw new IllegalArgumentException("Period millis must be >= 0");
        }
        return period;
    }
    
    private static double[] parseThresholds(final String ns, final Configuration conf, final int numLevels) {
        int[] percentages = conf.getInts(ns + "." + "faircallqueue.decay-scheduler.thresholds");
        if (percentages.length == 0) {
            percentages = conf.getInts(ns + "." + "decay-scheduler.thresholds");
            if (percentages.length == 0) {
                return getDefaultThresholds(numLevels);
            }
        }
        else {
            DecayRpcScheduler.LOG.warn("faircallqueue.decay-scheduler.thresholds is deprecated. Please use decay-scheduler.thresholds");
        }
        if (percentages.length != numLevels - 1) {
            throw new IllegalArgumentException("Number of thresholds should be " + (numLevels - 1) + ". Was: " + percentages.length);
        }
        final double[] decimals = new double[percentages.length];
        for (int i = 0; i < percentages.length; ++i) {
            decimals[i] = percentages[i] / 100.0;
        }
        return decimals;
    }
    
    private static double[] getDefaultThresholds(final int numLevels) {
        final double[] ret = new double[numLevels - 1];
        final double div = Math.pow(2.0, numLevels - 1);
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = Math.pow(2.0, i) / div;
        }
        return ret;
    }
    
    private static long[] parseBackOffResponseTimeThreshold(final String ns, final Configuration conf, final int numLevels) {
        final long[] responseTimeThresholds = conf.getTimeDurations(ns + "." + "decay-scheduler.backoff.responsetime.thresholds", TimeUnit.MILLISECONDS);
        if (responseTimeThresholds.length == 0) {
            return getDefaultBackOffResponseTimeThresholds(numLevels);
        }
        if (responseTimeThresholds.length != numLevels) {
            throw new IllegalArgumentException("responseTimeThresholds must match with the number of priority levels");
        }
        for (final long responseTimeThreshold : responseTimeThresholds) {
            if (responseTimeThreshold <= 0L) {
                throw new IllegalArgumentException("responseTimeThreshold millis must be >= 0");
            }
        }
        return responseTimeThresholds;
    }
    
    private static long[] getDefaultBackOffResponseTimeThresholds(final int numLevels) {
        final long[] ret = new long[numLevels];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = 10000 * (i + 1);
        }
        return ret;
    }
    
    private static Boolean parseBackOffByResponseTimeEnabled(final String ns, final Configuration conf) {
        return conf.getBoolean(ns + "." + "decay-scheduler.backoff.responsetime.enable", DecayRpcScheduler.IPC_DECAYSCHEDULER_BACKOFF_RESPONSETIME_ENABLE_DEFAULT);
    }
    
    private void decayCurrentCounts() {
        DecayRpcScheduler.LOG.debug("Start to decay current counts.");
        try {
            long totalDecayedCount = 0L;
            long totalRawCount = 0L;
            final Iterator<Map.Entry<Object, List<AtomicLong>>> it = this.callCounts.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<Object, List<AtomicLong>> entry = it.next();
                final AtomicLong decayedCount = entry.getValue().get(0);
                final AtomicLong rawCount = entry.getValue().get(1);
                totalRawCount += rawCount.get();
                final long currentValue = decayedCount.get();
                final long nextValue = (long)(currentValue * this.decayFactor);
                totalDecayedCount += nextValue;
                decayedCount.set(nextValue);
                DecayRpcScheduler.LOG.debug("Decaying counts for the user: {}, its decayedCount: {}, rawCount: {}", entry.getKey(), nextValue, rawCount.get());
                if (nextValue == 0L) {
                    DecayRpcScheduler.LOG.debug("The decayed count for the user {} is zero and being cleaned.", entry.getKey());
                    it.remove();
                }
            }
            this.totalDecayedCallCount.set(totalDecayedCount);
            this.totalRawCallCount.set(totalRawCount);
            DecayRpcScheduler.LOG.debug("After decaying the stored counts, totalDecayedCount: {}, totalRawCallCount: {}.", (Object)totalDecayedCount, totalRawCount);
            this.recomputeScheduleCache();
            this.updateAverageResponseTime(true);
        }
        catch (Exception ex) {
            DecayRpcScheduler.LOG.error("decayCurrentCounts exception: " + ExceptionUtils.getStackTrace(ex));
            throw ex;
        }
    }
    
    private void recomputeScheduleCache() {
        final Map<Object, Integer> nextCache = new HashMap<Object, Integer>();
        for (final Map.Entry<Object, List<AtomicLong>> entry : this.callCounts.entrySet()) {
            final Object id = entry.getKey();
            final AtomicLong value = entry.getValue().get(0);
            final long snapshot = value.get();
            final int computedLevel = this.computePriorityLevel(snapshot);
            nextCache.put(id, computedLevel);
        }
        this.scheduleCacheRef.set(Collections.unmodifiableMap((Map<?, ? extends Integer>)nextCache));
    }
    
    private long getAndIncrementCallCounts(final Object identity) throws InterruptedException {
        List<AtomicLong> count = this.callCounts.get(identity);
        if (count == null) {
            count = new ArrayList<AtomicLong>(2);
            count.add(new AtomicLong(0L));
            count.add(new AtomicLong(0L));
            final List<AtomicLong> otherCount = this.callCounts.putIfAbsent(identity, count);
            if (otherCount != null) {
                count = otherCount;
            }
        }
        this.totalDecayedCallCount.getAndIncrement();
        this.totalRawCallCount.getAndIncrement();
        count.get(1).getAndIncrement();
        return count.get(0).getAndIncrement();
    }
    
    private int computePriorityLevel(final long occurrences) {
        final long totalCallSnapshot = this.totalDecayedCallCount.get();
        double proportion = 0.0;
        if (totalCallSnapshot > 0L) {
            proportion = occurrences / (double)totalCallSnapshot;
        }
        for (int i = this.numLevels - 1; i > 0; --i) {
            if (proportion >= this.thresholds[i - 1]) {
                return i;
            }
        }
        return 0;
    }
    
    private int cachedOrComputedPriorityLevel(final Object identity) {
        try {
            final long occurrences = this.getAndIncrementCallCounts(identity);
            final Map<Object, Integer> scheduleCache = this.scheduleCacheRef.get();
            if (scheduleCache != null) {
                final Integer priority = scheduleCache.get(identity);
                if (priority != null) {
                    DecayRpcScheduler.LOG.debug("Cache priority for: {} with priority: {}", identity, priority);
                    return priority;
                }
            }
            final int priority2 = this.computePriorityLevel(occurrences);
            DecayRpcScheduler.LOG.debug("compute priority for " + identity + " priority " + priority2);
            return priority2;
        }
        catch (InterruptedException ie) {
            DecayRpcScheduler.LOG.warn("Caught InterruptedException, returning low priority level");
            DecayRpcScheduler.LOG.debug("Fallback priority for: {} with priority: {}", identity, this.numLevels - 1);
            return this.numLevels - 1;
        }
    }
    
    @Override
    public int getPriorityLevel(final Schedulable obj) {
        String identity = this.identityProvider.makeIdentity(obj);
        if (identity == null) {
            identity = "IdentityProvider.Unknown";
        }
        return this.cachedOrComputedPriorityLevel(identity);
    }
    
    @Override
    public boolean shouldBackOff(final Schedulable obj) {
        Boolean backOff = false;
        if (this.backOffByResponseTimeEnabled) {
            final int priorityLevel = obj.getPriorityLevel();
            if (DecayRpcScheduler.LOG.isDebugEnabled()) {
                final double[] responseTimes = this.getAverageResponseTime();
                DecayRpcScheduler.LOG.debug("Current Caller: {}  Priority: {} ", obj.getUserGroupInformation().getUserName(), obj.getPriorityLevel());
                for (int i = 0; i < this.numLevels; ++i) {
                    DecayRpcScheduler.LOG.debug("Queue: {} responseTime: {} backoffThreshold: {}", i, responseTimes[i], this.backOffResponseTimeThresholds[i]);
                }
            }
            for (int j = 0; j < priorityLevel + 1; ++j) {
                if (this.responseTimeAvgInLastWindow.get(j) > this.backOffResponseTimeThresholds[j]) {
                    backOff = true;
                    break;
                }
            }
        }
        return backOff;
    }
    
    @Override
    public void addResponseTime(final String name, final int priorityLevel, final int queueTime, final int processingTime) {
        this.responseTimeCountInCurrWindow.getAndIncrement(priorityLevel);
        this.responseTimeTotalInCurrWindow.getAndAdd(priorityLevel, queueTime + processingTime);
        if (DecayRpcScheduler.LOG.isDebugEnabled()) {
            DecayRpcScheduler.LOG.debug("addResponseTime for call: {}  priority: {} queueTime: {} processingTime: {} ", name, priorityLevel, queueTime, processingTime);
        }
    }
    
    void updateAverageResponseTime(final boolean enableDecay) {
        for (int i = 0; i < this.numLevels; ++i) {
            double averageResponseTime = 0.0;
            final long totalResponseTime = this.responseTimeTotalInCurrWindow.get(i);
            final long responseTimeCount = this.responseTimeCountInCurrWindow.get(i);
            if (responseTimeCount > 0L) {
                averageResponseTime = totalResponseTime / (double)responseTimeCount;
            }
            final double lastAvg = this.responseTimeAvgInLastWindow.get(i);
            if (lastAvg > 1.0E-4 || averageResponseTime > 1.0E-4) {
                if (enableDecay) {
                    final double decayed = this.decayFactor * lastAvg + averageResponseTime;
                    this.responseTimeAvgInLastWindow.set(i, decayed);
                }
                else {
                    this.responseTimeAvgInLastWindow.set(i, averageResponseTime);
                }
            }
            else {
                this.responseTimeAvgInLastWindow.set(i, 0.0);
            }
            this.responseTimeCountInLastWindow.set(i, responseTimeCount);
            if (DecayRpcScheduler.LOG.isDebugEnabled()) {
                DecayRpcScheduler.LOG.debug("updateAverageResponseTime queue: {} Average: {} Count: {}", i, averageResponseTime, responseTimeCount);
            }
            this.responseTimeTotalInCurrWindow.set(i, 0L);
            this.responseTimeCountInCurrWindow.set(i, 0L);
        }
    }
    
    @VisibleForTesting
    public double getDecayFactor() {
        return this.decayFactor;
    }
    
    @VisibleForTesting
    public long getDecayPeriodMillis() {
        return this.decayPeriodMillis;
    }
    
    @VisibleForTesting
    public double[] getThresholds() {
        return this.thresholds;
    }
    
    @VisibleForTesting
    public void forceDecay() {
        this.decayCurrentCounts();
    }
    
    @VisibleForTesting
    public Map<Object, Long> getCallCountSnapshot() {
        final HashMap<Object, Long> snapshot = new HashMap<Object, Long>();
        for (final Map.Entry<Object, List<AtomicLong>> entry : this.callCounts.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().get(0).get());
        }
        return Collections.unmodifiableMap((Map<?, ? extends Long>)snapshot);
    }
    
    @VisibleForTesting
    public long getTotalCallSnapshot() {
        return this.totalDecayedCallCount.get();
    }
    
    @Override
    public int getUniqueIdentityCount() {
        return this.callCounts.size();
    }
    
    @Override
    public long getTotalCallVolume() {
        return this.totalDecayedCallCount.get();
    }
    
    public long getTotalRawCallVolume() {
        return this.totalRawCallCount.get();
    }
    
    @Override
    public long[] getResponseTimeCountInLastWindow() {
        final long[] ret = new long[this.responseTimeCountInLastWindow.length()];
        for (int i = 0; i < this.responseTimeCountInLastWindow.length(); ++i) {
            ret[i] = this.responseTimeCountInLastWindow.get(i);
        }
        return ret;
    }
    
    @Override
    public double[] getAverageResponseTime() {
        final double[] ret = new double[this.responseTimeAvgInLastWindow.length()];
        for (int i = 0; i < this.responseTimeAvgInLastWindow.length(); ++i) {
            ret[i] = this.responseTimeAvgInLastWindow.get(i);
        }
        return ret;
    }
    
    @Override
    public void getMetrics(final MetricsCollector collector, final boolean all) {
        try {
            final MetricsRecordBuilder rb = collector.addRecord(this.getClass().getName()).setContext(this.namespace);
            this.addDecayedCallVolume(rb);
            this.addUniqueIdentityCount(rb);
            this.addTopNCallerSummary(rb);
            this.addAvgResponseTimePerPriority(rb);
            this.addCallVolumePerPriority(rb);
            this.addRawCallVolume(rb);
        }
        catch (Exception e) {
            DecayRpcScheduler.LOG.warn("Exception thrown while metric collection. Exception : " + e.getMessage());
        }
    }
    
    private void addUniqueIdentityCount(final MetricsRecordBuilder rb) {
        rb.addCounter(Interns.info("UniqueCallers", "Total unique callers"), this.getUniqueIdentityCount());
    }
    
    private void addDecayedCallVolume(final MetricsRecordBuilder rb) {
        rb.addCounter(Interns.info("DecayedCallVolume", "Decayed Total incoming Call Volume"), this.getTotalCallVolume());
    }
    
    private void addRawCallVolume(final MetricsRecordBuilder rb) {
        rb.addCounter(Interns.info("CallVolume", "Raw Total incoming Call Volume"), this.getTotalRawCallVolume());
    }
    
    private void addCallVolumePerPriority(final MetricsRecordBuilder rb) {
        for (int i = 0; i < this.responseTimeCountInLastWindow.length(); ++i) {
            rb.addGauge(Interns.info("Priority." + i + ".CompletedCallVolume", "Completed Call volume of priority " + i), this.responseTimeCountInLastWindow.get(i));
        }
    }
    
    private void addAvgResponseTimePerPriority(final MetricsRecordBuilder rb) {
        for (int i = 0; i < this.responseTimeAvgInLastWindow.length(); ++i) {
            rb.addGauge(Interns.info("Priority." + i + ".AvgResponseTime", "Average response time of priority " + i), this.responseTimeAvgInLastWindow.get(i));
        }
    }
    
    private void addTopNCallerSummary(final MetricsRecordBuilder rb) {
        final Metrics2Util.TopN topNCallers = this.getTopCallers(this.topUsersCount);
        final Map<Object, Integer> decisions = this.scheduleCacheRef.get();
        for (int actualCallerCount = topNCallers.size(), i = 0; i < actualCallerCount; ++i) {
            final Metrics2Util.NameValuePair entry = topNCallers.poll();
            final String topCaller = "Caller(" + entry.getName() + ")";
            final String topCallerVolume = topCaller + ".Volume";
            final String topCallerPriority = topCaller + ".Priority";
            rb.addCounter(Interns.info(topCallerVolume, topCallerVolume), entry.getValue());
            final Integer priority = decisions.get(entry.getName());
            if (priority != null) {
                rb.addCounter(Interns.info(topCallerPriority, topCallerPriority), priority);
            }
        }
    }
    
    private Metrics2Util.TopN getTopCallers(final int n) {
        final Metrics2Util.TopN topNCallers = new Metrics2Util.TopN(n);
        for (final Map.Entry<Object, List<AtomicLong>> entry : this.callCounts.entrySet()) {
            final String caller = entry.getKey().toString();
            final Long count = entry.getValue().get(1).get();
            if (count > 0L) {
                topNCallers.offer(new Metrics2Util.NameValuePair(caller, count));
            }
        }
        return topNCallers;
    }
    
    @Override
    public String getSchedulingDecisionSummary() {
        final Map<Object, Integer> decisions = this.scheduleCacheRef.get();
        if (decisions == null) {
            return "{}";
        }
        try {
            return DecayRpcScheduler.WRITER.writeValueAsString(decisions);
        }
        catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    @Override
    public String getCallVolumeSummary() {
        try {
            return DecayRpcScheduler.WRITER.writeValueAsString(this.getDecayedCallCounts());
        }
        catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private Map<Object, Long> getDecayedCallCounts() {
        final Map<Object, Long> decayedCallCounts = new HashMap<Object, Long>(this.callCounts.size());
        for (final Map.Entry<Object, List<AtomicLong>> entry : this.callCounts.entrySet()) {
            final Object user = entry.getKey();
            final Long decayedCount = entry.getValue().get(0).get();
            if (decayedCount > 0L) {
                decayedCallCounts.put(user, decayedCount);
            }
        }
        return decayedCallCounts;
    }
    
    @Override
    public void stop() {
        this.metricsProxy.unregisterSource(this.namespace);
        MetricsProxy.removeInstance(this.namespace);
    }
    
    static {
        IPC_DECAYSCHEDULER_BACKOFF_RESPONSETIME_ENABLE_DEFAULT = false;
        LOG = LoggerFactory.getLogger(DecayRpcScheduler.class);
        WRITER = new ObjectMapper().writer();
    }
    
    public static class DecayTask extends TimerTask
    {
        private WeakReference<DecayRpcScheduler> schedulerRef;
        private Timer timer;
        
        public DecayTask(final DecayRpcScheduler scheduler, final Timer timer) {
            this.schedulerRef = new WeakReference<DecayRpcScheduler>(scheduler);
            this.timer = timer;
        }
        
        @Override
        public void run() {
            final DecayRpcScheduler sched = this.schedulerRef.get();
            if (sched != null) {
                sched.decayCurrentCounts();
            }
            else {
                this.timer.cancel();
                this.timer.purge();
            }
        }
    }
    
    public static final class MetricsProxy implements DecayRpcSchedulerMXBean, MetricsSource
    {
        private static final HashMap<String, MetricsProxy> INSTANCES;
        private WeakReference<DecayRpcScheduler> delegate;
        private double[] averageResponseTimeDefault;
        private long[] callCountInLastWindowDefault;
        private ObjectName decayRpcSchedulerInfoBeanName;
        
        private MetricsProxy(final String namespace, final int numLevels, final DecayRpcScheduler drs) {
            this.averageResponseTimeDefault = new double[numLevels];
            this.callCountInLastWindowDefault = new long[numLevels];
            this.setDelegate(drs);
            this.decayRpcSchedulerInfoBeanName = MBeans.register(namespace, "DecayRpcScheduler", this);
            this.registerMetrics2Source(namespace);
        }
        
        public static synchronized MetricsProxy getInstance(final String namespace, final int numLevels, final DecayRpcScheduler drs) {
            MetricsProxy mp = MetricsProxy.INSTANCES.get(namespace);
            if (mp == null) {
                mp = new MetricsProxy(namespace, numLevels, drs);
                MetricsProxy.INSTANCES.put(namespace, mp);
            }
            else if (drs != mp.delegate.get()) {
                mp.setDelegate(drs);
            }
            return mp;
        }
        
        public static synchronized void removeInstance(final String namespace) {
            MetricsProxy.INSTANCES.remove(namespace);
        }
        
        public void setDelegate(final DecayRpcScheduler obj) {
            this.delegate = new WeakReference<DecayRpcScheduler>(obj);
        }
        
        void registerMetrics2Source(final String namespace) {
            final String name = "DecayRpcSchedulerMetrics2." + namespace;
            DefaultMetricsSystem.instance().register(name, name, this);
        }
        
        void unregisterSource(final String namespace) {
            final String name = "DecayRpcSchedulerMetrics2." + namespace;
            DefaultMetricsSystem.instance().unregisterSource(name);
            if (this.decayRpcSchedulerInfoBeanName != null) {
                MBeans.unregister(this.decayRpcSchedulerInfoBeanName);
            }
        }
        
        @Override
        public String getSchedulingDecisionSummary() {
            final DecayRpcScheduler scheduler = this.delegate.get();
            if (scheduler == null) {
                return "No Active Scheduler";
            }
            return scheduler.getSchedulingDecisionSummary();
        }
        
        @Override
        public String getCallVolumeSummary() {
            final DecayRpcScheduler scheduler = this.delegate.get();
            if (scheduler == null) {
                return "No Active Scheduler";
            }
            return scheduler.getCallVolumeSummary();
        }
        
        @Override
        public int getUniqueIdentityCount() {
            final DecayRpcScheduler scheduler = this.delegate.get();
            if (scheduler == null) {
                return -1;
            }
            return scheduler.getUniqueIdentityCount();
        }
        
        @Override
        public long getTotalCallVolume() {
            final DecayRpcScheduler scheduler = this.delegate.get();
            if (scheduler == null) {
                return -1L;
            }
            return scheduler.getTotalCallVolume();
        }
        
        @Override
        public double[] getAverageResponseTime() {
            final DecayRpcScheduler scheduler = this.delegate.get();
            if (scheduler == null) {
                return this.averageResponseTimeDefault;
            }
            return scheduler.getAverageResponseTime();
        }
        
        @Override
        public long[] getResponseTimeCountInLastWindow() {
            final DecayRpcScheduler scheduler = this.delegate.get();
            if (scheduler == null) {
                return this.callCountInLastWindowDefault;
            }
            return scheduler.getResponseTimeCountInLastWindow();
        }
        
        @Override
        public void getMetrics(final MetricsCollector collector, final boolean all) {
            final DecayRpcScheduler scheduler = this.delegate.get();
            if (scheduler != null) {
                scheduler.getMetrics(collector, all);
            }
        }
        
        static {
            INSTANCES = new HashMap<String, MetricsProxy>();
        }
    }
}
