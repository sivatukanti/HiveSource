// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift;

import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.TException;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import org.apache.commons.logging.Log;
import org.apache.thrift.protocol.TProtocol;

public class TBinarySortableProtocol extends TProtocol implements ConfigurableTProtocol, WriteNullsProtocol, WriteTextProtocol
{
    static final Log LOG;
    static byte ORDERED_TYPE;
    int stackLevel;
    int topLevelStructFieldID;
    String sortOrder;
    boolean ascending;
    byte[] rawBytesBuffer;
    private final byte[] bout;
    private final byte[] i16out;
    private final byte[] i32out;
    private final byte[] i64out;
    protected final byte[] nullByte;
    protected final byte[] nonNullByte;
    protected final byte[] escapedNull;
    protected final byte[] escapedOne;
    TStruct tstruct;
    TField f;
    private TMap tmap;
    private TList tlist;
    private TSet set;
    private final byte[] wasNull;
    private final byte[] bin;
    private final byte[] i16rd;
    private final byte[] i32rd;
    private final byte[] i64rd;
    private byte[] stringBytes;
    boolean lastPrimitiveWasNull;
    
    public TBinarySortableProtocol(final TTransport trans) {
        super(trans);
        this.bout = new byte[1];
        this.i16out = new byte[2];
        this.i32out = new byte[4];
        this.i64out = new byte[8];
        this.nullByte = new byte[] { 0 };
        this.nonNullByte = new byte[] { 1 };
        this.escapedNull = new byte[] { 1, 1 };
        this.escapedOne = new byte[] { 1, 2 };
        this.tstruct = new TStruct();
        this.f = null;
        this.tmap = null;
        this.tlist = null;
        this.set = null;
        this.wasNull = new byte[1];
        this.bin = new byte[1];
        this.i16rd = new byte[2];
        this.i32rd = new byte[4];
        this.i64rd = new byte[8];
        this.stringBytes = new byte[1000];
        this.stackLevel = 0;
    }
    
    @Override
    public void initialize(final Configuration conf, final Properties tbl) throws TException {
        this.sortOrder = tbl.getProperty("serialization.sort.order");
        if (this.sortOrder == null) {
            this.sortOrder = "";
        }
        for (int i = 0; i < this.sortOrder.length(); ++i) {
            final char c = this.sortOrder.charAt(i);
            if (c != '+' && c != '-') {
                throw new TException("serialization.sort.order should be a string consists of only '+' and '-'!");
            }
        }
        TBinarySortableProtocol.LOG.info("Sort order is \"" + this.sortOrder + "\"");
    }
    
    @Override
    public void writeMessageBegin(final TMessage message) throws TException {
    }
    
    @Override
    public void writeMessageEnd() throws TException {
    }
    
    @Override
    public void writeStructBegin(final TStruct struct) throws TException {
        ++this.stackLevel;
        if (this.stackLevel == 1) {
            this.topLevelStructFieldID = 0;
            this.ascending = (this.topLevelStructFieldID >= this.sortOrder.length() || this.sortOrder.charAt(this.topLevelStructFieldID) != '-');
        }
        else {
            this.writeRawBytes(this.nonNullByte, 0, 1);
        }
    }
    
    @Override
    public void writeStructEnd() throws TException {
        --this.stackLevel;
    }
    
    @Override
    public void writeFieldBegin(final TField field) throws TException {
    }
    
    @Override
    public void writeFieldEnd() throws TException {
        if (this.stackLevel == 1) {
            ++this.topLevelStructFieldID;
            this.ascending = (this.topLevelStructFieldID >= this.sortOrder.length() || this.sortOrder.charAt(this.topLevelStructFieldID) != '-');
        }
    }
    
    @Override
    public void writeFieldStop() {
    }
    
    @Override
    public void writeMapBegin(final TMap map) throws TException {
        ++this.stackLevel;
        if (map == null) {
            this.writeRawBytes(this.nonNullByte, 0, 1);
        }
        else {
            this.writeI32(map.size);
        }
    }
    
