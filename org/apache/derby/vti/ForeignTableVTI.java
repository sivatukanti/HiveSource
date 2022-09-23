// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import org.apache.derby.iapi.util.IdUtil;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.HashMap;

public class ForeignTableVTI extends VTITemplate implements RestrictedVTI
{
    private static HashMap _connections;
    private String _foreignSchemaName;
    private String _foreignTableName;
    private String _connectionURL;
    private String[] _columnNames;
    private Restriction _restriction;
    private int[] _columnNumberMap;
    private PreparedStatement _foreignPreparedStatement;
    private ResultSet _foreignResultSet;
    
    protected ForeignTableVTI(final String foreignSchemaName, final String foreignTableName, final String connectionURL) {
        this._foreignSchemaName = foreignSchemaName;
        this._foreignTableName = foreignTableName;
        this._connectionURL = connectionURL;
    }
    
    public static ForeignTableVTI readForeignTable(final String s, final String s2, final String s3) {
        return new ForeignTableVTI(s, s2, s3);
    }
    
    public void close() throws SQLException {
        if (!this.isClosed()) {
            this._foreignSchemaName = null;
            this._foreignTableName = null;
            this._connectionURL = null;
            this._columnNames = null;
            this._restriction = null;
            this._columnNumberMap = null;
            if (this._foreignResultSet != null) {
                this._foreignResultSet.close();
            }
            if (this._foreignPreparedStatement != null) {
                this._foreignPreparedStatement.close();
            }
            this._foreignResultSet = null;
            this._foreignPreparedStatement = null;
        }
    }
    
    public boolean next() throws SQLException {
        if (!this.isClosed() && this._foreignResultSet == null) {
            this._foreignPreparedStatement = prepareStatement(getForeignConnection(this._connectionURL), this.makeQuery());
            this._foreignResultSet = this._foreignPreparedStatement.executeQuery();
        }
        return this._foreignResultSet.next();
    }
    
    public boolean isClosed() {
        return this._connectionURL == null;
    }
    
    public boolean wasNull() throws SQLException {
        return this._foreignResultSet.wasNull();
    }
    
    public ResultSetMetaData getMetaData() throws SQLException {
        return this._foreignResultSet.getMetaData();
    }
    
    public InputStream getAsciiStream(final int n) throws SQLException {
        return this._foreignResultSet.getAsciiStream(this.mapColumnNumber(n));
    }
    
    public BigDecimal getBigDecimal(final int n) throws SQLException {
        return this._foreignResultSet.getBigDecimal(this.mapColumnNumber(n));
    }
    
    public BigDecimal getBigDecimal(final int n, final int n2) throws SQLException {
        return this._foreignResultSet.getBigDecimal(this.mapColumnNumber(n), n2);
    }
    
    public InputStream getBinaryStream(final int n) throws SQLException {
        return this._foreignResultSet.getBinaryStream(this.mapColumnNumber(n));
    }
    
    public Blob getBlob(final int n) throws SQLException {
        return this._foreignResultSet.getBlob(this.mapColumnNumber(n));
    }
    
    public boolean getBoolean(final int n) throws SQLException {
        return this._foreignResultSet.getBoolean(this.mapColumnNumber(n));
    }
    
    public byte getByte(final int n) throws SQLException {
        return this._foreignResultSet.getByte(this.mapColumnNumber(n));
    }
    
    public byte[] getBytes(final int n) throws SQLException {
        return this._foreignResultSet.getBytes(this.mapColumnNumber(n));
    }
    
    public Reader getCharacterStream(final int n) throws SQLException {
        return this._foreignResultSet.getCharacterStream(this.mapColumnNumber(n));
    }
    
    public Clob getClob(final int n) throws SQLException {
        return this._foreignResultSet.getClob(this.mapColumnNumber(n));
    }
    
    public Date getDate(final int n) throws SQLException {
        return this._foreignResultSet.getDate(this.mapColumnNumber(n));
    }
    
    public Date getDate(final int n, final Calendar calendar) throws SQLException {
        return this._foreignResultSet.getDate(this.mapColumnNumber(n), calendar);
    }
    
    public double getDouble(final int n) throws SQLException {
        return this._foreignResultSet.getDouble(this.mapColumnNumber(n));
    }
    
    public float getFloat(final int n) throws SQLException {
        return this._foreignResultSet.getFloat(this.mapColumnNumber(n));
    }
    
    public int getInt(final int n) throws SQLException {
        return this._foreignResultSet.getInt(this.mapColumnNumber(n));
    }
    
    public long getLong(final int n) throws SQLException {
        return this._foreignResultSet.getLong(this.mapColumnNumber(n));
    }
    
    public Object getObject(final int n) throws SQLException {
        return this._foreignResultSet.getObject(this.mapColumnNumber(n));
    }
    
    public short getShort(final int n) throws SQLException {
        return this._foreignResultSet.getShort(this.mapColumnNumber(n));
    }
    
    public String getString(final int n) throws SQLException {
        return this._foreignResultSet.getString(this.mapColumnNumber(n));
    }
    
    public Time getTime(final int n) throws SQLException {
        return this._foreignResultSet.getTime(this.mapColumnNumber(n));
    }
    
    public Time getTime(final int n, final Calendar calendar) throws SQLException {
        return this._foreignResultSet.getTime(this.mapColumnNumber(n), calendar);
    }
    
    public Timestamp getTimestamp(final int n) throws SQLException {
        return this._foreignResultSet.getTimestamp(this.mapColumnNumber(n));
    }
    
    public Timestamp getTimestamp(final int n, final Calendar calendar) throws SQLException {
        return this._foreignResultSet.getTimestamp(this.mapColumnNumber(n), calendar);
    }
    
    public void initScan(final String[] columnNames, final Restriction restriction) throws SQLException {
        this._columnNames = columnNames;
        this._restriction = restriction;
        final int length = this._columnNames.length;
        this._columnNumberMap = new int[length];
        int n = 1;
        for (int i = 0; i < length; ++i) {
            if (columnNames[i] != null) {
                this._columnNumberMap[i] = n++;
            }
        }
    }
    
    private static Connection getForeignConnection(final String key) throws SQLException {
        Connection connection = ForeignTableVTI._connections.get(key);
        if (connection == null) {
            connection = DriverManager.getConnection(key);
            if (connection != null) {
                ForeignTableVTI._connections.put(key, connection);
            }
        }
        return connection;
    }
    
    private String makeQuery() {
        final StringBuilder sb = new StringBuilder();
        sb.append("select ");
        final int length = this._columnNames.length;
        int n = 0;
        for (int i = 0; i < length; ++i) {
            final String s = this._columnNames[i];
            if (s != null) {
                if (n > 0) {
                    sb.append(", ");
                }
                ++n;
                sb.append(delimitedID(s));
            }
        }
        sb.append("\nfrom ");
        sb.append(delimitedID(this._foreignSchemaName));
        sb.append('.');
        sb.append(delimitedID(this._foreignTableName));
        if (this._restriction != null) {
            final String sql = this._restriction.toSQL();
            if (sql != null) {
                final String trim = sql.trim();
                if (trim.length() != 0) {
                    sb.append("\nwhere " + trim);
                }
            }
        }
        return sb.toString();
    }
    
    private static String delimitedID(final String s) {
        return IdUtil.normalToDelimited(s);
    }
    
    private static PreparedStatement prepareStatement(final Connection connection, final String s) throws SQLException {
        return connection.prepareStatement(s);
    }
    
    private int mapColumnNumber(final int n) {
        return this._columnNumberMap[n - 1];
    }
    
    static {
        ForeignTableVTI._connections = new HashMap();
    }
}
