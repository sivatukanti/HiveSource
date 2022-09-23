// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hive.service.auth.HiveAuthFactory;
import java.util.Collections;

public abstract class CLIServiceClient implements ICLIService
{
    private static final long DEFAULT_MAX_ROWS = 1000L;
    
    public SessionHandle openSession(final String username, final String password) throws HiveSQLException {
        return this.openSession(username, password, Collections.emptyMap());
    }
    
    @Override
    public RowSet fetchResults(final OperationHandle opHandle) throws HiveSQLException {
        return this.fetchResults(opHandle, FetchOrientation.FETCH_NEXT, 1000L, FetchType.QUERY_OUTPUT);
    }
    
    @Override
    public abstract String getDelegationToken(final SessionHandle p0, final HiveAuthFactory p1, final String p2, final String p3) throws HiveSQLException;
    
    @Override
    public abstract void cancelDelegationToken(final SessionHandle p0, final HiveAuthFactory p1, final String p2) throws HiveSQLException;
    
    @Override
    public abstract void renewDelegationToken(final SessionHandle p0, final HiveAuthFactory p1, final String p2) throws HiveSQLException;
}
