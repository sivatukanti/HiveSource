// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.metrics2.AbstractMetric;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.metrics2.util.MBeans;
import java.util.Iterator;
import org.apache.hadoop.metrics2.MetricsCollector;
import org.apache.hadoop.util.Time;
import javax.management.AttributeList;
import javax.management.InvalidAttributeValueException;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import org.apache.hadoop.metrics2.util.Contracts;
import com.google.common.collect.Maps;
import com.google.common.base.Preconditions;
import javax.management.ObjectName;
import javax.management.MBeanInfo;
import org.apache.hadoop.metrics2.MetricsTag;
import javax.management.Attribute;
import java.util.HashMap;
import org.apache.hadoop.metrics2.MetricsFilter;
import org.apache.hadoop.metrics2.MetricsSource;
import org.slf4j.Logger;
import javax.management.DynamicMBean;

class MetricsSourceAdapter implements DynamicMBean
{
    private static final Logger LOG;
    private final String prefix;
    private final String name;
    private final MetricsSource source;
    private final MetricsFilter recordFilter;
    private final MetricsFilter metricFilter;
    private final HashMap<String, Attribute> attrCache;
    private final MBeanInfoBuilder infoBuilder;
    private final Iterable<MetricsTag> injectedTags;
    private boolean lastRecsCleared;
    private long jmxCacheTS;
    private long jmxCacheTTL;
    private MBeanInfo infoCache;
    private ObjectName mbeanName;
    private final boolean startMBeans;
    
    MetricsSourceAdapter(final String prefix, final String name, final String description, final MetricsSource source, final Iterable<MetricsTag> injectedTags, final MetricsFilter recordFilter, final MetricsFilter metricFilter, final long jmxCacheTTL, final boolean startMBeans) {
        this.jmxCacheTS = 0L;
        this.prefix = Preconditions.checkNotNull(prefix, (Object)"prefix");
        this.name = Preconditions.checkNotNull(name, (Object)"name");
        this.source = Preconditions.checkNotNull(source, (Object)"source");
        this.attrCache = Maps.newHashMap();
        this.infoBuilder = new MBeanInfoBuilder(name, description);
        this.injectedTags = injectedTags;
        this.recordFilter = recordFilter;
        this.metricFilter = metricFilter;
        this.jmxCacheTTL = Contracts.checkArg(jmxCacheTTL, jmxCacheTTL > 0L, "jmxCacheTTL");
        this.startMBeans = startMBeans;
        this.lastRecsCleared = true;
    }
    
    MetricsSourceAdapter(final String prefix, final String name, final String description, final MetricsSource source, final Iterable<MetricsTag> injectedTags, final long period, final MetricsConfig conf) {
        this(prefix, name, description, source, injectedTags, conf.getFilter("record.filter"), conf.getFilter("metric.filter"), period + 1L, conf.getBoolean("source.start_mbeans", true));
    }
    
    void start() {
        if (this.startMBeans) {
            this.startMBeans();
        }
    }
    
    @Override
    public Object getAttribute(final String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        this.updateJmxCache();
        synchronized (this) {
            final Attribute a = this.attrCache.get(attribute);
            if (a == null) {
                throw new AttributeNotFoundException(attribute + " not found");
            }
            if (MetricsSourceAdapter.LOG.isDebugEnabled()) {
                MetricsSourceAdapter.LOG.debug(attribute + ": " + a);
            }
            return a.getValue();
        }
    }
    
