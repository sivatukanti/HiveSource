// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaHiveIntervalYearMonthObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableHiveIntervalYearMonthObjectInspector
{
    public JavaHiveIntervalYearMonthObjectInspector() {
        super(TypeInfoFactory.intervalYearMonthTypeInfo);
    }
    
    @Override
    public HiveIntervalYearMonth getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((HiveIntervalYearMonth)o);
    }
    
    @Override
    public HiveIntervalYearMonthWritable getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new HiveIntervalYearMonthWritable((HiveIntervalYearMonth)o);
    }
    
    @Override
    public Object set(final Object o, final HiveIntervalYearMonth i) {
        return (i == null) ? null : new HiveIntervalYearMonth(i);
    }
    
    @Override
    public Object set(final Object o, final HiveIntervalYearMonthWritable i) {
        return (i == null) ? null : i.getHiveIntervalYearMonth();
    }
    
    @Override
    public Object create(final HiveIntervalYearMonth i) {
        return (i == null) ? null : new HiveIntervalYearMonth(i);
    }
}
