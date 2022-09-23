// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.util;

import java.util.StringJoiner;
import java.util.Set;
import java.util.LinkedHashMap;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsRecord;
import com.google.common.collect.Maps;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MetricsCache
{
    static final Logger LOG;
    static final int MAX_RECS_PER_NAME_DEFAULT = 1000;
    private final Map<String, RecordCache> map;
    private final int maxRecsPerName;
    
    public MetricsCache() {
        this(1000);
    }
    
    public MetricsCache(final int maxRecsPerName) {
        this.map = (Map<String, RecordCache>)Maps.newHashMap();
        this.maxRecsPerName = maxRecsPerName;
    }
    
    public Record update(final MetricsRecord mr, final boolean includingTags) {
        final String name = mr.name();
        RecordCache recordCache = this.map.get(name);
        if (recordCache == null) {
            recordCache = new RecordCache();
            this.map.put(name, recordCache);
        }
        final Collection<MetricsTag> tags = mr.tags();
        Record record = ((LinkedHashMap<K, Record>)recordCache).get(tags);
        if (record == null) {
            record = new Record();
            recordCache.put(tags, record);
        }
        for (final AbstractMetric m : mr.metrics()) {
            record.metrics.put(m.name(), m);
        }
        if (includingTags) {
            for (final MetricsTag t : mr.tags()) {
                record.tags.put(t.name(), t.value());
            }
        }
        return record;
    }
    
    public Record update(final MetricsRecord mr) {
        return this.update(mr, false);
    }
    
    public Record get(final String name, final Collection<MetricsTag> tags) {
        final RecordCache rc = this.map.get(name);
        if (rc == null) {
            return null;
        }
        return ((LinkedHashMap<K, Record>)rc).get(tags);
    }
    
    static {
        LOG = LoggerFactory.getLogger(MetricsCache.class);
    }
    
    class RecordCache extends LinkedHashMap<Collection<MetricsTag>, Record>
    {
        private static final long serialVersionUID = 1L;
        private boolean gotOverflow;
        
        RecordCache() {
            this.gotOverflow = false;
        }
        
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Collection<MetricsTag>, Record> eldest) {
            final boolean overflow = this.size() > MetricsCache.this.maxRecsPerName;
            if (overflow && !this.gotOverflow) {
                MetricsCache.LOG.warn("Metrics cache overflow at " + this.size() + " for " + eldest);
                this.gotOverflow = true;
            }
            return overflow;
        }
    }
    
    public static class Record
    {
        final Map<String, String> tags;
        final Map<String, AbstractMetric> metrics;
        
        public Record() {
            this.tags = (Map<String, String>)Maps.newHashMap();
            this.metrics = (Map<String, AbstractMetric>)Maps.newHashMap();
        }
        
        public String getTag(final String key) {
            return this.tags.get(key);
        }
        
        public Number getMetric(final String key) {
            final AbstractMetric metric = this.metrics.get(key);
            return (metric != null) ? metric.value() : null;
        }
        
        public AbstractMetric getMetricInstance(final String key) {
            return this.metrics.get(key);
        }
        
        public Set<Map.Entry<String, String>> tags() {
            return this.tags.entrySet();
        }
        
        @Deprecated
        public Set<Map.Entry<String, Number>> metrics() {
            final Map<String, Number> map = new LinkedHashMap<String, Number>(this.metrics.size());
            for (final Map.Entry<String, AbstractMetric> mapEntry : this.metrics.entrySet()) {
                map.put(mapEntry.getKey(), mapEntry.getValue().value());
            }
            return map.entrySet();
        }
        
        public Set<Map.Entry<String, AbstractMetric>> metricsEntrySet() {
            return this.metrics.entrySet();
        }
        
        @Override
        public String toString() {
            return new StringJoiner(", ", this.getClass().getSimpleName() + "{", "}").add("tags=" + this.tags).add("metrics=" + this.metrics).toString();
        }
    }
}
