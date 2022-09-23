// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;

public abstract class AbstractPrimitiveJavaObjectInspector extends AbstractPrimitiveObjectInspector
{
    protected AbstractPrimitiveJavaObjectInspector() {
    }
    
    protected AbstractPrimitiveJavaObjectInspector(final PrimitiveTypeInfo typeInfo) {
        super(typeInfo);
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return o;
    }
    
    @Override
    public Object copyObject(final Object o) {
        return o;
    }
    
    @Override
    public boolean preferWritable() {
        return false;
    }
}
