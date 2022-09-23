// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantHiveDecimalObjectInspector extends JavaHiveDecimalObjectInspector implements ConstantObjectInspector
{
    private HiveDecimal value;
    
    public JavaConstantHiveDecimalObjectInspector(final HiveDecimal value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new HiveDecimalWritable(this.value);
    }
}
