// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.cache.ClassSize;
import org.apache.derby.iapi.db.DatabaseContext;
import java.util.Locale;
import java.io.OutputStream;
import org.apache.derby.iapi.services.io.FormatIdOutputStream;
import org.apache.derby.iapi.services.io.CounterOutputStream;
import java.sql.SQLWarning;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.StatementContext;
import java.sql.DataTruncation;
import org.apache.derby.iapi.util.StringUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.RuleBasedCollator;
import java.io.UTFDataFormatException;
import java.io.EOFException;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.io.Reader;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.io.FormatIdInputStream;
import java.sql.SQLException;
import java.io.IOException;
import org.apache.derby.iapi.util.UTF8Util;
import org.apache.derby.iapi.services.io.InputStreamUtil;
import java.io.ObjectInput;
import org.apache.derby.iapi.jdbc.CharacterStreamDescriptor;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.util.Calendar;
import org.apache.derby.iapi.error.StandardException;
import java.util.Arrays;
import org.apache.derby.iapi.services.i18n.LocaleFinder;
import java.io.InputStream;
import java.sql.Clob;
import java.text.CollationKey;
import org.apache.derby.iapi.services.io.StreamStorable;

public class SQLChar extends DataType implements StringDataValue, StreamStorable
{
    private static final char PAD = ' ';
    protected static final int RETURN_SPACE_THRESHOLD = 4096;
    private static final int GROWBY_FOR_CHAR = 64;
    private static final int BASE_MEMORY_USAGE;
    private static final char[] BLANKS;
    protected static final StreamHeaderGenerator CHAR_HEADER_GENERATOR;
    private String value;
    private char[] rawData;
    private int rawLength;
    private CollationKey cKey;
    protected Clob _clobValue;
    InputStream stream;
    private LocaleFinder localeFinder;
    char[][] arg_passer;
    
    public SQLChar() {
        this.rawLength = -1;
        this.arg_passer = new char[1][];
    }
    
    public SQLChar(final String value) {
        this.rawLength = -1;
        this.arg_passer = new char[1][];
        this.value = value;
    }
    
    public SQLChar(final Clob value) {
        this.rawLength = -1;
        this.arg_passer = new char[1][];
        this.setValue(value);
    }
    
    public SQLChar(final char[] array) {
        this.rawLength = -1;
        this.arg_passer = new char[1][];
        if (array == null) {
            this.value = null;
        }
        else {
            final int length = array.length;
            final char[] array2 = new char[length];
            System.arraycopy(array, 0, array2, 0, length);
            this.copyState(null, array2, length, null, null, null, null);
        }
    }
    
    private static void appendBlanks(final char[] array, int n, int i) {
        while (i > 0) {
            final int n2 = (i > SQLChar.BLANKS.length) ? SQLChar.BLANKS.length : i;
            System.arraycopy(SQLChar.BLANKS, 0, array, n, n2);
            i -= n2;
            n += n2;
        }
    }
    
    public char[] getRawDataAndZeroIt() {
        if (this.rawData == null) {
            return null;
        }
        final int length = this.rawData.length;
        final char[] array = new char[length];
        System.arraycopy(this.rawData, 0, array, 0, length);
        this.zeroRawData();
        return array;
    }
    
    public void zeroRawData() {
        if (this.rawData == null) {
            return;
        }
        Arrays.fill(this.rawData, '\0');
    }
    
    public boolean getBoolean() throws StandardException {
        if (this.isNull()) {
            return false;
        }
        final String trim = this.getString().trim();
        return !trim.equals("0") && !trim.equals("false");
    }
    
    public byte getByte() throws StandardException {
        if (this.isNull()) {
            return 0;
        }
        try {
            return Byte.parseByte(this.getString().trim());
        }
        catch (NumberFormatException ex) {
            throw StandardException.newException("22018", "byte");
        }
    }
    
    public short getShort() throws StandardException {
        if (this.isNull()) {
            return 0;
        }
        try {
            return Short.parseShort(this.getString().trim());
        }
        catch (NumberFormatException ex) {
            throw StandardException.newException("22018", "short");
        }
    }
    
    public int getInt() throws StandardException {
        if (this.isNull()) {
            return 0;
        }
        try {
            return Integer.parseInt(this.getString().trim());
        }
        catch (NumberFormatException ex) {
            throw StandardException.newException("22018", "int");
        }
    }
    
    public long getLong() throws StandardException {
        if (this.isNull()) {
            return 0L;
        }
        try {
            return Long.parseLong(this.getString().trim());
        }
        catch (NumberFormatException ex) {
            throw StandardException.newException("22018", "long");
        }
    }
    
    public float getFloat() throws StandardException {
        if (this.isNull()) {
            return 0.0f;
        }
        try {
            return new Float(this.getString().trim());
        }
        catch (NumberFormatException ex) {
            throw StandardException.newException("22018", "float");
        }
    }
    
    public double getDouble() throws StandardException {
        if (this.isNull()) {
            return 0.0;
        }
        try {
            return new Double(this.getString().trim());
        }
        catch (NumberFormatException ex) {
            throw StandardException.newException("22018", "double");
        }
    }
    
    public Date getDate(final Calendar calendar) throws StandardException {
        return getDate(calendar, this.getString(), this.getLocaleFinder());
    }
    
