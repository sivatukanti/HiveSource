// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.protocol;

import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.transport.TTransport;

public class TBinaryProtocol extends TProtocol
{
    private static final TStruct ANONYMOUS_STRUCT;
    protected static final int VERSION_MASK = -65536;
    protected static final int VERSION_1 = -2147418112;
    protected boolean strictRead_;
    protected boolean strictWrite_;
    protected int readLength_;
    protected boolean checkReadLength_;
    private byte[] bout;
    private byte[] i16out;
    private byte[] i32out;
    private byte[] i64out;
    private byte[] bin;
    private byte[] i16rd;
    private byte[] i32rd;
    private byte[] i64rd;
    
    public TBinaryProtocol(final TTransport trans) {
        this(trans, false, true);
    }
    
    public TBinaryProtocol(final TTransport trans, final boolean strictRead, final boolean strictWrite) {
        super(trans);
        this.strictRead_ = false;
        this.strictWrite_ = true;
        this.checkReadLength_ = false;
        this.bout = new byte[1];
        this.i16out = new byte[2];
        this.i32out = new byte[4];
        this.i64out = new byte[8];
        this.bin = new byte[1];
        this.i16rd = new byte[2];
        this.i32rd = new byte[4];
        this.i64rd = new byte[8];
        this.strictRead_ = strictRead;
        this.strictWrite_ = strictWrite;
    }
    
    @Override
    public void writeMessageBegin(final TMessage message) throws TException {
        if (this.strictWrite_) {
            final int version = 0x80010000 | message.type;
            this.writeI32(version);
            this.writeString(message.name);
            this.writeI32(message.seqid);
        }
        else {
            this.writeString(message.name);
            this.writeByte(message.type);
            this.writeI32(message.seqid);
        }
    }
    
    @Override
    public void writeMessageEnd() {
    }
    
    @Override
    public void writeStructBegin(final TStruct struct) {
    }
    
    @Override
    public void writeStructEnd() {
    }
    
    @Override
    public void writeFieldBegin(final TField field) throws TException {
        this.writeByte(field.type);
        this.writeI16(field.id);
    }
    
    @Override
    public void writeFieldEnd() {
    }
    
    @Override
    public void writeFieldStop() throws TException {
        this.writeByte((byte)0);
    }
    
    @Override
    public void writeMapBegin(final TMap map) throws TException {
        this.writeByte(map.keyType);
        this.writeByte(map.valueType);
        this.writeI32(map.size);
    }
    
    @Override
    public void writeMapEnd() {
    }
    
    @Override
    public void writeListBegin(final TList list) throws TException {
        this.writeByte(list.elemType);
        this.writeI32(list.size);
    }
    
    @Override
    public void writeListEnd() {
    }
    
    @Override
    public void writeSetBegin(final TSet set) throws TException {
        this.writeByte(set.elemType);
        this.writeI32(set.size);
    }
    
    @Override
    public void writeSetEnd() {
    }
    
    @Override
    public void writeBool(final boolean b) throws TException {
        this.writeByte((byte)(b ? 1 : 0));
    }
    
    @Override
    public void writeByte(final byte b) throws TException {
        this.bout[0] = b;
        this.trans_.write(this.bout, 0, 1);
    }
    
    @Override
    public void writeI16(final short i16) throws TException {
        this.i16out[0] = (byte)(0xFF & i16 >> 8);
        this.i16out[1] = (byte)(0xFF & i16);
        this.trans_.write(this.i16out, 0, 2);
    }
    
    @Override
    public void writeI32(final int i32) throws TException {
        this.i32out[0] = (byte)(0xFF & i32 >> 24);
        this.i32out[1] = (byte)(0xFF & i32 >> 16);
        this.i32out[2] = (byte)(0xFF & i32 >> 8);
        this.i32out[3] = (byte)(0xFF & i32);
        this.trans_.write(this.i32out, 0, 4);
    }
    
