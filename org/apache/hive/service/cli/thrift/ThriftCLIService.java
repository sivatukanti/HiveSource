// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.commons.logging.LogFactory;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.FetchType;
import org.apache.hive.service.cli.FetchOrientation;
import org.apache.hive.service.cli.TableSchema;
import org.apache.hive.service.cli.OperationStatus;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.cli.GetInfoValue;
import org.apache.hive.service.cli.GetInfoType;
import javax.security.auth.login.LoginException;
import org.apache.hadoop.hive.shims.HadoopShims;
import org.apache.hadoop.hive.shims.ShimLoader;
import java.io.IOException;
import org.apache.hive.service.auth.TSetIpAddressProcessor;
import org.apache.hive.service.cli.session.SessionManager;
import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TException;
import java.util.concurrent.TimeUnit;
import org.apache.hive.service.server.HiveServer2;
import java.net.UnknownHostException;
import org.apache.hive.service.ServiceException;
import org.apache.thrift.transport.TTransport;
import org.apache.hive.service.cli.SessionHandle;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.hadoop.hive.conf.HiveConf;
import org.eclipse.jetty.server.Server;
import org.apache.thrift.server.TServer;
import java.net.InetAddress;
import org.apache.hive.service.auth.HiveAuthFactory;
import org.apache.hive.service.cli.CLIService;
import org.apache.commons.logging.Log;
import org.apache.hive.service.AbstractService;

public abstract class ThriftCLIService extends AbstractService implements TCLIService.Iface, Runnable
{
    public static final Log LOG;
    protected CLIService cliService;
    private static final TStatus OK_STATUS;
    protected static HiveAuthFactory hiveAuthFactory;
    protected int portNum;
    protected InetAddress serverIPAddress;
    protected String hiveHost;
    protected TServer server;
    protected Server httpServer;
    private boolean isStarted;
    protected boolean isEmbedded;
    protected HiveConf hiveConf;
    protected int minWorkerThreads;
    protected int maxWorkerThreads;
    protected long workerKeepAliveTime;
    protected TServerEventHandler serverEventHandler;
    protected ThreadLocal<ServerContext> currentServerContext;
    
    public ThriftCLIService(final CLIService service, final String serviceName) {
        super(serviceName);
        this.isStarted = false;
        this.isEmbedded = false;
        this.cliService = service;
        this.currentServerContext = new ThreadLocal<ServerContext>();
        this.serverEventHandler = new TServerEventHandler() {
            @Override
            public ServerContext createContext(final TProtocol input, final TProtocol output) {
                return new ThriftCLIServerContext();
            }
            
            @Override
            public void deleteContext(final ServerContext serverContext, final TProtocol input, final TProtocol output) {
                final ThriftCLIServerContext context = (ThriftCLIServerContext)serverContext;
                final SessionHandle sessionHandle = context.getSessionHandle();
                if (sessionHandle != null) {
                    ThriftCLIService.LOG.info("Session disconnected without closing properly, close it now");
                    try {
                        ThriftCLIService.this.cliService.closeSession(sessionHandle);
                    }
                    catch (HiveSQLException e) {
                        ThriftCLIService.LOG.warn("Failed to close session: " + e, e);
                    }
                }
            }
            
            @Override
            public void preServe() {
            }
            
            @Override
            public void processContext(final ServerContext serverContext, final TTransport input, final TTransport output) {
                ThriftCLIService.this.currentServerContext.set(serverContext);
            }
        };
    }
    
