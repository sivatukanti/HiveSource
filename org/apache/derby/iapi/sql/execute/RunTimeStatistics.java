// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.sql.execute.xplain.XPLAINVisitor;
import java.sql.Timestamp;

public interface RunTimeStatistics
{
    long getCompileTimeInMillis();
    
    long getParseTimeInMillis();
    
    long getBindTimeInMillis();
    
    long getOptimizeTimeInMillis();
    
    long getGenerateTimeInMillis();
    
    long getExecuteTimeInMillis();
    
    Timestamp getBeginCompilationTimestamp();
    
    Timestamp getEndCompilationTimestamp();
    
    Timestamp getBeginExecutionTimestamp();
    
    Timestamp getEndExecutionTimestamp();
    
    String getStatementName();
    
    String getSPSName();
    
    String getStatementText();
    
    String getStatementExecutionPlanText();
    
    String getScanStatisticsText();
    
    String getScanStatisticsText(final String p0);
    
    double getEstimatedRowCount();
    
    void acceptFromTopResultSet(final XPLAINVisitor p0);
}
