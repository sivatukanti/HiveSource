// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.common.type.HiveChar;

public interface SettableHiveCharObjectInspector extends HiveCharObjectInspector
{
    Object set(final Object p0, final HiveChar p1);
    
    Object set(final Object p0, final String p1);
    
    Object create(final HiveChar p0);
}
