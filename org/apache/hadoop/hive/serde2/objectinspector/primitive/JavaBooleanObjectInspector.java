// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaBooleanObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableBooleanObjectInspector
{
    JavaBooleanObjectInspector() {
        super(TypeInfoFactory.booleanTypeInfo);
    }
    
    @Override
    public Object getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new BooleanWritable((boolean)o);
    }
    
    @Override
    public boolean get(final Object o) {
        return (boolean)o;
    }
    
    @Override
    public Object create(final boolean value) {
        return value ? Boolean.TRUE : Boolean.FALSE;
    }
    
    @Override
    public Object set(final Object o, final boolean value) {
        return value ? Boolean.TRUE : Boolean.FALSE;
    }
}
