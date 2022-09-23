// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift;

import java.util.NoSuchElementException;
import org.apache.thrift.transport.TTransportException;
import java.nio.charset.CharacterCodingException;
import java.util.StringTokenizer;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.commons.logging.LogFactory;
import java.io.EOFException;
import java.nio.ByteBuffer;
import org.apache.thrift.protocol.TSet;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.TException;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import java.util.regex.Matcher;
import java.util.ArrayList;
import org.apache.hadoop.io.Text;
import org.apache.thrift.transport.TTransport;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.thrift.protocol.TProtocol;

public class TCTLSeparatedProtocol extends TProtocol implements ConfigurableTProtocol, WriteNullsProtocol, SkippableTProtocol
{
    static final Log LOG;
    static byte ORDERED_TYPE;
    protected static final String defaultPrimarySeparator = "\u0001";
    protected static final String defaultSecondarySeparator = "\u0002";
    protected static final String defaultRowSeparator = "\n";
    protected static final String defaultMapSeparator = "\u0003";
    protected String primarySeparator;
    protected String secondarySeparator;
    protected String rowSeparator;
    protected String mapSeparator;
    protected Pattern primaryPattern;
    protected Pattern secondaryPattern;
    protected Pattern mapPattern;
    protected String quote;
    protected SimpleTransportTokenizer transportTokenizer;
    protected String[] columns;
    protected int index;
    protected String[] fields;
    protected int innerIndex;
    protected boolean firstField;
    protected boolean firstInnerField;
    protected boolean isMap;
    protected long elemIndex;
    protected boolean inner;
    protected boolean returnNulls;
    protected final TTransport innerTransport;
    public static final String ReturnNullsKey = "separators.return_nulls";
    public static final String BufferSizeKey = "separators.buffer_size";
    protected int bufferSize;
    protected String nullString;
    protected Text nullText;
    protected Pattern stripSeparatorPrefix;
    protected Pattern stripQuotePrefix;
    protected Pattern stripQuotePostfix;
    private final byte[] buf;
    Text tmpText;
    protected boolean lastPrimitiveWasNullFlag;
    
    public String getPrimarySeparator() {
        return this.primarySeparator;
    }
    
    public String getSecondarySeparator() {
        return this.secondarySeparator;
    }
    
    public String getRowSeparator() {
        return this.rowSeparator;
    }
    
    public String getMapSeparator() {
        return this.mapSeparator;
    }
    
    public TCTLSeparatedProtocol(final TTransport trans) {
        this(trans, "\u0001", "\u0002", "\u0003", "\n", true, 4096);
    }
    
    public TCTLSeparatedProtocol(final TTransport trans, final int buffer_size) {
        this(trans, "\u0001", "\u0002", "\u0003", "\n", true, buffer_size);
    }
    
    public TCTLSeparatedProtocol(final TTransport trans, final String primarySeparator, final String secondarySeparator, final String mapSeparator, final String rowSeparator, final boolean returnNulls, final int bufferSize) {
        super(trans);
        this.buf = new byte[1];
        this.tmpText = new Text();
        this.returnNulls = returnNulls;
        this.primarySeparator = primarySeparator;
        this.secondarySeparator = secondarySeparator;
        this.rowSeparator = rowSeparator;
        this.mapSeparator = mapSeparator;
        this.innerTransport = trans;
        this.bufferSize = bufferSize;
        this.nullString = "\\N";
    }
    
    protected void internalInitialize() {
        final String primaryPatternString = (this.quote == null) ? this.primarySeparator : ("(?:^|" + this.primarySeparator + ")(" + this.quote + "(?:[^" + this.quote + "]+|" + this.quote + this.quote + ")*" + this.quote + "|[^" + this.primarySeparator + "]*)");
        if (this.quote != null) {
            this.stripSeparatorPrefix = Pattern.compile("^" + this.primarySeparator);
            this.stripQuotePrefix = Pattern.compile("^" + this.quote);
            this.stripQuotePostfix = Pattern.compile(this.quote + "$");
        }
        this.primaryPattern = Pattern.compile(primaryPatternString);
        this.secondaryPattern = Pattern.compile(this.secondarySeparator);
        this.mapPattern = Pattern.compile(this.secondarySeparator + "|" + this.mapSeparator);
        this.nullText = new Text(this.nullString);
        (this.transportTokenizer = new SimpleTransportTokenizer(this.innerTransport, this.rowSeparator, this.bufferSize)).initialize();
    }
    
