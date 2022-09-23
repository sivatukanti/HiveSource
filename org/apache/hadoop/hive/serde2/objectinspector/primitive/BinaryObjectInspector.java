// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public interface BinaryObjectInspector extends PrimitiveObjectInspector
{
    byte[] getPrimitiveJavaObject(final Object p0);
    
    BytesWritable getPrimitiveWritableObject(final Object p0);
}
