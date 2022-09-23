// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLXML;
import java.io.InputStream;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.RowId;
import java.io.Reader;
import java.sql.SQLException;

public class BrokeredCallableStatement40 extends BrokeredCallableStatement implements EngineCallableStatement40
{
    public BrokeredCallableStatement40(final BrokeredStatementControl brokeredStatementControl, final String s) throws SQLException {
        super(brokeredStatementControl, s);
    }
    
    @Override
    public Reader getCharacterStream(final int n) throws SQLException {
        return this.getCallableStatement().getCharacterStream(n);
    }
    
    @Override
    public Reader getCharacterStream(final String s) throws SQLException {
        return this.getCallableStatement().getCharacterStream(s);
    }
    
    @Override
    public Reader getNCharacterStream(final int n) throws SQLException {
        return this.getCallableStatement().getNCharacterStream(n);
    }
    
    @Override
    public Reader getNCharacterStream(final String s) throws SQLException {
        return this.getCallableStatement().getNCharacterStream(s);
    }
    
    @Override
    public String getNString(final int n) throws SQLException {
        return this.getCallableStatement().getNString(n);
    }
    
    @Override
    public String getNString(final String s) throws SQLException {
        return this.getCallableStatement().getNString(s);
    }
    
    @Override
    public RowId getRowId(final int n) throws SQLException {
        return this.getCallableStatement().getRowId(n);
    }
    
    @Override
    public RowId getRowId(final String s) throws SQLException {
        return this.getCallableStatement().getRowId(s);
    }
    
    @Override
    public void setRowId(final String s, final RowId rowId) throws SQLException {
        this.getCallableStatement().setRowId(s, rowId);
    }
    
    @Override
    public void setBlob(final String s, final Blob blob) throws SQLException {
        this.getCallableStatement().setBlob(s, blob);
    }
    
    @Override
    public void setClob(final String s, final Clob clob) throws SQLException {
        this.getCallableStatement().setClob(s, clob);
    }
    
    @Override
    public void setNString(final String s, final String s2) throws SQLException {
        this.getCallableStatement().setNString(s, s2);
    }
    
    @Override
    public final void setNCharacterStream(final String s, final Reader reader) throws SQLException {
        this.getCallableStatement().setNCharacterStream(s, reader);
    }
    
