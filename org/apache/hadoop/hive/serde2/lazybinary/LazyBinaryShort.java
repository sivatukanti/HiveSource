// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableShortObjectInspector;

public class LazyBinaryShort extends LazyBinaryPrimitive<WritableShortObjectInspector, ShortWritable>
{
    LazyBinaryShort(final WritableShortObjectInspector oi) {
        super(oi);
        this.data = (T)new ShortWritable();
    }
    
    LazyBinaryShort(final LazyBinaryShort copy) {
        super(copy);
        this.data = (T)new ShortWritable(((ShortWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        assert 2 == length;
        ((ShortWritable)this.data).set(LazyBinaryUtils.byteArrayToShort(bytes.getData(), start));
    }
}
