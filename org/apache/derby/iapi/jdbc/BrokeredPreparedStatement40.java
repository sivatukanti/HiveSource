// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLXML;
import java.io.InputStream;
import java.sql.NClob;
import java.io.Reader;
import java.sql.RowId;
import java.sql.SQLException;

public class BrokeredPreparedStatement40 extends BrokeredPreparedStatement
{
    public BrokeredPreparedStatement40(final BrokeredStatementControl brokeredStatementControl, final String s, final Object o) throws SQLException {
        super(brokeredStatementControl, s, o);
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
        this.getPreparedStatement().setNCharacterStream(n, reader);
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
    public void setClob(final int n, final Reader reader, final long n2) throws SQLException {
        this.getPreparedStatement().setClob(n, reader, n2);
    }
    
    @Override
    public void setBlob(final int n, final InputStream inputStream, final long n2) throws SQLException {
        this.getPreparedStatement().setBlob(n, inputStream, n2);
    }
    
    @Override
    public final void setNClob(final int n, final Reader reader) throws SQLException {
        this.getPreparedStatement().setNClob(n, reader);
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
    public final void setAsciiStream(final int n, final InputStream inputStream) throws SQLException {
        this.getPreparedStatement().setAsciiStream(n, inputStream);
    }
    
    @Override
    public final void setAsciiStream(final int n, final InputStream inputStream, final long n2) throws SQLException {
        this.getPreparedStatement().setAsciiStream(n, inputStream, n2);
    }
    
    @Override
    public final void setBinaryStream(final int n, final InputStream inputStream, final long n2) throws SQLException {
        this.getPreparedStatement().setBinaryStream(n, inputStream, n2);
    }
    
    @Override
    public final void setBlob(final int n, final InputStream inputStream) throws SQLException {
        this.getPreparedStatement().setBlob(n, inputStream);
    }
    
    @Override
    public final void setCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        this.getPreparedStatement().setCharacterStream(n, reader, n2);
    }
    
    @Override
    public final void setClob(final int n, final Reader reader) throws SQLException {
        this.getPreparedStatement().setClob(n, reader);
    }
}