    @Override
    public synchronized void init(final HiveConf hiveConf) {
        this.hiveConf = hiveConf;
        this.hiveHost = System.getenv("HIVE_SERVER2_THRIFT_BIND_HOST");
        if (this.hiveHost == null) {
            this.hiveHost = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_BIND_HOST);
        }
        try {
            if (this.hiveHost != null && !this.hiveHost.isEmpty()) {
                this.serverIPAddress = InetAddress.getByName(this.hiveHost);
            }
            else {
                this.serverIPAddress = InetAddress.getLocalHost();
            }
        }
        catch (UnknownHostException e) {
            throw new ServiceException(e);
        }
        if (HiveServer2.isHTTPTransportMode(hiveConf)) {
            this.workerKeepAliveTime = hiveConf.getTimeVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_WORKER_KEEPALIVE_TIME, TimeUnit.SECONDS);
            final String portString = System.getenv("HIVE_SERVER2_THRIFT_HTTP_PORT");
            if (portString != null) {
                this.portNum = Integer.valueOf(portString);
            }
            else {
                this.portNum = hiveConf.getIntVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_PORT);
            }
        }
        else {
            this.workerKeepAliveTime = hiveConf.getTimeVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_WORKER_KEEPALIVE_TIME, TimeUnit.SECONDS);
            final String portString = System.getenv("HIVE_SERVER2_THRIFT_PORT");
            if (portString != null) {
                this.portNum = Integer.valueOf(portString);
            }
            else {
                this.portNum = hiveConf.getIntVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_PORT);
            }
        }
        this.minWorkerThreads = hiveConf.getIntVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_MIN_WORKER_THREADS);
        this.maxWorkerThreads = hiveConf.getIntVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_MAX_WORKER_THREADS);
        super.init(hiveConf);
    }
    
    @Override
    public synchronized void start() {
        super.start();
        if (!this.isStarted && !this.isEmbedded) {
            new Thread(this).start();
            this.isStarted = true;
        }
    }
    
    @Override
    public synchronized void stop() {
        if (this.isStarted && !this.isEmbedded) {
            if (this.server != null) {
                this.server.stop();
                ThriftCLIService.LOG.info("Thrift server has stopped");
            }
            if (this.httpServer != null && this.httpServer.isStarted()) {
                try {
                    this.httpServer.stop();
                    ThriftCLIService.LOG.info("Http server has stopped");
                }
                catch (Exception e) {
                    ThriftCLIService.LOG.error("Error stopping Http server: ", e);
                }
            }
            this.isStarted = false;
        }
        super.stop();
    }
    
    public int getPortNumber() {
        return this.portNum;
    }
    
    public InetAddress getServerIPAddress() {
        return this.serverIPAddress;
    }
    
    @Override
    public TGetDelegationTokenResp GetDelegationToken(final TGetDelegationTokenReq req) throws TException {
        final TGetDelegationTokenResp resp = new TGetDelegationTokenResp();
        if (ThriftCLIService.hiveAuthFactory == null) {
            resp.setStatus(this.unsecureTokenErrorStatus());
        }
        else {
            try {
                final String token = this.cliService.getDelegationToken(new SessionHandle(req.getSessionHandle()), ThriftCLIService.hiveAuthFactory, req.getOwner(), req.getRenewer());
                resp.setDelegationToken(token);
                resp.setStatus(ThriftCLIService.OK_STATUS);
            }
            catch (HiveSQLException e) {
                ThriftCLIService.LOG.error("Error obtaining delegation token", e);
                final TStatus tokenErrorStatus = HiveSQLException.toTStatus(e);
                tokenErrorStatus.setSqlState("42000");
                resp.setStatus(tokenErrorStatus);
            }
        }
        return resp;
    }
    
    @Override
    public TCancelDelegationTokenResp CancelDelegationToken(final TCancelDelegationTokenReq req) throws TException {
        final TCancelDelegationTokenResp resp = new TCancelDelegationTokenResp();
        if (ThriftCLIService.hiveAuthFactory == null) {
            resp.setStatus(this.unsecureTokenErrorStatus());
        }
        else {
            try {
                this.cliService.cancelDelegationToken(new SessionHandle(req.getSessionHandle()), ThriftCLIService.hiveAuthFactory, req.getDelegationToken());
                resp.setStatus(ThriftCLIService.OK_STATUS);
            }
            catch (HiveSQLException e) {
                ThriftCLIService.LOG.error("Error canceling delegation token", e);
                resp.setStatus(HiveSQLException.toTStatus(e));
            }
        }
        return resp;
    }
    
    @Override
    public TRenewDelegationTokenResp RenewDelegationToken(final TRenewDelegationTokenReq req) throws TException {
        final TRenewDelegationTokenResp resp = new TRenewDelegationTokenResp();
        if (ThriftCLIService.hiveAuthFactory == null) {
            resp.setStatus(this.unsecureTokenErrorStatus());
        }
        else {
            try {
                this.cliService.renewDelegationToken(new SessionHandle(req.getSessionHandle()), ThriftCLIService.hiveAuthFactory, req.getDelegationToken());
                resp.setStatus(ThriftCLIService.OK_STATUS);
            }
            catch (HiveSQLException e) {
                ThriftCLIService.LOG.error("Error obtaining renewing token", e);
                resp.setStatus(HiveSQLException.toTStatus(e));
            }
        }
        return resp;
    }
    
    private TStatus unsecureTokenErrorStatus() {
        final TStatus errorStatus = new TStatus(TStatusCode.ERROR_STATUS);
        errorStatus.setErrorMessage("Delegation token only supported over remote client with kerberos authentication");
        return errorStatus;
    }
    
    @Override
    public TOpenSessionResp OpenSession(final TOpenSessionReq req) throws TException {
        ThriftCLIService.LOG.info("Client protocol version: " + req.getClient_protocol());
        final TOpenSessionResp resp = new TOpenSessionResp();
        try {
            final SessionHandle sessionHandle = this.getSessionHandle(req, resp);
            resp.setSessionHandle(sessionHandle.toTSessionHandle());
            resp.setConfiguration(new HashMap<String, String>());
            resp.setStatus(ThriftCLIService.OK_STATUS);
            final ThriftCLIServerContext context = this.currentServerContext.get();
            if (context != null) {
                context.setSessionHandle(sessionHandle);
            }
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error opening session: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    private String getIpAddress() {
        String clientIpAddress;
        if (this.cliService.getHiveConf().getVar(HiveConf.ConfVars.HIVE_SERVER2_TRANSPORT_MODE).equalsIgnoreCase("http")) {
            clientIpAddress = SessionManager.getIpAddress();
        }
        else if (this.isKerberosAuthMode()) {
            clientIpAddress = ThriftCLIService.hiveAuthFactory.getIpAddress();
        }
        else {
            clientIpAddress = TSetIpAddressProcessor.getUserIpAddress();
        }
        ThriftCLIService.LOG.debug("Client's IP Address: " + clientIpAddress);
        return clientIpAddress;
    }
    
    private String getUserName(final TOpenSessionReq req) throws HiveSQLException, IOException {
        String userName = null;
        if (this.isKerberosAuthMode()) {
            userName = ThriftCLIService.hiveAuthFactory.getRemoteUser();
        }
        if (userName == null) {
            userName = TSetIpAddressProcessor.getUserName();
        }
        if (this.cliService.getHiveConf().getVar(HiveConf.ConfVars.HIVE_SERVER2_TRANSPORT_MODE).equalsIgnoreCase("http")) {
            userName = SessionManager.getUserName();
        }
        if (userName == null) {
            userName = req.getUsername();
        }
        userName = this.getShortName(userName);
        final String effectiveClientUser = this.getProxyUser(userName, req.getConfiguration(), this.getIpAddress());
        ThriftCLIService.LOG.debug("Client's username: " + effectiveClientUser);
        return effectiveClientUser;
    }
    
    private String getShortName(final String userName) throws IOException {
        String ret = null;
        if (userName != null) {
            final HadoopShims.KerberosNameShim fullKerberosName = ShimLoader.getHadoopShims().getKerberosNameShim(userName);
            ret = fullKerberosName.getShortName();
        }
        return ret;
    }
    
    SessionHandle getSessionHandle(final TOpenSessionReq req, final TOpenSessionResp res) throws HiveSQLException, LoginException, IOException {
        final String userName = this.getUserName(req);
        final String ipAddress = this.getIpAddress();
        final TProtocolVersion protocol = this.getMinVersion(CLIService.SERVER_VERSION, req.getClient_protocol());
        SessionHandle sessionHandle;
        if (this.cliService.getHiveConf().getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_ENABLE_DOAS) && userName != null) {
            final String delegationTokenStr = this.getDelegationToken(userName);
            sessionHandle = this.cliService.openSessionWithImpersonation(protocol, userName, req.getPassword(), ipAddress, req.getConfiguration(), delegationTokenStr);
        }
        else {
            sessionHandle = this.cliService.openSession(protocol, userName, req.getPassword(), ipAddress, req.getConfiguration());
        }
        res.setServerProtocolVersion(protocol);
        return sessionHandle;
    }
    
    private String getDelegationToken(final String userName) throws HiveSQLException, LoginException, IOException {
        if (userName == null || !this.cliService.getHiveConf().getVar(HiveConf.ConfVars.HIVE_SERVER2_AUTHENTICATION).equalsIgnoreCase(HiveAuthFactory.AuthTypes.KERBEROS.toString())) {
            return null;
        }
        try {
            return this.cliService.getDelegationTokenFromMetaStore(userName);
        }
        catch (UnsupportedOperationException ex) {
            return null;
        }
    }
    
    private TProtocolVersion getMinVersion(final TProtocolVersion... versions) {
        final TProtocolVersion[] values = TProtocolVersion.values();
        int current = values[values.length - 1].getValue();
        for (final TProtocolVersion version : versions) {
            if (current > version.getValue()) {
                current = version.getValue();
            }
        }
        for (final TProtocolVersion version : values) {
            if (version.getValue() == current) {
                return version;
            }
        }
        throw new IllegalArgumentException("never");
    }
    
    @Override
    public TCloseSessionResp CloseSession(final TCloseSessionReq req) throws TException {
        final TCloseSessionResp resp = new TCloseSessionResp();
        try {
            final SessionHandle sessionHandle = new SessionHandle(req.getSessionHandle());
            this.cliService.closeSession(sessionHandle);
            resp.setStatus(ThriftCLIService.OK_STATUS);
            final ThriftCLIServerContext context = this.currentServerContext.get();
            if (context != null) {
                context.setSessionHandle(null);
            }
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error closing session: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TGetInfoResp GetInfo(final TGetInfoReq req) throws TException {
        final TGetInfoResp resp = new TGetInfoResp();
        try {
            final GetInfoValue getInfoValue = this.cliService.getInfo(new SessionHandle(req.getSessionHandle()), GetInfoType.getGetInfoType(req.getInfoType()));
            resp.setInfoValue(getInfoValue.toTGetInfoValue());
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error getting info: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TExecuteStatementResp ExecuteStatement(final TExecuteStatementReq req) throws TException {
        final TExecuteStatementResp resp = new TExecuteStatementResp();
        try {
            final SessionHandle sessionHandle = new SessionHandle(req.getSessionHandle());
            final String statement = req.getStatement();
            final Map<String, String> confOverlay = req.getConfOverlay();
            final Boolean runAsync = req.isRunAsync();
            final OperationHandle operationHandle = runAsync ? this.cliService.executeStatementAsync(sessionHandle, statement, confOverlay) : this.cliService.executeStatement(sessionHandle, statement, confOverlay);
            resp.setOperationHandle(operationHandle.toTOperationHandle());
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error executing statement: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TGetTypeInfoResp GetTypeInfo(final TGetTypeInfoReq req) throws TException {
        final TGetTypeInfoResp resp = new TGetTypeInfoResp();
        try {
            final OperationHandle operationHandle = this.cliService.getTypeInfo(new SessionHandle(req.getSessionHandle()));
            resp.setOperationHandle(operationHandle.toTOperationHandle());
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error getting type info: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TGetCatalogsResp GetCatalogs(final TGetCatalogsReq req) throws TException {
        final TGetCatalogsResp resp = new TGetCatalogsResp();
        try {
            final OperationHandle opHandle = this.cliService.getCatalogs(new SessionHandle(req.getSessionHandle()));
            resp.setOperationHandle(opHandle.toTOperationHandle());
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error getting catalogs: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TGetSchemasResp GetSchemas(final TGetSchemasReq req) throws TException {
        final TGetSchemasResp resp = new TGetSchemasResp();
        try {
            final OperationHandle opHandle = this.cliService.getSchemas(new SessionHandle(req.getSessionHandle()), req.getCatalogName(), req.getSchemaName());
            resp.setOperationHandle(opHandle.toTOperationHandle());
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error getting schemas: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TGetTablesResp GetTables(final TGetTablesReq req) throws TException {
        final TGetTablesResp resp = new TGetTablesResp();
        try {
            final OperationHandle opHandle = this.cliService.getTables(new SessionHandle(req.getSessionHandle()), req.getCatalogName(), req.getSchemaName(), req.getTableName(), req.getTableTypes());
            resp.setOperationHandle(opHandle.toTOperationHandle());
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error getting tables: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TGetTableTypesResp GetTableTypes(final TGetTableTypesReq req) throws TException {
        final TGetTableTypesResp resp = new TGetTableTypesResp();
        try {
            final OperationHandle opHandle = this.cliService.getTableTypes(new SessionHandle(req.getSessionHandle()));
            resp.setOperationHandle(opHandle.toTOperationHandle());
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error getting table types: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TGetColumnsResp GetColumns(final TGetColumnsReq req) throws TException {
        final TGetColumnsResp resp = new TGetColumnsResp();
        try {
            final OperationHandle opHandle = this.cliService.getColumns(new SessionHandle(req.getSessionHandle()), req.getCatalogName(), req.getSchemaName(), req.getTableName(), req.getColumnName());
            resp.setOperationHandle(opHandle.toTOperationHandle());
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error getting columns: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TGetFunctionsResp GetFunctions(final TGetFunctionsReq req) throws TException {
        final TGetFunctionsResp resp = new TGetFunctionsResp();
        try {
            final OperationHandle opHandle = this.cliService.getFunctions(new SessionHandle(req.getSessionHandle()), req.getCatalogName(), req.getSchemaName(), req.getFunctionName());
            resp.setOperationHandle(opHandle.toTOperationHandle());
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error getting functions: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TGetOperationStatusResp GetOperationStatus(final TGetOperationStatusReq req) throws TException {
        final TGetOperationStatusResp resp = new TGetOperationStatusResp();
        try {
            final OperationStatus operationStatus = this.cliService.getOperationStatus(new OperationHandle(req.getOperationHandle()));
            resp.setOperationState(operationStatus.getState().toTOperationState());
            final HiveSQLException opException = operationStatus.getOperationException();
            if (opException != null) {
                resp.setSqlState(opException.getSQLState());
                resp.setErrorCode(opException.getErrorCode());
                resp.setErrorMessage(opException.getMessage());
            }
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error getting operation status: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TCancelOperationResp CancelOperation(final TCancelOperationReq req) throws TException {
        final TCancelOperationResp resp = new TCancelOperationResp();
        try {
            this.cliService.cancelOperation(new OperationHandle(req.getOperationHandle()));
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error cancelling operation: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TCloseOperationResp CloseOperation(final TCloseOperationReq req) throws TException {
        final TCloseOperationResp resp = new TCloseOperationResp();
        try {
            this.cliService.closeOperation(new OperationHandle(req.getOperationHandle()));
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error closing operation: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TGetResultSetMetadataResp GetResultSetMetadata(final TGetResultSetMetadataReq req) throws TException {
        final TGetResultSetMetadataResp resp = new TGetResultSetMetadataResp();
        try {
            final TableSchema schema = this.cliService.getResultSetMetadata(new OperationHandle(req.getOperationHandle()));
            resp.setSchema(schema.toTTableSchema());
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error getting result set metadata: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public TFetchResultsResp FetchResults(final TFetchResultsReq req) throws TException {
        final TFetchResultsResp resp = new TFetchResultsResp();
        try {
            final RowSet rowSet = this.cliService.fetchResults(new OperationHandle(req.getOperationHandle()), FetchOrientation.getFetchOrientation(req.getOrientation()), req.getMaxRows(), FetchType.getFetchType(req.getFetchType()));
            resp.setResults(rowSet.toTRowSet());
            resp.setHasMoreRows(false);
            resp.setStatus(ThriftCLIService.OK_STATUS);
        }
        catch (Exception e) {
            ThriftCLIService.LOG.warn("Error fetching results: ", e);
            resp.setStatus(HiveSQLException.toTStatus(e));
        }
        return resp;
    }
    
    @Override
    public abstract void run();
    
    private String getProxyUser(final String realUser, final Map<String, String> sessionConf, final String ipAddress) throws HiveSQLException {
        String proxyUser = null;
        if (this.cliService.getHiveConf().getVar(HiveConf.ConfVars.HIVE_SERVER2_TRANSPORT_MODE).equalsIgnoreCase("http")) {
            proxyUser = SessionManager.getProxyUserName();
            ThriftCLIService.LOG.debug("Proxy user from query string: " + proxyUser);
        }
        if (proxyUser == null && sessionConf != null && sessionConf.containsKey("hive.server2.proxy.user")) {
            final String proxyUserFromThriftBody = sessionConf.get("hive.server2.proxy.user");
            ThriftCLIService.LOG.debug("Proxy user from thrift body: " + proxyUserFromThriftBody);
            proxyUser = proxyUserFromThriftBody;
        }
        if (proxyUser == null) {
            return realUser;
        }
        if (!this.hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_ALLOW_USER_SUBSTITUTION)) {
            throw new HiveSQLException("Proxy user substitution is not allowed");
        }
        if (HiveAuthFactory.AuthTypes.NONE.toString().equalsIgnoreCase(this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_AUTHENTICATION))) {
            return proxyUser;
        }
        HiveAuthFactory.verifyProxyAccess(realUser, proxyUser, ipAddress, this.hiveConf);
        ThriftCLIService.LOG.debug("Verified proxy user: " + proxyUser);
        return proxyUser;
    }
    
    private boolean isKerberosAuthMode() {
        return this.cliService.getHiveConf().getVar(HiveConf.ConfVars.HIVE_SERVER2_AUTHENTICATION).equalsIgnoreCase(HiveAuthFactory.AuthTypes.KERBEROS.toString());
    }
    
    static {
        LOG = LogFactory.getLog(ThriftCLIService.class.getName());
        OK_STATUS = new TStatus(TStatusCode.SUCCESS_STATUS);
    }
    
    static class ThriftCLIServerContext implements ServerContext
    {
        private SessionHandle sessionHandle;
        
        ThriftCLIServerContext() {
            this.sessionHandle = null;
        }
        
        public void setSessionHandle(final SessionHandle sessionHandle) {
            this.sessionHandle = sessionHandle;
        }
        
        public SessionHandle getSessionHandle() {
            return this.sessionHandle;
        }
    }
}
