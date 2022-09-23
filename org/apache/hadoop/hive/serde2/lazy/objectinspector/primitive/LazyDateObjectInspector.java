// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyPrimitive;
import java.sql.Date;
import org.apache.hadoop.hive.serde2.lazy.LazyDate;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DateObjectInspector;
import org.apache.hadoop.hive.serde2.io.DateWritable;

public class LazyDateObjectInspector extends AbstractPrimitiveLazyObjectInspector<DateWritable> implements DateObjectInspector
{
    protected LazyDateObjectInspector() {
        super(TypeInfoFactory.dateTypeInfo);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyDate((LazyDate)o);
    }
    
    @Override
    public Date getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((LazyPrimitive<OI, DateWritable>)o).getWritableObject().get();
    }
}
