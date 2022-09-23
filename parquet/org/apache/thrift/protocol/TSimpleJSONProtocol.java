// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.protocol;

import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;
import parquet.org.apache.thrift.TException;
import parquet.org.apache.thrift.transport.TTransport;
import java.util.Stack;

public class TSimpleJSONProtocol extends TProtocol
{
    public static final byte[] COMMA;
    public static final byte[] COLON;
    public static final byte[] LBRACE;
    public static final byte[] RBRACE;
    public static final byte[] LBRACKET;
    public static final byte[] RBRACKET;
    public static final char QUOTE = '\"';
    private static final TStruct ANONYMOUS_STRUCT;
    private static final TField ANONYMOUS_FIELD;
    private static final TMessage EMPTY_MESSAGE;
    private static final TSet EMPTY_SET;
    private static final TList EMPTY_LIST;
    private static final TMap EMPTY_MAP;
    protected final Context BASE_CONTEXT;
    protected Stack<Context> writeContextStack_;
    protected Context writeContext_;
    
    protected void pushWriteContext(final Context c) {
        this.writeContextStack_.push(this.writeContext_);
        this.writeContext_ = c;
    }
    
    protected void popWriteContext() {
        this.writeContext_ = this.writeContextStack_.pop();
    }
    
    public TSimpleJSONProtocol(final TTransport trans) {
        super(trans);
        this.BASE_CONTEXT = new Context();
        this.writeContextStack_ = new Stack<Context>();
        this.writeContext_ = this.BASE_CONTEXT;
    }
    
    @Override
    public void writeMessageBegin(final TMessage message) throws TException {
        this.trans_.write(TSimpleJSONProtocol.LBRACKET);
        this.pushWriteContext(new ListContext());
        this.writeString(message.name);
        this.writeByte(message.type);
        this.writeI32(message.seqid);
    }
    
    @Override
    public void writeMessageEnd() throws TException {
        this.popWriteContext();
        this.trans_.write(TSimpleJSONProtocol.RBRACKET);
    }
    
    @Override
    public void writeStructBegin(final TStruct struct) throws TException {
        this.writeContext_.write();
        this.trans_.write(TSimpleJSONProtocol.LBRACE);
        this.pushWriteContext(new StructContext());
    }
    
    @Override
    public void writeStructEnd() throws TException {
        this.popWriteContext();
        this.trans_.write(TSimpleJSONProtocol.RBRACE);
    }
    
    @Override
    public void writeFieldBegin(final TField field) throws TException {
        this.writeString(field.name);
    }
    
    @Override
    public void writeFieldEnd() {
    }
    
    @Override
    public void writeFieldStop() {
    }
    
    @Override
    public void writeMapBegin(final TMap map) throws TException {
        this.writeContext_.write();
        this.trans_.write(TSimpleJSONProtocol.LBRACE);
        this.pushWriteContext(new StructContext());
    }
    
    @Override
    public void writeMapEnd() throws TException {
        this.popWriteContext();
        this.trans_.write(TSimpleJSONProtocol.RBRACE);
    }
    
    @Override
    public void writeListBegin(final TList list) throws TException {
        this.writeContext_.write();
        this.trans_.write(TSimpleJSONProtocol.LBRACKET);
        this.pushWriteContext(new ListContext());
    }
    
    @Override
    public void writeListEnd() throws TException {
        this.popWriteContext();
        this.trans_.write(TSimpleJSONProtocol.RBRACKET);
    }
    
    @Override
    public void writeSetBegin(final TSet set) throws TException {
        this.writeContext_.write();
        this.trans_.write(TSimpleJSONProtocol.LBRACKET);
        this.pushWriteContext(new ListContext());
    }
    
    @Override
    public void writeSetEnd() throws TException {
        this.popWriteContext();
        this.trans_.write(TSimpleJSONProtocol.RBRACKET);
    }
    
    @Override
    public void writeBool(final boolean b) throws TException {
        this.writeByte((byte)(b ? 1 : 0));
    }
    
    @Override
    public void writeByte(final byte b) throws TException {
        this.writeI32(b);
    }
    
    @Override
    public void writeI16(final short i16) throws TException {
        this.writeI32(i16);
    }
    
    @Override
    public void writeI32(final int i32) throws TException {
        this.writeContext_.write();
        this._writeStringData(Integer.toString(i32));
    }
    
