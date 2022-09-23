// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;

public class WritableVoidObjectInspector extends AbstractPrimitiveWritableObjectInspector implements VoidObjectInspector, ConstantObjectInspector
{
    WritableVoidObjectInspector() {
        super(TypeInfoFactory.voidTypeInfo);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return o;
    }
    
    @Override
    public Object getWritableConstantValue() {
        return null;
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return null;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return null != obj && obj instanceof WritableVoidObjectInspector;
    }
}
