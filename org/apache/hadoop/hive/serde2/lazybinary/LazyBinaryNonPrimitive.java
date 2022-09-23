// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.LazyUtils;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

public abstract class LazyBinaryNonPrimitive<OI extends ObjectInspector> extends LazyBinaryObject<OI>
{
    protected ByteArrayRef bytes;
    protected int start;
    protected int length;
    
    protected LazyBinaryNonPrimitive(final OI oi) {
        super(oi);
        this.bytes = null;
        this.start = 0;
        this.length = 0;
    }
    
    @Override
    public Object getObject() {
        return this;
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        if (null == bytes) {
            throw new RuntimeException("bytes cannot be null!");
        }
        if (length <= 0) {
            throw new RuntimeException("length should be positive!");
        }
        this.bytes = bytes;
        this.start = start;
        this.length = length;
    }
    
    @Override
    public int hashCode() {
        return LazyUtils.hashBytes(this.bytes.getData(), this.start, this.length);
    }
}
