// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.common.type.HiveVarchar;

public interface SettableHiveVarcharObjectInspector extends HiveVarcharObjectInspector
{
    Object set(final Object p0, final HiveVarchar p1);
    
    Object set(final Object p0, final String p1);
    
    Object create(final HiveVarchar p0);
}
