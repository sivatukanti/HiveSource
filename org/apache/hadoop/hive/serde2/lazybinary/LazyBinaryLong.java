// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableLongObjectInspector;

public class LazyBinaryLong extends LazyBinaryPrimitive<WritableLongObjectInspector, LongWritable>
{
    LazyBinaryUtils.VLong vLong;
    
    LazyBinaryLong(final WritableLongObjectInspector oi) {
        super(oi);
        this.vLong = new LazyBinaryUtils.VLong();
        this.data = (T)new LongWritable();
    }
    
    LazyBinaryLong(final LazyBinaryLong copy) {
        super(copy);
        this.vLong = new LazyBinaryUtils.VLong();
        this.data = (T)new LongWritable(((LongWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        LazyBinaryUtils.readVLong(bytes.getData(), start, this.vLong);
        assert length == this.vLong.length;
        ((LongWritable)this.data).set(this.vLong.value);
    }
}
