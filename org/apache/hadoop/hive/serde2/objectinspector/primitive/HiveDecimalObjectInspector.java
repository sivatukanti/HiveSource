// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public interface HiveDecimalObjectInspector extends PrimitiveObjectInspector
{
    HiveDecimalWritable getPrimitiveWritableObject(final Object p0);
    
    HiveDecimal getPrimitiveJavaObject(final Object p0);
}