    @Override
    public void writeMapEnd() throws TException {
        --this.stackLevel;
    }
    
    @Override
    public void writeListBegin(final TList list) throws TException {
        ++this.stackLevel;
        if (list == null) {
            this.writeRawBytes(this.nonNullByte, 0, 1);
        }
        else {
            this.writeI32(list.size);
        }
    }
    
    @Override
    public void writeListEnd() throws TException {
        --this.stackLevel;
    }
    
    @Override
    public void writeSetBegin(final TSet set) throws TException {
        ++this.stackLevel;
        if (set == null) {
            this.writeRawBytes(this.nonNullByte, 0, 1);
        }
        else {
            this.writeI32(set.size);
        }
    }
    
    @Override
    public void writeSetEnd() throws TException {
        --this.stackLevel;
    }
    
    private void writeRawBytes(final byte[] bytes, final int begin, final int length) throws TException {
        if (this.ascending) {
            this.trans_.write(bytes, begin, length);
        }
        else {
            if (this.rawBytesBuffer == null || this.rawBytesBuffer.length < bytes.length) {
                this.rawBytesBuffer = new byte[bytes.length];
            }
            for (int i = begin; i < begin + length; ++i) {
                this.rawBytesBuffer[i] = (byte)~bytes[i];
            }
            this.trans_.write(this.rawBytesBuffer, begin, length);
        }
    }
    
    @Override
    public void writeBool(final boolean b) throws TException {
        this.bout[0] = (byte)(b ? 2 : 1);
        this.writeRawBytes(this.bout, 0, 1);
    }
    
    @Override
    public void writeByte(final byte b) throws TException {
        this.writeRawBytes(this.nonNullByte, 0, 1);
        this.bout[0] = (byte)(b ^ 0x80);
        this.writeRawBytes(this.bout, 0, 1);
    }
    
    @Override
    public void writeI16(final short i16) throws TException {
        this.i16out[0] = (byte)(0xFF & (i16 >> 8 ^ 0x80));
        this.i16out[1] = (byte)(0xFF & i16);
        this.writeRawBytes(this.nonNullByte, 0, 1);
        this.writeRawBytes(this.i16out, 0, 2);
    }
    
    @Override
    public void writeI32(final int i32) throws TException {
        this.i32out[0] = (byte)(0xFF & (i32 >> 24 ^ 0x80));
        this.i32out[1] = (byte)(0xFF & i32 >> 16);
        this.i32out[2] = (byte)(0xFF & i32 >> 8);
        this.i32out[3] = (byte)(0xFF & i32);
        this.writeRawBytes(this.nonNullByte, 0, 1);
        this.writeRawBytes(this.i32out, 0, 4);
    }
    
    @Override
    public void writeI64(final long i64) throws TException {
        this.i64out[0] = (byte)(0xFFL & (i64 >> 56 ^ 0x80L));
        this.i64out[1] = (byte)(0xFFL & i64 >> 48);
        this.i64out[2] = (byte)(0xFFL & i64 >> 40);
        this.i64out[3] = (byte)(0xFFL & i64 >> 32);
        this.i64out[4] = (byte)(0xFFL & i64 >> 24);
        this.i64out[5] = (byte)(0xFFL & i64 >> 16);
        this.i64out[6] = (byte)(0xFFL & i64 >> 8);
        this.i64out[7] = (byte)(0xFFL & i64);
        this.writeRawBytes(this.nonNullByte, 0, 1);
        this.writeRawBytes(this.i64out, 0, 8);
    }
    
