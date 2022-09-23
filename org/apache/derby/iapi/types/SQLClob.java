// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.io.PushbackInputStream;
import org.apache.derby.iapi.services.io.FormatIdInputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.SQLException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import org.apache.derby.iapi.services.io.InputStreamUtil;
import java.sql.Date;
import java.util.Calendar;
import java.io.IOException;
import org.apache.derby.iapi.util.UTF8Util;
import java.sql.Clob;
import java.text.RuleBasedCollator;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.CloneableStream;
import org.apache.derby.iapi.jdbc.CharacterStreamDescriptor;

public class SQLClob extends SQLVarchar
{
    private static final StreamHeaderGenerator TEN_FOUR_CLOB_HEADER_GENERATOR;
    private static final StreamHeaderGenerator TEN_FIVE_CLOB_HEADER_GENERATOR;
    private static final int MAX_STREAM_HEADER_LENGTH;
    private CharacterStreamDescriptor csd;
    private Boolean inSoftUpgradeMode;
    
    public String getTypeName() {
        return "CLOB";
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        final SQLClob sqlClob = new SQLClob();
        sqlClob.inSoftUpgradeMode = this.inSoftUpgradeMode;
        if (this.isNull()) {
            return sqlClob;
        }
        if (!b) {
            if (this.stream != null && this.stream instanceof CloneableStream) {
                int n = -1;
                if (this.csd != null && this.csd.getCharLength() > 0L) {
                    n = (int)this.csd.getCharLength();
                }
                sqlClob.setValue(((CloneableStream)this.stream).cloneStream(), n);
            }
            else if (this._clobValue != null) {
                sqlClob.setValue(this._clobValue);
            }
        }
        if (!sqlClob.isNull()) {
            if (!b) {
                return sqlClob;
            }
        }
        try {
            sqlClob.setValue(this.getString());
        }
        catch (StandardException ex) {
            return null;
        }
        return sqlClob;
    }
    
    public DataValueDescriptor getNewNull() {
        final SQLClob sqlClob = new SQLClob();
        sqlClob.inSoftUpgradeMode = this.inSoftUpgradeMode;
        return sqlClob;
    }
    
    public StringDataValue getValue(final RuleBasedCollator ruleBasedCollator) {
        if (ruleBasedCollator == null) {
            return this;
        }
        final CollatorSQLClob collatorSQLClob = new CollatorSQLClob(ruleBasedCollator);
        collatorSQLClob.copyState(this);
        return collatorSQLClob;
    }
    
    public int getTypeFormatId() {
        return 447;
    }
    
    public SQLClob() {
        this.inSoftUpgradeMode = null;
    }
    
    public SQLClob(final String s) {
        super(s);
        this.inSoftUpgradeMode = null;
    }
    
    public SQLClob(final Clob clob) {
        super(clob);
        this.inSoftUpgradeMode = null;
    }
    
    public int typePrecedence() {
        return 14;
    }
    
    public boolean getBoolean() throws StandardException {
        throw this.dataTypeConversion("boolean");
    }
    
    public byte getByte() throws StandardException {
        throw this.dataTypeConversion("byte");
    }
    
    public short getShort() throws StandardException {
        throw this.dataTypeConversion("short");
    }
    
    public int getInt() throws StandardException {
        throw this.dataTypeConversion("int");
    }
    
    public int getLength() throws StandardException {
        if (this.stream == null) {
            return super.getLength();
        }
        if (!(this.stream instanceof Resetable)) {
            return super.getLength();
        }
        final boolean b = this.csd != null;
        if (this.csd == null) {
            this.getStreamWithDescriptor();
        }
        if (this.csd.getCharLength() != 0L) {
            return (int)this.csd.getCharLength();
        }
        long skipUntilEOF = 0L;
        try {
            if (b) {
                this.rewindStream(this.stream, this.csd.getDataOffset());
            }
            skipUntilEOF = UTF8Util.skipUntilEOF(this.stream);
            this.rewindStream(this.stream, 0L);
        }
        catch (IOException ex) {
            this.throwStreamingIOException(ex);
        }
        this.csd = new CharacterStreamDescriptor.Builder().copyState(this.csd).charLength(skipUntilEOF).curBytePos(0L).curCharPos(0L).build();
        return (int)skipUntilEOF;
    }
    
