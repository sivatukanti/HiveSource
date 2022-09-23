// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;

public abstract class AbstractPrimitiveWritableObjectInspector extends AbstractPrimitiveObjectInspector
{
    protected AbstractPrimitiveWritableObjectInspector() {
    }
    
    protected AbstractPrimitiveWritableObjectInspector(final PrimitiveTypeInfo typeInfo) {
        super(typeInfo);
    }
    
    @Override
    public Object getPrimitiveWritableObject(final Object o) {
        return o;
    }
    
    @Override
    public boolean preferWritable() {
        return true;
    }
}
