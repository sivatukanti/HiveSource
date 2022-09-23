// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import org.apache.derby.iapi.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public interface ConnectionContext
{
    public static final String CONTEXT_ID = "JDBC_ConnectionContext";
    
    Connection getNestedConnection(final boolean p0) throws SQLException;
    
    java.sql.ResultSet getResultSet(final ResultSet p0) throws SQLException;
    
    boolean processInaccessibleDynamicResult(final java.sql.ResultSet p0);
}
