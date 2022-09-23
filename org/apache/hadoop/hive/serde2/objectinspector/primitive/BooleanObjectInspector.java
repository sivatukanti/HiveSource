// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;

public interface BooleanObjectInspector extends PrimitiveObjectInspector
{
    boolean get(final Object p0);
}
