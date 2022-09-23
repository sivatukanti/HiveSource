// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

public interface SettableBooleanObjectInspector extends BooleanObjectInspector
{
    Object set(final Object p0, final boolean p1);
    
    Object create(final boolean p0);
}
