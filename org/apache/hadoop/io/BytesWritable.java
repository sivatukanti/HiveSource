// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class BytesWritable extends BinaryComparable implements WritableComparable<BinaryComparable>
{
    private static final int LENGTH_BYTES = 4;
    private static final byte[] EMPTY_BYTES;
    private int size;
    private byte[] bytes;
    
    public BytesWritable() {
        this(BytesWritable.EMPTY_BYTES);
    }
    
    public BytesWritable(final byte[] bytes) {
        this(bytes, bytes.length);
    }
    
    public BytesWritable(final byte[] bytes, final int length) {
        this.bytes = bytes;
        this.size = length;
    }
    
    public byte[] copyBytes() {
        final byte[] result = new byte[this.size];
        System.arraycopy(this.bytes, 0, result, 0, this.size);
        return result;
    }
    
    @Override
    public byte[] getBytes() {
        return this.bytes;
    }
    
    @Deprecated
    public byte[] get() {
        return this.getBytes();
    }
    
    @Override
    public int getLength() {
        return this.size;
    }
    
    @Deprecated
    public int getSize() {
        return this.getLength();
    }
    
    public void setSize(final int size) {
        if (size > this.getCapacity()) {
            final long newSize = Math.min(2147483647L, 3L * size / 2L);
            this.setCapacity((int)newSize);
        }
        this.size = size;
    }
    
    public int getCapacity() {
        return this.bytes.length;
    }
    
    public void setCapacity(final int new_cap) {
        if (new_cap != this.getCapacity()) {
            final byte[] new_data = new byte[new_cap];
            if (new_cap < this.size) {
                this.size = new_cap;
            }
            if (this.size != 0) {
                System.arraycopy(this.bytes, 0, new_data, 0, this.size);
            }
            this.bytes = new_data;
        }
    }
    
    public void set(final BytesWritable newData) {
        this.set(newData.bytes, 0, newData.size);
    }
    
    public void set(final byte[] newData, final int offset, final int length) {
        this.setSize(0);
        this.setSize(length);
        System.arraycopy(newData, offset, this.bytes, 0, this.size);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.setSize(0);
        this.setSize(in.readInt());
        in.readFully(this.bytes, 0, this.size);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeInt(this.size);
        out.write(this.bytes, 0, this.size);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(final Object right_obj) {
        return right_obj instanceof BytesWritable && super.equals(right_obj);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(3 * this.size);
        for (int idx = 0; idx < this.size; ++idx) {
            if (idx != 0) {
                sb.append(' ');
            }
            final String num = Integer.toHexString(0xFF & this.bytes[idx]);
            if (num.length() < 2) {
                sb.append('0');
            }
            sb.append(num);
        }
        return sb.toString();
    }
    
    static {
        EMPTY_BYTES = new byte[0];
        WritableComparator.define(BytesWritable.class, new Comparator());
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(BytesWritable.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            return WritableComparator.compareBytes(b1, s1 + 4, l1 - 4, b2, s2 + 4, l2 - 4);
        }
    }
}