    @Override
    public void writeI64(final long i64) throws TException {
        this.i64out[0] = (byte)(0xFFL & i64 >> 56);
        this.i64out[1] = (byte)(0xFFL & i64 >> 48);
        this.i64out[2] = (byte)(0xFFL & i64 >> 40);
        this.i64out[3] = (byte)(0xFFL & i64 >> 32);
        this.i64out[4] = (byte)(0xFFL & i64 >> 24);
        this.i64out[5] = (byte)(0xFFL & i64 >> 16);
        this.i64out[6] = (byte)(0xFFL & i64 >> 8);
        this.i64out[7] = (byte)(0xFFL & i64);
        this.trans_.write(this.i64out, 0, 8);
    }
    
    @Override
    public void writeDouble(final double dub) throws TException {
        this.writeI64(Double.doubleToLongBits(dub));
    }
    
    @Override
    public void writeString(final String str) throws TException {
        try {
            final byte[] dat = str.getBytes("UTF-8");
            this.writeI32(dat.length);
            this.trans_.write(dat, 0, dat.length);
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
    }
    
    @Override
    public void writeBinary(final ByteBuffer bin) throws TException {
        final int length = bin.limit() - bin.position();
        this.writeI32(length);
        this.trans_.write(bin.array(), bin.position() + bin.arrayOffset(), length);
    }
    
    @Override
    public TMessage readMessageBegin() throws TException {
        final int size = this.readI32();
        if (size < 0) {
            final int version = size & 0xFFFF0000;
            if (version != -2147418112) {
                throw new TProtocolException(4, "Bad version in readMessageBegin");
            }
            return new TMessage(this.readString(), (byte)(size & 0xFF), this.readI32());
        }
        else {
            if (this.strictRead_) {
                throw new TProtocolException(4, "Missing version in readMessageBegin, old client?");
            }
            return new TMessage(this.readStringBody(size), this.readByte(), this.readI32());
        }
    }
    
    @Override
    public void readMessageEnd() {
    }
    
    @Override
    public TStruct readStructBegin() {
        return TBinaryProtocol.ANONYMOUS_STRUCT;
    }
    
    @Override
    public void readStructEnd() {
    }
    
    @Override
    public TField readFieldBegin() throws TException {
        final byte type = this.readByte();
        final short id = (short)((type == 0) ? 0 : this.readI16());
        return new TField("", type, id);
    }
    
    @Override
    public void readFieldEnd() {
    }
    
    @Override
    public TMap readMapBegin() throws TException {
        return new TMap(this.readByte(), this.readByte(), this.readI32());
    }
    
    @Override
    public void readMapEnd() {
    }
    
    @Override
    public TList readListBegin() throws TException {
        return new TList(this.readByte(), this.readI32());
    }
    
    @Override
    public void readListEnd() {
    }
    
    @Override
    public TSet readSetBegin() throws TException {
        return new TSet(this.readByte(), this.readI32());
    }
    
    @Override
    public void readSetEnd() {
    }
    
    @Override
    public boolean readBool() throws TException {
        return this.readByte() == 1;
    }
    
    @Override
    public byte readByte() throws TException {
        if (this.trans_.getBytesRemainingInBuffer() >= 1) {
            final byte b = this.trans_.getBuffer()[this.trans_.getBufferPosition()];
            this.trans_.consumeBuffer(1);
            return b;
        }
        this.readAll(this.bin, 0, 1);
        return this.bin[0];
    }
    
    @Override
    public short readI16() throws TException {
        byte[] buf = this.i16rd;
        int off = 0;
        if (this.trans_.getBytesRemainingInBuffer() >= 2) {
            buf = this.trans_.getBuffer();
            off = this.trans_.getBufferPosition();
            this.trans_.consumeBuffer(2);
        }
        else {
            this.readAll(this.i16rd, 0, 2);
        }
        return (short)((buf[off] & 0xFF) << 8 | (buf[off + 1] & 0xFF));
    }
    
    @Override
    public int readI32() throws TException {
        byte[] buf = this.i32rd;
        int off = 0;
        if (this.trans_.getBytesRemainingInBuffer() >= 4) {
            buf = this.trans_.getBuffer();
            off = this.trans_.getBufferPosition();
            this.trans_.consumeBuffer(4);
        }
        else {
            this.readAll(this.i32rd, 0, 4);
        }
        return (buf[off] & 0xFF) << 24 | (buf[off + 1] & 0xFF) << 16 | (buf[off + 2] & 0xFF) << 8 | (buf[off + 3] & 0xFF);
    }
    
    @Override
    public long readI64() throws TException {
        byte[] buf = this.i64rd;
        int off = 0;
        if (this.trans_.getBytesRemainingInBuffer() >= 8) {
            buf = this.trans_.getBuffer();
            off = this.trans_.getBufferPosition();
            this.trans_.consumeBuffer(8);
        }
        else {
            this.readAll(this.i64rd, 0, 8);
        }
        return (long)(buf[off] & 0xFF) << 56 | (long)(buf[off + 1] & 0xFF) << 48 | (long)(buf[off + 2] & 0xFF) << 40 | (long)(buf[off + 3] & 0xFF) << 32 | (long)(buf[off + 4] & 0xFF) << 24 | (long)(buf[off + 5] & 0xFF) << 16 | (long)(buf[off + 6] & 0xFF) << 8 | (long)(buf[off + 7] & 0xFF);
    }
    
    @Override
    public double readDouble() throws TException {
        return Double.longBitsToDouble(this.readI64());
    }
    
    @Override
    public String readString() throws TException {
        final int size = this.readI32();
        if (this.trans_.getBytesRemainingInBuffer() >= size) {
            try {
                final String s = new String(this.trans_.getBuffer(), this.trans_.getBufferPosition(), size, "UTF-8");
                this.trans_.consumeBuffer(size);
                return s;
            }
            catch (UnsupportedEncodingException e) {
                throw new TException("JVM DOES NOT SUPPORT UTF-8");
            }
        }
        return this.readStringBody(size);
    }
    
    public String readStringBody(final int size) throws TException {
        try {
            this.checkReadLength(size);
            final byte[] buf = new byte[size];
            this.trans_.readAll(buf, 0, size);
            return new String(buf, "UTF-8");
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
    }
    
    @Override
    public ByteBuffer readBinary() throws TException {
        final int size = this.readI32();
        this.checkReadLength(size);
        if (this.trans_.getBytesRemainingInBuffer() >= size) {
            final ByteBuffer bb = ByteBuffer.wrap(this.trans_.getBuffer(), this.trans_.getBufferPosition(), size);
            this.trans_.consumeBuffer(size);
            return bb;
        }
        final byte[] buf = new byte[size];
        this.trans_.readAll(buf, 0, size);
        return ByteBuffer.wrap(buf);
    }
    
    private int readAll(final byte[] buf, final int off, final int len) throws TException {
        this.checkReadLength(len);
        return this.trans_.readAll(buf, off, len);
    }
    
    public void setReadLength(final int readLength) {
        this.readLength_ = readLength;
        this.checkReadLength_ = true;
    }
    
    protected void checkReadLength(final int length) throws TException {
        if (length < 0) {
            throw new TException("Negative length: " + length);
        }
        if (this.checkReadLength_) {
            this.readLength_ -= length;
            if (this.readLength_ < 0) {
                throw new TException("Message length exceeded: " + length);
            }
        }
    }
    
    static {
        ANONYMOUS_STRUCT = new TStruct();
    }
    
    public static class Factory implements TProtocolFactory
    {
        protected boolean strictRead_;
        protected boolean strictWrite_;
        protected int readLength_;
        
        public Factory() {
            this(false, true);
        }
        
        public Factory(final boolean strictRead, final boolean strictWrite) {
            this(strictRead, strictWrite, 0);
        }
        
        public Factory(final boolean strictRead, final boolean strictWrite, final int readLength) {
            this.strictRead_ = false;
            this.strictWrite_ = true;
            this.strictRead_ = strictRead;
            this.strictWrite_ = strictWrite;
            this.readLength_ = readLength;
        }
        
        public TProtocol getProtocol(final TTransport trans) {
            final TBinaryProtocol proto = new TBinaryProtocol(trans, this.strictRead_, this.strictWrite_);
            if (this.readLength_ != 0) {
                proto.setReadLength(this.readLength_);
            }
            return proto;
        }
    }
}
