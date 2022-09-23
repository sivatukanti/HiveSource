// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import java.util.Collection;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import org.apache.hadoop.metrics2.util.Contracts;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsTag;
import java.util.List;
import org.apache.hadoop.metrics2.MetricsInfo;

class MetricsRecordImpl extends AbstractMetricsRecord
{
    protected static final String DEFAULT_CONTEXT = "default";
    private final long timestamp;
    private final MetricsInfo info;
    private final List<MetricsTag> tags;
    private final Iterable<AbstractMetric> metrics;
    
    public MetricsRecordImpl(final MetricsInfo info, final long timestamp, final List<MetricsTag> tags, final Iterable<AbstractMetric> metrics) {
        this.timestamp = Contracts.checkArg(timestamp, timestamp > 0L, "timestamp");
        this.info = Preconditions.checkNotNull(info, (Object)"info");
        this.tags = Preconditions.checkNotNull(tags, (Object)"tags");
        this.metrics = Preconditions.checkNotNull(metrics, (Object)"metrics");
    }
    
    @Override
    public long timestamp() {
        return this.timestamp;
    }
    
    @Override
    public String name() {
        return this.info.name();
    }
    
    MetricsInfo info() {
        return this.info;
    }
    
    @Override
    public String description() {
        return this.info.description();
    }
    
    @Override
    public String context() {
        for (final MetricsTag t : this.tags) {
            if (t.info() == MsInfo.Context) {
                return t.value();
            }
        }
        return "default";
    }
    
    @Override
    public List<MetricsTag> tags() {
        return this.tags;
    }
    
    @Override
    public Iterable<AbstractMetric> metrics() {
        return this.metrics;
    }
}
