// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.protocol;

import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.ShortStack;

public class TCompactProtocol extends TProtocol
{
    private static final long NO_LENGTH_LIMIT = -1L;
    private static final TStruct ANONYMOUS_STRUCT;
    private static final TField TSTOP;
    private static final byte[] ttypeToCompactType;
    private static final byte PROTOCOL_ID = -126;
    private static final byte VERSION = 1;
    private static final byte VERSION_MASK = 31;
    private static final byte TYPE_MASK = -32;
    private static final byte TYPE_BITS = 7;
    private static final int TYPE_SHIFT_AMOUNT = 5;
    private ShortStack lastField_;
    private short lastFieldId_;
    private TField booleanField_;
    private Boolean boolValue_;
    private final long stringLengthLimit_;
    private final long containerLengthLimit_;
    byte[] i32buf;
    byte[] varint64out;
    private byte[] byteDirectBuffer;
    byte[] byteRawBuf;
    
    public TCompactProtocol(final TTransport transport, final long stringLengthLimit, final long containerLengthLimit) {
        super(transport);
        this.lastField_ = new ShortStack(15);
        this.lastFieldId_ = 0;
        this.booleanField_ = null;
        this.boolValue_ = null;
        this.i32buf = new byte[5];
        this.varint64out = new byte[10];
        this.byteDirectBuffer = new byte[1];
        this.byteRawBuf = new byte[1];
        this.stringLengthLimit_ = stringLengthLimit;
        this.containerLengthLimit_ = containerLengthLimit;
    }
    
    @Deprecated
    public TCompactProtocol(final TTransport transport, final long stringLengthLimit) {
        this(transport, stringLengthLimit, -1L);
    }
    
    public TCompactProtocol(final TTransport transport) {
        this(transport, -1L, -1L);
    }
    
    @Override
    public void reset() {
        this.lastField_.clear();
        this.lastFieldId_ = 0;
    }
    
    @Override
    public void writeMessageBegin(final TMessage message) throws TException {
        this.writeByteDirect((byte)(-126));
        this.writeByteDirect(0x1 | (message.type << 5 & 0xFFFFFFE0));
        this.writeVarint32(message.seqid);
        this.writeString(message.name);
    }
    
    @Override
    public void writeStructBegin(final TStruct struct) throws TException {
        this.lastField_.push(this.lastFieldId_);
        this.lastFieldId_ = 0;
    }
    
    @Override
    public void writeStructEnd() throws TException {
        this.lastFieldId_ = this.lastField_.pop();
    }
    
    @Override
    public void writeFieldBegin(final TField field) throws TException {
        if (field.type == 2) {
            this.booleanField_ = field;
        }
        else {
            this.writeFieldBeginInternal(field, (byte)(-1));
        }
    }
    
    private void writeFieldBeginInternal(final TField field, final byte typeOverride) throws TException {
        final byte typeToWrite = (typeOverride == -1) ? this.getCompactType(field.type) : typeOverride;
        if (field.id > this.lastFieldId_ && field.id - this.lastFieldId_ <= 15) {
            this.writeByteDirect(field.id - this.lastFieldId_ << 4 | typeToWrite);
        }
        else {
            this.writeByteDirect(typeToWrite);
            this.writeI16(field.id);
        }
        this.lastFieldId_ = field.id;
    }
    
    @Override
    public void writeFieldStop() throws TException {
        this.writeByteDirect((byte)0);
    }
    
    @Override
    public void writeMapBegin(final TMap map) throws TException {
        if (map.size == 0) {
            this.writeByteDirect(0);
        }
        else {
            this.writeVarint32(map.size);
            this.writeByteDirect(this.getCompactType(map.keyType) << 4 | this.getCompactType(map.valueType));
        }
    }
    
    @Override
    public void writeListBegin(final TList list) throws TException {
        this.writeCollectionBegin(list.elemType, list.size);
    }
    
    @Override
    public void writeSetBegin(final TSet set) throws TException {
        this.writeCollectionBegin(set.elemType, set.size);
    }
    
