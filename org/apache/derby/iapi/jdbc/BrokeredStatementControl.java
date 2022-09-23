// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public interface BrokeredStatementControl
{
    int checkHoldCursors(final int p0) throws SQLException;
    
    void closeRealStatement() throws SQLException;
    
    void closeRealCallableStatement() throws SQLException;
    
    void closeRealPreparedStatement() throws SQLException;
    
    Statement getRealStatement() throws SQLException;
    
    PreparedStatement getRealPreparedStatement() throws SQLException;
    
    CallableStatement getRealCallableStatement() throws SQLException;
    
    ResultSet wrapResultSet(final Statement p0, final ResultSet p1);
    
    ExceptionFactory getExceptionFactory();
}