    public long getLong() throws StandardException {
        throw this.dataTypeConversion("long");
    }
    
    public float getFloat() throws StandardException {
        throw this.dataTypeConversion("float");
    }
    
    public double getDouble() throws StandardException {
        throw this.dataTypeConversion("double");
    }
    
    public int typeToBigDecimal() throws StandardException {
        throw this.dataTypeConversion("java.math.BigDecimal");
    }
    
    public byte[] getBytes() throws StandardException {
        throw this.dataTypeConversion("byte[]");
    }
    
    public Date getDate(final Calendar calendar) throws StandardException {
        throw this.dataTypeConversion("java.sql.Date");
    }
    
    public Object getObject() throws StandardException {
        if (this._clobValue != null) {
            return this._clobValue;
        }
        final String string = this.getString();
        if (string == null) {
            return null;
        }
        return new HarmonySerialClob(string.toCharArray());
    }
    
    public CharacterStreamDescriptor getStreamWithDescriptor() throws StandardException {
        if (this.stream == null) {
            this.csd = null;
            throw StandardException.newException("42Z12.U", this.getTypeName());
        }
        if (this.csd != null && this.stream instanceof Resetable) {
            try {
                ((Resetable)this.stream).resetStream();
                InputStreamUtil.skipFully(this.stream, this.csd.getCurBytePos());
            }
            catch (IOException ex) {
                this.throwStreamingIOException(ex);
            }
        }
        if (this.csd == null) {
            try {
                final byte[] b = new byte[SQLClob.MAX_STREAM_HEADER_LENGTH];
                int n = this.stream.read(b);
                final HeaderInfo investigateHeader = this.investigateHeader(b, n);
                if (n > investigateHeader.headerLength()) {
                    n = investigateHeader.headerLength();
                    this.rewindStream(this.stream, n);
                }
                this.csd = new CharacterStreamDescriptor.Builder().stream(this.stream).bufferable(false).positionAware(false).curCharPos((n != 0) ? 1 : 0).curBytePos(n).dataOffset(investigateHeader.headerLength()).byteLength(investigateHeader.byteLength()).charLength(investigateHeader.charLength()).build();
            }
            catch (IOException ex2) {
                Throwable cause;
                for (cause = ex2; cause.getCause() != null; cause = cause.getCause()) {}
                if (cause instanceof StandardException && ((StandardException)cause).getMessageId().equals("40XD0")) {
                    throw StandardException.newException("XJ073.S", ex2);
                }
                this.throwStreamingIOException(ex2);
            }
        }
        return this.csd;
    }
    
    public boolean hasStream() {
        return this.stream != null;
    }
    
    public Time getTime(final Calendar calendar) throws StandardException {
        throw this.dataTypeConversion("java.sql.Time");
    }
    
    public Timestamp getTimestamp(final Calendar calendar) throws StandardException {
        throw this.dataTypeConversion("java.sql.Timestamp");
    }
    
