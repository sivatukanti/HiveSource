// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantHiveIntervalDayTimeObjectInspector extends WritableHiveIntervalDayTimeObjectInspector implements ConstantObjectInspector
{
    private HiveIntervalDayTimeWritable value;
    
    protected WritableConstantHiveIntervalDayTimeObjectInspector() {
    }
    
    WritableConstantHiveIntervalDayTimeObjectInspector(final HiveIntervalDayTimeWritable value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        return this.value;
    }
}
