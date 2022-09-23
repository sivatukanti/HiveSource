// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.TableSchema;
import java.io.FileNotFoundException;
import java.io.File;
import org.apache.hive.service.cli.OperationStatus;
import org.apache.hive.service.cli.thrift.TProtocolVersion;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.TimeUnit;
import org.apache.hive.service.cli.OperationType;
import java.util.EnumSet;
import org.apache.hadoop.hive.ql.session.OperationLog;
import java.util.concurrent.Future;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.FetchOrientation;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.session.HiveSession;

public abstract class Operation
{
    protected final HiveSession parentSession;
    private OperationState state;
    private final OperationHandle opHandle;
    private HiveConf configuration;
    public static final Log LOG;
    public static final FetchOrientation DEFAULT_FETCH_ORIENTATION;
    public static final long DEFAULT_FETCH_MAX_ROWS = 100L;
    protected boolean hasResultSet;
    protected volatile HiveSQLException operationException;
    protected final boolean runAsync;
    protected volatile Future<?> backgroundHandle;
    protected OperationLog operationLog;
    protected boolean isOperationLogEnabled;
    private long operationTimeout;
    private long lastAccessTime;
    protected static final EnumSet<FetchOrientation> DEFAULT_FETCH_ORIENTATION_SET;
    
    protected Operation(final HiveSession parentSession, final OperationType opType, final boolean runInBackground) {
        this.state = OperationState.INITIALIZED;
        this.parentSession = parentSession;
        this.runAsync = runInBackground;
        this.opHandle = new OperationHandle(opType, parentSession.getProtocolVersion());
        this.lastAccessTime = System.currentTimeMillis();
        this.operationTimeout = HiveConf.getTimeVar(parentSession.getHiveConf(), HiveConf.ConfVars.HIVE_SERVER2_IDLE_OPERATION_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    
    public Future<?> getBackgroundHandle() {
        return this.backgroundHandle;
    }
    
    protected void setBackgroundHandle(final Future<?> backgroundHandle) {
        this.backgroundHandle = backgroundHandle;
    }
    
    public boolean shouldRunAsync() {
        return this.runAsync;
    }
    
    public void setConfiguration(final HiveConf configuration) {
        this.configuration = new HiveConf(configuration);
    }
    
    public HiveConf getConfiguration() {
        return new HiveConf(this.configuration);
    }
    
    public HiveSession getParentSession() {
        return this.parentSession;
    }
    
    public OperationHandle getHandle() {
        return this.opHandle;
    }
    
    public TProtocolVersion getProtocolVersion() {
        return this.opHandle.getProtocolVersion();
    }
    
    public OperationType getType() {
        return this.opHandle.getOperationType();
    }
    
    public OperationStatus getStatus() {
        return new OperationStatus(this.state, this.operationException);
    }
    
    public boolean hasResultSet() {
        return this.hasResultSet;
    }
    
    protected void setHasResultSet(final boolean hasResultSet) {
        this.hasResultSet = hasResultSet;
        this.opHandle.setHasResultSet(hasResultSet);
    }
    
    public OperationLog getOperationLog() {
        return this.operationLog;
    }
    
    protected final OperationState setState(final OperationState newState) throws HiveSQLException {
        this.state.validateTransition(newState);
        this.state = newState;
        this.lastAccessTime = System.currentTimeMillis();
        return this.state;
    }
    
    public boolean isTimedOut(final long current) {
        if (this.operationTimeout == 0L) {
            return false;
        }
        if (this.operationTimeout > 0L) {
            return this.state.isTerminal() && this.lastAccessTime + this.operationTimeout <= current;
        }
        return this.lastAccessTime + -this.operationTimeout <= current;
    }
    
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }
    
    public long getOperationTimeout() {
        return this.operationTimeout;
    }
    
    public void setOperationTimeout(final long operationTimeout) {
        this.operationTimeout = operationTimeout;
    }
    
    protected void setOperationException(final HiveSQLException operationException) {
        this.operationException = operationException;
    }
    
    protected final void assertState(final OperationState state) throws HiveSQLException {
        if (this.state != state) {
            throw new HiveSQLException("Expected state " + state + ", but found " + this.state);
        }
        this.lastAccessTime = System.currentTimeMillis();
    }
    
    public boolean isRunning() {
        return OperationState.RUNNING.equals(this.state);
    }
    
    public boolean isFinished() {
        return OperationState.FINISHED.equals(this.state);
    }
    
    public boolean isCanceled() {
        return OperationState.CANCELED.equals(this.state);
    }
    
    public boolean isFailed() {
        return OperationState.ERROR.equals(this.state);
    }
    
    protected void createOperationLog() {
        if (this.parentSession.isOperationLogEnabled()) {
            final File operationLogFile = new File(this.parentSession.getOperationLogSessionDir(), this.opHandle.getHandleIdentifier().toString());
            this.isOperationLogEnabled = true;
            try {
                if (operationLogFile.exists()) {
                    Operation.LOG.warn("The operation log file should not exist, but it is already there: " + operationLogFile.getAbsolutePath());
                    operationLogFile.delete();
                }
                if (!operationLogFile.createNewFile() && (!operationLogFile.canRead() || !operationLogFile.canWrite())) {
                    Operation.LOG.warn("The already existed operation log file cannot be recreated, and it cannot be read or written: " + operationLogFile.getAbsolutePath());
                    this.isOperationLogEnabled = false;
                    return;
                }
            }
            catch (Exception e) {
                Operation.LOG.warn("Unable to create operation log file: " + operationLogFile.getAbsolutePath(), e);
                this.isOperationLogEnabled = false;
                return;
            }
            try {
                this.operationLog = new OperationLog(this.opHandle.toString(), operationLogFile, this.parentSession.getHiveConf());
            }
            catch (FileNotFoundException e2) {
                Operation.LOG.warn("Unable to instantiate OperationLog object for operation: " + this.opHandle, e2);
                this.isOperationLogEnabled = false;
                return;
            }
            OperationLog.setCurrentOperationLog(this.operationLog);
        }
    }
    
    protected void unregisterOperationLog() {
        if (this.isOperationLogEnabled) {
            OperationLog.removeCurrentOperationLog();
        }
    }
    
    protected void beforeRun() {
        this.createOperationLog();
    }
    
    protected void afterRun() {
        this.unregisterOperationLog();
    }
    
    protected abstract void runInternal() throws HiveSQLException;
    
    public void run() throws HiveSQLException {
        this.beforeRun();
        try {
            this.runInternal();
        }
        finally {
            this.afterRun();
        }
    }
    
    protected void cleanupOperationLog() {
        if (this.isOperationLogEnabled) {
            if (this.operationLog == null) {
                Operation.LOG.error("Operation [ " + this.opHandle.getHandleIdentifier() + " ] " + "logging is enabled, but its OperationLog object cannot be found.");
            }
            else {
                this.operationLog.close();
            }
        }
    }
    
    public void cancel() throws HiveSQLException {
        this.setState(OperationState.CANCELED);
        throw new UnsupportedOperationException("SQLOperation.cancel()");
    }
    
    public abstract void close() throws HiveSQLException;
    
    public abstract TableSchema getResultSetSchema() throws HiveSQLException;
    
    public abstract RowSet getNextRowSet(final FetchOrientation p0, final long p1) throws HiveSQLException;
    
    public RowSet getNextRowSet() throws HiveSQLException {
        return this.getNextRowSet(FetchOrientation.FETCH_NEXT, 100L);
    }
    
    protected void validateDefaultFetchOrientation(final FetchOrientation orientation) throws HiveSQLException {
        this.validateFetchOrientation(orientation, Operation.DEFAULT_FETCH_ORIENTATION_SET);
    }
    
    protected void validateFetchOrientation(final FetchOrientation orientation, final EnumSet<FetchOrientation> supportedOrientations) throws HiveSQLException {
        if (!supportedOrientations.contains(orientation)) {
            throw new HiveSQLException("The fetch type " + orientation.toString() + " is not supported for this resultset", "HY106");
        }
    }
    
    protected HiveSQLException toSQLException(final String prefix, final CommandProcessorResponse response) {
        final HiveSQLException ex = new HiveSQLException(prefix + ": " + response.getErrorMessage(), response.getSQLState(), response.getResponseCode());
        if (response.getException() != null) {
            ex.initCause(response.getException());
        }
        return ex;
    }
    
    static {
        LOG = LogFactory.getLog(Operation.class.getName());
        DEFAULT_FETCH_ORIENTATION = FetchOrientation.FETCH_NEXT;
        DEFAULT_FETCH_ORIENTATION_SET = EnumSet.of(FetchOrientation.FETCH_NEXT, FetchOrientation.FETCH_FIRST);
    }
}
