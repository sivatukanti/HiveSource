// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.io;

import org.apache.hadoop.io.WritableComparator;

public class DoubleWritable extends org.apache.hadoop.io.DoubleWritable
{
    public DoubleWritable() {
    }
    
    public DoubleWritable(final double value) {
        super(value);
    }
    
    static {
        WritableComparator.define(DoubleWritable.class, new Comparator());
    }
}
