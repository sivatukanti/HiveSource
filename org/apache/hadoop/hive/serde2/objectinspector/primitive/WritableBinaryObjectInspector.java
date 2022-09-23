// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector.primitive;

import java.util.Arrays;
import org.apache.hadoop.hive.serde2.lazy.LazyUtils;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

public class WritableBinaryObjectInspector extends AbstractPrimitiveWritableObjectInspector implements SettableBinaryObjectInspector
{
    WritableBinaryObjectInspector() {
        super(TypeInfoFactory.binaryTypeInfo);
    }
    
    @Override
    public BytesWritable copyObject(final Object o) {
        if (null == o) {
            return null;
        }
        final BytesWritable incoming = (BytesWritable)o;
        final byte[] bytes = new byte[incoming.getLength()];
        System.arraycopy(incoming.getBytes(), 0, bytes, 0, incoming.getLength());
        return new BytesWritable(bytes);
    }
    
    @Override
    public byte[] getPrimitiveJavaObject(final Object o) {
        return (byte[])((o == null) ? null : LazyUtils.createByteArray((BytesWritable)o));
    }
    
    @Override
    public BytesWritable getPrimitiveWritableObject(final Object o) {
        return (null == o) ? null : ((BytesWritable)o);
    }
    
    @Override
    public BytesWritable set(final Object o, final byte[] bb) {
        final BytesWritable incoming = (BytesWritable)o;
        if (bb != null) {
            incoming.set(bb, 0, bb.length);
        }
        return incoming;
    }
    
    @Override
    public BytesWritable set(final Object o, final BytesWritable bw) {
        final BytesWritable incoming = (BytesWritable)o;
        if (bw != null) {
            incoming.set(bw);
        }
        return incoming;
    }
    
    @Override
    public BytesWritable create(final byte[] bb) {
        return new BytesWritable(Arrays.copyOf(bb, bb.length));
    }
    
    @Override
    public BytesWritable create(final BytesWritable bw) {
        final BytesWritable newCpy = new BytesWritable();
        if (null != bw) {
            newCpy.set(bw);
        }
        return newCpy;
    }
}