    public static Date getDate(final Calendar calendar, final String s, final LocaleFinder localeFinder) throws StandardException {
        if (s == null) {
            return null;
        }
        return new SQLDate(s, false, localeFinder).getDate(calendar);
    }
    
    public Time getTime(final Calendar calendar) throws StandardException {
        return getTime(calendar, this.getString(), this.getLocaleFinder());
    }
    
    public static Time getTime(final Calendar calendar, final String s, final LocaleFinder localeFinder) throws StandardException {
        if (s == null) {
            return null;
        }
        return new SQLTime(s, false, localeFinder, calendar).getTime(calendar);
    }
    
    public Timestamp getTimestamp(final Calendar calendar) throws StandardException {
        return getTimestamp(calendar, this.getString(), this.getLocaleFinder());
    }
    
    public static Timestamp getTimestamp(final Calendar calendar, final String s, final LocaleFinder localeFinder) throws StandardException {
        if (s == null) {
            return null;
        }
        return new SQLTimestamp(s, false, localeFinder, calendar).getTimestamp(calendar);
    }
    
    public InputStream returnStream() {
        return this.stream;
    }
    
    public void setStream(final InputStream stream) {
        this.value = null;
        this.rawLength = -1;
        this.stream = stream;
        this.cKey = null;
        this._clobValue = null;
    }
    
    public void loadStream() throws StandardException {
        this.getString();
    }
    
    public Object getObject() throws StandardException {
        return this.getString();
    }
    
    public InputStream getStream() throws StandardException {
        if (!this.hasStream()) {
            throw StandardException.newException("42Z12.U", this.getTypeName());
        }
        return this.stream;
    }
    
    public CharacterStreamDescriptor getStreamWithDescriptor() throws StandardException {
        throw StandardException.newException("42Z12.U", this.getTypeName());
    }
    
    public int typeToBigDecimal() throws StandardException {
        return 1;
    }
    
    public int getLength() throws StandardException {
        if (this._clobValue != null) {
            return this.getClobLength();
        }
        if (this.rawLength != -1) {
            return this.rawLength;
        }
        if (this.stream != null && this.stream instanceof Resetable && this.stream instanceof ObjectInput) {
            try {
                InputStreamUtil.skipFully(this.stream, 2L);
                return (int)UTF8Util.skipUntilEOF(this.stream);
            }
            catch (IOException ex) {
                this.throwStreamingIOException(ex);
                try {
                    ((Resetable)this.stream).resetStream();
                }
                catch (IOException ex2) {
                    this.throwStreamingIOException(ex2);
                }
            }
            finally {
                try {
                    ((Resetable)this.stream).resetStream();
                }
                catch (IOException ex3) {
                    this.throwStreamingIOException(ex3);
                }
            }
        }
        final String string = this.getString();
        if (string == null) {
            return 0;
        }
        return string.length();
    }
    
    protected void throwStreamingIOException(final IOException ex) throws StandardException {
        throw StandardException.newException("XCL30.S", ex, this.getTypeName());
    }
    
    public String getTypeName() {
        return "CHAR";
    }
    
    public String getString() throws StandardException {
        if (this.value == null) {
            final int rawLength = this.rawLength;
            if (rawLength != -1) {
                this.value = new String(this.rawData, 0, rawLength);
                if (rawLength > 4096) {
                    this.rawData = null;
                    this.rawLength = -1;
                    this.cKey = null;
                }
            }
            else {
                if (this._clobValue != null) {
                    try {
                        this.value = this._clobValue.getSubString(1L, this.getClobLength());
                        this._clobValue = null;
                        return this.value;
                    }
                    catch (SQLException ex) {
                        throw StandardException.plainWrapException(ex);
                    }
                }
                if (this.stream != null) {
                    try {
                        if (this.stream instanceof FormatIdInputStream) {
                            this.readExternal((ObjectInput)this.stream);
                        }
                        else {
                            this.readExternal(new FormatIdInputStream(this.stream));
                        }
                        this.stream = null;
                        return this.getString();
                    }
                    catch (IOException ex2) {
                        throw StandardException.newException("XCL30.S", ex2, String.class.getName());
                    }
                }
            }
        }
        return this.value;
    }
    
    public char[] getCharArray() throws StandardException {
        if (this.isNull()) {
            return null;
        }
        if (this.rawLength != -1) {
            return this.rawData;
        }
        this.getString();
        this.rawData = this.value.toCharArray();
        this.rawLength = this.rawData.length;
        this.cKey = null;
        return this.rawData;
    }
    
    public int getTypeFormatId() {
        return 78;
    }
    
    public boolean isNull() {
        return this.value == null && this.rawLength == -1 && this.stream == null && this._clobValue == null;
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        if (this._clobValue != null) {
            this.writeClobUTF(objectOutput);
            return;
        }
        String value = null;
        char[] rawData = null;
        int n = this.rawLength;
        boolean b;
        if (n < 0) {
            value = this.value;
            n = value.length();
            b = false;
        }
        else {
            rawData = this.rawData;
            b = true;
        }
        int n2 = n;
        for (int index = 0; index < n && n2 <= 65535; ++index) {
            final char c = b ? rawData[index] : value.charAt(index);
            if (c < '\u0001' || c > '\u007f') {
                if (c > '\u07ff') {
                    n2 += 2;
                }
                else {
                    ++n2;
                }
            }
        }
        final StreamHeaderGenerator streamHeaderGenerator = this.getStreamHeaderGenerator();
        streamHeaderGenerator.generateInto(objectOutput, n2);
        this.writeUTF(objectOutput, n, b, null);
        streamHeaderGenerator.writeEOF(objectOutput, n2);
    }
    
