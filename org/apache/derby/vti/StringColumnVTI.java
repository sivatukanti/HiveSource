// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.DateFormat;
import org.apache.derby.iapi.types.HarmonySerialClob;
import java.sql.Clob;
import org.apache.derby.iapi.types.HarmonySerialBlob;
import java.sql.Blob;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLException;

public abstract class StringColumnVTI extends VTITemplate
{
    private String[] _columnNames;
    private boolean _lastColumnWasNull;
    
    protected abstract String getRawColumn(final int p0) throws SQLException;
    
    public StringColumnVTI(final String[] columnNames) {
        this._columnNames = columnNames;
    }
    
    public int getColumnCount() {
        return this._columnNames.length;
    }
    
    public String getColumnName(final int n) {
        return this._columnNames[n - 1];
    }
    
    public boolean wasNull() throws SQLException {
        return this._lastColumnWasNull;
    }
    
    public int findColumn(final String anObject) throws SQLException {
        for (int length = this._columnNames.length, i = 0; i < length; ++i) {
            if (this._columnNames[i].equals(anObject)) {
                return i + 1;
            }
        }
        throw new SQLException("Unknown column name.");
    }
    
    public String getString(final int n) throws SQLException {
        final String rawColumn = this.getRawColumn(n);
        this.checkNull(rawColumn);
        return rawColumn;
    }
    
    public boolean getBoolean(final int n) throws SQLException {
        final String string = this.getString(n);
        return string != null && Boolean.valueOf(string);
    }
    
    public byte getByte(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return 0;
        }
        try {
            return Byte.valueOf(string);
        }
        catch (NumberFormatException ex) {
            throw this.wrap(ex);
        }
    }
    
    public short getShort(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return 0;
        }
        try {
            return Short.valueOf(string);
        }
        catch (NumberFormatException ex) {
            throw this.wrap(ex);
        }
    }
    
    public int getInt(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return 0;
        }
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex) {
            throw this.wrap(ex);
        }
    }
    
    public long getLong(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return 0L;
        }
        try {
            return Long.valueOf(string);
        }
        catch (NumberFormatException ex) {
            throw this.wrap(ex);
        }
    }
    
    public float getFloat(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return 0.0f;
        }
        try {
            return Float.parseFloat(string);
        }
        catch (NumberFormatException ex) {
            throw this.wrap(ex);
        }
    }
    
    public double getDouble(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return 0.0;
        }
        try {
            return Double.parseDouble(string);
        }
        catch (NumberFormatException ex) {
            throw this.wrap(ex);
        }
    }
    
    public BigDecimal getBigDecimal(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return null;
        }
        try {
            return new BigDecimal(string);
        }
        catch (NumberFormatException ex) {
            throw this.wrap(ex);
        }
    }
    
    public byte[] getBytes(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return null;
        }
        try {
            return string.getBytes("UTF-8");
        }
        catch (Throwable t) {
            throw new SQLException(t.getMessage());
        }
    }
    
    public Date getDate(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return null;
        }
        return new Date(this.parseDateTime(string));
    }
    
    public Time getTime(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return null;
        }
        return new Time(this.parseDateTime(string));
    }
    
    public Timestamp getTimestamp(final int n) throws SQLException {
        final String string = this.getString(n);
        if (string == null) {
            return null;
        }
        return new Timestamp(this.parseDateTime(string));
    }
    
    public InputStream getAsciiStream(final int n) throws SQLException {
        return this.getEncodedStream(this.getString(n), "US-ASCII");
    }
    
    public InputStream getBinaryStream(final int n) throws SQLException {
        if (this.getString(n) == null) {
            return null;
        }
        return new ByteArrayInputStream(this.getBytes(n));
    }
    
    public Blob getBlob(final int n) throws SQLException {
        if (this.getString(n) == null) {
            return null;
        }
        return new HarmonySerialBlob(this.getBytes(n));
    }
    
    public Clob getClob(final int n) throws SQLException {
        if (this.getString(n) == null) {
            return null;
        }
        return new HarmonySerialClob(this.getString(n));
    }
    
    private void checkNull(final String s) {
        this._lastColumnWasNull = (s == null);
    }
    
    private SQLException wrap(final Throwable t) {
        return new SQLException(t.getMessage());
    }
    
    private long parseDateTime(final String source) throws SQLException {
        try {
            return DateFormat.getDateTimeInstance().parse(source).getTime();
        }
        catch (ParseException ex) {
            throw this.wrap(ex);
        }
    }
    
    private InputStream getEncodedStream(final String s, final String charsetName) throws SQLException {
        if (s == null) {
            return null;
        }
        try {
            return new ByteArrayInputStream(s.getBytes(charsetName));
        }
        catch (UnsupportedEncodingException ex) {
            throw this.wrap(ex);
        }
    }
}
