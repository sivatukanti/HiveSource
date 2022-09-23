// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.columnar;

import org.apache.hadoop.io.WritableFactories;
import org.apache.hadoop.io.WritableFactory;
import org.apache.hadoop.io.WritableComparator;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;

public class BytesRefWritable implements Writable, Comparable<BytesRefWritable>
{
    private static final byte[] EMPTY_BYTES;
    public static BytesRefWritable ZeroBytesRefWritable;
    int start;
    int length;
    byte[] bytes;
    LazyDecompressionCallback lazyDecompressObj;
    
    public BytesRefWritable() {
        this(BytesRefWritable.EMPTY_BYTES);
    }
    
    public BytesRefWritable(final int length) {
        this.start = 0;
        this.length = 0;
        this.bytes = null;
        assert length > 0;
        this.length = length;
        this.bytes = new byte[this.length];
        this.start = 0;
    }
    
    public BytesRefWritable(final byte[] bytes) {
        this.start = 0;
        this.length = 0;
        this.bytes = null;
        this.bytes = bytes;
        this.length = bytes.length;
        this.start = 0;
    }
    
    public BytesRefWritable(final byte[] data, final int offset, final int len) {
        this.start = 0;
        this.length = 0;
        this.bytes = null;
        this.bytes = data;
        this.start = offset;
        this.length = len;
    }
    
    public BytesRefWritable(final LazyDecompressionCallback lazyDecompressData, final int offset, final int len) {
        this.start = 0;
        this.length = 0;
        this.bytes = null;
        this.lazyDecompressObj = lazyDecompressData;
        this.start = offset;
        this.length = len;
    }
    
    private void lazyDecompress() throws IOException {
        if (this.bytes == null && this.lazyDecompressObj != null) {
            this.bytes = this.lazyDecompressObj.decompress();
        }
    }
    
    public byte[] getBytesCopy() throws IOException {
        this.lazyDecompress();
        final byte[] bb = new byte[this.length];
        System.arraycopy(this.bytes, this.start, bb, 0, this.length);
        return bb;
    }
    
    public byte[] getData() throws IOException {
        this.lazyDecompress();
        return this.bytes;
    }
    
    public void set(final byte[] newData, final int offset, final int len) {
        this.bytes = newData;
        this.start = offset;
        this.length = len;
        this.lazyDecompressObj = null;
    }
    
    public void set(final LazyDecompressionCallback newData, final int offset, final int len) {
        this.bytes = null;
        this.start = offset;
        this.length = len;
        this.lazyDecompressObj = newData;
    }
    
    public void writeDataTo(final DataOutput out) throws IOException {
        this.lazyDecompress();
        out.write(this.bytes, this.start, this.length);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        final int len = in.readInt();
        if (len > this.bytes.length) {
            this.bytes = new byte[len];
        }
        this.start = 0;
        this.length = len;
        in.readFully(this.bytes, this.start, this.length);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        this.lazyDecompress();
        out.writeInt(this.length);
        out.write(this.bytes, this.start, this.length);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(3 * this.length);
        for (int idx = this.start; idx < this.length; ++idx) {
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
    
    @Override
    public int compareTo(final BytesRefWritable other) {
        if (other == null) {
            throw new IllegalArgumentException("Argument can not be null.");
        }
        if (this == other) {
            return 0;
        }
        try {
            return WritableComparator.compareBytes(this.getData(), this.start, this.getLength(), other.getData(), other.start, other.getLength());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public boolean equals(final Object right_obj) {
        return right_obj != null && right_obj instanceof BytesRefWritable && this.compareTo((BytesRefWritable)right_obj) == 0;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getStart() {
        return this.start;
    }
    
    static {
        EMPTY_BYTES = new byte[0];
        BytesRefWritable.ZeroBytesRefWritable = new BytesRefWritable();
        WritableFactories.setFactory(BytesRefWritable.class, new WritableFactory() {
            @Override
            public Writable newInstance() {
                return new BytesRefWritable();
            }
        });
    }
}