    protected String[] complexSplit(final String line, final Pattern p) {
        final ArrayList<String> list = new ArrayList<String>();
        final Matcher m = p.matcher(line);
        while (m.find()) {
            String match = m.group();
            if (match == null) {
                break;
            }
            if (match.length() == 0) {
                match = null;
            }
            else {
                if (this.stripSeparatorPrefix.matcher(match).find()) {
                    match = match.substring(1);
                }
                if (this.stripQuotePrefix.matcher(match).find()) {
                    match = match.substring(1);
                }
                if (this.stripQuotePostfix.matcher(match).find()) {
                    match = match.substring(0, match.length() - 1);
                }
            }
            list.add(match);
        }
        return list.toArray(new String[1]);
    }
    
    protected String getByteValue(final String altValue, final String defaultVal) {
        if (altValue != null && altValue.length() > 0) {
            try {
                final byte[] b = { Byte.valueOf(altValue) };
                return new String(b);
            }
            catch (NumberFormatException e) {
                return altValue;
            }
        }
        return defaultVal;
    }
    
    @Override
    public void initialize(final Configuration conf, final Properties tbl) throws TException {
        this.primarySeparator = this.getByteValue(tbl.getProperty("field.delim"), this.primarySeparator);
        this.secondarySeparator = this.getByteValue(tbl.getProperty("colelction.delim"), this.secondarySeparator);
        this.rowSeparator = this.getByteValue(tbl.getProperty("line.delim"), this.rowSeparator);
        this.mapSeparator = this.getByteValue(tbl.getProperty("mapkey.delim"), this.mapSeparator);
        this.returnNulls = Boolean.valueOf(tbl.getProperty("separators.return_nulls", String.valueOf(this.returnNulls)));
        this.bufferSize = Integer.valueOf(tbl.getProperty("separators.buffer_size", String.valueOf(this.bufferSize)));
        this.nullString = tbl.getProperty("serialization.null.format", "\\N");
        this.quote = tbl.getProperty("quote.delim", null);
        this.internalInitialize();
    }
    
    @Override
    public void writeMessageBegin(final TMessage message) throws TException {
    }
    
    @Override
    public void writeMessageEnd() throws TException {
    }
    
    @Override
    public void writeStructBegin(final TStruct struct) throws TException {
        this.firstField = true;
    }
    
    @Override
    public void writeStructEnd() throws TException {
    }
    
    @Override
    public void writeFieldBegin(final TField field) throws TException {
        if (!this.firstField) {
            this.internalWriteString(this.primarySeparator);
        }
        this.firstField = false;
    }
    
    @Override
    public void writeFieldEnd() throws TException {
    }
    
    @Override
    public void writeFieldStop() {
    }
    
    @Override
    public void writeMapBegin(final TMap map) throws TException {
        if (map.keyType == 12 || map.keyType == 13 || map.keyType == 15 || map.keyType == 14) {
            throw new TException("Not implemented: nested structures");
        }
        if (map.valueType == 12 || map.valueType == 13 || map.valueType == 15 || map.valueType == 14) {
            throw new TException("Not implemented: nested structures");
        }
        this.firstInnerField = true;
        this.isMap = true;
        this.inner = true;
        this.elemIndex = 0L;
    }
    
    @Override
    public void writeMapEnd() throws TException {
        this.isMap = false;
        this.inner = false;
    }
    
    @Override
    public void writeListBegin(final TList list) throws TException {
        if (list.elemType == 12 || list.elemType == 13 || list.elemType == 15 || list.elemType == 14) {
            throw new TException("Not implemented: nested structures");
        }
        this.firstInnerField = true;
        this.inner = true;
    }
    
    @Override
    public void writeListEnd() throws TException {
        this.inner = false;
    }
    
    @Override
    public void writeSetBegin(final TSet set) throws TException {
        if (set.elemType == 12 || set.elemType == 13 || set.elemType == 15 || set.elemType == 14) {
            throw new TException("Not implemented: nested structures");
        }
        this.firstInnerField = true;
        this.inner = true;
    }
    
    @Override
    public void writeSetEnd() throws TException {
        this.inner = false;
    }
    
    @Override
    public void writeBool(final boolean b) throws TException {
        this.writeString(String.valueOf(b));
    }
    
    @Override
    public void writeByte(final byte b) throws TException {
        this.buf[0] = b;
        this.trans_.write(this.buf);
    }
    
    @Override
    public void writeI16(final short i16) throws TException {
        this.writeString(String.valueOf(i16));
    }
    
    @Override
    public void writeI32(final int i32) throws TException {
        this.writeString(String.valueOf(i32));
    }
    
    @Override
    public void writeI64(final long i64) throws TException {
        this.writeString(String.valueOf(i64));
    }
    
    @Override
    public void writeDouble(final double dub) throws TException {
        this.writeString(String.valueOf(dub));
    }
    
