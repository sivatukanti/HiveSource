// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.metrics2.util.MBeans;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.metrics2.MetricsCollector;
import java.net.InetAddress;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.hadoop.metrics2.lib.Interns;
import org.apache.hadoop.util.Time;
import com.google.common.annotations.VisibleForTesting;
import java.util.TimerTask;
import java.io.Writer;
import org.apache.commons.configuration2.Configuration;
import java.io.StringWriter;
import org.apache.commons.configuration2.PropertiesConfiguration;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.lib.MetricsSourceBuilder;
import org.apache.hadoop.metrics2.lib.MetricsAnnotations;
import java.util.Iterator;
import org.apache.hadoop.metrics2.MetricsException;
import com.google.common.base.Preconditions;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javax.management.ObjectName;
import java.util.Timer;
import org.apache.hadoop.metrics2.MetricsFilter;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.lib.MutableCounterLong;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.lib.MutableStat;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import java.util.List;
import org.apache.hadoop.metrics2.MetricsSink;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.metrics2.MetricsSource;
import org.apache.hadoop.metrics2.MetricsSystem;

@InterfaceAudience.Private
@Metrics(context = "metricssystem")
public class MetricsSystemImpl extends MetricsSystem implements MetricsSource
{
    static final Logger LOG;
    static final String MS_NAME = "MetricsSystem";
    static final String MS_STATS_NAME = "MetricsSystem,sub=Stats";
    static final String MS_STATS_DESC = "Metrics system metrics";
    static final String MS_CONTROL_NAME = "MetricsSystem,sub=Control";
    static final String MS_INIT_MODE_KEY = "hadoop.metrics.init.mode";
    private final Map<String, MetricsSourceAdapter> sources;
    private final Map<String, MetricsSource> allSources;
    private final Map<String, MetricsSinkAdapter> sinks;
    private final Map<String, MetricsSink> allSinks;
    private final List<Callback> callbacks;
    private final Map<String, Callback> namedCallbacks;
    private final MetricsCollectorImpl collector;
    private final MetricsRegistry registry;
    @Metric({ "Snapshot", "Snapshot stats" })
    MutableStat snapshotStat;
    @Metric({ "Publish", "Publishing stats" })
    MutableStat publishStat;
    @Metric({ "Dropped updates by all sinks" })
    MutableCounterLong droppedPubAll;
    private final List<MetricsTag> injectedTags;
    private String prefix;
    private MetricsFilter sourceFilter;
    private MetricsConfig config;
    private Map<String, MetricsConfig> sourceConfigs;
    private Map<String, MetricsConfig> sinkConfigs;
    private boolean monitoring;
    private Timer timer;
    private long period;
    private long logicalTime;
    private ObjectName mbeanName;
    private boolean publishSelfMetrics;
    private MetricsSourceAdapter sysSource;
    private int refCount;
    
    public MetricsSystemImpl(final String prefix) {
        this.registry = new MetricsRegistry("MetricsSystem");
        this.monitoring = false;
        this.publishSelfMetrics = true;
        this.refCount = 0;
        this.prefix = prefix;
        this.allSources = (Map<String, MetricsSource>)Maps.newHashMap();
        this.sources = (Map<String, MetricsSourceAdapter>)Maps.newLinkedHashMap();
        this.allSinks = (Map<String, MetricsSink>)Maps.newHashMap();
        this.sinks = (Map<String, MetricsSinkAdapter>)Maps.newLinkedHashMap();
        this.sourceConfigs = (Map<String, MetricsConfig>)Maps.newHashMap();
        this.sinkConfigs = (Map<String, MetricsConfig>)Maps.newHashMap();
        this.callbacks = (List<Callback>)Lists.newArrayList();
        this.namedCallbacks = (Map<String, Callback>)Maps.newHashMap();
        this.injectedTags = (List<MetricsTag>)Lists.newArrayList();
        this.collector = new MetricsCollectorImpl();
        if (prefix != null) {
            this.initSystemMBean();
        }
    }
    
    public MetricsSystemImpl() {
        this(null);
    }
    
