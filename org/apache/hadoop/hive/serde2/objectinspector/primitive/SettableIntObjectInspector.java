// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

public interface SettableIntObjectInspector extends IntObjectInspector
{
    Object set(final Object p0, final int p1);
    
    Object create(final int p0);
}
