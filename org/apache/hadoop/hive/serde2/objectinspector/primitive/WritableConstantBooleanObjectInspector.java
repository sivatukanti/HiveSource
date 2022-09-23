// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantBooleanObjectInspector extends WritableBooleanObjectInspector implements ConstantObjectInspector
{
    private BooleanWritable value;
    
    protected WritableConstantBooleanObjectInspector() {
    }
    
    WritableConstantBooleanObjectInspector(final BooleanWritable value) {
        this.value = value;
    }
    
    @Override
    public BooleanWritable getWritableConstantValue() {
        return this.value;
    }
}
