// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.protocol;

import parquet.org.apache.thrift.TByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import parquet.org.apache.thrift.transport.TTransport;
import parquet.org.apache.thrift.TException;
import java.util.Stack;

public class TJSONProtocol extends TProtocol
{
    private static final byte[] COMMA;
    private static final byte[] COLON;
    private static final byte[] LBRACE;
    private static final byte[] RBRACE;
    private static final byte[] LBRACKET;
    private static final byte[] RBRACKET;
    private static final byte[] QUOTE;
    private static final byte[] BACKSLASH;
    private static final byte[] ZERO;
    private static final byte[] ESCSEQ;
    private static final long VERSION = 1L;
    private static final byte[] JSON_CHAR_TABLE;
    private static final String ESCAPE_CHARS = "\"\\bfnrt";
    private static final byte[] ESCAPE_CHAR_VALS;
    private static final int DEF_STRING_SIZE = 16;
    private static final byte[] NAME_BOOL;
    private static final byte[] NAME_BYTE;
    private static final byte[] NAME_I16;
    private static final byte[] NAME_I32;
    private static final byte[] NAME_I64;
    private static final byte[] NAME_DOUBLE;
    private static final byte[] NAME_STRUCT;
    private static final byte[] NAME_STRING;
    private static final byte[] NAME_MAP;
    private static final byte[] NAME_LIST;
    private static final byte[] NAME_SET;
    private static final TStruct ANONYMOUS_STRUCT;
    private Stack<JSONBaseContext> contextStack_;
    private JSONBaseContext context_;
    private LookaheadReader reader_;
    private byte[] tmpbuf_;
    
    private static final byte[] getTypeNameForTypeID(final byte typeID) throws TException {
        switch (typeID) {
            case 2: {
                return TJSONProtocol.NAME_BOOL;
            }
            case 3: {
                return TJSONProtocol.NAME_BYTE;
            }
            case 6: {
                return TJSONProtocol.NAME_I16;
            }
            case 8: {
                return TJSONProtocol.NAME_I32;
            }
            case 10: {
                return TJSONProtocol.NAME_I64;
            }
            case 4: {
                return TJSONProtocol.NAME_DOUBLE;
            }
            case 11: {
                return TJSONProtocol.NAME_STRING;
            }
            case 12: {
                return TJSONProtocol.NAME_STRUCT;
            }
            case 13: {
                return TJSONProtocol.NAME_MAP;
            }
            case 14: {
                return TJSONProtocol.NAME_SET;
            }
            case 15: {
                return TJSONProtocol.NAME_LIST;
            }
            default: {
                throw new TProtocolException(5, "Unrecognized type");
            }
        }
    }
    
    private static final byte getTypeIDForTypeName(final byte[] name) throws TException {
        byte result = 0;
        if (name.length > 1) {
            switch (name[0]) {
                case 100: {
                    result = 4;
                    break;
                }
                case 105: {
                    switch (name[1]) {
                        case 56: {
                            result = 3;
                            break;
                        }
                        case 49: {
                            result = 6;
                            break;
                        }
                        case 51: {
                            result = 8;
                            break;
                        }
                        case 54: {
                            result = 10;
                            break;
                        }
                    }
                    break;
                }
                case 108: {
                    result = 15;
                    break;
                }
                case 109: {
                    result = 13;
                    break;
                }
                case 114: {
                    result = 12;
                    break;
                }
                case 115: {
                    if (name[1] == 116) {
                        result = 11;
                        break;
                    }
                    if (name[1] == 101) {
                        result = 14;
                        break;
                    }
                    break;
                }
                case 116: {
                    result = 2;
                    break;
                }
            }
        }
        if (result == 0) {
            throw new TProtocolException(5, "Unrecognized type");
        }
        return result;
    }
    
    private void pushContext(final JSONBaseContext c) {
        this.contextStack_.push(this.context_);
        this.context_ = c;
    }
    
    private void popContext() {
        this.context_ = this.contextStack_.pop();
    }
    
    public TJSONProtocol(final TTransport trans) {
        super(trans);
        this.contextStack_ = new Stack<JSONBaseContext>();
        this.context_ = new JSONBaseContext();
        this.reader_ = new LookaheadReader();
        this.tmpbuf_ = new byte[4];
    }
    
