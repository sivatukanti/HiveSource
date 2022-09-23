// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantDoubleObjectInspector extends WritableDoubleObjectInspector implements ConstantObjectInspector
{
    private DoubleWritable value;
    
    protected WritableConstantDoubleObjectInspector() {
    }
    
    WritableConstantDoubleObjectInspector(final DoubleWritable value) {
        this.value = value;
    }
    
    @Override
    public DoubleWritable getWritableConstantValue() {
        return this.value;
    }
}