    public void internalWriteString(final String str) throws TException {
        if (str != null) {
            this.tmpText.set(str);
            this.trans_.write(this.tmpText.getBytes(), 0, this.tmpText.getLength());
        }
        else {
            this.trans_.write(this.nullText.getBytes(), 0, this.nullText.getLength());
        }
    }
    
    @Override
    public void writeString(final String str) throws TException {
        if (this.inner) {
            if (!this.firstInnerField) {
                if (this.isMap && this.elemIndex++ % 2L == 0L) {
                    this.internalWriteString(this.mapSeparator);
                }
                else {
                    this.internalWriteString(this.secondarySeparator);
                }
            }
            else {
                this.firstInnerField = false;
            }
        }
        this.internalWriteString(str);
    }
    
    @Override
    public void writeBinary(final ByteBuffer bin) throws TException {
        throw new TException("Ctl separated protocol cannot support writing Binary data!");
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
        assert !this.inner;
        try {
            final String tmp = this.transportTokenizer.nextToken();
            this.columns = ((this.quote == null) ? this.primaryPattern.split(tmp) : this.complexSplit(tmp, this.primaryPattern));
            this.index = 0;
            return new TStruct();
        }
        catch (EOFException e) {
            return null;
        }
    }
    
    @Override
    public void readStructEnd() throws TException {
        this.columns = null;
    }
    
    @Override
    public void skip(final byte type) {
        if (this.inner) {
            ++this.innerIndex;
        }
        else {
            ++this.index;
        }
    }
    
    @Override
    public TField readFieldBegin() throws TException {
        assert !this.inner;
        final TField f = new TField("", TCTLSeparatedProtocol.ORDERED_TYPE, (short)(-1));
        return f;
    }
    
    @Override
    public void readFieldEnd() throws TException {
        this.fields = null;
    }
    
    @Override
    public TMap readMapBegin() throws TException {
        assert !this.inner;
        TMap map = new TMap();
        if (this.columns[this.index] == null || this.columns[this.index].equals(this.nullString)) {
            ++this.index;
            if (this.returnNulls) {
                return null;
            }
        }
        else if (this.columns[this.index].isEmpty()) {
            ++this.index;
        }
        else {
            this.fields = this.mapPattern.split(this.columns[this.index++]);
            map = new TMap(TCTLSeparatedProtocol.ORDERED_TYPE, TCTLSeparatedProtocol.ORDERED_TYPE, this.fields.length / 2);
        }
        this.innerIndex = 0;
        this.inner = true;
        this.isMap = true;
        return map;
    }
    
    @Override
    public void readMapEnd() throws TException {
        this.inner = false;
        this.isMap = false;
    }
    
    @Override
    public TList readListBegin() throws TException {
        assert !this.inner;
        TList list = new TList();
        if (this.columns[this.index] == null || this.columns[this.index].equals(this.nullString)) {
            ++this.index;
            if (this.returnNulls) {
                return null;
            }
        }
        else if (this.columns[this.index].isEmpty()) {
            ++this.index;
        }
        else {
            this.fields = this.secondaryPattern.split(this.columns[this.index++]);
            list = new TList(TCTLSeparatedProtocol.ORDERED_TYPE, this.fields.length);
        }
        this.innerIndex = 0;
        this.inner = true;
        return list;
    }
    
    @Override
    public void readListEnd() throws TException {
        this.inner = false;
    }
    
    @Override
    public TSet readSetBegin() throws TException {
        assert !this.inner;
        TSet set = new TSet();
        if (this.columns[this.index] == null || this.columns[this.index].equals(this.nullString)) {
            ++this.index;
            if (this.returnNulls) {
                return null;
            }
        }
        else if (this.columns[this.index].isEmpty()) {
            ++this.index;
        }
        else {
            this.fields = this.secondaryPattern.split(this.columns[this.index++]);
            set = new TSet(TCTLSeparatedProtocol.ORDERED_TYPE, this.fields.length);
        }
        this.inner = true;
        this.innerIndex = 0;
        return set;
    }
    
    @Override
    public boolean lastPrimitiveWasNull() throws TException {
        return this.lastPrimitiveWasNullFlag;
    }
    
    @Override
    public void writeNull() throws TException {
        this.writeString(null);
    }
    
    @Override
    public void readSetEnd() throws TException {
        this.inner = false;
    }
    
    @Override
    public boolean readBool() throws TException {
        final String val = this.readString();
        this.lastPrimitiveWasNullFlag = (val == null);
        return val != null && !val.isEmpty() && Boolean.valueOf(val);
    }
    
    @Override
    public byte readByte() throws TException {
        final String val = this.readString();
        this.lastPrimitiveWasNullFlag = (val == null);
        try {
            return (byte)((val == null || val.isEmpty()) ? 0 : ((byte)Byte.valueOf(val)));
        }
        catch (NumberFormatException e) {
            this.lastPrimitiveWasNullFlag = true;
            return 0;
        }
    }
    
