// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import java.nio.ByteBuffer;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryUtils;
import org.apache.hadoop.io.WritableUtils;
import java.util.ArrayList;

public final class WriteBuffers implements ByteStream.RandomAccessOutput
{
    private final ArrayList<byte[]> writeBuffers;
    private final int wbSize;
    private final int wbSizeLog2;
    private final long offsetMask;
    private final long maxSize;
    Position writePos;
    Position defaultReadPos;
    
    public WriteBuffers(final int wbSize, final long maxSize) {
        this.writeBuffers = new ArrayList<byte[]>(1);
        this.writePos = new Position();
        this.defaultReadPos = new Position();
        this.wbSize = ((Integer.bitCount(wbSize) == 1) ? wbSize : (Integer.highestOneBit(wbSize) << 1));
        this.wbSizeLog2 = 31 - Integer.numberOfLeadingZeros(this.wbSize);
        this.offsetMask = this.wbSize - 1;
        this.maxSize = maxSize;
        this.writePos.bufferIndex = -1;
        this.nextBufferToWrite();
    }
    
    public int readVInt() {
        return (int)this.readVLong(this.defaultReadPos);
    }
    
    public int readVInt(final Position readPos) {
        return (int)this.readVLong(readPos);
    }
    
    public long readVLong() {
        return this.readVLong(this.defaultReadPos);
    }
    
    public long readVLong(final Position readPos) {
        this.ponderNextBufferToRead(readPos);
        final byte firstByte = readPos.buffer[readPos.offset++];
        final int length = (byte)WritableUtils.decodeVIntSize(firstByte) - 1;
        if (length == 0) {
            return firstByte;
        }
        long i = 0L;
        if (this.isAllInOneReadBuffer(length, readPos)) {
            for (int idx = 0; idx < length; ++idx) {
                i = (i << 8 | (long)(readPos.buffer[readPos.offset + idx] & 0xFF));
            }
            readPos.offset += length;
        }
        else {
            for (int idx = 0; idx < length; ++idx) {
                i = (i << 8 | (long)(this.readNextByte(readPos) & 0xFF));
            }
        }
        return WritableUtils.isNegativeVInt(firstByte) ? (~i) : i;
    }
    
    public void skipVLong() {
        this.skipVLong(this.defaultReadPos);
    }
    
    public void skipVLong(final Position readPos) {
        this.ponderNextBufferToRead(readPos);
        final byte firstByte = readPos.buffer[readPos.offset++];
        final int length = (byte)WritableUtils.decodeVIntSize(firstByte);
        if (length > 1) {
            readPos.offset += length - 1;
        }
        for (int diff = readPos.offset - this.wbSize; diff >= 0; diff = readPos.offset - this.wbSize) {
            ++readPos.bufferIndex;
            readPos.buffer = this.writeBuffers.get(readPos.bufferIndex);
            readPos.offset = diff;
        }
    }
    
    public void setReadPoint(final long offset) {
        this.setReadPoint(offset, this.defaultReadPos);
    }
    
    public void setReadPoint(final long offset, final Position readPos) {
        readPos.bufferIndex = this.getBufferIndex(offset);
        readPos.buffer = this.writeBuffers.get(readPos.bufferIndex);
        readPos.offset = this.getOffset(offset);
    }
    
    public int hashCode(final long offset, final int length) {
        return this.hashCode(offset, length, this.defaultReadPos);
    }
    
    public int hashCode(final long offset, final int length, final Position readPos) {
        this.setReadPoint(offset, readPos);
        if (this.isAllInOneReadBuffer(length, readPos)) {
            final int result = murmurHash(readPos.buffer, readPos.offset, length);
            readPos.offset += length;
            return result;
        }
        final byte[] bytes = new byte[length];
        int toRead;
        for (int destOffset = 0; destOffset < length; destOffset += toRead) {
            this.ponderNextBufferToRead(readPos);
            toRead = Math.min(length - destOffset, this.wbSize - readPos.offset);
            System.arraycopy(readPos.buffer, readPos.offset, bytes, destOffset, toRead);
            readPos.offset += toRead;
        }
        return murmurHash(bytes, 0, bytes.length);
    }
    
