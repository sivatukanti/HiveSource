// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.cache.ClassSize;
import java.sql.SQLWarning;
import java.sql.DataTruncation;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.StatementContext;
import java.sql.PreparedStatement;
import java.io.EOFException;
import org.apache.derby.iapi.services.io.DerbyIOException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.io.InputStreamUtil;
import java.sql.SQLException;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.derby.iapi.services.io.FormatIdInputStream;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.error.StandardException;
import java.io.InputStream;
import java.sql.Blob;

abstract class SQLBinary extends DataType implements BitDataValue
{
    static final byte PAD = 32;
    private static final int BASE_MEMORY_USAGE;
    private static final int LEN_OF_BUFFER_TO_WRITE_BLOB = 1024;
    Blob _blobValue;
    byte[] dataValue;
    InputStream stream;
    int streamValueLength;
    
    public int estimateMemoryUsage() {
        if (this.dataValue != null) {
            return SQLBinary.BASE_MEMORY_USAGE + this.dataValue.length;
        }
        if (this.streamValueLength >= 0) {
            return SQLBinary.BASE_MEMORY_USAGE + this.streamValueLength;
        }
        return this.getMaxMemoryUsage();
    }
    
    abstract int getMaxMemoryUsage();
    
    SQLBinary() {
    }
    
    SQLBinary(final byte[] dataValue) {
        this.dataValue = dataValue;
    }
    
    SQLBinary(final Blob value) {
        this.setValue(value);
    }
    
    public final void setValue(final byte[] dataValue) {
        this.dataValue = dataValue;
        this._blobValue = null;
        this.stream = null;
        this.streamValueLength = -1;
    }
    
    public final void setValue(final Blob blobValue) {
        this.dataValue = null;
        this._blobValue = blobValue;
        this.stream = null;
        this.streamValueLength = -1;
    }
    
    public final String getString() throws StandardException {
        if (this.getValue() == null) {
            return null;
        }
        if (this.dataValue.length * 2 < 0) {
            throw StandardException.newException("22001", this.getTypeName(), "", String.valueOf(Integer.MAX_VALUE));
        }
        return StringUtil.toHexString(this.dataValue, 0, this.dataValue.length);
    }
    
    public final InputStream getStream() throws StandardException {
        if (!this.hasStream()) {
            throw StandardException.newException("42Z12.U", this.getTypeName());
        }
        return this.stream;
    }
    
    public final byte[] getBytes() throws StandardException {
        return this.getValue();
    }
    
    byte[] getValue() throws StandardException {
        try {
            if (this.dataValue == null && this._blobValue != null) {
                this.dataValue = this._blobValue.getBytes(1L, this.getBlobLength());
                this._blobValue = null;
                this.stream = null;
                this.streamValueLength = -1;
            }
            else if (this.dataValue == null && this.stream != null) {
                if (this.stream instanceof FormatIdInputStream) {
                    this.readExternal((ObjectInput)this.stream);
                }
                else {
                    this.readExternal(new FormatIdInputStream(this.stream));
                }
                this._blobValue = null;
                this.stream = null;
                this.streamValueLength = -1;
            }
        }
        catch (IOException ex) {
            this.throwStreamingIOException(ex);
        }
        catch (SQLException ex2) {
            throw StandardException.plainWrapException(ex2);
        }
        return this.dataValue;
    }
    
