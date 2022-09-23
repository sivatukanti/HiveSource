// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.Map;

public interface MapObjectInspector extends ObjectInspector
{
    ObjectInspector getMapKeyObjectInspector();
    
    ObjectInspector getMapValueObjectInspector();
    
    Object getMapValueElement(final Object p0, final Object p1);
    
    Map<?, ?> getMap(final Object p0);
    
    int getMapSize(final Object p0);
}
