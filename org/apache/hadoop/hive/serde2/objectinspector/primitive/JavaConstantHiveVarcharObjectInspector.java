// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantHiveVarcharObjectInspector extends JavaHiveVarcharObjectInspector implements ConstantObjectInspector
{
    private HiveVarchar value;
    
    public JavaConstantHiveVarcharObjectInspector(final HiveVarchar value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new HiveVarcharWritable(this.value);
    }
}
