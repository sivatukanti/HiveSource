// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantTimestampObjectInspector extends WritableTimestampObjectInspector implements ConstantObjectInspector
{
    private TimestampWritable value;
    
    protected WritableConstantTimestampObjectInspector() {
    }
    
    WritableConstantTimestampObjectInspector(final TimestampWritable value) {
        this.value = value;
    }
    
    @Override
    public TimestampWritable getWritableConstantValue() {
        return this.value;
    }
}
