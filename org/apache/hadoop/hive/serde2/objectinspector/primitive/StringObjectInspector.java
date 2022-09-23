// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public interface StringObjectInspector extends PrimitiveObjectInspector
{
    Text getPrimitiveWritableObject(final Object p0);
    
    String getPrimitiveJavaObject(final Object p0);
}
