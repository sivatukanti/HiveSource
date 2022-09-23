// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantFloatObjectInspector extends WritableFloatObjectInspector implements ConstantObjectInspector
{
    private FloatWritable value;
    
    protected WritableConstantFloatObjectInspector() {
    }
    
    WritableConstantFloatObjectInspector(final FloatWritable value) {
        this.value = value;
    }
    
    @Override
    public FloatWritable getWritableConstantValue() {
        return this.value;
    }
}
