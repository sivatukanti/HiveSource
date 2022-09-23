// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableFloatObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableFloatObjectInspector
{
    WritableFloatObjectInspector() {
        super(TypeInfoFactory.floatTypeInfo);
    }
    
    @Override
    public float get(final Object o) {
        return ((FloatWritable)o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new FloatWritable(((FloatWritable)o).get());
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Float.valueOf(((FloatWritable)o).get());
    }
    
    @Override
    public Object create(final float value) {
        return new FloatWritable(value);
    }
    
    @Override
    public Object set(final Object o, final float value) {
        ((FloatWritable)o).set(value);
        return o;
    }
}
