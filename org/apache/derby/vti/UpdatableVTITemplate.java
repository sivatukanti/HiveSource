// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.sql.ParameterMetaData;
import java.net.URL;
import java.sql.Array;
import java.sql.Clob;
import java.sql.Ref;
import java.util.Calendar;
import java.sql.Connection;
import java.io.Reader;
import java.sql.Blob;
import java.sql.ResultSetMetaData;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLWarning;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public abstract class UpdatableVTITemplate implements PreparedStatement
{
    protected UpdatableVTITemplate() {
    }
    
    public ResultSet executeQuery(final String s) throws SQLException {
        throw new SQLException("executeQuery");
    }
    
    public int executeUpdate(final String s) throws SQLException {
        throw new SQLException("executeUpdate");
    }
    
    public void close() throws SQLException {
        throw new SQLException("close");
    }
    
    public SQLWarning getWarnings() throws SQLException {
        throw new SQLException("getWarnings");
    }
    
    public void clearWarnings() throws SQLException {
        throw new SQLException("clearWarnings");
    }
    
    public int getMaxFieldSize() throws SQLException {
        throw new SQLException("getMaxFieldSize");
    }
    
    public void setMaxFieldSize(final int n) throws SQLException {
        throw new SQLException("setMaxFieldSize");
    }
    
    public int getMaxRows() throws SQLException {
        throw new SQLException("getMaxRows");
    }
    
    public void setMaxRows(final int n) throws SQLException {
        throw new SQLException("setMaxRows");
    }
    
    public void setEscapeProcessing(final boolean b) throws SQLException {
        throw new SQLException("setEscapeProcessing");
    }
    
    public int getQueryTimeout() throws SQLException {
        throw new SQLException("getQueryTimeout");
    }
    
    public void setQueryTimeout(final int n) throws SQLException {
        throw new SQLException("setQueryTimeout");
    }
    
    public void addBatch(final String s) throws SQLException {
        throw new SQLException("addBatch");
    }
    
    public void clearBatch() throws SQLException {
        throw new SQLException("clearBatch");
    }
    
    public int[] executeBatch() throws SQLException {
        throw new SQLException("executeBatch");
    }
    
    public void cancel() throws SQLException {
        throw new SQLException("cancel");
    }
    
    public void setCursorName(final String s) throws SQLException {
        throw new SQLException("setCursorName");
    }
    
    public boolean execute(final String s) throws SQLException {
        throw new SQLException("execute");
    }
    
    public ResultSet getResultSet() throws SQLException {
        throw new SQLException("getResultSet");
    }
    
    public int getUpdateCount() throws SQLException {
        throw new SQLException("getUpdateCount");
    }
    
    public boolean getMoreResults() throws SQLException {
        throw new SQLException("getMoreResults");
    }
    
    public int getResultSetConcurrency() throws SQLException {
        return 1008;
    }
    
    public ResultSet executeQuery() throws SQLException {
        throw new SQLException("executeQuery");
    }
    
    public int executeUpdate() throws SQLException {
        throw new SQLException("executeUpdate");
    }
    
    public void setNull(final int n, final int n2) throws SQLException {
        throw new SQLException("setNull");
    }
    
    public void setNull(final int n, final int n2, final String s) throws SQLException {
        throw new SQLException("setNull");
    }
    
    public void setBoolean(final int n, final boolean b) throws SQLException {
        throw new SQLException("setBoolean");
    }
    
    public void setByte(final int n, final byte b) throws SQLException {
        throw new SQLException("setByte");
    }
    
    public void setShort(final int n, final short n2) throws SQLException {
        throw new SQLException("setShort");
    }
    
    public void setInt(final int n, final int n2) throws SQLException {
        throw new SQLException("setInt");
    }
    
    public void setLong(final int n, final long n2) throws SQLException {
        throw new SQLException("setLong");
    }
    
    public void setFloat(final int n, final float n2) throws SQLException {
        throw new SQLException("setFloat");
    }
    
    public void setDouble(final int n, final double n2) throws SQLException {
        throw new SQLException("setDouble");
    }
    
    public void setBigDecimal(final int n, final BigDecimal bigDecimal) throws SQLException {
        throw new SQLException("setBigDecimal");
    }
    
    public void setString(final int n, final String s) throws SQLException {
        throw new SQLException("setString");
    }
    
    public void setBytes(final int n, final byte[] array) throws SQLException {
        throw new SQLException("setBytes");
    }
    
    public void setDate(final int n, final Date date) throws SQLException {
        throw new SQLException("setDate");
    }
    
    public void setTime(final int n, final Time time) throws SQLException {
        throw new SQLException("setTime");
    }
    
    public void setTimestamp(final int n, final Timestamp timestamp) throws SQLException {
        throw new SQLException("setTimestamp");
    }
    
    public void setAsciiStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        throw new SQLException("setAsciiStream");
    }
    
    public void setUnicodeStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        throw new SQLException("setUnicodeStream");
    }
    
    public void setBinaryStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        throw new SQLException("setBinaryStream");
    }
    
    public void clearParameters() throws SQLException {
        throw new SQLException("clearParameters");
    }
    
    public void setObject(final int n, final Object o, final int n2, final int n3) throws SQLException {
        throw new SQLException("setObject");
    }
    
    public void setObject(final int n, final Object o, final int n2) throws SQLException {
        throw new SQLException("setObject");
    }
    
    public void setObject(final int n, final Object o) throws SQLException {
        throw new SQLException("setObject");
    }
    
    public boolean execute() throws SQLException {
        throw new SQLException("execute");
    }
    
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLException("ResultSetMetaData");
    }
    
    public int getResultSetType() throws SQLException {
        throw new SQLException("getResultSetType");
    }
    
    public void setBlob(final int n, final Blob blob) throws SQLException {
        throw new SQLException("setBlob");
    }
    
    public void setFetchDirection(final int n) throws SQLException {
        throw new SQLException("setFetchDirection");
    }
    
    public void setFetchSize(final int n) throws SQLException {
        throw new SQLException("setFetchSize");
    }
    
    public void addBatch() throws SQLException {
        throw new SQLException("addBatch");
    }
    
    public void setCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        throw new SQLException("setCharacterStream");
    }
    
    public Connection getConnection() throws SQLException {
        throw new SQLException("getConnection");
    }
    
    public int getFetchDirection() throws SQLException {
        throw new SQLException("getFetchDirection");
    }
    
    public void setTime(final int n, final Time time, final Calendar calendar) throws SQLException {
        throw new SQLException("setTime");
    }
    
    public void setTimestamp(final int n, final Timestamp timestamp, final Calendar calendar) throws SQLException {
        throw new SQLException("setTimestamp");
    }
    
    public int getFetchSize() throws SQLException {
        throw new SQLException("getFetchSize");
    }
    
    public void setRef(final int n, final Ref ref) throws SQLException {
        throw new SQLException("setRef");
    }
    
    public void setDate(final int n, final Date date, final Calendar calendar) throws SQLException {
        throw new SQLException("setDate");
    }
    
    public void setClob(final int n, final Clob clob) throws SQLException {
        throw new SQLException("setClob");
    }
    
    public void setArray(final int n, final Array array) throws SQLException {
        throw new SQLException("setArray");
    }
    
    public void setURL(final int n, final URL url) throws SQLException {
        throw new SQLException("setURL");
    }
    
    public boolean getMoreResults(final int n) throws SQLException {
        throw new SQLException("getMoreResults");
    }
    
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLException("getGeneratedKeys");
    }
    
    public int executeUpdate(final String s, final int n) throws SQLException {
        throw new SQLException("executeUpdate");
    }
    
    public int executeUpdate(final String s, final int[] array) throws SQLException {
        throw new SQLException("executeUpdate");
    }
    
    public int executeUpdate(final String s, final String[] array) throws SQLException {
        throw new SQLException("executeUpdate");
    }
    
    public boolean execute(final String s, final int n) throws SQLException {
        throw new SQLException("execute");
    }
    
    public boolean execute(final String s, final int[] array) throws SQLException {
        throw new SQLException("execute");
    }
    
    public boolean execute(final String s, final String[] array) throws SQLException {
        throw new SQLException("execute");
    }
    
    public int getResultSetHoldability() throws SQLException {
        throw new SQLException("getResultSetHoldability");
    }
    
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new SQLException("getParameterMetaData");
    }
}
