// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableBooleanObjectInspector;

public class LazyBinaryBoolean extends LazyBinaryPrimitive<WritableBooleanObjectInspector, BooleanWritable>
{
    public LazyBinaryBoolean(final WritableBooleanObjectInspector oi) {
        super(oi);
        this.data = (T)new BooleanWritable();
    }
    
    public LazyBinaryBoolean(final LazyBinaryBoolean copy) {
        super(copy);
        this.data = (T)new BooleanWritable(((BooleanWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        assert 1 == length;
        final byte val = bytes.getData()[start];
        if (val == 0) {
            ((BooleanWritable)this.data).set(false);
        }
        else if (val == 1) {
            ((BooleanWritable)this.data).set(true);
        }
    }
}
