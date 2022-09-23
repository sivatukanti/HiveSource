// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

public interface ConstantObjectInspector extends ObjectInspector
{
    Object getWritableConstantValue();
}
