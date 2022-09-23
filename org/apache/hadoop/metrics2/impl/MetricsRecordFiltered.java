// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsTag;
import java.util.Collection;
import org.apache.hadoop.metrics2.MetricsFilter;
import org.apache.hadoop.metrics2.MetricsRecord;

class MetricsRecordFiltered extends AbstractMetricsRecord
{
    private final MetricsRecord delegate;
    private final MetricsFilter filter;
    
    MetricsRecordFiltered(final MetricsRecord delegate, final MetricsFilter filter) {
        this.delegate = delegate;
        this.filter = filter;
    }
    
    @Override
    public long timestamp() {
        return this.delegate.timestamp();
    }
    
    @Override
    public String name() {
        return this.delegate.name();
    }
    
    @Override
    public String description() {
        return this.delegate.description();
    }
    
    @Override
    public String context() {
        return this.delegate.context();
    }
    
    @Override
    public Collection<MetricsTag> tags() {
        return this.delegate.tags();
    }
    
    @Override
    public Iterable<AbstractMetric> metrics() {
        return new Iterable<AbstractMetric>() {
            final Iterator<AbstractMetric> it = MetricsRecordFiltered.this.delegate.metrics().iterator();
            
            @Override
            public Iterator<AbstractMetric> iterator() {
                return new AbstractIterator<AbstractMetric>() {
                    public AbstractMetric computeNext() {
                        while (Iterable.this.it.hasNext()) {
                            final AbstractMetric next = Iterable.this.it.next();
                            if (MetricsRecordFiltered.this.filter.accepts(next.name())) {
                                return next;
                            }
                        }
                        return this.endOfData();
                    }
                };
            }
        };
    }
}
