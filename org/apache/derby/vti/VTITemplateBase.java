// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.sql.Array;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.util.Map;
import java.net.URL;
import java.util.Calendar;
import java.sql.Statement;
import java.io.Reader;
import java.sql.SQLWarning;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;

class VTITemplateBase implements ResultSet
{
    public ResultSetMetaData getMetaData() throws SQLException {
        throw this.notImplemented("getMetaData");
    }
    
    public boolean next() throws SQLException {
        throw this.notImplemented("next");
    }
    
    public void close() throws SQLException {
        throw this.notImplemented("close");
    }
    
    public boolean wasNull() throws SQLException {
        throw this.notImplemented("wasNull");
    }
    
    public String getString(final int n) throws SQLException {
        throw this.notImplemented("getString");
    }
    
    public boolean getBoolean(final int n) throws SQLException {
        throw this.notImplemented("getBoolean");
    }
    
    public byte getByte(final int n) throws SQLException {
        throw this.notImplemented("getByte");
    }
    
    public short getShort(final int n) throws SQLException {
        throw this.notImplemented("getShort");
    }
    
    public int getInt(final int n) throws SQLException {
        throw this.notImplemented("getInt");
    }
    
    public long getLong(final int n) throws SQLException {
        throw this.notImplemented("getLong");
    }
    
    public float getFloat(final int n) throws SQLException {
        throw this.notImplemented("getFloat");
    }
    
    public double getDouble(final int n) throws SQLException {
        throw this.notImplemented("getDouble");
    }
    
    public BigDecimal getBigDecimal(final int n, final int n2) throws SQLException {
        throw this.notImplemented("getBigDecimal");
    }
    
    public byte[] getBytes(final int n) throws SQLException {
        throw this.notImplemented("] getBytes");
    }
    
    public Date getDate(final int n) throws SQLException {
        throw this.notImplemented("sql.Date getDate");
    }
    
    public Time getTime(final int n) throws SQLException {
        throw this.notImplemented("sql.Time getTime");
    }
    
    public Timestamp getTimestamp(final int n) throws SQLException {
        throw this.notImplemented("sql.Timestamp getTimestamp");
    }
    
    public InputStream getAsciiStream(final int n) throws SQLException {
        throw this.notImplemented("io.InputStream getAsciiStream");
    }
    
    public InputStream getUnicodeStream(final int n) throws SQLException {
        throw this.notImplemented("io.InputStream getUnicodeStream");
    }
    
    public InputStream getBinaryStream(final int n) throws SQLException {
        throw this.notImplemented("io.InputStream getBinaryStream");
    }
    
    public String getString(final String s) throws SQLException {
        throw this.notImplemented("getString");
    }
    
    public boolean getBoolean(final String s) throws SQLException {
        throw this.notImplemented("getBoolean");
    }
    
    public byte getByte(final String s) throws SQLException {
        throw this.notImplemented("getByte");
    }
    
    public short getShort(final String s) throws SQLException {
        throw this.notImplemented("getShort");
    }
    
    public int getInt(final String s) throws SQLException {
        throw this.notImplemented("getInt");
    }
    
    public long getLong(final String s) throws SQLException {
        throw this.notImplemented("getLong");
    }
    
    public float getFloat(final String s) throws SQLException {
        throw this.notImplemented("getFloat");
    }
    
    public double getDouble(final String s) throws SQLException {
        throw this.notImplemented("getDouble");
    }
    
    public BigDecimal getBigDecimal(final String s, final int n) throws SQLException {
        throw this.notImplemented("getBigDecimal");
    }
    
    public byte[] getBytes(final String s) throws SQLException {
        throw this.notImplemented("] getBytes");
    }
    
    public Date getDate(final String s) throws SQLException {
        throw this.notImplemented("sql.Date getDate");
    }
    
    public Time getTime(final String s) throws SQLException {
        throw this.notImplemented("sql.Time getTime");
    }
    
    public Timestamp getTimestamp(final String s) throws SQLException {
        throw this.notImplemented("sql.Timestamp getTimestamp");
    }
    
