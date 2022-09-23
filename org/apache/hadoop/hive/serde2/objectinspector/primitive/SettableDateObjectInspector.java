// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.DateWritable;
import java.sql.Date;

public interface SettableDateObjectInspector extends DateObjectInspector
{
    Object set(final Object p0, final Date p1);
    
    Object set(final Object p0, final DateWritable p1);
    
    Object create(final Date p0);
}
