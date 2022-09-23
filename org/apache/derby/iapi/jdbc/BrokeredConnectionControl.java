// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public interface BrokeredConnectionControl
{
    EngineConnection getRealConnection() throws SQLException;
    
    void notifyException(final SQLException p0);
    
    void checkAutoCommit(final boolean p0) throws SQLException;
    
    void checkSavepoint() throws SQLException;
    
    void checkRollback() throws SQLException;
    
    void checkCommit() throws SQLException;
    
    void checkClose() throws SQLException;
    
    int checkHoldCursors(final int p0, final boolean p1) throws SQLException;
    
    boolean isIsolationLevelSetUsingSQLorJDBC() throws SQLException;
    
    void resetIsolationLevelFlag() throws SQLException;
    
    boolean isInGlobalTransaction();
    
    boolean closingConnection() throws SQLException;
    
    Statement wrapStatement(final Statement p0) throws SQLException;
    
    PreparedStatement wrapStatement(final PreparedStatement p0, final String p1, final Object p2) throws SQLException;
    
    CallableStatement wrapStatement(final CallableStatement p0, final String p1) throws SQLException;
    
    void onStatementClose(final PreparedStatement p0);
    
    void onStatementErrorOccurred(final PreparedStatement p0, final SQLException p1);
}
