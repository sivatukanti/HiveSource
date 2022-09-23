// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.net.URL;
import java.sql.Time;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.util.Map;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import java.sql.Timestamp;
import org.apache.hive.service.cli.Type;
import java.sql.NClob;
import java.sql.ResultSetMetaData;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;
import java.math.MathContext;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.Array;
import java.util.Iterator;
import java.sql.SQLException;
import org.apache.hive.service.cli.TableSchema;
import java.util.List;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.ResultSet;

public abstract class HiveBaseResultSet implements ResultSet
{
    protected Statement statement;
    protected SQLWarning warningChain;
    protected boolean wasNull;
    protected Object[] row;
    protected List<String> columnNames;
    protected List<String> normalizedColumnNames;
    protected List<String> columnTypes;
    protected List<JdbcColumnAttributes> columnAttributes;
    private TableSchema schema;
    
    public HiveBaseResultSet() {
        this.statement = null;
        this.warningChain = null;
        this.wasNull = false;
    }
    
    @Override
    public boolean absolute(final int row) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void afterLast() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void deleteRow() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int findColumn(final String columnName) throws SQLException {
        int columnIndex = 0;
        boolean findColumn = false;
        for (final String normalizedColumnName : this.normalizedColumnNames) {
            ++columnIndex;
            final String[] names = normalizedColumnName.split("\\.");
            final String name = names[names.length - 1];
            if (name.equalsIgnoreCase(columnName) || normalizedColumnName.equalsIgnoreCase(columnName)) {
                findColumn = true;
                break;
            }
        }
        if (!findColumn) {
            throw new SQLException("Could not find " + columnName + " in " + this.normalizedColumnNames);
        }
        return columnIndex;
    }
    
