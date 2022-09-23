// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableConstantHiveVarcharObjectInspector extends WritableHiveVarcharObjectInspector implements ConstantObjectInspector
{
    protected HiveVarcharWritable value;
    
    WritableConstantHiveVarcharObjectInspector() {
    }
    
    WritableConstantHiveVarcharObjectInspector(final VarcharTypeInfo typeInfo, final HiveVarcharWritable value) {
        super(typeInfo);
        this.value = value;
    }
    
    @Override
    public HiveVarcharWritable getWritableConstantValue() {
        return this.value;
    }
}
