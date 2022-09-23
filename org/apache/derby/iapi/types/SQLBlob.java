// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.CloneableStream;
import java.sql.Blob;

public class SQLBlob extends SQLBinary
{
    public SQLBlob() {
    }
    
    public SQLBlob(final byte[] array) {
        super(array);
    }
    
    public SQLBlob(final Blob blob) {
        super(blob);
    }
    
    public String getTypeName() {
        return "BLOB";
    }
    
    int getMaxMemoryUsage() {
        return Integer.MAX_VALUE;
    }
    
    public boolean hasStream() {
        return this.stream != null;
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        final SQLBlob sqlBlob = new SQLBlob();
        if (this.isNull()) {
            return sqlBlob;
        }
        if (!b && this.dataValue == null) {
            if (this.stream != null && this.stream instanceof CloneableStream) {
                sqlBlob.setStream(((CloneableStream)this.stream).cloneStream());
                if (this.streamValueLength != -1) {
                    sqlBlob.streamValueLength = this.streamValueLength;
                }
            }
            else if (this._blobValue != null) {
                sqlBlob.setValue(this._blobValue);
            }
        }
        if (!sqlBlob.isNull()) {
            if (!b) {
                return sqlBlob;
            }
        }
        try {
            sqlBlob.setValue(this.getBytes());
        }
        catch (StandardException ex) {
            return null;
        }
        return sqlBlob;
    }
    
    public DataValueDescriptor getNewNull() {
        return new SQLBlob();
    }
    
    public Object getObject() throws StandardException {
        if (this._blobValue != null) {
            return this._blobValue;
        }
        final byte[] bytes = this.getBytes();
        if (bytes == null) {
            return null;
        }
        return new HarmonySerialBlob(bytes);
    }
    
    public void normalize(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor value) throws StandardException {
        this.setValue(value);
        this.setWidth(dataTypeDescriptor.getMaximumWidth(), 0, true);
    }
    
    public void setWidth(final int i, final int n, final boolean b) throws StandardException {
        if (this.isNull()) {
            return;
        }
        if (this.isLengthLess()) {
            return;
        }
        final int length = this.getLength();
        if (length > i) {
            if (b) {
                throw StandardException.newException("22001", this.getTypeName(), "XXXX", String.valueOf(i));
            }
            this.truncate(length, i, true);
        }
    }
    
    public int getTypeFormatId() {
        return 443;
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws SQLException, StandardException {
        final Blob blob = set.getBlob(n);
        if (blob == null) {
            this.setToNull();
        }
        else {
            this.setObject(blob);
        }
    }
    
    public int typePrecedence() {
        return 170;
    }
    
    public void setInto(final PreparedStatement preparedStatement, final int n) throws SQLException, StandardException {
        if (this.isNull()) {
            preparedStatement.setBlob(n, (Blob)null);
            return;
        }
        preparedStatement.setBytes(n, this.getBytes());
    }
    
    final void setObject(final Object o) throws StandardException {
        final Blob blob = (Blob)o;
        try {
            final long length = blob.length();
            if (length < 0L || length > 2147483647L) {
                throw this.outOfRange();
            }
            this.setValue(new RawToBinaryFormatStream(blob.getBinaryStream(), (int)length), (int)length);
        }
        catch (SQLException ex) {
            throw this.dataTypeConversion("DAN-438-tmp");
        }
    }
    
    private final boolean isLengthLess() {
        return this.stream != null && this.streamValueLength < 0;
    }
}
