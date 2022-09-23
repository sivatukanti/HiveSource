// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantBooleanObjectInspector extends JavaBooleanObjectInspector implements ConstantObjectInspector
{
    private Boolean value;
    
    public JavaConstantBooleanObjectInspector(final Boolean value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new BooleanWritable(this.value);
    }
}
