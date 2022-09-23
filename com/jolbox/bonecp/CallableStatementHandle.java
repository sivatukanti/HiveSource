// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Ref;
import java.util.Map;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.InputStream;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.NClob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;

public class CallableStatementHandle extends PreparedStatementHandle implements CallableStatement
{
    private CallableStatement internalCallableStatement;
    
    public CallableStatementHandle(final CallableStatement internalCallableStatement, final String sql, final ConnectionHandle connectionHandle, final String cacheKey, final IStatementCache cache) {
        super(internalCallableStatement, sql, connectionHandle, cacheKey, cache);
        this.internalCallableStatement = internalCallableStatement;
        this.connectionHandle = connectionHandle;
        this.sql = sql;
        this.cache = cache;
    }
    
    public Array getArray(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getArray(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Array getArray(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getArray(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public BigDecimal getBigDecimal(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getBigDecimal(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public BigDecimal getBigDecimal(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getBigDecimal(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    @Deprecated
    public BigDecimal getBigDecimal(final int parameterIndex, final int scale) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getBigDecimal(parameterIndex, scale);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Blob getBlob(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getBlob(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Blob getBlob(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getBlob(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public boolean getBoolean(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getBoolean(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public boolean getBoolean(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getBoolean(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public byte getByte(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getByte(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public byte getByte(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getByte(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public byte[] getBytes(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getBytes(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public byte[] getBytes(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getBytes(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Reader getCharacterStream(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getCharacterStream(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Reader getCharacterStream(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getCharacterStream(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Reader getNCharacterStream(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getNCharacterStream(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Reader getNCharacterStream(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getNCharacterStream(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public NClob getNClob(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getNClob(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public NClob getNClob(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getNClob(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public String getNString(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getNString(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public String getNString(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getNString(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public RowId getRowId(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getRowId(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public RowId getRowId(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getRowId(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public SQLXML getSQLXML(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getSQLXML(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public SQLXML getSQLXML(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getSQLXML(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setAsciiStream(final String parameterName, final InputStream x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setAsciiStream(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setAsciiStream(final String parameterName, final InputStream x, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setAsciiStream(parameterName, x, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBinaryStream(final String parameterName, final InputStream x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setBinaryStream(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBinaryStream(final String parameterName, final InputStream x, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setBinaryStream(parameterName, x, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBlob(final String parameterName, final Blob x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setBlob(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBlob(final String parameterName, final InputStream inputStream) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setBlob(parameterName, inputStream);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, inputStream);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBlob(final String parameterName, final InputStream inputStream, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setBlob(parameterName, inputStream, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, inputStream);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setCharacterStream(final String parameterName, final Reader reader) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setCharacterStream(parameterName, reader);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setCharacterStream(final String parameterName, final Reader reader, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setCharacterStream(parameterName, reader, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setClob(final String parameterName, final Clob x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setClob(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setClob(final String parameterName, final Reader reader) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setClob(parameterName, reader);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setClob(final String parameterName, final Reader reader, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setClob(parameterName, reader, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNCharacterStream(final String parameterName, final Reader value) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setNCharacterStream(parameterName, value);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, value);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNCharacterStream(final String parameterName, final Reader value, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setNCharacterStream(parameterName, value, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, value);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNClob(final String parameterName, final NClob value) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setNClob(parameterName, value);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, value);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNClob(final String parameterName, final Reader reader) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setNClob(parameterName, reader);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNClob(final String parameterName, final Reader reader, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setNClob(parameterName, reader, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNString(final String parameterName, final String value) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setNString(parameterName, value);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, value);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setRowId(final String parameterName, final RowId x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setRowId(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setSQLXML(final String parameterName, final SQLXML xmlObject) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setSQLXML(parameterName, xmlObject);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, xmlObject);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public <T> T getObject(final int parameterIndex, final Class<T> type) throws SQLException {
        return this.internalCallableStatement.getObject(parameterIndex, type);
    }
    
    public <T> T getObject(final String parameterName, final Class<T> type) throws SQLException {
        return this.internalCallableStatement.getObject(parameterName, type);
    }
    
    public Clob getClob(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getClob(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Clob getClob(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getClob(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Date getDate(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getDate(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Date getDate(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getDate(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Date getDate(final int parameterIndex, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getDate(parameterIndex, cal);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Date getDate(final String parameterName, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getDate(parameterName, cal);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public double getDouble(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getDouble(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public double getDouble(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getDouble(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public float getFloat(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getFloat(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public float getFloat(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getFloat(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public int getInt(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getInt(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public int getInt(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getInt(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public long getLong(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getLong(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public long getLong(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getLong(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Object getObject(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getObject(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Object getObject(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getObject(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Object getObject(final int parameterIndex, final Map<String, Class<?>> map) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getObject(parameterIndex, map);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Object getObject(final String parameterName, final Map<String, Class<?>> map) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getObject(parameterName, map);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Ref getRef(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getRef(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Ref getRef(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getRef(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public short getShort(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getShort(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public short getShort(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getShort(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public String getString(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getString(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public String getString(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getString(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Time getTime(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getTime(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Time getTime(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getTime(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Time getTime(final int parameterIndex, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getTime(parameterIndex, cal);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Time getTime(final String parameterName, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getTime(parameterName, cal);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Timestamp getTimestamp(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getTimestamp(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Timestamp getTimestamp(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getTimestamp(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Timestamp getTimestamp(final int parameterIndex, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getTimestamp(parameterIndex, cal);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public Timestamp getTimestamp(final String parameterName, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getTimestamp(parameterName, cal);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public URL getURL(final int parameterIndex) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getURL(parameterIndex);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public URL getURL(final String parameterName) throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.getURL(parameterName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void registerOutParameter(final int parameterIndex, final int sqlType) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.registerOutParameter(parameterIndex, sqlType);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void registerOutParameter(final String parameterName, final int sqlType) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.registerOutParameter(parameterName, sqlType);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void registerOutParameter(final int parameterIndex, final int sqlType, final int scale) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.registerOutParameter(parameterIndex, sqlType, scale);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void registerOutParameter(final int parameterIndex, final int sqlType, final String typeName) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.registerOutParameter(parameterIndex, sqlType, typeName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void registerOutParameter(final String parameterName, final int sqlType, final int scale) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.registerOutParameter(parameterName, sqlType, scale);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void registerOutParameter(final String parameterName, final int sqlType, final String typeName) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.registerOutParameter(parameterName, sqlType, typeName);
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setAsciiStream(final String parameterName, final InputStream x, final int length) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setAsciiStream(parameterName, x, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBigDecimal(final String parameterName, final BigDecimal x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setBigDecimal(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBinaryStream(final String parameterName, final InputStream x, final int length) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setBinaryStream(parameterName, x, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBoolean(final String parameterName, final boolean x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setBoolean(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setByte(final String parameterName, final byte x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setByte(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBytes(final String parameterName, final byte[] x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setBytes(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setCharacterStream(final String parameterName, final Reader reader, final int length) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setCharacterStream(parameterName, reader, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setDate(final String parameterName, final Date x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setDate(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setDate(final String parameterName, final Date x, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setDate(parameterName, x, cal);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, PoolUtil.safePrint(x, ", cal=", cal));
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setDouble(final String parameterName, final double x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setDouble(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setFloat(final String parameterName, final float x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setFloat(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setInt(final String parameterName, final int x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setInt(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setLong(final String parameterName, final long x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setLong(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNull(final String parameterName, final int sqlType) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setNull(parameterName, sqlType);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, PoolUtil.safePrint("[SQL NULL type ", sqlType, "]"));
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNull(final String parameterName, final int sqlType, final String typeName) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setNull(parameterName, sqlType, typeName);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, PoolUtil.safePrint("[SQL NULL type ", sqlType, ", type=", typeName + "]"));
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setObject(final String parameterName, final Object x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setObject(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setObject(final String parameterName, final Object x, final int targetSqlType) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setObject(parameterName, x, targetSqlType);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setObject(final String parameterName, final Object x, final int targetSqlType, final int scale) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setObject(parameterName, x, targetSqlType, scale);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setShort(final String parameterName, final short x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setShort(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setString(final String parameterName, final String x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setString(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setTime(final String parameterName, final Time x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setTime(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setTime(final String parameterName, final Time x, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setTime(parameterName, x, cal);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, PoolUtil.safePrint(x, ", cal=", cal));
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setTimestamp(final String parameterName, final Timestamp x) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setTimestamp(parameterName, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setTimestamp(final String parameterName, final Timestamp x, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setTimestamp(parameterName, x, cal);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, PoolUtil.safePrint(x, ", cal=", cal));
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setURL(final String parameterName, final URL val) throws SQLException {
        this.checkClosed();
        try {
            this.internalCallableStatement.setURL(parameterName, val);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterName, val);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public boolean wasNull() throws SQLException {
        this.checkClosed();
        try {
            return this.internalCallableStatement.wasNull();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public CallableStatement getInternalCallableStatement() {
        return this.internalCallableStatement;
    }
    
    public void setInternalCallableStatement(final CallableStatement internalCallableStatement) {
        this.internalCallableStatement = internalCallableStatement;
    }
}
