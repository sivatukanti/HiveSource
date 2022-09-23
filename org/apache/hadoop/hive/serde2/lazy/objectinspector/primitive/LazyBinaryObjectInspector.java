// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyPrimitive;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.lazy.LazyUtils;
import org.apache.hadoop.hive.serde2.lazy.LazyBinary;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.io.BytesWritable;

public class LazyBinaryObjectInspector extends AbstractPrimitiveLazyObjectInspector<BytesWritable> implements BinaryObjectInspector
{
    public LazyBinaryObjectInspector() {
        super(TypeInfoFactory.binaryTypeInfo);
    }
    
    @Override
    public Object copyObject(final Object o) {
        return (null == o) ? null : new LazyBinary((LazyBinary)o);
    }
    
    @Override
    public byte[] getPrimitiveJavaObject(final Object o) {
        if (null == o) {
            return null;
        }
        return LazyUtils.createByteArray(((LazyPrimitive<OI, BytesWritable>)o).getWritableObject());
    }
    
    @Override
    public BytesWritable getPrimitiveWritableObject(final Object o) {
        return (null == o) ? null : ((LazyPrimitive<OI, BytesWritable>)o).getWritableObject();
    }
}
