// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.session;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import org.apache.hadoop.hive.common.cli.HiveFileProcessor;
import org.apache.commons.logging.LogFactory;
import org.apache.hive.service.auth.HiveAuthFactory;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.FetchType;
import org.apache.hive.service.cli.FetchOrientation;
import org.apache.hive.service.cli.TableSchema;
import org.apache.hive.service.cli.operation.Operation;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hive.ql.history.HiveHistory;
import org.apache.hive.service.cli.operation.GetFunctionsOperation;
import org.apache.hive.service.cli.operation.GetColumnsOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hive.service.cli.operation.GetTableTypesOperation;
import org.apache.hive.service.cli.operation.MetadataOperation;
import java.util.List;
import org.apache.hive.service.cli.operation.GetSchemasOperation;
import org.apache.hive.service.cli.operation.GetCatalogsOperation;
import org.apache.hive.service.cli.operation.GetTypeInfoOperation;
import org.apache.hive.service.cli.operation.ExecuteStatementOperation;
import org.apache.hive.common.util.HiveVersionInfo;
import org.apache.hive.service.cli.GetInfoValue;
import org.apache.hive.service.cli.GetInfoType;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hive.service.server.ThreadWithGarbageCleanup;
import java.util.Iterator;
import org.apache.hadoop.hive.ql.processors.SetProcessor;
import org.apache.hadoop.hive.common.cli.IHiveFileProcessor;
import org.apache.hive.service.cli.HiveSQLException;
import java.util.Map;
import org.apache.hadoop.hive.ql.exec.FetchFormatter;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.shims.ShimLoader;
import java.util.HashSet;
import org.apache.hive.service.cli.thrift.TProtocolVersion;
import java.io.File;
import org.apache.hive.service.cli.OperationHandle;
import java.util.Set;
import org.apache.hive.service.cli.operation.OperationManager;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.cli.SessionHandle;

public class HiveSessionImpl implements HiveSession
{
    private final SessionHandle sessionHandle;
    private String username;
    private final String password;
    private HiveConf hiveConf;
    private SessionState sessionState;
    private String ipAddress;
    private static final String FETCH_WORK_SERDE_CLASS = "org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe";
    private static final Log LOG;
    private SessionManager sessionManager;
    private OperationManager operationManager;
    private final Set<OperationHandle> opHandleSet;
    private boolean isOperationLogEnabled;
    private File sessionLogDir;
    private volatile long lastAccessTime;
    private volatile long lastIdleTime;
    
