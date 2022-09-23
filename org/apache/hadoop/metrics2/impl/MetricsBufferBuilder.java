// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import java.util.ArrayList;

class MetricsBufferBuilder extends ArrayList<MetricsBuffer.Entry>
{
    private static final long serialVersionUID = 1L;
    
    boolean add(final String name, final Iterable<MetricsRecordImpl> records) {
        return this.add(new MetricsBuffer.Entry(name, records));
    }
    
    MetricsBuffer get() {
        return new MetricsBuffer(this);
    }
}
