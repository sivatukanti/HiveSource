// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.math.BigDecimal;
import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantByteObjectInspector extends WritableByteObjectInspector implements ConstantObjectInspector
{
    private ByteWritable value;
    
    protected WritableConstantByteObjectInspector() {
    }
    
    WritableConstantByteObjectInspector(final ByteWritable value) {
        this.value = value;
    }
    
    @Override
    public ByteWritable getWritableConstantValue() {
        return this.value;
    }
    
    @Override
    public int precision() {
        if (this.value == null) {
            return super.precision();
        }
        return BigDecimal.valueOf(this.value.get()).precision();
    }
}
