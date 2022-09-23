// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantHiveCharObjectInspector extends JavaHiveCharObjectInspector implements ConstantObjectInspector
{
    private HiveChar value;
    
    public JavaConstantHiveCharObjectInspector(final HiveChar value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new HiveCharWritable(this.value);
    }
}
