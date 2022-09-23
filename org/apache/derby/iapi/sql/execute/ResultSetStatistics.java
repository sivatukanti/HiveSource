// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.sql.execute.xplain.XPLAINable;

public interface ResultSetStatistics extends XPLAINable
{
    String getStatementExecutionPlanText(final int p0);
    
    String getScanStatisticsText(final String p0, final int p1);
    
    double getEstimatedRowCount();
}
