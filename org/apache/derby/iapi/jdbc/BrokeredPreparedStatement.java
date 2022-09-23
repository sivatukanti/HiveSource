// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ParameterMetaData;
import java.net.URL;
import java.util.Calendar;
import java.sql.Array;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Ref;
import java.io.Reader;
import java.sql.ResultSetMetaData;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BrokeredPreparedStatement extends BrokeredStatement implements EnginePreparedStatement
{
    final String sql;
    private final Object generatedKeys;
    
    public BrokeredPreparedStatement(final BrokeredStatementControl brokeredStatementControl, final String sql, final Object generatedKeys) throws SQLException {
        super(brokeredStatementControl);
        this.sql = sql;
        this.generatedKeys = generatedKeys;
    }
    
    public final ResultSet executeQuery() throws SQLException {
        return this.wrapResultSet(this.getPreparedStatement().executeQuery());
    }
    
    public final int executeUpdate() throws SQLException {
        return this.getPreparedStatement().executeUpdate();
    }
    
    @Override
    public void close() throws SQLException {
        this.control.closeRealPreparedStatement();
    }
    
    public final void setNull(final int n, final int n2) throws SQLException {
        this.getPreparedStatement().setNull(n, n2);
    }
    
    public final void setNull(final int n, final int n2, final String s) throws SQLException {
        this.getPreparedStatement().setNull(n, n2, s);
    }
    
    public final void setBoolean(final int n, final boolean b) throws SQLException {
        this.getPreparedStatement().setBoolean(n, b);
    }
    
    public final void setByte(final int n, final byte b) throws SQLException {
        this.getPreparedStatement().setByte(n, b);
    }
    
    public final void setShort(final int n, final short n2) throws SQLException {
        this.getPreparedStatement().setShort(n, n2);
    }
    
    public final void setInt(final int n, final int n2) throws SQLException {
        this.getPreparedStatement().setInt(n, n2);
    }
    
    public final void setLong(final int n, final long n2) throws SQLException {
        this.getPreparedStatement().setLong(n, n2);
    }
    
    public final void setFloat(final int n, final float n2) throws SQLException {
        this.getPreparedStatement().setFloat(n, n2);
    }
    
    public final void setDouble(final int n, final double n2) throws SQLException {
        this.getPreparedStatement().setDouble(n, n2);
    }
    
    public final void setBigDecimal(final int n, final BigDecimal bigDecimal) throws SQLException {
        this.getPreparedStatement().setBigDecimal(n, bigDecimal);
    }
    
    public final void setString(final int n, final String s) throws SQLException {
        this.getPreparedStatement().setString(n, s);
    }
    
    public final void setBytes(final int n, final byte[] array) throws SQLException {
        this.getPreparedStatement().setBytes(n, array);
    }
    
    public final void setDate(final int n, final Date date) throws SQLException {
        this.getPreparedStatement().setDate(n, date);
    }
    
    public final void setTime(final int n, final Time time) throws SQLException {
        this.getPreparedStatement().setTime(n, time);
    }
    
    public final void setTimestamp(final int n, final Timestamp timestamp) throws SQLException {
        this.getPreparedStatement().setTimestamp(n, timestamp);
    }
    
    public final void setAsciiStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.getPreparedStatement().setAsciiStream(n, inputStream, n2);
    }
    
    @Deprecated
    public final void setUnicodeStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.getPreparedStatement().setUnicodeStream(n, inputStream, n2);
    }
    
    public final void setBinaryStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.getPreparedStatement().setBinaryStream(n, inputStream, n2);
    }
    
    public final void addBatch() throws SQLException {
        this.getPreparedStatement().addBatch();
    }
    
    public final void clearParameters() throws SQLException {
        this.getPreparedStatement().clearParameters();
    }
    
    public final ResultSetMetaData getMetaData() throws SQLException {
        return this.getPreparedStatement().getMetaData();
    }
    
    public final void setObject(final int n, final Object o, final int n2, final int n3) throws SQLException {
        this.getPreparedStatement().setObject(n, o, n2, n3);
    }
    
    public final void setObject(final int n, final Object o, final int n2) throws SQLException {
        this.getPreparedStatement().setObject(n, o, n2);
    }
    
    public final void setObject(final int n, final Object o) throws SQLException {
        this.getPreparedStatement().setObject(n, o);
    }
    
    public final boolean execute() throws SQLException {
        return this.getPreparedStatement().execute();
    }
    
    public final void setCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        this.getPreparedStatement().setCharacterStream(n, reader, n2);
    }
    
    public final void setRef(final int n, final Ref ref) throws SQLException {
        this.getPreparedStatement().setRef(n, ref);
    }
    
    public final void setBlob(final int n, final Blob blob) throws SQLException {
        this.getPreparedStatement().setBlob(n, blob);
    }
    
    public final void setClob(final int n, final Clob clob) throws SQLException {
        this.getPreparedStatement().setClob(n, clob);
    }
    
    public final void setArray(final int n, final Array array) throws SQLException {
        this.getPreparedStatement().setArray(n, array);
    }
    
    public final void setDate(final int n, final Date date, final Calendar calendar) throws SQLException {
        this.getPreparedStatement().setDate(n, date, calendar);
    }
    
    public final void setTime(final int n, final Time time, final Calendar calendar) throws SQLException {
        this.getPreparedStatement().setTime(n, time, calendar);
    }
    
    public final void setTimestamp(final int n, final Timestamp timestamp, final Calendar calendar) throws SQLException {
        this.getPreparedStatement().setTimestamp(n, timestamp, calendar);
    }
    
    public long executeLargeUpdate() throws SQLException {
        return ((EnginePreparedStatement)this.getPreparedStatement()).executeLargeUpdate();
    }
    
    public void setBinaryStream(final int n, final InputStream inputStream) throws SQLException {
        ((EnginePreparedStatement)this.getPreparedStatement()).setBinaryStream(n, inputStream);
    }
    
    public void setCharacterStream(final int n, final Reader reader) throws SQLException {
        ((EnginePreparedStatement)this.getPreparedStatement()).setCharacterStream(n, reader);
    }
    
    public final void setURL(final int n, final URL url) throws SQLException {
        this.getPreparedStatement().setURL(n, url);
    }
    
    public final ParameterMetaData getParameterMetaData() throws SQLException {
        return this.getPreparedStatement().getParameterMetaData();
    }
    
    PreparedStatement getPreparedStatement() throws SQLException {
        return this.control.getRealPreparedStatement();
    }
    
    @Override
    public final Statement getStatement() throws SQLException {
        return this.getPreparedStatement();
    }
    
    public PreparedStatement createDuplicateStatement(final Connection connection, final PreparedStatement preparedStatement) throws SQLException {
        PreparedStatement preparedStatement2;
        if (this.generatedKeys == null) {
            preparedStatement2 = connection.prepareStatement(this.sql, this.resultSetType, this.resultSetConcurrency, this.resultSetHoldability);
        }
        else if (this.generatedKeys instanceof Integer) {
            preparedStatement2 = connection.prepareStatement(this.sql, (int)this.generatedKeys);
        }
        else if (this.generatedKeys instanceof int[]) {
            preparedStatement2 = connection.prepareStatement(this.sql, (int[])this.generatedKeys);
        }
        else {
            preparedStatement2 = connection.prepareStatement(this.sql, (String[])this.generatedKeys);
        }
        this.setStatementState(preparedStatement, preparedStatement2);
        return preparedStatement2;
    }
    
    public final long getVersionCounter() throws SQLException {
        return ((EnginePreparedStatement)this.getPreparedStatement()).getVersionCounter();
    }
}