    @Override
    public boolean first() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Array getArray(final int i) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Array getArray(final String colName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public InputStream getAsciiStream(final int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public InputStream getAsciiStream(final String columnName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        final Object val = this.getObject(columnIndex);
        if (val == null || val instanceof BigDecimal) {
            return (BigDecimal)val;
        }
        throw new SQLException("Illegal conversion");
    }
    
    @Override
    public BigDecimal getBigDecimal(final String columnName) throws SQLException {
        return this.getBigDecimal(this.findColumn(columnName));
    }
    
    @Override
    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        final MathContext mc = new MathContext(scale);
        return this.getBigDecimal(columnIndex).round(mc);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String columnName, final int scale) throws SQLException {
        return this.getBigDecimal(this.findColumn(columnName), scale);
    }
    
    @Override
    public InputStream getBinaryStream(final int columnIndex) throws SQLException {
        final Object obj = this.getObject(columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof InputStream) {
            return (InputStream)obj;
        }
        if (obj instanceof byte[]) {
            final byte[] byteArray = (byte[])obj;
            final InputStream is = new ByteArrayInputStream(byteArray);
            return is;
        }
        if (obj instanceof String) {
            final String str = (String)obj;
            InputStream is = null;
            try {
                is = new ByteArrayInputStream(str.getBytes("UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                throw new SQLException("Illegal conversion to binary stream from column " + columnIndex + " - Unsupported encoding exception");
            }
            return is;
        }
        throw new SQLException("Illegal conversion to binary stream from column " + columnIndex);
    }
    
    @Override
    public InputStream getBinaryStream(final String columnName) throws SQLException {
        return this.getBinaryStream(this.findColumn(columnName));
    }
    
    @Override
    public Blob getBlob(final int i) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Blob getBlob(final String colName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean getBoolean(final int columnIndex) throws SQLException {
        final Object obj = this.getObject(columnIndex);
        if (Boolean.class.isInstance(obj)) {
            return (boolean)obj;
        }
        if (obj == null) {
            return false;
        }
        if (Number.class.isInstance(obj)) {
            return ((Number)obj).intValue() != 0;
        }
        if (String.class.isInstance(obj)) {
            return !((String)obj).equals("0");
        }
        throw new SQLException("Cannot convert column " + columnIndex + " to boolean");
    }
    
    @Override
    public boolean getBoolean(final String columnName) throws SQLException {
        return this.getBoolean(this.findColumn(columnName));
    }
    
    @Override
    public byte getByte(final int columnIndex) throws SQLException {
        final Object obj = this.getObject(columnIndex);
        if (Number.class.isInstance(obj)) {
            return ((Number)obj).byteValue();
        }
        if (obj == null) {
            return 0;
        }
        throw new SQLException("Cannot convert column " + columnIndex + " to byte");
    }
    
    @Override
    public byte getByte(final String columnName) throws SQLException {
        return this.getByte(this.findColumn(columnName));
    }
    
    @Override
    public byte[] getBytes(final int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public byte[] getBytes(final String columnName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Reader getCharacterStream(final int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Reader getCharacterStream(final String columnName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Clob getClob(final int i) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Clob getClob(final String colName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getConcurrency() throws SQLException {
        return 1007;
    }
    
    @Override
    public String getCursorName() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Date getDate(final int columnIndex) throws SQLException {
        final Object obj = this.getObject(columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date)obj;
        }
        try {
            if (obj instanceof String) {
                return Date.valueOf((String)obj);
            }
        }
        catch (Exception e) {
            throw new SQLException("Cannot convert column " + columnIndex + " to date: " + e.toString(), e);
        }
        throw new SQLException("Cannot convert column " + columnIndex + " to date: Illegal conversion");
    }
    
    @Override
    public Date getDate(final String columnName) throws SQLException {
        return this.getDate(this.findColumn(columnName));
    }
    
    @Override
    public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Date getDate(final String columnName, final Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public double getDouble(final int columnIndex) throws SQLException {
        try {
            final Object obj = this.getObject(columnIndex);
            if (Number.class.isInstance(obj)) {
                return ((Number)obj).doubleValue();
            }
            if (obj == null) {
                return 0.0;
            }
            if (String.class.isInstance(obj)) {
                return Double.valueOf((String)obj);
            }
            throw new Exception("Illegal conversion");
        }
        catch (Exception e) {
            throw new SQLException("Cannot convert column " + columnIndex + " to double: " + e.toString(), e);
        }
    }
    
    @Override
    public double getDouble(final String columnName) throws SQLException {
        return this.getDouble(this.findColumn(columnName));
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        return 1000;
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public float getFloat(final int columnIndex) throws SQLException {
        try {
            final Object obj = this.getObject(columnIndex);
            if (Number.class.isInstance(obj)) {
                return ((Number)obj).floatValue();
            }
            if (obj == null) {
                return 0.0f;
            }
            if (String.class.isInstance(obj)) {
                return Float.valueOf((String)obj);
            }
            throw new Exception("Illegal conversion");
        }
        catch (Exception e) {
            throw new SQLException("Cannot convert column " + columnIndex + " to float: " + e.toString(), e);
        }
    }
    
    @Override
    public float getFloat(final String columnName) throws SQLException {
        return this.getFloat(this.findColumn(columnName));
    }
    
    @Override
    public int getHoldability() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getInt(final int columnIndex) throws SQLException {
        try {
            final Object obj = this.getObject(columnIndex);
            if (Number.class.isInstance(obj)) {
                return ((Number)obj).intValue();
            }
            if (obj == null) {
                return 0;
            }
            if (String.class.isInstance(obj)) {
                return Integer.valueOf((String)obj);
            }
            throw new Exception("Illegal conversion");
        }
        catch (Exception e) {
            throw new SQLException("Cannot convert column " + columnIndex + " to integer" + e.toString(), e);
        }
    }
    
    @Override
    public int getInt(final String columnName) throws SQLException {
        return this.getInt(this.findColumn(columnName));
    }
    
    @Override
    public long getLong(final int columnIndex) throws SQLException {
        try {
            final Object obj = this.getObject(columnIndex);
            if (Number.class.isInstance(obj)) {
                return ((Number)obj).longValue();
            }
            if (obj == null) {
                return 0L;
            }
            if (String.class.isInstance(obj)) {
                return Long.valueOf((String)obj);
            }
            throw new Exception("Illegal conversion");
        }
        catch (Exception e) {
            throw new SQLException("Cannot convert column " + columnIndex + " to long: " + e.toString(), e);
        }
    }
    
    @Override
    public long getLong(final String columnName) throws SQLException {
        return this.getLong(this.findColumn(columnName));
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new HiveResultSetMetaData(this.columnNames, this.columnTypes, this.columnAttributes);
    }
    
    @Override
    public Reader getNCharacterStream(final int arg0) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Reader getNCharacterStream(final String arg0) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public NClob getNClob(final int arg0) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public NClob getNClob(final String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getNString(final int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getNString(final String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    private Object getColumnValue(final int columnIndex) throws SQLException {
        if (this.row == null) {
            throw new SQLException("No row found.");
        }
        if (this.row.length == 0) {
            throw new SQLException("RowSet does not contain any columns!");
        }
        if (columnIndex > this.row.length) {
            throw new SQLException("Invalid columnIndex: " + columnIndex);
        }
        final Type columnType = this.getSchema().getColumnDescriptorAt(columnIndex - 1).getType();
        try {
            final Object evaluated = this.evaluate(columnType, this.row[columnIndex - 1]);
            this.wasNull = (evaluated == null);
            return evaluated;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Unrecognized column type:" + columnType, e);
        }
    }
    
    private Object evaluate(final Type type, final Object value) {
        if (value == null) {
            return null;
        }
        switch (type) {
            case BINARY_TYPE: {
                if (value instanceof String) {
                    return ((String)value).getBytes();
                }
                return value;
            }
            case TIMESTAMP_TYPE: {
                return Timestamp.valueOf((String)value);
            }
            case DECIMAL_TYPE: {
                return new BigDecimal((String)value);
            }
            case DATE_TYPE: {
                return Date.valueOf((String)value);
            }
            case INTERVAL_YEAR_MONTH_TYPE: {
                return HiveIntervalYearMonth.valueOf((String)value);
            }
            case INTERVAL_DAY_TIME_TYPE: {
                return HiveIntervalDayTime.valueOf((String)value);
            }
            case ARRAY_TYPE:
            case MAP_TYPE:
            case STRUCT_TYPE: {
                return value;
            }
            default: {
                return value;
            }
        }
    }
    
    @Override
    public Object getObject(final int columnIndex) throws SQLException {
        return this.getColumnValue(columnIndex);
    }
    
    @Override
    public Object getObject(final String columnName) throws SQLException {
        return this.getObject(this.findColumn(columnName));
    }
    
    @Override
    public Object getObject(final int i, final Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Object getObject(final String colName, final Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Ref getRef(final int i) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Ref getRef(final String colName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getRow() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public RowId getRowId(final int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public RowId getRowId(final String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public SQLXML getSQLXML(final int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public SQLXML getSQLXML(final String columnLabel) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public short getShort(final int columnIndex) throws SQLException {
        try {
            final Object obj = this.getObject(columnIndex);
            if (Number.class.isInstance(obj)) {
                return ((Number)obj).shortValue();
            }
            if (obj == null) {
                return 0;
            }
            if (String.class.isInstance(obj)) {
                return Short.valueOf((String)obj);
            }
            throw new Exception("Illegal conversion");
        }
        catch (Exception e) {
            throw new SQLException("Cannot convert column " + columnIndex + " to short: " + e.toString(), e);
        }
    }
    
    @Override
    public short getShort(final String columnName) throws SQLException {
        return this.getShort(this.findColumn(columnName));
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        return this.statement;
    }
    
    @Override
    public String getString(final int columnIndex) throws SQLException {
        final Object value = this.getColumnValue(columnIndex);
        if (this.wasNull) {
            return null;
        }
        if (value instanceof byte[]) {
            return new String((byte[])value);
        }
        return value.toString();
    }
    
    @Override
    public String getString(final String columnName) throws SQLException {
        return this.getString(this.findColumn(columnName));
    }
    
    @Override
    public Time getTime(final int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Time getTime(final String columnName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Time getTime(final String columnName, final Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex) throws SQLException {
        final Object obj = this.getObject(columnIndex);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Timestamp) {
            return (Timestamp)obj;
        }
        if (obj instanceof String) {
            return Timestamp.valueOf((String)obj);
        }
        throw new SQLException("Illegal conversion");
    }
    
    @Override
    public Timestamp getTimestamp(final String columnName) throws SQLException {
        return this.getTimestamp(this.findColumn(columnName));
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Timestamp getTimestamp(final String columnName, final Calendar cal) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getType() throws SQLException {
        return 1003;
    }
    
    @Override
    public URL getURL(final int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public URL getURL(final String columnName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public InputStream getUnicodeStream(final String columnName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void insertRow() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isLast() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean last() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean previous() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void refreshRow() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean relative(final int rows) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean rowDeleted() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean rowInserted() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean rowUpdated() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setFetchSize(final int rows) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateArray(final int columnIndex, final Array x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateArray(final String columnName, final Array x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateAsciiStream(final String columnName, final InputStream x, final int length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBigDecimal(final String columnName, final BigDecimal x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBinaryStream(final String columnName, final InputStream x, final int length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBlob(final int columnIndex, final Blob x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBlob(final String columnName, final Blob x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBoolean(final int columnIndex, final boolean x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBoolean(final String columnName, final boolean x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateByte(final int columnIndex, final byte x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateByte(final String columnName, final byte x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateBytes(final String columnName, final byte[] x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateCharacterStream(final String columnName, final Reader reader, final int length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateClob(final int columnIndex, final Clob x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateClob(final String columnName, final Clob x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateDate(final int columnIndex, final Date x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateDate(final String columnName, final Date x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateDouble(final int columnIndex, final double x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateDouble(final String columnName, final double x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateFloat(final int columnIndex, final float x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateFloat(final String columnName, final float x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateInt(final int columnIndex, final int x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateInt(final String columnName, final int x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateLong(final int columnIndex, final long x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateLong(final String columnName, final long x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNClob(final int columnIndex, final NClob clob) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNClob(final String columnLabel, final NClob clob) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNString(final int columnIndex, final String string) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNString(final String columnLabel, final String string) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNull(final int columnIndex) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateNull(final String columnName) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateObject(final String columnName, final Object x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x, final int scale) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateObject(final String columnName, final Object x, final int scale) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateRef(final int columnIndex, final Ref x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateRef(final String columnName, final Ref x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateRow() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateRowId(final int columnIndex, final RowId x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateRowId(final String columnLabel, final RowId x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateShort(final int columnIndex, final short x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateShort(final String columnName, final short x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateString(final int columnIndex, final String x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateString(final String columnName, final String x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateTime(final int columnIndex, final Time x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateTime(final String columnName, final Time x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void updateTimestamp(final String columnName, final Timestamp x) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.warningChain;
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.warningChain = null;
    }
    
    @Override
    public void close() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        return this.wasNull;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    protected void setSchema(final TableSchema schema) {
        this.schema = schema;
    }
    
    protected TableSchema getSchema() {
        return this.schema;
    }
}
