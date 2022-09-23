// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantHiveCharObjectInspector extends WritableHiveCharObjectInspector implements ConstantObjectInspector
{
    protected HiveCharWritable value;
    
    WritableConstantHiveCharObjectInspector() {
    }
    
    WritableConstantHiveCharObjectInspector(final CharTypeInfo typeInfo, final HiveCharWritable value) {
        super(typeInfo);
        this.value = value;
    }
    
    @Override
    public HiveCharWritable getWritableConstantValue() {
        return this.value;
    }
}
