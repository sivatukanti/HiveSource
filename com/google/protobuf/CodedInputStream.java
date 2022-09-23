// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;

public final class CodedInputStream
{
    private final byte[] buffer;
    private int bufferSize;
    private int bufferSizeAfterLimit;
    private int bufferPos;
    private final InputStream input;
    private int lastTag;
    private int totalBytesRetired;
    private int currentLimit;
    private int recursionDepth;
    private int recursionLimit;
    private int sizeLimit;
    private static final int DEFAULT_RECURSION_LIMIT = 64;
    private static final int DEFAULT_SIZE_LIMIT = 67108864;
    private static final int BUFFER_SIZE = 4096;
    
    public static CodedInputStream newInstance(final InputStream input) {
        return new CodedInputStream(input);
    }
    
    public static CodedInputStream newInstance(final byte[] buf) {
        return newInstance(buf, 0, buf.length);
    }
    
    public static CodedInputStream newInstance(final byte[] buf, final int off, final int len) {
        final CodedInputStream result = new CodedInputStream(buf, off, len);
        try {
            result.pushLimit(len);
        }
        catch (InvalidProtocolBufferException ex) {
            throw new IllegalArgumentException(ex);
        }
        return result;
    }
    
    public int readTag() throws IOException {
        if (this.isAtEnd()) {
            return this.lastTag = 0;
        }
        this.lastTag = this.readRawVarint32();
        if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
            throw InvalidProtocolBufferException.invalidTag();
        }
        return this.lastTag;
    }
    
    public void checkLastTagWas(final int value) throws InvalidProtocolBufferException {
        if (this.lastTag != value) {
            throw InvalidProtocolBufferException.invalidEndTag();
        }
    }
    
    public boolean skipField(final int tag) throws IOException {
        switch (WireFormat.getTagWireType(tag)) {
            case 0: {
                this.readInt32();
                return true;
            }
            case 1: {
                this.readRawLittleEndian64();
                return true;
            }
            case 2: {
                this.skipRawBytes(this.readRawVarint32());
                return true;
            }
            case 3: {
                this.skipMessage();
                this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
                return true;
            }
            case 4: {
                return false;
            }
            case 5: {
                this.readRawLittleEndian32();
                return true;
            }
            default: {
                throw InvalidProtocolBufferException.invalidWireType();
            }
        }
    }
    
    public void skipMessage() throws IOException {
        int tag;
        do {
            tag = this.readTag();
        } while (tag != 0 && this.skipField(tag));
    }
    
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readRawLittleEndian64());
    }
    
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readRawLittleEndian32());
    }
    
    public long readUInt64() throws IOException {
        return this.readRawVarint64();
    }
    
    public long readInt64() throws IOException {
        return this.readRawVarint64();
    }
    
    public int readInt32() throws IOException {
        return this.readRawVarint32();
    }
    
    public long readFixed64() throws IOException {
        return this.readRawLittleEndian64();
    }
    
    public int readFixed32() throws IOException {
        return this.readRawLittleEndian32();
    }
    
    public boolean readBool() throws IOException {
        return this.readRawVarint32() != 0;
    }
    
    public String readString() throws IOException {
        final int size = this.readRawVarint32();
        if (size <= this.bufferSize - this.bufferPos && size > 0) {
            final String result = new String(this.buffer, this.bufferPos, size, "UTF-8");
            this.bufferPos += size;
            return result;
        }
        return new String(this.readRawBytes(size), "UTF-8");
    }
    
    public void readGroup(final int fieldNumber, final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException {
        if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
        }
        ++this.recursionDepth;
        builder.mergeFrom(this, extensionRegistry);
        this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
        --this.recursionDepth;
    }
    
    public <T extends MessageLite> T readGroup(final int fieldNumber, final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException {
        if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
        }
        ++this.recursionDepth;
        final T result = parser.parsePartialFrom(this, extensionRegistry);
        this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
        --this.recursionDepth;
        return result;
    }
    
    @Deprecated
    public void readUnknownGroup(final int fieldNumber, final MessageLite.Builder builder) throws IOException {
        this.readGroup(fieldNumber, builder, null);
    }
    
    public void readMessage(final MessageLite.Builder builder, final ExtensionRegistryLite extensionRegistry) throws IOException {
        final int length = this.readRawVarint32();
        if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
        }
        final int oldLimit = this.pushLimit(length);
        ++this.recursionDepth;
        builder.mergeFrom(this, extensionRegistry);
        this.checkLastTagWas(0);
        --this.recursionDepth;
        this.popLimit(oldLimit);
    }
    
    public <T extends MessageLite> T readMessage(final Parser<T> parser, final ExtensionRegistryLite extensionRegistry) throws IOException {
        final int length = this.readRawVarint32();
        if (this.recursionDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
        }
        final int oldLimit = this.pushLimit(length);
        ++this.recursionDepth;
        final T result = parser.parsePartialFrom(this, extensionRegistry);
        this.checkLastTagWas(0);
        --this.recursionDepth;
        this.popLimit(oldLimit);
        return result;
    }
    
    public ByteString readBytes() throws IOException {
        final int size = this.readRawVarint32();
        if (size == 0) {
            return ByteString.EMPTY;
        }
        if (size <= this.bufferSize - this.bufferPos && size > 0) {
            final ByteString result = ByteString.copyFrom(this.buffer, this.bufferPos, size);
            this.bufferPos += size;
            return result;
        }
        return ByteString.copyFrom(this.readRawBytes(size));
    }
    
    public int readUInt32() throws IOException {
        return this.readRawVarint32();
    }
    
    public int readEnum() throws IOException {
        return this.readRawVarint32();
    }
    
    public int readSFixed32() throws IOException {
        return this.readRawLittleEndian32();
    }
    
    public long readSFixed64() throws IOException {
        return this.readRawLittleEndian64();
    }
    
    public int readSInt32() throws IOException {
        return decodeZigZag32(this.readRawVarint32());
    }
    
    public long readSInt64() throws IOException {
        return decodeZigZag64(this.readRawVarint64());
    }
    
    public int readRawVarint32() throws IOException {
        byte tmp = this.readRawByte();
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 0x7F;
        if ((tmp = this.readRawByte()) >= 0) {
            result |= tmp << 7;
        }
        else {
            result |= (tmp & 0x7F) << 7;
            if ((tmp = this.readRawByte()) >= 0) {
                result |= tmp << 14;
            }
            else {
                result |= (tmp & 0x7F) << 14;
                if ((tmp = this.readRawByte()) >= 0) {
                    result |= tmp << 21;
                }
                else {
                    result |= (tmp & 0x7F) << 21;
                    result |= (tmp = this.readRawByte()) << 28;
                    if (tmp < 0) {
                        for (int i = 0; i < 5; ++i) {
                            if (this.readRawByte() >= 0) {
                                return result;
                            }
                        }
                        throw InvalidProtocolBufferException.malformedVarint();
                    }
                }
            }
        }
        return result;
    }
    
    static int readRawVarint32(final InputStream input) throws IOException {
        final int firstByte = input.read();
        if (firstByte == -1) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return readRawVarint32(firstByte, input);
    }
    
    public static int readRawVarint32(final int firstByte, final InputStream input) throws IOException {
        if ((firstByte & 0x80) == 0x0) {
            return firstByte;
        }
        int result = firstByte & 0x7F;
        int offset;
        for (offset = 7; offset < 32; offset += 7) {
            final int b = input.read();
            if (b == -1) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            result |= (b & 0x7F) << offset;
            if ((b & 0x80) == 0x0) {
                return result;
            }
        }
        while (offset < 64) {
            final int b = input.read();
            if (b == -1) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            if ((b & 0x80) == 0x0) {
                return result;
            }
            offset += 7;
        }
        throw InvalidProtocolBufferException.malformedVarint();
    }
    
    public long readRawVarint64() throws IOException {
        int shift = 0;
        long result = 0L;
        while (shift < 64) {
            final byte b = this.readRawByte();
            result |= (long)(b & 0x7F) << shift;
            if ((b & 0x80) == 0x0) {
                return result;
            }
            shift += 7;
        }
        throw InvalidProtocolBufferException.malformedVarint();
    }
    
    public int readRawLittleEndian32() throws IOException {
        final byte b1 = this.readRawByte();
        final byte b2 = this.readRawByte();
        final byte b3 = this.readRawByte();
        final byte b4 = this.readRawByte();
        return (b1 & 0xFF) | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24;
    }
    
    public long readRawLittleEndian64() throws IOException {
        final byte b1 = this.readRawByte();
        final byte b2 = this.readRawByte();
        final byte b3 = this.readRawByte();
        final byte b4 = this.readRawByte();
        final byte b5 = this.readRawByte();
        final byte b6 = this.readRawByte();
        final byte b7 = this.readRawByte();
        final byte b8 = this.readRawByte();
        return ((long)b1 & 0xFFL) | ((long)b2 & 0xFFL) << 8 | ((long)b3 & 0xFFL) << 16 | ((long)b4 & 0xFFL) << 24 | ((long)b5 & 0xFFL) << 32 | ((long)b6 & 0xFFL) << 40 | ((long)b7 & 0xFFL) << 48 | ((long)b8 & 0xFFL) << 56;
    }
    
    public static int decodeZigZag32(final int n) {
        return n >>> 1 ^ -(n & 0x1);
    }
    
    public static long decodeZigZag64(final long n) {
        return n >>> 1 ^ -(n & 0x1L);
    }
    
    private CodedInputStream(final byte[] buffer, final int off, final int len) {
        this.currentLimit = Integer.MAX_VALUE;
        this.recursionLimit = 64;
        this.sizeLimit = 67108864;
        this.buffer = buffer;
        this.bufferSize = off + len;
        this.bufferPos = off;
        this.totalBytesRetired = -off;
        this.input = null;
    }
    
    private CodedInputStream(final InputStream input) {
        this.currentLimit = Integer.MAX_VALUE;
        this.recursionLimit = 64;
        this.sizeLimit = 67108864;
        this.buffer = new byte[4096];
        this.bufferSize = 0;
        this.bufferPos = 0;
        this.totalBytesRetired = 0;
        this.input = input;
    }
    
    public int setRecursionLimit(final int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Recursion limit cannot be negative: " + limit);
        }
        final int oldLimit = this.recursionLimit;
        this.recursionLimit = limit;
        return oldLimit;
    }
    
    public int setSizeLimit(final int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Size limit cannot be negative: " + limit);
        }
        final int oldLimit = this.sizeLimit;
        this.sizeLimit = limit;
        return oldLimit;
    }
    
    public void resetSizeCounter() {
        this.totalBytesRetired = -this.bufferPos;
    }
    
    public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
        if (byteLimit < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        byteLimit += this.totalBytesRetired + this.bufferPos;
        final int oldLimit = this.currentLimit;
        if (byteLimit > oldLimit) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        this.currentLimit = byteLimit;
        this.recomputeBufferSizeAfterLimit();
        return oldLimit;
    }
    
    private void recomputeBufferSizeAfterLimit() {
        this.bufferSize += this.bufferSizeAfterLimit;
        final int bufferEnd = this.totalBytesRetired + this.bufferSize;
        if (bufferEnd > this.currentLimit) {
            this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
            this.bufferSize -= this.bufferSizeAfterLimit;
        }
        else {
            this.bufferSizeAfterLimit = 0;
        }
    }
    
    public void popLimit(final int oldLimit) {
        this.currentLimit = oldLimit;
        this.recomputeBufferSizeAfterLimit();
    }
    
    public int getBytesUntilLimit() {
        if (this.currentLimit == Integer.MAX_VALUE) {
            return -1;
        }
        final int currentAbsolutePosition = this.totalBytesRetired + this.bufferPos;
        return this.currentLimit - currentAbsolutePosition;
    }
    
    public boolean isAtEnd() throws IOException {
        return this.bufferPos == this.bufferSize && !this.refillBuffer(false);
    }
    
    public int getTotalBytesRead() {
        return this.totalBytesRetired + this.bufferPos;
    }
    
    private boolean refillBuffer(final boolean mustSucceed) throws IOException {
        if (this.bufferPos < this.bufferSize) {
            throw new IllegalStateException("refillBuffer() called when buffer wasn't empty.");
        }
        if (this.totalBytesRetired + this.bufferSize == this.currentLimit) {
            if (mustSucceed) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            return false;
        }
        else {
            this.totalBytesRetired += this.bufferSize;
            this.bufferPos = 0;
            this.bufferSize = ((this.input == null) ? -1 : this.input.read(this.buffer));
            if (this.bufferSize == 0 || this.bufferSize < -1) {
                throw new IllegalStateException("InputStream#read(byte[]) returned invalid result: " + this.bufferSize + "\nThe InputStream implementation is buggy.");
            }
            if (this.bufferSize == -1) {
                this.bufferSize = 0;
                if (mustSucceed) {
                    throw InvalidProtocolBufferException.truncatedMessage();
                }
                return false;
            }
            else {
                this.recomputeBufferSizeAfterLimit();
                final int totalBytesRead = this.totalBytesRetired + this.bufferSize + this.bufferSizeAfterLimit;
                if (totalBytesRead > this.sizeLimit || totalBytesRead < 0) {
                    throw InvalidProtocolBufferException.sizeLimitExceeded();
                }
                return true;
            }
        }
    }
    
    public byte readRawByte() throws IOException {
        if (this.bufferPos == this.bufferSize) {
            this.refillBuffer(true);
        }
        return this.buffer[this.bufferPos++];
    }
    
    public byte[] readRawBytes(final int size) throws IOException {
        if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (this.totalBytesRetired + this.bufferPos + size > this.currentLimit) {
            this.skipRawBytes(this.currentLimit - this.totalBytesRetired - this.bufferPos);
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        if (size <= this.bufferSize - this.bufferPos) {
            final byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, this.bufferPos, bytes, 0, size);
            this.bufferPos += size;
            return bytes;
        }
        if (size < 4096) {
            final byte[] bytes = new byte[size];
            int pos = this.bufferSize - this.bufferPos;
            System.arraycopy(this.buffer, this.bufferPos, bytes, 0, pos);
            this.bufferPos = this.bufferSize;
            this.refillBuffer(true);
            while (size - pos > this.bufferSize) {
                System.arraycopy(this.buffer, 0, bytes, pos, this.bufferSize);
                pos += this.bufferSize;
                this.bufferPos = this.bufferSize;
                this.refillBuffer(true);
            }
            System.arraycopy(this.buffer, 0, bytes, pos, size - pos);
            this.bufferPos = size - pos;
            return bytes;
        }
        final int originalBufferPos = this.bufferPos;
        final int originalBufferSize = this.bufferSize;
        this.totalBytesRetired += this.bufferSize;
        this.bufferPos = 0;
        this.bufferSize = 0;
        int sizeLeft = size - (originalBufferSize - originalBufferPos);
        final List<byte[]> chunks = new ArrayList<byte[]>();
        while (sizeLeft > 0) {
            final byte[] chunk = new byte[Math.min(sizeLeft, 4096)];
            int n;
            for (int pos2 = 0; pos2 < chunk.length; pos2 += n) {
                n = ((this.input == null) ? -1 : this.input.read(chunk, pos2, chunk.length - pos2));
                if (n == -1) {
                    throw InvalidProtocolBufferException.truncatedMessage();
                }
                this.totalBytesRetired += n;
            }
            sizeLeft -= chunk.length;
            chunks.add(chunk);
        }
        final byte[] bytes2 = new byte[size];
        int pos2 = originalBufferSize - originalBufferPos;
        System.arraycopy(this.buffer, originalBufferPos, bytes2, 0, pos2);
        for (final byte[] chunk2 : chunks) {
            System.arraycopy(chunk2, 0, bytes2, pos2, chunk2.length);
            pos2 += chunk2.length;
        }
        return bytes2;
    }
    
    public void skipRawBytes(final int size) throws IOException {
        if (size < 0) {
            throw InvalidProtocolBufferException.negativeSize();
        }
        if (this.totalBytesRetired + this.bufferPos + size > this.currentLimit) {
            this.skipRawBytes(this.currentLimit - this.totalBytesRetired - this.bufferPos);
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        if (size <= this.bufferSize - this.bufferPos) {
            this.bufferPos += size;
        }
        else {
            int pos = this.bufferSize - this.bufferPos;
            this.bufferPos = this.bufferSize;
            this.refillBuffer(true);
            while (size - pos > this.bufferSize) {
                pos += this.bufferSize;
                this.bufferPos = this.bufferSize;
                this.refillBuffer(true);
            }
            this.bufferPos = size - pos;
        }
    }
}