    @Override
    public void writeDouble(final double dub) throws TException {
        final long i64 = Double.doubleToLongBits(dub);
        if ((i64 & Long.MIN_VALUE) != 0x0L) {
            this.i64out[0] = (byte)(0xFFL & (i64 >> 56 ^ 0xFFL));
            this.i64out[1] = (byte)(0xFFL & (i64 >> 48 ^ 0xFFL));
            this.i64out[2] = (byte)(0xFFL & (i64 >> 40 ^ 0xFFL));
            this.i64out[3] = (byte)(0xFFL & (i64 >> 32 ^ 0xFFL));
            this.i64out[4] = (byte)(0xFFL & (i64 >> 24 ^ 0xFFL));
            this.i64out[5] = (byte)(0xFFL & (i64 >> 16 ^ 0xFFL));
            this.i64out[6] = (byte)(0xFFL & (i64 >> 8 ^ 0xFFL));
            this.i64out[7] = (byte)(0xFFL & (i64 ^ 0xFFL));
        }
        else {
            this.i64out[0] = (byte)(0xFFL & (i64 >> 56 ^ 0x80L));
            this.i64out[1] = (byte)(0xFFL & i64 >> 48);
            this.i64out[2] = (byte)(0xFFL & i64 >> 40);
            this.i64out[3] = (byte)(0xFFL & i64 >> 32);
            this.i64out[4] = (byte)(0xFFL & i64 >> 24);
            this.i64out[5] = (byte)(0xFFL & i64 >> 16);
            this.i64out[6] = (byte)(0xFFL & i64 >> 8);
            this.i64out[7] = (byte)(0xFFL & i64);
        }
        this.writeRawBytes(this.nonNullByte, 0, 1);
        this.writeRawBytes(this.i64out, 0, 8);
    }
    
    @Override
    public void writeString(final String str) throws TException {
        byte[] dat;
        try {
            dat = str.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8: ", uex);
        }
        this.writeTextBytes(dat, 0, dat.length);
    }
    
    @Override
    public void writeBinary(final ByteBuffer bin) throws TException {
        if (bin == null) {
            this.writeRawBytes(this.nullByte, 0, 1);
            return;
        }
        final int length = bin.limit() - bin.position() - bin.arrayOffset();
        if (bin.hasArray()) {
            this.writeBinary(bin.array(), bin.arrayOffset() + bin.position(), length);
        }
        else {
            final byte[] copy = new byte[length];
            bin.get(copy);
            this.writeBinary(copy);
        }
    }
    
    public void writeBinary(final byte[] bin) throws TException {
        if (bin == null) {
            this.writeRawBytes(this.nullByte, 0, 1);
        }
        else {
            this.writeBinary(bin, 0, bin.length);
        }
    }
    
    public void writeBinary(final byte[] bin, final int offset, final int length) throws TException {
        if (bin == null) {
            this.writeRawBytes(this.nullByte, 0, 1);
        }
        else {
            this.writeI32(length);
            this.writeRawBytes(bin, offset, length);
        }
    }
    
    @Override
    public TMessage readMessageBegin() throws TException {
        return new TMessage();
    }
    
    @Override
    public void readMessageEnd() throws TException {
    }
    
    @Override
    public TStruct readStructBegin() throws TException {
        ++this.stackLevel;
        if (this.stackLevel == 1) {
            this.topLevelStructFieldID = 0;
            this.ascending = (this.topLevelStructFieldID >= this.sortOrder.length() || this.sortOrder.charAt(this.topLevelStructFieldID) != '-');
        }
        else if (this.readIsNull()) {
            return null;
        }
        return this.tstruct;
    }
    
    @Override
    public void readStructEnd() throws TException {
        --this.stackLevel;
    }
    
    @Override
    public TField readFieldBegin() throws TException {
        return this.f = new TField("", TBinarySortableProtocol.ORDERED_TYPE, (short)(-1));
    }
    
    @Override
    public void readFieldEnd() throws TException {
        if (this.stackLevel == 1) {
            ++this.topLevelStructFieldID;
            this.ascending = (this.topLevelStructFieldID >= this.sortOrder.length() || this.sortOrder.charAt(this.topLevelStructFieldID) != '-');
        }
    }
    
