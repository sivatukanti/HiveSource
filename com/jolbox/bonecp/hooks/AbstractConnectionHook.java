// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp.hooks;

import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import com.jolbox.bonecp.StatementHandle;
import com.jolbox.bonecp.PoolUtil;
import java.util.Map;
import java.sql.Statement;
import com.jolbox.bonecp.ConnectionHandle;
import org.slf4j.Logger;

public abstract class AbstractConnectionHook implements ConnectionHook
{
    private static final Logger logger;
    
    public void onAcquire(final ConnectionHandle connection) {
    }
    
    public void onCheckIn(final ConnectionHandle connection) {
    }
    
    public void onCheckOut(final ConnectionHandle connection) {
    }
    
    public void onDestroy(final ConnectionHandle connection) {
    }
    
    public boolean onAcquireFail(final Throwable t, final AcquireFailConfig acquireConfig) {
        boolean tryAgain = false;
        final String log = acquireConfig.getLogMessage();
        AbstractConnectionHook.logger.error(log + " Sleeping for " + acquireConfig.getAcquireRetryDelayInMs() + "ms and trying again. Attempts left: " + acquireConfig.getAcquireRetryAttempts() + ". Exception: " + t.getCause() + ".Message:" + t.getMessage());
        try {
            Thread.sleep(acquireConfig.getAcquireRetryDelayInMs());
            if (acquireConfig.getAcquireRetryAttempts().get() > 0) {
                tryAgain = (acquireConfig.getAcquireRetryAttempts().decrementAndGet() > 0);
            }
        }
        catch (Exception e) {
            tryAgain = false;
        }
        return tryAgain;
    }
    
    public void onQueryExecuteTimeLimitExceeded(final ConnectionHandle handle, final Statement statement, final String sql, final Map<Object, Object> logParams, final long timeElapsedInNs) {
        this.onQueryExecuteTimeLimitExceeded(handle, statement, sql, logParams);
    }
    
    @Deprecated
    public void onQueryExecuteTimeLimitExceeded(final ConnectionHandle handle, final Statement statement, final String sql, final Map<Object, Object> logParams) {
        this.onQueryExecuteTimeLimitExceeded(sql, logParams);
    }
    
    @Deprecated
    public void onQueryExecuteTimeLimitExceeded(final String sql, final Map<Object, Object> logParams) {
        final StringBuilder sb = new StringBuilder("Query execute time limit exceeded. Query: ");
        sb.append(PoolUtil.fillLogParams(sql, logParams));
        AbstractConnectionHook.logger.warn(sb.toString());
    }
    
    public boolean onConnectionException(final ConnectionHandle connection, final String state, final Throwable t) {
        return true;
    }
    
    public void onBeforeStatementExecute(final ConnectionHandle conn, final StatementHandle statement, final String sql, final Map<Object, Object> params) {
    }
    
    public void onAfterStatementExecute(final ConnectionHandle conn, final StatementHandle statement, final String sql, final Map<Object, Object> params) {
    }
    
    public ConnectionState onMarkPossiblyBroken(final ConnectionHandle connection, final String state, final SQLException e) {
        return ConnectionState.NOP;
    }
    
    static {
        logger = LoggerFactory.getLogger(AbstractConnectionHook.class);
    }
}