    @Override
    public synchronized MetricsSystem init(final String prefix) {
        if (this.monitoring && !DefaultMetricsSystem.inMiniClusterMode()) {
            MetricsSystemImpl.LOG.warn(this.prefix + " metrics system already initialized!");
            return this;
        }
        this.prefix = Preconditions.checkNotNull(prefix, (Object)"prefix");
        ++this.refCount;
        if (this.monitoring) {
            MetricsSystemImpl.LOG.info(this.prefix + " metrics system started (again)");
            return this;
        }
        switch (this.initMode()) {
            case NORMAL: {
                try {
                    this.start();
                }
                catch (MetricsConfigException e) {
                    MetricsSystemImpl.LOG.warn("Metrics system not started: " + e.getMessage());
                    MetricsSystemImpl.LOG.debug("Stacktrace: ", e);
                }
                break;
            }
            case STANDBY: {
                MetricsSystemImpl.LOG.info(prefix + " metrics system started in standby mode");
                break;
            }
        }
        this.initSystemMBean();
        return this;
    }
    
    @Override
    public synchronized void start() {
        Preconditions.checkNotNull(this.prefix, (Object)"prefix");
        if (this.monitoring) {
            MetricsSystemImpl.LOG.warn(this.prefix + " metrics system already started!", new MetricsException("Illegal start"));
            return;
        }
        for (final Callback cb : this.callbacks) {
            cb.preStart();
        }
        for (final Callback cb : this.namedCallbacks.values()) {
            cb.preStart();
        }
        this.configure(this.prefix);
        this.startTimer();
        this.monitoring = true;
        MetricsSystemImpl.LOG.info(this.prefix + " metrics system started");
        for (final Callback cb : this.callbacks) {
            cb.postStart();
        }
        for (final Callback cb : this.namedCallbacks.values()) {
            cb.postStart();
        }
    }
    
    @Override
    public synchronized void stop() {
        if (!this.monitoring && !DefaultMetricsSystem.inMiniClusterMode()) {
            MetricsSystemImpl.LOG.warn(this.prefix + " metrics system not yet started!", new MetricsException("Illegal stop"));
            return;
        }
        if (!this.monitoring) {
            MetricsSystemImpl.LOG.info(this.prefix + " metrics system stopped (again)");
            return;
        }
        for (final Callback cb : this.callbacks) {
            cb.preStop();
        }
        for (final Callback cb : this.namedCallbacks.values()) {
            cb.preStop();
        }
        MetricsSystemImpl.LOG.info("Stopping " + this.prefix + " metrics system...");
        this.stopTimer();
        this.stopSources();
        this.stopSinks();
        this.clearConfigs();
        this.monitoring = false;
        MetricsSystemImpl.LOG.info(this.prefix + " metrics system stopped.");
        for (final Callback cb : this.callbacks) {
            cb.postStop();
        }
        for (final Callback cb : this.namedCallbacks.values()) {
            cb.postStop();
        }
    }
    
    @Override
    public synchronized <T> T register(final String name, final String desc, final T source) {
        final MetricsSourceBuilder sb = MetricsAnnotations.newSourceBuilder(source);
        final MetricsSource s = sb.build();
        final MetricsInfo si = sb.info();
        final String name2 = (name == null) ? si.name() : name;
        final String finalDesc = (desc == null) ? si.description() : desc;
        final String finalName = DefaultMetricsSystem.sourceName(name2, !this.monitoring);
        this.allSources.put(finalName, s);
        MetricsSystemImpl.LOG.debug(finalName + ", " + finalDesc);
        if (this.monitoring) {
            this.registerSource(finalName, finalDesc, s);
        }
        this.register(finalName, new AbstractCallback() {
            @Override
            public void postStart() {
                MetricsSystemImpl.this.registerSource(finalName, finalDesc, s);
            }
        });
        return source;
    }
    
    @Override
    public synchronized void unregisterSource(final String name) {
        if (this.sources.containsKey(name)) {
            this.sources.get(name).stop();
            this.sources.remove(name);
        }
        if (this.allSources.containsKey(name)) {
            this.allSources.remove(name);
        }
        if (this.namedCallbacks.containsKey(name)) {
            this.namedCallbacks.remove(name);
        }
        DefaultMetricsSystem.removeSourceName(name);
    }
    
    synchronized void registerSource(final String name, final String desc, final MetricsSource source) {
        Preconditions.checkNotNull(this.config, (Object)"config");
        final MetricsConfig conf = this.sourceConfigs.get(name);
        final MetricsSourceAdapter sa = new MetricsSourceAdapter(this.prefix, name, desc, source, this.injectedTags, this.period, (conf != null) ? conf : this.config.subset("source"));
        this.sources.put(name, sa);
        sa.start();
        MetricsSystemImpl.LOG.debug("Registered source " + name);
    }
    
