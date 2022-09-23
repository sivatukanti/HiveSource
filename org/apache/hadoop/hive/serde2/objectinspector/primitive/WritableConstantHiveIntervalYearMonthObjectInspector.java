// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantHiveIntervalYearMonthObjectInspector extends WritableHiveIntervalYearMonthObjectInspector implements ConstantObjectInspector
{
    private HiveIntervalYearMonthWritable value;
    
    protected WritableConstantHiveIntervalYearMonthObjectInspector() {
    }
    
    WritableConstantHiveIntervalYearMonthObjectInspector(final HiveIntervalYearMonthWritable value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        return this.value;
    }
}
