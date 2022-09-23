// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantFloatObjectInspector extends JavaFloatObjectInspector implements ConstantObjectInspector
{
    private Float value;
    
    public JavaConstantFloatObjectInspector(final Float value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new FloatWritable(this.value);
    }
}
