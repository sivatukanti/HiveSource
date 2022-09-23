// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.RowIdLifetime;
import org.apache.derby.iapi.services.info.JVMInfo;
import java.sql.SQLException;

public class EmbedDatabaseMetaData40 extends EmbedDatabaseMetaData
{
    public EmbedDatabaseMetaData40(final EmbedConnection embedConnection, final String s) throws SQLException {
        super(embedConnection, s);
    }
    
    @Override
    public int getJDBCMajorVersion() {
        return 4;
    }
    
    @Override
    public int getJDBCMinorVersion() {
        return JVMInfo.jdbcMinorVersion();
    }
    
    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return RowIdLifetime.ROWID_UNSUPPORTED;
    }
    
    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return true;
    }
    
    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return true;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        return clazz.isInstance(this);
    }
    
    @Override
    public <T> T unwrap(final Class<T> clazz) throws SQLException {
        try {
            return clazz.cast(this);
        }
        catch (ClassCastException ex) {
            throw this.newSQLException("XJ128.S", clazz);
        }
    }
}
