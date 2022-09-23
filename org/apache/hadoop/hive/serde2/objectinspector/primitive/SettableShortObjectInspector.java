// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

public interface SettableShortObjectInspector extends ShortObjectInspector
{
    Object set(final Object p0, final short p1);
    
    Object create(final short p0);
}
