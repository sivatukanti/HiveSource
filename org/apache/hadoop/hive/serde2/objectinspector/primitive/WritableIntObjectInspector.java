// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableIntObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableIntObjectInspector
{
    WritableIntObjectInspector() {
        super(TypeInfoFactory.intTypeInfo);
    }
    
    @Override
    public int get(final Object o) {
        return ((IntWritable)o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new IntWritable(((IntWritable)o).get());
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Integer.valueOf(((IntWritable)o).get());
    }
    
    @Override
    public Object create(final int value) {
        return new IntWritable(value);
    }
    
    @Override
    public Object set(final Object o, final int value) {
        ((IntWritable)o).set(value);
        return o;
    }
}
