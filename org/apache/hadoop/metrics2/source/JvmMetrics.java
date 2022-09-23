// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.source;

import org.apache.hadoop.log.metrics.EventCounter;
import java.lang.management.ThreadInfo;
import org.apache.hadoop.metrics2.lib.Interns;
import java.util.Iterator;
import java.lang.management.MemoryUsage;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.impl.MsInfo;
import org.apache.hadoop.metrics2.MetricsCollector;
import com.google.common.base.Preconditions;
import java.lang.management.ManagementFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.util.GcTimeMonitor;
import org.apache.hadoop.metrics2.MetricsInfo;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.util.JvmPauseMonitor;
import java.lang.management.ThreadMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.util.List;
import java.lang.management.MemoryMXBean;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.metrics2.MetricsSource;

@InterfaceAudience.Private
public class JvmMetrics implements MetricsSource
{
    static final float M = 1048576.0f;
    public static final float MEMORY_MAX_UNLIMITED_MB = -1.0f;
    final MemoryMXBean memoryMXBean;
    final List<GarbageCollectorMXBean> gcBeans;
    final ThreadMXBean threadMXBean;
    final String processName;
    final String sessionId;
    private JvmPauseMonitor pauseMonitor;
    final ConcurrentHashMap<String, MetricsInfo[]> gcInfoCache;
    private GcTimeMonitor gcTimeMonitor;
    
    @VisibleForTesting
    public synchronized void registerIfNeeded() {
        final MetricsSystem ms = DefaultMetricsSystem.instance();
        if (ms.getSource("JvmMetrics") == null) {
            ms.register(JvmMetricsInfo.JvmMetrics.name(), JvmMetricsInfo.JvmMetrics.description(), this);
        }
    }
    
    @VisibleForTesting
    JvmMetrics(final String processName, final String sessionId) {
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.pauseMonitor = null;
        this.gcInfoCache = new ConcurrentHashMap<String, MetricsInfo[]>();
        this.gcTimeMonitor = null;
        this.processName = processName;
        this.sessionId = sessionId;
    }
    
    public void setPauseMonitor(final JvmPauseMonitor pauseMonitor) {
        this.pauseMonitor = pauseMonitor;
    }
    
    public void setGcTimeMonitor(final GcTimeMonitor gcTimeMonitor) {
        Preconditions.checkNotNull(gcTimeMonitor);
        this.gcTimeMonitor = gcTimeMonitor;
    }
    
    public static JvmMetrics create(final String processName, final String sessionId, final MetricsSystem ms) {
        return ms.register(JvmMetricsInfo.JvmMetrics.name(), JvmMetricsInfo.JvmMetrics.description(), new JvmMetrics(processName, sessionId));
    }
    
    public static void reattach(final MetricsSystem ms, final JvmMetrics jvmMetrics) {
        ms.register(JvmMetricsInfo.JvmMetrics.name(), JvmMetricsInfo.JvmMetrics.description(), jvmMetrics);
    }
    
    public static JvmMetrics initSingleton(final String processName, final String sessionId) {
        return Singleton.INSTANCE.init(processName, sessionId);
    }
    
    public static void shutdownSingleton() {
        Singleton.INSTANCE.shutdown();
    }
    
    @Override
    public void getMetrics(final MetricsCollector collector, final boolean all) {
        final MetricsRecordBuilder rb = collector.addRecord(JvmMetricsInfo.JvmMetrics).setContext("jvm").tag(MsInfo.ProcessName, this.processName).tag(MsInfo.SessionId, this.sessionId);
        this.getMemoryUsage(rb);
        this.getGcUsage(rb);
        this.getThreadUsage(rb);
        this.getEventCounters(rb);
    }
    
    private void getMemoryUsage(final MetricsRecordBuilder rb) {
        final MemoryUsage memNonHeap = this.memoryMXBean.getNonHeapMemoryUsage();
        final MemoryUsage memHeap = this.memoryMXBean.getHeapMemoryUsage();
        final Runtime runtime = Runtime.getRuntime();
        rb.addGauge(JvmMetricsInfo.MemNonHeapUsedM, memNonHeap.getUsed() / 1048576.0f).addGauge(JvmMetricsInfo.MemNonHeapCommittedM, memNonHeap.getCommitted() / 1048576.0f).addGauge(JvmMetricsInfo.MemNonHeapMaxM, this.calculateMaxMemoryUsage(memNonHeap)).addGauge(JvmMetricsInfo.MemHeapUsedM, memHeap.getUsed() / 1048576.0f).addGauge(JvmMetricsInfo.MemHeapCommittedM, memHeap.getCommitted() / 1048576.0f).addGauge(JvmMetricsInfo.MemHeapMaxM, this.calculateMaxMemoryUsage(memHeap)).addGauge(JvmMetricsInfo.MemMaxM, runtime.maxMemory() / 1048576.0f);
    }
    
