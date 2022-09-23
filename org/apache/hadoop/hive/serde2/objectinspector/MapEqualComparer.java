// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

public interface MapEqualComparer
{
    int compare(final Object p0, final MapObjectInspector p1, final Object p2, final MapObjectInspector p3);
}
