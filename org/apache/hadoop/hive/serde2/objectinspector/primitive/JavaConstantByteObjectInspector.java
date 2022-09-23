// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantByteObjectInspector extends JavaByteObjectInspector implements ConstantObjectInspector
{
    private Byte value;
    
    public JavaConstantByteObjectInspector(final Byte value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new ByteWritable(this.value);
    }
}
