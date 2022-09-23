// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import java.sql.Timestamp;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantTimestampObjectInspector extends JavaTimestampObjectInspector implements ConstantObjectInspector
{
    private Timestamp value;
    
    public JavaConstantTimestampObjectInspector(final Timestamp value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new TimestampWritable(this.value);
    }
}
