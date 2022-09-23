// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class ByteArray implements RawComparable
{
    private final byte[] buffer;
    private final int offset;
    private final int len;
    
    public ByteArray(final BytesWritable other) {
        this(other.getBytes(), 0, other.getLength());
    }
    
    public ByteArray(final byte[] buffer) {
        this(buffer, 0, buffer.length);
    }
    
    public ByteArray(final byte[] buffer, final int offset, final int len) {
        if ((offset | len | buffer.length - offset - len) < 0) {
            throw new IndexOutOfBoundsException();
        }
        this.buffer = buffer;
        this.offset = offset;
        this.len = len;
    }
    
    @Override
    public byte[] buffer() {
        return this.buffer;
    }
    
    @Override
    public int offset() {
        return this.offset;
    }
    
    @Override
    public int size() {
        return this.len;
    }
}
