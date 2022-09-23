// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import org.apache.hive.service.cli.thrift.TFetchOrientation;
import java.util.Iterator;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.thrift.TFetchResultsResp;
import org.apache.hive.service.cli.RowSetFactory;
import org.apache.hive.service.cli.thrift.TFetchResultsReq;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Connection;
import org.apache.hive.service.cli.thrift.TGetOperationStatusResp;
import org.apache.hive.service.cli.thrift.TExecuteStatementResp;
import org.apache.hive.service.cli.thrift.TGetOperationStatusReq;
import org.apache.hive.service.cli.thrift.TExecuteStatementReq;
import org.apache.hive.service.cli.thrift.TCloseOperationResp;
import org.apache.hive.service.cli.thrift.TCloseOperationReq;
import org.apache.hive.service.cli.thrift.TCancelOperationResp;
import org.apache.hive.service.cli.thrift.TCancelOperationReq;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.sql.SQLWarning;
import java.sql.ResultSet;
import java.util.Map;
import org.apache.hive.service.cli.thrift.TSessionHandle;
import org.apache.hive.service.cli.thrift.TOperationHandle;
import org.apache.hive.service.cli.thrift.TCLIService;
import java.sql.Statement;

public class HiveStatement implements Statement
{
    private final HiveConnection connection;
    private TCLIService.Iface client;
    private TOperationHandle stmtHandle;
    private final TSessionHandle sessHandle;
    Map<String, String> sessConf;
    private int fetchSize;
    private boolean isScrollableResultset;
    private ResultSet resultSet;
    private int maxRows;
    private SQLWarning warningChain;
    private boolean isClosed;
    private boolean isCancelled;
    private boolean isQueryClosed;
    private boolean isLogBeingGenerated;
    private boolean isExecuteStatementFailed;
    private ReentrantLock transportLock;
    
    public HiveStatement(final HiveConnection connection, final TCLIService.Iface client, final TSessionHandle sessHandle) {
        this(connection, client, sessHandle, false);
    }
    
    public HiveStatement(final HiveConnection connection, final TCLIService.Iface client, final TSessionHandle sessHandle, final boolean isScrollableResultset) {
        this.stmtHandle = null;
        this.sessConf = new HashMap<String, String>();
        this.fetchSize = 50;
        this.isScrollableResultset = false;
        this.resultSet = null;
        this.maxRows = 0;
        this.warningChain = null;
        this.isClosed = false;
        this.isCancelled = false;
        this.isQueryClosed = false;
        this.isLogBeingGenerated = true;
        this.isExecuteStatementFailed = false;
        this.transportLock = new ReentrantLock(true);
        this.connection = connection;
        this.client = client;
        this.sessHandle = sessHandle;
        this.isScrollableResultset = isScrollableResultset;
    }
    
    @Override
    public void addBatch(final String sql) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void cancel() throws SQLException {
        this.checkConnection("cancel");
        if (this.isCancelled) {
            return;
        }
        this.transportLock.lock();
        try {
            if (this.stmtHandle != null) {
                final TCancelOperationReq cancelReq = new TCancelOperationReq(this.stmtHandle);
                final TCancelOperationResp cancelResp = this.client.CancelOperation(cancelReq);
                Utils.verifySuccessWithInfo(cancelResp.getStatus());
            }
        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new SQLException(e2.toString(), "08S01", e2);
        }
        finally {
            this.transportLock.unlock();
        }
        this.isCancelled = true;
    }
    
