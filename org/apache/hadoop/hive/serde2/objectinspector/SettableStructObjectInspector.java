// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

public abstract class SettableStructObjectInspector extends StructObjectInspector
{
    public abstract Object create();
    
    public abstract Object setStructFieldData(final Object p0, final StructField p1, final Object p2);
    
    @Override
    public boolean isSettable() {
        return true;
    }
}
