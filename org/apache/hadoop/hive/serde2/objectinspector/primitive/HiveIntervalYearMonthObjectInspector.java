// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public interface HiveIntervalYearMonthObjectInspector extends PrimitiveObjectInspector
{
    HiveIntervalYearMonthWritable getPrimitiveWritableObject(final Object p0);
    
    HiveIntervalYearMonth getPrimitiveJavaObject(final Object p0);
}
