// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli;

import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hive.service.cli.operation.Operation;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.ql.exec.FunctionRegistry;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import org.apache.hive.service.ServiceException;
import org.apache.hadoop.hive.shims.Utils;
import org.apache.hive.service.auth.HiveAuthFactory;
import org.apache.hive.service.Service;
import org.apache.commons.logging.LogFactory;
import org.apache.hive.service.server.HiveServer2;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hive.service.cli.session.SessionManager;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;
import org.apache.hive.service.cli.thrift.TProtocolVersion;
import org.apache.hive.service.CompositeService;

public class CLIService extends CompositeService implements ICLIService
{
    public static final TProtocolVersion SERVER_VERSION;
    private final Log LOG;
    private HiveConf hiveConf;
    private SessionManager sessionManager;
    private UserGroupInformation serviceUGI;
    private UserGroupInformation httpUGI;
    private final HiveServer2 hiveServer2;
    
    public CLIService(final HiveServer2 hiveServer2) {
        super(CLIService.class.getSimpleName());
        this.LOG = LogFactory.getLog(CLIService.class.getName());
        this.hiveServer2 = hiveServer2;
    }
    
    @Override
    public synchronized void init(final HiveConf hiveConf) {
        this.hiveConf = hiveConf;
        this.addService(this.sessionManager = new SessionManager(this.hiveServer2));
        if (UserGroupInformation.isSecurityEnabled()) {
            try {
                HiveAuthFactory.loginFromKeytab(hiveConf);
                this.serviceUGI = Utils.getUGI();
            }
            catch (IOException e) {
                throw new ServiceException("Unable to login to kerberos with given principal/keytab", e);
            }
            catch (LoginException e2) {
                throw new ServiceException("Unable to login to kerberos with given principal/keytab", e2);
            }
            final String principal = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_SPNEGO_PRINCIPAL);
            final String keyTabFile = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_SPNEGO_KEYTAB);
            if (principal.isEmpty() || keyTabFile.isEmpty()) {
                this.LOG.info("SPNego httpUGI not created, spNegoPrincipal: " + principal + ", ketabFile: " + keyTabFile);
            }
            else {
                try {
                    this.httpUGI = HiveAuthFactory.loginFromSpnegoKeytabAndReturnUGI(hiveConf);
                    this.LOG.info("SPNego httpUGI successfully created.");
                }
                catch (IOException e3) {
                    this.LOG.warn("SPNego httpUGI creation failed: ", e3);
                }
            }
        }
        try {
            this.applyAuthorizationConfigPolicy(hiveConf);
        }
        catch (Exception e4) {
            throw new RuntimeException("Error applying authorization policy on hive configuration: " + e4.getMessage(), e4);
        }
        this.setupBlockedUdfs();
        super.init(hiveConf);
    }
    
    private void applyAuthorizationConfigPolicy(final HiveConf newHiveConf) throws HiveException, MetaException {
        final SessionState ss = new SessionState(newHiveConf);
        ss.setIsHiveServerQuery(true);
        SessionState.start(ss);
        ss.applyAuthorizationPolicy();
    }
    
    private void setupBlockedUdfs() {
        FunctionRegistry.setupPermissionsForBuiltinUDFs(this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_BUILTIN_UDF_WHITELIST), this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_BUILTIN_UDF_BLACKLIST));
    }
    
    public UserGroupInformation getServiceUGI() {
        return this.serviceUGI;
    }
    
    public UserGroupInformation getHttpUGI() {
        return this.httpUGI;
    }
    
    @Override
    public synchronized void start() {
        super.start();
        IMetaStoreClient metastoreClient = null;
        try {
            metastoreClient = new HiveMetaStoreClient(this.hiveConf);
            metastoreClient.getDatabases("default");
        }
        catch (Exception e) {
            throw new ServiceException("Unable to connect to MetaStore!", e);
        }
        finally {
            if (metastoreClient != null) {
                metastoreClient.close();
            }
        }
    }
    
    @Override
    public synchronized void stop() {
        super.stop();
    }
    
    @Deprecated
    public SessionHandle openSession(final TProtocolVersion protocol, final String username, final String password, final Map<String, String> configuration) throws HiveSQLException {
        final SessionHandle sessionHandle = this.sessionManager.openSession(protocol, username, password, null, configuration, false, null);
        this.LOG.debug(sessionHandle + ": openSession()");
        return sessionHandle;
    }
    
    @Deprecated
    public SessionHandle openSessionWithImpersonation(final TProtocolVersion protocol, final String username, final String password, final Map<String, String> configuration, final String delegationToken) throws HiveSQLException {
        final SessionHandle sessionHandle = this.sessionManager.openSession(protocol, username, password, null, configuration, true, delegationToken);
        this.LOG.debug(sessionHandle + ": openSessionWithImpersonation()");
        return sessionHandle;
    }
    
    public SessionHandle openSession(final TProtocolVersion protocol, final String username, final String password, final String ipAddress, final Map<String, String> configuration) throws HiveSQLException {
        final SessionHandle sessionHandle = this.sessionManager.openSession(protocol, username, password, ipAddress, configuration, false, null);
        this.LOG.debug(sessionHandle + ": openSession()");
        return sessionHandle;
    }
    
    public SessionHandle openSessionWithImpersonation(final TProtocolVersion protocol, final String username, final String password, final String ipAddress, final Map<String, String> configuration, final String delegationToken) throws HiveSQLException {
        final SessionHandle sessionHandle = this.sessionManager.openSession(protocol, username, password, ipAddress, configuration, true, delegationToken);
        this.LOG.debug(sessionHandle + ": openSession()");
        return sessionHandle;
    }
    
    @Override
    public SessionHandle openSession(final String username, final String password, final Map<String, String> configuration) throws HiveSQLException {
        final SessionHandle sessionHandle = this.sessionManager.openSession(CLIService.SERVER_VERSION, username, password, null, configuration, false, null);
        this.LOG.debug(sessionHandle + ": openSession()");
        return sessionHandle;
    }
    
    @Override
    public SessionHandle openSessionWithImpersonation(final String username, final String password, final Map<String, String> configuration, final String delegationToken) throws HiveSQLException {
        final SessionHandle sessionHandle = this.sessionManager.openSession(CLIService.SERVER_VERSION, username, password, null, configuration, true, delegationToken);
        this.LOG.debug(sessionHandle + ": openSession()");
        return sessionHandle;
    }
    
    @Override
    public void closeSession(final SessionHandle sessionHandle) throws HiveSQLException {
        this.sessionManager.closeSession(sessionHandle);
        this.LOG.debug(sessionHandle + ": closeSession()");
    }
    
    @Override
    public GetInfoValue getInfo(final SessionHandle sessionHandle, final GetInfoType getInfoType) throws HiveSQLException {
        final GetInfoValue infoValue = this.sessionManager.getSession(sessionHandle).getInfo(getInfoType);
        this.LOG.debug(sessionHandle + ": getInfo()");
        return infoValue;
    }
    
    @Override
    public OperationHandle executeStatement(final SessionHandle sessionHandle, final String statement, final Map<String, String> confOverlay) throws HiveSQLException {
        final OperationHandle opHandle = this.sessionManager.getSession(sessionHandle).executeStatement(statement, confOverlay);
        this.LOG.debug(sessionHandle + ": executeStatement()");
        return opHandle;
    }
    
    @Override
    public OperationHandle executeStatementAsync(final SessionHandle sessionHandle, final String statement, final Map<String, String> confOverlay) throws HiveSQLException {
        final OperationHandle opHandle = this.sessionManager.getSession(sessionHandle).executeStatementAsync(statement, confOverlay);
        this.LOG.debug(sessionHandle + ": executeStatementAsync()");
        return opHandle;
    }
    
    @Override
    public OperationHandle getTypeInfo(final SessionHandle sessionHandle) throws HiveSQLException {
        final OperationHandle opHandle = this.sessionManager.getSession(sessionHandle).getTypeInfo();
        this.LOG.debug(sessionHandle + ": getTypeInfo()");
        return opHandle;
    }
    
    @Override
    public OperationHandle getCatalogs(final SessionHandle sessionHandle) throws HiveSQLException {
        final OperationHandle opHandle = this.sessionManager.getSession(sessionHandle).getCatalogs();
        this.LOG.debug(sessionHandle + ": getCatalogs()");
        return opHandle;
    }
    
    @Override
    public OperationHandle getSchemas(final SessionHandle sessionHandle, final String catalogName, final String schemaName) throws HiveSQLException {
        final OperationHandle opHandle = this.sessionManager.getSession(sessionHandle).getSchemas(catalogName, schemaName);
        this.LOG.debug(sessionHandle + ": getSchemas()");
        return opHandle;
    }
    
    @Override
    public OperationHandle getTables(final SessionHandle sessionHandle, final String catalogName, final String schemaName, final String tableName, final List<String> tableTypes) throws HiveSQLException {
        final OperationHandle opHandle = this.sessionManager.getSession(sessionHandle).getTables(catalogName, schemaName, tableName, tableTypes);
        this.LOG.debug(sessionHandle + ": getTables()");
        return opHandle;
    }
    
    @Override
    public OperationHandle getTableTypes(final SessionHandle sessionHandle) throws HiveSQLException {
        final OperationHandle opHandle = this.sessionManager.getSession(sessionHandle).getTableTypes();
        this.LOG.debug(sessionHandle + ": getTableTypes()");
        return opHandle;
    }
    
    @Override
    public OperationHandle getColumns(final SessionHandle sessionHandle, final String catalogName, final String schemaName, final String tableName, final String columnName) throws HiveSQLException {
        final OperationHandle opHandle = this.sessionManager.getSession(sessionHandle).getColumns(catalogName, schemaName, tableName, columnName);
        this.LOG.debug(sessionHandle + ": getColumns()");
        return opHandle;
    }
    
    @Override
    public OperationHandle getFunctions(final SessionHandle sessionHandle, final String catalogName, final String schemaName, final String functionName) throws HiveSQLException {
        final OperationHandle opHandle = this.sessionManager.getSession(sessionHandle).getFunctions(catalogName, schemaName, functionName);
        this.LOG.debug(sessionHandle + ": getFunctions()");
        return opHandle;
    }
    
    @Override
    public OperationStatus getOperationStatus(final OperationHandle opHandle) throws HiveSQLException {
        final Operation operation = this.sessionManager.getOperationManager().getOperation(opHandle);
        if (operation.shouldRunAsync()) {
            final HiveConf conf = operation.getParentSession().getHiveConf();
            final long timeout = HiveConf.getTimeVar(conf, HiveConf.ConfVars.HIVE_SERVER2_LONG_POLLING_TIMEOUT, TimeUnit.MILLISECONDS);
            try {
                operation.getBackgroundHandle().get(timeout, TimeUnit.MILLISECONDS);
            }
            catch (TimeoutException e3) {
                this.LOG.trace(opHandle + ": Long polling timed out");
            }
            catch (CancellationException e) {
                this.LOG.trace(opHandle + ": The background operation was cancelled", e);
            }
            catch (ExecutionException e2) {
                this.LOG.warn(opHandle + ": The background operation was aborted", e2);
            }
            catch (InterruptedException ex) {}
        }
        final OperationStatus opStatus = operation.getStatus();
        this.LOG.debug(opHandle + ": getOperationStatus()");
        return opStatus;
    }
    
    @Override
    public void cancelOperation(final OperationHandle opHandle) throws HiveSQLException {
        this.sessionManager.getOperationManager().getOperation(opHandle).getParentSession().cancelOperation(opHandle);
        this.LOG.debug(opHandle + ": cancelOperation()");
    }
    
    @Override
    public void closeOperation(final OperationHandle opHandle) throws HiveSQLException {
        this.sessionManager.getOperationManager().getOperation(opHandle).getParentSession().closeOperation(opHandle);
        this.LOG.debug(opHandle + ": closeOperation");
    }
    
    @Override
    public TableSchema getResultSetMetadata(final OperationHandle opHandle) throws HiveSQLException {
        final TableSchema tableSchema = this.sessionManager.getOperationManager().getOperation(opHandle).getParentSession().getResultSetMetadata(opHandle);
        this.LOG.debug(opHandle + ": getResultSetMetadata()");
        return tableSchema;
    }
    
    @Override
    public RowSet fetchResults(final OperationHandle opHandle) throws HiveSQLException {
        return this.fetchResults(opHandle, Operation.DEFAULT_FETCH_ORIENTATION, 100L, FetchType.QUERY_OUTPUT);
    }
    
    @Override
    public RowSet fetchResults(final OperationHandle opHandle, final FetchOrientation orientation, final long maxRows, final FetchType fetchType) throws HiveSQLException {
        final RowSet rowSet = this.sessionManager.getOperationManager().getOperation(opHandle).getParentSession().fetchResults(opHandle, orientation, maxRows, fetchType);
        this.LOG.debug(opHandle + ": fetchResults()");
        return rowSet;
    }
    
    public synchronized String getDelegationTokenFromMetaStore(final String owner) throws HiveSQLException, UnsupportedOperationException, LoginException, IOException {
        if (!this.hiveConf.getBoolVar(HiveConf.ConfVars.METASTORE_USE_THRIFT_SASL) || !this.hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_ENABLE_DOAS)) {
            throw new UnsupportedOperationException("delegation token is can only be obtained for a secure remote metastore");
        }
        try {
            Hive.closeCurrent();
            return Hive.get(this.hiveConf).getDelegationToken(owner, owner);
        }
        catch (HiveException e) {
            if (e.getCause() instanceof UnsupportedOperationException) {
                throw (UnsupportedOperationException)e.getCause();
            }
            throw new HiveSQLException("Error connect metastore to setup impersonation", (Throwable)e);
        }
    }
    
    @Override
    public String getDelegationToken(final SessionHandle sessionHandle, final HiveAuthFactory authFactory, final String owner, final String renewer) throws HiveSQLException {
        final String delegationToken = this.sessionManager.getSession(sessionHandle).getDelegationToken(authFactory, owner, renewer);
        this.LOG.info(sessionHandle + ": getDelegationToken()");
        return delegationToken;
    }
    
    @Override
    public void cancelDelegationToken(final SessionHandle sessionHandle, final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        this.sessionManager.getSession(sessionHandle).cancelDelegationToken(authFactory, tokenStr);
        this.LOG.info(sessionHandle + ": cancelDelegationToken()");
    }
    
    @Override
    public void renewDelegationToken(final SessionHandle sessionHandle, final HiveAuthFactory authFactory, final String tokenStr) throws HiveSQLException {
        this.sessionManager.getSession(sessionHandle).renewDelegationToken(authFactory, tokenStr);
        this.LOG.info(sessionHandle + ": renewDelegationToken()");
    }
    
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }
    
    static {
        final TProtocolVersion[] protocols = TProtocolVersion.values();
        SERVER_VERSION = protocols[protocols.length - 1];
    }
}
