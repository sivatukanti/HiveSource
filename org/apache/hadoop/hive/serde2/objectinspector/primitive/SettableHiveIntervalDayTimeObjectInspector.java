// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;

public interface SettableHiveIntervalDayTimeObjectInspector extends HiveIntervalDayTimeObjectInspector
{
    Object set(final Object p0, final HiveIntervalDayTime p1);
    
    Object set(final Object p0, final HiveIntervalDayTimeWritable p1);
    
    Object create(final HiveIntervalDayTime p0);
}