    @Override
    public void writeBool(final boolean b) throws TException {
        if (this.booleanField_ != null) {
            this.writeFieldBeginInternal(this.booleanField_, (byte)(b ? 1 : 2));
            this.booleanField_ = null;
        }
        else {
            this.writeByteDirect((byte)(b ? 1 : 2));
        }
    }
    
    @Override
    public void writeByte(final byte b) throws TException {
        this.writeByteDirect(b);
    }
    
    @Override
    public void writeI16(final short i16) throws TException {
        this.writeVarint32(this.intToZigZag(i16));
    }
    
    @Override
    public void writeI32(final int i32) throws TException {
        this.writeVarint32(this.intToZigZag(i32));
    }
    
    @Override
    public void writeI64(final long i64) throws TException {
        this.writeVarint64(this.longToZigzag(i64));
    }
    
    @Override
    public void writeDouble(final double dub) throws TException {
        final byte[] data = { 0, 0, 0, 0, 0, 0, 0, 0 };
        this.fixedLongToBytes(Double.doubleToLongBits(dub), data, 0);
        this.trans_.write(data);
    }
    
    @Override
    public void writeString(final String str) throws TException {
        try {
            final byte[] bytes = str.getBytes("UTF-8");
            this.writeBinary(bytes, 0, bytes.length);
        }
        catch (UnsupportedEncodingException e) {
            throw new TException("UTF-8 not supported!");
        }
    }
    
    @Override
    public void writeBinary(final ByteBuffer bin) throws TException {
        final int length = bin.limit() - bin.position();
        this.writeBinary(bin.array(), bin.position() + bin.arrayOffset(), length);
    }
    
    private void writeBinary(final byte[] buf, final int offset, final int length) throws TException {
        this.writeVarint32(length);
        this.trans_.write(buf, offset, length);
    }
    
    @Override
    public void writeMessageEnd() throws TException {
    }
    
    @Override
    public void writeMapEnd() throws TException {
    }
    
    @Override
    public void writeListEnd() throws TException {
    }
    
    @Override
    public void writeSetEnd() throws TException {
    }
    
    @Override
    public void writeFieldEnd() throws TException {
    }
    
    protected void writeCollectionBegin(final byte elemType, final int size) throws TException {
        if (size <= 14) {
            this.writeByteDirect(size << 4 | this.getCompactType(elemType));
        }
        else {
            this.writeByteDirect(0xF0 | this.getCompactType(elemType));
            this.writeVarint32(size);
        }
    }
    
    private void writeVarint32(int n) throws TException {
        int idx = 0;
        while ((n & 0xFFFFFF80) != 0x0) {
            this.i32buf[idx++] = (byte)((n & 0x7F) | 0x80);
            n >>>= 7;
        }
        this.i32buf[idx++] = (byte)n;
        this.trans_.write(this.i32buf, 0, idx);
    }
    
    private void writeVarint64(long n) throws TException {
        int idx = 0;
        while ((n & 0xFFFFFFFFFFFFFF80L) != 0x0L) {
            this.varint64out[idx++] = (byte)((n & 0x7FL) | 0x80L);
            n >>>= 7;
        }
        this.varint64out[idx++] = (byte)n;
        this.trans_.write(this.varint64out, 0, idx);
    }
    
    private long longToZigzag(final long l) {
        return l << 1 ^ l >> 63;
    }
    
    private int intToZigZag(final int n) {
        return n << 1 ^ n >> 31;
    }
    
    private void fixedLongToBytes(final long n, final byte[] buf, final int off) {
        buf[off + 0] = (byte)(n & 0xFFL);
        buf[off + 1] = (byte)(n >> 8 & 0xFFL);
        buf[off + 2] = (byte)(n >> 16 & 0xFFL);
        buf[off + 3] = (byte)(n >> 24 & 0xFFL);
        buf[off + 4] = (byte)(n >> 32 & 0xFFL);
        buf[off + 5] = (byte)(n >> 40 & 0xFFL);
        buf[off + 6] = (byte)(n >> 48 & 0xFFL);
        buf[off + 7] = (byte)(n >> 56 & 0xFFL);
    }
    
