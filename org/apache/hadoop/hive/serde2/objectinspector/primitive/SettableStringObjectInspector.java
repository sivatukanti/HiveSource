// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.Text;

public interface SettableStringObjectInspector extends StringObjectInspector
{
    Object set(final Object p0, final Text p1);
    
    Object set(final Object p0, final String p1);
    
    Object create(final Text p0);
    
    Object create(final String p0);
}
