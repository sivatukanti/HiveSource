// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableDoubleObjectInspector;

public class LazyBinaryDouble extends LazyBinaryPrimitive<WritableDoubleObjectInspector, DoubleWritable>
{
    LazyBinaryDouble(final WritableDoubleObjectInspector oi) {
        super(oi);
        this.data = (T)new DoubleWritable();
    }
    
    LazyBinaryDouble(final LazyBinaryDouble copy) {
        super(copy);
        this.data = (T)new DoubleWritable(((DoubleWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        assert 8 == length;
        ((DoubleWritable)this.data).set(Double.longBitsToDouble(LazyBinaryUtils.byteArrayToLong(bytes.getData(), start)));
    }
}
