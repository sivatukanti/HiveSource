// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.common.type.HiveDecimal;

public interface SettableHiveDecimalObjectInspector extends HiveDecimalObjectInspector
{
    Object set(final Object p0, final byte[] p1, final int p2);
    
    Object set(final Object p0, final HiveDecimal p1);
    
    Object set(final Object p0, final HiveDecimalWritable p1);
    
    Object create(final byte[] p0, final int p1);
    
    Object create(final HiveDecimal p0);
}
