// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.sql.Timestamp;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaTimestampObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableTimestampObjectInspector
{
    protected JavaTimestampObjectInspector() {
        super(TypeInfoFactory.timestampTypeInfo);
    }
    
    @Override
    public TimestampWritable getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new TimestampWritable((Timestamp)o);
    }
    
    @Override
    public Timestamp getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : ((Timestamp)o);
    }
    
    @Override
    public Object copyObject(final Object o) {
        if (o == null) {
            return null;
        }
        final Timestamp source = (Timestamp)o;
        final Timestamp copy = new Timestamp(source.getTime());
        copy.setNanos(source.getNanos());
        return copy;
    }
    
    public Timestamp get(final Object o) {
        return (Timestamp)o;
    }
    
    @Override
    public Object set(final Object o, final Timestamp value) {
        if (value == null) {
            return null;
        }
        ((Timestamp)o).setTime(value.getTime());
        return o;
    }
    
    @Override
    public Object set(final Object o, final byte[] bytes, final int offset) {
        TimestampWritable.setTimestamp((Timestamp)o, bytes, offset);
        return o;
    }
    
    @Override
    public Object set(final Object o, final TimestampWritable tw) {
        if (tw == null) {
            return null;
        }
        final Timestamp t = (Timestamp)o;
        t.setTime(tw.getTimestamp().getTime());
        t.setNanos(tw.getTimestamp().getNanos());
        return t;
    }
    
    @Override
    public Object create(final Timestamp value) {
        return new Timestamp(value.getTime());
    }
    
    @Override
    public Object create(final byte[] bytes, final int offset) {
        return TimestampWritable.createTimestamp(bytes, offset);
    }
}
