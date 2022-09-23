// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;

public interface SettableHiveIntervalYearMonthObjectInspector extends HiveIntervalYearMonthObjectInspector
{
    Object set(final Object p0, final HiveIntervalYearMonth p1);
    
    Object set(final Object p0, final HiveIntervalYearMonthWritable p1);
    
    Object create(final HiveIntervalYearMonth p0);
}
