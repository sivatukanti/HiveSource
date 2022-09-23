// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.net.URL;
import java.sql.Time;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.text.MessageFormat;
import java.sql.Timestamp;
import java.sql.NClob;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.util.Scanner;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.Array;
import java.sql.ParameterMetaData;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.hive.service.cli.thrift.TSessionHandle;
import org.apache.hive.service.cli.thrift.TCLIService;
import java.util.HashMap;
import java.sql.PreparedStatement;

public class HivePreparedStatement extends HiveStatement implements PreparedStatement
{
    private final String sql;
    private final HashMap<Integer, String> parameters;
    
    public HivePreparedStatement(final HiveConnection connection, final TCLIService.Iface client, final TSessionHandle sessHandle, final String sql) {
        super(connection, client, sessHandle);
        this.parameters = new HashMap<Integer, String>();
        this.sql = sql;
    }
    
    @Override
    public void addBatch() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void clearParameters() throws SQLException {
        this.parameters.clear();
    }
    
    @Override
    public boolean execute() throws SQLException {
        return super.execute(this.updateSql(this.sql, this.parameters));
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        return super.executeQuery(this.updateSql(this.sql, this.parameters));
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        super.executeUpdate(this.updateSql(this.sql, this.parameters));
        return 0;
    }
    
    private String updateSql(final String sql, final HashMap<Integer, String> parameters) {
        if (!sql.contains("?")) {
            return sql;
        }
        final StringBuffer newSql = new StringBuffer(sql);
        for (int paramLoc = 1; this.getCharIndexFromSqlByParamLocation(sql, '?', paramLoc) > 0; ++paramLoc) {
            if (parameters.containsKey(paramLoc)) {
                final int tt = this.getCharIndexFromSqlByParamLocation(newSql.toString(), '?', 1);
                newSql.deleteCharAt(tt);
                newSql.insert(tt, parameters.get(paramLoc));
            }
        }
        return newSql.toString();
    }
    
    private int getCharIndexFromSqlByParamLocation(final String sql, final char cchar, final int paramLoc) {
        int signalCount = 0;
        int charIndex = -1;
        int num = 0;
        for (int i = 0; i < sql.length(); ++i) {
            final char c = sql.charAt(i);
            if (c == '\'' || c == '\\') {
                ++signalCount;
            }
            else if (c == cchar && signalCount % 2 == 0 && ++num == paramLoc) {
                charIndex = i;
                break;
            }
        }
        return charIndex;
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setArray(final int i, final Array x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
        final String str = new Scanner(x, "UTF-8").useDelimiter("\\A").next();
        this.parameters.put(parameterIndex, str);
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setBlob(final int i, final Blob x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
        this.parameters.put(parameterIndex, "" + x);
    }
    
    @Override
    public void setByte(final int parameterIndex, final byte x) throws SQLException {
        this.parameters.put(parameterIndex, "" + x);
    }
    
    @Override
    public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setClob(final int i, final Clob x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x) throws SQLException {
        this.parameters.put(parameterIndex, "'" + x.toString() + "'");
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setDouble(final int parameterIndex, final double x) throws SQLException {
        this.parameters.put(parameterIndex, "" + x);
    }
    
    @Override
    public void setFloat(final int parameterIndex, final float x) throws SQLException {
        this.parameters.put(parameterIndex, "" + x);
    }
    
    @Override
    public void setInt(final int parameterIndex, final int x) throws SQLException {
        this.parameters.put(parameterIndex, "" + x);
    }
    
    @Override
    public void setLong(final int parameterIndex, final long x) throws SQLException {
        this.parameters.put(parameterIndex, "" + x);
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setNString(final int parameterIndex, final String value) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
        this.parameters.put(parameterIndex, "NULL");
    }
    
    @Override
    public void setNull(final int paramIndex, final int sqlType, final String typeName) throws SQLException {
        this.parameters.put(paramIndex, "NULL");
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x) throws SQLException {
        if (x == null) {
            this.setNull(parameterIndex, 0);
        }
        else if (x instanceof String) {
            this.setString(parameterIndex, (String)x);
        }
        else if (x instanceof Short) {
            this.setShort(parameterIndex, (short)x);
        }
        else if (x instanceof Integer) {
            this.setInt(parameterIndex, (int)x);
        }
        else if (x instanceof Long) {
            this.setLong(parameterIndex, (long)x);
        }
        else if (x instanceof Float) {
            this.setFloat(parameterIndex, (float)x);
        }
        else if (x instanceof Double) {
            this.setDouble(parameterIndex, (double)x);
        }
        else if (x instanceof Boolean) {
            this.setBoolean(parameterIndex, (boolean)x);
        }
        else if (x instanceof Byte) {
            this.setByte(parameterIndex, (byte)x);
        }
        else if (x instanceof Character) {
            this.setString(parameterIndex, x.toString());
        }
        else if (x instanceof Timestamp) {
            this.setString(parameterIndex, x.toString());
        }
        else {
            if (!(x instanceof BigDecimal)) {
                throw new SQLException(MessageFormat.format("Can''t infer the SQL type to use for an instance of {0}. Use setObject() with an explicit Types value to specify the type to use.", x.getClass().getName()));
            }
            this.setString(parameterIndex, x.toString());
        }
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scale) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setRef(final int i, final Ref x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setShort(final int parameterIndex, final short x) throws SQLException {
        this.parameters.put(parameterIndex, "" + x);
    }
    
    @Override
    public void setString(final int parameterIndex, String x) throws SQLException {
        x = x.replace("'", "\\'");
        this.parameters.put(parameterIndex, "'" + x + "'");
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
        this.parameters.put(parameterIndex, x.toString());
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setURL(final int parameterIndex, final URL x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setUnicodeStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        throw new SQLException("Method not supported");
    }
}
