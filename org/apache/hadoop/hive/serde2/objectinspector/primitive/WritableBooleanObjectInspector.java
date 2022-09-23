// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableBooleanObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableBooleanObjectInspector
{
    WritableBooleanObjectInspector() {
        super(TypeInfoFactory.booleanTypeInfo);
    }
    
    @Override
    public boolean get(final Object o) {
        return ((BooleanWritable)o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new BooleanWritable(((BooleanWritable)o).get());
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Boolean.valueOf(((BooleanWritable)o).get());
    }
    
    @Override
    public Object create(final boolean value) {
        return new BooleanWritable(value);
    }
    
    @Override
    public Object set(final Object o, final boolean value) {
        ((BooleanWritable)o).set(value);
        return o;
    }
}
