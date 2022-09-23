// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantDoubleObjectInspector extends JavaDoubleObjectInspector implements ConstantObjectInspector
{
    private Double value;
    
    public JavaConstantDoubleObjectInspector(final Double value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new DoubleWritable(this.value);
    }
}
