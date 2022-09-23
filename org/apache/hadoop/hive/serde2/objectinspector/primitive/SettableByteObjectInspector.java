// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

public interface SettableByteObjectInspector extends ByteObjectInspector
{
    Object set(final Object p0, final byte p1);
    
    Object create(final byte p0);
}
