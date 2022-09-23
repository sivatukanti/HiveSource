// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableHiveIntervalYearMonthObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableHiveIntervalYearMonthObjectInspector
{
    public WritableHiveIntervalYearMonthObjectInspector() {
        super(TypeInfoFactory.intervalYearMonthTypeInfo);
    }
    
    @Override
    public HiveIntervalYearMonth getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((HiveIntervalYearMonthWritable)o).getHiveIntervalYearMonth();
    }
    
    @Override
    public HiveIntervalYearMonthWritable getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : ((HiveIntervalYearMonthWritable)o);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new HiveIntervalYearMonthWritable((HiveIntervalYearMonthWritable)o);
    }
    
    @Override
    public Object set(final Object o, final HiveIntervalYearMonth i) {
        if (i == null) {
            return null;
        }
        ((HiveIntervalYearMonthWritable)o).set(i);
        return o;
    }
    
    @Override
    public Object set(final Object o, final HiveIntervalYearMonthWritable i) {
        if (i == null) {
            return null;
        }
        ((HiveIntervalYearMonthWritable)o).set(i);
        return o;
    }
    
    @Override
    public Object create(final HiveIntervalYearMonth i) {
        return (i == null) ? null : new HiveIntervalYearMonthWritable(i);
    }
}