    private void writeByteDirect(final byte b) throws TException {
        this.byteDirectBuffer[0] = b;
        this.trans_.write(this.byteDirectBuffer);
    }
    
    private void writeByteDirect(final int n) throws TException {
        this.writeByteDirect((byte)n);
    }
    
    @Override
    public TMessage readMessageBegin() throws TException {
        final byte protocolId = this.readByte();
        if (protocolId != -126) {
            throw new TProtocolException("Expected protocol id " + Integer.toHexString(-126) + " but got " + Integer.toHexString(protocolId));
        }
        final byte versionAndType = this.readByte();
        final byte version = (byte)(versionAndType & 0x1F);
        if (version != 1) {
            throw new TProtocolException("Expected version 1 but got " + version);
        }
        final byte type = (byte)(versionAndType >> 5 & 0x7);
        final int seqid = this.readVarint32();
        final String messageName = this.readString();
        return new TMessage(messageName, type, seqid);
    }
    
    @Override
    public TStruct readStructBegin() throws TException {
        this.lastField_.push(this.lastFieldId_);
        this.lastFieldId_ = 0;
        return TCompactProtocol.ANONYMOUS_STRUCT;
    }
    
    @Override
    public void readStructEnd() throws TException {
        this.lastFieldId_ = this.lastField_.pop();
    }
    
    @Override
    public TField readFieldBegin() throws TException {
        final byte type = this.readByte();
        if (type == 0) {
            return TCompactProtocol.TSTOP;
        }
        final short modifier = (short)((type & 0xF0) >> 4);
        short fieldId;
        if (modifier == 0) {
            fieldId = this.readI16();
        }
        else {
            fieldId = (short)(this.lastFieldId_ + modifier);
        }
        final TField field = new TField("", this.getTType((byte)(type & 0xF)), fieldId);
        if (this.isBoolType(type)) {
            this.boolValue_ = (((byte)(type & 0xF) == 1) ? Boolean.TRUE : Boolean.FALSE);
        }
        this.lastFieldId_ = field.id;
        return field;
    }
    
    @Override
    public TMap readMapBegin() throws TException {
        final int size = this.readVarint32();
        this.checkContainerReadLength(size);
        final byte keyAndValueType = (byte)((size == 0) ? 0 : this.readByte());
        return new TMap(this.getTType((byte)(keyAndValueType >> 4)), this.getTType((byte)(keyAndValueType & 0xF)), size);
    }
    
    @Override
    public TList readListBegin() throws TException {
        final byte size_and_type = this.readByte();
        int size = size_and_type >> 4 & 0xF;
        if (size == 15) {
            size = this.readVarint32();
        }
        this.checkContainerReadLength(size);
        final byte type = this.getTType(size_and_type);
        return new TList(type, size);
    }
    
    @Override
    public TSet readSetBegin() throws TException {
        return new TSet(this.readListBegin());
    }
    
    @Override
    public boolean readBool() throws TException {
        if (this.boolValue_ != null) {
            final boolean result = this.boolValue_;
            this.boolValue_ = null;
            return result;
        }
        return this.readByte() == 1;
    }
    
    @Override
    public byte readByte() throws TException {
        byte b;
        if (this.trans_.getBytesRemainingInBuffer() > 0) {
            b = this.trans_.getBuffer()[this.trans_.getBufferPosition()];
            this.trans_.consumeBuffer(1);
        }
        else {
            this.trans_.readAll(this.byteRawBuf, 0, 1);
            b = this.byteRawBuf[0];
        }
        return b;
    }
    
    @Override
    public short readI16() throws TException {
        return (short)this.zigzagToInt(this.readVarint32());
    }
    
    @Override
    public int readI32() throws TException {
        return this.zigzagToInt(this.readVarint32());
    }
    
    @Override
    public long readI64() throws TException {
        return this.zigzagToLong(this.readVarint64());
    }
    
    @Override
    public double readDouble() throws TException {
        final byte[] longBits = new byte[8];
        this.trans_.readAll(longBits, 0, 8);
        return Double.longBitsToDouble(this.bytesToLong(longBits));
    }
    
