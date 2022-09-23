// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.OutputStream;

public final class CodedOutputStream
{
    private final byte[] buffer;
    private final int limit;
    private int position;
    private final OutputStream output;
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final int LITTLE_ENDIAN_32_SIZE = 4;
    public static final int LITTLE_ENDIAN_64_SIZE = 8;
    
    static int computePreferredBufferSize(final int dataLength) {
        if (dataLength > 4096) {
            return 4096;
        }
        return dataLength;
    }
    
    private CodedOutputStream(final byte[] buffer, final int offset, final int length) {
        this.output = null;
        this.buffer = buffer;
        this.position = offset;
        this.limit = offset + length;
    }
    
    private CodedOutputStream(final OutputStream output, final byte[] buffer) {
        this.output = output;
        this.buffer = buffer;
        this.position = 0;
        this.limit = buffer.length;
    }
    
    public static CodedOutputStream newInstance(final OutputStream output) {
        return newInstance(output, 4096);
    }
    
    public static CodedOutputStream newInstance(final OutputStream output, final int bufferSize) {
        return new CodedOutputStream(output, new byte[bufferSize]);
    }
    
    public static CodedOutputStream newInstance(final byte[] flatArray) {
        return newInstance(flatArray, 0, flatArray.length);
    }
    
    public static CodedOutputStream newInstance(final byte[] flatArray, final int offset, final int length) {
        return new CodedOutputStream(flatArray, offset, length);
    }
    
    public void writeDouble(final int fieldNumber, final double value) throws IOException {
        this.writeTag(fieldNumber, 1);
        this.writeDoubleNoTag(value);
    }
    
    public void writeFloat(final int fieldNumber, final float value) throws IOException {
        this.writeTag(fieldNumber, 5);
        this.writeFloatNoTag(value);
    }
    
    public void writeUInt64(final int fieldNumber, final long value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeUInt64NoTag(value);
    }
    
    public void writeInt64(final int fieldNumber, final long value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeInt64NoTag(value);
    }
    
    public void writeInt32(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeInt32NoTag(value);
    }
    
    public void writeFixed64(final int fieldNumber, final long value) throws IOException {
        this.writeTag(fieldNumber, 1);
        this.writeFixed64NoTag(value);
    }
    
