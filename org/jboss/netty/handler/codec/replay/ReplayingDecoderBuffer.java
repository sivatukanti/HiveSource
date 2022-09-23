// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.replay;

import java.nio.charset.Charset;
import java.nio.channels.ScatteringByteChannel;
import java.io.InputStream;
import java.nio.ByteOrder;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.ByteBuffer;
import org.jboss.netty.buffer.ChannelBuffer;

class ReplayingDecoderBuffer implements ChannelBuffer
{
    private static final Error REPLAY;
    private final ReplayingDecoder<?> parent;
    private boolean terminated;
    
    ReplayingDecoderBuffer(final ReplayingDecoder<?> parent) {
        this.parent = parent;
    }
    
    private ChannelBuffer buf() {
        return this.parent.internalBuffer();
    }
    
    void terminate() {
        this.terminated = true;
    }
    
    public int capacity() {
        if (this.terminated) {
            return this.buf().capacity();
        }
        return Integer.MAX_VALUE;
    }
    
    public boolean isDirect() {
        return this.buf().isDirect();
    }
    
    public boolean hasArray() {
        return false;
    }
    
    public byte[] array() {
        throw new UnsupportedOperationException();
    }
    
    public int arrayOffset() {
        throw new UnsupportedOperationException();
    }
    
    public void clear() {
        throw new UnreplayableOperationException();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }
    
    public int compareTo(final ChannelBuffer buffer) {
        throw new UnreplayableOperationException();
    }
    
    public ChannelBuffer copy() {
        throw new UnreplayableOperationException();
    }
    
    public ChannelBuffer copy(final int index, final int length) {
        this.checkIndex(index, length);
        return this.buf().copy(index, length);
    }
    
    public void discardReadBytes() {
        throw new UnreplayableOperationException();
    }
    
    public void ensureWritableBytes(final int writableBytes) {
        throw new UnreplayableOperationException();
    }
    
    public ChannelBuffer duplicate() {
        throw new UnreplayableOperationException();
    }
    
    public byte getByte(final int index) {
        this.checkIndex(index, 1);
        return this.buf().getByte(index);
    }
    
    public short getUnsignedByte(final int index) {
        this.checkIndex(index, 1);
        return this.buf().getUnsignedByte(index);
    }
    
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        this.checkIndex(index, length);
        this.buf().getBytes(index, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final byte[] dst) {
        this.checkIndex(index, dst.length);
        this.buf().getBytes(index, dst);
    }
    
    public void getBytes(final int index, final ByteBuffer dst) {
        throw new UnreplayableOperationException();
    }
    
    public void getBytes(final int index, final ChannelBuffer dst, final int dstIndex, final int length) {
        this.checkIndex(index, length);
        this.buf().getBytes(index, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final ChannelBuffer dst, final int length) {
        throw new UnreplayableOperationException();
    }
    
    public void getBytes(final int index, final ChannelBuffer dst) {
        throw new UnreplayableOperationException();
    }
    
    public int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        throw new UnreplayableOperationException();
    }
    
    public void getBytes(final int index, final OutputStream out, final int length) throws IOException {
        throw new UnreplayableOperationException();
    }
    
    public int getInt(final int index) {
        this.checkIndex(index, 4);
        return this.buf().getInt(index);
    }
    
    public long getUnsignedInt(final int index) {
        this.checkIndex(index, 4);
        return this.buf().getUnsignedInt(index);
    }
    
    public long getLong(final int index) {
        this.checkIndex(index, 8);
        return this.buf().getLong(index);
    }
    
    public int getMedium(final int index) {
        this.checkIndex(index, 3);
        return this.buf().getMedium(index);
    }
    
    public int getUnsignedMedium(final int index) {
        this.checkIndex(index, 3);
        return this.buf().getUnsignedMedium(index);
    }
    
    public short getShort(final int index) {
        this.checkIndex(index, 2);
        return this.buf().getShort(index);
    }
    
