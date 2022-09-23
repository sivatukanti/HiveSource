// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyByte;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.ByteObjectInspector;
import org.apache.hadoop.hive.serde2.io.ByteWritable;

public class LazyByteObjectInspector extends AbstractPrimitiveLazyObjectInspector<ByteWritable> implements ByteObjectInspector
{
    LazyByteObjectInspector() {
        super(TypeInfoFactory.byteTypeInfo);
    }
    
    @Override
    public byte get(final Object o) {
        return this.getPrimitiveWritableObject(o).get();
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (o == null) ? null : new LazyByte((LazyByte)o);
    }
    
    @Override
    public Object getPrimitiveJavaObject(final Object o) {
        return (o == null) ? null : Byte.valueOf(this.get(o));
    }
}
