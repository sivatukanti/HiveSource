// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp.hooks;

import java.sql.SQLException;
import com.jolbox.bonecp.StatementHandle;
import java.util.Map;
import java.sql.Statement;
import com.jolbox.bonecp.ConnectionHandle;

public interface ConnectionHook
{
    void onAcquire(final ConnectionHandle p0);
    
    void onCheckIn(final ConnectionHandle p0);
    
    void onCheckOut(final ConnectionHandle p0);
    
    void onDestroy(final ConnectionHandle p0);
    
    boolean onAcquireFail(final Throwable p0, final AcquireFailConfig p1);
    
    void onQueryExecuteTimeLimitExceeded(final ConnectionHandle p0, final Statement p1, final String p2, final Map<Object, Object> p3, final long p4);
    
    @Deprecated
    void onQueryExecuteTimeLimitExceeded(final ConnectionHandle p0, final Statement p1, final String p2, final Map<Object, Object> p3);
    
    @Deprecated
    void onQueryExecuteTimeLimitExceeded(final String p0, final Map<Object, Object> p1);
    
    void onBeforeStatementExecute(final ConnectionHandle p0, final StatementHandle p1, final String p2, final Map<Object, Object> p3);
    
    void onAfterStatementExecute(final ConnectionHandle p0, final StatementHandle p1, final String p2, final Map<Object, Object> p3);
    
    boolean onConnectionException(final ConnectionHandle p0, final String p1, final Throwable p2);
    
    ConnectionState onMarkPossiblyBroken(final ConnectionHandle p0, final String p1, final SQLException p2);
}
