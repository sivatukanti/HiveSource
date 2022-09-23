// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantDateObjectInspector extends WritableDateObjectInspector implements ConstantObjectInspector
{
    private DateWritable value;
    
    protected WritableConstantDateObjectInspector() {
    }
    
    WritableConstantDateObjectInspector(final DateWritable value) {
        this.value = value;
    }
    
    @Override
    public DateWritable getWritableConstantValue() {
        return this.value;
    }
}
