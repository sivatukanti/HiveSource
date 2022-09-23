// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.db;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.derby.catalog.UUID;

public interface TriggerExecutionContext
{
    public static final int UPDATE_EVENT = 1;
    public static final int DELETE_EVENT = 2;
    public static final int INSERT_EVENT = 3;
    
    String getTargetTableName();
    
    UUID getTargetTableId();
    
    int getEventType();
    
    String getEventStatementText();
    
    String[] getModifiedColumns();
    
    boolean wasColumnModified(final String p0);
    
    boolean wasColumnModified(final int p0);
    
    ResultSet getOldRowSet() throws SQLException;
    
    ResultSet getNewRowSet() throws SQLException;
    
    ResultSet getOldRow() throws SQLException;
    
    ResultSet getNewRow() throws SQLException;
}