    @Override
    public TMap readMapBegin() throws TException {
        ++this.stackLevel;
        this.tmap = new TMap(TBinarySortableProtocol.ORDERED_TYPE, TBinarySortableProtocol.ORDERED_TYPE, this.readI32());
        if (this.tmap.size == 0 && this.lastPrimitiveWasNull()) {
            return null;
        }
        return this.tmap;
    }
    
    @Override
    public void readMapEnd() throws TException {
        --this.stackLevel;
    }
    
    @Override
    public TList readListBegin() throws TException {
        ++this.stackLevel;
        this.tlist = new TList(TBinarySortableProtocol.ORDERED_TYPE, this.readI32());
        if (this.tlist.size == 0 && this.lastPrimitiveWasNull()) {
            return null;
        }
        return this.tlist;
    }
    
    @Override
    public void readListEnd() throws TException {
        --this.stackLevel;
    }
    
    @Override
    public TSet readSetBegin() throws TException {
        ++this.stackLevel;
        this.set = new TSet(TBinarySortableProtocol.ORDERED_TYPE, this.readI32());
        if (this.set.size == 0 && this.lastPrimitiveWasNull()) {
            return null;
        }
        return this.set;
    }
    
    @Override
    public void readSetEnd() throws TException {
        --this.stackLevel;
    }
    
    private int readRawAll(final byte[] buf, final int off, final int len) throws TException {
        final int bytes = this.trans_.readAll(buf, off, len);
        if (!this.ascending) {
            for (int i = off; i < off + bytes; ++i) {
                buf[i] ^= -1;
            }
        }
        return bytes;
    }
    
    @Override
    public boolean readBool() throws TException {
        this.readRawAll(this.bin, 0, 1);
        this.lastPrimitiveWasNull = (this.bin[0] == 0);
        return !this.lastPrimitiveWasNull && this.bin[0] == 2;
    }
    
    public final boolean readIsNull() throws TException {
        this.readRawAll(this.wasNull, 0, 1);
        return this.lastPrimitiveWasNull = (this.wasNull[0] == 0);
    }
    
    @Override
    public byte readByte() throws TException {
        if (this.readIsNull()) {
            return 0;
        }
        this.readRawAll(this.bin, 0, 1);
        return (byte)(this.bin[0] ^ 0x80);
    }
    
    @Override
    public short readI16() throws TException {
        if (this.readIsNull()) {
            return 0;
        }
        this.readRawAll(this.i16rd, 0, 2);
        return (short)(((this.i16rd[0] ^ 0x80) & 0xFF) << 8 | (this.i16rd[1] & 0xFF));
    }
    
    @Override
    public int readI32() throws TException {
        if (this.readIsNull()) {
            return 0;
        }
        this.readRawAll(this.i32rd, 0, 4);
        return ((this.i32rd[0] ^ 0x80) & 0xFF) << 24 | (this.i32rd[1] & 0xFF) << 16 | (this.i32rd[2] & 0xFF) << 8 | (this.i32rd[3] & 0xFF);
    }
    
    @Override
    public long readI64() throws TException {
        if (this.readIsNull()) {
            return 0L;
        }
        this.readRawAll(this.i64rd, 0, 8);
        return (long)((this.i64rd[0] ^ 0x80) & 0xFF) << 56 | (long)(this.i64rd[1] & 0xFF) << 48 | (long)(this.i64rd[2] & 0xFF) << 40 | (long)(this.i64rd[3] & 0xFF) << 32 | (long)(this.i64rd[4] & 0xFF) << 24 | (long)(this.i64rd[5] & 0xFF) << 16 | (long)(this.i64rd[6] & 0xFF) << 8 | (long)(this.i64rd[7] & 0xFF);
    }
    
