// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io.api;

import java.io.ObjectStreamException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import parquet.bytes.BytesUtils;
import java.io.UnsupportedEncodingException;
import parquet.io.ParquetEncodingException;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public abstract class Binary implements Comparable<Binary>, Serializable
{
    public static final Binary EMPTY;
    
    private Binary() {
    }
    
    public abstract String toStringUsingUTF8();
    
    public abstract int length();
    
    public abstract void writeTo(final OutputStream p0) throws IOException;
    
    public abstract void writeTo(final DataOutput p0) throws IOException;
    
    public abstract byte[] getBytes();
    
    abstract boolean equals(final byte[] p0, final int p1, final int p2);
    
    abstract boolean equals(final Binary p0);
    
    @Override
    public abstract int compareTo(final Binary p0);
    
    abstract int compareTo(final byte[] p0, final int p1, final int p2);
    
    public abstract ByteBuffer toByteBuffer();
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Binary && this.equals((Binary)obj);
    }
    
    @Override
    public String toString() {
        return "Binary{" + this.length() + " bytes, " + Arrays.toString(this.getBytes()) + "}";
    }
    
    public static Binary fromByteArray(final byte[] value, final int offset, final int length) {
        return new ByteArraySliceBackedBinary(value, offset, length);
    }
    
    public static Binary fromByteArray(final byte[] value) {
        return new ByteArrayBackedBinary(value);
    }
    
    public static Binary fromByteBuffer(final ByteBuffer value) {
        return new ByteBufferBackedBinary(value);
    }
    
    public static Binary fromString(final String value) {
        try {
            return new FromStringBinary(value.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new ParquetEncodingException("UTF-8 not supported.", e);
        }
    }
    
    private static final int hashCode(final byte[] array, final int offset, final int length) {
        int result = 1;
        for (int i = offset; i < offset + length; ++i) {
            final byte b = array[i];
            result = 31 * result + b;
        }
        return result;
    }
    
    private static final boolean equals(final byte[] array1, final int offset1, final int length1, final byte[] array2, final int offset2, final int length2) {
        if (array1 == null && array2 == null) {
            return true;
        }
        if (array1 == null || array2 == null) {
            return false;
        }
        if (length1 != length2) {
            return false;
        }
        if (array1 == array2 && offset1 == offset2) {
            return true;
        }
        for (int i = 0; i < length1; ++i) {
            if (array1[i + offset1] != array2[i + offset2]) {
                return false;
            }
        }
        return true;
    }
    
    private static final int compareTwoByteArrays(final byte[] array1, final int offset1, final int length1, final byte[] array2, final int offset2, final int length2) {
        if (array1 == null && array2 == null) {
            return 0;
        }
        if (array1 == array2 && offset1 == offset2 && length1 == length2) {
            return 0;
        }
        for (int min_length = (length1 < length2) ? length1 : length2, i = 0; i < min_length; ++i) {
            if (array1[i + offset1] < array2[i + offset2]) {
                return 1;
            }
            if (array1[i + offset1] > array2[i + offset2]) {
                return -1;
            }
        }
        if (length1 == length2) {
            return 0;
        }
        if (length1 < length2) {
            return 1;
        }
        return -1;
    }
    
    static {
        EMPTY = fromByteArray(new byte[0]);
    }
    
    private static class ByteArraySliceBackedBinary extends Binary
    {
        private final byte[] value;
        private final int offset;
        private final int length;
        
        public ByteArraySliceBackedBinary(final byte[] value, final int offset, final int length) {
            super(null);
            this.value = value;
            this.offset = offset;
            this.length = length;
        }
        
        @Override
        public String toStringUsingUTF8() {
            return BytesUtils.UTF8.decode(ByteBuffer.wrap(this.value, this.offset, this.length)).toString();
        }
        
        @Override
        public int length() {
            return this.length;
        }
        
        @Override
        public void writeTo(final OutputStream out) throws IOException {
            out.write(this.value, this.offset, this.length);
        }
        
        @Override
        public byte[] getBytes() {
            return Arrays.copyOfRange(this.value, this.offset, this.offset + this.length);
        }
        
        @Override
        public int hashCode() {
            return hashCode(this.value, this.offset, this.length);
        }
        
        @Override
        boolean equals(final Binary other) {
            return other.equals(this.value, this.offset, this.length);
        }
        
        @Override
        boolean equals(final byte[] other, final int otherOffset, final int otherLength) {
            return equals(this.value, this.offset, this.length, other, otherOffset, otherLength);
        }
        
        @Override
        public int compareTo(final Binary other) {
            return other.compareTo(this.value, this.offset, this.length);
        }
        
        @Override
        int compareTo(final byte[] other, final int otherOffset, final int otherLength) {
            return compareTwoByteArrays(this.value, this.offset, this.length, other, otherOffset, otherLength);
        }
        
        @Override
        public ByteBuffer toByteBuffer() {
            return ByteBuffer.wrap(this.value, this.offset, this.length);
        }
        
        @Override
        public void writeTo(final DataOutput out) throws IOException {
            out.write(this.value, this.offset, this.length);
        }
    }
    
    private static class FromStringBinary extends ByteArrayBackedBinary
    {
        public FromStringBinary(final byte[] value) {
            super(value);
        }
        
        @Override
        public String toString() {
            return "Binary{\"" + this.toStringUsingUTF8() + "\"}";
        }
    }
    
    private static class ByteArrayBackedBinary extends Binary
    {
        private final byte[] value;
        
        public ByteArrayBackedBinary(final byte[] value) {
            super(null);
            this.value = value;
        }
        
        @Override
        public String toStringUsingUTF8() {
            return BytesUtils.UTF8.decode(ByteBuffer.wrap(this.value)).toString();
        }
        
        @Override
        public int length() {
            return this.value.length;
        }
        
        @Override
        public void writeTo(final OutputStream out) throws IOException {
            out.write(this.value);
        }
        
        @Override
        public byte[] getBytes() {
            return this.value;
        }
        
        @Override
        public int hashCode() {
            return hashCode(this.value, 0, this.value.length);
        }
        
        @Override
        boolean equals(final Binary other) {
            return other.equals(this.value, 0, this.value.length);
        }
        
        @Override
        boolean equals(final byte[] other, final int otherOffset, final int otherLength) {
            return equals(this.value, 0, this.value.length, other, otherOffset, otherLength);
        }
        
        @Override
        public int compareTo(final Binary other) {
            return other.compareTo(this.value, 0, this.value.length);
        }
        
        @Override
        int compareTo(final byte[] other, final int otherOffset, final int otherLength) {
            return compareTwoByteArrays(this.value, 0, this.value.length, other, otherOffset, otherLength);
        }
        
        @Override
        public ByteBuffer toByteBuffer() {
            return ByteBuffer.wrap(this.value);
        }
        
        @Override
        public void writeTo(final DataOutput out) throws IOException {
            out.write(this.value);
        }
    }
    
    private static class ByteBufferBackedBinary extends Binary
    {
        private transient ByteBuffer value;
        
        public ByteBufferBackedBinary(final ByteBuffer value) {
            super(null);
            this.value = value;
        }
        
        @Override
        public String toStringUsingUTF8() {
            return BytesUtils.UTF8.decode(this.value).toString();
        }
        
        @Override
        public int length() {
            return this.value.remaining();
        }
        
        @Override
        public void writeTo(final OutputStream out) throws IOException {
            out.write(this.getBytes());
        }
        
        @Override
        public byte[] getBytes() {
            final byte[] bytes = new byte[this.value.remaining()];
            this.value.mark();
            this.value.get(bytes).reset();
            return bytes;
        }
        
        @Override
        public int hashCode() {
            if (this.value.hasArray()) {
                return hashCode(this.value.array(), this.value.arrayOffset() + this.value.position(), this.value.arrayOffset() + this.value.remaining());
            }
            final byte[] bytes = this.getBytes();
            return hashCode(bytes, 0, bytes.length);
        }
        
        @Override
        boolean equals(final Binary other) {
            if (this.value.hasArray()) {
                return other.equals(this.value.array(), this.value.arrayOffset() + this.value.position(), this.value.arrayOffset() + this.value.remaining());
            }
            final byte[] bytes = this.getBytes();
            return other.equals(bytes, 0, bytes.length);
        }
        
        @Override
        boolean equals(final byte[] other, final int otherOffset, final int otherLength) {
            if (this.value.hasArray()) {
                return equals(this.value.array(), this.value.arrayOffset() + this.value.position(), this.value.arrayOffset() + this.value.remaining(), other, otherOffset, otherLength);
            }
            final byte[] bytes = this.getBytes();
            return equals(bytes, 0, bytes.length, other, otherOffset, otherLength);
        }
        
        @Override
        public int compareTo(final Binary other) {
            if (this.value.hasArray()) {
                return other.compareTo(this.value.array(), this.value.arrayOffset() + this.value.position(), this.value.arrayOffset() + this.value.remaining());
            }
            final byte[] bytes = this.getBytes();
            return other.compareTo(bytes, 0, bytes.length);
        }
        
        @Override
        int compareTo(final byte[] other, final int otherOffset, final int otherLength) {
            if (this.value.hasArray()) {
                return compareTwoByteArrays(this.value.array(), this.value.arrayOffset() + this.value.position(), this.value.arrayOffset() + this.value.remaining(), other, otherOffset, otherLength);
            }
            final byte[] bytes = this.getBytes();
            return compareTwoByteArrays(bytes, 0, bytes.length, other, otherOffset, otherLength);
        }
        
        @Override
        public ByteBuffer toByteBuffer() {
            return this.value;
        }
        
        @Override
        public void writeTo(final DataOutput out) throws IOException {
            out.write(this.getBytes());
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            final byte[] bytes = this.getBytes();
            out.writeInt(bytes.length);
            out.write(bytes);
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            final int length = in.readInt();
            final byte[] bytes = new byte[length];
            in.readFully(bytes, 0, length);
            this.value = ByteBuffer.wrap(bytes);
        }
        
        private void readObjectNoData() throws ObjectStreamException {
            this.value = ByteBuffer.wrap(new byte[0]);
        }
    }
}
