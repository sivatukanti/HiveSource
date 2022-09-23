// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.math.BigDecimal;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantIntObjectInspector extends WritableIntObjectInspector implements ConstantObjectInspector
{
    private IntWritable value;
    
    protected WritableConstantIntObjectInspector() {
    }
    
    WritableConstantIntObjectInspector(final IntWritable value) {
        this.value = value;
    }
    
    @Override
    public IntWritable getWritableConstantValue() {
        return this.value;
    }
    
    @Override
    public int precision() {
        if (this.value == null) {
            return super.precision();
        }
        return BigDecimal.valueOf(this.value.get()).precision();
    }
}
