// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.sql.Date;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaDateObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableDateObjectInspector
{
    protected JavaDateObjectInspector() {
        super(TypeInfoFactory.dateTypeInfo);
    }
    
    @Override
    public DateWritable getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new DateWritable((Date)o);
    }
    
    @Override
    public Date getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((Date)o);
    }
    
    public Date get(final Object o) {
        return (Date)o;
    }
    
    @Override
    public Object set(final Object o, final Date value) {
        if (value == null) {
            return null;
        }
        ((Date)o).setTime(value.getTime());
        return o;
    }
    
    @Override
    public Object set(final Object o, final DateWritable d) {
        if (d == null) {
            return null;
        }
        ((Date)o).setTime(d.get().getTime());
        return o;
    }
    
    @Override
    public Object create(final Date value) {
        return new Date(value.getTime());
    }
}