    private final void writeUTF(final ObjectOutput objectOutput, final int n, final boolean b, final Reader reader) throws IOException {
        final char[] array = (char[])(b ? this.rawData : null);
        final String s = b ? null : this.value;
        for (int i = 0; i < n; ++i) {
            int read;
            if (reader != null) {
                read = reader.read();
            }
            else {
                read = (b ? array[i] : s.charAt(i));
            }
            writeUTF(objectOutput, read);
        }
    }
    
    private static void writeUTF(final ObjectOutput objectOutput, final int n) throws IOException {
        if (n >= 1 && n <= 127) {
            objectOutput.write(n);
        }
        else if (n > 2047) {
            objectOutput.write(0xE0 | (n >> 12 & 0xF));
            objectOutput.write(0x80 | (n >> 6 & 0x3F));
            objectOutput.write(0x80 | (n >> 0 & 0x3F));
        }
        else {
            objectOutput.write(0xC0 | (n >> 6 & 0x1F));
            objectOutput.write(0x80 | (n >> 0 & 0x3F));
        }
    }
    
    protected final void writeClobUTF(final ObjectOutput objectOutput) throws IOException {
        final boolean b = this._clobValue != null;
        try {
            final boolean b2 = this.rawLength >= 0;
            int n = this.rawLength;
            if (!b2) {
                if (b) {
                    n = this.rawGetClobLength();
                }
                else {
                    n = this.value.length();
                }
            }
            final StreamHeaderGenerator streamHeaderGenerator = this.getStreamHeaderGenerator();
            final int n2 = streamHeaderGenerator.expectsCharCount() ? n : -1;
            streamHeaderGenerator.generateInto(objectOutput, n2);
            Reader characterStream = null;
            if (b) {
                characterStream = this._clobValue.getCharacterStream();
            }
            this.writeUTF(objectOutput, n, b2, characterStream);
            streamHeaderGenerator.writeEOF(objectOutput, n2);
            if (b) {
                characterStream.close();
            }
        }
        catch (SQLException cause) {
            final IOException ex = new IOException(cause.getMessage());
            ex.initCause(cause);
            throw ex;
        }
    }
    
    public void readExternalFromArray(final ArrayInputStream arrayInputStream) throws IOException {
        this.resetForMaterialization();
        final int n = (arrayInputStream.read() & 0xFF) << 8 | (arrayInputStream.read() & 0xFF);
        if (this.rawData == null || this.rawData.length < n) {
            this.rawData = new char[n];
        }
        this.arg_passer[0] = this.rawData;
        this.rawLength = arrayInputStream.readDerbyUTF(this.arg_passer, n);
        this.rawData = this.arg_passer[0];
    }
    
    protected void readExternalClobFromArray(final ArrayInputStream arrayInputStream, final int n) throws IOException {
        this.resetForMaterialization();
        if (this.rawData == null || this.rawData.length < n) {
            this.rawData = new char[n];
        }
        this.arg_passer[0] = this.rawData;
        this.rawLength = arrayInputStream.readDerbyUTF(this.arg_passer, 0);
        this.rawData = this.arg_passer[0];
    }
    