    public final int getLength() throws StandardException {
        if (this._blobValue != null) {
            return this.getBlobLength();
        }
        if (this.stream != null) {
            if (this.streamValueLength != -1) {
                return this.streamValueLength;
            }
            if (this.stream instanceof Resetable) {
                try {
                    this.streamValueLength = readBinaryLength((ObjectInput)this.stream);
                    if (this.streamValueLength == 0) {
                        this.streamValueLength = (int)InputStreamUtil.skipUntilEOF(this.stream);
                    }
                    return this.streamValueLength;
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
        }
        final byte[] bytes = this.getBytes();
        return (bytes == null) ? 0 : bytes.length;
    }
    
    private void throwStreamingIOException(final IOException ex) throws StandardException {
        throw StandardException.newException("XCL30.S", ex, this.getTypeName());
    }
    
    public final boolean isNull() {
        return this.dataValue == null && this.stream == null && this._blobValue == null;
    }
    
    public final void writeExternal(final ObjectOutput objectOutput) throws IOException {
        if (this._blobValue != null) {
            this.writeBlob(objectOutput);
            return;
        }
        this.writeLength(objectOutput, this.dataValue.length);
        objectOutput.write(this.dataValue, 0, this.dataValue.length);
    }
    
    private void writeBlob(final ObjectOutput objectOutput) throws IOException {
        try {
            final int blobLength = this.getBlobLength();
            final InputStream binaryStream = this._blobValue.getBinaryStream();
            this.writeLength(objectOutput, blobLength);
            int i = 0;
            final byte[] b = new byte[Math.min(blobLength, 1024)];
            while (i < blobLength) {
                final int read = binaryStream.read(b);
                if (read == -1) {
                    throw new DerbyIOException(MessageService.getTextMessage("XJ023.S"), "XJ023.S");
                }
                objectOutput.write(b, 0, read);
                i += read;
            }
        }
        catch (StandardException ex) {
            throw new IOException(ex.getMessage());
        }
        catch (SQLException ex2) {
            throw new IOException(ex2.getMessage());
        }
    }
    
    private void writeLength(final ObjectOutput objectOutput, final int n) throws IOException {
        if (n <= 31) {
            objectOutput.write((byte)(0x80 | (n & 0xFF)));
        }
        else if (n <= 65535) {
            objectOutput.write(-96);
            objectOutput.writeShort((short)n);
        }
        else {
            objectOutput.write(-64);
            objectOutput.writeInt(n);
        }
    }
    
    public final void readExternal(final ObjectInput objectInput) throws IOException {
        this.stream = null;
        this.streamValueLength = -1;
        this._blobValue = null;
        final int binaryLength = readBinaryLength(objectInput);
        if (binaryLength != 0) {
            objectInput.readFully(this.dataValue = new byte[binaryLength]);
        }
        else {
            this.readFromStream((InputStream)objectInput);
        }
    }
    
    private static int readBinaryLength(final ObjectInput objectInput) throws IOException {
        final int read = objectInput.read();
        if (read == -1) {
            throw new EOFException();
        }
        final byte b = (byte)read;
        int n;
        if ((b & 0xFFFFFF80) != 0x0) {
            if (b == -64) {
                n = objectInput.readInt();
            }
            else if (b == -96) {
                n = objectInput.readUnsignedShort();
            }
            else {
                n = (b & 0x1F);
            }
        }
        else {
            final int read2 = objectInput.read();
            final int read3 = objectInput.read();
            final int read4 = objectInput.read();
            if (read2 == -1 || read3 == -1 || read4 == -1) {
                throw new EOFException();
            }
            final int n2 = (read & 0xFF) << 24 | (read2 & 0xFF) << 16 | (read3 & 0xFF) << 8 | (read4 & 0xFF);
            n = n2 / 8;
            if (n2 % 8 != 0) {
                ++n;
            }
        }
        return n;
    }
    
    private void readFromStream(final InputStream inputStream) throws IOException {
        this.dataValue = null;
        byte[] b = new byte[32768];
        int off = 0;
        while (true) {
            final int read = inputStream.read(b, off, b.length - off);
            if (read == -1) {
                break;
            }
            off += read;
            final int n = Math.max(1, inputStream.available()) - (b.length - off);
            if (n <= 0) {
                continue;
            }
            int n2 = b.length * 2;
            if (n > b.length) {
                n2 += n;
            }
            final byte[] array = new byte[n2];
            System.arraycopy(b, 0, array, 0, off);
            b = array;
        }
        System.arraycopy(b, 0, this.dataValue = new byte[off], 0, off);
    }
    
    public final void restoreToNull() {
        this.dataValue = null;
        this._blobValue = null;
        this.stream = null;
        this.streamValueLength = -1;
    }
    
    public final boolean compare(final int n, final DataValueDescriptor dataValueDescriptor, final boolean b, final boolean b2) throws StandardException {
        if (!b && (this.isNull() || dataValueDescriptor.isNull())) {
            return b2;
        }
        return super.compare(n, dataValueDescriptor, b, b2);
    }
    
    public final int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
            return -dataValueDescriptor.compare(this);
        }
        if (!this.isNull() && !dataValueDescriptor.isNull()) {
            return compare(this.getBytes(), dataValueDescriptor.getBytes());
        }
        if (!this.isNull()) {
            return -1;
        }
        if (!dataValueDescriptor.isNull()) {
            return 1;
        }
        return 0;
    }
    
    public final DataValueDescriptor cloneHolder() {
        if (this.stream == null && this._blobValue == null) {
            return this.cloneValue(false);
        }
        final SQLBinary sqlBinary = (SQLBinary)this.getNewNull();
        if (this.stream != null) {
            sqlBinary.setValue(this.stream, this.streamValueLength);
        }
        else {
            if (this._blobValue == null) {
                throw new IllegalStateException("unknown BLOB value repr");
            }
            sqlBinary.setValue(this._blobValue);
        }
        return sqlBinary;
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        try {
            final DataValueDescriptor newNull = this.getNewNull();
            newNull.setValue(this.getValue());
            return newNull;
        }
        catch (StandardException ex) {
            return null;
        }
    }
    
