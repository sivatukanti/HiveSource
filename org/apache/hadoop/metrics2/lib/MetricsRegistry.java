// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.lib;

import java.util.StringJoiner;
import java.util.Iterator;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import java.util.Collection;
import org.apache.hadoop.metrics2.impl.MsInfo;
import org.apache.hadoop.metrics2.MetricsException;
import com.google.common.collect.Maps;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.apache.hadoop.metrics2.MetricsTag;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MetricsRegistry
{
    private final Map<String, MutableMetric> metricsMap;
    private final Map<String, MetricsTag> tagsMap;
    private final MetricsInfo metricsInfo;
    
    public MetricsRegistry(final String name) {
        this.metricsMap = (Map<String, MutableMetric>)Maps.newLinkedHashMap();
        this.tagsMap = (Map<String, MetricsTag>)Maps.newLinkedHashMap();
        this.metricsInfo = Interns.info(name, name);
    }
    
    public MetricsRegistry(final MetricsInfo info) {
        this.metricsMap = (Map<String, MutableMetric>)Maps.newLinkedHashMap();
        this.tagsMap = (Map<String, MetricsTag>)Maps.newLinkedHashMap();
        this.metricsInfo = info;
    }
    
    public MetricsInfo info() {
        return this.metricsInfo;
    }
    
    public synchronized MutableMetric get(final String name) {
        return this.metricsMap.get(name);
    }
    
    public synchronized MetricsTag getTag(final String name) {
        return this.tagsMap.get(name);
    }
    
    public MutableCounterInt newCounter(final String name, final String desc, final int iVal) {
        return this.newCounter(Interns.info(name, desc), iVal);
    }
    
    public synchronized MutableCounterInt newCounter(final MetricsInfo info, final int iVal) {
        this.checkMetricName(info.name());
        final MutableCounterInt ret = new MutableCounterInt(info, iVal);
        this.metricsMap.put(info.name(), ret);
        return ret;
    }
    
    public MutableCounterLong newCounter(final String name, final String desc, final long iVal) {
        return this.newCounter(Interns.info(name, desc), iVal);
    }
    
    public synchronized MutableCounterLong newCounter(final MetricsInfo info, final long iVal) {
        this.checkMetricName(info.name());
        final MutableCounterLong ret = new MutableCounterLong(info, iVal);
        this.metricsMap.put(info.name(), ret);
        return ret;
    }
    
    public MutableGaugeInt newGauge(final String name, final String desc, final int iVal) {
        return this.newGauge(Interns.info(name, desc), iVal);
    }
    
    public synchronized MutableGaugeInt newGauge(final MetricsInfo info, final int iVal) {
        this.checkMetricName(info.name());
        final MutableGaugeInt ret = new MutableGaugeInt(info, iVal);
        this.metricsMap.put(info.name(), ret);
        return ret;
    }
    
    public MutableGaugeLong newGauge(final String name, final String desc, final long iVal) {
        return this.newGauge(Interns.info(name, desc), iVal);
    }
    
    public synchronized MutableGaugeLong newGauge(final MetricsInfo info, final long iVal) {
        this.checkMetricName(info.name());
        final MutableGaugeLong ret = new MutableGaugeLong(info, iVal);
        this.metricsMap.put(info.name(), ret);
        return ret;
    }
    
    public MutableGaugeFloat newGauge(final String name, final String desc, final float iVal) {
        return this.newGauge(Interns.info(name, desc), iVal);
    }
    
    public synchronized MutableGaugeFloat newGauge(final MetricsInfo info, final float iVal) {
        this.checkMetricName(info.name());
        final MutableGaugeFloat ret = new MutableGaugeFloat(info, iVal);
        this.metricsMap.put(info.name(), ret);
        return ret;
    }
    
    public synchronized MutableQuantiles newQuantiles(final String name, final String desc, final String sampleName, final String valueName, final int interval) {
        this.checkMetricName(name);
        if (interval <= 0) {
            throw new MetricsException("Interval should be positive.  Value passed is: " + interval);
        }
        final MutableQuantiles ret = new MutableQuantiles(name, desc, sampleName, valueName, interval);
        this.metricsMap.put(name, ret);
        return ret;
    }
    
    public synchronized MutableStat newStat(final String name, final String desc, final String sampleName, final String valueName, final boolean extended) {
        this.checkMetricName(name);
        final MutableStat ret = new MutableStat(name, desc, sampleName, valueName, extended);
        this.metricsMap.put(name, ret);
        return ret;
    }
    
    public MutableStat newStat(final String name, final String desc, final String sampleName, final String valueName) {
        return this.newStat(name, desc, sampleName, valueName, false);
    }
    
    public MutableRate newRate(final String name) {
        return this.newRate(name, name, false);
    }
    
    public MutableRate newRate(final String name, final String description) {
        return this.newRate(name, description, false);
    }
    
    public MutableRate newRate(final String name, final String desc, final boolean extended) {
        return this.newRate(name, desc, extended, true);
    }
    
    @InterfaceAudience.Private
    public synchronized MutableRate newRate(final String name, final String desc, final boolean extended, final boolean returnExisting) {
        if (returnExisting) {
            final MutableMetric rate = this.metricsMap.get(name);
            if (rate != null) {
                if (rate instanceof MutableRate) {
                    return (MutableRate)rate;
                }
                throw new MetricsException("Unexpected metrics type " + rate.getClass() + " for " + name);
            }
        }
        this.checkMetricName(name);
        final MutableRate ret = new MutableRate(name, desc, extended);
        this.metricsMap.put(name, ret);
        return ret;
    }
    
    public synchronized MutableRatesWithAggregation newRatesWithAggregation(final String name) {
        this.checkMetricName(name);
        final MutableRatesWithAggregation rates = new MutableRatesWithAggregation();
        this.metricsMap.put(name, rates);
        return rates;
    }
    
    public synchronized MutableRollingAverages newMutableRollingAverages(final String name, final String valueName) {
        this.checkMetricName(name);
        final MutableRollingAverages rollingAverages = new MutableRollingAverages(valueName);
        this.metricsMap.put(name, rollingAverages);
        return rollingAverages;
    }
    
    synchronized void add(final String name, final MutableMetric metric) {
        this.checkMetricName(name);
        this.metricsMap.put(name, metric);
    }
    
    public synchronized void add(final String name, final long value) {
        final MutableMetric m = this.metricsMap.get(name);
        if (m != null) {
            if (!(m instanceof MutableStat)) {
                throw new MetricsException("Unsupported add(value) for metric " + name);
            }
            ((MutableStat)m).add(value);
        }
        else {
            this.metricsMap.put(name, this.newRate(name));
            this.add(name, value);
        }
    }
    
    public MetricsRegistry setContext(final String name) {
        return this.tag(MsInfo.Context, name, true);
    }
    
    public MetricsRegistry tag(final String name, final String description, final String value) {
        return this.tag(name, description, value, false);
    }
    
    public MetricsRegistry tag(final String name, final String description, final String value, final boolean override) {
        return this.tag(Interns.info(name, description), value, override);
    }
    
    public synchronized MetricsRegistry tag(final MetricsInfo info, final String value, final boolean override) {
        if (!override) {
            this.checkTagName(info.name());
        }
        this.tagsMap.put(info.name(), Interns.tag(info, value));
        return this;
    }
    
    public MetricsRegistry tag(final MetricsInfo info, final String value) {
        return this.tag(info, value, false);
    }
    
    Collection<MetricsTag> tags() {
        return this.tagsMap.values();
    }
    
    Collection<MutableMetric> metrics() {
        return this.metricsMap.values();
    }
    
    private void checkMetricName(final String name) {
        boolean foundWhitespace = false;
        for (int i = 0; i < name.length(); ++i) {
            final char c = name.charAt(i);
            if (Character.isWhitespace(c)) {
                foundWhitespace = true;
                break;
            }
        }
        if (foundWhitespace) {
            throw new MetricsException("Metric name '" + name + "' contains illegal whitespace character");
        }
        if (this.metricsMap.containsKey(name)) {
            throw new MetricsException("Metric name " + name + " already exists!");
        }
    }
    
    private void checkTagName(final String name) {
        if (this.tagsMap.containsKey(name)) {
            throw new MetricsException("Tag " + name + " already exists!");
        }
    }
    
    public synchronized void snapshot(final MetricsRecordBuilder builder, final boolean all) {
        for (final MetricsTag tag : this.tags()) {
            builder.add(tag);
        }
        for (final MutableMetric metric : this.metrics()) {
            metric.snapshot(builder, all);
        }
    }
    
    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "{", "}").add("info=" + this.metricsInfo.toString()).add("tags=" + this.tags()).add("metrics=" + this.metrics()).toString();
    }
}
