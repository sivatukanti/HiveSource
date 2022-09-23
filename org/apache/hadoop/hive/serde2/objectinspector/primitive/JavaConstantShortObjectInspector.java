// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantShortObjectInspector extends JavaDateObjectInspector implements ConstantObjectInspector
{
    private Short value;
    
    public JavaConstantShortObjectInspector(final Short value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new ShortWritable(this.value);
    }
}
