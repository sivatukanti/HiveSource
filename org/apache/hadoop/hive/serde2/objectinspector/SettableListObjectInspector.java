// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

public interface SettableListObjectInspector extends ListObjectInspector
{
    Object create(final int p0);
    
    Object set(final Object p0, final int p1, final Object p2);
    
    Object resize(final Object p0, final int p1);
}