    private byte readNextByte(final Position readPos) {
        this.ponderNextBufferToRead(readPos);
        return readPos.buffer[readPos.offset++];
    }
    
    private void ponderNextBufferToRead(final Position readPos) {
        if (readPos.offset >= this.wbSize) {
            ++readPos.bufferIndex;
            readPos.buffer = this.writeBuffers.get(readPos.bufferIndex);
            readPos.offset = 0;
        }
    }
    
    public int hashCode(final byte[] key, final int offset, final int length) {
        return murmurHash(key, offset, length);
    }
    
    private void setByte(final long offset, final byte value) {
        this.writeBuffers.get(this.getBufferIndex(offset))[this.getOffset(offset)] = value;
    }
    
    @Override
    public void reserve(final int byteCount) {
        if (byteCount < 0) {
            throw new AssertionError((Object)"byteCount must be non-negative");
        }
        int currentWriteOffset;
        for (currentWriteOffset = this.writePos.offset + byteCount; currentWriteOffset > this.wbSize; currentWriteOffset -= this.wbSize) {
            this.nextBufferToWrite();
        }
        this.writePos.offset = currentWriteOffset;
    }
    
    public void setWritePoint(final long offset) {
        this.writePos.bufferIndex = this.getBufferIndex(offset);
        this.writePos.buffer = this.writeBuffers.get(this.writePos.bufferIndex);
        this.writePos.offset = this.getOffset(offset);
    }
    
    @Override
    public void write(final int b) {
        if (this.writePos.offset == this.wbSize) {
            this.nextBufferToWrite();
        }
        this.writePos.buffer[this.writePos.offset++] = (byte)b;
    }
    
    @Override
    public void write(final byte[] b) {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
        int srcOffset = 0;
        while (srcOffset < len) {
            final int toWrite = Math.min(len - srcOffset, this.wbSize - this.writePos.offset);
            System.arraycopy(b, srcOffset + off, this.writePos.buffer, this.writePos.offset, toWrite);
            final Position writePos = this.writePos;
            writePos.offset += toWrite;
            srcOffset += toWrite;
            if (this.writePos.offset == this.wbSize) {
                this.nextBufferToWrite();
            }
        }
    }
    
    @Override
    public int getLength() {
        return (int)this.getWritePoint();
    }
    
    private int getOffset(final long offset) {
        return (int)(offset & this.offsetMask);
    }
    
    private int getBufferIndex(final long offset) {
        return (int)(offset >>> this.wbSizeLog2);
    }
    
    private void nextBufferToWrite() {
        if (this.writePos.bufferIndex == this.writeBuffers.size() - 1) {
            if ((1 + this.writeBuffers.size()) * (long)this.wbSize > this.maxSize) {
                throw new RuntimeException("Too much memory used by write buffers");
            }
            this.writeBuffers.add(new byte[this.wbSize]);
        }
        ++this.writePos.bufferIndex;
        this.writePos.buffer = this.writeBuffers.get(this.writePos.bufferIndex);
        this.writePos.offset = 0;
    }
    
