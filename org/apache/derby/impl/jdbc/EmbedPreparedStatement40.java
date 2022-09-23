// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLXML;
import java.sql.NClob;
import java.io.Reader;
import java.sql.RowId;
import java.sql.SQLException;

public class EmbedPreparedStatement40 extends EmbedPreparedStatement30
{
    public EmbedPreparedStatement40(final EmbedConnection embedConnection, final String s, final boolean b, final int n, final int n2, final int n3, final int n4, final int[] array, final String[] array2) throws SQLException {
        super(embedConnection, s, b, n, n2, n3, n4, array, array2);
    }
    
    @Override
    public void setRowId(final int n, final RowId rowId) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNString(final int n, final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNCharacterStream(final int n, final Reader reader) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNClob(final int n, final Reader reader) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNClob(final int n, final NClob nClob) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setNClob(final int n, final Reader reader, final long n2) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public void setSQLXML(final int n, final SQLXML sqlxml) throws SQLException {
        throw Util.notImplemented();
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        this.checkStatus();
        return clazz.isInstance(this);
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
}
