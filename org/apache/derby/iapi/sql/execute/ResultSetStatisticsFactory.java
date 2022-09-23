// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.Activation;

public interface ResultSetStatisticsFactory
{
    public static final String MODULE = "org.apache.derby.iapi.sql.execute.ResultSetStatisticsFactory";
    
    RunTimeStatistics getRunTimeStatistics(final Activation p0, final ResultSet p1, final NoPutResultSet[] p2) throws StandardException;
    
    ResultSetStatistics getResultSetStatistics(final ResultSet p0);
    
    ResultSetStatistics getResultSetStatistics(final NoPutResultSet p0);
    
    ResultSetStatistics getNoRowsResultSetStatistics(final ResultSet p0);
}