    @Override
    public void reset() {
        this.contextStack_.clear();
        this.context_ = new JSONBaseContext();
        this.reader_ = new LookaheadReader();
    }
    
    protected void readJSONSyntaxChar(final byte[] b) throws TException {
        final byte ch = this.reader_.read();
        if (ch != b[0]) {
            throw new TProtocolException(1, "Unexpected character:" + (char)ch);
        }
    }
    
    private static final byte hexVal(final byte ch) throws TException {
        if (ch >= 48 && ch <= 57) {
            return (byte)((char)ch - '0');
        }
        if (ch >= 97 && ch <= 102) {
            return (byte)((char)ch - 'a');
        }
        throw new TProtocolException(1, "Expected hex character");
    }
    
    private static final byte hexChar(byte val) {
        val &= 0xF;
        if (val < 10) {
            return (byte)((char)val + '0');
        }
        return (byte)((char)val + 'a');
    }
    
    private void writeJSONString(final byte[] b) throws TException {
        this.context_.write();
        this.trans_.write(TJSONProtocol.QUOTE);
        for (int len = b.length, i = 0; i < len; ++i) {
            if ((b[i] & 0xFF) >= 48) {
                if (b[i] == TJSONProtocol.BACKSLASH[0]) {
                    this.trans_.write(TJSONProtocol.BACKSLASH);
                    this.trans_.write(TJSONProtocol.BACKSLASH);
                }
                else {
                    this.trans_.write(b, i, 1);
                }
            }
            else {
                this.tmpbuf_[0] = TJSONProtocol.JSON_CHAR_TABLE[b[i]];
                if (this.tmpbuf_[0] == 1) {
                    this.trans_.write(b, i, 1);
                }
                else if (this.tmpbuf_[0] > 1) {
                    this.trans_.write(TJSONProtocol.BACKSLASH);
                    this.trans_.write(this.tmpbuf_, 0, 1);
                }
                else {
                    this.trans_.write(TJSONProtocol.ESCSEQ);
                    this.tmpbuf_[0] = hexChar((byte)(b[i] >> 4));
                    this.tmpbuf_[1] = hexChar(b[i]);
                    this.trans_.write(this.tmpbuf_, 0, 2);
                }
            }
        }
        this.trans_.write(TJSONProtocol.QUOTE);
    }
    
