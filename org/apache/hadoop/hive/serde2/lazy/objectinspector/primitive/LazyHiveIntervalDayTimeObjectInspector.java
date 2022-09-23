// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.serde2.lazy.LazyHiveIntervalDayTime;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveIntervalDayTimeObjectInspector;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;

public class LazyHiveIntervalDayTimeObjectInspector extends AbstractPrimitiveLazyObjectInspector<HiveIntervalDayTimeWritable> implements HiveIntervalDayTimeObjectInspector
{
    LazyHiveIntervalDayTimeObjectInspector() {
        super(TypeInfoFactory.intervalDayTimeTypeInfo);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyHiveIntervalDayTime((LazyHiveIntervalDayTime)o);
    }
    
    @Override
    public HiveIntervalDayTime getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((LazyHiveIntervalDayTime)o).getWritableObject().getHiveIntervalDayTime();
    }
}