    @Override
    public short readI16() throws TException {
        final String val = this.readString();
        this.lastPrimitiveWasNullFlag = (val == null);
        try {
            return (short)((val == null || val.isEmpty()) ? 0 : ((short)Short.valueOf(val)));
        }
        catch (NumberFormatException e) {
            this.lastPrimitiveWasNullFlag = true;
            return 0;
        }
    }
    
    @Override
    public int readI32() throws TException {
        final String val = this.readString();
        this.lastPrimitiveWasNullFlag = (val == null);
        try {
            return (val == null || val.isEmpty()) ? 0 : Integer.valueOf(val);
        }
        catch (NumberFormatException e) {
            this.lastPrimitiveWasNullFlag = true;
            return 0;
        }
    }
    
    @Override
    public long readI64() throws TException {
        final String val = this.readString();
        this.lastPrimitiveWasNullFlag = (val == null);
        try {
            return (val == null || val.isEmpty()) ? 0L : Long.valueOf(val);
        }
        catch (NumberFormatException e) {
            this.lastPrimitiveWasNullFlag = true;
            return 0L;
        }
    }
    
    @Override
    public double readDouble() throws TException {
        final String val = this.readString();
        this.lastPrimitiveWasNullFlag = (val == null);
        try {
            return (val == null || val.isEmpty()) ? 0.0 : Double.valueOf(val);
        }
        catch (NumberFormatException e) {
            this.lastPrimitiveWasNullFlag = true;
            return 0.0;
        }
    }
    
    @Override
    public String readString() throws TException {
        String ret;
        if (!this.inner) {
            ret = ((this.columns != null && this.index < this.columns.length) ? this.columns[this.index] : null);
            ++this.index;
        }
        else {
            ret = ((this.fields != null && this.innerIndex < this.fields.length) ? this.fields[this.innerIndex] : null);
            ++this.innerIndex;
        }
        if (ret == null || ret.equals(this.nullString)) {
            return this.returnNulls ? null : "";
        }
        return ret;
    }
    
    @Override
    public ByteBuffer readBinary() throws TException {
        throw new TException("Not implemented for control separated data");
    }
    
    static {
        LOG = LogFactory.getLog(TCTLSeparatedProtocol.class.getName());
        TCTLSeparatedProtocol.ORDERED_TYPE = -1;
    }
    
    public static class Factory implements TProtocolFactory
    {
        @Override
        public TProtocol getProtocol(final TTransport trans) {
            return new TCTLSeparatedProtocol(trans);
        }
    }
    
    class SimpleTransportTokenizer
    {
        TTransport trans;
        StringTokenizer tokenizer;
        final String separator;
        byte[] buf;
        
        public SimpleTransportTokenizer(final TTransport trans, final String separator, final int buffer_length) {
            this.trans = trans;
            this.separator = separator;
            this.buf = new byte[buffer_length];
        }
        
        private void initialize() {
            try {
                this.fillTokenizer();
            }
            catch (Exception e) {
                TCTLSeparatedProtocol.LOG.warn("Unable to initialize tokenizer", e);
            }
        }
        
        private boolean fillTokenizer() {
            try {
                final int length = this.trans.read(this.buf, 0, this.buf.length);
                if (length <= 0) {
                    this.tokenizer = new StringTokenizer("", this.separator, true);
                    return false;
                }
                String row;
                try {
                    row = Text.decode(this.buf, 0, length);
                }
                catch (CharacterCodingException e) {
                    throw new RuntimeException(e);
                }
                this.tokenizer = new StringTokenizer(row, this.separator, true);
            }
            catch (TTransportException e2) {
                if (e2.getType() == 4) {
                    this.tokenizer = new StringTokenizer("", this.separator, true);
                    return false;
                }
                this.tokenizer = null;
                throw new RuntimeException(e2);
            }
            return true;
        }
        
        public String nextToken() throws EOFException {
            StringBuilder ret = null;
            boolean done = false;
            if (this.tokenizer == null) {
                this.fillTokenizer();
            }
            while (!done && (this.tokenizer.hasMoreTokens() || this.fillTokenizer())) {
                try {
                    final String nextToken = this.tokenizer.nextToken();
                    if (nextToken.equals(this.separator)) {
                        done = true;
                    }
                    else if (ret == null) {
                        ret = new StringBuilder(nextToken);
                    }
                    else {
                        ret.append(nextToken);
                    }
                }
                catch (NoSuchElementException e) {
                    if (ret == null) {
                        throw new EOFException(e.getMessage());
                    }
                    done = true;
                }
            }
            final String theRet = (ret == null) ? null : ret.toString();
            return theRet;
        }
    }
}