    public final InputStream returnStream() {
        return this.stream;
    }
    
    public final void setStream(final InputStream stream) {
        this.dataValue = null;
        this._blobValue = null;
        this.stream = stream;
        this.streamValueLength = -1;
    }
    
    public final void loadStream() throws StandardException {
        this.getValue();
    }
    
    boolean objectNull(final Object o) {
        if (o == null) {
            this.setToNull();
            return true;
        }
        return false;
    }
    
    public final void setValue(final InputStream stream, final int streamValueLength) {
        this.dataValue = null;
        this._blobValue = null;
        this.stream = stream;
        this.streamValueLength = streamValueLength;
    }
    
    protected final void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor instanceof SQLBinary) {
            final SQLBinary sqlBinary = (SQLBinary)dataValueDescriptor;
            this.dataValue = sqlBinary.dataValue;
            this._blobValue = sqlBinary._blobValue;
            this.stream = sqlBinary.stream;
            this.streamValueLength = sqlBinary.streamValueLength;
        }
        else {
            this.setValue(dataValueDescriptor.getBytes());
        }
    }
    
    public final BooleanDataValue equals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, !dataValueDescriptor.isNull() && !dataValueDescriptor2.isNull() && compare(dataValueDescriptor.getBytes(), dataValueDescriptor2.getBytes()) == 0);
    }
    
    public final BooleanDataValue notEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, !dataValueDescriptor.isNull() && !dataValueDescriptor2.isNull() && compare(dataValueDescriptor.getBytes(), dataValueDescriptor2.getBytes()) != 0);
    }
    
    public final BooleanDataValue lessThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, !dataValueDescriptor.isNull() && !dataValueDescriptor2.isNull() && compare(dataValueDescriptor.getBytes(), dataValueDescriptor2.getBytes()) < 0);
    }
    
    public final BooleanDataValue greaterThan(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, !dataValueDescriptor.isNull() && !dataValueDescriptor2.isNull() && compare(dataValueDescriptor.getBytes(), dataValueDescriptor2.getBytes()) > 0);
    }
    
    public final BooleanDataValue lessOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, !dataValueDescriptor.isNull() && !dataValueDescriptor2.isNull() && compare(dataValueDescriptor.getBytes(), dataValueDescriptor2.getBytes()) <= 0);
    }
    
    public final BooleanDataValue greaterOrEquals(final DataValueDescriptor dataValueDescriptor, final DataValueDescriptor dataValueDescriptor2) throws StandardException {
        return SQLBoolean.truthValue(dataValueDescriptor, dataValueDescriptor2, !dataValueDescriptor.isNull() && !dataValueDescriptor2.isNull() && compare(dataValueDescriptor.getBytes(), dataValueDescriptor2.getBytes()) >= 0);
    }
    
    public final NumberDataValue charLength(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = new SQLInteger();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        numberDataValue.setValue(this.getValue().length);
        return numberDataValue;
    }
    
    public final BitDataValue concatenate(final BitDataValue bitDataValue, final BitDataValue bitDataValue2, BitDataValue bitDataValue3) throws StandardException {
        if (bitDataValue3 == null) {
            bitDataValue3 = (BitDataValue)this.getNewNull();
        }
        if (bitDataValue.isNull() || bitDataValue2.isNull()) {
            bitDataValue3.setToNull();
            return bitDataValue3;
        }
        final byte[] bytes = bitDataValue.getBytes();
        final byte[] bytes2 = bitDataValue2.getBytes();
        final byte[] value = new byte[bytes.length + bytes2.length];
        System.arraycopy(bytes, 0, value, 0, bytes.length);
        System.arraycopy(bytes2, 0, value, bytes.length, bytes2.length);
        bitDataValue3.setValue(value);
        return bitDataValue3;
    }
    
    public final ConcatableDataValue substring(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, ConcatableDataValue concatableDataValue, final int n) throws StandardException {
        if (concatableDataValue == null) {
            concatableDataValue = new SQLVarbit();
        }
        final BitDataValue bitDataValue = (BitDataValue)concatableDataValue;
        if (this.isNull() || numberDataValue.isNull() || (numberDataValue2 != null && numberDataValue2.isNull())) {
            bitDataValue.setToNull();
            return bitDataValue;
        }
        int int1 = numberDataValue.getInt();
        int int2;
        if (numberDataValue2 != null) {
            int2 = numberDataValue2.getInt();
        }
        else {
            int2 = this.getLength() - int1 + 1;
        }
        if (int1 <= 0 || int2 < 0 || int1 > this.getLength() || int2 > this.getLength() - int1 + 1) {
            throw StandardException.newException("22011");
        }
        if (int2 < 0) {
            bitDataValue.setToNull();
            return bitDataValue;
        }
        if (int1 < 0) {
            int1 += this.getLength();
            if (int1 < 0) {
                int2 += int1;
                int1 = 0;
            }
            if (int2 + int1 > 0) {
                int2 += int1;
            }
            else {
                int2 = 0;
            }
        }
        else if (int1 > 0) {
            --int1;
        }
        if (int2 == 0 || int2 <= 0 - int1 || int1 > this.getLength()) {
            bitDataValue.setValue(new byte[0]);
            return bitDataValue;
        }
        if (int2 >= this.getLength() - int1) {
            final byte[] value = new byte[this.dataValue.length - int1];
            System.arraycopy(this.dataValue, int1, value, 0, value.length);
            bitDataValue.setValue(value);
        }
        else {
            final byte[] value2 = new byte[int2];
            System.arraycopy(this.dataValue, int1, value2, 0, value2.length);
            bitDataValue.setValue(value2);
        }
        return bitDataValue;
    }
    
    public final void checkHostVariable(final int i) throws StandardException {
        int n = -1;
        if (this._blobValue != null) {
            n = -1;
        }
        else if (this.stream == null) {
            if (this.dataValue != null) {
                n = this.dataValue.length;
            }
        }
        else {
            n = this.streamValueLength;
        }
        if (n != -1 && n > i) {
            throw StandardException.newException("22001", this.getTypeName(), MessageService.getTextMessage("BIN01"), String.valueOf(i));
        }
    }
    
    public final String toString() {
        if (this.dataValue != null) {
            return StringUtil.toHexString(this.dataValue, 0, this.dataValue.length);
        }
        if (this.stream == null && this._blobValue == null) {
            return "NULL";
        }
        return "";
    }
    
    public final int hashCode() {
        try {
            if (this.getValue() == null) {
                return 0;
            }
        }
        catch (StandardException ex) {
            return 0;
        }
        byte[] dataValue;
        int n;
        for (dataValue = this.dataValue, n = dataValue.length - 1; n >= 0 && dataValue[n] == 32; --n) {}
        int n2 = 0;
        for (int i = 0; i <= n; ++i) {
            n2 = n2 * 31 + dataValue[i];
        }
        return n2;
    }
    
    private static int compare(final byte[] array, final byte[] array2) {
        int n = array.length;
        byte[] array3 = array2;
        if (array2.length < n) {
            n = array2.length;
            array3 = array;
        }
        for (int i = 0; i < n; ++i) {
            final int n2 = array[i] & 0xFF;
            final int n3 = array2[i] & 0xFF;
            if (n2 != n3) {
                return n2 - n3;
            }
        }
        int j = n;
        while (j < array3.length) {
            if (array3[j] == 32) {
                ++j;
            }
            else {
                if (array == array3) {
                    return 1;
                }
                return -1;
            }
        }
        return 0;
    }
    
    public void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException, StandardException {
        preparedStatement.setBytes(n, this.getBytes());
    }
    
    public final String getTraceString() throws StandardException {
        if (this.isNull()) {
            return "NULL";
        }
        if (this.hasStream()) {
            return this.getTypeName() + "(" + this.getStream().toString() + ")";
        }
        return this.getTypeName() + ":Length=" + this.getLength();
    }
    
    private int getBlobLength() throws StandardException {
        try {
            final long i = 2147483647L;
            final long length = this._blobValue.length();
            if (length > 2147483647L) {
                throw StandardException.newException("XJ093.S", Long.toString(length), Long.toString(i));
            }
            return (int)length;
        }
        catch (SQLException ex) {
            throw StandardException.plainWrapException(ex);
        }
    }
    
    void truncate(final int n, final int transferSize, final boolean b) throws StandardException {
        if (b) {
            ((StatementContext)ContextService.getContext("StatementContext")).getActivation().getResultSet().addWarning(new DataTruncation(-1, false, true, this.getLength(), transferSize));
        }
        final byte[] value = new byte[transferSize];
        System.arraycopy(this.getValue(), 0, value, 0, transferSize);
        this.setValue(value);
    }
    
    static {
        BASE_MEMORY_USAGE = ClassSize.estimateBaseFromCatalog(SQLBinary.class);
    }
}
