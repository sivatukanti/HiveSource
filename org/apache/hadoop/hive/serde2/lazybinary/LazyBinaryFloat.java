// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableFloatObjectInspector;

public class LazyBinaryFloat extends LazyBinaryPrimitive<WritableFloatObjectInspector, FloatWritable>
{
    LazyBinaryFloat(final WritableFloatObjectInspector oi) {
        super(oi);
        this.data = (T)new FloatWritable();
    }
    
    LazyBinaryFloat(final LazyBinaryFloat copy) {
        super(copy);
        this.data = (T)new FloatWritable(((FloatWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        assert 4 == length;
        ((FloatWritable)this.data).set(Float.intBitsToFloat(LazyBinaryUtils.byteArrayToInt(bytes.getData(), start)));
    }
}
