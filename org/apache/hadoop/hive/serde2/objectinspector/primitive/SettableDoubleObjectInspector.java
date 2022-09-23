// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

public interface SettableDoubleObjectInspector extends DoubleObjectInspector
{
    Object set(final Object p0, final double p1);
    
    Object create(final double p0);
}
