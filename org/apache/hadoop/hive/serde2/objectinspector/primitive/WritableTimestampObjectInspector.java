// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.sql.Timestamp;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableTimestampObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableTimestampObjectInspector
{
    public WritableTimestampObjectInspector() {
        super(TypeInfoFactory.timestampTypeInfo);
    }
    
    @Override
    public TimestampWritable getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : ((TimestampWritable)o);
    }
    
    @Override
    public Timestamp getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((TimestampWritable)o).getTimestamp();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new TimestampWritable((TimestampWritable)o);
    }
    
    @Override
    public Object set(final Object o, final byte[] bytes, final int offset) {
        ((TimestampWritable)o).set(bytes, offset);
        return o;
    }
    
    @Override
    public Object set(final Object o, final Timestamp t) {
        if (t == null) {
            return null;
        }
        ((TimestampWritable)o).set(t);
        return o;
    }
    
    @Override
    public Object set(final Object o, final TimestampWritable t) {
        if (t == null) {
            return null;
        }
        ((TimestampWritable)o).set(t);
        return o;
    }
    
    @Override
    public Object create(final byte[] bytes, final int offset) {
        return new TimestampWritable(bytes, offset);
    }
    
    @Override
    public Object create(final Timestamp t) {
        return new TimestampWritable(t);
    }
}
