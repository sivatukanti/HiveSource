// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp;

import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.ParameterMetaData;
import java.net.URL;
import java.util.Calendar;
import java.sql.ResultSetMetaData;
import java.sql.Array;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.io.Reader;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class DelegatingPreparedStatement extends DelegatingStatement implements PreparedStatement
{
    public DelegatingPreparedStatement(final DelegatingConnection c, final PreparedStatement s) {
        super(c, s);
    }
    
    @Override
    public boolean equals(final Object obj) {
        final PreparedStatement delegate = (PreparedStatement)this.getInnermostDelegate();
        if (delegate == null) {
            return false;
        }
        if (obj instanceof DelegatingPreparedStatement) {
            final DelegatingPreparedStatement s = (DelegatingPreparedStatement)obj;
            return delegate.equals(s.getInnermostDelegate());
        }
        return delegate.equals(obj);
    }
    
    public void setDelegate(final PreparedStatement s) {
        super.setDelegate(s);
        this._stmt = s;
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        this.checkOpen();
        try {
            return DelegatingResultSet.wrapResultSet(this, ((PreparedStatement)this._stmt).executeQuery());
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        this.checkOpen();
        try {
            return ((PreparedStatement)this._stmt).executeUpdate();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setNull(parameterIndex, sqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setBoolean(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setByte(final int parameterIndex, final byte x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setByte(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setShort(final int parameterIndex, final short x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setShort(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setInt(final int parameterIndex, final int x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setInt(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setLong(final int parameterIndex, final long x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setLong(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setFloat(final int parameterIndex, final float x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setFloat(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setDouble(final int parameterIndex, final double x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setDouble(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setBigDecimal(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setString(final int parameterIndex, final String x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setString(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setBytes(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setDate(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setTime(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setTimestamp(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setAsciiStream(parameterIndex, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    @Deprecated
    public void setUnicodeStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setUnicodeStream(parameterIndex, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setBinaryStream(parameterIndex, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void clearParameters() throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).clearParameters();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scale) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setObject(parameterIndex, x, targetSqlType, scale);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setObject(parameterIndex, x, targetSqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setObject(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public boolean execute() throws SQLException {
        this.checkOpen();
        try {
            return ((PreparedStatement)this._stmt).execute();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public void addBatch() throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).addBatch();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setCharacterStream(parameterIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setRef(final int i, final Ref x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setRef(i, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBlob(final int i, final Blob x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setBlob(i, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setClob(final int i, final Clob x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setClob(i, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setArray(final int i, final Array x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setArray(i, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        this.checkOpen();
        try {
            return ((PreparedStatement)this._stmt).getMetaData();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setDate(parameterIndex, x, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setTime(parameterIndex, x, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setTimestamp(parameterIndex, x, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNull(final int paramIndex, final int sqlType, final String typeName) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setNull(paramIndex, sqlType, typeName);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public String toString() {
        return this._stmt.toString();
    }
    
    @Override
    public void setURL(final int parameterIndex, final URL x) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setURL(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        this.checkOpen();
        try {
            return ((PreparedStatement)this._stmt).getParameterMetaData();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public void setRowId(final int parameterIndex, final RowId value) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setRowId(parameterIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNString(final int parameterIndex, final String value) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setNString(parameterIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setNCharacterStream(parameterIndex, value, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setNClob(parameterIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setClob(parameterIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setBlob(parameterIndex, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setNClob(parameterIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setSQLXML(final int parameterIndex, final SQLXML value) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setSQLXML(parameterIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setAsciiStream(parameterIndex, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setBinaryStream(parameterIndex, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setCharacterStream(parameterIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setAsciiStream(parameterIndex, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setBinaryStream(parameterIndex, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setCharacterStream(parameterIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setNCharacterStream(parameterIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setClob(parameterIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setBlob(parameterIndex, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            ((PreparedStatement)this._stmt).setNClob(parameterIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }
}
