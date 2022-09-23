// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.sql.Date;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public interface DateObjectInspector extends PrimitiveObjectInspector
{
    DateWritable getPrimitiveWritableObject(final Object p0);
    
    Date getPrimitiveJavaObject(final Object p0);
}
