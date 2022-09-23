// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import java.util.HashMap;
import java.util.Map;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.NClob;
import java.net.URL;
import org.datanucleus.exceptions.NucleusUserException;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Ref;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.Array;
import java.sql.SQLWarning;
import java.sql.ParameterMetaData;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.List;
import java.sql.PreparedStatement;

class ParamLoggingPreparedStatement implements PreparedStatement
{
    private final PreparedStatement ps;
    private SubStatement currentStatement;
    private List subStatements;
    private boolean paramAngleBrackets;
    private static final String DN_UNPRINTABLE = "DN_UNPRINTABLE";
    
    public ParamLoggingPreparedStatement(final PreparedStatement ps, final String jdbcSql) {
        this.currentStatement = null;
        this.subStatements = null;
        this.paramAngleBrackets = true;
        this.ps = ps;
        this.currentStatement = new SubStatement(jdbcSql);
    }
    
    public void setParamsInAngleBrackets(final boolean flag) {
        this.paramAngleBrackets = flag;
    }
    
    public String getStatementWithParamsReplaced() {
        final StringBuffer statementWithParams = new StringBuffer();
        if (this.subStatements == null) {
            return this.getStatementWithParamsReplacedForSubStatement(this.currentStatement);
        }
        statementWithParams.append("BATCH [");
        final Iterator iter = this.subStatements.iterator();
        while (iter.hasNext()) {
            final SubStatement stParams = iter.next();
            final String stmt = this.getStatementWithParamsReplacedForSubStatement(stParams);
            statementWithParams.append(stmt);
            if (iter.hasNext()) {
                statementWithParams.append("; ");
            }
        }
        statementWithParams.append("]");
        return statementWithParams.toString();
    }
    
