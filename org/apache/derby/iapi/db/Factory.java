// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.db;

import java.sql.SQLException;
import org.apache.derby.iapi.sql.conn.ConnectionUtil;
import org.apache.derby.database.Database;

public class Factory
{
    public static Database getDatabaseOfConnection() throws SQLException {
        return ConnectionUtil.getCurrentLCC().getDatabase();
    }
    
    public static TriggerExecutionContext getTriggerExecutionContext() throws SQLException {
        return ConnectionUtil.getCurrentLCC().getTriggerExecutionContext();
    }
}
