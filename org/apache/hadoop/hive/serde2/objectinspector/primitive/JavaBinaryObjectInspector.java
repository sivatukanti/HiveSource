// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import org.apache.hadoop.hive.serde2.lazy.LazyUtils;
import java.util.Arrays;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class JavaBinaryObjectInspector extends AbstractPrimitiveJavaObjectInspector implements SettableBinaryObjectInspector
{
    JavaBinaryObjectInspector() {
        super(TypeInfoFactory.binaryTypeInfo);
    }
    
    @Override
    public BytesWritable getPrimitiveWritableObject(final Object o) {
        return (o == null) ? null : new BytesWritable((byte[])o);
    }
    
    @Override
    public byte[] getPrimitiveJavaObject(final Object o) {
        return (byte[])o;
    }
    
    @Override
    public byte[] set(final Object o, final byte[] bb) {
        return (byte[])((bb == null) ? null : Arrays.copyOf(bb, bb.length));
    }
    
    @Override
    public byte[] set(final Object o, final BytesWritable bw) {
        return (byte[])((bw == null) ? null : LazyUtils.createByteArray(bw));
    }
    
    @Override
    public byte[] create(final byte[] bb) {
        return (byte[])((bb == null) ? null : Arrays.copyOf(bb, bb.length));
    }
    
    @Override
    public byte[] create(final BytesWritable bw) {
        return (byte[])((bw == null) ? null : LazyUtils.createByteArray(bw));
    }
}
