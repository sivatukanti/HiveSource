// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.Set;
import com.google.common.collect.Sets;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Private
public class JvmPauseMonitor extends AbstractService
{
    private static final Logger LOG;
    private static final long SLEEP_INTERVAL_MS = 500L;
    private long warnThresholdMs;
    private static final String WARN_THRESHOLD_KEY = "jvm.pause.warn-threshold.ms";
    private static final long WARN_THRESHOLD_DEFAULT = 10000L;
    private long infoThresholdMs;
    private static final String INFO_THRESHOLD_KEY = "jvm.pause.info-threshold.ms";
    private static final long INFO_THRESHOLD_DEFAULT = 1000L;
    private long numGcWarnThresholdExceeded;
    private long numGcInfoThresholdExceeded;
    private long totalGcExtraSleepTime;
    private Thread monitorThread;
    private volatile boolean shouldRun;
    
    public JvmPauseMonitor() {
        super(JvmPauseMonitor.class.getName());
        this.numGcWarnThresholdExceeded = 0L;
        this.numGcInfoThresholdExceeded = 0L;
        this.totalGcExtraSleepTime = 0L;
        this.shouldRun = true;
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.warnThresholdMs = conf.getLong("jvm.pause.warn-threshold.ms", 10000L);
        this.infoThresholdMs = conf.getLong("jvm.pause.info-threshold.ms", 1000L);
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        (this.monitorThread = new Daemon(new Monitor())).start();
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        this.shouldRun = false;
        if (this.monitorThread != null) {
            this.monitorThread.interrupt();
            try {
                this.monitorThread.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        super.serviceStop();
    }
    
    public boolean isStarted() {
        return this.monitorThread != null;
    }
    
    public long getNumGcWarnThresholdExceeded() {
        return this.numGcWarnThresholdExceeded;
    }
    
    public long getNumGcInfoThresholdExceeded() {
        return this.numGcInfoThresholdExceeded;
    }
    
    public long getTotalGcExtraSleepTime() {
        return this.totalGcExtraSleepTime;
    }
    
    private String formatMessage(final long extraSleepTime, final Map<String, GcTimes> gcTimesAfterSleep, final Map<String, GcTimes> gcTimesBeforeSleep) {
        final Set<String> gcBeanNames = Sets.intersection(gcTimesAfterSleep.keySet(), gcTimesBeforeSleep.keySet());
        final List<String> gcDiffs = (List<String>)Lists.newArrayList();
        for (final String name : gcBeanNames) {
            final GcTimes diff = gcTimesAfterSleep.get(name).subtract(gcTimesBeforeSleep.get(name));
            if (diff.gcCount != 0L) {
                gcDiffs.add("GC pool '" + name + "' had collection(s): " + diff.toString());
            }
        }
        String ret = "Detected pause in JVM or host machine (eg GC): pause of approximately " + extraSleepTime + "ms\n";
        if (gcDiffs.isEmpty()) {
            ret += "No GCs detected";
        }
        else {
            ret += Joiner.on("\n").join(gcDiffs);
        }
        return ret;
    }
    
    private Map<String, GcTimes> getGcTimes() {
        final Map<String, GcTimes> map = (Map<String, GcTimes>)Maps.newHashMap();
        final List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (final GarbageCollectorMXBean gcBean : gcBeans) {
            map.put(gcBean.getName(), new GcTimes(gcBean));
        }
        return map;
    }
    
    public static void main(final String[] args) throws Exception {
        final JvmPauseMonitor monitor = new JvmPauseMonitor();
        monitor.init(new Configuration());
        monitor.start();
        final List<String> list = (List<String>)Lists.newArrayList();
        int i = 0;
        while (true) {
            list.add(String.valueOf(i++));
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(JvmPauseMonitor.class);
    }
    
    private static class GcTimes
    {
        private long gcCount;
        private long gcTimeMillis;
        
        private GcTimes(final GarbageCollectorMXBean gcBean) {
            this.gcCount = gcBean.getCollectionCount();
            this.gcTimeMillis = gcBean.getCollectionTime();
        }
        
        private GcTimes(final long count, final long time) {
            this.gcCount = count;
            this.gcTimeMillis = time;
        }
        
        private GcTimes subtract(final GcTimes other) {
            return new GcTimes(this.gcCount - other.gcCount, this.gcTimeMillis - other.gcTimeMillis);
        }
        
        @Override
        public String toString() {
            return "count=" + this.gcCount + " time=" + this.gcTimeMillis + "ms";
        }
    }
    
    private class Monitor implements Runnable
    {
        @Override
        public void run() {
            final StopWatch sw = new StopWatch();
            Map<String, GcTimes> gcTimesBeforeSleep = JvmPauseMonitor.this.getGcTimes();
            JvmPauseMonitor.LOG.info("Starting JVM pause monitor");
            while (JvmPauseMonitor.this.shouldRun) {
                sw.reset().start();
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException ie) {
                    return;
                }
                final long extraSleepTime = sw.now(TimeUnit.MILLISECONDS) - 500L;
                final Map<String, GcTimes> gcTimesAfterSleep = JvmPauseMonitor.this.getGcTimes();
                if (extraSleepTime > JvmPauseMonitor.this.warnThresholdMs) {
                    ++JvmPauseMonitor.this.numGcWarnThresholdExceeded;
                    JvmPauseMonitor.LOG.warn(JvmPauseMonitor.this.formatMessage(extraSleepTime, gcTimesAfterSleep, gcTimesBeforeSleep));
                }
                else if (extraSleepTime > JvmPauseMonitor.this.infoThresholdMs) {
                    ++JvmPauseMonitor.this.numGcInfoThresholdExceeded;
                    JvmPauseMonitor.LOG.info(JvmPauseMonitor.this.formatMessage(extraSleepTime, gcTimesAfterSleep, gcTimesBeforeSleep));
                }
                JvmPauseMonitor.this.totalGcExtraSleepTime += extraSleepTime;
                gcTimesBeforeSleep = gcTimesAfterSleep;
            }
        }
    }
}
