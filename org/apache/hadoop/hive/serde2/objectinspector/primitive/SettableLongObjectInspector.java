// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

public interface SettableLongObjectInspector extends LongObjectInspector
{
    Object set(final Object p0, final long p1);
    
    Object create(final long p0);
}