    public void _writeStringData(final String s) throws TException {
        try {
            final byte[] b = s.getBytes("UTF-8");
            this.trans_.write(b);
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
    }
    
    @Override
    public void writeI64(final long i64) throws TException {
        this.writeContext_.write();
        this._writeStringData(Long.toString(i64));
    }
    
    @Override
    public void writeDouble(final double dub) throws TException {
        this.writeContext_.write();
        this._writeStringData(Double.toString(dub));
    }
    
    @Override
    public void writeString(final String str) throws TException {
        this.writeContext_.write();
        final int length = str.length();
        final StringBuffer escape = new StringBuffer(length + 16);
        escape.append('\"');
        for (int i = 0; i < length; ++i) {
            final char c = str.charAt(i);
            switch (c) {
                case '\"':
                case '\\': {
                    escape.append('\\');
                    escape.append(c);
                    break;
                }
                case '\b': {
                    escape.append('\\');
                    escape.append('b');
                    break;
                }
                case '\f': {
                    escape.append('\\');
                    escape.append('f');
                    break;
                }
                case '\n': {
                    escape.append('\\');
                    escape.append('n');
                    break;
                }
                case '\r': {
                    escape.append('\\');
                    escape.append('r');
                    break;
                }
                case '\t': {
                    escape.append('\\');
                    escape.append('t');
                    break;
                }
                default: {
                    if (c < ' ') {
                        final String hex = Integer.toHexString(c);
                        escape.append('\\');
                        escape.append('u');
                        for (int j = 4; j > hex.length(); --j) {
                            escape.append('0');
                        }
                        escape.append(hex);
                        break;
                    }
                    escape.append(c);
                    break;
                }
            }
        }
        escape.append('\"');
        this._writeStringData(escape.toString());
    }
    
    @Override
    public void writeBinary(final ByteBuffer bin) throws TException {
        try {
            this.writeString(new String(bin.array(), bin.position() + bin.arrayOffset(), bin.limit() - bin.position() - bin.arrayOffset(), "UTF-8"));
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
    }
    
    @Override
    public TMessage readMessageBegin() throws TException {
        return TSimpleJSONProtocol.EMPTY_MESSAGE;
    }
    
    @Override
    public void readMessageEnd() {
    }
    
    @Override
    public TStruct readStructBegin() {
        return TSimpleJSONProtocol.ANONYMOUS_STRUCT;
    }
    
    @Override
    public void readStructEnd() {
    }
    
    @Override
    public TField readFieldBegin() throws TException {
        return TSimpleJSONProtocol.ANONYMOUS_FIELD;
    }
    
    @Override
    public void readFieldEnd() {
    }
    
    @Override
    public TMap readMapBegin() throws TException {
        return TSimpleJSONProtocol.EMPTY_MAP;
    }
    
    @Override
    public void readMapEnd() {
    }
    
    @Override
    public TList readListBegin() throws TException {
        return TSimpleJSONProtocol.EMPTY_LIST;
    }
    
    @Override
    public void readListEnd() {
    }
    
    @Override
    public TSet readSetBegin() throws TException {
        return TSimpleJSONProtocol.EMPTY_SET;
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
        return 0;
    }
    
    @Override
    public short readI16() throws TException {
        return 0;
    }
    
    @Override
    public int readI32() throws TException {
        return 0;
    }
    
    @Override
    public long readI64() throws TException {
        return 0L;
    }
    
    @Override
    public double readDouble() throws TException {
        return 0.0;
    }
    
    @Override
    public String readString() throws TException {
        return "";
    }
    
    public String readStringBody(final int size) throws TException {
        return "";
    }
    
    @Override
    public ByteBuffer readBinary() throws TException {
        return ByteBuffer.wrap(new byte[0]);
    }
    
    static {
        COMMA = new byte[] { 44 };
        COLON = new byte[] { 58 };
        LBRACE = new byte[] { 123 };
        RBRACE = new byte[] { 125 };
        LBRACKET = new byte[] { 91 };
        RBRACKET = new byte[] { 93 };
        ANONYMOUS_STRUCT = new TStruct();
        ANONYMOUS_FIELD = new TField();
        EMPTY_MESSAGE = new TMessage();
        EMPTY_SET = new TSet();
        EMPTY_LIST = new TList();
        EMPTY_MAP = new TMap();
    }
    
    public static class Factory implements TProtocolFactory
    {
        public TProtocol getProtocol(final TTransport trans) {
            return new TSimpleJSONProtocol(trans);
        }
    }
    
    protected class Context
    {
        protected void write() throws TException {
        }
    }
    
    protected class ListContext extends Context
    {
        protected boolean first_;
        
        protected ListContext() {
            this.first_ = true;
        }
        
        @Override
        protected void write() throws TException {
            if (this.first_) {
                this.first_ = false;
            }
            else {
                TSimpleJSONProtocol.this.trans_.write(TSimpleJSONProtocol.COMMA);
            }
        }
    }
    
    protected class StructContext extends Context
    {
        protected boolean first_;
        protected boolean colon_;
        
        protected StructContext() {
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
                TSimpleJSONProtocol.this.trans_.write(this.colon_ ? TSimpleJSONProtocol.COLON : TSimpleJSONProtocol.COMMA);
                this.colon_ = !this.colon_;
            }
        }
    }
}
