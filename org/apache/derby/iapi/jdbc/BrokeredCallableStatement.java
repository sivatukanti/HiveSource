// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.sql.Array;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.util.Map;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.Calendar;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.CallableStatement;

public class BrokeredCallableStatement extends BrokeredPreparedStatement implements CallableStatement
{
    public BrokeredCallableStatement(final BrokeredStatementControl brokeredStatementControl, final String s) throws SQLException {
        super(brokeredStatementControl, s, null);
    }
    
    public final void registerOutParameter(final int n, final int n2) throws SQLException {
        this.getCallableStatement().registerOutParameter(n, n2);
    }
    
    public final void registerOutParameter(final int n, final int n2, final int n3) throws SQLException {
        this.getCallableStatement().registerOutParameter(n, n2, n3);
    }
    
    public final boolean wasNull() throws SQLException {
        return this.getCallableStatement().wasNull();
    }
    
    @Override
    public final void close() throws SQLException {
        this.control.closeRealCallableStatement();
    }
    
    public final String getString(final int n) throws SQLException {
        return this.getCallableStatement().getString(n);
    }
    
    public final boolean getBoolean(final int n) throws SQLException {
        return this.getCallableStatement().getBoolean(n);
    }
    
    public final byte getByte(final int n) throws SQLException {
        return this.getCallableStatement().getByte(n);
    }
    
    public final short getShort(final int n) throws SQLException {
        return this.getCallableStatement().getShort(n);
    }
    
    public final int getInt(final int n) throws SQLException {
        return this.getCallableStatement().getInt(n);
    }
    
    public final long getLong(final int n) throws SQLException {
        return this.getCallableStatement().getLong(n);
    }
    
    public final float getFloat(final int n) throws SQLException {
        return this.getCallableStatement().getFloat(n);
    }
    
    public final double getDouble(final int n) throws SQLException {
        return this.getCallableStatement().getDouble(n);
    }
    
    @Deprecated
    public final BigDecimal getBigDecimal(final int n, final int n2) throws SQLException {
        return this.getCallableStatement().getBigDecimal(n, n2);
    }
    
    public final byte[] getBytes(final int n) throws SQLException {
        return this.getCallableStatement().getBytes(n);
    }
    
    public final Date getDate(final int n) throws SQLException {
        return this.getCallableStatement().getDate(n);
    }
    
    public final Date getDate(final int n, final Calendar calendar) throws SQLException {
        return this.getCallableStatement().getDate(n, calendar);
    }
    
    public final Time getTime(final int n) throws SQLException {
        return this.getCallableStatement().getTime(n);
    }
    
    public final Timestamp getTimestamp(final int n) throws SQLException {
        return this.getCallableStatement().getTimestamp(n);
    }
    
    public final Object getObject(final int n) throws SQLException {
        return this.getCallableStatement().getObject(n);
    }
    
    public final BigDecimal getBigDecimal(final int n) throws SQLException {
        return this.getCallableStatement().getBigDecimal(n);
    }
    
    public final Object getObject(final int n, final Map<String, Class<?>> map) throws SQLException {
        return this.getCallableStatement().getObject(n, map);
    }
    
    public final Ref getRef(final int n) throws SQLException {
        return this.getCallableStatement().getRef(n);
    }
    
    public final Blob getBlob(final int n) throws SQLException {
        return this.getCallableStatement().getBlob(n);
    }
    
    public final Clob getClob(final int n) throws SQLException {
        return this.getCallableStatement().getClob(n);
    }
    
    public final Array getArray(final int n) throws SQLException {
        return this.getCallableStatement().getArray(n);
    }
    
    public final Time getTime(final int n, final Calendar calendar) throws SQLException {
        return this.getCallableStatement().getTime(n, calendar);
    }
    
    public final Timestamp getTimestamp(final int n, final Calendar calendar) throws SQLException {
        return this.getCallableStatement().getTimestamp(n, calendar);
    }
    
    public final void registerOutParameter(final int n, final int n2, final String s) throws SQLException {
        this.getCallableStatement().registerOutParameter(n, n2, s);
    }
    
    public final void setURL(final String s, final URL url) throws SQLException {
        this.getCallableStatement().setURL(s, url);
    }
    
    public final void setNull(final String s, final int n) throws SQLException {
        this.getCallableStatement().setNull(s, n);
    }
    
    public final void setBoolean(final String s, final boolean b) throws SQLException {
        this.getCallableStatement().setBoolean(s, b);
    }
    
    public final void setByte(final String s, final byte b) throws SQLException {
        this.getCallableStatement().setByte(s, b);
    }
    
    public final void setShort(final String s, final short n) throws SQLException {
        this.getCallableStatement().setShort(s, n);
    }
    
    public final void setInt(final String s, final int n) throws SQLException {
        this.getCallableStatement().setInt(s, n);
    }
    
    public final void setLong(final String s, final long n) throws SQLException {
        this.getCallableStatement().setLong(s, n);
    }
    
    public final void setFloat(final String s, final float n) throws SQLException {
        this.getCallableStatement().setFloat(s, n);
    }
    
    public final void setDouble(final String s, final double n) throws SQLException {
        this.getCallableStatement().setDouble(s, n);
    }
    
