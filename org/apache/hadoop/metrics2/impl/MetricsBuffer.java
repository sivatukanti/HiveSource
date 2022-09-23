// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.impl;

import java.util.Iterator;

class MetricsBuffer implements Iterable<Entry>
{
    private final Iterable<Entry> mutable;
    
    MetricsBuffer(final Iterable<Entry> mutable) {
        this.mutable = mutable;
    }
    
    @Override
    public Iterator<Entry> iterator() {
        return this.mutable.iterator();
    }
    
    static class Entry
    {
        private final String sourceName;
        private final Iterable<MetricsRecordImpl> records;
        
        Entry(final String name, final Iterable<MetricsRecordImpl> records) {
            this.sourceName = name;
            this.records = records;
        }
        
        String name() {
            return this.sourceName;
        }
        
        Iterable<MetricsRecordImpl> records() {
            return this.records;
        }
    }
}
