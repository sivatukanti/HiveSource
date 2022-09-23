// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.sql.Timestamp;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public interface TimestampObjectInspector extends PrimitiveObjectInspector
{
    TimestampWritable getPrimitiveWritableObject(final Object p0);
    
    Timestamp getPrimitiveJavaObject(final Object p0);
}