    public final void setBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        this.getCallableStatement().setBigDecimal(s, bigDecimal);
    }
    
    public final void setString(final String s, final String s2) throws SQLException {
        this.getCallableStatement().setString(s, s2);
    }
    
    public final void setBytes(final String s, final byte[] array) throws SQLException {
        this.getCallableStatement().setBytes(s, array);
    }
    
    public final void setDate(final String s, final Date date) throws SQLException {
        this.getCallableStatement().setDate(s, date);
    }
    
    public final void setTime(final String s, final Time time) throws SQLException {
        this.getCallableStatement().setTime(s, time);
    }
    
    public final void setTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        this.getCallableStatement().setTimestamp(s, timestamp);
    }
    
    public final void setAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.getCallableStatement().setAsciiStream(s, inputStream, n);
    }
    
    public final void setBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.getCallableStatement().setBinaryStream(s, inputStream, n);
    }
    
    public final void setObject(final String s, final Object o, final int n, final int n2) throws SQLException {
        this.getCallableStatement().setObject(s, o, n, n2);
    }
    
    public final void setObject(final String s, final Object o, final int n) throws SQLException {
        this.getCallableStatement().setObject(s, o, n);
    }
    
    public final void setObject(final String s, final Object o) throws SQLException {
        this.getCallableStatement().setObject(s, o);
    }
    
    public final void setCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        this.getCallableStatement().setCharacterStream(s, reader, n);
    }
    
    public final void setDate(final String s, final Date date, final Calendar calendar) throws SQLException {
        this.getCallableStatement().setDate(s, date, calendar);
    }
    
    public final void setTime(final String s, final Time time, final Calendar calendar) throws SQLException {
        this.getCallableStatement().setTime(s, time, calendar);
    }
    
    public final void setTimestamp(final String s, final Timestamp timestamp, final Calendar calendar) throws SQLException {
        this.getCallableStatement().setTimestamp(s, timestamp, calendar);
    }
    
    public final void setNull(final String s, final int n, final String s2) throws SQLException {
        this.getCallableStatement().setNull(s, n, s2);
    }
    
    public final String getString(final String s) throws SQLException {
        return this.getCallableStatement().getString(s);
    }
    
    public final boolean getBoolean(final String s) throws SQLException {
        return this.getCallableStatement().getBoolean(s);
    }
    
    public final byte getByte(final String s) throws SQLException {
        return this.getCallableStatement().getByte(s);
    }
    
    public final short getShort(final String s) throws SQLException {
        return this.getCallableStatement().getShort(s);
    }
    
    public final int getInt(final String s) throws SQLException {
        return this.getCallableStatement().getInt(s);
    }
    
    public final long getLong(final String s) throws SQLException {
        return this.getCallableStatement().getLong(s);
    }
    
    public final float getFloat(final String s) throws SQLException {
        return this.getCallableStatement().getFloat(s);
    }
    
    public final double getDouble(final String s) throws SQLException {
        return this.getCallableStatement().getDouble(s);
    }
    
    public final byte[] getBytes(final String s) throws SQLException {
        return this.getCallableStatement().getBytes(s);
    }
    
    public final Date getDate(final String s) throws SQLException {
        return this.getCallableStatement().getDate(s);
    }
    
    public final Time getTime(final String s) throws SQLException {
        return this.getCallableStatement().getTime(s);
    }
    
    public final Timestamp getTimestamp(final String s) throws SQLException {
        return this.getCallableStatement().getTimestamp(s);
    }
    
    public final Object getObject(final String s) throws SQLException {
        return this.getCallableStatement().getObject(s);
    }
    
    public final BigDecimal getBigDecimal(final String s) throws SQLException {
        return this.getCallableStatement().getBigDecimal(s);
    }
    
    public final Object getObject(final String s, final Map<String, Class<?>> map) throws SQLException {
        return this.getCallableStatement().getObject(s, map);
    }
    
    public final Ref getRef(final String s) throws SQLException {
        return this.getCallableStatement().getRef(s);
    }
    
    public final Blob getBlob(final String s) throws SQLException {
        return this.getCallableStatement().getBlob(s);
    }
    
    public final Clob getClob(final String s) throws SQLException {
        return this.getCallableStatement().getClob(s);
    }
    
    public final Array getArray(final String s) throws SQLException {
        return this.getCallableStatement().getArray(s);
    }
    
    public final Date getDate(final String s, final Calendar calendar) throws SQLException {
        return this.getCallableStatement().getDate(s, calendar);
    }
    
    public final Time getTime(final String s, final Calendar calendar) throws SQLException {
        return this.getCallableStatement().getTime(s, calendar);
    }
    
    public final Timestamp getTimestamp(final String s, final Calendar calendar) throws SQLException {
        return this.getCallableStatement().getTimestamp(s, calendar);
    }
    
    public final URL getURL(final String s) throws SQLException {
        return this.getCallableStatement().getURL(s);
    }
    
    public final URL getURL(final int n) throws SQLException {
        return this.getCallableStatement().getURL(n);
    }
    
    public final void registerOutParameter(final String s, final int n) throws SQLException {
        this.getCallableStatement().registerOutParameter(s, n);
    }
    
    public final void registerOutParameter(final String s, final int n, final int n2) throws SQLException {
        this.getCallableStatement().registerOutParameter(s, n, n2);
    }
    
    public final void registerOutParameter(final String s, final int n, final String s2) throws SQLException {
        this.getCallableStatement().registerOutParameter(s, n, s2);
    }
    
    final CallableStatement getCallableStatement() throws SQLException {
        return this.control.getRealCallableStatement();
    }
    
    @Override
    final PreparedStatement getPreparedStatement() throws SQLException {
        return this.getCallableStatement();
    }
    
    public CallableStatement createDuplicateStatement(final Connection connection, final CallableStatement callableStatement) throws SQLException {
        final CallableStatement prepareCall = connection.prepareCall(this.sql, this.resultSetType, this.resultSetConcurrency, this.resultSetHoldability);
        this.setStatementState(callableStatement, prepareCall);
        return prepareCall;
    }
}
