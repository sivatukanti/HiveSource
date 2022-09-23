// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableBinaryObjectInspector;

public class LazyBinaryBinary extends LazyBinaryPrimitive<WritableBinaryObjectInspector, BytesWritable>
{
    LazyBinaryBinary(final LazyBinaryPrimitive<WritableBinaryObjectInspector, BytesWritable> copy) {
        super(copy);
        final BytesWritable incoming = copy.getWritableObject();
        final byte[] outgoing = new byte[incoming.getLength()];
        System.arraycopy(incoming.getBytes(), 0, outgoing, 0, incoming.getLength());
        this.data = (T)new BytesWritable(outgoing);
    }
    
    public LazyBinaryBinary(final WritableBinaryObjectInspector baoi) {
        super(baoi);
        this.data = (T)new BytesWritable();
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        assert length > -1;
        ((BytesWritable)this.data).set(bytes.getData(), start, length);
    }
}