    public final String getTraceString() throws StandardException {
        if (this.isNull()) {
            return "NULL";
        }
        if (this.hasStream()) {
            return this.getTypeName() + "(" + this.getStream().toString() + ")";
        }
        return this.getTypeName() + "(" + this.getLength() + ")";
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor instanceof SQLClob) {
            final SQLClob sqlClob = (SQLClob)dataValueDescriptor;
            if (sqlClob.stream != null) {
                this.copyState(sqlClob);
                return;
            }
        }
        super.normalize(dataTypeDescriptor, dataValueDescriptor);
    }
    
    public void setValue(final Time time, final Calendar calendar) throws StandardException {
        this.throwLangSetMismatch("java.sql.Time");
    }
    
    public void setValue(final Timestamp timestamp, final Calendar calendar) throws StandardException {
        this.throwLangSetMismatch("java.sql.Timestamp");
    }
    
    public void setValue(final Date date, final Calendar calendar) throws StandardException {
        this.throwLangSetMismatch("java.sql.Date");
    }
    
    public void setBigDecimal(final Number n) throws StandardException {
        this.throwLangSetMismatch("java.math.BigDecimal");
    }
    
    public final void setStream(final InputStream stream) {
        super.setStream(stream);
        this.csd = null;
    }
    
    public final void restoreToNull() {
        this.csd = null;
        super.restoreToNull();
    }
    
    public void setValue(final int n) throws StandardException {
        this.throwLangSetMismatch("int");
    }
    
    public void setValue(final double n) throws StandardException {
        this.throwLangSetMismatch("double");
    }
    
    public void setValue(final float n) throws StandardException {
        this.throwLangSetMismatch("float");
    }
    
    public void setValue(final short n) throws StandardException {
        this.throwLangSetMismatch("short");
    }
    
    public void setValue(final long n) throws StandardException {
        this.throwLangSetMismatch("long");
    }
    
    public void setValue(final byte b) throws StandardException {
        this.throwLangSetMismatch("byte");
    }
    
    public void setValue(final boolean b) throws StandardException {
        this.throwLangSetMismatch("boolean");
    }
    
    public void setValue(final byte[] array) throws StandardException {
        this.throwLangSetMismatch("byte[]");
    }
    
    final void setObject(final Object o) throws StandardException {
        final Clob clob = (Clob)o;
        try {
            final long length = clob.length();
            if (length < 0L || length > 2147483647L) {
                throw this.outOfRange();
            }
            if (length < 32768L) {
                this.setValue(clob.getSubString(1L, (int)length));
            }
            else {
                this.setValue(new ReaderToUTF8Stream(clob.getCharacterStream(), (int)length, 0, "CLOB", this.getStreamHeaderGenerator()), (int)length);
            }
        }
        catch (SQLException ex) {
            throw this.dataTypeConversion("DAN-438-tmp");
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        super.writeClobUTF(objectOutput);
    }
    
    public StreamHeaderGenerator getStreamHeaderGenerator() {
        if (this.inSoftUpgradeMode == null) {
            return new ClobStreamHeaderGenerator(this);
        }
        if (this.inSoftUpgradeMode == Boolean.TRUE) {
            return SQLClob.TEN_FOUR_CLOB_HEADER_GENERATOR;
        }
        return SQLClob.TEN_FIVE_CLOB_HEADER_GENERATOR;
    }
    
    public void setStreamHeaderFormat(final Boolean inSoftUpgradeMode) {
        this.inSoftUpgradeMode = inSoftUpgradeMode;
    }
    
    private HeaderInfo investigateHeader(final byte[] array, final int i) throws IOException {
        int max_STREAM_HEADER_LENGTH = SQLClob.MAX_STREAM_HEADER_LENGTH;
        int j = -1;
        int n = -1;
        if (i < max_STREAM_HEADER_LENGTH || (array[2] & 0xF0) != 0xF0) {
            max_STREAM_HEADER_LENGTH = 2;
        }
        if (max_STREAM_HEADER_LENGTH == 2) {
            j = ((array[0] & 0xFF) << 8 | (array[1] & 0xFF));
            if (i < SQLClob.MAX_STREAM_HEADER_LENGTH && max_STREAM_HEADER_LENGTH + j != i) {
                throw new IOException("Corrupted stream; headerLength=" + max_STREAM_HEADER_LENGTH + ", utfLen=" + j + ", bytesRead=" + i);
            }
            if (j > 0) {
                j += max_STREAM_HEADER_LENGTH;
            }
        }
        else if (max_STREAM_HEADER_LENGTH == 5) {
            final int k = array[2] & 0xF;
            switch (k) {
                case 0: {
                    n = ((array[0] & 0xFF) << 24 | (array[1] & 0xFF) << 16 | (array[3] & 0xFF) << 8 | (array[4] & 0xFF) << 0);
                    break;
                }
                default: {
                    throw new IOException("Invalid header format identifier: " + k + "(magic byte is 0x" + Integer.toHexString(array[2] & 0xFF) + ")");
                }
            }
        }
        return new HeaderInfo(max_STREAM_HEADER_LENGTH, (max_STREAM_HEADER_LENGTH == 5) ? n : j);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        HeaderInfo investigateHeader;
        if (this.csd != null) {
            final int n = (int)this.csd.getDataOffset();
            investigateHeader = new HeaderInfo(n, (n == 5) ? ((int)this.csd.getCharLength()) : ((int)this.csd.getByteLength()));
            this.rewindStream((InputStream)objectInput, n);
        }
        else {
            final InputStream inputStream = (InputStream)objectInput;
            final boolean markSupported = inputStream.markSupported();
            if (markSupported) {
                inputStream.mark(SQLClob.MAX_STREAM_HEADER_LENGTH);
            }
            final byte[] b = new byte[SQLClob.MAX_STREAM_HEADER_LENGTH];
            final int read = objectInput.read(b);
            investigateHeader = this.investigateHeader(b, read);
            if (read > investigateHeader.headerLength()) {
                if (markSupported) {
                    inputStream.reset();
                    InputStreamUtil.skipFully(inputStream, investigateHeader.headerLength());
                }
                else if (objectInput instanceof FormatIdInputStream) {
                    final int n2 = read - investigateHeader.headerLength();
                    final FormatIdInputStream formatIdInputStream = (FormatIdInputStream)objectInput;
                    final PushbackInputStream input = new PushbackInputStream(formatIdInputStream.getInputStream(), n2);
                    input.unread(b, investigateHeader.headerLength(), n2);
                    formatIdInputStream.setInput(input);
                }
                else {
                    this.rewindStream(inputStream, investigateHeader.headerLength());
                }
            }
        }
        int n3 = 0;
        if (investigateHeader.byteLength() != 0) {
            n3 = investigateHeader.byteLength() - investigateHeader.headerLength();
        }
        super.readExternal(objectInput, n3, investigateHeader.charLength());
    }
    
    public void readExternalFromArray(final ArrayInputStream arrayInputStream) throws IOException {
        final int position = arrayInputStream.getPosition();
        final byte[] b = new byte[SQLClob.MAX_STREAM_HEADER_LENGTH];
        final int read = arrayInputStream.read(b);
        final HeaderInfo investigateHeader = this.investigateHeader(b, read);
        if (read > investigateHeader.headerLength()) {
            arrayInputStream.setPosition(position);
            super.readExternalFromArray(arrayInputStream);
        }
        else {
            super.readExternalClobFromArray(arrayInputStream, investigateHeader.charLength());
        }
    }
    
    private void rewindStream(final InputStream inputStream, final long n) throws IOException {
        try {
            ((Resetable)inputStream).resetStream();
            InputStreamUtil.skipFully(inputStream, n);
        }
        catch (StandardException cause) {
            final IOException ex = new IOException(cause.getMessage());
            ex.initCause(cause);
            throw ex;
        }
    }
    
    static {
        TEN_FOUR_CLOB_HEADER_GENERATOR = new ClobStreamHeaderGenerator(true);
        TEN_FIVE_CLOB_HEADER_GENERATOR = new ClobStreamHeaderGenerator(false);
        MAX_STREAM_HEADER_LENGTH = SQLClob.TEN_FIVE_CLOB_HEADER_GENERATOR.getMaxHeaderLength();
    }
    
    private static class HeaderInfo
    {
        private final int valueLength;
        private final int headerLength;
        
        HeaderInfo(final int headerLength, final int valueLength) {
            this.headerLength = headerLength;
            this.valueLength = valueLength;
        }
        
        int headerLength() {
            return this.headerLength;
        }
        
        int charLength() {
            return this.isCharLength() ? this.valueLength : 0;
        }
        
        int byteLength() {
            return this.isCharLength() ? 0 : this.valueLength;
        }
        
        boolean isCharLength() {
            return this.headerLength == 5;
        }
        
        public String toString() {
            return "headerLength=" + this.headerLength + ", valueLength= " + this.valueLength + ", isCharLength=" + this.isCharLength();
        }
    }
}