    private void resetForMaterialization() {
        this.value = null;
        this.stream = null;
        this.cKey = null;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException {
        this.readExternal(objectInput, objectInput.readUnsignedShort(), 0);
    }
    
    protected void readExternal(final ObjectInput objectInput, final int n, final int n2) throws IOException {
        final int growBy = this.growBy();
        int available;
        if (n != 0) {
            available = n;
        }
        else {
            available = objectInput.available();
            if (available < growBy) {
                available = growBy;
            }
        }
        char[] rawData;
        if (this.rawData == null || available > this.rawData.length) {
            rawData = new char[available];
        }
        else {
            rawData = this.rawData;
        }
        int length = rawData.length;
        this.rawData = null;
        this.resetForMaterialization();
        int n3 = 0;
        int rawLength;
        char c;
        for (rawLength = 0; rawLength < n2 || n2 == 0; rawData[rawLength++] = c) {
            if (n3 >= n) {
                if (n != 0) {
                    break;
                }
            }
            int unsignedByte;
            try {
                unsignedByte = objectInput.readUnsignedByte();
            }
            catch (EOFException ex) {
                if (n != 0) {
                    throw new EOFException();
                }
                break;
            }
            if (rawLength >= length) {
                int available2 = objectInput.available();
                if (available2 < growBy) {
                    available2 = growBy;
                }
                final int n4 = length + available2;
                final char[] array = rawData;
                rawData = new char[n4];
                System.arraycopy(array, 0, rawData, 0, length);
                length = n4;
            }
            if ((unsignedByte & 0x80) == 0x0) {
                ++n3;
                c = (char)unsignedByte;
            }
            else if ((unsignedByte & 0x60) == 0x40) {
                n3 += 2;
                if (n != 0 && n3 > n) {
                    throw new UTFDataFormatException();
                }
                final int unsignedByte2 = objectInput.readUnsignedByte();
                if ((unsignedByte2 & 0xC0) != 0x80) {
                    throw new UTFDataFormatException();
                }
                c = (char)((unsignedByte & 0x1F) << 6 | (unsignedByte2 & 0x3F));
            }
            else {
                if ((unsignedByte & 0x70) != 0x60) {
                    throw new UTFDataFormatException("Invalid code point: " + Integer.toHexString(unsignedByte));
                }
                n3 += 3;
                if (n != 0 && n3 > n) {
                    throw new UTFDataFormatException();
                }
                final int unsignedByte3 = objectInput.readUnsignedByte();
                final int unsignedByte4 = objectInput.readUnsignedByte();
                if (unsignedByte == 224 && unsignedByte3 == 0 && unsignedByte4 == 0 && n == 0) {
                    break;
                }
                if ((unsignedByte3 & 0xC0) != 0x80 || (unsignedByte4 & 0xC0) != 0x80) {
                    throw new UTFDataFormatException();
                }
                c = (char)((unsignedByte & 0xF) << 12 | (unsignedByte3 & 0x3F) << 6 | (unsignedByte4 & 0x3F) << 0);
            }
        }
        this.rawData = rawData;
        this.rawLength = rawLength;
    }
    
    protected int growBy() {
        return 64;
    }
    
    public void restoreToNull() {
        this.value = null;
        this._clobValue = null;
        this.stream = null;
        this.rawLength = -1;
        this.cKey = null;
    }
    
    public boolean compare(final int n, final DataValueDescriptor dataValueDescriptor, final boolean b, final boolean b2) throws StandardException {
        if (!b && (this.isNull() || dataValueDescriptor.isNull())) {
            return b2;
        }
        if (!(dataValueDescriptor instanceof SQLChar)) {
            return dataValueDescriptor.compare(DataType.flip(n), this, b, b2);
        }
        return super.compare(n, dataValueDescriptor, b, b2);
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
            return -dataValueDescriptor.compare(this);
        }
        return this.stringCompare(this, (SQLChar)dataValueDescriptor);
    }
    
    public DataValueDescriptor cloneHolder() {
        if (this.stream == null && this._clobValue == null) {
            return this.cloneValue(false);
        }
        final SQLChar sqlChar = (SQLChar)this.getNewNull();
        sqlChar.copyState(this);
        return sqlChar;
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        try {
            return new SQLChar(this.getString());
        }
        catch (StandardException ex) {
            return null;
        }
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLChar();
    }
    
    public StringDataValue getValue(final RuleBasedCollator ruleBasedCollator) {
        if (ruleBasedCollator == null) {
            return this;
        }
        final CollatorSQLChar collatorSQLChar = new CollatorSQLChar(ruleBasedCollator);
        collatorSQLChar.copyState(this);
        return collatorSQLChar;
    }
    
    public final void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException {
        this.setValue(set.getString(n));
    }
    
    public final void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException, StandardException {
        preparedStatement.setString(n, this.getString());
    }
    
    public void setValue(final Clob clobValue) {
        this.stream = null;
        this.rawLength = -1;
        this.cKey = null;
        this.value = null;
        this._clobValue = clobValue;
    }
    
    public void setValue(final String value) {
        this.stream = null;
        this.rawLength = -1;
        this.cKey = null;
        this._clobValue = null;
        this.value = value;
    }
    
    public void setValue(final boolean b) throws StandardException {
        this.setValue(Boolean.toString(b));
    }
    
    public void setValue(final int i) throws StandardException {
        this.setValue(Integer.toString(i));
    }
    
    public void setValue(final double d) throws StandardException {
        this.setValue(Double.toString(d));
    }
    
    public void setValue(final float f) throws StandardException {
        this.setValue(Float.toString(f));
    }
    
    public void setValue(final short s) throws StandardException {
        this.setValue(Short.toString(s));
    }
    
    public void setValue(final long i) throws StandardException {
        this.setValue(Long.toString(i));
    }
    
    public void setValue(final byte b) throws StandardException {
        this.setValue(Byte.toString(b));
    }
    
    public void setValue(final byte[] array) throws StandardException {
        if (array == null) {
            this.restoreToNull();
            return;
        }
        final int n = array.length % 2;
        int n2 = array.length / 2 + n;
        final char[] value = new char[n2];
        int i = 0;
        int n3 = 0;
        if (n == 1) {
            value[--n2] = (char)(array[array.length - 1] << 8);
        }
        while (i < n2) {
            value[i] = (char)(array[n3] << 8 | (array[n3 + 1] & 0xFF));
            n3 += 2;
            ++i;
        }
        this.setValue(new String(value));
    }
    
    public void setBigDecimal(final Number n) throws StandardException {
        if (n == null) {
            this.setToNull();
        }
        else {
            this.setValue(n.toString());
        }
    }
    
    public void setValue(final Date time, final Calendar calendar) throws StandardException {
        String value = null;
        if (time != null) {
            if (calendar == null) {
                value = time.toString();
            }
            else {
                calendar.setTime(time);
                final StringBuffer sb = new StringBuffer();
                this.formatJDBCDate(calendar, sb);
                value = sb.toString();
            }
        }
        this.setValue(value);
    }
    
    public void setValue(final Time time, final Calendar calendar) throws StandardException {
        String value = null;
        if (time != null) {
            if (calendar == null) {
                value = time.toString();
            }
            else {
                calendar.setTime(time);
                final StringBuffer sb = new StringBuffer();
                this.formatJDBCTime(calendar, sb);
                value = sb.toString();
            }
        }
        this.setValue(value);
    }
    
