// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableDoubleObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableDoubleObjectInspector
{
    WritableDoubleObjectInspector() {
        super(TypeInfoFactory.doubleTypeInfo);
    }
    
    @Override
    public double get(final Object o) {
        return ((DoubleWritable)o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new org.apache.hadoop.hive.serde2.io.DoubleWritable(this.get(o));
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Double.valueOf(this.get(o));
    }
    
    @Override
    public Object create(final double value) {
        return new org.apache.hadoop.hive.serde2.io.DoubleWritable(value);
    }
    
    @Override
    public Object set(final Object o, final double value) {
        ((DoubleWritable)o).set(value);
        return o;
    }
}
