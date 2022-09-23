// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.List;

public interface UnionObjectInspector extends ObjectInspector
{
    List<ObjectInspector> getObjectInspectors();
    
    byte getTag(final Object p0);
    
    Object getField(final Object p0);
}
