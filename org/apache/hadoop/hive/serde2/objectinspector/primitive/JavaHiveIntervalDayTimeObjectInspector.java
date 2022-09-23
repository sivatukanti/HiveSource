// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaHiveIntervalDayTimeObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableHiveIntervalDayTimeObjectInspector
{
    public JavaHiveIntervalDayTimeObjectInspector() {
        super(TypeInfoFactory.intervalDayTimeTypeInfo);
    }
    
    @Override
    public HiveIntervalDayTime getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((HiveIntervalDayTime)o);
    }
    
    @Override
    public HiveIntervalDayTimeWritable getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new HiveIntervalDayTimeWritable((HiveIntervalDayTime)o);
    }
    
    @Override
    public Object set(final Object o, final HiveIntervalDayTime i) {
        return (i == null) ? null : new HiveIntervalDayTime(i);
    }
    
    @Override
    public Object set(final Object o, final HiveIntervalDayTimeWritable i) {
        return (i == null) ? null : i.getHiveIntervalDayTime();
    }
    
    @Override
    public Object create(final HiveIntervalDayTime i) {
        return (i == null) ? null : new HiveIntervalDayTime(i);
    }
}