    public void writeFixed32(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 5);
        this.writeFixed32NoTag(value);
    }
    
    public void writeBool(final int fieldNumber, final boolean value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeBoolNoTag(value);
    }
    
    public void writeString(final int fieldNumber, final String value) throws IOException {
        this.writeTag(fieldNumber, 2);
        this.writeStringNoTag(value);
    }
    
    public void writeGroup(final int fieldNumber, final MessageLite value) throws IOException {
        this.writeTag(fieldNumber, 3);
        this.writeGroupNoTag(value);
        this.writeTag(fieldNumber, 4);
    }
    
    @Deprecated
    public void writeUnknownGroup(final int fieldNumber, final MessageLite value) throws IOException {
        this.writeGroup(fieldNumber, value);
    }
    
    public void writeMessage(final int fieldNumber, final MessageLite value) throws IOException {
        this.writeTag(fieldNumber, 2);
        this.writeMessageNoTag(value);
    }
    
    public void writeBytes(final int fieldNumber, final ByteString value) throws IOException {
        this.writeTag(fieldNumber, 2);
        this.writeBytesNoTag(value);
    }
    
    public void writeUInt32(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeUInt32NoTag(value);
    }
    
    public void writeEnum(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeEnumNoTag(value);
    }
    
    public void writeSFixed32(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 5);
        this.writeSFixed32NoTag(value);
    }
    
    public void writeSFixed64(final int fieldNumber, final long value) throws IOException {
        this.writeTag(fieldNumber, 1);
        this.writeSFixed64NoTag(value);
    }
    
    public void writeSInt32(final int fieldNumber, final int value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeSInt32NoTag(value);
    }
    
    public void writeSInt64(final int fieldNumber, final long value) throws IOException {
        this.writeTag(fieldNumber, 0);
        this.writeSInt64NoTag(value);
    }
    
    public void writeMessageSetExtension(final int fieldNumber, final MessageLite value) throws IOException {
        this.writeTag(1, 3);
        this.writeUInt32(2, fieldNumber);
        this.writeMessage(3, value);
        this.writeTag(1, 4);
    }
    
    public void writeRawMessageSetExtension(final int fieldNumber, final ByteString value) throws IOException {
        this.writeTag(1, 3);
        this.writeUInt32(2, fieldNumber);
        this.writeBytes(3, value);
        this.writeTag(1, 4);
    }
    
    public void writeDoubleNoTag(final double value) throws IOException {
        this.writeRawLittleEndian64(Double.doubleToRawLongBits(value));
    }
    
    public void writeFloatNoTag(final float value) throws IOException {
        this.writeRawLittleEndian32(Float.floatToRawIntBits(value));
    }
    
    public void writeUInt64NoTag(final long value) throws IOException {
        this.writeRawVarint64(value);
    }
    
    public void writeInt64NoTag(final long value) throws IOException {
        this.writeRawVarint64(value);
    }
    
    public void writeInt32NoTag(final int value) throws IOException {
        if (value >= 0) {
            this.writeRawVarint32(value);
        }
        else {
            this.writeRawVarint64(value);
        }
    }
    
    public void writeFixed64NoTag(final long value) throws IOException {
        this.writeRawLittleEndian64(value);
    }
    
    public void writeFixed32NoTag(final int value) throws IOException {
        this.writeRawLittleEndian32(value);
    }
    
    public void writeBoolNoTag(final boolean value) throws IOException {
        this.writeRawByte(value ? 1 : 0);
    }
    
    public void writeStringNoTag(final String value) throws IOException {
        final byte[] bytes = value.getBytes("UTF-8");
        this.writeRawVarint32(bytes.length);
        this.writeRawBytes(bytes);
    }
    
    public void writeGroupNoTag(final MessageLite value) throws IOException {
        value.writeTo(this);
    }
    
    @Deprecated
    public void writeUnknownGroupNoTag(final MessageLite value) throws IOException {
        this.writeGroupNoTag(value);
    }
    
    public void writeMessageNoTag(final MessageLite value) throws IOException {
        this.writeRawVarint32(value.getSerializedSize());
        value.writeTo(this);
    }
    
    public void writeBytesNoTag(final ByteString value) throws IOException {
        this.writeRawVarint32(value.size());
        this.writeRawBytes(value);
    }
    
    public void writeUInt32NoTag(final int value) throws IOException {
        this.writeRawVarint32(value);
    }
    
    public void writeEnumNoTag(final int value) throws IOException {
        this.writeInt32NoTag(value);
    }
    
    public void writeSFixed32NoTag(final int value) throws IOException {
        this.writeRawLittleEndian32(value);
    }
    
    public void writeSFixed64NoTag(final long value) throws IOException {
        this.writeRawLittleEndian64(value);
    }
    
    public void writeSInt32NoTag(final int value) throws IOException {
        this.writeRawVarint32(encodeZigZag32(value));
    }
    
    public void writeSInt64NoTag(final long value) throws IOException {
        this.writeRawVarint64(encodeZigZag64(value));
    }
    
    public static int computeDoubleSize(final int fieldNumber, final double value) {
        return computeTagSize(fieldNumber) + computeDoubleSizeNoTag(value);
    }
    
    public static int computeFloatSize(final int fieldNumber, final float value) {
        return computeTagSize(fieldNumber) + computeFloatSizeNoTag(value);
    }
    
    public static int computeUInt64Size(final int fieldNumber, final long value) {
        return computeTagSize(fieldNumber) + computeUInt64SizeNoTag(value);
    }
    
    public static int computeInt64Size(final int fieldNumber, final long value) {
        return computeTagSize(fieldNumber) + computeInt64SizeNoTag(value);
    }
    
    public static int computeInt32Size(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + computeInt32SizeNoTag(value);
    }
    
    public static int computeFixed64Size(final int fieldNumber, final long value) {
        return computeTagSize(fieldNumber) + computeFixed64SizeNoTag(value);
    }
    
    public static int computeFixed32Size(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + computeFixed32SizeNoTag(value);
    }
    
    public static int computeBoolSize(final int fieldNumber, final boolean value) {
        return computeTagSize(fieldNumber) + computeBoolSizeNoTag(value);
    }
    
    public static int computeStringSize(final int fieldNumber, final String value) {
        return computeTagSize(fieldNumber) + computeStringSizeNoTag(value);
    }
    
    public static int computeGroupSize(final int fieldNumber, final MessageLite value) {
        return computeTagSize(fieldNumber) * 2 + computeGroupSizeNoTag(value);
    }
    
    @Deprecated
    public static int computeUnknownGroupSize(final int fieldNumber, final MessageLite value) {
        return computeGroupSize(fieldNumber, value);
    }
    
    public static int computeMessageSize(final int fieldNumber, final MessageLite value) {
        return computeTagSize(fieldNumber) + computeMessageSizeNoTag(value);
    }
    
    public static int computeBytesSize(final int fieldNumber, final ByteString value) {
        return computeTagSize(fieldNumber) + computeBytesSizeNoTag(value);
    }
    
    public static int computeLazyFieldSize(final int fieldNumber, final LazyField value) {
        return computeTagSize(fieldNumber) + computeLazyFieldSizeNoTag(value);
    }
    
    public static int computeUInt32Size(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + computeUInt32SizeNoTag(value);
    }
    
    public static int computeEnumSize(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + computeEnumSizeNoTag(value);
    }
    
    public static int computeSFixed32Size(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + computeSFixed32SizeNoTag(value);
    }
    
    public static int computeSFixed64Size(final int fieldNumber, final long value) {
        return computeTagSize(fieldNumber) + computeSFixed64SizeNoTag(value);
    }
    
    public static int computeSInt32Size(final int fieldNumber, final int value) {
        return computeTagSize(fieldNumber) + computeSInt32SizeNoTag(value);
    }
    
    public static int computeSInt64Size(final int fieldNumber, final long value) {
        return computeTagSize(fieldNumber) + computeSInt64SizeNoTag(value);
    }
    
    public static int computeMessageSetExtensionSize(final int fieldNumber, final MessageLite value) {
        return computeTagSize(1) * 2 + computeUInt32Size(2, fieldNumber) + computeMessageSize(3, value);
    }
    
    public static int computeRawMessageSetExtensionSize(final int fieldNumber, final ByteString value) {
        return computeTagSize(1) * 2 + computeUInt32Size(2, fieldNumber) + computeBytesSize(3, value);
    }
    
    public static int computeLazyFieldMessageSetExtensionSize(final int fieldNumber, final LazyField value) {
        return computeTagSize(1) * 2 + computeUInt32Size(2, fieldNumber) + computeLazyFieldSize(3, value);
    }
    
    public static int computeDoubleSizeNoTag(final double value) {
        return 8;
    }
    
    public static int computeFloatSizeNoTag(final float value) {
        return 4;
    }
    
    public static int computeUInt64SizeNoTag(final long value) {
        return computeRawVarint64Size(value);
    }
    
    public static int computeInt64SizeNoTag(final long value) {
        return computeRawVarint64Size(value);
    }
    
    public static int computeInt32SizeNoTag(final int value) {
        if (value >= 0) {
            return computeRawVarint32Size(value);
        }
        return 10;
    }
    
    public static int computeFixed64SizeNoTag(final long value) {
        return 8;
    }
    
    public static int computeFixed32SizeNoTag(final int value) {
        return 4;
    }
    
    public static int computeBoolSizeNoTag(final boolean value) {
        return 1;
    }
    
    public static int computeStringSizeNoTag(final String value) {
        try {
            final byte[] bytes = value.getBytes("UTF-8");
            return computeRawVarint32Size(bytes.length) + bytes.length;
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported.", e);
        }
    }
    
    public static int computeGroupSizeNoTag(final MessageLite value) {
        return value.getSerializedSize();
    }
    
    @Deprecated
    public static int computeUnknownGroupSizeNoTag(final MessageLite value) {
        return computeGroupSizeNoTag(value);
    }
    
    public static int computeMessageSizeNoTag(final MessageLite value) {
        final int size = value.getSerializedSize();
        return computeRawVarint32Size(size) + size;
    }
    
    public static int computeLazyFieldSizeNoTag(final LazyField value) {
        final int size = value.getSerializedSize();
        return computeRawVarint32Size(size) + size;
    }
    
    public static int computeBytesSizeNoTag(final ByteString value) {
        return computeRawVarint32Size(value.size()) + value.size();
    }
    
    public static int computeUInt32SizeNoTag(final int value) {
        return computeRawVarint32Size(value);
    }
    
    public static int computeEnumSizeNoTag(final int value) {
        return computeInt32SizeNoTag(value);
    }
    
    public static int computeSFixed32SizeNoTag(final int value) {
        return 4;
    }
    
    public static int computeSFixed64SizeNoTag(final long value) {
        return 8;
    }
    
    public static int computeSInt32SizeNoTag(final int value) {
        return computeRawVarint32Size(encodeZigZag32(value));
    }
    
    public static int computeSInt64SizeNoTag(final long value) {
        return computeRawVarint64Size(encodeZigZag64(value));
    }
    
    private void refreshBuffer() throws IOException {
        if (this.output == null) {
            throw new OutOfSpaceException();
        }
        this.output.write(this.buffer, 0, this.position);
        this.position = 0;
    }
    
    public void flush() throws IOException {
        if (this.output != null) {
            this.refreshBuffer();
        }
    }
    
    public int spaceLeft() {
        if (this.output == null) {
            return this.limit - this.position;
        }
        throw new UnsupportedOperationException("spaceLeft() can only be called on CodedOutputStreams that are writing to a flat array.");
    }
    
    public void checkNoSpaceLeft() {
        if (this.spaceLeft() != 0) {
            throw new IllegalStateException("Did not write as much data as expected.");
        }
    }
    
    public void writeRawByte(final byte value) throws IOException {
        if (this.position == this.limit) {
            this.refreshBuffer();
        }
        this.buffer[this.position++] = value;
    }
    
    public void writeRawByte(final int value) throws IOException {
        this.writeRawByte((byte)value);
    }
    
    public void writeRawBytes(final ByteString value) throws IOException {
        this.writeRawBytes(value, 0, value.size());
    }
    
    public void writeRawBytes(final byte[] value) throws IOException {
        this.writeRawBytes(value, 0, value.length);
    }
    
    public void writeRawBytes(final byte[] value, int offset, int length) throws IOException {
        if (this.limit - this.position >= length) {
            System.arraycopy(value, offset, this.buffer, this.position, length);
            this.position += length;
        }
        else {
            final int bytesWritten = this.limit - this.position;
            System.arraycopy(value, offset, this.buffer, this.position, bytesWritten);
            offset += bytesWritten;
            length -= bytesWritten;
            this.position = this.limit;
            this.refreshBuffer();
            if (length <= this.limit) {
                System.arraycopy(value, offset, this.buffer, 0, length);
                this.position = length;
            }
            else {
                this.output.write(value, offset, length);
            }
        }
    }
    
    public void writeRawBytes(final ByteString value, int offset, int length) throws IOException {
        if (this.limit - this.position >= length) {
            value.copyTo(this.buffer, offset, this.position, length);
            this.position += length;
        }
        else {
            final int bytesWritten = this.limit - this.position;
            value.copyTo(this.buffer, offset, this.position, bytesWritten);
            offset += bytesWritten;
            length -= bytesWritten;
            this.position = this.limit;
            this.refreshBuffer();
            if (length <= this.limit) {
                value.copyTo(this.buffer, offset, 0, length);
                this.position = length;
            }
            else {
                final InputStream inputStreamFrom = value.newInput();
                if (offset != inputStreamFrom.skip(offset)) {
                    throw new IllegalStateException("Skip failed? Should never happen.");
                }
                while (length > 0) {
                    final int bytesToRead = Math.min(length, this.limit);
                    final int bytesRead = inputStreamFrom.read(this.buffer, 0, bytesToRead);
                    if (bytesRead != bytesToRead) {
                        throw new IllegalStateException("Read failed? Should never happen");
                    }
                    this.output.write(this.buffer, 0, bytesRead);
                    length -= bytesRead;
                }
            }
        }
    }
    
    public void writeTag(final int fieldNumber, final int wireType) throws IOException {
        this.writeRawVarint32(WireFormat.makeTag(fieldNumber, wireType));
    }
    
    public static int computeTagSize(final int fieldNumber) {
        return computeRawVarint32Size(WireFormat.makeTag(fieldNumber, 0));
    }
    
    public void writeRawVarint32(int value) throws IOException {
        while ((value & 0xFFFFFF80) != 0x0) {
            this.writeRawByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        this.writeRawByte(value);
    }
    
    public static int computeRawVarint32Size(final int value) {
        if ((value & 0xFFFFFF80) == 0x0) {
            return 1;
        }
        if ((value & 0xFFFFC000) == 0x0) {
            return 2;
        }
        if ((value & 0xFFE00000) == 0x0) {
            return 3;
        }
        if ((value & 0xF0000000) == 0x0) {
            return 4;
        }
        return 5;
    }
    
    public void writeRawVarint64(long value) throws IOException {
        while ((value & 0xFFFFFFFFFFFFFF80L) != 0x0L) {
            this.writeRawByte(((int)value & 0x7F) | 0x80);
            value >>>= 7;
        }
        this.writeRawByte((int)value);
    }
    
    public static int computeRawVarint64Size(final long value) {
        if ((value & 0xFFFFFFFFFFFFFF80L) == 0x0L) {
            return 1;
        }
        if ((value & 0xFFFFFFFFFFFFC000L) == 0x0L) {
            return 2;
        }
        if ((value & 0xFFFFFFFFFFE00000L) == 0x0L) {
            return 3;
        }
        if ((value & 0xFFFFFFFFF0000000L) == 0x0L) {
            return 4;
        }
        if ((value & 0xFFFFFFF800000000L) == 0x0L) {
            return 5;
        }
        if ((value & 0xFFFFFC0000000000L) == 0x0L) {
            return 6;
        }
        if ((value & 0xFFFE000000000000L) == 0x0L) {
            return 7;
        }
        if ((value & 0xFF00000000000000L) == 0x0L) {
            return 8;
        }
        if ((value & Long.MIN_VALUE) == 0x0L) {
            return 9;
        }
        return 10;
    }
    
    public void writeRawLittleEndian32(final int value) throws IOException {
        this.writeRawByte(value & 0xFF);
        this.writeRawByte(value >> 8 & 0xFF);
        this.writeRawByte(value >> 16 & 0xFF);
        this.writeRawByte(value >> 24 & 0xFF);
    }
    
    public void writeRawLittleEndian64(final long value) throws IOException {
        this.writeRawByte((int)value & 0xFF);
        this.writeRawByte((int)(value >> 8) & 0xFF);
        this.writeRawByte((int)(value >> 16) & 0xFF);
        this.writeRawByte((int)(value >> 24) & 0xFF);
        this.writeRawByte((int)(value >> 32) & 0xFF);
        this.writeRawByte((int)(value >> 40) & 0xFF);
        this.writeRawByte((int)(value >> 48) & 0xFF);
        this.writeRawByte((int)(value >> 56) & 0xFF);
    }
    
    public static int encodeZigZag32(final int n) {
        return n << 1 ^ n >> 31;
    }
    
    public static long encodeZigZag64(final long n) {
        return n << 1 ^ n >> 63;
    }
    
    public static class OutOfSpaceException extends IOException
    {
        private static final long serialVersionUID = -6947486886997889499L;
        
        OutOfSpaceException() {
            super("CodedOutputStream was writing to a flat byte array and ran out of space.");
        }
    }
}
