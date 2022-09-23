// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.sql.Date;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableDateObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableDateObjectInspector
{
    public WritableDateObjectInspector() {
        super(TypeInfoFactory.dateTypeInfo);
    }
    
    @Override
    public DateWritable getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : ((DateWritable)o);
    }
    
    @Override
    public Date getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((DateWritable)o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new DateWritable((DateWritable)o);
    }
    
    @Override
    public Object set(final Object o, final Date d) {
        if (d == null) {
            return null;
        }
        ((DateWritable)o).set(d);
        return o;
    }
    
    @Override
    public Object set(final Object o, final DateWritable d) {
        if (d == null) {
            return null;
        }
        ((DateWritable)o).set(d);
        return o;
    }
    
    @Override
    public Object create(final Date d) {
        return new DateWritable(d);
    }
}
