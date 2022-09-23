// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import java.util.ArrayList;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Schema;
import java.util.Iterator;
import org.apache.hive.service.cli.RowSetFactory;
import java.sql.SQLException;
import org.apache.hive.service.cli.FetchOrientation;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.TableSchema;
import org.apache.hive.service.cli.OperationState;
import org.apache.hive.service.cli.OperationStatus;
import java.util.List;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.session.HiveSession;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.hadoop.hive.ql.session.OperationLog;
import org.apache.hadoop.hive.conf.HiveConf;
import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import org.apache.hive.service.cli.OperationHandle;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.hive.service.AbstractService;

public class OperationManager extends AbstractService
{
    private final Log LOG;
    private final Map<OperationHandle, Operation> handleToOperation;
    
    public OperationManager() {
        super(OperationManager.class.getSimpleName());
        this.LOG = LogFactory.getLog(OperationManager.class.getName());
        this.handleToOperation = new HashMap<OperationHandle, Operation>();
    }
    
    @Override
    public synchronized void init(final HiveConf hiveConf) {
        if (hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_LOGGING_OPERATION_ENABLED)) {
            this.initOperationLogCapture(hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_LOGGING_OPERATION_LEVEL));
        }
        else {
            this.LOG.debug("Operation level logging is turned off");
        }
        super.init(hiveConf);
    }
    
    @Override
    public synchronized void start() {
        super.start();
    }
    
    @Override
    public synchronized void stop() {
        super.stop();
    }
    
    private void initOperationLogCapture(final String loggingMode) {
        final Appender ap = new LogDivertAppender(this, OperationLog.getLoggingLevel(loggingMode));
        Logger.getRootLogger().addAppender(ap);
    }
    
    public ExecuteStatementOperation newExecuteStatementOperation(final HiveSession parentSession, final String statement, final Map<String, String> confOverlay, final boolean runAsync) throws HiveSQLException {
        final ExecuteStatementOperation executeStatementOperation = ExecuteStatementOperation.newExecuteStatementOperation(parentSession, statement, confOverlay, runAsync);
        this.addOperation(executeStatementOperation);
        return executeStatementOperation;
    }
    
    public GetTypeInfoOperation newGetTypeInfoOperation(final HiveSession parentSession) {
        final GetTypeInfoOperation operation = new GetTypeInfoOperation(parentSession);
        this.addOperation(operation);
        return operation;
    }
    
    public GetCatalogsOperation newGetCatalogsOperation(final HiveSession parentSession) {
        final GetCatalogsOperation operation = new GetCatalogsOperation(parentSession);
        this.addOperation(operation);
        return operation;
    }
    
    public GetSchemasOperation newGetSchemasOperation(final HiveSession parentSession, final String catalogName, final String schemaName) {
        final GetSchemasOperation operation = new GetSchemasOperation(parentSession, catalogName, schemaName);
        this.addOperation(operation);
        return operation;
    }
    
    public MetadataOperation newGetTablesOperation(final HiveSession parentSession, final String catalogName, final String schemaName, final String tableName, final List<String> tableTypes) {
        final MetadataOperation operation = new GetTablesOperation(parentSession, catalogName, schemaName, tableName, tableTypes);
        this.addOperation(operation);
        return operation;
    }
    
    public GetTableTypesOperation newGetTableTypesOperation(final HiveSession parentSession) {
        final GetTableTypesOperation operation = new GetTableTypesOperation(parentSession);
        this.addOperation(operation);
        return operation;
    }
    
    public GetColumnsOperation newGetColumnsOperation(final HiveSession parentSession, final String catalogName, final String schemaName, final String tableName, final String columnName) {
        final GetColumnsOperation operation = new GetColumnsOperation(parentSession, catalogName, schemaName, tableName, columnName);
        this.addOperation(operation);
        return operation;
    }
    
    public GetFunctionsOperation newGetFunctionsOperation(final HiveSession parentSession, final String catalogName, final String schemaName, final String functionName) {
        final GetFunctionsOperation operation = new GetFunctionsOperation(parentSession, catalogName, schemaName, functionName);
        this.addOperation(operation);
        return operation;
    }
    
    public Operation getOperation(final OperationHandle operationHandle) throws HiveSQLException {
        final Operation operation = this.getOperationInternal(operationHandle);
        if (operation == null) {
            throw new HiveSQLException("Invalid OperationHandle: " + operationHandle);
        }
        return operation;
    }
    
    private synchronized Operation getOperationInternal(final OperationHandle operationHandle) {
        return this.handleToOperation.get(operationHandle);
    }
    
    private synchronized Operation removeTimedOutOperation(final OperationHandle operationHandle) {
        final Operation operation = this.handleToOperation.get(operationHandle);
        if (operation != null && operation.isTimedOut(System.currentTimeMillis())) {
            this.handleToOperation.remove(operationHandle);
            return operation;
        }
        return null;
    }
    
    private synchronized void addOperation(final Operation operation) {
        this.handleToOperation.put(operation.getHandle(), operation);
    }
    
    private synchronized Operation removeOperation(final OperationHandle opHandle) {
        return this.handleToOperation.remove(opHandle);
    }
    
    public OperationStatus getOperationStatus(final OperationHandle opHandle) throws HiveSQLException {
        return this.getOperation(opHandle).getStatus();
    }
    
    public void cancelOperation(final OperationHandle opHandle) throws HiveSQLException {
        final Operation operation = this.getOperation(opHandle);
        final OperationState opState = operation.getStatus().getState();
        if (opState == OperationState.CANCELED || opState == OperationState.CLOSED || opState == OperationState.FINISHED || opState == OperationState.ERROR || opState == OperationState.UNKNOWN) {
            this.LOG.debug(opHandle + ": Operation is already aborted in state - " + opState);
        }
        else {
            this.LOG.debug(opHandle + ": Attempting to cancel from state - " + opState);
            operation.cancel();
        }
    }
    
    public void closeOperation(final OperationHandle opHandle) throws HiveSQLException {
        final Operation operation = this.removeOperation(opHandle);
        if (operation == null) {
            throw new HiveSQLException("Operation does not exist!");
        }
        operation.close();
    }
    
    public TableSchema getOperationResultSetSchema(final OperationHandle opHandle) throws HiveSQLException {
        return this.getOperation(opHandle).getResultSetSchema();
    }
    
    public RowSet getOperationNextRowSet(final OperationHandle opHandle) throws HiveSQLException {
        return this.getOperation(opHandle).getNextRowSet();
    }
    
    public RowSet getOperationNextRowSet(final OperationHandle opHandle, final FetchOrientation orientation, final long maxRows) throws HiveSQLException {
        return this.getOperation(opHandle).getNextRowSet(orientation, maxRows);
    }
    
    public RowSet getOperationLogRowSet(final OperationHandle opHandle, final FetchOrientation orientation, final long maxRows) throws HiveSQLException {
        final OperationLog operationLog = this.getOperation(opHandle).getOperationLog();
        if (operationLog == null) {
            throw new HiveSQLException("Couldn't find log associated with operation handle: " + opHandle);
        }
        List<String> logs;
        try {
            logs = (List<String>)operationLog.readOperationLog(this.isFetchFirst(orientation), maxRows);
        }
        catch (SQLException e) {
            throw new HiveSQLException(e.getMessage(), e.getCause());
        }
        final TableSchema tableSchema = new TableSchema(this.getLogSchema());
        final RowSet rowSet = RowSetFactory.create(tableSchema, this.getOperation(opHandle).getProtocolVersion());
        for (final String log : logs) {
            rowSet.addRow(new String[] { log });
        }
        return rowSet;
    }
    
    private boolean isFetchFirst(final FetchOrientation fetchOrientation) {
        return fetchOrientation.equals(FetchOrientation.FETCH_FIRST);
    }
    
    private Schema getLogSchema() {
        final Schema schema = new Schema();
        final FieldSchema fieldSchema = new FieldSchema();
        fieldSchema.setName("operation_log");
        fieldSchema.setType("string");
        schema.addToFieldSchemas(fieldSchema);
        return schema;
    }
    
    public OperationLog getOperationLogByThread() {
        return OperationLog.getCurrentOperationLog();
    }
    
    public List<Operation> removeExpiredOperations(final OperationHandle[] handles) {
        final List<Operation> removed = new ArrayList<Operation>();
        for (final OperationHandle handle : handles) {
            final Operation operation = this.removeTimedOutOperation(handle);
            if (operation != null) {
                this.LOG.warn("Operation " + handle + " is timed-out and will be closed");
                removed.add(operation);
            }
        }
        return removed;
    }
}
