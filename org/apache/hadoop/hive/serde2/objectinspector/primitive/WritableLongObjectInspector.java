// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableLongObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableLongObjectInspector
{
    WritableLongObjectInspector() {
        super(TypeInfoFactory.longTypeInfo);
    }
    
    @Override
    public long get(final Object o) {
        return ((LongWritable)o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LongWritable(((LongWritable)o).get());
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Long.valueOf(((LongWritable)o).get());
    }
    
    @Override
    public Object create(final long value) {
        return new LongWritable(value);
    }
    
    @Override
    public Object set(final Object o, final long value) {
        ((LongWritable)o).set(value);
        return o;
    }
}