    @Override
    public synchronized <T extends MetricsSink> T register(final String name, final String description, final T sink) {
        MetricsSystemImpl.LOG.debug(name + ", " + description);
        if (this.allSinks.containsKey(name)) {
            MetricsSystemImpl.LOG.warn("Sink " + name + " already exists!");
            return sink;
        }
        this.allSinks.put(name, sink);
        if (this.config != null) {
            this.registerSink(name, description, sink);
        }
        this.register(name, new AbstractCallback() {
            @Override
            public void postStart() {
                MetricsSystemImpl.this.register(name, description, sink);
            }
        });
        return sink;
    }
    
    synchronized void registerSink(final String name, final String desc, final MetricsSink sink) {
        Preconditions.checkNotNull(this.config, (Object)"config");
        final MetricsConfig conf = this.sinkConfigs.get(name);
        final MetricsSinkAdapter sa = (conf != null) ? newSink(name, desc, sink, conf) : newSink(name, desc, sink, this.config.subset("sink"));
        this.sinks.put(name, sa);
        sa.start();
        MetricsSystemImpl.LOG.info("Registered sink " + name);
    }
    
    @Override
    public synchronized void register(final Callback callback) {
        this.callbacks.add((Callback)this.getProxyForCallback(callback));
    }
    
    private synchronized void register(final String name, final Callback callback) {
        this.namedCallbacks.put(name, (Callback)this.getProxyForCallback(callback));
    }
    
