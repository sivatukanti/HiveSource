// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyFloat;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.FloatObjectInspector;
import org.apache.hadoop.io.FloatWritable;

public class LazyFloatObjectInspector extends AbstractPrimitiveLazyObjectInspector<FloatWritable> implements FloatObjectInspector
{
    LazyFloatObjectInspector() {
        super(TypeInfoFactory.floatTypeInfo);
    }
    
    @Override
    public float get(final Object o) {
        return this.getPrimitiveWritableObject(o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyFloat((LazyFloat)o);
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Float.valueOf(this.get(o));
    }
}
