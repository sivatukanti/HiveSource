// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.util.LinkedHashMap;
import org.codehaus.jackson.map.ObjectWriter;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class MetricsJsonBuilder extends MetricsRecordBuilder
{
    public static final Logger LOG;
    private final MetricsCollector parent;
    private Map<String, Object> innerMetrics;
    private static final ObjectWriter WRITER;
    
    public MetricsJsonBuilder(final MetricsCollector parent) {
        this.innerMetrics = new LinkedHashMap<String, Object>();
        this.parent = parent;
    }
    
    private MetricsRecordBuilder tuple(final String key, final Object value) {
        this.innerMetrics.put(key, value);
        return this;
    }
    
    @Override
    public MetricsRecordBuilder tag(final MetricsInfo info, final String value) {
        return this.tuple(info.name(), value);
    }
    
    @Override
    public MetricsRecordBuilder add(final MetricsTag tag) {
        return this.tuple(tag.name(), tag.value());
    }
    
    @Override
    public MetricsRecordBuilder add(final AbstractMetric metric) {
        return this.tuple(metric.info().name(), metric.toString());
    }
    
    @Override
    public MetricsRecordBuilder setContext(final String value) {
        return this.tuple("context", value);
    }
    
    @Override
    public MetricsRecordBuilder addCounter(final MetricsInfo info, final int value) {
        return this.tuple(info.name(), value);
    }
    
    @Override
    public MetricsRecordBuilder addCounter(final MetricsInfo info, final long value) {
        return this.tuple(info.name(), value);
    }
    
    @Override
    public MetricsRecordBuilder addGauge(final MetricsInfo info, final int value) {
        return this.tuple(info.name(), value);
    }
    
    @Override
    public MetricsRecordBuilder addGauge(final MetricsInfo info, final long value) {
        return this.tuple(info.name(), value);
    }
    
    @Override
    public MetricsRecordBuilder addGauge(final MetricsInfo info, final float value) {
        return this.tuple(info.name(), value);
    }
    
    @Override
    public MetricsRecordBuilder addGauge(final MetricsInfo info, final double value) {
        return this.tuple(info.name(), value);
    }
    
    @Override
    public MetricsCollector parent() {
        return this.parent;
    }
    
    @Override
    public String toString() {
        try {
            return MetricsJsonBuilder.WRITER.writeValueAsString(this.innerMetrics);
        }
        catch (IOException e) {
            MetricsJsonBuilder.LOG.warn("Failed to dump to Json.", e);
            return ExceptionUtils.getStackTrace(e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(MetricsRecordBuilder.class);
        WRITER = new ObjectMapper().writer();
    }
}
