// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.BytesWritable;

public interface SettableBinaryObjectInspector extends BinaryObjectInspector
{
    Object set(final Object p0, final byte[] p1);
    
    Object set(final Object p0, final BytesWritable p1);
    
    Object create(final byte[] p0);
    
    Object create(final BytesWritable p0);
}
