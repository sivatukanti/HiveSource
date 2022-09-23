// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantBinaryObjectInspector extends WritableBinaryObjectInspector implements ConstantObjectInspector
{
    private BytesWritable value;
    
    protected WritableConstantBinaryObjectInspector() {
    }
    
    public WritableConstantBinaryObjectInspector(final BytesWritable value) {
        this.value = value;
    }
    
    @Override
    public BytesWritable getWritableConstantValue() {
        return this.value;
    }
}
