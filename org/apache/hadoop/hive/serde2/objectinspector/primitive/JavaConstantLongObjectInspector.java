// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantLongObjectInspector extends JavaLongObjectInspector implements ConstantObjectInspector
{
    private Long value;
    
    public JavaConstantLongObjectInspector(final Long value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new LongWritable(this.value);
    }
}
