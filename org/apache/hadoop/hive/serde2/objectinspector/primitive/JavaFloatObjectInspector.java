// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaFloatObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableFloatObjectInspector
{
    JavaFloatObjectInspector() {
        super(TypeInfoFactory.floatTypeInfo);
    }
    
    @Override
    public Object getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new FloatWritable((float)o);
    }
    
    @Override
    public float get(final Object o) {
        return (float)o;
    }
    
    @Override
    public Object create(final float value) {
        return value;
    }
    
    @Override
    public Object set(final Object o, final float value) {
        return value;
    }
}
