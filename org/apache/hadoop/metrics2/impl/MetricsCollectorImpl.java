// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import java.util.Iterator;
import org.apache.hadoop.metrics2.lib.Interns;
import org.apache.hadoop.metrics2.MetricsInfo;
import com.google.common.collect.Lists;
import org.apache.hadoop.metrics2.MetricsFilter;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.metrics2.MetricsCollector;

@InterfaceAudience.Private
@VisibleForTesting
public class MetricsCollectorImpl implements MetricsCollector, Iterable<MetricsRecordBuilderImpl>
{
    private final List<MetricsRecordBuilderImpl> rbs;
    private MetricsFilter recordFilter;
    private MetricsFilter metricFilter;
    
    public MetricsCollectorImpl() {
        this.rbs = (List<MetricsRecordBuilderImpl>)Lists.newArrayList();
    }
    
    @Override
    public MetricsRecordBuilderImpl addRecord(final MetricsInfo info) {
        final boolean acceptable = this.recordFilter == null || this.recordFilter.accepts(info.name());
        final MetricsRecordBuilderImpl rb = new MetricsRecordBuilderImpl(this, info, this.recordFilter, this.metricFilter, acceptable);
        if (acceptable) {
            this.rbs.add(rb);
        }
        return rb;
    }
    
    @Override
    public MetricsRecordBuilderImpl addRecord(final String name) {
        return this.addRecord(Interns.info(name, name + " record"));
    }
    
    public List<MetricsRecordImpl> getRecords() {
        final List<MetricsRecordImpl> recs = (List<MetricsRecordImpl>)Lists.newArrayListWithCapacity(this.rbs.size());
        for (final MetricsRecordBuilderImpl rb : this.rbs) {
            final MetricsRecordImpl mr = rb.getRecord();
            if (mr != null) {
                recs.add(mr);
            }
        }
        return recs;
    }
    
    @Override
    public Iterator<MetricsRecordBuilderImpl> iterator() {
        return this.rbs.iterator();
    }
    
    @InterfaceAudience.Private
    public void clear() {
        this.rbs.clear();
    }
    
    MetricsCollectorImpl setRecordFilter(final MetricsFilter rf) {
        this.recordFilter = rf;
        return this;
    }
    
    MetricsCollectorImpl setMetricFilter(final MetricsFilter mf) {
        this.metricFilter = mf;
        return this;
    }
}
