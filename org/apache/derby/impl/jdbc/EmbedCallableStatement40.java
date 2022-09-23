// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.SQLXML;
import java.io.InputStream;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.Clob;
import java.sql.Blob;
import java.io.Reader;
import java.sql.SQLException;
import org.apache.derby.iapi.jdbc.EngineCallableStatement40;

public class EmbedCallableStatement40 extends EmbedCallableStatement30 implements EngineCallableStatement40
{
    public EmbedCallableStatement40(final EmbedConnection embedConnection, final String s, final int n, final int n2, final int n3) throws SQLException {
        super(embedConnection, s, n, n2, n3);
    }
    
    @Override
    public Reader getCharacterStream(final String s) throws SQLException {
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
    public String getNString(final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public String getNString(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setBlob(final String s, final Blob blob) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setClob(final String s, final Clob clob) throws SQLException {
        throw Util.notImplemented();
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
    public void setRowId(final String s, final RowId rowId) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNString(final String s, final String s2) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNClob(final String s, final NClob nClob) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setClob(final String s, final Reader reader, final long n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setBlob(final String s, final InputStream inputStream, final long n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNClob(final String s, final Reader reader, final long n) throws SQLException {
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
    public void setSQLXML(final String s, final SQLXML sqlxml) throws SQLException {
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
    public void setRowId(final int n, final RowId rowId) throws SQLException {
        throw Util.notImplemented("setRowId(int, RowId)");
    }
    
    @Override
    public void setNString(final int n, final String s) throws SQLException {
        throw Util.notImplemented("setNString (int,value)");
    }
    
    @Override
    public void setNCharacterStream(final int n, final Reader reader) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        throw Util.notImplemented("setNCharacterStream (int, Reader, long)");
    }
    
    @Override
    public void setNClob(final int n, final NClob nClob) throws SQLException {
        throw Util.notImplemented("setNClob (int, NClob)");
    }
    
    @Override
    public void setNClob(final int n, final Reader reader) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw Util.notImplemented("setNClob(int,reader,length)");
    }
    
    @Override
    public void setSQLXML(final int n, final SQLXML sqlxml) throws SQLException {
        throw Util.notImplemented("setSQLXML (int, SQLXML)");
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        this.checkStatus();
        return clazz.isInstance(this);
    }
    
    @Override
    public void setAsciiStream(final String s, final InputStream inputStream) throws SQLException {
        throw Util.notImplemented("setAsciiStream(String,InputStream)");
    }
    
    @Override
    public void setBinaryStream(final String s, final InputStream inputStream) throws SQLException {
        throw Util.notImplemented("setBinaryStream(String,InputStream)");
    }
    
    @Override
    public void setBlob(final String s, final InputStream inputStream) throws SQLException {
        throw Util.notImplemented("setBlob(String,InputStream)");
    }
    
    @Override
    public void setCharacterStream(final String s, final Reader reader) throws SQLException {
        throw Util.notImplemented("setCharacterStream(String,Reader)");
    }
    
    @Override
    public void setClob(final String s, final Reader reader) throws SQLException {
        throw Util.notImplemented("setClob(String,Reader)");
    }
    
    @Override
    public void setNCharacterStream(final String s, final Reader reader) throws SQLException {
        throw Util.notImplemented("setNCharacterStream(String,Reader)");
    }
    
    @Override
    public void setNClob(final String s, final Reader reader) throws SQLException {
        throw Util.notImplemented("setNClob(String,Reader)");
    }
    
    @Override
    public <T> T unwrap(final Class<T> clazz) throws SQLException {
        this.checkStatus();
        try {
            return clazz.cast(this);
        }
        catch (ClassCastException ex) {
            throw this.newSQLException("XJ128.S", clazz);
        }
    }
    
    @Override
    public final void setAsciiStream(final String s, final InputStream inputStream, final long n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public final void setBinaryStream(final String s, final InputStream inputStream, final long n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public final void setCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public <T> T getObject(final int n, final Class<T> clazz) throws SQLException {
        this.checkStatus();
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
        return this.newSQLException("22005", s, this.getParameterMetaData().getParameterTypeName(n));
    }
    
    @Override
    public <T> T getObject(final String s, final Class<T> clazz) throws SQLException {
        throw Util.notImplemented();
    }
}
