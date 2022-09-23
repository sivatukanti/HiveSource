// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

public interface LazyObjectBase
{
    void init(final ByteArrayRef p0, final int p1, final int p2);
    
    void setNull();
    
    Object getObject();
}
