// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

import org.apache.derby.iapi.db.TriggerExecutionContext;
import org.apache.derby.iapi.db.Factory;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.derby.vti.UpdatableVTITemplate;

public class TriggerOldTransitionRows extends UpdatableVTITemplate
{
    private ResultSet resultSet;
    
    public TriggerOldTransitionRows() throws SQLException {
        this.initializeResultSet();
    }
    
    private ResultSet initializeResultSet() throws SQLException {
        if (this.resultSet != null) {
            this.resultSet.close();
        }
        final TriggerExecutionContext triggerExecutionContext = Factory.getTriggerExecutionContext();
        if (triggerExecutionContext == null) {
            throw new SQLException("There are no active triggers", "38000");
        }
        this.resultSet = triggerExecutionContext.getOldRowSet();
        if (this.resultSet == null) {
            throw new SQLException("There is no old transition rows result set for this trigger", "38000");
        }
        return this.resultSet;
    }
    
    public ResultSet executeQuery() throws SQLException {
        return this.initializeResultSet();
    }
    
    public int getResultSetConcurrency() {
        return 1007;
    }
    
    public void close() throws SQLException {
        this.resultSet.close();
    }
}
