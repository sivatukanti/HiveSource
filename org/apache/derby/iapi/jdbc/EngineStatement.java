// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLException;
import java.sql.Statement;

public interface EngineStatement extends Statement
{
    boolean getMoreResults(final int p0) throws SQLException;
    
    int getResultSetHoldability() throws SQLException;
    
    boolean isClosed() throws SQLException;
    
    void closeOnCompletion() throws SQLException;
    
    boolean isCloseOnCompletion() throws SQLException;
    
    long[] executeLargeBatch() throws SQLException;
    
    long executeLargeUpdate(final String p0) throws SQLException;
    
    long executeLargeUpdate(final String p0, final int p1) throws SQLException;
    
    long executeLargeUpdate(final String p0, final int[] p1) throws SQLException;
    
    long executeLargeUpdate(final String p0, final String[] p1) throws SQLException;
    
    long getLargeMaxRows() throws SQLException;
    
    long getLargeUpdateCount() throws SQLException;
    
    void setLargeMaxRows(final long p0) throws SQLException;
}
