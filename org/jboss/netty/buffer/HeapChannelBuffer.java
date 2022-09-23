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
import java.nio.ByteBuffer;

public abstract class HeapChannelBuffer extends AbstractChannelBuffer
{
    protected final byte[] array;
    
    protected HeapChannelBuffer(final int length) {
        this(new byte[length], 0, 0);
    }
    
    protected HeapChannelBuffer(final byte[] array) {
        this(array, 0, array.length);
    }
    
    protected HeapChannelBuffer(final byte[] array, final int readerIndex, final int writerIndex) {
        if (array == null) {
            throw new NullPointerException("array");
        }
        this.array = array;
        this.setIndex(readerIndex, writerIndex);
    }
    
    public boolean isDirect() {
        return false;
    }
    
    public int capacity() {
        return this.array.length;
    }
    
    public boolean hasArray() {
        return true;
    }
    
    public byte[] array() {
        return this.array;
    }
    
    public int arrayOffset() {
        return 0;
    }
    
    public byte getByte(final int index) {
        return this.array[index];
    }
    
    public void getBytes(final int index, final ChannelBuffer dst, final int dstIndex, final int length) {
        if (dst instanceof HeapChannelBuffer) {
            this.getBytes(index, ((HeapChannelBuffer)dst).array, dstIndex, length);
        }
        else {
            dst.setBytes(dstIndex, this.array, index, length);
        }
    }
    
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        System.arraycopy(this.array, index, dst, dstIndex, length);
    }
    
    public void getBytes(final int index, final ByteBuffer dst) {
        dst.put(this.array, index, Math.min(this.capacity() - index, dst.remaining()));
    }
    
    public void getBytes(final int index, final OutputStream out, final int length) throws IOException {
        out.write(this.array, index, length);
    }
    
    public int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        return out.write(ByteBuffer.wrap(this.array, index, length));
    }
    
    public void setByte(final int index, final int value) {
        this.array[index] = (byte)value;
    }
    
    public void setBytes(final int index, final ChannelBuffer src, final int srcIndex, final int length) {
        if (src instanceof HeapChannelBuffer) {
            this.setBytes(index, ((HeapChannelBuffer)src).array, srcIndex, length);
        }
        else {
            src.getBytes(srcIndex, this.array, index, length);
        }
    }
    
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        System.arraycopy(src, srcIndex, this.array, index, length);
    }
    
    public void setBytes(final int index, final ByteBuffer src) {
        src.get(this.array, index, src.remaining());
    }
    
    public int setBytes(int index, final InputStream in, int length) throws IOException {
        int readBytes = 0;
        do {
            final int localReadBytes = in.read(this.array, index, length);
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
        return readBytes;
    }
    
    public int setBytes(final int index, final ScatteringByteChannel in, final int length) throws IOException {
        final ByteBuffer buf = ByteBuffer.wrap(this.array, index, length);
        int readBytes = 0;
        while (true) {
            int localReadBytes;
            try {
                localReadBytes = in.read(buf);
            }
            catch (ClosedChannelException e) {
                localReadBytes = -1;
            }
            if (localReadBytes < 0) {
                if (readBytes == 0) {
                    return -1;
                }
                break;
            }
            else {
                if (localReadBytes == 0) {
                    break;
                }
                readBytes += localReadBytes;
                if (readBytes >= length) {
                    break;
                }
                continue;
            }
        }
        return readBytes;
    }
    
    public ChannelBuffer slice(final int index, final int length) {
        if (index == 0) {
            if (length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            if (length == this.array.length) {
                final ChannelBuffer slice = this.duplicate();
                slice.setIndex(0, length);
                return slice;
            }
            return new TruncatedChannelBuffer(this, length);
        }
        else {
            if (length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new SlicedChannelBuffer(this, index, length);
        }
    }
    
    public ByteBuffer toByteBuffer(final int index, final int length) {
        return ByteBuffer.wrap(this.array, index, length).order(this.order());
    }
}