    public boolean isEqual(final long leftOffset, final int leftLength, final long rightOffset, final int rightLength) {
        if (rightLength != leftLength) {
            return false;
        }
        int leftIndex = this.getBufferIndex(leftOffset);
        int rightIndex = this.getBufferIndex(rightOffset);
        int leftFrom = this.getOffset(leftOffset);
        int rightFrom = this.getOffset(rightOffset);
        byte[] leftBuffer = this.writeBuffers.get(leftIndex);
        byte[] rightBuffer = this.writeBuffers.get(rightIndex);
        if (leftFrom + leftLength <= this.wbSize && rightFrom + rightLength <= this.wbSize) {
            for (int i = 0; i < leftLength; ++i) {
                if (leftBuffer[leftFrom + i] != rightBuffer[rightFrom + i]) {
                    return false;
                }
            }
            return true;
        }
        for (int i = 0; i < leftLength; ++i) {
            if (leftFrom == this.wbSize) {
                ++leftIndex;
                leftBuffer = this.writeBuffers.get(leftIndex);
                leftFrom = 0;
            }
            if (rightFrom == this.wbSize) {
                ++rightIndex;
                rightBuffer = this.writeBuffers.get(rightIndex);
                rightFrom = 0;
            }
            if (leftBuffer[leftFrom++] != rightBuffer[rightFrom++]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isEqual(final byte[] left, final int leftLength, final long rightOffset, final int rightLength) {
        if (rightLength != leftLength) {
            return false;
        }
        int rightIndex = this.getBufferIndex(rightOffset);
        int rightFrom = this.getOffset(rightOffset);
        byte[] rightBuffer = this.writeBuffers.get(rightIndex);
        if (rightFrom + rightLength <= this.wbSize) {
            for (int i = 0; i < leftLength; ++i) {
                if (left[i] != rightBuffer[rightFrom + i]) {
                    return false;
                }
            }
            return true;
        }
        for (int i = 0; i < rightLength; ++i) {
            if (rightFrom == this.wbSize) {
                ++rightIndex;
                rightBuffer = this.writeBuffers.get(rightIndex);
                rightFrom = 0;
            }
            if (left[i] != rightBuffer[rightFrom++]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isEqual(final byte[] left, final int leftOffset, final int leftLength, final long rightOffset, final int rightLength) {
        if (rightLength != leftLength) {
            return false;
        }
        int rightIndex = this.getBufferIndex(rightOffset);
        int rightFrom = this.getOffset(rightOffset);
        byte[] rightBuffer = this.writeBuffers.get(rightIndex);
        if (rightFrom + rightLength <= this.wbSize) {
            for (int i = 0; i < leftLength; ++i) {
                if (left[leftOffset + i] != rightBuffer[rightFrom + i]) {
                    return false;
                }
            }
            return true;
        }
        for (int i = 0; i < rightLength; ++i) {
            if (rightFrom == this.wbSize) {
                ++rightIndex;
                rightBuffer = this.writeBuffers.get(rightIndex);
                rightFrom = 0;
            }
            if (left[leftOffset + i] != rightBuffer[rightFrom++]) {
                return false;
            }
        }
        return true;
    }
    
    public void clear() {
        this.writeBuffers.clear();
        this.clearState();
    }
    
    private void clearState() {
        this.writePos.clear();
        this.defaultReadPos.clear();
    }
    
    public long getWritePoint() {
        return ((long)this.writePos.bufferIndex << this.wbSizeLog2) + this.writePos.offset;
    }
    
    public long getReadPoint() {
        return this.getReadPoint(this.defaultReadPos);
    }
    
    public long getReadPoint(final Position readPos) {
        return readPos.bufferIndex * (long)this.wbSize + readPos.offset;
    }
    
    public void getByteSegmentRefToCurrent(final ByteSegmentRef byteSegmentRef, final int length, final Position readPos) {
        byteSegmentRef.reset(readPos.bufferIndex * (long)this.wbSize + readPos.offset, length);
        if (length > 0) {
            this.populateValue(byteSegmentRef);
        }
    }
    
    public void writeVInt(final int value) {
        LazyBinaryUtils.writeVInt(this, value);
    }
    
    public void writeVLong(final long value) {
        LazyBinaryUtils.writeVLong(this, value);
    }
    
    public void writeBytes(final long offset, final int length) {
        int readBufIndex = this.getBufferIndex(offset);
        byte[] readBuffer = this.writeBuffers.get(readBufIndex);
        int readBufOffset = this.getOffset(offset);
        int toRead;
        for (int srcOffset = 0; srcOffset < length; srcOffset += toRead) {
            if (readBufOffset == this.wbSize) {
                ++readBufIndex;
                readBuffer = this.writeBuffers.get(readBufIndex);
                readBufOffset = 0;
            }
            if (this.writePos.offset == this.wbSize) {
                this.nextBufferToWrite();
            }
            toRead = Math.min(length - srcOffset, this.wbSize - readBufOffset);
            final int toWrite = Math.min(toRead, this.wbSize - this.writePos.offset);
            System.arraycopy(readBuffer, readBufOffset, this.writePos.buffer, this.writePos.offset, toWrite);
            final Position writePos = this.writePos;
            writePos.offset += toWrite;
            readBufOffset += toWrite;
            srcOffset += toWrite;
            if (toRead > toWrite) {
                this.nextBufferToWrite();
                toRead -= toWrite;
                System.arraycopy(readBuffer, readBufOffset, this.writePos.buffer, this.writePos.offset, toRead);
                final Position writePos2 = this.writePos;
                writePos2.offset += toRead;
                readBufOffset += toRead;
            }
        }
    }
    
    public void populateValue(final ByteSegmentRef value) {
        int index = this.getBufferIndex(value.getOffset());
        byte[] buffer = this.writeBuffers.get(index);
        int bufferOffset = this.getOffset(value.getOffset());
        final int length = value.getLength();
        if (bufferOffset + length <= this.wbSize) {
            value.bytes = buffer;
            value.offset = bufferOffset;
            return;
        }
        value.bytes = new byte[length];
        value.offset = 0L;
        int toCopy;
        for (int destOffset = 0; destOffset < length; destOffset += toCopy) {
            if (destOffset > 0) {
                buffer = this.writeBuffers.get(++index);
                bufferOffset = 0;
            }
            toCopy = Math.min(length - destOffset, this.wbSize - bufferOffset);
            System.arraycopy(buffer, bufferOffset, value.bytes, destOffset, toCopy);
        }
    }
    
    private boolean isAllInOneReadBuffer(final int length, final Position readPos) {
        return readPos.offset + length <= this.wbSize;
    }
    
    private boolean isAllInOneWriteBuffer(final int length) {
        return this.writePos.offset + length <= this.wbSize;
    }
    
    public void seal() {
        if (this.writePos.offset < this.wbSize * 0.8) {
            final byte[] smallerBuffer = new byte[this.writePos.offset];
            System.arraycopy(this.writePos.buffer, 0, smallerBuffer, 0, this.writePos.offset);
            this.writeBuffers.set(this.writePos.bufferIndex, smallerBuffer);
        }
        if (this.writePos.bufferIndex + 1 < this.writeBuffers.size()) {
            this.writeBuffers.subList(this.writePos.bufferIndex + 1, this.writeBuffers.size()).clear();
        }
        this.clearState();
    }
    
    public long readNByteLong(final long offset, final int bytes) {
        return this.readNByteLong(offset, bytes, this.defaultReadPos);
    }
    
    public long readNByteLong(final long offset, final int bytes, final Position readPos) {
        this.setReadPoint(offset, readPos);
        long v = 0L;
        if (this.isAllInOneReadBuffer(bytes, readPos)) {
            for (int i = 0; i < bytes; ++i) {
                v = (v << 8) + (readPos.buffer[readPos.offset + i] & 0xFF);
            }
            readPos.offset += bytes;
        }
        else {
            for (int i = 0; i < bytes; ++i) {
                v = (v << 8) + (this.readNextByte(readPos) & 0xFF);
            }
        }
        return v;
    }
    
    public void writeFiveByteULong(long offset, final long v) {
        final int prevIndex = this.writePos.bufferIndex;
        final int prevOffset = this.writePos.offset;
        this.setWritePoint(offset);
        if (this.isAllInOneWriteBuffer(5)) {
            this.writePos.buffer[this.writePos.offset] = (byte)(v >>> 32);
            this.writePos.buffer[this.writePos.offset + 1] = (byte)(v >>> 24);
            this.writePos.buffer[this.writePos.offset + 2] = (byte)(v >>> 16);
            this.writePos.buffer[this.writePos.offset + 3] = (byte)(v >>> 8);
            this.writePos.buffer[this.writePos.offset + 4] = (byte)v;
            final Position writePos = this.writePos;
            writePos.offset += 5;
        }
        else {
            this.setByte(offset++, (byte)(v >>> 32));
            this.setByte(offset++, (byte)(v >>> 24));
            this.setByte(offset++, (byte)(v >>> 16));
            this.setByte(offset++, (byte)(v >>> 8));
            this.setByte(offset, (byte)v);
        }
        this.writePos.bufferIndex = prevIndex;
        this.writePos.buffer = this.writeBuffers.get(this.writePos.bufferIndex);
        this.writePos.offset = prevOffset;
    }
    
    public int readInt(final long offset) {
        return (int)this.readNByteLong(offset, 4);
    }
    
    @Override
    public void writeInt(long offset, final int v) {
        final int prevIndex = this.writePos.bufferIndex;
        final int prevOffset = this.writePos.offset;
        this.setWritePoint(offset);
        if (this.isAllInOneWriteBuffer(4)) {
            this.writePos.buffer[this.writePos.offset] = (byte)(v >> 24);
            this.writePos.buffer[this.writePos.offset + 1] = (byte)(v >> 16);
            this.writePos.buffer[this.writePos.offset + 2] = (byte)(v >> 8);
            this.writePos.buffer[this.writePos.offset + 3] = (byte)v;
            final Position writePos = this.writePos;
            writePos.offset += 4;
        }
        else {
            this.setByte(offset++, (byte)(v >>> 24));
            this.setByte(offset++, (byte)(v >>> 16));
            this.setByte(offset++, (byte)(v >>> 8));
            this.setByte(offset, (byte)v);
        }
        this.writePos.bufferIndex = prevIndex;
        this.writePos.buffer = this.writeBuffers.get(this.writePos.bufferIndex);
        this.writePos.offset = prevOffset;
    }
    
    @Override
    public void writeByte(final long offset, final byte value) {
        final int prevIndex = this.writePos.bufferIndex;
        final int prevOffset = this.writePos.offset;
        this.setWritePoint(offset);
        this.writePos.buffer[this.writePos.offset] = value;
        this.writePos.bufferIndex = prevIndex;
        this.writePos.buffer = this.writeBuffers.get(this.writePos.bufferIndex);
        this.writePos.offset = prevOffset;
    }
    
    public static int murmurHash(final byte[] data, final int offset, int length) {
        final int m = 1540483477;
        final int r = 24;
        int h = length;
        final int len_4 = length >> 2;
        for (int i = 0; i < len_4; ++i) {
            final int i_4 = offset + (i << 2);
            int k = data[i_4 + 3];
            k <<= 8;
            k |= (data[i_4 + 2] & 0xFF);
            k <<= 8;
            k |= (data[i_4 + 1] & 0xFF);
            k <<= 8;
            k |= (data[i_4 + 0] & 0xFF);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }
        final int len_m = len_4 << 2;
        final int left = length - len_m;
        if (left != 0) {
            length += offset;
            if (left >= 3) {
                h ^= data[length - 3] << 16;
            }
            if (left >= 2) {
                h ^= data[length - 2] << 8;
            }
            if (left >= 1) {
                h ^= data[length - 1];
            }
            h *= m;
        }
        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;
        return h;
    }
    
    public long size() {
        return this.writeBuffers.size() * (long)this.wbSize;
    }
    
    public Position getReadPosition() {
        return this.defaultReadPos;
    }
    
    public static class Position
    {
        private byte[] buffer;
        private int bufferIndex;
        private int offset;
        
        public Position() {
            this.buffer = null;
            this.bufferIndex = 0;
            this.offset = 0;
        }
        
        public void clear() {
            this.buffer = null;
            final int n = -1;
            this.offset = n;
            this.bufferIndex = n;
        }
    }
    
    public static class ByteSegmentRef
    {
        private byte[] bytes;
        private long offset;
        private int length;
        
        public ByteSegmentRef(final long offset, final int length) {
            this.bytes = null;
            this.reset(offset, length);
        }
        
        public void reset(final long offset, final int length) {
            if (length < 0) {
                throw new AssertionError((Object)("Length is negative: " + length));
            }
            this.offset = offset;
            this.length = length;
        }
        
        public ByteSegmentRef() {
            this.bytes = null;
        }
        
        public byte[] getBytes() {
            return this.bytes;
        }
        
        public long getOffset() {
            return this.offset;
        }
        
        public int getLength() {
            return this.length;
        }
        
        public ByteBuffer copy() {
            final byte[] copy = new byte[this.length];
            if (this.length > 0) {
                System.arraycopy(this.bytes, (int)this.offset, copy, 0, this.length);
            }
            return ByteBuffer.wrap(copy);
        }
    }
}
