// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantIntObjectInspector extends JavaIntObjectInspector implements ConstantObjectInspector
{
    private Integer value;
    
    public JavaConstantIntObjectInspector(final Integer value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new IntWritable(this.value);
    }
}
