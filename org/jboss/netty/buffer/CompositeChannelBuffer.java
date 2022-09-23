// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.channels.ScatteringByteChannel;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import org.jboss.netty.util.internal.DetectionUtil;
import java.util.List;
import java.nio.ByteOrder;

public class CompositeChannelBuffer extends AbstractChannelBuffer
{
    private final ByteOrder order;
    private ChannelBuffer[] components;
    private int[] indices;
    private int lastAccessedComponentId;
    private final boolean gathering;
    
    public CompositeChannelBuffer(final ByteOrder endianness, final List<ChannelBuffer> buffers, final boolean gathering) {
        this.order = endianness;
        this.gathering = gathering;
        this.setComponents(buffers);
    }
    
    public boolean useGathering() {
        return this.gathering && DetectionUtil.javaVersion() >= 7;
    }
    
    public List<ChannelBuffer> decompose(final int index, final int length) {
        if (length == 0) {
            return Collections.emptyList();
        }
        if (index + length > this.capacity()) {
            throw new IndexOutOfBoundsException("Too many bytes to decompose - Need " + (index + length) + ", capacity is " + this.capacity());
        }
        int componentId = this.componentId(index);
        final List<ChannelBuffer> slice = new ArrayList<ChannelBuffer>(this.components.length);
        final ChannelBuffer first = this.components[componentId].duplicate();
        first.readerIndex(index - this.indices[componentId]);
        ChannelBuffer buf = first;
        int bytesToSlice = length;
        do {
            final int readableBytes = buf.readableBytes();
            if (bytesToSlice <= readableBytes) {
                buf.writerIndex(buf.readerIndex() + bytesToSlice);
                slice.add(buf);
                break;
            }
            slice.add(buf);
            bytesToSlice -= readableBytes;
            ++componentId;
            buf = this.components[componentId].duplicate();
        } while (bytesToSlice > 0);
        for (int i = 0; i < slice.size(); ++i) {
            slice.set(i, slice.get(i).slice());
        }
        return slice;
    }
    
    private void setComponents(final List<ChannelBuffer> newComponents) {
        assert !newComponents.isEmpty();
        this.lastAccessedComponentId = 0;
        this.components = new ChannelBuffer[newComponents.size()];
        for (int i = 0; i < this.components.length; ++i) {
            final ChannelBuffer c = newComponents.get(i);
            if (c.order() != this.order()) {
                throw new IllegalArgumentException("All buffers must have the same endianness.");
            }
            assert c.readerIndex() == 0;
            assert c.writerIndex() == c.capacity();
            this.components[i] = c;
        }
        (this.indices = new int[this.components.length + 1])[0] = 0;
        for (int i = 1; i <= this.components.length; ++i) {
            this.indices[i] = this.indices[i - 1] + this.components[i - 1].capacity();
        }
        this.setIndex(0, this.capacity());
    }
    
    private CompositeChannelBuffer(final CompositeChannelBuffer buffer) {
        this.order = buffer.order;
        this.gathering = buffer.gathering;
        this.components = buffer.components.clone();
        this.indices = buffer.indices.clone();
        this.setIndex(buffer.readerIndex(), buffer.writerIndex());
    }
    
    public ChannelBufferFactory factory() {
        return HeapChannelBufferFactory.getInstance(this.order());
    }
    
    public ByteOrder order() {
        return this.order;
    }
    
