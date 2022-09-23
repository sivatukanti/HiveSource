// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableIntObjectInspector;

public class LazyBinaryInteger extends LazyBinaryPrimitive<WritableIntObjectInspector, IntWritable>
{
    LazyBinaryUtils.VInt vInt;
    
    LazyBinaryInteger(final WritableIntObjectInspector oi) {
        super(oi);
        this.vInt = new LazyBinaryUtils.VInt();
        this.data = (T)new IntWritable();
    }
    
    LazyBinaryInteger(final LazyBinaryInteger copy) {
        super(copy);
        this.vInt = new LazyBinaryUtils.VInt();
        this.data = (T)new IntWritable(((IntWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        LazyBinaryUtils.readVInt(bytes.getData(), start, this.vInt);
        assert length == this.vInt.length;
        ((IntWritable)this.data).set(this.vInt.value);
    }
}