    public InputStream getAsciiStream(final String s) throws SQLException {
        throw this.notImplemented("io.InputStream getAsciiStream");
    }
    
    public InputStream getUnicodeStream(final String s) throws SQLException {
        throw this.notImplemented("io.InputStream getUnicodeStream");
    }
    
    public InputStream getBinaryStream(final String s) throws SQLException {
        throw this.notImplemented("io.InputStream getBinaryStream");
    }
    
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }
    
    public void clearWarnings() throws SQLException {
        throw this.notImplemented("clearWarnings");
    }
    
    public String getCursorName() throws SQLException {
        throw this.notImplemented("getCursorName");
    }
    
    public Object getObject(final int n) throws SQLException {
        throw this.notImplemented("getObject");
    }
    
    public Object getObject(final String s) throws SQLException {
        throw this.notImplemented("getObject");
    }
    
    public int findColumn(final String s) throws SQLException {
        throw this.notImplemented("findColumn");
    }
    
    public Reader getCharacterStream(final int n) throws SQLException {
        throw this.notImplemented("io.Reader getCharacterStream");
    }
    
    public Reader getCharacterStream(final String s) throws SQLException {
        throw this.notImplemented("io.Reader getCharacterStream");
    }
    
    public BigDecimal getBigDecimal(final int n) throws SQLException {
        throw this.notImplemented("getBigDecimal");
    }
    
    public BigDecimal getBigDecimal(final String s) throws SQLException {
        throw this.notImplemented("getBigDecimal");
    }
    
    public boolean isBeforeFirst() throws SQLException {
        throw this.notImplemented("isBeforeFirst");
    }
    
    public boolean isAfterLast() throws SQLException {
        throw this.notImplemented("isAfterLast");
    }
    
    public boolean isFirst() throws SQLException {
        throw this.notImplemented("isFirst");
    }
    
    public boolean isLast() throws SQLException {
        throw this.notImplemented("isLast");
    }
    
    public void beforeFirst() throws SQLException {
        throw this.notImplemented("beforeFirst");
    }
    
    public void afterLast() throws SQLException {
        throw this.notImplemented("afterLast");
    }
    
    public boolean first() throws SQLException {
        throw this.notImplemented("first");
    }
    
    public boolean last() throws SQLException {
        throw this.notImplemented("last");
    }
    
    public int getRow() throws SQLException {
        throw this.notImplemented("getRow");
    }
    
    public boolean absolute(final int n) throws SQLException {
        throw this.notImplemented("absolute");
    }
    
    public boolean relative(final int n) throws SQLException {
        throw this.notImplemented("relative");
    }
    
    public boolean previous() throws SQLException {
        throw this.notImplemented("previous");
    }
    
    public void setFetchDirection(final int n) throws SQLException {
        throw this.notImplemented("setFetchDirection");
    }
    
    public int getFetchDirection() throws SQLException {
        throw this.notImplemented("getFetchDirection");
    }
    
    public void setFetchSize(final int n) throws SQLException {
        throw this.notImplemented("setFetchSize");
    }
    
    public int getFetchSize() throws SQLException {
        throw this.notImplemented("getFetchSize");
    }
    
    public int getType() throws SQLException {
        throw this.notImplemented("getType");
    }
    
    public int getConcurrency() throws SQLException {
        throw this.notImplemented("getConcurrency");
    }
    
    public boolean rowUpdated() throws SQLException {
        throw this.notImplemented("rowUpdated");
    }
    
    public boolean rowInserted() throws SQLException {
        throw this.notImplemented("rowInserted");
    }
    
    public boolean rowDeleted() throws SQLException {
        throw this.notImplemented("rowDeleted");
    }
    
    public void updateNull(final int n) throws SQLException {
        throw this.notImplemented("updateNull");
    }
    
    public void updateBoolean(final int n, final boolean b) throws SQLException {
        throw this.notImplemented("updateBoolean");
    }
    
    public void updateByte(final int n, final byte b) throws SQLException {
        throw this.notImplemented("updateByte");
    }
    
    public void updateShort(final int n, final short n2) throws SQLException {
        throw this.notImplemented("updateShort");
    }
    
    public void updateInt(final int n, final int n2) throws SQLException {
        throw this.notImplemented("updateInt");
    }
    
    public void updateLong(final int n, final long n2) throws SQLException {
        throw this.notImplemented("updateLong");
    }
    
    public void updateFloat(final int n, final float n2) throws SQLException {
        throw this.notImplemented("updateFloat");
    }
    
    public void updateDouble(final int n, final double n2) throws SQLException {
        throw this.notImplemented("updateDouble");
    }
    
    public void updateBigDecimal(final int n, final BigDecimal bigDecimal) throws SQLException {
        throw this.notImplemented("updateBigDecimal");
    }
    
    public void updateString(final int n, final String s) throws SQLException {
        throw this.notImplemented("updateString");
    }
    
    public void updateBytes(final int n, final byte[] array) throws SQLException {
        throw this.notImplemented("updateBytes");
    }
    
    public void updateDate(final int n, final Date date) throws SQLException {
        throw this.notImplemented("updateDate");
    }
    
    public void updateTime(final int n, final Time time) throws SQLException {
        throw this.notImplemented("updateTime");
    }
    
    public void updateTimestamp(final int n, final Timestamp timestamp) throws SQLException {
        throw this.notImplemented("updateTimestamp");
    }
    
    public void updateAsciiStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        throw this.notImplemented("updateAsciiStream");
    }
    
    public void updateBinaryStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        throw this.notImplemented("updateBinaryStream");
    }
    
    public void updateCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        throw this.notImplemented("updateCharacterStream");
    }
    
    public void updateObject(final int n, final Object o, final int n2) throws SQLException {
        throw this.notImplemented("updateObject");
    }
    
    public void updateObject(final int n, final Object o) throws SQLException {
        throw this.notImplemented("updateObject");
    }
    
    public void updateNull(final String s) throws SQLException {
        throw this.notImplemented("updateNull");
    }
    
    public void updateBoolean(final String s, final boolean b) throws SQLException {
        throw this.notImplemented("updateBoolean");
    }
    
    public void updateByte(final String s, final byte b) throws SQLException {
        throw this.notImplemented("updateByte");
    }
    
    public void updateShort(final String s, final short n) throws SQLException {
        throw this.notImplemented("updateShort");
    }
    
    public void updateInt(final String s, final int n) throws SQLException {
        throw this.notImplemented("updateInt");
    }
    
    public void updateLong(final String s, final long n) throws SQLException {
        throw this.notImplemented("updateLong");
    }
    
    public void updateFloat(final String s, final float n) throws SQLException {
        throw this.notImplemented("updateFloat");
    }
    
    public void updateDouble(final String s, final double n) throws SQLException {
        throw this.notImplemented("updateDouble");
    }
    
    public void updateBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        throw this.notImplemented("updateBigDecimal");
    }
    
    public void updateString(final String s, final String s2) throws SQLException {
        throw this.notImplemented("updateString");
    }
    
    public void updateBytes(final String s, final byte[] array) throws SQLException {
        throw this.notImplemented("updateBytes");
    }
    
    public void updateDate(final String s, final Date date) throws SQLException {
        throw this.notImplemented("updateDate");
    }
    
    public void updateTime(final String s, final Time time) throws SQLException {
        throw this.notImplemented("updateTime");
    }
    
    public void updateTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        throw this.notImplemented("updateTimestamp");
    }
    
    public void updateAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw this.notImplemented("updateAsciiStream");
    }
    
    public void updateBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw this.notImplemented("updateBinaryStream");
    }
    
    public void updateCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        throw this.notImplemented("updateCharacterStream");
    }
    
    public void updateObject(final String s, final Object o, final int n) throws SQLException {
        throw this.notImplemented("updateObject");
    }
    
    public void updateObject(final String s, final Object o) throws SQLException {
        throw this.notImplemented("updateObject");
    }
    
    public void insertRow() throws SQLException {
        throw this.notImplemented("insertRow");
    }
    
    public void updateRow() throws SQLException {
        throw this.notImplemented("updateRow");
    }
    
    public void deleteRow() throws SQLException {
        throw this.notImplemented("deleteRow");
    }
    
    public void refreshRow() throws SQLException {
        throw this.notImplemented("refreshRow");
    }
    
    public void cancelRowUpdates() throws SQLException {
        throw this.notImplemented("cancelRowUpdates");
    }
    
    public void moveToInsertRow() throws SQLException {
        throw this.notImplemented("moveToInsertRow");
    }
    
    public void moveToCurrentRow() throws SQLException {
        throw this.notImplemented("moveToCurrentRow");
    }
    
    public Statement getStatement() throws SQLException {
        throw this.notImplemented("getStatement");
    }
    
    public Date getDate(final int n, final Calendar calendar) throws SQLException {
        throw this.notImplemented("sql.Date getDate");
    }
    
    public Date getDate(final String s, final Calendar calendar) throws SQLException {
        throw this.notImplemented("sql.Date getDate");
    }
    
    public Time getTime(final int n, final Calendar calendar) throws SQLException {
        throw this.notImplemented("sql.Time getTime");
    }
    
    public Time getTime(final String s, final Calendar calendar) throws SQLException {
        throw this.notImplemented("sql.Time getTime");
    }
    
    public Timestamp getTimestamp(final int n, final Calendar calendar) throws SQLException {
        throw this.notImplemented("sql.Timestamp getTimestamp");
    }
    
    public Timestamp getTimestamp(final String s, final Calendar calendar) throws SQLException {
        throw this.notImplemented("sql.Timestamp getTimestamp");
    }
    
    public URL getURL(final int n) throws SQLException {
        throw this.notImplemented("getURL");
    }
    
    public URL getURL(final String s) throws SQLException {
        throw this.notImplemented("getURL");
    }
    
    public Object getObject(final int n, final Map map) throws SQLException {
        throw this.notImplemented("getObject");
    }
    
    public Ref getRef(final int n) throws SQLException {
        throw this.notImplemented("getRef");
    }
    
    public Blob getBlob(final int n) throws SQLException {
        throw this.notImplemented("getBlob");
    }
    
    public Clob getClob(final int n) throws SQLException {
        throw this.notImplemented("getClob");
    }
    
    public Array getArray(final int n) throws SQLException {
        throw this.notImplemented("getArray");
    }
    
    public Object getObject(final String s, final Map map) throws SQLException {
        throw this.notImplemented("getObject");
    }
    
    public Ref getRef(final String s) throws SQLException {
        throw this.notImplemented("getRef");
    }
    
    public Blob getBlob(final String s) throws SQLException {
        throw this.notImplemented("getBlob");
    }
    
    public Clob getClob(final String s) throws SQLException {
        throw this.notImplemented("getClob");
    }
    
    public Array getArray(final String s) throws SQLException {
        throw this.notImplemented("getArray");
    }
    
    public void updateRef(final int n, final Ref ref) throws SQLException {
        throw this.notImplemented("updateRef");
    }
    
    public void updateRef(final String s, final Ref ref) throws SQLException {
        throw this.notImplemented("updateRef");
    }
    
    public void updateBlob(final int n, final Blob blob) throws SQLException {
        throw this.notImplemented("updateBlob");
    }
    
    public void updateBlob(final String s, final Blob blob) throws SQLException {
        throw this.notImplemented("updateBlob");
    }
    
    public void updateClob(final int n, final Clob clob) throws SQLException {
        throw this.notImplemented("updateClob");
    }
    
    public void updateClob(final String s, final Clob clob) throws SQLException {
        throw this.notImplemented("updateClob");
    }
    
    public void updateArray(final int n, final Array array) throws SQLException {
        throw this.notImplemented("updateArray");
    }
    
    public void updateArray(final String s, final Array array) throws SQLException {
        throw this.notImplemented("updateArray");
    }
    
    protected SQLException notImplemented(final String str) {
        return new SQLException("Unimplemented method: " + str);
    }
}
