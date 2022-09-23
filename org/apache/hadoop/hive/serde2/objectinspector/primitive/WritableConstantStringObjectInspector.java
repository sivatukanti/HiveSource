// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantStringObjectInspector extends WritableStringObjectInspector implements ConstantObjectInspector
{
    private Text value;
    
    protected WritableConstantStringObjectInspector() {
    }
    
    WritableConstantStringObjectInspector(final Text value) {
        this.value = value;
    }
    
    @Override
    public Text getWritableConstantValue() {
        return this.value;
    }
}
