// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.Deserializer;
import java.util.Properties;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import java.sql.SQLException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.SerDeUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import java.io.UnsupportedEncodingException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import java.io.IOException;
import org.apache.hive.service.cli.RowSetFactory;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.FetchOrientation;
import org.apache.hadoop.hive.ql.session.OperationLog;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.shims.Utils;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import org.apache.hive.service.server.ThreadWithGarbageCleanup;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.session.SessionState;
import java.io.Serializable;
import java.util.Iterator;
import org.apache.hadoop.hive.ql.exec.ExplainTask;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hadoop.hive.ql.parse.VariableSubstitution;
import org.apache.commons.codec.binary.Base64;
import org.apache.hive.service.cli.OperationState;
import org.apache.hadoop.hive.conf.HiveConf;
import java.util.ArrayList;
import java.util.Map;
import org.apache.hive.service.cli.session.HiveSession;
import java.util.List;
import org.apache.hadoop.hive.serde2.SerDe;
import org.apache.hadoop.hive.metastore.api.Schema;
import org.apache.hive.service.cli.TableSchema;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hadoop.hive.ql.Driver;

public class SQLOperation extends ExecuteStatementOperation
{
    private Driver driver;
    private CommandProcessorResponse response;
    private TableSchema resultSchema;
    private Schema mResultSchema;
    private SerDe serde;
    private boolean fetchStarted;
    private final transient List<Object> convey;
    
    public SQLOperation(final HiveSession parentSession, final String statement, final Map<String, String> confOverlay, final boolean runInBackground) {
        super(parentSession, statement, confOverlay, runInBackground);
        this.driver = null;
        this.resultSchema = null;
        this.mResultSchema = null;
        this.serde = null;
        this.fetchStarted = false;
        this.convey = new ArrayList<Object>();
    }
    
    public void prepare(final HiveConf sqlOperationConf) throws HiveSQLException {
        this.setState(OperationState.RUNNING);
        try {
            this.driver = new Driver(sqlOperationConf, this.getParentSession().getUserName());
            final String guid64 = Base64.encodeBase64URLSafeString(this.getHandle().getHandleIdentifier().toTHandleIdentifier().getGuid()).trim();
            this.driver.setOperationId(guid64);
            this.driver.setTryCount(Integer.MAX_VALUE);
            final String subStatement = new VariableSubstitution().substitute(sqlOperationConf, this.statement);
            this.response = this.driver.compileAndRespond(subStatement);
            if (0 != this.response.getResponseCode()) {
                throw this.toSQLException("Error while compiling statement", this.response);
            }
            this.mResultSchema = this.driver.getSchema();
            if (this.driver.getPlan().getFetchTask() != null) {
                if (this.mResultSchema == null || !this.mResultSchema.isSetFieldSchemas()) {
                    throw new HiveSQLException("Error compiling query: Schema and FieldSchema should be set when query plan has a FetchTask");
                }
                this.resultSchema = new TableSchema(this.mResultSchema);
                this.setHasResultSet(true);
            }
            else {
                this.setHasResultSet(false);
            }
            for (final Task<? extends Serializable> task : this.driver.getPlan().getRootTasks()) {
                if (task.getClass() == ExplainTask.class) {
                    this.resultSchema = new TableSchema(this.mResultSchema);
                    this.setHasResultSet(true);
                    break;
                }
            }
        }
        catch (HiveSQLException e) {
            this.setState(OperationState.ERROR);
            throw e;
        }
        catch (Exception e2) {
            this.setState(OperationState.ERROR);
            throw new HiveSQLException("Error running query: " + e2.toString(), e2);
        }
    }
    
    private void runQuery(final HiveConf sqlOperationConf) throws HiveSQLException {
        try {
            this.driver.setTryCount(Integer.MAX_VALUE);
            this.response = this.driver.run();
            if (0 != this.response.getResponseCode()) {
                throw this.toSQLException("Error while processing statement", this.response);
            }
        }
        catch (HiveSQLException e) {
            if (this.getStatus().getState() == OperationState.CANCELED) {
                return;
            }
            this.setState(OperationState.ERROR);
            throw e;
        }
        catch (Exception e2) {
            this.setState(OperationState.ERROR);
            throw new HiveSQLException("Error running query: " + e2.toString(), e2);
        }
        this.setState(OperationState.FINISHED);
    }
    
