// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.math.BigDecimal;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantShortObjectInspector extends WritableShortObjectInspector implements ConstantObjectInspector
{
    private ShortWritable value;
    
    protected WritableConstantShortObjectInspector() {
    }
    
    WritableConstantShortObjectInspector(final ShortWritable value) {
        this.value = value;
    }
    
    @Override
    public ShortWritable getWritableConstantValue() {
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
