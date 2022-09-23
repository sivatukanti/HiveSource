// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.sink.ganglia;

import java.io.IOException;
import org.apache.hadoop.metrics2.MetricsException;
import org.apache.hadoop.metrics2.MetricsVisitor;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.Collection;
import org.apache.hadoop.metrics2.impl.MsInfo;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.MetricsRecord;
import java.util.Iterator;
import java.util.HashSet;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.SubsetConfiguration;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.Map;
import org.apache.hadoop.metrics2.util.MetricsCache;
import org.slf4j.Logger;

public class GangliaSink30 extends AbstractGangliaSink
{
    public final Logger LOG;
    private static final String TAGS_FOR_PREFIX_PROPERTY_PREFIX = "tagsForPrefix.";
    private MetricsCache metricsCache;
    private Map<String, Set<String>> useTagsMap;
    
    public GangliaSink30() {
        this.LOG = LoggerFactory.getLogger(this.getClass());
        this.metricsCache = new MetricsCache();
        this.useTagsMap = new HashMap<String, Set<String>>();
    }
    
    @Override
    public void init(final SubsetConfiguration conf) {
        super.init(conf);
        conf.setListDelimiterHandler(new DefaultListDelimiterHandler(','));
        final Iterator<String> it = conf.getKeys();
        while (it.hasNext()) {
            final String propertyName = it.next();
            if (propertyName.startsWith("tagsForPrefix.")) {
                final String contextName = propertyName.substring("tagsForPrefix.".length());
                final String[] tags = conf.getStringArray(propertyName);
                boolean useAllTags = false;
                Set<String> set = new HashSet<String>();
                for (String tag : tags) {
                    tag = tag.trim();
                    useAllTags |= tag.equals("*");
                    if (tag.length() > 0) {
                        set.add(tag);
                    }
                }
                if (useAllTags) {
                    set = null;
                }
                this.useTagsMap.put(contextName, set);
            }
        }
    }
    
    @InterfaceAudience.Private
    public void appendPrefix(final MetricsRecord record, final StringBuilder sb) {
        final String contextName = record.context();
        final Collection<MetricsTag> tags = record.tags();
        if (this.useTagsMap.containsKey(contextName)) {
            final Set<String> useTags = this.useTagsMap.get(contextName);
            for (final MetricsTag t : tags) {
                if ((useTags == null || useTags.contains(t.name())) && t.info() != MsInfo.Context && t.info() != MsInfo.Hostname && t.value() != null) {
                    sb.append('.').append(t.name()).append('=').append(t.value());
                }
            }
        }
    }
    
    @Override
    public void putMetrics(final MetricsRecord record) {
        try {
            final String recordName = record.name();
            final String contextName = record.context();
            final StringBuilder sb = new StringBuilder();
            sb.append(contextName);
            sb.append('.');
            sb.append(recordName);
            this.appendPrefix(record, sb);
            final String groupName = sb.toString();
            sb.append('.');
            final int sbBaseLen = sb.length();
            String type = null;
            GangliaSlope slopeFromMetric = null;
            GangliaSlope calculatedSlope = null;
            MetricsCache.Record cachedMetrics = null;
            this.resetBuffer();
            if (!this.isSupportSparseMetrics()) {
                cachedMetrics = this.metricsCache.update(record);
                if (cachedMetrics != null && cachedMetrics.metricsEntrySet() != null) {
                    for (final Map.Entry<String, AbstractMetric> entry : cachedMetrics.metricsEntrySet()) {
                        final AbstractMetric metric = entry.getValue();
                        sb.append(metric.name());
                        final String name = sb.toString();
                        metric.visit(this.gangliaMetricVisitor);
                        type = this.gangliaMetricVisitor.getType();
                        slopeFromMetric = this.gangliaMetricVisitor.getSlope();
                        final GangliaConf gConf = this.getGangliaConfForMetric(name);
                        calculatedSlope = this.calculateSlope(gConf, slopeFromMetric);
                        this.emitMetric(groupName, name, type, metric.value().toString(), gConf, calculatedSlope);
                        sb.setLength(sbBaseLen);
                    }
                }
            }
            else {
                final Collection<AbstractMetric> metrics = (Collection<AbstractMetric>)(Collection)record.metrics();
                if (metrics.size() > 0) {
                    for (final AbstractMetric metric : record.metrics()) {
                        sb.append(metric.name());
                        final String name = sb.toString();
                        metric.visit(this.gangliaMetricVisitor);
                        type = this.gangliaMetricVisitor.getType();
                        slopeFromMetric = this.gangliaMetricVisitor.getSlope();
                        final GangliaConf gConf = this.getGangliaConfForMetric(name);
                        calculatedSlope = this.calculateSlope(gConf, slopeFromMetric);
                        this.emitMetric(groupName, name, type, metric.value().toString(), gConf, calculatedSlope);
                        sb.setLength(sbBaseLen);
                    }
                }
            }
        }
        catch (IOException io) {
            throw new MetricsException("Failed to putMetrics", io);
        }
    }
    
    private GangliaSlope calculateSlope(final GangliaConf gConf, final GangliaSlope slopeFromMetric) {
        if (gConf.getSlope() != null) {
            return gConf.getSlope();
        }
        if (slopeFromMetric != null) {
            return slopeFromMetric;
        }
        return GangliaSink30.DEFAULT_SLOPE;
    }
    
    protected void emitMetric(final String groupName, final String name, final String type, final String value, final GangliaConf gConf, final GangliaSlope gSlope) throws IOException {
        if (name == null) {
            this.LOG.warn("Metric was emitted with no name.");
            return;
        }
        if (value == null) {
            this.LOG.warn("Metric name " + name + " was emitted with a null value.");
            return;
        }
        if (type == null) {
            this.LOG.warn("Metric name " + name + ", value " + value + " has no type.");
            return;
        }
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("Emitting metric " + name + ", type " + type + ", value " + value + ", slope " + gSlope.name() + " from hostname " + this.getHostName());
        }
        this.xdr_int(0);
        this.xdr_string(type);
        this.xdr_string(name);
        this.xdr_string(value);
        this.xdr_string(gConf.getUnits());
        this.xdr_int(gSlope.ordinal());
        this.xdr_int(gConf.getTmax());
        this.xdr_int(gConf.getDmax());
        this.emitToGangliaHosts();
    }
}