    @Override
    public String readString() throws TException {
        final int length = this.readVarint32();
        this.checkStringReadLength(length);
        if (length == 0) {
            return "";
        }
        try {
            if (this.trans_.getBytesRemainingInBuffer() >= length) {
                final String str = new String(this.trans_.getBuffer(), this.trans_.getBufferPosition(), length, "UTF-8");
                this.trans_.consumeBuffer(length);
                return str;
            }
            return new String(this.readBinary(length), "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new TException("UTF-8 not supported!");
        }
    }
    
    @Override
    public ByteBuffer readBinary() throws TException {
        final int length = this.readVarint32();
        this.checkStringReadLength(length);
        if (length == 0) {
            return ByteBuffer.wrap(new byte[0]);
        }
        if (this.trans_.getBytesRemainingInBuffer() >= length) {
            final ByteBuffer bb = ByteBuffer.wrap(this.trans_.getBuffer(), this.trans_.getBufferPosition(), length);
            this.trans_.consumeBuffer(length);
            return bb;
        }
        final byte[] buf = new byte[length];
        this.trans_.readAll(buf, 0, length);
        return ByteBuffer.wrap(buf);
    }
    
    private byte[] readBinary(final int length) throws TException {
        if (length == 0) {
            return new byte[0];
        }
        final byte[] buf = new byte[length];
        this.trans_.readAll(buf, 0, length);
        return buf;
    }
    
    private void checkStringReadLength(final int length) throws TProtocolException {
        if (length < 0) {
            throw new TProtocolException(2, "Negative length: " + length);
        }
        if (this.stringLengthLimit_ != -1L && length > this.stringLengthLimit_) {
            throw new TProtocolException(3, "Length exceeded max allowed: " + length);
        }
    }
    
    private void checkContainerReadLength(final int length) throws TProtocolException {
        if (length < 0) {
            throw new TProtocolException(2, "Negative length: " + length);
        }
        if (this.containerLengthLimit_ != -1L && length > this.containerLengthLimit_) {
            throw new TProtocolException(3, "Length exceeded max allowed: " + length);
        }
    }
    
    @Override
    public void readMessageEnd() throws TException {
    }
    
    @Override
    public void readFieldEnd() throws TException {
    }
    
    @Override
    public void readMapEnd() throws TException {
    }
    
    @Override
    public void readListEnd() throws TException {
    }
    
    @Override
    public void readSetEnd() throws TException {
    }
    
    private int readVarint32() throws TException {
        int result = 0;
        int shift = 0;
        if (this.trans_.getBytesRemainingInBuffer() >= 5) {
            final byte[] buf = this.trans_.getBuffer();
            final int pos = this.trans_.getBufferPosition();
            int off = 0;
            while (true) {
                final byte b = buf[pos + off];
                result |= (b & 0x7F) << shift;
                if ((b & 0x80) != 0x80) {
                    break;
                }
                shift += 7;
                ++off;
            }
            this.trans_.consumeBuffer(off + 1);
        }
        else {
            while (true) {
                final byte b2 = this.readByte();
                result |= (b2 & 0x7F) << shift;
                if ((b2 & 0x80) != 0x80) {
                    break;
                }
                shift += 7;
            }
        }
        return result;
    }
    
    private long readVarint64() throws TException {
        int shift = 0;
        long result = 0L;
        if (this.trans_.getBytesRemainingInBuffer() >= 10) {
            final byte[] buf = this.trans_.getBuffer();
            final int pos = this.trans_.getBufferPosition();
            int off = 0;
            while (true) {
                final byte b = buf[pos + off];
                result |= (long)(b & 0x7F) << shift;
                if ((b & 0x80) != 0x80) {
                    break;
                }
                shift += 7;
                ++off;
            }
            this.trans_.consumeBuffer(off + 1);
        }
        else {
            while (true) {
                final byte b2 = this.readByte();
                result |= (long)(b2 & 0x7F) << shift;
                if ((b2 & 0x80) != 0x80) {
                    break;
                }
                shift += 7;
            }
        }
        return result;
    }
    
    private int zigzagToInt(final int n) {
        return n >>> 1 ^ -(n & 0x1);
    }
    
    private long zigzagToLong(final long n) {
        return n >>> 1 ^ -(n & 0x1L);
    }
    
    private long bytesToLong(final byte[] bytes) {
        return ((long)bytes[7] & 0xFFL) << 56 | ((long)bytes[6] & 0xFFL) << 48 | ((long)bytes[5] & 0xFFL) << 40 | ((long)bytes[4] & 0xFFL) << 32 | ((long)bytes[3] & 0xFFL) << 24 | ((long)bytes[2] & 0xFFL) << 16 | ((long)bytes[1] & 0xFFL) << 8 | ((long)bytes[0] & 0xFFL);
    }
    
    private boolean isBoolType(final byte b) {
        final int lowerNibble = b & 0xF;
        return lowerNibble == 1 || lowerNibble == 2;
    }
    
    private byte getTType(final byte type) throws TProtocolException {
        switch ((byte)(type & 0xF)) {
            case 0: {
                return 0;
            }
            case 1:
            case 2: {
                return 2;
            }
            case 3: {
                return 3;
            }
            case 4: {
                return 6;
            }
            case 5: {
                return 8;
            }
            case 6: {
                return 10;
            }
            case 7: {
                return 4;
            }
            case 8: {
                return 11;
            }
            case 9: {
                return 15;
            }
            case 10: {
                return 14;
            }
            case 11: {
                return 13;
            }
            case 12: {
                return 12;
            }
            default: {
                throw new TProtocolException("don't know what type: " + (byte)(type & 0xF));
            }
        }
    }
    
    private byte getCompactType(final byte ttype) {
        return TCompactProtocol.ttypeToCompactType[ttype];
    }
    
    static {
        ANONYMOUS_STRUCT = new TStruct("");
        TSTOP = new TField("", (byte)0, (short)0);
        (ttypeToCompactType = new byte[16])[0] = 0;
        TCompactProtocol.ttypeToCompactType[2] = 1;
        TCompactProtocol.ttypeToCompactType[3] = 3;
        TCompactProtocol.ttypeToCompactType[6] = 4;
        TCompactProtocol.ttypeToCompactType[8] = 5;
        TCompactProtocol.ttypeToCompactType[10] = 6;
        TCompactProtocol.ttypeToCompactType[4] = 7;
        TCompactProtocol.ttypeToCompactType[11] = 8;
        TCompactProtocol.ttypeToCompactType[15] = 9;
        TCompactProtocol.ttypeToCompactType[14] = 10;
        TCompactProtocol.ttypeToCompactType[13] = 11;
        TCompactProtocol.ttypeToCompactType[12] = 12;
    }
    
    public static class Factory implements TProtocolFactory
    {
        private final long stringLengthLimit_;
        private final long containerLengthLimit_;
        
        public Factory() {
            this(-1L, -1L);
        }
        
        public Factory(final long stringLengthLimit) {
            this(stringLengthLimit, -1L);
        }
        
        public Factory(final long stringLengthLimit, final long containerLengthLimit) {
            this.containerLengthLimit_ = containerLengthLimit;
            this.stringLengthLimit_ = stringLengthLimit;
        }
        
        public TProtocol getProtocol(final TTransport trans) {
            return new TCompactProtocol(trans, this.stringLengthLimit_, this.containerLengthLimit_);
        }
    }
    
    private static class Types
    {
        public static final byte BOOLEAN_TRUE = 1;
        public static final byte BOOLEAN_FALSE = 2;
        public static final byte BYTE = 3;
        public static final byte I16 = 4;
        public static final byte I32 = 5;
        public static final byte I64 = 6;
        public static final byte DOUBLE = 7;
        public static final byte BINARY = 8;
        public static final byte LIST = 9;
        public static final byte SET = 10;
        public static final byte MAP = 11;
        public static final byte STRUCT = 12;
    }
}