    private float calculateMaxMemoryUsage(final MemoryUsage memHeap) {
        final long max = memHeap.getMax();
        if (max == -1L) {
            return -1.0f;
        }
        return max / 1048576.0f;
    }
    
    private void getGcUsage(final MetricsRecordBuilder rb) {
        long count = 0L;
        long timeMillis = 0L;
        for (final GarbageCollectorMXBean gcBean : this.gcBeans) {
            final long c = gcBean.getCollectionCount();
            final long t = gcBean.getCollectionTime();
            final MetricsInfo[] gcInfo = this.getGcInfo(gcBean.getName());
            rb.addCounter(gcInfo[0], c).addCounter(gcInfo[1], t);
            count += c;
            timeMillis += t;
        }
        rb.addCounter(JvmMetricsInfo.GcCount, count).addCounter(JvmMetricsInfo.GcTimeMillis, timeMillis);
        if (this.pauseMonitor != null) {
            rb.addCounter(JvmMetricsInfo.GcNumWarnThresholdExceeded, this.pauseMonitor.getNumGcWarnThresholdExceeded());
            rb.addCounter(JvmMetricsInfo.GcNumInfoThresholdExceeded, this.pauseMonitor.getNumGcInfoThresholdExceeded());
            rb.addCounter(JvmMetricsInfo.GcTotalExtraSleepTime, this.pauseMonitor.getTotalGcExtraSleepTime());
        }
        if (this.gcTimeMonitor != null) {
            rb.addGauge(JvmMetricsInfo.GcTimePercentage, this.gcTimeMonitor.getLatestGcData().getGcTimePercentage());
        }
    }
    
    private MetricsInfo[] getGcInfo(final String gcName) {
        MetricsInfo[] gcInfo = this.gcInfoCache.get(gcName);
        if (gcInfo == null) {
            gcInfo = new MetricsInfo[] { Interns.info("GcCount" + gcName, "GC Count for " + gcName), Interns.info("GcTimeMillis" + gcName, "GC Time for " + gcName) };
            final MetricsInfo[] previousGcInfo = this.gcInfoCache.putIfAbsent(gcName, gcInfo);
            if (previousGcInfo != null) {
                return previousGcInfo;
            }
        }
        return gcInfo;
    }
    
    private void getThreadUsage(final MetricsRecordBuilder rb) {
        int threadsNew = 0;
        int threadsRunnable = 0;
        int threadsBlocked = 0;
        int threadsWaiting = 0;
        int threadsTimedWaiting = 0;
        int threadsTerminated = 0;
        final long[] threadIds = this.threadMXBean.getAllThreadIds();
        for (final ThreadInfo threadInfo : this.threadMXBean.getThreadInfo(threadIds, 0)) {
            if (threadInfo != null) {
                switch (threadInfo.getThreadState()) {
                    case NEW: {
                        ++threadsNew;
                        break;
                    }
                    case RUNNABLE: {
                        ++threadsRunnable;
                        break;
                    }
                    case BLOCKED: {
                        ++threadsBlocked;
                        break;
                    }
                    case WAITING: {
                        ++threadsWaiting;
                        break;
                    }
                    case TIMED_WAITING: {
                        ++threadsTimedWaiting;
                        break;
                    }
                    case TERMINATED: {
                        ++threadsTerminated;
                        break;
                    }
                }
            }
        }
        rb.addGauge(JvmMetricsInfo.ThreadsNew, threadsNew).addGauge(JvmMetricsInfo.ThreadsRunnable, threadsRunnable).addGauge(JvmMetricsInfo.ThreadsBlocked, threadsBlocked).addGauge(JvmMetricsInfo.ThreadsWaiting, threadsWaiting).addGauge(JvmMetricsInfo.ThreadsTimedWaiting, threadsTimedWaiting).addGauge(JvmMetricsInfo.ThreadsTerminated, threadsTerminated);
    }
    
    private void getEventCounters(final MetricsRecordBuilder rb) {
        rb.addCounter(JvmMetricsInfo.LogFatal, EventCounter.getFatal()).addCounter(JvmMetricsInfo.LogError, EventCounter.getError()).addCounter(JvmMetricsInfo.LogWarn, EventCounter.getWarn()).addCounter(JvmMetricsInfo.LogInfo, EventCounter.getInfo());
    }
    
    enum Singleton
    {
        INSTANCE;
        
        JvmMetrics impl;
        
        synchronized JvmMetrics init(final String processName, final String sessionId) {
            if (this.impl == null) {
                this.impl = JvmMetrics.create(processName, sessionId, DefaultMetricsSystem.instance());
            }
            return this.impl;
        }
        
        synchronized void shutdown() {
            DefaultMetricsSystem.instance().unregisterSource(JvmMetricsInfo.JvmMetrics.name());
            this.impl = null;
        }
    }
}