    public boolean isDirect() {
        return false;
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
    
    public int capacity() {
        return this.indices[this.components.length];
    }
    
    public int numComponents() {
        return this.components.length;
    }
    
    public byte getByte(final int index) {
        final int componentId = this.componentId(index);
        return this.components[componentId].getByte(index - this.indices[componentId]);
    }
    
    public short getShort(final int index) {
        final int componentId = this.componentId(index);
        if (index + 2 <= this.indices[componentId + 1]) {
            return this.components[componentId].getShort(index - this.indices[componentId]);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (short)((this.getByte(index) & 0xFF) << 8 | (this.getByte(index + 1) & 0xFF));
        }
        return (short)((this.getByte(index) & 0xFF) | (this.getByte(index + 1) & 0xFF) << 8);
    }
    
    public int getUnsignedMedium(final int index) {
        final int componentId = this.componentId(index);
        if (index + 3 <= this.indices[componentId + 1]) {
            return this.components[componentId].getUnsignedMedium(index - this.indices[componentId]);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (this.getShort(index) & 0xFFFF) << 8 | (this.getByte(index + 2) & 0xFF);
        }
        return (this.getShort(index) & 0xFFFF) | (this.getByte(index + 2) & 0xFF) << 16;
    }
    
    public int getInt(final int index) {
        final int componentId = this.componentId(index);
        if (index + 4 <= this.indices[componentId + 1]) {
            return this.components[componentId].getInt(index - this.indices[componentId]);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return (this.getShort(index) & 0xFFFF) << 16 | (this.getShort(index + 2) & 0xFFFF);
        }
        return (this.getShort(index) & 0xFFFF) | (this.getShort(index + 2) & 0xFFFF) << 16;
    }
    
    public long getLong(final int index) {
        final int componentId = this.componentId(index);
        if (index + 8 <= this.indices[componentId + 1]) {
            return this.components[componentId].getLong(index - this.indices[componentId]);
        }
        if (this.order() == ByteOrder.BIG_ENDIAN) {
            return ((long)this.getInt(index) & 0xFFFFFFFFL) << 32 | ((long)this.getInt(index + 4) & 0xFFFFFFFFL);
        }
        return ((long)this.getInt(index) & 0xFFFFFFFFL) | ((long)this.getInt(index + 4) & 0xFFFFFFFFL) << 32;
    }
    
    public void getBytes(int index, final byte[] dst, int dstIndex, int length) {
        if (index > this.capacity() - length || dstIndex > dst.length - length) {
            throw new IndexOutOfBoundsException("Too many bytes to read - Needs " + (index + length) + ", maximum is " + this.capacity() + " or " + dst.length);
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index must be >= 0");
        }
        if (length == 0) {
            return;
        }
        int i;
        int localLength;
        for (int componentId = i = this.componentId(index); length > 0; length -= localLength, ++i) {
            final ChannelBuffer s = this.components[i];
            final int adjustment = this.indices[i];
            localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
        }
    }
    
    public void getBytes(int index, final ByteBuffer dst) {
        final int componentId = this.componentId(index);
        final int limit = dst.limit();
        int length = dst.remaining();
        if (index > this.capacity() - length) {
            throw new IndexOutOfBoundsException("Too many bytes to be read - Needs " + (index + length) + ", maximum is " + this.capacity());
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index must be >= 0");
        }
        int i = componentId;
        try {
            while (length > 0) {
                final ChannelBuffer s = this.components[i];
                final int adjustment = this.indices[i];
                final int localLength = Math.min(length, s.capacity() - (index - adjustment));
                dst.limit(dst.position() + localLength);
                s.getBytes(index - adjustment, dst);
                index += localLength;
                length -= localLength;
                ++i;
            }
        }
        finally {
            dst.limit(limit);
        }
    }
    
    public void getBytes(int index, final ChannelBuffer dst, int dstIndex, int length) {
        if (index > this.capacity() - length || dstIndex > dst.capacity() - length) {
            throw new IndexOutOfBoundsException("Too many bytes to be read - Needs " + (index + length) + " or " + (dstIndex + length) + ", maximum is " + this.capacity() + " or " + dst.capacity());
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index must be >= 0");
        }
        if (length == 0) {
            return;
        }
        int localLength;
        for (int i = this.componentId(index); length > 0; length -= localLength, ++i) {
            final ChannelBuffer s = this.components[i];
            final int adjustment = this.indices[i];
            localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
        }
    }
    
    public int getBytes(final int index, final GatheringByteChannel out, final int length) throws IOException {
        if (this.useGathering()) {
            return (int)out.write(this.toByteBuffers(index, length));
        }
        return out.write(this.toByteBuffer(index, length));
    }
    
    public void getBytes(int index, final OutputStream out, int length) throws IOException {
        if (index > this.capacity() - length) {
            throw new IndexOutOfBoundsException("Too many bytes to be read - needs " + (index + length) + ", maximum of " + this.capacity());
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index must be >= 0");
        }
        if (length == 0) {
            return;
        }
        int localLength;
        for (int i = this.componentId(index); length > 0; length -= localLength, ++i) {
            final ChannelBuffer s = this.components[i];
            final int adjustment = this.indices[i];
            localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, out, localLength);
            index += localLength;
        }
    }
    
