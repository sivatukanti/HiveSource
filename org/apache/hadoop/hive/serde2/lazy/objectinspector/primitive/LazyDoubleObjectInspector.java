// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyDouble;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DoubleObjectInspector;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;

public class LazyDoubleObjectInspector extends AbstractPrimitiveLazyObjectInspector<DoubleWritable> implements DoubleObjectInspector
{
    LazyDoubleObjectInspector() {
        super(TypeInfoFactory.doubleTypeInfo);
    }
    
    @Override
    public double get(final Object o) {
        return this.getPrimitiveWritableObject(o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyDouble((LazyDouble)o);
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Double.valueOf(this.get(o));
    }
}