    public void setValue(final Timestamp time, final Calendar calendar) throws StandardException {
        String value = null;
        if (time != null) {
            if (calendar == null) {
                value = time.toString();
            }
            else {
                calendar.setTime(time);
                final StringBuffer sb = new StringBuffer();
                this.formatJDBCDate(calendar, sb);
                sb.append(' ');
                this.formatJDBCTime(calendar, sb);
                sb.append('.');
                final int nanos = time.getNanos();
                if (nanos == 0) {
                    sb.append('0');
                }
                else if (nanos > 0) {
                    final String string = Integer.toString(nanos);
                    int i;
                    int endIndex;
                    for (endIndex = (i = string.length()); i < 9; ++i) {
                        sb.append('0');
                    }
                    while (string.charAt(endIndex - 1) == '0') {
                        --endIndex;
                    }
                    sb.append(string.substring(0, endIndex));
                }
                value = sb.toString();
            }
        }
        this.setValue(value);
    }
    
    private void formatJDBCDate(final Calendar calendar, final StringBuffer sb) {
        SQLDate.dateToString(calendar.get(1), calendar.get(2) - 0 + 1, calendar.get(5), sb);
    }
    
    private void formatJDBCTime(final Calendar calendar, final StringBuffer sb) {
        SQLTime.timeToString(calendar.get(11), calendar.get(12), calendar.get(13), sb);
    }
    
    public final void setValue(final InputStream stream, final int n) {
        this.setStream(stream);
    }
    