    public void setByte(final int index, final int value) {
        final int componentId = this.componentId(index);
        this.components[componentId].setByte(index - this.indices[componentId], value);
    }
    
    public void setShort(final int index, final int value) {
        final int componentId = this.componentId(index);
        if (index + 2 <= this.indices[componentId + 1]) {
            this.components[componentId].setShort(index - this.indices[componentId], value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this.setByte(index, (byte)(value >>> 8));
            this.setByte(index + 1, (byte)value);
        }
        else {
            this.setByte(index, (byte)value);
            this.setByte(index + 1, (byte)(value >>> 8));
        }
    }
    
    public void setMedium(final int index, final int value) {
        final int componentId = this.componentId(index);
        if (index + 3 <= this.indices[componentId + 1]) {
            this.components[componentId].setMedium(index - this.indices[componentId], value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this.setShort(index, (short)(value >> 8));
            this.setByte(index + 2, (byte)value);
        }
        else {
            this.setShort(index, (short)value);
            this.setByte(index + 2, (byte)(value >>> 16));
        }
    }
    
    public void setInt(final int index, final int value) {
        final int componentId = this.componentId(index);
        if (index + 4 <= this.indices[componentId + 1]) {
            this.components[componentId].setInt(index - this.indices[componentId], value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this.setShort(index, (short)(value >>> 16));
            this.setShort(index + 2, (short)value);
        }
        else {
            this.setShort(index, (short)value);
            this.setShort(index + 2, (short)(value >>> 16));
        }
    }
    
    public void setLong(final int index, final long value) {
        final int componentId = this.componentId(index);
        if (index + 8 <= this.indices[componentId + 1]) {
            this.components[componentId].setLong(index - this.indices[componentId], value);
        }
        else if (this.order() == ByteOrder.BIG_ENDIAN) {
            this.setInt(index, (int)(value >>> 32));
            this.setInt(index + 4, (int)value);
        }
        else {
            this.setInt(index, (int)value);
            this.setInt(index + 4, (int)(value >>> 32));
        }
    }
    
    public void setBytes(int index, final byte[] src, int srcIndex, int length) {
        final int componentId = this.componentId(index);
        if (index > this.capacity() - length || srcIndex > src.length - length) {
            throw new IndexOutOfBoundsException("Too many bytes to read - needs " + (index + length) + " or " + (srcIndex + length) + ", maximum is " + this.capacity() + " or " + src.length);
        }
        int localLength;
        for (int i = componentId; length > 0; length -= localLength, ++i) {
            final ChannelBuffer s = this.components[i];
            final int adjustment = this.indices[i];
            localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.setBytes(index - adjustment, src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
        }
    }
    
    public void setBytes(int index, final ByteBuffer src) {
        final int componentId = this.componentId(index);
        final int limit = src.limit();
        int length = src.remaining();
        if (index > this.capacity() - length) {
            throw new IndexOutOfBoundsException("Too many bytes to be written - Needs " + (index + length) + ", maximum is " + this.capacity());
        }
        int i = componentId;
        try {
            while (length > 0) {
                final ChannelBuffer s = this.components[i];
                final int adjustment = this.indices[i];
                final int localLength = Math.min(length, s.capacity() - (index - adjustment));
                src.limit(src.position() + localLength);
                s.setBytes(index - adjustment, src);
                index += localLength;
                length -= localLength;
                ++i;
            }
        }
        finally {
            src.limit(limit);
        }
    }
    
    public void setBytes(int index, final ChannelBuffer src, int srcIndex, int length) {
        final int componentId = this.componentId(index);
        if (index > this.capacity() - length || srcIndex > src.capacity() - length) {
            throw new IndexOutOfBoundsException("Too many bytes to be written - Needs " + (index + length) + " or " + (srcIndex + length) + ", maximum is " + this.capacity() + " or " + src.capacity());
        }
        int localLength;
        for (int i = componentId; length > 0; length -= localLength, ++i) {
            final ChannelBuffer s = this.components[i];
            final int adjustment = this.indices[i];
            localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.setBytes(index - adjustment, src, srcIndex, localLength);
            index += localLength;
            srcIndex += localLength;
        }
    }
    
    public int setBytes(int index, final InputStream in, int length) throws IOException {
        final int componentId = this.componentId(index);
        if (index > this.capacity() - length) {
            throw new IndexOutOfBoundsException("Too many bytes to write - Needs " + (index + length) + ", maximum is " + this.capacity());
        }
        int i = componentId;
        int readBytes = 0;
        do {
            final ChannelBuffer s = this.components[i];
            final int adjustment = this.indices[i];
            final int localLength = Math.min(length, s.capacity() - (index - adjustment));
            final int localReadBytes = s.setBytes(index - adjustment, in, localLength);
            if (localReadBytes < 0) {
                if (readBytes == 0) {
                    return -1;
                }
                break;
            }
            else if (localReadBytes == localLength) {
                index += localLength;
                length -= localLength;
                readBytes += localLength;
                ++i;
            }
            else {
                index += localReadBytes;
                length -= localReadBytes;
                readBytes += localReadBytes;
            }
        } while (length > 0);
        return readBytes;
    }
    
    public int setBytes(int index, final ScatteringByteChannel in, int length) throws IOException {
        final int componentId = this.componentId(index);
        if (index > this.capacity() - length) {
            throw new IndexOutOfBoundsException("Too many bytes to write - Needs " + (index + length) + ", maximum is " + this.capacity());
        }
        int i = componentId;
        int readBytes = 0;
        do {
            final ChannelBuffer s = this.components[i];
            final int adjustment = this.indices[i];
            final int localLength = Math.min(length, s.capacity() - (index - adjustment));
            final int localReadBytes = s.setBytes(index - adjustment, in, localLength);
            if (localReadBytes == 0) {
                break;
            }
            if (localReadBytes < 0) {
                if (readBytes == 0) {
                    return -1;
                }
                break;
            }
            else if (localReadBytes == localLength) {
                index += localLength;
                length -= localLength;
                readBytes += localLength;
                ++i;
            }
            else {
                index += localReadBytes;
                length -= localReadBytes;
                readBytes += localReadBytes;
            }
        } while (length > 0);
        return readBytes;
    }
    
    public ChannelBuffer duplicate() {
        final ChannelBuffer duplicate = new CompositeChannelBuffer(this);
        duplicate.setIndex(this.readerIndex(), this.writerIndex());
        return duplicate;
    }
    
    public ChannelBuffer copy(final int index, final int length) {
        final int componentId = this.componentId(index);
        if (index > this.capacity() - length) {
            throw new IndexOutOfBoundsException("Too many bytes to copy - Needs " + (index + length) + ", maximum is " + this.capacity());
        }
        final ChannelBuffer dst = this.factory().getBuffer(this.order(), length);
        this.copyTo(index, length, componentId, dst);
        return dst;
    }
    
    private void copyTo(int index, int length, final int componentId, final ChannelBuffer dst) {
        int dstIndex = 0;
        int localLength;
        for (int i = componentId; length > 0; length -= localLength, ++i) {
            final ChannelBuffer s = this.components[i];
            final int adjustment = this.indices[i];
            localLength = Math.min(length, s.capacity() - (index - adjustment));
            s.getBytes(index - adjustment, dst, dstIndex, localLength);
            index += localLength;
            dstIndex += localLength;
        }
        dst.writerIndex(dst.capacity());
    }
    
    public ChannelBuffer getBuffer(final int index) {
        if (index < 0 || index >= this.capacity()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index + " - Bytes needed: " + index + ", maximum is " + this.capacity());
        }
        return this.components[this.componentId(index)];
    }
    
    public ChannelBuffer slice(final int index, final int length) {
        if (index == 0) {
            if (length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
        }
        else {
            if (index < 0 || index > this.capacity() - length) {
                throw new IndexOutOfBoundsException("Invalid index: " + index + " - Bytes needed: " + (index + length) + ", maximum is " + this.capacity());
            }
            if (length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
        }
        final List<ChannelBuffer> components = this.decompose(index, length);
        switch (components.size()) {
            case 0: {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            case 1: {
                return components.get(0);
            }
            default: {
                return new CompositeChannelBuffer(this.order(), components, this.gathering);
            }
        }
    }
    
    public ByteBuffer toByteBuffer(final int index, final int length) {
        if (this.components.length == 1) {
            return this.components[0].toByteBuffer(index, length);
        }
        final ByteBuffer[] buffers = this.toByteBuffers(index, length);
        final ByteBuffer merged = ByteBuffer.allocate(length).order(this.order());
        for (final ByteBuffer b : buffers) {
            merged.put(b);
        }
        merged.flip();
        return merged;
    }
    
    @Override
    public ByteBuffer[] toByteBuffers(int index, int length) {
        if (index + length > this.capacity()) {
            throw new IndexOutOfBoundsException("Too many bytes to convert - Needs" + (index + length) + ", maximum is " + this.capacity());
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index must be >= 0");
        }
        if (length == 0) {
            return new ByteBuffer[0];
        }
        final List<ByteBuffer> buffers = new ArrayList<ByteBuffer>(this.components.length);
        int localLength;
        for (int i = this.componentId(index); length > 0; length -= localLength, ++i) {
            final ChannelBuffer s = this.components[i];
            final int adjustment = this.indices[i];
            localLength = Math.min(length, s.capacity() - (index - adjustment));
            buffers.add(s.toByteBuffer(index - adjustment, localLength));
            index += localLength;
        }
        return buffers.toArray(new ByteBuffer[buffers.size()]);
    }
    
    private int componentId(final int index) {
        final int lastComponentId = this.lastAccessedComponentId;
        if (index >= this.indices[lastComponentId]) {
            if (index < this.indices[lastComponentId + 1]) {
                return lastComponentId;
            }
            for (int i = lastComponentId + 1; i < this.components.length; ++i) {
                if (index < this.indices[i + 1]) {
                    return this.lastAccessedComponentId = i;
                }
            }
        }
        else {
            for (int i = lastComponentId - 1; i >= 0; --i) {
                if (index >= this.indices[i]) {
                    return this.lastAccessedComponentId = i;
                }
            }
        }
        throw new IndexOutOfBoundsException("Invalid index: " + index + ", maximum: " + this.indices.length);
    }
    
    @Override
    public void discardReadBytes() {
        final int localReaderIndex = this.readerIndex();
        if (localReaderIndex == 0) {
            return;
        }
        int localWriterIndex = this.writerIndex();
        final int bytesToMove = this.capacity() - localReaderIndex;
        List<ChannelBuffer> list = this.decompose(localReaderIndex, bytesToMove);
        if (list.isEmpty()) {
            list = new ArrayList<ChannelBuffer>(1);
        }
        final ChannelBuffer padding = ChannelBuffers.buffer(this.order(), localReaderIndex);
        padding.writerIndex(localReaderIndex);
        list.add(padding);
        int localMarkedReaderIndex = localReaderIndex;
        try {
            this.resetReaderIndex();
            localMarkedReaderIndex = this.readerIndex();
        }
        catch (IndexOutOfBoundsException ex) {}
        int localMarkedWriterIndex = localWriterIndex;
        try {
            this.resetWriterIndex();
            localMarkedWriterIndex = this.writerIndex();
        }
        catch (IndexOutOfBoundsException ex2) {}
        this.setComponents(list);
        localMarkedReaderIndex = Math.max(localMarkedReaderIndex - localReaderIndex, 0);
        localMarkedWriterIndex = Math.max(localMarkedWriterIndex - localReaderIndex, 0);
        this.setIndex(localMarkedReaderIndex, localMarkedWriterIndex);
        this.markReaderIndex();
        this.markWriterIndex();
        localWriterIndex = Math.max(localWriterIndex - localReaderIndex, 0);
        this.setIndex(0, localWriterIndex);
    }
    
    @Override
    public String toString() {
        String result = super.toString();
        result = result.substring(0, result.length() - 1);
        return result + ", components=" + this.components.length + ')';
    }
}