    private void writeJSONInteger(final long num) throws TException {
        this.context_.write();
        final String str = Long.toString(num);
        final boolean escapeNum = this.context_.escapeNum();
        if (escapeNum) {
            this.trans_.write(TJSONProtocol.QUOTE);
        }
        try {
            final byte[] buf = str.getBytes("UTF-8");
            this.trans_.write(buf);
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
        if (escapeNum) {
            this.trans_.write(TJSONProtocol.QUOTE);
        }
    }
    
    private void writeJSONDouble(final double num) throws TException {
        this.context_.write();
        final String str = Double.toString(num);
        boolean special = false;
        switch (str.charAt(0)) {
            case 'I':
            case 'N': {
                special = true;
                break;
            }
            case '-': {
                if (str.charAt(1) == 'I') {
                    special = true;
                    break;
                }
                break;
            }
        }
        final boolean escapeNum = special || this.context_.escapeNum();
        if (escapeNum) {
            this.trans_.write(TJSONProtocol.QUOTE);
        }
        try {
            final byte[] b = str.getBytes("UTF-8");
            this.trans_.write(b, 0, b.length);
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
        if (escapeNum) {
            this.trans_.write(TJSONProtocol.QUOTE);
        }
    }
    
    private void writeJSONBase64(final byte[] b, final int offset, final int length) throws TException {
        this.context_.write();
        this.trans_.write(TJSONProtocol.QUOTE);
        int len = length;
        int off = offset;
        while (len >= 3) {
            TBase64Utils.encode(b, off, 3, this.tmpbuf_, 0);
            this.trans_.write(this.tmpbuf_, 0, 4);
            off += 3;
            len -= 3;
        }
        if (len > 0) {
            TBase64Utils.encode(b, off, len, this.tmpbuf_, 0);
            this.trans_.write(this.tmpbuf_, 0, len + 1);
        }
        this.trans_.write(TJSONProtocol.QUOTE);
    }
    
    private void writeJSONObjectStart() throws TException {
        this.context_.write();
        this.trans_.write(TJSONProtocol.LBRACE);
        this.pushContext(new JSONPairContext());
    }
    
    private void writeJSONObjectEnd() throws TException {
        this.popContext();
        this.trans_.write(TJSONProtocol.RBRACE);
    }
    
    private void writeJSONArrayStart() throws TException {
        this.context_.write();
        this.trans_.write(TJSONProtocol.LBRACKET);
        this.pushContext(new JSONListContext());
    }
    
    private void writeJSONArrayEnd() throws TException {
        this.popContext();
        this.trans_.write(TJSONProtocol.RBRACKET);
    }
    
    @Override
    public void writeMessageBegin(final TMessage message) throws TException {
        this.writeJSONArrayStart();
        this.writeJSONInteger(1L);
        try {
            final byte[] b = message.name.getBytes("UTF-8");
            this.writeJSONString(b);
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
        this.writeJSONInteger(message.type);
        this.writeJSONInteger(message.seqid);
    }
    
    @Override
    public void writeMessageEnd() throws TException {
        this.writeJSONArrayEnd();
    }
    
    @Override
    public void writeStructBegin(final TStruct struct) throws TException {
        this.writeJSONObjectStart();
    }
    
    @Override
    public void writeStructEnd() throws TException {
        this.writeJSONObjectEnd();
    }
    
    @Override
    public void writeFieldBegin(final TField field) throws TException {
        this.writeJSONInteger(field.id);
        this.writeJSONObjectStart();
        this.writeJSONString(getTypeNameForTypeID(field.type));
    }
    
    @Override
    public void writeFieldEnd() throws TException {
        this.writeJSONObjectEnd();
    }
    
    @Override
    public void writeFieldStop() {
    }
    
    @Override
    public void writeMapBegin(final TMap map) throws TException {
        this.writeJSONArrayStart();
        this.writeJSONString(getTypeNameForTypeID(map.keyType));
        this.writeJSONString(getTypeNameForTypeID(map.valueType));
        this.writeJSONInteger(map.size);
        this.writeJSONObjectStart();
    }
    
    @Override
    public void writeMapEnd() throws TException {
        this.writeJSONObjectEnd();
        this.writeJSONArrayEnd();
    }
    
    @Override
    public void writeListBegin(final TList list) throws TException {
        this.writeJSONArrayStart();
        this.writeJSONString(getTypeNameForTypeID(list.elemType));
        this.writeJSONInteger(list.size);
    }
    
    @Override
    public void writeListEnd() throws TException {
        this.writeJSONArrayEnd();
    }
    
    @Override
    public void writeSetBegin(final TSet set) throws TException {
        this.writeJSONArrayStart();
        this.writeJSONString(getTypeNameForTypeID(set.elemType));
        this.writeJSONInteger(set.size);
    }
    
    @Override
    public void writeSetEnd() throws TException {
        this.writeJSONArrayEnd();
    }
    
    @Override
    public void writeBool(final boolean b) throws TException {
        this.writeJSONInteger(b ? 1 : 0);
    }
    
    @Override
    public void writeByte(final byte b) throws TException {
        this.writeJSONInteger(b);
    }
    
    @Override
    public void writeI16(final short i16) throws TException {
        this.writeJSONInteger(i16);
    }
    
    @Override
    public void writeI32(final int i32) throws TException {
        this.writeJSONInteger(i32);
    }
    
    @Override
    public void writeI64(final long i64) throws TException {
        this.writeJSONInteger(i64);
    }
    
    @Override
    public void writeDouble(final double dub) throws TException {
        this.writeJSONDouble(dub);
    }
    
    @Override
    public void writeString(final String str) throws TException {
        try {
            final byte[] b = str.getBytes("UTF-8");
            this.writeJSONString(b);
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
    }
    
    @Override
    public void writeBinary(final ByteBuffer bin) throws TException {
        this.writeJSONBase64(bin.array(), bin.position() + bin.arrayOffset(), bin.limit() - bin.position() - bin.arrayOffset());
    }
    
    private TByteArrayOutputStream readJSONString(final boolean skipContext) throws TException {
        final TByteArrayOutputStream arr = new TByteArrayOutputStream(16);
        if (!skipContext) {
            this.context_.read();
        }
        this.readJSONSyntaxChar(TJSONProtocol.QUOTE);
        while (true) {
            byte ch = this.reader_.read();
            if (ch == TJSONProtocol.QUOTE[0]) {
                return arr;
            }
            if (ch == TJSONProtocol.ESCSEQ[0]) {
                ch = this.reader_.read();
                if (ch == TJSONProtocol.ESCSEQ[1]) {
                    this.readJSONSyntaxChar(TJSONProtocol.ZERO);
                    this.readJSONSyntaxChar(TJSONProtocol.ZERO);
                    this.trans_.readAll(this.tmpbuf_, 0, 2);
                    ch = (byte)((hexVal(this.tmpbuf_[0]) << 4) + hexVal(this.tmpbuf_[1]));
                }
                else {
                    final int off = "\"\\bfnrt".indexOf(ch);
                    if (off == -1) {
                        throw new TProtocolException(1, "Expected control char");
                    }
                    ch = TJSONProtocol.ESCAPE_CHAR_VALS[off];
                }
            }
            arr.write(ch);
        }
    }
    
    private boolean isJSONNumeric(final byte b) {
        switch (b) {
            case 43:
            case 45:
            case 46:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 69:
            case 101: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private String readJSONNumericChars() throws TException {
        final StringBuilder strbld = new StringBuilder();
        while (true) {
            final byte ch = this.reader_.peek();
            if (!this.isJSONNumeric(ch)) {
                break;
            }
            strbld.append((char)this.reader_.read());
        }
        return strbld.toString();
    }
    
    private long readJSONInteger() throws TException {
        this.context_.read();
        if (this.context_.escapeNum()) {
            this.readJSONSyntaxChar(TJSONProtocol.QUOTE);
        }
        final String str = this.readJSONNumericChars();
        if (this.context_.escapeNum()) {
            this.readJSONSyntaxChar(TJSONProtocol.QUOTE);
        }
        try {
            return Long.valueOf(str);
        }
        catch (NumberFormatException ex) {
            throw new TProtocolException(1, "Bad data encounted in numeric data");
        }
    }
    
    private double readJSONDouble() throws TException {
        this.context_.read();
        if (this.reader_.peek() == TJSONProtocol.QUOTE[0]) {
            final TByteArrayOutputStream arr = this.readJSONString(true);
            try {
                final double dub = Double.valueOf(arr.toString("UTF-8"));
                if (!this.context_.escapeNum() && !Double.isNaN(dub) && !Double.isInfinite(dub)) {
                    throw new TProtocolException(1, "Numeric data unexpectedly quoted");
                }
                return dub;
            }
            catch (UnsupportedEncodingException ex) {
                throw new TException("JVM DOES NOT SUPPORT UTF-8");
            }
        }
        if (this.context_.escapeNum()) {
            this.readJSONSyntaxChar(TJSONProtocol.QUOTE);
        }
        try {
            return Double.valueOf(this.readJSONNumericChars());
        }
        catch (NumberFormatException ex2) {
            throw new TProtocolException(1, "Bad data encounted in numeric data");
        }
    }
    
    private byte[] readJSONBase64() throws TException {
        final TByteArrayOutputStream arr = this.readJSONString(false);
        final byte[] b = arr.get();
        int len = arr.len();
        int off = 0;
        int size;
        for (size = 0; len >= 4; len -= 4, size += 3) {
            TBase64Utils.decode(b, off, 4, b, size);
            off += 4;
        }
        if (len > 1) {
            TBase64Utils.decode(b, off, len, b, size);
            size += len - 1;
        }
        final byte[] result = new byte[size];
        System.arraycopy(b, 0, result, 0, size);
        return result;
    }
    
    private void readJSONObjectStart() throws TException {
        this.context_.read();
        this.readJSONSyntaxChar(TJSONProtocol.LBRACE);
        this.pushContext(new JSONPairContext());
    }
    
    private void readJSONObjectEnd() throws TException {
        this.readJSONSyntaxChar(TJSONProtocol.RBRACE);
        this.popContext();
    }
    
    private void readJSONArrayStart() throws TException {
        this.context_.read();
        this.readJSONSyntaxChar(TJSONProtocol.LBRACKET);
        this.pushContext(new JSONListContext());
    }
    
    private void readJSONArrayEnd() throws TException {
        this.readJSONSyntaxChar(TJSONProtocol.RBRACKET);
        this.popContext();
    }
    
    @Override
    public TMessage readMessageBegin() throws TException {
        this.readJSONArrayStart();
        if (this.readJSONInteger() != 1L) {
            throw new TProtocolException(4, "Message contained bad version.");
        }
        String name;
        try {
            name = this.readJSONString(false).toString("UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
        final byte type = (byte)this.readJSONInteger();
        final int seqid = (int)this.readJSONInteger();
        return new TMessage(name, type, seqid);
    }
    
    @Override
    public void readMessageEnd() throws TException {
        this.readJSONArrayEnd();
    }
    
    @Override
    public TStruct readStructBegin() throws TException {
        this.readJSONObjectStart();
        return TJSONProtocol.ANONYMOUS_STRUCT;
    }
    
    @Override
    public void readStructEnd() throws TException {
        this.readJSONObjectEnd();
    }
    
    @Override
    public TField readFieldBegin() throws TException {
        final byte ch = this.reader_.peek();
        short id = 0;
        byte type;
        if (ch == TJSONProtocol.RBRACE[0]) {
            type = 0;
        }
        else {
            id = (short)this.readJSONInteger();
            this.readJSONObjectStart();
            type = getTypeIDForTypeName(this.readJSONString(false).get());
        }
        return new TField("", type, id);
    }
    
    @Override
    public void readFieldEnd() throws TException {
        this.readJSONObjectEnd();
    }
    
    @Override
    public TMap readMapBegin() throws TException {
        this.readJSONArrayStart();
        final byte keyType = getTypeIDForTypeName(this.readJSONString(false).get());
        final byte valueType = getTypeIDForTypeName(this.readJSONString(false).get());
        final int size = (int)this.readJSONInteger();
        this.readJSONObjectStart();
        return new TMap(keyType, valueType, size);
    }
    
    @Override
    public void readMapEnd() throws TException {
        this.readJSONObjectEnd();
        this.readJSONArrayEnd();
    }
    
    @Override
    public TList readListBegin() throws TException {
        this.readJSONArrayStart();
        final byte elemType = getTypeIDForTypeName(this.readJSONString(false).get());
        final int size = (int)this.readJSONInteger();
        return new TList(elemType, size);
    }
    
    @Override
    public void readListEnd() throws TException {
        this.readJSONArrayEnd();
    }
    
    @Override
    public TSet readSetBegin() throws TException {
        this.readJSONArrayStart();
        final byte elemType = getTypeIDForTypeName(this.readJSONString(false).get());
        final int size = (int)this.readJSONInteger();
        return new TSet(elemType, size);
    }
    
    @Override
    public void readSetEnd() throws TException {
        this.readJSONArrayEnd();
    }
    
    @Override
    public boolean readBool() throws TException {
        return this.readJSONInteger() != 0L;
    }
    
    @Override
    public byte readByte() throws TException {
        return (byte)this.readJSONInteger();
    }
    
    @Override
    public short readI16() throws TException {
        return (short)this.readJSONInteger();
    }
    
    @Override
    public int readI32() throws TException {
        return (int)this.readJSONInteger();
    }
    
    @Override
    public long readI64() throws TException {
        return this.readJSONInteger();
    }
    
    @Override
    public double readDouble() throws TException {
        return this.readJSONDouble();
    }
    
    @Override
    public String readString() throws TException {
        try {
            return this.readJSONString(false).toString("UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
    }
    
    @Override
    public ByteBuffer readBinary() throws TException {
        return ByteBuffer.wrap(this.readJSONBase64());
    }
    
    static {
        COMMA = new byte[] { 44 };
        COLON = new byte[] { 58 };
        LBRACE = new byte[] { 123 };
        RBRACE = new byte[] { 125 };
        LBRACKET = new byte[] { 91 };
        RBRACKET = new byte[] { 93 };
        QUOTE = new byte[] { 34 };
        BACKSLASH = new byte[] { 92 };
        ZERO = new byte[] { 48 };
        ESCSEQ = new byte[] { 92, 117, 48, 48 };
        JSON_CHAR_TABLE = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 98, 116, 110, 0, 102, 114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 34, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        ESCAPE_CHAR_VALS = new byte[] { 34, 92, 8, 12, 10, 13, 9 };
        NAME_BOOL = new byte[] { 116, 102 };
        NAME_BYTE = new byte[] { 105, 56 };
        NAME_I16 = new byte[] { 105, 49, 54 };
        NAME_I32 = new byte[] { 105, 51, 50 };
        NAME_I64 = new byte[] { 105, 54, 52 };
        NAME_DOUBLE = new byte[] { 100, 98, 108 };
        NAME_STRUCT = new byte[] { 114, 101, 99 };
        NAME_STRING = new byte[] { 115, 116, 114 };
        NAME_MAP = new byte[] { 109, 97, 112 };
        NAME_LIST = new byte[] { 108, 115, 116 };
        NAME_SET = new byte[] { 115, 101, 116 };
        ANONYMOUS_STRUCT = new TStruct();
    }
    
    public static class Factory implements TProtocolFactory
    {
        public TProtocol getProtocol(final TTransport trans) {
            return new TJSONProtocol(trans);
        }
    }
    
    protected class JSONBaseContext
    {
        protected void write() throws TException {
        }
        
        protected void read() throws TException {
        }
        
        protected boolean escapeNum() {
            return false;
        }
    }
    
    protected class JSONListContext extends JSONBaseContext
    {
        private boolean first_;
        
        protected JSONListContext() {
            this.first_ = true;
        }
        
        @Override
        protected void write() throws TException {
            if (this.first_) {
                this.first_ = false;
            }
            else {
                TJSONProtocol.this.trans_.write(TJSONProtocol.COMMA);
            }
        }
        
        @Override
        protected void read() throws TException {
            if (this.first_) {
                this.first_ = false;
            }
            else {
                TJSONProtocol.this.readJSONSyntaxChar(TJSONProtocol.COMMA);
            }
        }
    }
    
    protected class JSONPairContext extends JSONBaseContext
    {
        private boolean first_;
        private boolean colon_;
        
        protected JSONPairContext() {
            this.first_ = true;
            this.colon_ = true;
        }
        
        @Override
        protected void write() throws TException {
            if (this.first_) {
                this.first_ = false;
                this.colon_ = true;
            }
            else {
                TJSONProtocol.this.trans_.write(this.colon_ ? TJSONProtocol.COLON : TJSONProtocol.COMMA);
                this.colon_ = !this.colon_;
            }
        }
        
        @Override
        protected void read() throws TException {
            if (this.first_) {
                this.first_ = false;
                this.colon_ = true;
            }
            else {
                TJSONProtocol.this.readJSONSyntaxChar(this.colon_ ? TJSONProtocol.COLON : TJSONProtocol.COMMA);
                this.colon_ = !this.colon_;
            }
        }
        
        @Override
        protected boolean escapeNum() {
            return this.colon_;
        }
    }
    
    protected class LookaheadReader
    {
        private boolean hasData_;
        private byte[] data_;
        
        protected LookaheadReader() {
            this.data_ = new byte[1];
        }
        
        protected byte read() throws TException {
            if (this.hasData_) {
                this.hasData_ = false;
            }
            else {
                TJSONProtocol.this.trans_.readAll(this.data_, 0, 1);
            }
            return this.data_[0];
        }
        
        protected byte peek() throws TException {
            if (!this.hasData_) {
                TJSONProtocol.this.trans_.readAll(this.data_, 0, 1);
            }
            this.hasData_ = true;
            return this.data_[0];
        }
    }
}