    private String getStatementWithParamsReplacedForSubStatement(final SubStatement stParams) {
        final StringBuffer statementWithParams = new StringBuffer();
        final StringTokenizer tokenizer = new StringTokenizer(stParams.statementText, "?", true);
        int i = 1;
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if (token.equals("?")) {
                Object paramValue = "DN_UNPRINTABLE";
                final Integer paramPos = i++;
                if (stParams.parameters.containsKey(paramPos)) {
                    paramValue = stParams.parameters.get(paramPos);
                }
                this.appendParamValue(statementWithParams, paramValue);
            }
            else {
                statementWithParams.append(token);
            }
        }
        if (i > 1) {
            return statementWithParams.toString();
        }
        return stParams.statementText;
    }
    
    private void appendParamValue(final StringBuffer statementWithParams, final Object paramValue) {
        if (this.paramAngleBrackets) {
            if (paramValue instanceof String) {
                if (paramValue.equals("DN_UNPRINTABLE")) {
                    statementWithParams.append("<UNPRINTABLE>");
                }
                else {
                    statementWithParams.append("<'" + paramValue + "'>");
                }
            }
            else {
                statementWithParams.append("<" + paramValue + ">");
            }
        }
        else if (paramValue instanceof String) {
            if (paramValue.equals("DN_UNPRINTABLE")) {
                statementWithParams.append("<UNPRINTABLE'>");
            }
            else {
                statementWithParams.append("'" + paramValue + "'");
            }
        }
        else {
            statementWithParams.append("" + paramValue);
        }
    }
    
    private void setParameter(final int i, final Object p) {
        this.currentStatement.parameters.put(i, p);
    }
    
    public Object getParameter(final int i) {
        return this.currentStatement.parameters.get(i);
    }
    
    @Override
    public void addBatch() throws SQLException {
        final SubStatement newSubStmt = new SubStatement(this.currentStatement.statementText);
        newSubStmt.parameters.putAll(this.currentStatement.parameters);
        if (this.subStatements == null) {
            this.subStatements = new ArrayList();
        }
        this.subStatements.add(newSubStmt);
        this.ps.addBatch();
    }
    
    @Override
    public void addBatch(final String sql) throws SQLException {
        final SubStatement newSubStmt = new SubStatement(sql);
        newSubStmt.parameters.putAll(this.currentStatement.parameters);
        if (this.subStatements == null) {
            this.subStatements = new ArrayList();
        }
        this.subStatements.add(newSubStmt);
        this.ps.addBatch(sql);
    }
    
    @Override
    public void cancel() throws SQLException {
        this.ps.cancel();
    }
    
    @Override
    public void clearBatch() throws SQLException {
        if (this.subStatements != null) {
            this.subStatements.clear();
        }
        this.ps.clearBatch();
    }
    
    @Override
    public void clearParameters() throws SQLException {
        this.currentStatement.parameters.clear();
        if (this.subStatements != null) {
            for (final SubStatement subStmt : this.subStatements) {
                subStmt.parameters.clear();
            }
        }
        this.ps.clearParameters();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.ps.clearWarnings();
    }
    
    @Override
    public void close() throws SQLException {
        this.ps.close();
    }
    
    @Override
    public boolean execute() throws SQLException {
        return this.ps.execute();
    }
    
    @Override
    public boolean execute(final String sql, final int autoGeneratedKeys) throws SQLException {
        return this.ps.execute(sql, autoGeneratedKeys);
    }
    
    @Override
    public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
        return this.ps.execute(sql, columnIndexes);
    }
    
    @Override
    public boolean execute(final String sql, final String[] columnNames) throws SQLException {
        return this.ps.execute(sql, columnNames);
    }
    
    @Override
    public boolean execute(final String sql) throws SQLException {
        return this.ps.execute(sql);
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        return this.ps.executeBatch();
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        return this.ps.executeQuery();
    }
    
    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        return this.ps.executeQuery(sql);
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        return this.ps.executeUpdate();
    }
    
    @Override
    public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        return this.ps.executeUpdate(sql, autoGeneratedKeys);
    }
    
    @Override
    public int executeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
        return this.ps.executeUpdate(sql, columnIndexes);
    }
    
    @Override
    public int executeUpdate(final String sql, final String[] columnNames) throws SQLException {
        return this.ps.executeUpdate(sql, columnNames);
    }
    
    @Override
    public int executeUpdate(final String sql) throws SQLException {
        return this.ps.executeUpdate(sql);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.ps.getConnection();
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        return this.ps.getFetchDirection();
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        return this.ps.getFetchSize();
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return this.ps.getGeneratedKeys();
    }
    
    @Override
    public int getMaxFieldSize() throws SQLException {
        return this.ps.getMaxFieldSize();
    }
    
    @Override
    public int getMaxRows() throws SQLException {
        return this.ps.getMaxRows();
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.ps.getMetaData();
    }
    
    @Override
    public boolean getMoreResults() throws SQLException {
        return this.ps.getMoreResults();
    }
    
    @Override
    public boolean getMoreResults(final int current) throws SQLException {
        return this.ps.getMoreResults(current);
    }
    
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return this.ps.getParameterMetaData();
    }
    
    @Override
    public int getQueryTimeout() throws SQLException {
        return this.ps.getQueryTimeout();
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        return this.ps.getResultSet();
    }
    
    @Override
    public int getResultSetConcurrency() throws SQLException {
        return this.ps.getResultSetConcurrency();
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.ps.getResultSetHoldability();
    }
    
    @Override
    public int getResultSetType() throws SQLException {
        return this.ps.getResultSetType();
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        return this.ps.getUpdateCount();
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.ps.getWarnings();
    }
    
    @Override
    public void setArray(final int i, final Array x) throws SQLException {
        this.setParameter(i, x);
        this.ps.setArray(i, x);
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.ps.setAsciiStream(parameterIndex, x, length);
    }
    
    @Override
    public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setBigDecimal(parameterIndex, x);
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.ps.setBinaryStream(parameterIndex, x, length);
    }
    
    @Override
    public void setBlob(final int i, final Blob x) throws SQLException {
        this.ps.setBlob(i, x);
    }
    
    @Override
    public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setBoolean(parameterIndex, x);
    }
    
    @Override
    public void setByte(final int parameterIndex, final byte x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setByte(parameterIndex, x);
    }
    
    @Override
    public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
        this.ps.setBytes(parameterIndex, x);
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
        this.ps.setCharacterStream(parameterIndex, reader, length);
    }
    
    @Override
    public void setClob(final int i, final Clob x) throws SQLException {
        this.ps.setClob(i, x);
    }
    
    @Override
    public void setCursorName(final String name) throws SQLException {
        this.ps.setCursorName(name);
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setDate(parameterIndex, x, cal);
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setDate(parameterIndex, x);
    }
    
    @Override
    public void setDouble(final int parameterIndex, final double x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setDouble(parameterIndex, x);
    }
    
    @Override
    public void setEscapeProcessing(final boolean enable) throws SQLException {
        this.ps.setEscapeProcessing(enable);
    }
    
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        this.ps.setFetchDirection(direction);
    }
    
    @Override
    public void setFetchSize(final int rows) throws SQLException {
        this.ps.setFetchSize(rows);
    }
    
    @Override
    public void setFloat(final int parameterIndex, final float x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setFloat(parameterIndex, x);
    }
    
    @Override
    public void setInt(final int parameterIndex, final int x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setInt(parameterIndex, x);
    }
    
    @Override
    public void setLong(final int parameterIndex, final long x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setLong(parameterIndex, x);
    }
    
    @Override
    public void setMaxFieldSize(final int max) throws SQLException {
        this.ps.setMaxFieldSize(max);
    }
    
    @Override
    public void setMaxRows(final int max) throws SQLException {
        this.ps.setMaxRows(max);
    }
    
    @Override
    public void setNull(final int paramIndex, final int sqlType, final String typeName) throws SQLException {
        this.setParameter(paramIndex, null);
        this.ps.setNull(paramIndex, sqlType, typeName);
    }
    
    @Override
    public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
        this.setParameter(parameterIndex, null);
        this.ps.setNull(parameterIndex, sqlType);
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scale) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setObject(parameterIndex, x, targetSqlType, scale);
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setObject(parameterIndex, x, targetSqlType);
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setObject(parameterIndex, x);
    }
    
    @Override
    public void setQueryTimeout(final int seconds) throws SQLException {
        this.ps.setQueryTimeout(seconds);
    }
    
    @Override
    public void setRef(final int i, final Ref x) throws SQLException {
        this.setParameter(i, x);
        this.ps.setRef(i, x);
    }
    
    @Override
    public void setShort(final int parameterIndex, final short x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setShort(parameterIndex, x);
    }
    
    @Override
    public void setString(final int parameterIndex, final String x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setString(parameterIndex, x);
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setTime(parameterIndex, x, cal);
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setTime(parameterIndex, x);
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setTimestamp(parameterIndex, x, cal);
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setTimestamp(parameterIndex, x);
    }
    
    @Override
    public void setUnicodeStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        throw new NucleusUserException("Not supported");
    }
    
    @Override
    public void setURL(final int parameterIndex, final URL x) throws SQLException {
        this.setParameter(parameterIndex, x);
        this.ps.setURL(parameterIndex, x);
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
    }
    
    @Override
    public void setNString(final int parameterIndex, final String value) throws SQLException {
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }
    
    @Override
    public void setPoolable(final boolean poolable) throws SQLException {
    }
    
    @Override
    public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
    }
    
    @Override
    public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
    }
    
    @Override
    public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
    }
    
    @Override
    public boolean isWrapperFor(final Class iface) throws SQLException {
        return PreparedStatement.class.equals(iface);
    }
    
    @Override
    public Object unwrap(final Class iface) throws SQLException {
        if (!PreparedStatement.class.equals(iface)) {
            throw new SQLException("PreparedStatement of type [" + this.getClass().getName() + "] can only be unwrapped as [java.sql.PreparedStatement], not as [" + iface.getName() + "]");
        }
        return this;
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }
    
    static class SubStatement
    {
        public final Map parameters;
        public final String statementText;
        
        public SubStatement(final String statementText) {
            this.parameters = new HashMap();
            this.statementText = statementText;
        }
    }
}
