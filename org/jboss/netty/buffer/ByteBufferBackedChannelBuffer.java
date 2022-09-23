// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.ScatteringByteChannel;
import java.io.InputStream;
import java.nio.channels.GatheringByteChannel;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public class ByteBufferBackedChannelBuffer extends AbstractChannelBuffer
{
    private final ByteBuffer buffer;
    private final ByteOrder order;
    private final int capacity;
    
    public ByteBufferBackedChannelBuffer(final ByteBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.order = buffer.order();
        this.buffer = buffer.slice().order(this.order);
        this.writerIndex(this.capacity = buffer.remaining());
    }
    
    private ByteBufferBackedChannelBuffer(final ByteBufferBackedChannelBuffer buffer) {
        this.buffer = buffer.buffer;
        this.order = buffer.order;
        this.capacity = buffer.capacity;
        this.setIndex(buffer.readerIndex(), buffer.writerIndex());
    }
    
    public ChannelBufferFactory factory() {
        if (this.buffer.isDirect()) {
            return DirectChannelBufferFactory.getInstance(this.order());
        }
        return HeapChannelBufferFactory.getInstance(this.order());
    }
    
    public boolean isDirect() {
        return this.buffer.isDirect();
    }
    
    public ByteOrder order() {
        return this.order;
    }
    
    public int capacity() {
        return this.capacity;
    }
    
    public boolean hasArray() {
        return this.buffer.hasArray();
    }
    
    public byte[] array() {
        return this.buffer.array();
    }
    
    public int arrayOffset() {
        return this.buffer.arrayOffset();
    }
    
    public byte getByte(final int index) {
        return this.buffer.get(index);
    }
    
    public short getShort(final int index) {
        return this.buffer.getShort(index);
    }
    
    public int getUnsignedMedium(final int index) {
        return (this.getByte(index) & 0xFF) << 16 | (this.getByte(index + 1) & 0xFF) << 8 | (this.getByte(index + 2) & 0xFF);
    }
    
    public int getInt(final int index) {
        return this.buffer.getInt(index);
    }
    
    public long getLong(final int index) {
        return this.buffer.getLong(index);
    }
    
    public void getBytes(final int index, final ChannelBuffer dst, final int dstIndex, final int length) {
        if (dst instanceof ByteBufferBackedChannelBuffer) {
            final ByteBufferBackedChannelBuffer bbdst = (ByteBufferBackedChannelBuffer)dst;
            final ByteBuffer data = bbdst.buffer.duplicate();
            data.limit(dstIndex + length).position(dstIndex);
            this.getBytes(index, data);
        }
        else if (this.buffer.hasArray()) {
            dst.setBytes(dstIndex, this.buffer.array(), index + this.buffer.arrayOffset(), length);
        }
        else {
            dst.setBytes(dstIndex, this, index, length);
        }
    }
    
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        final ByteBuffer data = this.buffer.duplicate();
        try {
            data.limit(index + length).position(index);
        }
        catch (IllegalArgumentException e) {
            throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + length) + ", maximum is " + data.limit());
        }
        data.get(dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final ByteBuffer dst) {
        final ByteBuffer data = this.buffer.duplicate();
        final int bytesToCopy = Math.min(this.capacity() - index, dst.remaining());
        try {
            data.limit(index + bytesToCopy).position(index);
        }
        catch (IllegalArgumentException e) {
            throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + bytesToCopy) + ", maximum is " + data.limit());
        }
        dst.put(data);
    }
    
    public void setByte(final int index, final int value) {
        this.buffer.put(index, (byte)value);
    }
    
    public void setShort(final int index, final int value) {
        this.buffer.putShort(index, (short)value);
    }
    
    public void setMedium(final int index, final int value) {
        this.setByte(index, (byte)(value >>> 16));
        this.setByte(index + 1, (byte)(value >>> 8));
        this.setByte(index + 2, (byte)value);
    }
    
    public void setInt(final int index, final int value) {
        this.buffer.putInt(index, value);
    }
    
    public void setLong(final int index, final long value) {
        this.buffer.putLong(index, value);
    }
    
    public void setBytes(final int index, final ChannelBuffer src, final int srcIndex, final int length) {
        if (src instanceof ByteBufferBackedChannelBuffer) {
            final ByteBufferBackedChannelBuffer bbsrc = (ByteBufferBackedChannelBuffer)src;
            final ByteBuffer data = bbsrc.buffer.duplicate();
            data.limit(srcIndex + length).position(srcIndex);
            this.setBytes(index, data);
        }
        else if (this.buffer.hasArray()) {
            src.getBytes(srcIndex, this.buffer.array(), index + this.buffer.arrayOffset(), length);
        }
        else {
            src.getBytes(srcIndex, this, index, length);
        }
    }
    
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        final ByteBuffer data = this.buffer.duplicate();
        data.limit(index + length).position(index);
        data.put(src, srcIndex, length);
    }
    
    public void setBytes(final int index, final ByteBuffer src) {
        final ByteBuffer data = this.buffer.duplicate();
        data.limit(index + src.remaining()).position(index);
        data.put(src);
    }
    
    public void getBytes(final int index, final OutputStream out, final int length) throws IOException {
        if (length == 0) {
            return;
        }
        if (this.buffer.hasArray()) {
            out.write(this.buffer.array(), index + this.buffer.arrayOffset(), length);
        }
        else {
            final byte[] tmp = new byte[length];
            ((ByteBuffer)this.buffer.duplicate().position(index)).get(tmp);
            out.write(tmp);
        }
    }
    
    public int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        if (length == 0) {
            return 0;
        }
        return out.write((ByteBuffer)this.buffer.duplicate().position(index).limit(index + length));
    }
    
    public int setBytes(int index, final InputStream in, int length) throws IOException {
        int readBytes = 0;
        if (this.buffer.hasArray()) {
            index += this.buffer.arrayOffset();
            do {
                final int localReadBytes = in.read(this.buffer.array(), index, length);
                if (localReadBytes < 0) {
                    if (readBytes == 0) {
                        return -1;
                    }
                    break;
                }
                else {
                    readBytes += localReadBytes;
                    index += localReadBytes;
                    length -= localReadBytes;
                }
            } while (length > 0);
        }
        else {
            final byte[] tmp = new byte[length];
            int i = 0;
            do {
                final int localReadBytes2 = in.read(tmp, i, tmp.length - i);
                if (localReadBytes2 < 0) {
                    if (readBytes == 0) {
                        return -1;
                    }
                    break;
                }
                else {
                    readBytes += localReadBytes2;
                    i += readBytes;
                }
            } while (i < tmp.length);
            ((ByteBuffer)this.buffer.duplicate().position(index)).put(tmp);
        }
        return readBytes;
    }
    
    public int setBytes(final int index, final ScatteringByteChannel in, final int length) throws IOException {
        final ByteBuffer slice = (ByteBuffer)this.buffer.duplicate().limit(index + length).position(index);
        int readBytes = 0;
        while (readBytes < length) {
            int localReadBytes;
            try {
                localReadBytes = in.read(slice);
            }
            catch (ClosedChannelException e) {
                localReadBytes = -1;
            }
            if (localReadBytes < 0) {
                if (readBytes == 0) {
                    return -1;
                }
                return readBytes;
            }
            else {
                if (localReadBytes == 0) {
                    break;
                }
                readBytes += localReadBytes;
            }
        }
        return readBytes;
    }
    
    public ByteBuffer toByteBuffer(final int index, final int length) {
        if (index == 0 && length == this.capacity()) {
            return this.buffer.duplicate().order(this.order());
        }
        return ((ByteBuffer)this.buffer.duplicate().position(index).limit(index + length)).slice().order(this.order());
    }
    
    public ChannelBuffer slice(final int index, final int length) {
        if (index == 0 && length == this.capacity()) {
            final ChannelBuffer slice = this.duplicate();
            slice.setIndex(0, length);
            return slice;
        }
        if (index >= 0 && length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        return new ByteBufferBackedChannelBuffer(((ByteBuffer)this.buffer.duplicate().position(index).limit(index + length)).order(this.order()));
    }
    
    public ChannelBuffer duplicate() {
        return new ByteBufferBackedChannelBuffer(this);
    }
    
    public ChannelBuffer copy(final int index, final int length) {
        ByteBuffer src;
        try {
            src = (ByteBuffer)this.buffer.duplicate().position(index).limit(index + length);
        }
        catch (IllegalArgumentException e) {
            throw new IndexOutOfBoundsException("Too many bytes to read - Need " + (index + length));
        }
        final ByteBuffer dst = this.buffer.isDirect() ? ByteBuffer.allocateDirect(length) : ByteBuffer.allocate(length);
        dst.put(src);
        dst.order(this.order());
        dst.clear();
        return new ByteBufferBackedChannelBuffer(dst);
    }
}
