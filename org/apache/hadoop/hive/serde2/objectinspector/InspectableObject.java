// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

public class InspectableObject
{
    public Object o;
    public ObjectInspector oi;
    
    public InspectableObject() {
        this(null, null);
    }
    
    public InspectableObject(final Object o, final ObjectInspector oi) {
        this.o = o;
        this.oi = oi;
    }
}
