// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.List;

public interface ListObjectInspector extends ObjectInspector
{
    ObjectInspector getListElementObjectInspector();
    
    Object getListElement(final Object p0, final int p1);
    
    int getListLength(final Object p0);
    
    List<?> getList(final Object p0);
}
