// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.DateWritable;
import java.sql.Date;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class JavaConstantDateObjectInspector extends JavaDateObjectInspector implements ConstantObjectInspector
{
    private Date value;
    
    public JavaConstantDateObjectInspector(final Date value) {
        this.value = value;
    }
    
    @Override
    public Object getWritableConstantValue() {
        if (this.value == null) {
            return null;
        }
        return new DateWritable(this.value);
    }
}