    public void setObjectForCast(final Object o, final boolean b, final String anObject) throws StandardException {
        if (o == null) {
            this.setToNull();
            return;
        }
        if ("java.lang.String".equals(anObject)) {
            this.setValue(o.toString());
        }
        else {
            super.setObjectForCast(o, b, anObject);
        }
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor instanceof SQLChar) {
            final SQLChar sqlChar = (SQLChar)dataValueDescriptor;
            if (sqlChar._clobValue != null) {
                this.setValue(sqlChar._clobValue);
                return;
            }
        }
        this.setValue(dataValueDescriptor.getString());
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        this.normalize(dataTypeDescriptor, dataValueDescriptor.getString());
    }
    
    protected void normalize(final DataTypeDescriptor dataTypeDescriptor, final String value) throws StandardException {
        final int maximumWidth = dataTypeDescriptor.getMaximumWidth();
        final int length = value.length();
        if (length == maximumWidth) {
            this.setValue(value);
            return;
        }
        if (length < maximumWidth) {
            this.setToNull();
            char[] rawData2;
            if (this.rawData == null || maximumWidth > this.rawData.length) {
                final char[] rawData = new char[maximumWidth];
                this.rawData = rawData;
                rawData2 = rawData;
            }
            else {
                rawData2 = this.rawData;
            }
            value.getChars(0, length, rawData2, 0);
            appendBlanks(rawData2, length, maximumWidth - length);
            this.rawLength = maximumWidth;
            return;
        }
        this.hasNonBlankChars(value, maximumWidth, length);
        this.setValue(value.substring(0, maximumWidth));
    }
    
    protected final void hasNonBlankChars(final String s, final int i, final int n) throws StandardException {
        for (int j = i; j < n; ++j) {
            if (s.charAt(j) != ' ') {
                throw StandardException.newException("22001", this.getTypeName(), StringUtil.formatForPrint(s), String.valueOf(i));
            }
        }
    }
    
    public void setWidth(final int endIndex, final int n, final boolean b) throws StandardException {
        if (this._clobValue == null && this.getString() == null) {
            return;
        }
        int i = this.getLength();
        if (i < endIndex) {
            if (!(this instanceof SQLVarchar)) {
                final StringBuffer buffer = new StringBuffer(this.getString());
                while (i < endIndex) {
                    buffer.append(' ');
                    ++i;
                }
                this.setValue(new String(buffer));
            }
        }
        else if (i > endIndex && endIndex > 0) {
            try {
                this.hasNonBlankChars(this.getString(), endIndex, i);
            }
            catch (StandardException cause) {
                if (b) {
                    throw cause;
                }
                final String string = this.getString();
                final int utf8Length = this.getUTF8Length(string, 0, endIndex);
                final DataTruncation dataTruncation = new DataTruncation(-1, false, true, utf8Length + this.getUTF8Length(string, endIndex, string.length()), utf8Length);
                dataTruncation.initCause(cause);
                ((StatementContext)ContextService.getContext("StatementContext")).getActivation().getResultSet().addWarning(dataTruncation);
            }
            this.setValue(this.getString().substring(0, endIndex));
        }
    }
    
    private int getUTF8Length(final String s, final int n, final int n2) throws StandardException {
        final CounterOutputStream counterOutputStream = new CounterOutputStream();
        try {
            final FormatIdOutputStream formatIdOutputStream = new FormatIdOutputStream(counterOutputStream);
            for (int i = n; i < n2; ++i) {
                writeUTF(formatIdOutputStream, s.charAt(i));
            }
            formatIdOutputStream.close();
        }
        catch (IOException ex) {
            throw StandardException.newException("X0X63.S", ex, ex.toString());
        }
        return counterOutputStream.getCount();
    }
    
    public BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        boolean b;
        if (dataValueDescriptor instanceof SQLChar && dataValueDescriptor2 instanceof SQLChar) {
            b = (this.stringCompare((SQLChar)dataValueDescriptor, (SQLChar)dataValueDescriptor2) == 0);
        }
        else {
            b = (stringCompare(dataValueDescriptor.getString(), dataValueDescriptor2.getString()) == 0);
        }
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, b);
    }
    
    public BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        boolean b;
        if (dataValueDescriptor instanceof SQLChar && dataValueDescriptor2 instanceof SQLChar) {
            b = (this.stringCompare((SQLChar)dataValueDescriptor, (SQLChar)dataValueDescriptor2) != 0);
        }
        else {
            b = (stringCompare(dataValueDescriptor.getString(), dataValueDescriptor2.getString()) != 0);
        }
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, b);
    }
    
    public BooleanDataValue lessThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        boolean b;
        if (dataValueDescriptor instanceof SQLChar && dataValueDescriptor2 instanceof SQLChar) {
            b = (this.stringCompare((SQLChar)dataValueDescriptor, (SQLChar)dataValueDescriptor2) < 0);
        }
        else {
            b = (stringCompare(dataValueDescriptor.getString(), dataValueDescriptor2.getString()) < 0);
        }
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, b);
    }
    
    public BooleanDataValue greaterThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        boolean b;
        if (dataValueDescriptor instanceof SQLChar && dataValueDescriptor2 instanceof SQLChar) {
            b = (this.stringCompare((SQLChar)dataValueDescriptor, (SQLChar)dataValueDescriptor2) > 0);
        }
        else {
            b = (stringCompare(dataValueDescriptor.getString(), dataValueDescriptor2.getString()) > 0);
        }
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, b);
    }
    
    public BooleanDataValue lessOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        boolean b;
        if (dataValueDescriptor instanceof SQLChar && dataValueDescriptor2 instanceof SQLChar) {
            b = (this.stringCompare((SQLChar)dataValueDescriptor, (SQLChar)dataValueDescriptor2) <= 0);
        }
        else {
            b = (stringCompare(dataValueDescriptor.getString(), dataValueDescriptor2.getString()) <= 0);
        }
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, b);
    }
    
    public BooleanDataValue greaterOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        boolean b;
        if (dataValueDescriptor instanceof SQLChar && dataValueDescriptor2 instanceof SQLChar) {
            b = (this.stringCompare((SQLChar)dataValueDescriptor, (SQLChar)dataValueDescriptor2) >= 0);
        }
        else {
            b = (stringCompare(dataValueDescriptor.getString(), dataValueDescriptor2.getString()) >= 0);
        }
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, b);
    }
    
    public NumberDataValue charLength(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLInteger();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        numberDataValue.setValue(this.getLength());
        return numberDataValue;
    }
    
    public StringDataValue concatenate(final StringDataValue stringDataValue, final StringDataValue stringDataValue2, StringDataValue stringDataValue3) throws StandardException {
        if (stringDataValue3 == null) {
            stringDataValue3 = (StringDataValue)this.getNewNull();
        }
        if (stringDataValue.isNull() || stringDataValue.getString() == null || stringDataValue2.isNull() || stringDataValue2.getString() == null) {
            stringDataValue3.setToNull();
            return stringDataValue3;
        }
        stringDataValue3.setValue(stringDataValue.getString().concat(stringDataValue2.getString()));
        return stringDataValue3;
    }
    
    public BooleanDataValue like(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        return SQLBoolean.truthValue(this, dataValueDescriptor, Like.like(this.getCharArray(), this.getLength(), ((SQLChar)dataValueDescriptor).getCharArray(), dataValueDescriptor.getLength(), null));
    }
    
    public BooleanDataValue like(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        if (dataValueDescriptor2.isNull()) {
            throw StandardException.newException("22501");
        }
        final char[] charArray = this.getCharArray();
        final char[] charArray2 = ((SQLChar)dataValueDescriptor).getCharArray();
        final char[] charArray3 = ((SQLChar)dataValueDescriptor2).getCharArray();
        final int length = dataValueDescriptor2.getLength();
        if (charArray3 != null && length != 1) {
            throw StandardException.newException("22019", new String(charArray3));
        }
        return SQLBoolean.truthValue(this, dataValueDescriptor, Like.like(charArray, this.getLength(), charArray2, dataValueDescriptor.getLength(), charArray3, length, null));
    }
    
    public NumberDataValue locate(final StringDataValue stringDataValue, final NumberDataValue numberDataValue, NumberDataValue numberDataValue2) throws StandardException {
        if (numberDataValue2 == null) {
            numberDataValue2 = new SQLInteger();
        }
        int int1;
        if (numberDataValue.isNull()) {
            int1 = 1;
        }
        else {
            int1 = numberDataValue.getInt();
        }
        if (this.isNull() || stringDataValue.isNull()) {
            numberDataValue2.setToNull();
            return numberDataValue2;
        }
        final String string = stringDataValue.getString();
        final String string2 = this.getString();
        if (int1 < 1) {
            throw StandardException.newException("22014", this.getString(), string, new Integer(int1));
        }
        if (string2.length() == 0) {
            numberDataValue2.setValue(int1);
            return numberDataValue2;
        }
        numberDataValue2.setValue(string.indexOf(string2, int1 - 1) + 1);
        return numberDataValue2;
    }
    
    public ConcatableDataValue substring(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, ConcatableDataValue newVarchar, final int n) throws StandardException {
        if (newVarchar == null) {
            newVarchar = this.getNewVarchar();
        }
        final StringDataValue stringDataValue = (StringDataValue)newVarchar;
        if (this.isNull() || numberDataValue.isNull() || (numberDataValue2 != null && numberDataValue2.isNull())) {
            stringDataValue.setToNull();
            return stringDataValue;
        }
        int i = numberDataValue.getInt();
        int int1;
        if (numberDataValue2 != null) {
            int1 = numberDataValue2.getInt();
        }
        else {
            int1 = n - i + 1;
        }
        if (i <= 0 || int1 < 0 || i > n || int1 > n - i + 1) {
            throw StandardException.newException("22011");
        }
        if (int1 < 0) {
            stringDataValue.setToNull();
            return stringDataValue;
        }
        if (i < 0) {
            if (i + this.getLength() < 0 && i + this.getLength() + int1 <= 0) {
                stringDataValue.setValue("");
                return stringDataValue;
            }
            for (i += this.getLength(); i < 0; ++i, --int1) {}
        }
        else if (i > 0) {
            --i;
        }
        if (int1 == 0 || int1 <= 0 - i || i > this.getLength()) {
            stringDataValue.setValue("");
            return stringDataValue;
        }
        if (int1 >= this.getLength() - i) {
            stringDataValue.setValue(this.getString().substring(i));
        }
        else {
            stringDataValue.setValue(this.getString().substring(i, i + int1));
        }
        return stringDataValue;
    }
    
    private String trimInternal(final int n, final char c, final String s) {
        if (s == null) {
            return null;
        }
        final int length = s.length();
        int i = 0;
        if (n == 2 || n == 0) {
            while (i < length) {
                if (c != s.charAt(i)) {
                    break;
                }
                ++i;
            }
        }
        if (i == length) {
            return "";
        }
        int j = length - 1;
        if (n == 1 || n == 0) {
            while (j >= 0) {
                if (c != s.charAt(j)) {
                    break;
                }
                --j;
            }
        }
        if (j == -1) {
            return "";
        }
        return s.substring(i, j + 1);
    }
    
    public StringDataValue ansiTrim(final int n, final StringDataValue stringDataValue, StringDataValue newVarchar) throws StandardException {
        if (newVarchar == null) {
            newVarchar = this.getNewVarchar();
        }
        if (stringDataValue == null || stringDataValue.getString() == null) {
            newVarchar.setToNull();
            return newVarchar;
        }
        if (stringDataValue.getString().length() != 1) {
            throw StandardException.newException("22020", stringDataValue.getString());
        }
        newVarchar.setValue(this.trimInternal(n, stringDataValue.getString().charAt(0), this.getString()));
        return newVarchar;
    }
    
    public StringDataValue upper(StringDataValue stringDataValue) throws StandardException {
        if (stringDataValue == null) {
            stringDataValue = (StringDataValue)this.getNewNull();
        }
        if (this.isNull()) {
            stringDataValue.setToNull();
            return stringDataValue;
        }
        stringDataValue.setValue(this.getString().toUpperCase(this.getLocale()));
        return stringDataValue;
    }
    
    public StringDataValue lower(StringDataValue stringDataValue) throws StandardException {
        if (stringDataValue == null) {
            stringDataValue = (StringDataValue)this.getNewNull();
        }
        if (this.isNull()) {
            stringDataValue.setToNull();
            return stringDataValue;
        }
        stringDataValue.setValue(this.getString().toLowerCase(this.getLocale()));
        return stringDataValue;
    }
    
    public int typePrecedence() {
        return 0;
    }
    
    protected static int stringCompare(final String s, final String s2) {
        if (s == null || s2 == null) {
            if (s != null) {
                return -1;
            }
            if (s2 != null) {
                return 1;
            }
            return 0;
        }
        else {
            final int length = s.length();
            final int length2 = s2.length();
            final int n = (length < length2) ? length : length2;
            int i = 0;
            while (i < n) {
                final char char1 = s.charAt(i);
                final char char2 = s2.charAt(i);
                if (char1 != char2) {
                    if (char1 < char2) {
                        return -1;
                    }
                    return 1;
                }
                else {
                    ++i;
                }
            }
            if (length == length2) {
                return 0;
            }
            int n2;
            String s3;
            int j;
            int n3;
            if (length > length2) {
                n2 = -1;
                s3 = s;
                j = length2;
                n3 = length;
            }
            else {
                n2 = 1;
                s3 = s2;
                j = length;
                n3 = length2;
            }
            while (j < n3) {
                final char char3 = s3.charAt(j);
                if (char3 < ' ') {
                    return n2;
                }
                if (char3 > ' ') {
                    return -n2;
                }
                ++j;
            }
            return 0;
        }
    }
    
    protected int stringCompare(final SQLChar sqlChar, final SQLChar sqlChar2) throws StandardException {
        return stringCompare(sqlChar.getCharArray(), sqlChar.getLength(), sqlChar2.getCharArray(), sqlChar2.getLength());
    }
    
    protected static int stringCompare(final char[] array, final int n, final char[] array2, final int n2) {
        if (array == null || array2 == null) {
            if (array != null) {
                return -1;
            }
            if (array2 != null) {
                return 1;
            }
            return 0;
        }
        else {
            final int n3 = (n < n2) ? n : n2;
            int i = 0;
            while (i < n3) {
                final char c = array[i];
                final char c2 = array2[i];
                if (c != c2) {
                    if (c < c2) {
                        return -1;
                    }
                    return 1;
                }
                else {
                    ++i;
                }
            }
            if (n == n2) {
                return 0;
            }
            int n4;
            char[] array3;
            int j;
            int n5;
            if (n > n2) {
                n4 = -1;
                array3 = array;
                j = n2;
                n5 = n;
            }
            else {
                n4 = 1;
                array3 = array2;
                j = n;
                n5 = n2;
            }
            while (j < n5) {
                final char c3 = array3[j];
                if (c3 < ' ') {
                    return n4;
                }
                if (c3 > ' ') {
                    return -n4;
                }
                ++j;
            }
            return 0;
        }
    }
    
    protected CollationKey getCollationKey() throws StandardException {
        if (this.cKey != null) {
            return this.cKey;
        }
        if (this.rawLength == -1 && this.getCharArray() == null) {
            return null;
        }
        int rawLength;
        for (rawLength = this.rawLength; rawLength > 0 && this.rawData[rawLength - 1] == ' '; --rawLength) {}
        return this.cKey = this.getCollatorForCollation().getCollationKey(new String(this.rawData, 0, rawLength));
    }
    
    public String toString() {
        if (this.isNull()) {
            return "NULL";
        }
        if (this.value == null && this.rawLength != -1) {
            return new String(this.rawData, 0, this.rawLength);
        }
        if (this.stream != null) {
            try {
                return this.getString();
            }
            catch (Exception ex) {
                return ex.toString();
            }
        }
        return this.value;
    }
    
    public int hashCode() {
        try {
            if (this.getString() == null) {
                return 0;
            }
        }
        catch (StandardException ex) {
            return 0;
        }
        String value;
        int index;
        for (value = this.value, index = value.length() - 1; index >= 0 && value.charAt(index) == ' '; --index) {}
        int n = 0;
        for (int i = 0; i <= index; ++i) {
            n = n * 31 + value.charAt(i);
        }
        return n;
    }
    
    int hashCodeForCollation() {
        Object collationKey = null;
        try {
            collationKey = this.getCollationKey();
        }
        catch (StandardException ex) {}
        return (collationKey == null) ? 0 : collationKey.hashCode();
    }
    
    protected StringDataValue getNewVarchar() throws StandardException {
        return new SQLVarchar();
    }
    
    protected void setLocaleFinder(final LocaleFinder localeFinder) {
        this.localeFinder = localeFinder;
    }
    
    private Locale getLocale() throws StandardException {
        return this.getLocaleFinder().getCurrentLocale();
    }
    
    protected RuleBasedCollator getCollatorForCollation() throws StandardException {
        return null;
    }
    
    protected LocaleFinder getLocaleFinder() {
        if (this.localeFinder == null) {
            final DatabaseContext databaseContext = (DatabaseContext)ContextService.getContext("Database");
            if (databaseContext != null) {
                this.localeFinder = databaseContext.getDatabase();
            }
        }
        return this.localeFinder;
    }
    
    public int estimateMemoryUsage() {
        int n = SQLChar.BASE_MEMORY_USAGE + ClassSize.estimateMemoryUsage(this.value);
        if (null != this.rawData) {
            n += 2 * this.rawData.length;
        }
        return n;
    }
    
    protected void copyState(final SQLChar sqlChar) {
        this.copyState(sqlChar.value, sqlChar.rawData, sqlChar.rawLength, sqlChar.cKey, sqlChar.stream, sqlChar._clobValue, sqlChar.localeFinder);
    }
    
    private void copyState(final String value, final char[] rawData, final int rawLength, final CollationKey cKey, final InputStream stream, final Clob clobValue, final LocaleFinder localeFinder) {
        this.value = value;
        this.rawData = rawData;
        this.rawLength = rawLength;
        this.cKey = cKey;
        this.stream = stream;
        this._clobValue = clobValue;
        this.localeFinder = localeFinder;
    }
    
    public String getTraceString() throws StandardException {
        if (this.isNull()) {
            return "NULL";
        }
        return this.toString();
    }
    
    public StreamHeaderGenerator getStreamHeaderGenerator() {
        return SQLChar.CHAR_HEADER_GENERATOR;
    }
    
    public void setStreamHeaderFormat(final Boolean b) {
    }
    
    private int getClobLength() throws StandardException {
        try {
            return this.rawGetClobLength();
        }
        catch (SQLException ex) {
            throw StandardException.plainWrapException(ex);
        }
    }
    
    private int rawGetClobLength() throws SQLException {
        final long i = 2147483647L;
        final long length = this._clobValue.length();
        if (length > 2147483647L) {
            throw new SQLException(StandardException.newException("XJ093.S", Long.toString(length), Long.toString(i)).getMessage());
        }
        return (int)length;
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLChar.class);
        BLANKS = new char[40];
        for (int i = 0; i < SQLChar.BLANKS.length; ++i) {
            SQLChar.BLANKS[i] = ' ';
        }
        CHAR_HEADER_GENERATOR = new CharStreamHeaderGenerator();
    }
}