    @Override
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        throw new UnsupportedOperationException("Metrics are read-only.");
    }
    
    @Override
    public AttributeList getAttributes(final String[] attributes) {
        this.updateJmxCache();
        synchronized (this) {
            final AttributeList ret = new AttributeList();
            for (final String key : attributes) {
                final Attribute attr = this.attrCache.get(key);
                if (MetricsSourceAdapter.LOG.isDebugEnabled()) {
                    MetricsSourceAdapter.LOG.debug(key + ": " + attr);
                }
                ret.add(attr);
            }
            return ret;
        }
    }
    
    @Override
    public AttributeList setAttributes(final AttributeList attributes) {
        throw new UnsupportedOperationException("Metrics are read-only.");
    }
    
    @Override
    public Object invoke(final String actionName, final Object[] params, final String[] signature) throws MBeanException, ReflectionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        this.updateJmxCache();
        return this.infoCache;
    }
    
    private void updateJmxCache() {
        boolean getAllMetrics = false;
        synchronized (this) {
            if (Time.now() - this.jmxCacheTS < this.jmxCacheTTL) {
                return;
            }
            this.jmxCacheTS = Time.now() + this.jmxCacheTTL;
            if (this.lastRecsCleared) {
                getAllMetrics = true;
                this.lastRecsCleared = false;
            }
        }
        Iterable<MetricsRecordImpl> lastRecs = null;
        if (getAllMetrics) {
            lastRecs = this.getMetrics(new MetricsCollectorImpl(), true);
        }
        synchronized (this) {
            if (lastRecs != null) {
                this.updateAttrCache(lastRecs);
                this.updateInfoCache(lastRecs);
            }
            this.jmxCacheTS = Time.now();
            this.lastRecsCleared = true;
        }
    }
    
    Iterable<MetricsRecordImpl> getMetrics(final MetricsCollectorImpl builder, final boolean all) {
        builder.setRecordFilter(this.recordFilter).setMetricFilter(this.metricFilter);
        try {
            this.source.getMetrics(builder, all);
        }
        catch (Exception e) {
            MetricsSourceAdapter.LOG.error("Error getting metrics from source " + this.name, e);
        }
        for (final MetricsRecordBuilderImpl rb : builder) {
            for (final MetricsTag t : this.injectedTags) {
                rb.add(t);
            }
        }
        return builder.getRecords();
    }
    
    synchronized void stop() {
        this.stopMBeans();
    }
    
    synchronized void startMBeans() {
        if (this.mbeanName != null) {
            MetricsSourceAdapter.LOG.warn("MBean " + this.name + " already initialized!");
            MetricsSourceAdapter.LOG.debug("Stacktrace: ", new Throwable());
            return;
        }
        this.mbeanName = MBeans.register(this.prefix, this.name, this);
        MetricsSourceAdapter.LOG.debug("MBean for source " + this.name + " registered.");
    }
    
    synchronized void stopMBeans() {
        if (this.mbeanName != null) {
            MBeans.unregister(this.mbeanName);
            this.mbeanName = null;
        }
    }
    
    @VisibleForTesting
    ObjectName getMBeanName() {
        return this.mbeanName;
    }
    
    @VisibleForTesting
    long getJmxCacheTTL() {
        return this.jmxCacheTTL;
    }
    
    private void updateInfoCache(final Iterable<MetricsRecordImpl> lastRecs) {
        Preconditions.checkNotNull(lastRecs, (Object)"LastRecs should not be null");
        MetricsSourceAdapter.LOG.debug("Updating info cache...");
        this.infoCache = this.infoBuilder.reset(lastRecs).get();
        MetricsSourceAdapter.LOG.debug("Done");
    }
    
    private int updateAttrCache(final Iterable<MetricsRecordImpl> lastRecs) {
        Preconditions.checkNotNull(lastRecs, (Object)"LastRecs should not be null");
        MetricsSourceAdapter.LOG.debug("Updating attr cache...");
        int recNo = 0;
        int numMetrics = 0;
        for (final MetricsRecordImpl record : lastRecs) {
            for (final MetricsTag t : record.tags()) {
                this.setAttrCacheTag(t, recNo);
                ++numMetrics;
            }
            for (final AbstractMetric m : record.metrics()) {
                this.setAttrCacheMetric(m, recNo);
                ++numMetrics;
            }
            ++recNo;
        }
        MetricsSourceAdapter.LOG.debug("Done. # tags & metrics=" + numMetrics);
        return numMetrics;
    }
    
    private static String tagName(final String name, final int recNo) {
        final StringBuilder sb = new StringBuilder(name.length() + 16);
        sb.append("tag.").append(name);
        if (recNo > 0) {
            sb.append('.').append(recNo);
        }
        return sb.toString();
    }
    
    private void setAttrCacheTag(final MetricsTag tag, final int recNo) {
        final String key = tagName(tag.name(), recNo);
        this.attrCache.put(key, new Attribute(key, tag.value()));
    }
    
    private static String metricName(final String name, final int recNo) {
        if (recNo == 0) {
            return name;
        }
        final StringBuilder sb = new StringBuilder(name.length() + 12);
        sb.append(name);
        if (recNo > 0) {
            sb.append('.').append(recNo);
        }
        return sb.toString();
    }
    
    private void setAttrCacheMetric(final AbstractMetric metric, final int recNo) {
        final String key = metricName(metric.name(), recNo);
        this.attrCache.put(key, new Attribute(key, metric.value()));
    }
    
    String name() {
        return this.name;
    }
    
    MetricsSource source() {
        return this.source;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MetricsSourceAdapter.class);
    }
}