    @Override
    public void clearBatch() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.warningChain = null;
    }
    
    void closeClientOperation() throws SQLException {
        this.transportLock.lock();
        try {
            if (this.stmtHandle != null) {
                final TCloseOperationReq closeReq = new TCloseOperationReq(this.stmtHandle);
                final TCloseOperationResp closeResp = this.client.CloseOperation(closeReq);
                Utils.verifySuccessWithInfo(closeResp.getStatus());
            }
        }
        catch (SQLException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new SQLException(e2.toString(), "08S01", e2);
        }
        finally {
            this.transportLock.unlock();
        }
        this.isQueryClosed = true;
        this.isExecuteStatementFailed = false;
        this.stmtHandle = null;
    }
    
    @Override
    public void close() throws SQLException {
        if (this.isClosed) {
            return;
        }
        this.closeClientOperation();
        this.client = null;
        this.resultSet = null;
        this.isClosed = true;
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean execute(final String sql) throws SQLException {
        this.checkConnection("execute");
        this.closeClientOperation();
        this.initFlags();
        final TExecuteStatementReq execReq = new TExecuteStatementReq(this.sessHandle, sql);
        execReq.setRunAsync(true);
        execReq.setConfOverlay(this.sessConf);
        this.transportLock.lock();
        try {
            final TExecuteStatementResp execResp = this.client.ExecuteStatement(execReq);
            Utils.verifySuccessWithInfo(execResp.getStatus());
            this.stmtHandle = execResp.getOperationHandle();
            this.isExecuteStatementFailed = false;
        }
        catch (SQLException eS) {
            this.isExecuteStatementFailed = true;
            throw eS;
        }
        catch (Exception ex) {
            this.isExecuteStatementFailed = true;
            throw new SQLException(ex.toString(), "08S01", ex);
        }
        finally {
            this.transportLock.unlock();
        }
        final TGetOperationStatusReq statusReq = new TGetOperationStatusReq(this.stmtHandle);
        boolean operationComplete = false;
        while (!operationComplete) {
            try {
                this.transportLock.lock();
                TGetOperationStatusResp statusResp;
                try {
                    statusResp = this.client.GetOperationStatus(statusReq);
                }
                finally {
                    this.transportLock.unlock();
                }
                Utils.verifySuccessWithInfo(statusResp.getStatus());
                if (!statusResp.isSetOperationState()) {
                    continue;
                }
                switch (statusResp.getOperationState()) {
                    case CLOSED_STATE:
                    case FINISHED_STATE: {
                        operationComplete = true;
                        continue;
                    }
                    case CANCELED_STATE: {
                        throw new SQLException("Query was cancelled", "01000");
                    }
                    case ERROR_STATE: {
                        throw new SQLException(statusResp.getErrorMessage(), statusResp.getSqlState(), statusResp.getErrorCode());
                    }
                    case UKNOWN_STATE: {
                        throw new SQLException("Unknown query", "HY000");
                    }
                }
                continue;
            }
            catch (SQLException e) {
                this.isLogBeingGenerated = false;
                throw e;
            }
            catch (Exception e2) {
                this.isLogBeingGenerated = false;
                throw new SQLException(e2.toString(), "08S01", e2);
            }
            break;
        }
        this.isLogBeingGenerated = false;
        if (!this.stmtHandle.isHasResultSet()) {
            return false;
        }
        this.resultSet = new HiveQueryResultSet.Builder(this).setClient(this.client).setSessionHandle(this.sessHandle).setStmtHandle(this.stmtHandle).setMaxRows(this.maxRows).setFetchSize(this.fetchSize).setScrollable(this.isScrollableResultset).setTransportLock(this.transportLock).build();
        return true;
    }
    
    private void checkConnection(final String action) throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Can't " + action + " after statement has been closed");
        }
    }
    
    private void initFlags() {
        this.isCancelled = false;
        this.isQueryClosed = false;
        this.isLogBeingGenerated = true;
        this.isExecuteStatementFailed = false;
    }
    
    @Override
    public boolean execute(final String sql, final int autoGeneratedKeys) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean execute(final String sql, final int[] columnIndexes) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean execute(final String sql, final String[] columnNames) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        if (!this.execute(sql)) {
            throw new SQLException("The query did not generate a result set!");
        }
        return this.resultSet;
    }
    
    @Override
    public int executeUpdate(final String sql) throws SQLException {
        this.execute(sql);
        return 0;
    }
    
    @Override
    public int executeUpdate(final String sql, final int autoGeneratedKeys) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int executeUpdate(final String sql, final int[] columnIndexes) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int executeUpdate(final String sql, final String[] columnNames) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        this.checkConnection("getConnection");
        return this.connection;
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        this.checkConnection("getFetchDirection");
        return 1000;
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        this.checkConnection("getFetchSize");
        return this.fetchSize;
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLFeatureNotSupportedException("Method not supported");
    }
    
    @Override
    public int getMaxFieldSize() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getMaxRows() throws SQLException {
        this.checkConnection("getMaxRows");
        return this.maxRows;
    }
    
    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }
    
    @Override
    public boolean getMoreResults(final int current) throws SQLException {
        throw new SQLFeatureNotSupportedException("Method not supported");
    }
    
    @Override
    public int getQueryTimeout() throws SQLException {
        this.checkConnection("getQueryTimeout");
        return 0;
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        this.checkConnection("getResultSet");
        return this.resultSet;
    }
    
    @Override
    public int getResultSetConcurrency() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getResultSetType() throws SQLException {
        this.checkConnection("getResultSetType");
        return 1003;
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        this.checkConnection("getUpdateCount");
        return -1;
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.checkConnection("getWarnings");
        return this.warningChain;
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.isClosed;
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }
    
    @Override
    public void setCursorName(final String name) throws SQLException {
        throw new SQLFeatureNotSupportedException("Method not supported");
    }
    
    @Override
    public void setEscapeProcessing(final boolean enable) throws SQLException {
        if (enable) {
            throw new SQLException("Method not supported");
        }
    }
    
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        this.checkConnection("setFetchDirection");
        if (direction != 1000) {
            throw new SQLException("Not supported direction " + direction);
        }
    }
    
    @Override
    public void setFetchSize(final int rows) throws SQLException {
        this.checkConnection("setFetchSize");
        this.fetchSize = rows;
    }
    
    @Override
    public void setMaxFieldSize(final int max) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setMaxRows(final int max) throws SQLException {
        this.checkConnection("setMaxRows");
        if (max < 0) {
            throw new SQLException("max must be >= 0");
        }
        this.maxRows = max;
    }
    
    @Override
    public void setPoolable(final boolean poolable) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setQueryTimeout(final int seconds) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException("Cannot unwrap to " + iface);
    }
    
    public boolean hasMoreLogs() {
        return this.isLogBeingGenerated;
    }
    
    public List<String> getQueryLog() throws SQLException, ClosedOrCancelledStatementException {
        return this.getQueryLog(true, this.fetchSize);
    }
    
    public List<String> getQueryLog(final boolean incremental, final int fetchSize) throws SQLException, ClosedOrCancelledStatementException {
        this.checkConnection("getQueryLog");
        if (this.isCancelled) {
            throw new ClosedOrCancelledStatementException("Method getQueryLog() failed. The statement has been closed or cancelled.");
        }
        final List<String> logs = new ArrayList<String>();
        TFetchResultsResp tFetchResultsResp = null;
        this.transportLock.lock();
        Label_0204: {
            try {
                if (this.stmtHandle != null) {
                    final TFetchResultsReq tFetchResultsReq = new TFetchResultsReq(this.stmtHandle, this.getFetchOrientation(incremental), fetchSize);
                    tFetchResultsReq.setFetchType((short)1);
                    tFetchResultsResp = this.client.FetchResults(tFetchResultsReq);
                    Utils.verifySuccessWithInfo(tFetchResultsResp.getStatus());
                    break Label_0204;
                }
                if (this.isQueryClosed) {
                    throw new ClosedOrCancelledStatementException("Method getQueryLog() failed. The statement has been closed or cancelled.");
                }
                if (this.isExecuteStatementFailed) {
                    throw new SQLException("Method getQueryLog() failed. Because the stmtHandle in HiveStatement is null and the statement execution might fail.");
                }
                return logs;
            }
            catch (SQLException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new SQLException("Error when getting query log: " + e2, e2);
            }
            finally {
                this.transportLock.unlock();
            }
        }
        final RowSet rowSet = RowSetFactory.create(tFetchResultsResp.getResults(), this.connection.getProtocol());
        for (final Object[] row : rowSet) {
            logs.add(String.valueOf(row[0]));
        }
        return logs;
    }
    
    private TFetchOrientation getFetchOrientation(final boolean incremental) {
        if (incremental) {
            return TFetchOrientation.FETCH_NEXT;
        }
        return TFetchOrientation.FETCH_FIRST;
    }
}
