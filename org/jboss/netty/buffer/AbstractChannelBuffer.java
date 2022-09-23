// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.charset.Charset;
import java.nio.channels.ScatteringByteChannel;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.ByteBuffer;

public abstract class AbstractChannelBuffer implements ChannelBuffer
{
    private int readerIndex;
    private int writerIndex;
    private int markedReaderIndex;
    private int markedWriterIndex;
    
    public int readerIndex() {
        return this.readerIndex;
    }
    
    public void readerIndex(final int readerIndex) {
        if (readerIndex < 0 || readerIndex > this.writerIndex) {
            throw new IndexOutOfBoundsException();
        }
        this.readerIndex = readerIndex;
    }
    
    public int writerIndex() {
        return this.writerIndex;
    }
    
    public void writerIndex(final int writerIndex) {
        if (writerIndex < this.readerIndex || writerIndex > this.capacity()) {
            throw new IndexOutOfBoundsException("Invalid readerIndex: " + this.readerIndex + " - Maximum is " + writerIndex);
        }
        this.writerIndex = writerIndex;
    }
    
    public void setIndex(final int readerIndex, final int writerIndex) {
        if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > this.capacity()) {
            throw new IndexOutOfBoundsException("Invalid writerIndex: " + writerIndex + " - Maximum is " + readerIndex + " or " + this.capacity());
        }
        this.readerIndex = readerIndex;
        this.writerIndex = writerIndex;
    }
    
    public void clear() {
        final int n = 0;
        this.writerIndex = n;
        this.readerIndex = n;
    }
    
    public boolean readable() {
        return this.readableBytes() > 0;
    }
    
    public boolean writable() {
        return this.writableBytes() > 0;
    }
    
    public int readableBytes() {
        return this.writerIndex - this.readerIndex;
    }
    
    public int writableBytes() {
        return this.capacity() - this.writerIndex;
    }
    
    public void markReaderIndex() {
        this.markedReaderIndex = this.readerIndex;
    }
    
    public void resetReaderIndex() {
        this.readerIndex(this.markedReaderIndex);
    }
    
    public void markWriterIndex() {
        this.markedWriterIndex = this.writerIndex;
    }
    
    public void resetWriterIndex() {
        this.writerIndex = this.markedWriterIndex;
    }
    
    public void discardReadBytes() {
        if (this.readerIndex == 0) {
            return;
        }
        this.setBytes(0, this, this.readerIndex, this.writerIndex - this.readerIndex);
        this.writerIndex -= this.readerIndex;
        this.markedReaderIndex = Math.max(this.markedReaderIndex - this.readerIndex, 0);
        this.markedWriterIndex = Math.max(this.markedWriterIndex - this.readerIndex, 0);
        this.readerIndex = 0;
    }
    
    public void ensureWritableBytes(final int writableBytes) {
        if (writableBytes > this.writableBytes()) {
            throw new IndexOutOfBoundsException("Writable bytes exceeded: Got " + writableBytes + ", maximum is " + this.writableBytes());
        }
    }
    
    public short getUnsignedByte(final int index) {
        return (short)(this.getByte(index) & 0xFF);
    }
    
    public int getUnsignedShort(final int index) {
        return this.getShort(index) & 0xFFFF;
    }
    
    public int getMedium(final int index) {
        int value = this.getUnsignedMedium(index);
        if ((value & 0x800000) != 0x0) {
            value |= 0xFF000000;
        }
        return value;
    }
    
    public long getUnsignedInt(final int index) {
        return (long)this.getInt(index) & 0xFFFFFFFFL;
    }
    
    public char getChar(final int index) {
        return (char)this.getShort(index);
    }
    
    public float getFloat(final int index) {
        return Float.intBitsToFloat(this.getInt(index));
    }
    
    public double getDouble(final int index) {
        return Double.longBitsToDouble(this.getLong(index));
    }
    
    public void getBytes(final int index, final byte[] dst) {
        this.getBytes(index, dst, 0, dst.length);
    }
    
    public void getBytes(final int index, final ChannelBuffer dst) {
        this.getBytes(index, dst, dst.writableBytes());
    }
    
    public void getBytes(final int index, final ChannelBuffer dst, final int length) {
        if (length > dst.writableBytes()) {
            throw new IndexOutOfBoundsException("Too many bytes to be read: Need " + length + ", maximum is " + dst.writableBytes());
        }
        this.getBytes(index, dst, dst.writerIndex(), length);
        dst.writerIndex(dst.writerIndex() + length);
    }
    
    public void setChar(final int index, final int value) {
        this.setShort(index, value);
    }
    
    public void setFloat(final int index, final float value) {
        this.setInt(index, Float.floatToRawIntBits(value));
    }
    
    public void setDouble(final int index, final double value) {
        this.setLong(index, Double.doubleToRawLongBits(value));
    }
    
    public void setBytes(final int index, final byte[] src) {
        this.setBytes(index, src, 0, src.length);
    }
    
    public void setBytes(final int index, final ChannelBuffer src) {
        this.setBytes(index, src, src.readableBytes());
    }
    
    public void setBytes(final int index, final ChannelBuffer src, final int length) {
        if (length > src.readableBytes()) {
            throw new IndexOutOfBoundsException("Too many bytes to write: Need " + length + ", maximum is " + src.readableBytes());
        }
        this.setBytes(index, src, src.readerIndex(), length);
        src.readerIndex(src.readerIndex() + length);
    }
    
    public void setZero(int index, final int length) {
        if (length == 0) {
            return;
        }
        if (length < 0) {
            throw new IllegalArgumentException("length must be 0 or greater than 0.");
        }
        final int nLong = length >>> 3;
        final int nBytes = length & 0x7;
        for (int i = nLong; i > 0; --i) {
            this.setLong(index, 0L);
            index += 8;
        }
        if (nBytes == 4) {
            this.setInt(index, 0);
        }
        else if (nBytes < 4) {
            for (int i = nBytes; i > 0; --i) {
                this.setByte(index, 0);
                ++index;
            }
        }
        else {
            this.setInt(index, 0);
            index += 4;
            for (int i = nBytes - 4; i > 0; --i) {
                this.setByte(index, 0);
                ++index;
            }
        }
    }
    
    public byte readByte() {
        if (this.readerIndex == this.writerIndex) {
            throw new IndexOutOfBoundsException("Readable byte limit exceeded: " + this.readerIndex);
        }
        return this.getByte(this.readerIndex++);
    }
    
    public short readUnsignedByte() {
        return (short)(this.readByte() & 0xFF);
    }
    
    public short readShort() {
        this.checkReadableBytes(2);
        final short v = this.getShort(this.readerIndex);
        this.readerIndex += 2;
        return v;
    }
    
    public int readUnsignedShort() {
        return this.readShort() & 0xFFFF;
    }
    
    public int readMedium() {
        int value = this.readUnsignedMedium();
        if ((value & 0x800000) != 0x0) {
            value |= 0xFF000000;
        }
        return value;
    }
    
    public int readUnsignedMedium() {
        this.checkReadableBytes(3);
        final int v = this.getUnsignedMedium(this.readerIndex);
        this.readerIndex += 3;
        return v;
    }
    
    public int readInt() {
        this.checkReadableBytes(4);
        final int v = this.getInt(this.readerIndex);
        this.readerIndex += 4;
        return v;
    }
    
    public long readUnsignedInt() {
        return (long)this.readInt() & 0xFFFFFFFFL;
    }
    
    public long readLong() {
        this.checkReadableBytes(8);
        final long v = this.getLong(this.readerIndex);
        this.readerIndex += 8;
        return v;
    }
    
    public char readChar() {
        return (char)this.readShort();
    }
    
    public float readFloat() {
        return Float.intBitsToFloat(this.readInt());
    }
    
    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }
    
    public ChannelBuffer readBytes(final int length) {
        this.checkReadableBytes(length);
        if (length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        final ChannelBuffer buf = this.factory().getBuffer(this.order(), length);
        buf.writeBytes(this, this.readerIndex, length);
        this.readerIndex += length;
        return buf;
    }
    
    public ChannelBuffer readSlice(final int length) {
        final ChannelBuffer slice = this.slice(this.readerIndex, length);
        this.readerIndex += length;
        return slice;
    }
    
    public void readBytes(final byte[] dst, final int dstIndex, final int length) {
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, dst, dstIndex, length);
        this.readerIndex += length;
    }
    
    public void readBytes(final byte[] dst) {
        this.readBytes(dst, 0, dst.length);
    }
    
    public void readBytes(final ChannelBuffer dst) {
        this.readBytes(dst, dst.writableBytes());
    }
    
    public void readBytes(final ChannelBuffer dst, final int length) {
        if (length > dst.writableBytes()) {
            throw new IndexOutOfBoundsException("Too many bytes to be read: Need " + length + ", maximum is " + dst.writableBytes());
        }
        this.readBytes(dst, dst.writerIndex(), length);
        dst.writerIndex(dst.writerIndex() + length);
    }
    
    public void readBytes(final ChannelBuffer dst, final int dstIndex, final int length) {
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, dst, dstIndex, length);
        this.readerIndex += length;
    }
    
    public void readBytes(final ByteBuffer dst) {
        final int length = dst.remaining();
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, dst);
        this.readerIndex += length;
    }
    
    public int readBytes(final GatheringByteChannel out, final int length) throws IOException {
        this.checkReadableBytes(length);
        final int readBytes = this.getBytes(this.readerIndex, out, length);
        this.readerIndex += readBytes;
        return readBytes;
    }
    
    public void readBytes(final OutputStream out, final int length) throws IOException {
        this.checkReadableBytes(length);
        this.getBytes(this.readerIndex, out, length);
        this.readerIndex += length;
    }
    
    public void skipBytes(final int length) {
        final int newReaderIndex = this.readerIndex + length;
        if (newReaderIndex > this.writerIndex) {
            throw new IndexOutOfBoundsException("Readable bytes exceeded - Need " + newReaderIndex + ", maximum is " + this.writerIndex);
        }
        this.readerIndex = newReaderIndex;
    }
    
    public void writeByte(final int value) {
        this.setByte(this.writerIndex, value);
        ++this.writerIndex;
    }
    
    public void writeShort(final int value) {
        this.setShort(this.writerIndex, value);
        this.writerIndex += 2;
    }
    
    public void writeMedium(final int value) {
        this.setMedium(this.writerIndex, value);
        this.writerIndex += 3;
    }
    
    public void writeInt(final int value) {
        this.setInt(this.writerIndex, value);
        this.writerIndex += 4;
    }
    
    public void writeLong(final long value) {
        this.setLong(this.writerIndex, value);
        this.writerIndex += 8;
    }
    
    public void writeChar(final int value) {
        this.writeShort(value);
    }
    
    public void writeFloat(final float value) {
        this.writeInt(Float.floatToRawIntBits(value));
    }
    
    public void writeDouble(final double value) {
        this.writeLong(Double.doubleToRawLongBits(value));
    }
    
    public void writeBytes(final byte[] src, final int srcIndex, final int length) {
        this.setBytes(this.writerIndex, src, srcIndex, length);
        this.writerIndex += length;
    }
    
    public void writeBytes(final byte[] src) {
        this.writeBytes(src, 0, src.length);
    }
    
    public void writeBytes(final ChannelBuffer src) {
        this.writeBytes(src, src.readableBytes());
    }
    
    public void writeBytes(final ChannelBuffer src, final int length) {
        if (length > src.readableBytes()) {
            throw new IndexOutOfBoundsException("Too many bytes to write - Need " + length + ", maximum is " + src.readableBytes());
        }
        this.writeBytes(src, src.readerIndex(), length);
        src.readerIndex(src.readerIndex() + length);
    }
    
    public void writeBytes(final ChannelBuffer src, final int srcIndex, final int length) {
        this.setBytes(this.writerIndex, src, srcIndex, length);
        this.writerIndex += length;
    }
    
    public void writeBytes(final ByteBuffer src) {
        final int length = src.remaining();
        this.setBytes(this.writerIndex, src);
        this.writerIndex += length;
    }
    
    public int writeBytes(final InputStream in, final int length) throws IOException {
        final int writtenBytes = this.setBytes(this.writerIndex, in, length);
        if (writtenBytes > 0) {
            this.writerIndex += writtenBytes;
        }
        return writtenBytes;
    }
    
    public int writeBytes(final ScatteringByteChannel in, final int length) throws IOException {
        final int writtenBytes = this.setBytes(this.writerIndex, in, length);
        if (writtenBytes > 0) {
            this.writerIndex += writtenBytes;
        }
        return writtenBytes;
    }
    
    public void writeZero(final int length) {
        if (length == 0) {
            return;
        }
        if (length < 0) {
            throw new IllegalArgumentException("length must be 0 or greater than 0.");
        }
        final int nLong = length >>> 3;
        final int nBytes = length & 0x7;
        for (int i = nLong; i > 0; --i) {
            this.writeLong(0L);
        }
        if (nBytes == 4) {
            this.writeInt(0);
        }
        else if (nBytes < 4) {
            for (int i = nBytes; i > 0; --i) {
                this.writeByte(0);
            }
        }
        else {
            this.writeInt(0);
            for (int i = nBytes - 4; i > 0; --i) {
                this.writeByte(0);
            }
        }
    }
    
    public ChannelBuffer copy() {
        return this.copy(this.readerIndex, this.readableBytes());
    }
    
    public ChannelBuffer slice() {
        return this.slice(this.readerIndex, this.readableBytes());
    }
    
    public ByteBuffer toByteBuffer() {
        return this.toByteBuffer(this.readerIndex, this.readableBytes());
    }
    
    public ByteBuffer[] toByteBuffers() {
        return this.toByteBuffers(this.readerIndex, this.readableBytes());
    }
    
    public ByteBuffer[] toByteBuffers(final int index, final int length) {
        return new ByteBuffer[] { this.toByteBuffer(index, length) };
    }
    
    public String toString(final Charset charset) {
        return this.toString(this.readerIndex, this.readableBytes(), charset);
    }
    
    public String toString(final int index, final int length, final Charset charset) {
        if (length == 0) {
            return "";
        }
        return ChannelBuffers.decodeString(this.toByteBuffer(index, length), charset);
    }
    
    public int indexOf(final int fromIndex, final int toIndex, final byte value) {
        return ChannelBuffers.indexOf(this, fromIndex, toIndex, value);
    }
    
    public int indexOf(final int fromIndex, final int toIndex, final ChannelBufferIndexFinder indexFinder) {
        return ChannelBuffers.indexOf(this, fromIndex, toIndex, indexFinder);
    }
    
    public int bytesBefore(final byte value) {
        return this.bytesBefore(this.readerIndex(), this.readableBytes(), value);
    }
    
    public int bytesBefore(final ChannelBufferIndexFinder indexFinder) {
        return this.bytesBefore(this.readerIndex(), this.readableBytes(), indexFinder);
    }
    
    public int bytesBefore(final int length, final byte value) {
        this.checkReadableBytes(length);
        return this.bytesBefore(this.readerIndex(), length, value);
    }
    
    public int bytesBefore(final int length, final ChannelBufferIndexFinder indexFinder) {
        this.checkReadableBytes(length);
        return this.bytesBefore(this.readerIndex(), length, indexFinder);
    }
    
    public int bytesBefore(final int index, final int length, final byte value) {
        if (index < 0 || length < 0 || index + length > this.capacity()) {
            throw new IndexOutOfBoundsException();
        }
        final int endIndex = this.indexOf(index, index + length, value);
        if (endIndex < 0) {
            return -1;
        }
        return endIndex - index;
    }
    
    public int bytesBefore(final int index, final int length, final ChannelBufferIndexFinder indexFinder) {
        if (index < 0 || length < 0 || index + length > this.capacity()) {
            throw new IndexOutOfBoundsException();
        }
        final int endIndex = this.indexOf(index, index + length, indexFinder);
        if (endIndex < 0) {
            return -1;
        }
        return endIndex - index;
    }
    
    @Override
    public int hashCode() {
        return ChannelBuffers.hashCode(this);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ChannelBuffer && ChannelBuffers.equals(this, (ChannelBuffer)o);
    }
    
    public int compareTo(final ChannelBuffer that) {
        return ChannelBuffers.compare(this, that);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + "ridx=" + this.readerIndex + ", " + "widx=" + this.writerIndex + ", " + "cap=" + this.capacity() + ')';
    }
    
    protected void checkReadableBytes(final int minimumReadableBytes) {
        if (this.readableBytes() < minimumReadableBytes) {
            throw new IndexOutOfBoundsException("Not enough readable bytes - Need " + minimumReadableBytes + ", maximum is " + this.readableBytes());
        }
    }
}
