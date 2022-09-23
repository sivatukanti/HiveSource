// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableByteObjectInspector;

public class LazyBinaryByte extends LazyBinaryPrimitive<WritableByteObjectInspector, ByteWritable>
{
    LazyBinaryByte(final WritableByteObjectInspector oi) {
        super(oi);
        this.data = (T)new ByteWritable();
    }
    
    LazyBinaryByte(final LazyBinaryByte copy) {
        super(copy);
        this.data = (T)new ByteWritable(((ByteWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        assert 1 == length;
        ((ByteWritable)this.data).set(bytes.getData()[start]);
    }
}