    public void runInternal() throws HiveSQLException {
        this.setState(OperationState.PENDING);
        final HiveConf opConfig = this.getConfigForOperation();
        this.prepare(opConfig);
        if (!this.shouldRunAsync()) {
            this.runQuery(opConfig);
        }
        else {
            final SessionState parentSessionState = SessionState.get();
            final Hive parentHive = this.getSessionHive();
            final UserGroupInformation currentUGI = this.getCurrentUGI(opConfig);
            final Runnable backgroundOperation = new Runnable() {
                @Override
                public void run() {
                    final PrivilegedExceptionAction<Object> doAsAction = new PrivilegedExceptionAction<Object>() {
                        @Override
                        public Object run() throws HiveSQLException {
                            Hive.set(parentHive);
                            SessionState.setCurrentSessionState(parentSessionState);
                            SQLOperation.this.registerCurrentOperationLog();
                            try {
                                SQLOperation.this.runQuery(opConfig);
                            }
                            catch (HiveSQLException e) {
                                SQLOperation.this.setOperationException(e);
                                Operation.LOG.error("Error running hive query: ", e);
                            }
                            finally {
                                SQLOperation.this.unregisterOperationLog();
                            }
                            return null;
                        }
                    };
                    try {
                        currentUGI.doAs(doAsAction);
                    }
                    catch (Exception e) {
                        SQLOperation.this.setOperationException(new HiveSQLException(e));
                        Operation.LOG.error("Error running hive query as user : " + currentUGI.getShortUserName(), e);
                    }
                    finally {
                        if (ThreadWithGarbageCleanup.currentThread() instanceof ThreadWithGarbageCleanup) {
                            final ThreadWithGarbageCleanup currentThread = (ThreadWithGarbageCleanup)Thread.currentThread();
                            currentThread.cacheThreadLocalRawStore();
                        }
                    }
                }
            };
            try {
                final Future<?> backgroundHandle = this.getParentSession().getSessionManager().submitBackgroundOperation(backgroundOperation);
                this.setBackgroundHandle(backgroundHandle);
            }
            catch (RejectedExecutionException rejected) {
                this.setState(OperationState.ERROR);
                throw new HiveSQLException("The background threadpool cannot accept new task for execution, please retry the operation", rejected);
            }
        }
    }
    
    private UserGroupInformation getCurrentUGI(final HiveConf opConfig) throws HiveSQLException {
        try {
            return Utils.getUGI();
        }
        catch (Exception e) {
            throw new HiveSQLException("Unable to get current user", e);
        }
    }
    
    private Hive getSessionHive() throws HiveSQLException {
        try {
            return Hive.get();
        }
        catch (HiveException e) {
            throw new HiveSQLException("Failed to get ThreadLocal Hive object", (Throwable)e);
        }
    }
    
    private void registerCurrentOperationLog() {
        if (this.isOperationLogEnabled) {
            if (this.operationLog == null) {
                SQLOperation.LOG.warn("Failed to get current OperationLog object of Operation: " + this.getHandle().getHandleIdentifier());
                this.isOperationLogEnabled = false;
                return;
            }
            OperationLog.setCurrentOperationLog(this.operationLog);
        }
    }
    
    private void cleanup(final OperationState state) throws HiveSQLException {
        this.setState(state);
        if (this.shouldRunAsync()) {
            final Future<?> backgroundHandle = this.getBackgroundHandle();
            if (backgroundHandle != null) {
                backgroundHandle.cancel(true);
            }
        }
        if (this.driver != null) {
            this.driver.close();
            this.driver.destroy();
        }
        this.driver = null;
        final SessionState ss = SessionState.get();
        if (ss.getTmpOutputFile() != null) {
            ss.getTmpOutputFile().delete();
        }
    }
    
    @Override
    public void cancel() throws HiveSQLException {
        this.cleanup(OperationState.CANCELED);
    }
    
    @Override
    public void close() throws HiveSQLException {
        this.cleanup(OperationState.CLOSED);
        this.cleanupOperationLog();
    }
    
    @Override
    public TableSchema getResultSetSchema() throws HiveSQLException {
        this.assertState(OperationState.FINISHED);
        if (this.resultSchema == null) {
            this.resultSchema = new TableSchema(this.driver.getSchema());
        }
        return this.resultSchema;
    }
    