    @Override
    public double readDouble() throws TException {
        if (this.readIsNull()) {
            return 0.0;
        }
        this.readRawAll(this.i64rd, 0, 8);
        long v = 0L;
        if ((this.i64rd[0] & 0x80) != 0x0) {
            v = ((long)((this.i64rd[0] ^ 0x80) & 0xFF) << 56 | (long)(this.i64rd[1] & 0xFF) << 48 | (long)(this.i64rd[2] & 0xFF) << 40 | (long)(this.i64rd[3] & 0xFF) << 32 | (long)(this.i64rd[4] & 0xFF) << 24 | (long)(this.i64rd[5] & 0xFF) << 16 | (long)(this.i64rd[6] & 0xFF) << 8 | (long)(this.i64rd[7] & 0xFF));
        }
        else {
            v = ((long)((this.i64rd[0] ^ 0xFF) & 0xFF) << 56 | (long)((this.i64rd[1] ^ 0xFF) & 0xFF) << 48 | (long)((this.i64rd[2] ^ 0xFF) & 0xFF) << 40 | (long)((this.i64rd[3] ^ 0xFF) & 0xFF) << 32 | (long)((this.i64rd[4] ^ 0xFF) & 0xFF) << 24 | (long)((this.i64rd[5] ^ 0xFF) & 0xFF) << 16 | (long)((this.i64rd[6] ^ 0xFF) & 0xFF) << 8 | (long)((this.i64rd[7] ^ 0xFF) & 0xFF));
        }
        return Double.longBitsToDouble(v);
    }
    
    @Override
    public String readString() throws TException {
        if (this.readIsNull()) {
            return null;
        }
        int i = 0;
        while (true) {
            this.readRawAll(this.bin, 0, 1);
            if (this.bin[0] == 0) {
                try {
                    final String r = new String(this.stringBytes, 0, i, "UTF-8");
                    return r;
                }
                catch (UnsupportedEncodingException uex) {
                    throw new TException("JVM DOES NOT SUPPORT UTF-8: ", uex);
                }
            }
            else {
                if (this.bin[0] == 1) {
                    this.readRawAll(this.bin, 0, 1);
                    assert this.bin[0] == 2;
                    --this.bin[0];
                }
                if (i == this.stringBytes.length) {
                    this.stringBytes = Arrays.copyOf(this.stringBytes, this.stringBytes.length * 2);
                }
                this.stringBytes[i] = this.bin[0];
                ++i;
            }
        }
    }
    
    @Override
    public ByteBuffer readBinary() throws TException {
        final int size = this.readI32();
        if (this.lastPrimitiveWasNull) {
            return null;
        }
        final byte[] buf = new byte[size];
        this.readRawAll(buf, 0, size);
        return ByteBuffer.wrap(buf);
    }
    
    @Override
    public boolean lastPrimitiveWasNull() throws TException {
        return this.lastPrimitiveWasNull;
    }
    
    @Override
    public void writeNull() throws TException {
        this.writeRawBytes(this.nullByte, 0, 1);
    }
    
    void writeTextBytes(final byte[] bytes, final int start, final int length) throws TException {
        this.writeRawBytes(this.nonNullByte, 0, 1);
        int begin = 0;
        int i;
        for (i = start; i < length; ++i) {
            if (bytes[i] == 0 || bytes[i] == 1) {
                if (i > begin) {
                    this.writeRawBytes(bytes, begin, i - begin);
                }
                if (bytes[i] == 0) {
                    this.writeRawBytes(this.escapedNull, 0, this.escapedNull.length);
                }
                else {
                    this.writeRawBytes(this.escapedOne, 0, this.escapedOne.length);
                }
                begin = i + 1;
            }
        }
        if (i > begin) {
            this.writeRawBytes(bytes, begin, i - begin);
        }
        this.writeRawBytes(this.nullByte, 0, 1);
    }
    
    @Override
    public void writeText(final Text text) throws TException {
        this.writeTextBytes(text.getBytes(), 0, text.getLength());
    }
    
    static {
        LOG = LogFactory.getLog(TBinarySortableProtocol.class.getName());
        TBinarySortableProtocol.ORDERED_TYPE = -1;
    }
    
    public static class Factory implements TProtocolFactory
    {
        @Override
        public TProtocol getProtocol(final TTransport trans) {
            return new TBinarySortableProtocol(trans);
        }
    }
}
