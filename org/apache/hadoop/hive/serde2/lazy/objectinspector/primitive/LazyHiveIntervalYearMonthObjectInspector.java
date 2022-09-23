// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.hive.serde2.lazy.LazyHiveIntervalYearMonth;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveIntervalYearMonthObjectInspector;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;

public class LazyHiveIntervalYearMonthObjectInspector extends AbstractPrimitiveLazyObjectInspector<HiveIntervalYearMonthWritable> implements HiveIntervalYearMonthObjectInspector
{
    LazyHiveIntervalYearMonthObjectInspector() {
        super(TypeInfoFactory.intervalYearMonthTypeInfo);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyHiveIntervalYearMonth((LazyHiveIntervalYearMonth)o);
    }
    
    @Override
    public HiveIntervalYearMonth getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((LazyHiveIntervalYearMonth)o).getWritableObject().getHiveIntervalYearMonth();
    }
}
