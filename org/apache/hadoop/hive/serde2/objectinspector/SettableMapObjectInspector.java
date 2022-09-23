// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

public interface SettableMapObjectInspector extends MapObjectInspector
{
    Object create();
    
    Object put(final Object p0, final Object p1, final Object p2);
    
    Object remove(final Object p0, final Object p1);
    
    Object clear(final Object p0);
}
