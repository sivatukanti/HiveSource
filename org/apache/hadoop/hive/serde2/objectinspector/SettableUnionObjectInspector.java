// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

public abstract class SettableUnionObjectInspector implements UnionObjectInspector
{
    public abstract Object create();
    
    public abstract Object addField(final Object p0, final ObjectInspector p1);
}
