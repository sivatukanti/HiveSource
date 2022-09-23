// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public interface HiveVarcharObjectInspector extends PrimitiveObjectInspector
{
    HiveVarcharWritable getPrimitiveWritableObject(final Object p0);
    
    HiveVarchar getPrimitiveJavaObject(final Object p0);
}