    public int getUnsignedShort(final int index) {
        this.checkIndex(index, 2);
        return this.buf().getUnsignedShort(index);
    }
    
    public char getChar(final int index) {
        this.checkIndex(index, 2);
        return this.buf().getChar(index);
    }
    
    public float getFloat(final int index) {
        this.checkIndex(index, 4);
        return this.buf().getFloat(index);
    }
    
    public double getDouble(final int index) {
        this.checkIndex(index, 8);
        return this.buf().getDouble(index);
    }
    
    @Override
    public int hashCode() {
        throw new UnreplayableOperationException();
    }
    
    public int indexOf(final int fromIndex, final int toIndex, final byte value) {
        final int endIndex = this.buf().indexOf(fromIndex, toIndex, value);
        if (endIndex < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
        return endIndex;
    }
    
    public int indexOf(final int fromIndex, final int toIndex, final ChannelBufferIndexFinder indexFinder) {
        final int endIndex = this.buf().indexOf(fromIndex, toIndex, indexFinder);
        if (endIndex < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
        return endIndex;
    }
    
    public int bytesBefore(final byte value) {
        final int bytes = this.buf().bytesBefore(value);
        if (bytes < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
        return bytes;
    }
    
    public int bytesBefore(final ChannelBufferIndexFinder indexFinder) {
        final int bytes = this.buf().bytesBefore(indexFinder);
        if (bytes < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
        return bytes;
    }
    
    public int bytesBefore(final int length, final byte value) {
        this.checkReadableBytes(length);
        final int bytes = this.buf().bytesBefore(length, value);
        if (bytes < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
        return bytes;
    }
    
    public int bytesBefore(final int length, final ChannelBufferIndexFinder indexFinder) {
        this.checkReadableBytes(length);
        final int bytes = this.buf().bytesBefore(length, indexFinder);
        if (bytes < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
        return bytes;
    }
    
    public int bytesBefore(final int index, final int length, final byte value) {
        final int bytes = this.buf().bytesBefore(index, length, value);
        if (bytes < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
        return bytes;
    }
    
    public int bytesBefore(final int index, final int length, final ChannelBufferIndexFinder indexFinder) {
        final int bytes = this.buf().bytesBefore(index, length, indexFinder);
        if (bytes < 0) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
        return bytes;
    }
    
    public void markReaderIndex() {
        this.buf().markReaderIndex();
    }
    
    public void markWriterIndex() {
        throw new UnreplayableOperationException();
    }
    
    public ChannelBufferFactory factory() {
        return this.buf().factory();
    }
    
    public ByteOrder order() {
        return this.buf().order();
    }
    
    public boolean readable() {
        return !this.terminated || this.buf().readable();
    }
    
    public int readableBytes() {
        if (this.terminated) {
            return this.buf().readableBytes();
        }
        return Integer.MAX_VALUE - this.buf().readerIndex();
    }
    
    public byte readByte() {
        this.checkReadableBytes(1);
        return this.buf().readByte();
    }
    
    public short readUnsignedByte() {
        this.checkReadableBytes(1);
        return this.buf().readUnsignedByte();
    }
    
    public void readBytes(final byte[] dst, final int dstIndex, final int length) {
        this.checkReadableBytes(length);
        this.buf().readBytes(dst, dstIndex, length);
    }
    
    public void readBytes(final byte[] dst) {
        this.checkReadableBytes(dst.length);
        this.buf().readBytes(dst);
    }
    
    public void readBytes(final ByteBuffer dst) {
        throw new UnreplayableOperationException();
    }
    
    public void readBytes(final ChannelBuffer dst, final int dstIndex, final int length) {
        this.checkReadableBytes(length);
        this.buf().readBytes(dst, dstIndex, length);
    }
    
    public void readBytes(final ChannelBuffer dst, final int length) {
        throw new UnreplayableOperationException();
    }
    
    public void readBytes(final ChannelBuffer dst) {
        throw new UnreplayableOperationException();
    }
    
    public int readBytes(final GatheringByteChannel out, final int length) throws IOException {
        throw new UnreplayableOperationException();
    }
    
    public ChannelBuffer readBytes(final int length) {
        this.checkReadableBytes(length);
        return this.buf().readBytes(length);
    }
    
    public ChannelBuffer readSlice(final int length) {
        this.checkReadableBytes(length);
        return this.buf().readSlice(length);
    }
    
    public void readBytes(final OutputStream out, final int length) throws IOException {
        throw new UnreplayableOperationException();
    }
    
    public int readerIndex() {
        return this.buf().readerIndex();
    }
    
    public void readerIndex(final int readerIndex) {
        this.buf().readerIndex(readerIndex);
    }
    
    public int readInt() {
        this.checkReadableBytes(4);
        return this.buf().readInt();
    }
    
    public long readUnsignedInt() {
        this.checkReadableBytes(4);
        return this.buf().readUnsignedInt();
    }
    
    public long readLong() {
        this.checkReadableBytes(8);
        return this.buf().readLong();
    }
    
    public int readMedium() {
        this.checkReadableBytes(3);
        return this.buf().readMedium();
    }
    
    public int readUnsignedMedium() {
        this.checkReadableBytes(3);
        return this.buf().readUnsignedMedium();
    }
    
    public short readShort() {
        this.checkReadableBytes(2);
        return this.buf().readShort();
    }
    
    public int readUnsignedShort() {
        this.checkReadableBytes(2);
        return this.buf().readUnsignedShort();
    }
    
    public char readChar() {
        this.checkReadableBytes(2);
        return this.buf().readChar();
    }
    
    public float readFloat() {
        this.checkReadableBytes(4);
        return this.buf().readFloat();
    }
    
    public double readDouble() {
        this.checkReadableBytes(8);
        return this.buf().readDouble();
    }
    
    public void resetReaderIndex() {
        this.buf().resetReaderIndex();
    }
    
    public void resetWriterIndex() {
        throw new UnreplayableOperationException();
    }
    
    public void setByte(final int index, final int value) {
        throw new UnreplayableOperationException();
    }
    
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        throw new UnreplayableOperationException();
    }
    
    public void setBytes(final int index, final byte[] src) {
        throw new UnreplayableOperationException();
    }
    
    public void setBytes(final int index, final ByteBuffer src) {
        throw new UnreplayableOperationException();
    }
    
    public void setBytes(final int index, final ChannelBuffer src, final int srcIndex, final int length) {
        throw new UnreplayableOperationException();
    }
    
    public void setBytes(final int index, final ChannelBuffer src, final int length) {
        throw new UnreplayableOperationException();
    }
    
    public void setBytes(final int index, final ChannelBuffer src) {
        throw new UnreplayableOperationException();
    }
    
    public int setBytes(final int index, final InputStream in, final int length) throws IOException {
        throw new UnreplayableOperationException();
    }
    
    public void setZero(final int index, final int length) {
        throw new UnreplayableOperationException();
    }
    
    public int setBytes(final int index, final ScatteringByteChannel in, final int length) throws IOException {
        throw new UnreplayableOperationException();
    }
    
    public void setIndex(final int readerIndex, final int writerIndex) {
        throw new UnreplayableOperationException();
    }
    
    public void setInt(final int index, final int value) {
        throw new UnreplayableOperationException();
    }
    
    public void setLong(final int index, final long value) {
        throw new UnreplayableOperationException();
    }
    
    public void setMedium(final int index, final int value) {
        throw new UnreplayableOperationException();
    }
    
    public void setShort(final int index, final int value) {
        throw new UnreplayableOperationException();
    }
    
    public void setChar(final int index, final int value) {
        throw new UnreplayableOperationException();
    }
    
    public void setFloat(final int index, final float value) {
        throw new UnreplayableOperationException();
    }
    
    public void setDouble(final int index, final double value) {
        throw new UnreplayableOperationException();
    }
    
    public void skipBytes(final int length) {
        this.checkReadableBytes(length);
        this.buf().skipBytes(length);
    }
    
    public ChannelBuffer slice() {
        throw new UnreplayableOperationException();
    }
    
    public ChannelBuffer slice(final int index, final int length) {
        this.checkIndex(index, length);
        return this.buf().slice(index, length);
    }
    
    public ByteBuffer toByteBuffer() {
        throw new UnreplayableOperationException();
    }
    
    public ByteBuffer toByteBuffer(final int index, final int length) {
        this.checkIndex(index, length);
        return this.buf().toByteBuffer(index, length);
    }
    
    public ByteBuffer[] toByteBuffers() {
        throw new UnreplayableOperationException();
    }
    
    public ByteBuffer[] toByteBuffers(final int index, final int length) {
        this.checkIndex(index, length);
        return this.buf().toByteBuffers(index, length);
    }
    
    public String toString(final int index, final int length, final Charset charset) {
        this.checkIndex(index, length);
        return this.buf().toString(index, length, charset);
    }
    
    public String toString(final Charset charsetName) {
        throw new UnreplayableOperationException();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '(' + "ridx=" + this.readerIndex() + ", " + "widx=" + this.writerIndex() + ')';
    }
    
    public boolean writable() {
        return false;
    }
    
    public int writableBytes() {
        return 0;
    }
    
    public void writeByte(final int value) {
        throw new UnreplayableOperationException();
    }
    
    public void writeBytes(final byte[] src, final int srcIndex, final int length) {
        throw new UnreplayableOperationException();
    }
    
    public void writeBytes(final byte[] src) {
        throw new UnreplayableOperationException();
    }
    
    public void writeBytes(final ByteBuffer src) {
        throw new UnreplayableOperationException();
    }
    
    public void writeBytes(final ChannelBuffer src, final int srcIndex, final int length) {
        throw new UnreplayableOperationException();
    }
    
    public void writeBytes(final ChannelBuffer src, final int length) {
        throw new UnreplayableOperationException();
    }
    
    public void writeBytes(final ChannelBuffer src) {
        throw new UnreplayableOperationException();
    }
    
    public int writeBytes(final InputStream in, final int length) throws IOException {
        throw new UnreplayableOperationException();
    }
    
    public int writeBytes(final ScatteringByteChannel in, final int length) throws IOException {
        throw new UnreplayableOperationException();
    }
    
    public void writeInt(final int value) {
        throw new UnreplayableOperationException();
    }
    
    public void writeLong(final long value) {
        throw new UnreplayableOperationException();
    }
    
    public void writeMedium(final int value) {
        throw new UnreplayableOperationException();
    }
    
    public void writeZero(final int length) {
        throw new UnreplayableOperationException();
    }
    
    public int writerIndex() {
        return this.buf().writerIndex();
    }
    
    public void writerIndex(final int writerIndex) {
        throw new UnreplayableOperationException();
    }
    
    public void writeShort(final int value) {
        throw new UnreplayableOperationException();
    }
    
    public void writeChar(final int value) {
        throw new UnreplayableOperationException();
    }
    
    public void writeFloat(final float value) {
        throw new UnreplayableOperationException();
    }
    
    public void writeDouble(final double value) {
        throw new UnreplayableOperationException();
    }
    
    private void checkIndex(final int index, final int length) {
        if (index + length > this.buf().writerIndex()) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
    }
    
    private void checkReadableBytes(final int readableBytes) {
        if (this.buf().readableBytes() < readableBytes) {
            throw ReplayingDecoderBuffer.REPLAY;
        }
    }
    
    static {
        REPLAY = new ReplayError();
    }
}