    @Override
    public void setNCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        this.getCallableStatement().setNCharacterStream(s, reader, n);
    }
    
    @Override
    public void setNClob(final String s, final NClob nClob) throws SQLException {
        this.getCallableStatement().setNClob(s, nClob);
    }
    
    @Override
    public final void setClob(final String s, final Reader reader) throws SQLException {
        this.getCallableStatement().setClob(s, reader);
    }
    
    @Override
    public void setClob(final String s, final Reader reader, final long n) throws SQLException {
        this.getCallableStatement().setClob(s, reader, n);
    }
    
    @Override
    public final void setBlob(final String s, final InputStream inputStream) throws SQLException {
        this.getCallableStatement().setBlob(s, inputStream);
    }
    
    @Override
    public void setBlob(final String s, final InputStream inputStream, final long n) throws SQLException {
        this.getCallableStatement().setBlob(s, inputStream, n);
    }
    
    @Override
    public final void setNClob(final String s, final Reader reader) throws SQLException {
        this.getCallableStatement().setNClob(s, reader);
    }
    
    @Override
    public void setNClob(final String s, final Reader reader, final long n) throws SQLException {
        this.getCallableStatement().setNClob(s, reader, n);
    }
    
    @Override
    public NClob getNClob(final int n) throws SQLException {
        return this.getCallableStatement().getNClob(n);
    }
    
    @Override
    public NClob getNClob(final String s) throws SQLException {
        return this.getCallableStatement().getNClob(s);
    }
    
    @Override
    public void setSQLXML(final String s, final SQLXML sqlxml) throws SQLException {
        this.getCallableStatement().setSQLXML(s, sqlxml);
    }
    
    @Override
    public SQLXML getSQLXML(final int n) throws SQLException {
        return this.getCallableStatement().getSQLXML(n);
    }
    
    @Override
    public SQLXML getSQLXML(final String s) throws SQLException {
        return this.getCallableStatement().getSQLXML(s);
    }
    
    @Override
    public final void setAsciiStream(final int n, final InputStream inputStream) throws SQLException {
        this.getCallableStatement().setAsciiStream(n, inputStream);
    }
    
    @Override
    public void setRowId(final int n, final RowId rowId) throws SQLException {
        this.getPreparedStatement().setRowId(n, rowId);
    }
    
    @Override
    public void setNString(final int n, final String s) throws SQLException {
        this.getPreparedStatement().setNString(n, s);
    }
    
    @Override
    public void setNCharacterStream(final int n, final Reader reader) throws SQLException {
        this.getCallableStatement().setNCharacterStream(n, reader);
    }
    
    @Override
    public void setNCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        this.getPreparedStatement().setNCharacterStream(n, reader, n2);
    }
    
    @Override
    public void setNClob(final int n, final NClob nClob) throws SQLException {
        this.getPreparedStatement().setNClob(n, nClob);
    }
    
    @Override
    public final void setClob(final int n, final Reader reader) throws SQLException {
        this.getCallableStatement().setClob(n, reader);
    }
    
    @Override
    public void setClob(final int n, final Reader reader, final long n2) throws SQLException {
        this.getPreparedStatement().setClob(n, reader, n2);
    }
    
    @Override
    public final void setBlob(final int n, final InputStream inputStream) throws SQLException {
        this.getCallableStatement().setBlob(n, inputStream);
    }
    
    @Override
    public void setBlob(final int n, final InputStream inputStream, final long n2) throws SQLException {
        this.getPreparedStatement().setBlob(n, inputStream, n2);
    }
    
    @Override
    public final void setNClob(final int n, final Reader reader) throws SQLException {
        this.getCallableStatement().setNClob(n, reader);
    }
    
    @Override
    public void setNClob(final int n, final Reader reader, final long n2) throws SQLException {
        this.getPreparedStatement().setNClob(n, reader, n2);
    }
    
    @Override
    public void setSQLXML(final int n, final SQLXML sqlxml) throws SQLException {
        this.getPreparedStatement().setSQLXML(n, sqlxml);
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return this.getStatement().isPoolable();
    }
    
    @Override
    public void setPoolable(final boolean poolable) throws SQLException {
        this.getStatement().setPoolable(poolable);
    }
    
    @Override
    public final void setAsciiStream(final int n, final InputStream inputStream, final long n2) throws SQLException {
        this.getCallableStatement().setAsciiStream(n, inputStream, n2);
    }
    
    @Override
    public final void setBinaryStream(final int n, final InputStream inputStream, final long n2) throws SQLException {
        this.getCallableStatement().setBinaryStream(n, inputStream, n2);
    }
    
    @Override
    public final void setCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        this.getCallableStatement().setCharacterStream(n, reader, n2);
    }
    
    @Override
    public final void setAsciiStream(final String s, final InputStream inputStream) throws SQLException {
        this.getCallableStatement().setAsciiStream(s, inputStream);
    }
    
    @Override
    public final void setAsciiStream(final String s, final InputStream inputStream, final long n) throws SQLException {
        this.getCallableStatement().setAsciiStream(s, inputStream, n);
    }
    
    @Override
    public final void setBinaryStream(final String s, final InputStream inputStream) throws SQLException {
        this.getCallableStatement().setBinaryStream(s, inputStream);
    }
    
    @Override
    public final void setBinaryStream(final String s, final InputStream inputStream, final long n) throws SQLException {
        this.getCallableStatement().setBinaryStream(s, inputStream, n);
    }
    
    @Override
    public final void setCharacterStream(final String s, final Reader reader) throws SQLException {
        this.getCallableStatement().setCharacterStream(s, reader);
    }
    
    @Override
    public final void setCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        this.getCallableStatement().setCharacterStream(s, reader, n);
    }
    
    @Override
    public <T> T getObject(final int n, final Class<T> clazz) throws SQLException {
        return ((EngineCallableStatement40)this.getCallableStatement()).getObject(n, clazz);
    }
    
    @Override
    public <T> T getObject(final String s, final Class<T> clazz) throws SQLException {
        return ((EngineCallableStatement40)this.getCallableStatement()).getObject(s, clazz);
    }
}
