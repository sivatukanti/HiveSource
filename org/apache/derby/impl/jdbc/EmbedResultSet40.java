// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.Clob;
import java.sql.Blob;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLXML;
import java.sql.NClob;
import java.io.Reader;
import java.sql.RowId;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.ResultSet;

public class EmbedResultSet40 extends EmbedResultSet20
{
    public EmbedResultSet40(final EmbedConnection embedConnection, final org.apache.derby.iapi.sql.ResultSet set, final boolean b, final EmbedStatement embedStatement, final boolean b2) throws SQLException {
        super(embedConnection, set, b, embedStatement, b2);
    }
    
    @Override
    public RowId getRowId(final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public RowId getRowId(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNCharacterStream(final int n, final Reader reader) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNCharacterStream(final String s, final Reader reader) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNString(final int n, final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNString(final String s, final String s2) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNClob(final int n, final NClob nClob) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNClob(final int n, final Reader reader) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNClob(final String s, final NClob nClob) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNClob(final String s, final Reader reader) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public Reader getNCharacterStream(final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public Reader getNCharacterStream(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public NClob getNClob(final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public NClob getNClob(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public String getNString(final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public String getNString(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateRowId(final int n, final RowId rowId) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateRowId(final String s, final RowId rowId) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public SQLXML getSQLXML(final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public SQLXML getSQLXML(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateSQLXML(final int n, final SQLXML sqlxml) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateSQLXML(final String s, final SQLXML sqlxml) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        this.checkIfClosed("isWrapperFor");
        return clazz.isInstance(this);
    }
    
    @Override
    public <T> T unwrap(final Class<T> clazz) throws SQLException {
        this.checkIfClosed("unwrap");
        try {
            return clazz.cast(this);
        }
        catch (ClassCastException ex) {
            throw this.newSQLException("XJ128.S", clazz);
        }
    }
    
    @Override
    public void updateNClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void updateNClob(final String s, final Reader reader, final long n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public <T> T getObject(final int n, final Class<T> clazz) throws SQLException {
        this.checkIfClosed("getObject");
        if (clazz == null) {
            throw this.mismatchException("NULL", n);
        }
        Object obj;
        if (String.class.equals(clazz)) {
            obj = this.getString(n);
        }
        else if (BigDecimal.class.equals(clazz)) {
            obj = this.getBigDecimal(n);
        }
        else if (Boolean.class.equals(clazz)) {
            obj = this.getBoolean(n);
        }
        else if (Byte.class.equals(clazz)) {
            obj = this.getByte(n);
        }
        else if (Short.class.equals(clazz)) {
            obj = this.getShort(n);
        }
        else if (Integer.class.equals(clazz)) {
            obj = this.getInt(n);
        }
        else if (Long.class.equals(clazz)) {
            obj = this.getLong(n);
        }
        else if (Float.class.equals(clazz)) {
            obj = this.getFloat(n);
        }
        else if (Double.class.equals(clazz)) {
            obj = this.getDouble(n);
        }
        else if (Date.class.equals(clazz)) {
            obj = this.getDate(n);
        }
        else if (Time.class.equals(clazz)) {
            obj = this.getTime(n);
        }
        else if (Timestamp.class.equals(clazz)) {
            obj = this.getTimestamp(n);
        }
        else if (Blob.class.equals(clazz)) {
            obj = this.getBlob(n);
        }
        else if (Clob.class.equals(clazz)) {
            obj = this.getClob(n);
        }
        else if (clazz.isArray() && clazz.getComponentType().equals(Byte.TYPE)) {
            obj = this.getBytes(n);
        }
        else {
            obj = this.getObject(n);
        }
        if (this.wasNull()) {
            obj = null;
        }
        if (obj == null || clazz.isInstance(obj)) {
            return clazz.cast(obj);
        }
        throw this.mismatchException(clazz.getName(), n);
    }
    
    private SQLException mismatchException(final String s, final int n) throws SQLException {
        return this.newSQLException("22005", s, this.getMetaData().getColumnTypeName(n));
    }
    
    @Override
    public <T> T getObject(final String s, final Class<T> clazz) throws SQLException {
        this.checkIfClosed("getObject");
        return this.getObject(this.findColumn(s), clazz);
    }
}
