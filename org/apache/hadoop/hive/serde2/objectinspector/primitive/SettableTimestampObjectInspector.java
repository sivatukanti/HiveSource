// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import java.sql.Timestamp;

public interface SettableTimestampObjectInspector extends TimestampObjectInspector
{
    Object set(final Object p0, final byte[] p1, final int p2);
    
    Object set(final Object p0, final Timestamp p1);
    
    Object set(final Object p0, final TimestampWritable p1);
    
    Object create(final byte[] p0, final int p1);
    
    Object create(final Timestamp p0);
}
