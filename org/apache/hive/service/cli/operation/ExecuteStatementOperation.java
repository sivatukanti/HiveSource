// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hadoop.hive.ql.processors.CommandProcessor;
import java.sql.SQLException;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hadoop.hive.ql.processors.CommandProcessorFactory;
import java.util.HashMap;
import org.apache.hive.service.cli.OperationType;
import org.apache.hive.service.cli.session.HiveSession;
import java.util.Map;

public abstract class ExecuteStatementOperation extends Operation
{
    protected String statement;
    protected Map<String, String> confOverlay;
    
    public ExecuteStatementOperation(final HiveSession parentSession, final String statement, final Map<String, String> confOverlay, final boolean runInBackground) {
        super(parentSession, OperationType.EXECUTE_STATEMENT, runInBackground);
        this.statement = null;
        this.confOverlay = new HashMap<String, String>();
        this.statement = statement;
        this.setConfOverlay(confOverlay);
    }
    
    public String getStatement() {
        return this.statement;
    }
    
    public static ExecuteStatementOperation newExecuteStatementOperation(final HiveSession parentSession, final String statement, final Map<String, String> confOverlay, final boolean runAsync) throws HiveSQLException {
        final String[] tokens = statement.trim().split("\\s+");
        CommandProcessor processor = null;
        try {
            processor = CommandProcessorFactory.getForHiveCommand(tokens, parentSession.getHiveConf());
        }
        catch (SQLException e) {
            throw new HiveSQLException(e.getMessage(), e.getSQLState(), e);
        }
        if (processor == null) {
            return new SQLOperation(parentSession, statement, confOverlay, runAsync);
        }
        return new HiveCommandOperation(parentSession, statement, processor, confOverlay);
    }
    
    protected Map<String, String> getConfOverlay() {
        return this.confOverlay;
    }
    
    protected void setConfOverlay(final Map<String, String> confOverlay) {
        if (confOverlay != null) {
            this.confOverlay = confOverlay;
        }
    }
}
