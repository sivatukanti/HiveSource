// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyPrimitive;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.AbstractPrimitiveObjectInspector;
import org.apache.hadoop.io.Writable;

public abstract class AbstractPrimitiveLazyObjectInspector<T extends Writable> extends AbstractPrimitiveObjectInspector
{
    protected AbstractPrimitiveLazyObjectInspector() {
    }
    
    protected AbstractPrimitiveLazyObjectInspector(final PrimitiveTypeInfo typeInfo) {
        super(typeInfo);
    }
    
    @Override
    public T getPrimitiveWritableObject(final Object o) {
        return (T)((o == null) ? null : ((LazyPrimitive)o).getWritableObject());
    }
    
    @Override
    public boolean preferWritable() {
        return true;
    }
}
