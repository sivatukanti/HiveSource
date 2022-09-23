// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantBinaryObjectInspector extends JavaBinaryObjectInspector implements ConstantObjectInspector
{
    private byte[] value;
    
    public JavaConstantBinaryObjectInspector(final byte[] value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new BytesWritable(this.value);
    }
}