    @Override
    public RowSet getNextRowSet(final FetchOrientation orientation, final long maxRows) throws HiveSQLException {
        this.validateDefaultFetchOrientation(orientation);
        this.assertState(OperationState.FINISHED);
        final RowSet rowSet = RowSetFactory.create(this.resultSchema, this.getProtocolVersion());
        try {
            if (orientation.equals(FetchOrientation.FETCH_FIRST) && this.fetchStarted) {
                this.driver.resetFetch();
            }
            this.fetchStarted = true;
            this.driver.setMaxRows((int)maxRows);
            if (this.driver.getResults((List)this.convey)) {
                return this.decode(this.convey, rowSet);
            }
            return rowSet;
        }
        catch (IOException e) {
            throw new HiveSQLException(e);
        }
        catch (CommandNeedRetryException e2) {
            throw new HiveSQLException((Throwable)e2);
        }
        catch (Exception e3) {
            throw new HiveSQLException(e3);
        }
        finally {
            this.convey.clear();
        }
    }
    
    private RowSet decode(final List<Object> rows, final RowSet rowSet) throws Exception {
        if (this.driver.isFetchingTable()) {
            return this.prepareFromRow(rows, rowSet);
        }
        return this.decodeFromString(rows, rowSet);
    }
    
    private RowSet prepareFromRow(final List<Object> rows, final RowSet rowSet) throws Exception {
        for (final Object row : rows) {
            rowSet.addRow((Object[])row);
        }
        return rowSet;
    }
    
    private RowSet decodeFromString(final List<Object> rows, final RowSet rowSet) throws SQLException, SerDeException {
        this.getSerDe();
        final StructObjectInspector soi = (StructObjectInspector)this.serde.getObjectInspector();
        final List<? extends StructField> fieldRefs = soi.getAllStructFieldRefs();
        final Object[] deserializedFields = new Object[fieldRefs.size()];
        final int protocol = this.getProtocolVersion().getValue();
        for (final Object rowString : rows) {
            Object rowObj;
            try {
                rowObj = this.serde.deserialize(new BytesWritable(((String)rowString).getBytes("UTF-8")));
            }
            catch (UnsupportedEncodingException e) {
                throw new SerDeException(e);
            }
            for (int i = 0; i < fieldRefs.size(); ++i) {
                final StructField fieldRef = (StructField)fieldRefs.get(i);
                final ObjectInspector fieldOI = fieldRef.getFieldObjectInspector();
                final Object fieldData = soi.getStructFieldData(rowObj, fieldRef);
                deserializedFields[i] = SerDeUtils.toThriftPayload(fieldData, fieldOI, protocol);
            }
            rowSet.addRow(deserializedFields);
        }
        return rowSet;
    }
    
    private SerDe getSerDe() throws SQLException {
        if (this.serde != null) {
            return this.serde;
        }
        try {
            final List<FieldSchema> fieldSchemas = this.mResultSchema.getFieldSchemas();
            final StringBuilder namesSb = new StringBuilder();
            final StringBuilder typesSb = new StringBuilder();
            if (fieldSchemas != null && !fieldSchemas.isEmpty()) {
                for (int pos = 0; pos < fieldSchemas.size(); ++pos) {
                    if (pos != 0) {
                        namesSb.append(",");
                        typesSb.append(",");
                    }
                    namesSb.append(fieldSchemas.get(pos).getName());
                    typesSb.append(fieldSchemas.get(pos).getType());
                }
            }
            final String names = namesSb.toString();
            final String types = typesSb.toString();
            this.serde = new LazySimpleSerDe();
            final Properties props = new Properties();
            if (names.length() > 0) {
                SQLOperation.LOG.debug("Column names: " + names);
                props.setProperty("columns", names);
            }
            if (types.length() > 0) {
                SQLOperation.LOG.debug("Column types: " + types);
                props.setProperty("columns.types", types);
            }
            SerDeUtils.initializeSerDe(this.serde, new HiveConf(), props, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new SQLException("Could not create ResultSet: " + ex.getMessage(), ex);
        }
        return this.serde;
    }
    
    private HiveConf getConfigForOperation() throws HiveSQLException {
        HiveConf sqlOperationConf = this.getParentSession().getHiveConf();
        if (!this.getConfOverlay().isEmpty() || this.shouldRunAsync()) {
            sqlOperationConf = new HiveConf(sqlOperationConf);
            for (final Map.Entry<String, String> confEntry : this.getConfOverlay().entrySet()) {
                try {
                    sqlOperationConf.verifyAndSet(confEntry.getKey(), confEntry.getValue());
                }
                catch (IllegalArgumentException e) {
                    throw new HiveSQLException("Error applying statement specific settings", e);
                }
            }
        }
        return sqlOperationConf;
    }
}
