// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

public interface SettableFloatObjectInspector extends FloatObjectInspector
{
    Object set(final Object p0, final float p1);
    
    Object create(final float p0);
}
