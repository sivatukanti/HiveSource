// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableHiveIntervalDayTimeObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableHiveIntervalDayTimeObjectInspector
{
    public WritableHiveIntervalDayTimeObjectInspector() {
        super(TypeInfoFactory.intervalDayTimeTypeInfo);
    }
    
    @Override
    public HiveIntervalDayTime getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((HiveIntervalDayTimeWritable)o).getHiveIntervalDayTime();
    }
    
    @Override
    public HiveIntervalDayTimeWritable getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : ((HiveIntervalDayTimeWritable)o);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new HiveIntervalDayTimeWritable((HiveIntervalDayTimeWritable)o);
    }
    
    @Override
    public Object set(final Object o, final HiveIntervalDayTime i) {
        if (i == null) {
            return null;
        }
        ((HiveIntervalDayTimeWritable)o).set(i);
        return o;
    }
    
    @Override
    public Object set(final Object o, final HiveIntervalDayTimeWritable i) {
        if (i == null) {
            return null;
        }
        ((HiveIntervalDayTimeWritable)o).set(i);
        return o;
    }
    
    @Override
    public Object create(final HiveIntervalDayTime i) {
        return (i == null) ? null : new HiveIntervalDayTimeWritable(i);
    }
}
