// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

public interface ConnectionProvider
{
    void setFailOnError(final boolean p0);
    
    Connection getConnection(final DataSource[] p0) throws SQLException;
}
