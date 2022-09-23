// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaDoubleObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableDoubleObjectInspector
{
    JavaDoubleObjectInspector() {
        super(TypeInfoFactory.doubleTypeInfo);
    }
    
    @Override
    public Object getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new DoubleWritable((double)o);
    }
    
    @Override
    public double get(final Object o) {
        return (double)o;
    }
    
    @Override
    public Object create(final double value) {
        return value;
    }
    
    @Override
    public Object set(final Object o, final double value) {
        return value;
    }
}
