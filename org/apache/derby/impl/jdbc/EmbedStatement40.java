// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLException;

public class EmbedStatement40 extends EmbedStatement
{
    public EmbedStatement40(final EmbedConnection embedConnection, final boolean b, final int n, final int n2, final int n3) {
        super(embedConnection, b, n, n2, n3);
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
