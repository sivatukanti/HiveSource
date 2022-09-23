// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

public abstract class LazyNonPrimitive<OI extends ObjectInspector> extends LazyObject<OI>
{
    protected ByteArrayRef bytes;
    protected int start;
    protected int length;
    
    protected LazyNonPrimitive(final OI oi) {
        super(oi);
        this.bytes = null;
        this.start = 0;
        this.length = 0;
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        super.init(bytes, start, length);
        this.bytes = bytes;
        this.start = start;
        this.length = length;
        assert start >= 0;
        assert start + length <= bytes.getData().length;
    }
    
    protected final boolean isNull(final Text nullSequence, final ByteArrayRef ref, final int fieldByteBegin, final int fieldLength) {
        return ref == null || this.isNull(nullSequence, ref.getData(), fieldByteBegin, fieldLength);
    }
    
    protected final boolean isNull(final Text nullSequence, final byte[] bytes, final int fieldByteBegin, final int fieldLength) {
        return fieldLength < 0 || (fieldLength == nullSequence.getLength() && LazyUtils.compare(bytes, fieldByteBegin, fieldLength, nullSequence.getBytes(), 0, nullSequence.getLength()) == 0);
    }
    
    @Override
    public int hashCode() {
        return LazyUtils.hashBytes(this.bytes.getData(), this.start, this.length);
    }
}
