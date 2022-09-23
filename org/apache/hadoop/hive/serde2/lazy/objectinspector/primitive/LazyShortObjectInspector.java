// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyShort;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.ShortObjectInspector;
import org.apache.hadoop.hive.serde2.io.ShortWritable;

public class LazyShortObjectInspector extends AbstractPrimitiveLazyObjectInspector<ShortWritable> implements ShortObjectInspector
{
    LazyShortObjectInspector() {
        super(TypeInfoFactory.shortTypeInfo);
    }
    
    @Override
    public short get(final Object o) {
        return this.getPrimitiveWritableObject(o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyShort((LazyShort)o);
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Short.valueOf(this.get(o));
    }
}
