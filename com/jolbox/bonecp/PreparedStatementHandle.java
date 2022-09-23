// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp;

import org.slf4j.LoggerFactory;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Ref;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.sql.Blob;
import java.math.BigDecimal;
import java.sql.NClob;
import java.sql.SQLXML;
import java.sql.RowId;
import java.io.Reader;
import java.io.InputStream;
import java.sql.Array;
import java.sql.ParameterMetaData;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import java.sql.PreparedStatement;

public class PreparedStatementHandle extends StatementHandle implements PreparedStatement
{
    private PreparedStatement internalPreparedStatement;
    protected static final Logger logger;
    
    public PreparedStatementHandle(final PreparedStatement internalPreparedStatement, final String sql, final ConnectionHandle connectionHandle, final String cacheKey, final IStatementCache cache) {
        super(internalPreparedStatement, sql, cache, connectionHandle, cacheKey, connectionHandle.isLogStatementsEnabled());
        this.internalPreparedStatement = internalPreparedStatement;
        this.connectionHandle = connectionHandle;
        this.sql = sql;
        this.cache = cache;
    }
    
    public void addBatch() throws SQLException {
        this.checkClosed();
        try {
            if (this.logStatementsEnabled) {
                this.batchSQL.append(this.sql);
            }
            this.internalPreparedStatement.addBatch();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void clearParameters() throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.clearParameters();
            if (this.logStatementsEnabled) {
                this.logParams.clear();
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public boolean execute() throws SQLException {
        this.checkClosed();
        try {
            if (this.logStatementsEnabled) {
                PreparedStatementHandle.logger.debug(PoolUtil.fillLogParams(this.sql, this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, this.sql, this.logParams);
            }
            final boolean result = this.internalPreparedStatement.execute();
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, this.sql, this.logParams);
            }
            this.queryTimerEnd(this.sql, queryStartTime);
            return result;
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public ResultSet executeQuery() throws SQLException {
        this.checkClosed();
        try {
            if (this.logStatementsEnabled) {
                PreparedStatementHandle.logger.debug(PoolUtil.fillLogParams(this.sql, this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, this.sql, this.logParams);
            }
            final ResultSet result = this.internalPreparedStatement.executeQuery();
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, this.sql, this.logParams);
            }
            this.queryTimerEnd(this.sql, queryStartTime);
            return result;
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public int executeUpdate() throws SQLException {
        this.checkClosed();
        try {
            if (this.logStatementsEnabled) {
                PreparedStatementHandle.logger.debug(PoolUtil.fillLogParams(this.sql, this.logParams));
            }
            final long queryStartTime = this.queryTimerStart();
            if (this.connectionHook != null) {
                this.connectionHook.onBeforeStatementExecute(this.connectionHandle, this, this.sql, this.logParams);
            }
            final int result = this.internalPreparedStatement.executeUpdate();
            if (this.connectionHook != null) {
                this.connectionHook.onAfterStatementExecute(this.connectionHandle, this, this.sql, this.logParams);
            }
            this.queryTimerEnd(this.sql, queryStartTime);
            return result;
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public ResultSetMetaData getMetaData() throws SQLException {
        this.checkClosed();
        try {
            return this.internalPreparedStatement.getMetaData();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public ParameterMetaData getParameterMetaData() throws SQLException {
        this.checkClosed();
        try {
            return this.internalPreparedStatement.getParameterMetaData();
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setArray(final int parameterIndex, final Array x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setArray(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setBinaryStream(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setBinaryStream(parameterIndex, x, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setBlob(parameterIndex, inputStream);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, inputStream);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setAsciiStream(parameterIndex, x, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setClob(parameterIndex, reader);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setRowId(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setSQLXML(parameterIndex, xmlObject);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, xmlObject);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setClob(parameterIndex, reader, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setNCharacterStream(parameterIndex, value);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, value);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setNCharacterStream(parameterIndex, value, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, value);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setNClob(parameterIndex, value);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, value);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setNClob(parameterIndex, reader);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setNClob(parameterIndex, reader, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNString(final int parameterIndex, final String value) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setNString(parameterIndex, value);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, value);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setAsciiStream(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setCharacterStream(parameterIndex, reader, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setBlob(parameterIndex, inputStream, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, inputStream);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setCharacterStream(parameterIndex, reader);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setAsciiStream(parameterIndex, x, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setBigDecimal(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setBinaryStream(parameterIndex, x, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBlob(final int parameterIndex, final Blob x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setBlob(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setBoolean(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setByte(final int parameterIndex, final byte x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setByte(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setBytes(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setCharacterStream(parameterIndex, reader, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, reader);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setClob(final int parameterIndex, final Clob x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setClob(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setDate(final int parameterIndex, final Date x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setDate(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setDate(parameterIndex, x, cal);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, PoolUtil.safePrint(x, ", cal=", cal));
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setDouble(final int parameterIndex, final double x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setDouble(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setFloat(final int parameterIndex, final float x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setFloat(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setInt(final int parameterIndex, final int x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setInt(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setLong(final int parameterIndex, final long x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setLong(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setNull(parameterIndex, sqlType);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, "[SQL NULL of type " + sqlType + "]");
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setNull(final int parameterIndex, final int sqlType, final String typeName) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setNull(parameterIndex, sqlType, typeName);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, PoolUtil.safePrint("[SQL NULL of type ", sqlType, ", type = ", typeName, "]"));
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setObject(final int parameterIndex, final Object x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setObject(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setObject(parameterIndex, x, targetSqlType);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scaleOrLength) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setRef(final int parameterIndex, final Ref x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setRef(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setShort(final int parameterIndex, final short x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setShort(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setString(final int parameterIndex, final String x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setString(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setTime(final int parameterIndex, final Time x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setTime(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setTime(parameterIndex, x, cal);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, PoolUtil.safePrint(x, ", cal=", cal));
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setTimestamp(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setTimestamp(parameterIndex, x, cal);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, PoolUtil.safePrint(x, ", cal=", cal));
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public void setURL(final int parameterIndex, final URL x) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setURL(parameterIndex, x);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    @Deprecated
    public void setUnicodeStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.checkClosed();
        try {
            this.internalPreparedStatement.setUnicodeStream(parameterIndex, x, length);
            if (this.logStatementsEnabled) {
                this.logParams.put(parameterIndex, x);
            }
        }
        catch (SQLException e) {
            throw this.connectionHandle.markPossiblyBroken(e);
        }
    }
    
    public PreparedStatement getInternalPreparedStatement() {
        return this.internalPreparedStatement;
    }
    
    public void setInternalPreparedStatement(final PreparedStatement internalPreparedStatement) {
        this.internalPreparedStatement = internalPreparedStatement;
    }
    
    static {
        logger = LoggerFactory.getLogger(PreparedStatementHandle.class);
    }
}