    public HiveSessionImpl(final TProtocolVersion protocol, final String username, final String password, final HiveConf serverhiveConf, final String ipAddress) {
        this.opHandleSet = new HashSet<OperationHandle>();
        this.username = username;
        this.password = password;
        this.sessionHandle = new SessionHandle(protocol);
        this.hiveConf = new HiveConf(serverhiveConf);
        this.ipAddress = ipAddress;
        try {
            if (!this.hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_ENABLE_DOAS) && this.hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_MAP_FAIR_SCHEDULER_QUEUE)) {
                ShimLoader.getHadoopShims().refreshDefaultQueue(this.hiveConf, username);
            }
        }
        catch (IOException e) {
            HiveSessionImpl.LOG.warn("Error setting scheduler queue: " + e, e);
        }
        this.hiveConf.set(HiveConf.ConfVars.HIVESESSIONID.varname, this.sessionHandle.getHandleIdentifier().toString());
        this.hiveConf.set("output.formatter", FetchFormatter.ThriftFormatter.class.getName());
        this.hiveConf.setInt("output.protocol", protocol.getValue());
    }
    
    @Override
    public void open(final Map<String, String> sessionConfMap) throws HiveSQLException {
        (this.sessionState = new SessionState(this.hiveConf, this.username)).setUserIpAddress(this.ipAddress);
        this.sessionState.setIsHiveServerQuery(true);
        SessionState.start(this.sessionState);
        try {
            this.sessionState.reloadAuxJars();
        }
        catch (IOException e) {
            final String msg = "Failed to load reloadable jar file path: " + e;
            HiveSessionImpl.LOG.error(msg, e);
            throw new HiveSQLException(msg, e);
        }
        this.processGlobalInitFile();
        if (sessionConfMap != null) {
            this.configureSession(sessionConfMap);
        }
        this.lastAccessTime = System.currentTimeMillis();
        this.lastIdleTime = this.lastAccessTime;
    }
    
    private void processGlobalInitFile() {
        final IHiveFileProcessor processor = new GlobalHivercFileProcessor();
        try {
            final String hiverc = this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_GLOBAL_INIT_FILE_LOCATION);
            if (hiverc != null) {
                File hivercFile = new File(hiverc);
                if (hivercFile.isDirectory()) {
                    hivercFile = new File(hivercFile, ".hiverc");
                }
                if (hivercFile.isFile()) {
                    HiveSessionImpl.LOG.info("Running global init file: " + hivercFile);
                    final int rc = processor.processFile(hivercFile.getAbsolutePath());
                    if (rc != 0) {
                        HiveSessionImpl.LOG.error("Failed on initializing global .hiverc file");
                    }
                }
                else {
                    HiveSessionImpl.LOG.debug("Global init file " + hivercFile + " does not exist");
                }
            }
        }
        catch (IOException e) {
            HiveSessionImpl.LOG.warn("Failed on initializing global .hiverc file", e);
        }
    }
    
    private void configureSession(final Map<String, String> sessionConfMap) throws HiveSQLException {
        SessionState.setCurrentSessionState(this.sessionState);
        for (final Map.Entry<String, String> entry : sessionConfMap.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith("set:")) {
                try {
                    SetProcessor.setVariable(key.substring(4), (String)entry.getValue());
                    continue;
                }
                catch (Exception e) {
                    throw new HiveSQLException(e);
                }
            }
            if (key.startsWith("use:")) {
                SessionState.get().setCurrentDatabase((String)entry.getValue());
            }
            else {
                this.hiveConf.verifyAndSet(key, entry.getValue());
            }
        }
    }
    
    @Override
    public void setOperationLogSessionDir(final File operationLogRootDir) {
        this.sessionLogDir = new File(operationLogRootDir, this.sessionHandle.getHandleIdentifier().toString());
        this.isOperationLogEnabled = true;
        if (!this.sessionLogDir.exists() && !this.sessionLogDir.mkdir()) {
            HiveSessionImpl.LOG.warn("Unable to create operation log session directory: " + this.sessionLogDir.getAbsolutePath());
            this.isOperationLogEnabled = false;
        }
        if (this.isOperationLogEnabled) {
            HiveSessionImpl.LOG.info("Operation log session directory is created: " + this.sessionLogDir.getAbsolutePath());
        }
    }
    
    @Override
    public boolean isOperationLogEnabled() {
        return this.isOperationLogEnabled;
    }
    
    @Override
    public File getOperationLogSessionDir() {
        return this.sessionLogDir;
    }
    
    @Override
    public TProtocolVersion getProtocolVersion() {
        return this.sessionHandle.getProtocolVersion();
    }
    
    @Override
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }
    
    @Override
    public void setSessionManager(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    
    private OperationManager getOperationManager() {
        return this.operationManager;
    }
    
    @Override
    public void setOperationManager(final OperationManager operationManager) {
        this.operationManager = operationManager;
    }
    
    protected synchronized void acquire(final boolean userAccess) {
        SessionState.setCurrentSessionState(this.sessionState);
        if (userAccess) {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }
    
    protected synchronized void release(final boolean userAccess) {
        SessionState.detachSession();
        if (ThreadWithGarbageCleanup.currentThread() instanceof ThreadWithGarbageCleanup) {
            final ThreadWithGarbageCleanup currentThread = (ThreadWithGarbageCleanup)Thread.currentThread();
            currentThread.cacheThreadLocalRawStore();
        }
        if (userAccess) {
            this.lastAccessTime = System.currentTimeMillis();
        }
        if (this.opHandleSet.isEmpty()) {
            this.lastIdleTime = System.currentTimeMillis();
        }
        else {
            this.lastIdleTime = 0L;
        }
    }
    
    @Override
    public SessionHandle getSessionHandle() {
        return this.sessionHandle;
    }
    
    @Override
    public String getUsername() {
        return this.username;
    }
    
    @Override
    public String getPassword() {
        return this.password;
    }
    
    @Override
    public HiveConf getHiveConf() {
        this.hiveConf.setVar(HiveConf.ConfVars.HIVEFETCHOUTPUTSERDE, "org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe");
        return this.hiveConf;
    }
    
    @Override
    public IMetaStoreClient getMetaStoreClient() throws HiveSQLException {
        try {
            return Hive.get(this.getHiveConf()).getMSC();
        }
        catch (HiveException e) {
            throw new HiveSQLException("Failed to get metastore connection", (Throwable)e);
        }
        catch (MetaException e2) {
            throw new HiveSQLException("Failed to get metastore connection", e2);
        }
    }
    
    @Override
    public GetInfoValue getInfo(final GetInfoType getInfoType) throws HiveSQLException {
        this.acquire(true);
        try {
            switch (getInfoType) {
                case CLI_SERVER_NAME: {
                    return new GetInfoValue("Hive");
                }
                case CLI_DBMS_NAME: {
                    return new GetInfoValue("Apache Hive");
                }
                case CLI_DBMS_VER: {
                    return new GetInfoValue(HiveVersionInfo.getVersion());
                }
                case CLI_MAX_COLUMN_NAME_LEN: {
                    return new GetInfoValue(128);
                }
                case CLI_MAX_SCHEMA_NAME_LEN: {
                    return new GetInfoValue(128);
                }
                case CLI_MAX_TABLE_NAME_LEN: {
                    return new GetInfoValue(128);
                }
                default: {
                    throw new HiveSQLException("Unrecognized GetInfoType value: " + getInfoType.toString());
                }
            }
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public OperationHandle executeStatement(final String statement, final Map<String, String> confOverlay) throws HiveSQLException {
        return this.executeStatementInternal(statement, confOverlay, false);
    }
    
    @Override
    public OperationHandle executeStatementAsync(final String statement, final Map<String, String> confOverlay) throws HiveSQLException {
        return this.executeStatementInternal(statement, confOverlay, true);
    }
    
    private OperationHandle executeStatementInternal(final String statement, final Map<String, String> confOverlay, final boolean runAsync) throws HiveSQLException {
        this.acquire(true);
        final OperationManager operationManager = this.getOperationManager();
        final ExecuteStatementOperation operation = operationManager.newExecuteStatementOperation(this.getSession(), statement, confOverlay, runAsync);
        final OperationHandle opHandle = operation.getHandle();
        try {
            operation.run();
            this.opHandleSet.add(opHandle);
            return opHandle;
        }
        catch (HiveSQLException e) {
            operationManager.closeOperation(opHandle);
            throw e;
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public OperationHandle getTypeInfo() throws HiveSQLException {
        this.acquire(true);
        final OperationManager operationManager = this.getOperationManager();
        final GetTypeInfoOperation operation = operationManager.newGetTypeInfoOperation(this.getSession());
        final OperationHandle opHandle = operation.getHandle();
        try {
            operation.run();
            this.opHandleSet.add(opHandle);
            return opHandle;
        }
        catch (HiveSQLException e) {
            operationManager.closeOperation(opHandle);
            throw e;
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public OperationHandle getCatalogs() throws HiveSQLException {
        this.acquire(true);
        final OperationManager operationManager = this.getOperationManager();
        final GetCatalogsOperation operation = operationManager.newGetCatalogsOperation(this.getSession());
        final OperationHandle opHandle = operation.getHandle();
        try {
            operation.run();
            this.opHandleSet.add(opHandle);
            return opHandle;
        }
        catch (HiveSQLException e) {
            operationManager.closeOperation(opHandle);
            throw e;
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public OperationHandle getSchemas(final String catalogName, final String schemaName) throws HiveSQLException {
        this.acquire(true);
        final OperationManager operationManager = this.getOperationManager();
        final GetSchemasOperation operation = operationManager.newGetSchemasOperation(this.getSession(), catalogName, schemaName);
        final OperationHandle opHandle = operation.getHandle();
        try {
            operation.run();
            this.opHandleSet.add(opHandle);
            return opHandle;
        }
        catch (HiveSQLException e) {
            operationManager.closeOperation(opHandle);
            throw e;
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public OperationHandle getTables(final String catalogName, final String schemaName, final String tableName, final List<String> tableTypes) throws HiveSQLException {
        this.acquire(true);
        final OperationManager operationManager = this.getOperationManager();
        final MetadataOperation operation = operationManager.newGetTablesOperation(this.getSession(), catalogName, schemaName, tableName, tableTypes);
        final OperationHandle opHandle = operation.getHandle();
        try {
            operation.run();
            this.opHandleSet.add(opHandle);
            return opHandle;
        }
        catch (HiveSQLException e) {
            operationManager.closeOperation(opHandle);
            throw e;
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public OperationHandle getTableTypes() throws HiveSQLException {
        this.acquire(true);
        final OperationManager operationManager = this.getOperationManager();
        final GetTableTypesOperation operation = operationManager.newGetTableTypesOperation(this.getSession());
        final OperationHandle opHandle = operation.getHandle();
        try {
            operation.run();
            this.opHandleSet.add(opHandle);
            return opHandle;
        }
        catch (HiveSQLException e) {
            operationManager.closeOperation(opHandle);
            throw e;
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public OperationHandle getColumns(final String catalogName, final String schemaName, final String tableName, final String columnName) throws HiveSQLException {
        this.acquire(true);
        final String addedJars = Utilities.getResourceFiles((Configuration)this.hiveConf, SessionState.ResourceType.JAR);
        if (StringUtils.isNotBlank(addedJars)) {
            final IMetaStoreClient metastoreClient = this.getSession().getMetaStoreClient();
            metastoreClient.setHiveAddedJars(addedJars);
        }
        final OperationManager operationManager = this.getOperationManager();
        final GetColumnsOperation operation = operationManager.newGetColumnsOperation(this.getSession(), catalogName, schemaName, tableName, columnName);
        final OperationHandle opHandle = operation.getHandle();
        try {
            operation.run();
            this.opHandleSet.add(opHandle);
            return opHandle;
        }
        catch (HiveSQLException e) {
            operationManager.closeOperation(opHandle);
            throw e;
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public OperationHandle getFunctions(final String catalogName, final String schemaName, final String functionName) throws HiveSQLException {
        this.acquire(true);
        final OperationManager operationManager = this.getOperationManager();
        final GetFunctionsOperation operation = operationManager.newGetFunctionsOperation(this.getSession(), catalogName, schemaName, functionName);
        final OperationHandle opHandle = operation.getHandle();
        try {
            operation.run();
            this.opHandleSet.add(opHandle);
            return opHandle;
        }
        catch (HiveSQLException e) {
            operationManager.closeOperation(opHandle);
            throw e;
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public void close() throws HiveSQLException {
        try {
            this.acquire(true);
            for (final OperationHandle opHandle : this.opHandleSet) {
                this.operationManager.closeOperation(opHandle);
            }
            this.opHandleSet.clear();
            this.cleanupSessionLogDir();
            final HiveHistory hiveHist = this.sessionState.getHiveHistory();
            if (null != hiveHist) {
                hiveHist.closeStream();
            }
            try {
                this.sessionState.close();
            }
            finally {
                this.sessionState = null;
            }
        }
        catch (IOException ioe) {
            throw new HiveSQLException("Failure to close", ioe);
        }
        finally {
            if (this.sessionState != null) {
                try {
                    this.sessionState.close();
                }
                catch (Throwable t) {
                    HiveSessionImpl.LOG.warn("Error closing session", t);
                }
                this.sessionState = null;
            }
            this.release(true);
        }
    }
    
    private void cleanupSessionLogDir() {
        if (this.isOperationLogEnabled) {
            try {
                FileUtils.forceDelete(this.sessionLogDir);
            }
            catch (Exception e) {
                HiveSessionImpl.LOG.error("Failed to cleanup session log dir: " + this.sessionHandle, e);
            }
        }
    }
    
    @Override
    public SessionState getSessionState() {
        return this.sessionState;
    }
    
    @Override
    public String getUserName() {
        return this.username;
    }
    
    @Override
    public void setUserName(final String userName) {
        this.username = userName;
    }
    
    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }
    
    @Override
    public void closeExpiredOperations() {
        final OperationHandle[] handles = this.opHandleSet.toArray(new OperationHandle[this.opHandleSet.size()]);
        if (handles.length > 0) {
            final List<Operation> operations = this.operationManager.removeExpiredOperations(handles);
            if (!operations.isEmpty()) {
                this.closeTimedOutOperations(operations);
            }
        }
    }
    
    @Override
    public long getNoOperationTime() {
        return (this.lastIdleTime > 0L) ? (System.currentTimeMillis() - this.lastIdleTime) : 0L;
    }
    
    private void closeTimedOutOperations(final List<Operation> operations) {
        this.acquire(false);
        try {
            for (final Operation operation : operations) {
                this.opHandleSet.remove(operation.getHandle());
                try {
                    operation.close();
                }
                catch (Exception e) {
                    HiveSessionImpl.LOG.warn("Exception is thrown closing timed-out operation " + operation.getHandle(), e);
                }
            }
        }
        finally {
            this.release(false);
        }
    }
    
    @Override
    public void cancelOperation(final OperationHandle opHandle) throws HiveSQLException {
        this.acquire(true);
        try {
            this.sessionManager.getOperationManager().cancelOperation(opHandle);
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public void closeOperation(final OperationHandle opHandle) throws HiveSQLException {
        this.acquire(true);
        try {
            this.operationManager.closeOperation(opHandle);
            this.opHandleSet.remove(opHandle);
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public TableSchema getResultSetMetadata(final OperationHandle opHandle) throws HiveSQLException {
        this.acquire(true);
        try {
            return this.sessionManager.getOperationManager().getOperationResultSetSchema(opHandle);
        }
        finally {
            this.release(true);
        }
    }
    
    @Override
    public RowSet fetchResults(final OperationHandle opHandle, final FetchOrientation orientation, final long maxRows, final FetchType fetchType) throws HiveSQLException {
        this.acquire(true);
        try {
            if (fetchType == FetchType.QUERY_OUTPUT) {
                return this.operationManager.getOperationNextRowSet(opHandle, orientation, maxRows);
            }
            return this.operationManager.getOperationLogRowSet(opHandle, orientation, maxRows);
        }
        finally {
            this.release(true);
        }
    }
    
    protected HiveSession getSession() {
        return this;
    }
    
    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }
    
    @Override
    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    @Override
    public String getDelegationToken(final HiveAuthFactory authFactory, final String owner, final String renewer) throws HiveSQLException {
        HiveAuthFactory.verifyProxyAccess(this.getUsername(), owner, this.getIpAddress(), this.getHiveConf());
        return authFactory.getDelegationToken(owner, renewer);
    }
    
    @Override
    public void cancelDelegationToken(final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        HiveAuthFactory.verifyProxyAccess(this.getUsername(), this.getUserFromToken(authFactory, tokenStr), this.getIpAddress(), this.getHiveConf());
        authFactory.cancelDelegationToken(tokenStr);
    }
    
    @Override
    public void renewDelegationToken(final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        HiveAuthFactory.verifyProxyAccess(this.getUsername(), this.getUserFromToken(authFactory, tokenStr), this.getIpAddress(), this.getHiveConf());
        authFactory.renewDelegationToken(tokenStr);
    }
    
    private String getUserFromToken(final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        return authFactory.getUserFromToken(tokenStr);
    }
    
    static {
        LOG = LogFactory.getLog(HiveSessionImpl.class);
    }
    
    private class GlobalHivercFileProcessor extends HiveFileProcessor
    {
        @Override
        protected BufferedReader loadFile(final String fileName) throws IOException {
            FileInputStream initStream = null;
            BufferedReader bufferedReader = null;
            initStream = new FileInputStream(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(initStream));
            return bufferedReader;
        }
        
        @Override
        protected int processCmd(final String cmd) {
            int rc = 0;
            final String cmd_trimed = cmd.trim();
            try {
                HiveSessionImpl.this.executeStatementInternal(cmd_trimed, null, false);
            }
            catch (HiveSQLException e) {
                rc = -1;
                HiveSessionImpl.LOG.warn("Failed to execute HQL command in global .hiverc file.", e);
            }
            return rc;
        }
    }
}