    private Object getProxyForCallback(final Callback callback) {
        return Proxy.newProxyInstance(callback.getClass().getClassLoader(), new Class[] { Callback.class }, new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                try {
                    return method.invoke(callback, args);
                }
                catch (Exception e) {
                    MetricsSystemImpl.LOG.warn("Caught exception in callback " + method.getName(), e);
                    return null;
                }
            }
        });
    }
    
    @Override
    public synchronized void startMetricsMBeans() {
        for (final MetricsSourceAdapter sa : this.sources.values()) {
            sa.startMBeans();
        }
    }
    
    @Override
    public synchronized void stopMetricsMBeans() {
        for (final MetricsSourceAdapter sa : this.sources.values()) {
            sa.stopMBeans();
        }
    }
    
    @Override
    public synchronized String currentConfig() {
        final PropertiesConfiguration saver = new PropertiesConfiguration();
        final StringWriter writer = new StringWriter();
        saver.copy(this.config);
        try {
            saver.write(writer);
        }
        catch (Exception e) {
            throw new MetricsConfigException("Error stringify config", e);
        }
        return writer.toString();
    }
    
    private synchronized void startTimer() {
        if (this.timer != null) {
            MetricsSystemImpl.LOG.warn(this.prefix + " metrics system timer already started!");
            return;
        }
        this.logicalTime = 0L;
        final long millis = this.period;
        (this.timer = new Timer("Timer for '" + this.prefix + "' metrics system", true)).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    MetricsSystemImpl.this.onTimerEvent();
                }
                catch (Exception e) {
                    MetricsSystemImpl.LOG.warn("Error invoking metrics timer", e);
                }
            }
        }, millis, millis);
        MetricsSystemImpl.LOG.info("Scheduled Metric snapshot period at " + this.period / 1000L + " second(s).");
    }
    
    synchronized void onTimerEvent() {
        this.logicalTime += this.period;
        if (this.sinks.size() > 0) {
            this.publishMetrics(this.sampleMetrics(), false);
        }
    }
    
    @Override
    public synchronized void publishMetricsNow() {
        if (this.sinks.size() > 0) {
            this.publishMetrics(this.sampleMetrics(), true);
        }
    }
    
    @VisibleForTesting
    public synchronized MetricsBuffer sampleMetrics() {
        this.collector.clear();
        final MetricsBufferBuilder bufferBuilder = new MetricsBufferBuilder();
        for (final Map.Entry<String, MetricsSourceAdapter> entry : this.sources.entrySet()) {
            if (this.sourceFilter == null || this.sourceFilter.accepts(entry.getKey())) {
                this.snapshotMetrics(entry.getValue(), bufferBuilder);
            }
        }
        if (this.publishSelfMetrics) {
            this.snapshotMetrics(this.sysSource, bufferBuilder);
        }
        final MetricsBuffer buffer = bufferBuilder.get();
        return buffer;
    }
    
    private void snapshotMetrics(final MetricsSourceAdapter sa, final MetricsBufferBuilder bufferBuilder) {
        final long startTime = Time.monotonicNow();
        bufferBuilder.add(sa.name(), sa.getMetrics(this.collector, true));
        this.collector.clear();
        this.snapshotStat.add(Time.monotonicNow() - startTime);
        MetricsSystemImpl.LOG.debug("Snapshotted source " + sa.name());
    }
    
    synchronized void publishMetrics(final MetricsBuffer buffer, final boolean immediate) {
        int dropped = 0;
        for (final MetricsSinkAdapter sa : this.sinks.values()) {
            final long startTime = Time.monotonicNow();
            boolean result;
            if (immediate) {
                result = sa.putMetricsImmediate(buffer);
            }
            else {
                result = sa.putMetrics(buffer, this.logicalTime);
            }
            dropped += (result ? 0 : 1);
            this.publishStat.add(Time.monotonicNow() - startTime);
        }
        this.droppedPubAll.incr(dropped);
    }
    
    private synchronized void stopTimer() {
        if (this.timer == null) {
            MetricsSystemImpl.LOG.warn(this.prefix + " metrics system timer already stopped!");
            return;
        }
        this.timer.cancel();
        this.timer = null;
    }
    
    private synchronized void stopSources() {
        for (final Map.Entry<String, MetricsSourceAdapter> entry : this.sources.entrySet()) {
            final MetricsSourceAdapter sa = entry.getValue();
            MetricsSystemImpl.LOG.debug("Stopping metrics source " + entry.getKey() + ": class=" + sa.source().getClass());
            sa.stop();
        }
        this.sysSource.stop();
        this.sources.clear();
    }
    
    private synchronized void stopSinks() {
        for (final Map.Entry<String, MetricsSinkAdapter> entry : this.sinks.entrySet()) {
            final MetricsSinkAdapter sa = entry.getValue();
            MetricsSystemImpl.LOG.debug("Stopping metrics sink " + entry.getKey() + ": class=" + sa.sink().getClass());
            sa.stop();
        }
        this.sinks.clear();
    }
    
    private synchronized void configure(final String prefix) {
        this.config = MetricsConfig.create(prefix);
        this.configureSinks();
        this.configureSources();
        this.configureSystem();
    }
    
    private synchronized void configureSystem() {
        this.injectedTags.add(Interns.tag(MsInfo.Hostname, getHostname()));
    }
    
    private synchronized void configureSinks() {
        this.sinkConfigs = this.config.getInstanceConfigs("sink");
        long confPeriodMillis = 0L;
        for (final Map.Entry<String, MetricsConfig> entry : this.sinkConfigs.entrySet()) {
            final MetricsConfig conf = entry.getValue();
            final int sinkPeriod = conf.getInt("period", 10);
            final long sinkPeriodMillis = conf.getLong("periodMillis", sinkPeriod * 1000);
            confPeriodMillis = ((confPeriodMillis == 0L) ? sinkPeriodMillis : ArithmeticUtils.gcd(confPeriodMillis, sinkPeriodMillis));
            final String clsName = conf.getClassName("");
            if (clsName == null) {
                continue;
            }
            final String sinkName = entry.getKey();
            try {
                final MetricsSinkAdapter sa = newSink(sinkName, conf.getString("description", sinkName), conf);
                sa.start();
                this.sinks.put(sinkName, sa);
            }
            catch (Exception e) {
                MetricsSystemImpl.LOG.warn("Error creating sink '" + sinkName + "'", e);
            }
        }
        final long periodSec = this.config.getInt("period", 10);
        this.period = ((confPeriodMillis > 0L) ? confPeriodMillis : this.config.getLong("periodMillis", periodSec * 1000L));
    }
    
    static MetricsSinkAdapter newSink(final String name, final String desc, final MetricsSink sink, final MetricsConfig conf) {
        return new MetricsSinkAdapter(name, desc, sink, conf.getString("context"), conf.getFilter("source.filter"), conf.getFilter("record.filter"), conf.getFilter("metric.filter"), conf.getInt("period", 10) * 1000, conf.getInt("queue.capacity", 1), conf.getInt("retry.delay", 10), conf.getFloat("retry.backoff", 2.0f), conf.getInt("retry.count", 1));
    }
    
    static MetricsSinkAdapter newSink(final String name, final String desc, final MetricsConfig conf) {
        return newSink(name, desc, conf.getPlugin(""), conf);
    }
    
    private void configureSources() {
        this.sourceFilter = this.config.getFilter("*.source.filter");
        this.sourceConfigs = this.config.getInstanceConfigs("source");
        this.registerSystemSource();
    }
    
    private void clearConfigs() {
        this.sinkConfigs.clear();
        this.sourceConfigs.clear();
        this.injectedTags.clear();
        this.config = null;
    }
    
    static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (Exception e) {
            MetricsSystemImpl.LOG.error("Error getting localhost name. Using 'localhost'...", e);
            return "localhost";
        }
    }
    
    private void registerSystemSource() {
        final MetricsConfig sysConf = this.sourceConfigs.get("MetricsSystem");
        (this.sysSource = new MetricsSourceAdapter(this.prefix, "MetricsSystem,sub=Stats", "Metrics system metrics", MetricsAnnotations.makeSource(this), this.injectedTags, this.period, (sysConf == null) ? this.config.subset("source") : sysConf)).start();
    }
    
    @Override
    public synchronized void getMetrics(final MetricsCollector builder, final boolean all) {
        final MetricsRecordBuilder rb = builder.addRecord("MetricsSystem").addGauge(MsInfo.NumActiveSources, this.sources.size()).addGauge(MsInfo.NumAllSources, this.allSources.size()).addGauge(MsInfo.NumActiveSinks, this.sinks.size()).addGauge(MsInfo.NumAllSinks, this.allSinks.size());
        for (final MetricsSinkAdapter sa : this.sinks.values()) {
            sa.snapshot(rb, all);
        }
        this.registry.snapshot(rb, all);
    }
    
    private void initSystemMBean() {
        Preconditions.checkNotNull(this.prefix, (Object)"prefix should not be null here!");
        if (this.mbeanName == null) {
            this.mbeanName = MBeans.register(this.prefix, "MetricsSystem,sub=Control", this);
        }
    }
    
    @Override
    public synchronized boolean shutdown() {
        MetricsSystemImpl.LOG.debug("refCount=" + this.refCount);
        if (this.refCount <= 0) {
            MetricsSystemImpl.LOG.debug("Redundant shutdown", new Throwable());
            return true;
        }
        if (--this.refCount > 0) {
            return false;
        }
        if (this.monitoring) {
            try {
                this.stop();
            }
            catch (Exception e) {
                MetricsSystemImpl.LOG.warn("Error stopping the metrics system", e);
            }
        }
        this.allSources.clear();
        this.allSinks.clear();
        this.callbacks.clear();
        this.namedCallbacks.clear();
        if (this.mbeanName != null) {
            MBeans.unregister(this.mbeanName);
            this.mbeanName = null;
        }
        MetricsSystemImpl.LOG.info(this.prefix + " metrics system shutdown complete.");
        return true;
    }
    
    @Override
    public MetricsSource getSource(final String name) {
        return this.allSources.get(name);
    }
    
    @VisibleForTesting
    MetricsSourceAdapter getSourceAdapter(final String name) {
        return this.sources.get(name);
    }
    
    @VisibleForTesting
    public MetricsSinkAdapter getSinkAdapter(final String name) {
        return this.sinks.get(name);
    }
    
    private InitMode initMode() {
        MetricsSystemImpl.LOG.debug("from system property: " + System.getProperty("hadoop.metrics.init.mode"));
        MetricsSystemImpl.LOG.debug("from environment variable: " + System.getenv("hadoop.metrics.init.mode"));
        final String m = System.getProperty("hadoop.metrics.init.mode");
        final String m2 = (m == null) ? System.getenv("hadoop.metrics.init.mode") : m;
        return InitMode.valueOf(StringUtils.toUpperCase((m2 == null) ? InitMode.NORMAL.name() : m2));
    }
    
    static {
        LOG = LoggerFactory.getLogger(MetricsSystemImpl.class);
    }
    
    enum InitMode
    {
        NORMAL, 
        STANDBY;
    }
}
