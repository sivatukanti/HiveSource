// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import org.apache.commons.logging.LogFactory;
import java.sql.ClientInfoStatus;
import java.sql.SQLClientInfoException;
import java.sql.Savepoint;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.Struct;
import java.sql.Statement;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import org.apache.hive.service.cli.thrift.TRenewDelegationTokenResp;
import org.apache.hive.service.cli.thrift.TRenewDelegationTokenReq;
import org.apache.hive.service.cli.thrift.TCancelDelegationTokenResp;
import org.apache.hive.service.cli.thrift.TCancelDelegationTokenReq;
import org.apache.hive.service.cli.thrift.TGetDelegationTokenResp;
import org.apache.hive.service.cli.thrift.TGetDelegationTokenReq;
import java.util.concurrent.Executor;
import java.sql.DriverManager;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.security.SecureRandom;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.security.sasl.SaslException;
import org.apache.hive.service.auth.PlainSaslHelper;
import org.apache.hive.service.auth.KerberosSaslHelper;
import org.apache.hive.service.auth.HiveAuthFactory;
import org.apache.hive.service.auth.SaslQOP;
import org.apache.http.config.Registry;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.config.Lookup;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.config.RegistryBuilder;
import javax.net.ssl.HostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContexts;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClients;
import java.util.HashMap;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.hive.service.cli.thrift.TOpenSessionResp;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.thrift.TException;
import org.apache.hive.service.cli.thrift.TCloseSessionReq;
import org.apache.hive.service.cli.thrift.TOpenSessionReq;
import org.apache.http.client.HttpClient;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import java.util.Iterator;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.cli.thrift.EmbeddedThriftBinaryCLIService;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;
import org.apache.hive.service.cli.thrift.TProtocolVersion;
import java.util.List;
import org.apache.hive.service.cli.thrift.TSessionHandle;
import java.sql.SQLWarning;
import org.apache.hive.service.cli.thrift.TCLIService;
import org.apache.thrift.transport.TTransport;
import java.util.Map;
import org.apache.commons.logging.Log;
import java.sql.Connection;

public class HiveConnection implements Connection
{
    public static final Log LOG;
    private static final String HIVE_VAR_PREFIX = "hivevar:";
    private static final String HIVE_CONF_PREFIX = "hiveconf:";
    private String jdbcUriString;
    private String host;
    private int port;
    private final Map<String, String> sessConfMap;
    private final Map<String, String> hiveConfMap;
    private final Map<String, String> hiveVarMap;
    private Utils.JdbcConnectionParams connParams;
    private final boolean isEmbeddedMode;
    private TTransport transport;
    private boolean assumeSubject;
    private TCLIService.Iface client;
    private boolean isClosed;
    private SQLWarning warningChain;
    private TSessionHandle sessHandle;
    private final List<TProtocolVersion> supportedProtocols;
    private int loginTimeout;
    private TProtocolVersion protocol;
    
    public HiveConnection(final String uri, final Properties info) throws SQLException {
        this.isClosed = true;
        this.warningChain = null;
        this.sessHandle = null;
        this.supportedProtocols = new LinkedList<TProtocolVersion>();
        this.loginTimeout = 0;
        this.setupLoginTimeout();
        try {
            this.connParams = Utils.parseURL(uri);
        }
        catch (ZooKeeperHiveClientException e) {
            throw new SQLException(e);
        }
        this.jdbcUriString = this.connParams.getJdbcUriString();
        this.host = this.connParams.getHost();
        this.port = this.connParams.getPort();
        this.sessConfMap = this.connParams.getSessionVars();
        this.hiveConfMap = this.connParams.getHiveConfs();
        this.hiveVarMap = this.connParams.getHiveVars();
        for (final Map.Entry<Object, Object> kv : info.entrySet()) {
            if (kv.getKey() instanceof String) {
                final String key = kv.getKey();
                if (key.startsWith("hivevar:")) {
                    this.hiveVarMap.put(key.substring("hivevar:".length()), info.getProperty(key));
                }
                else {
                    if (!key.startsWith("hiveconf:")) {
                        continue;
                    }
                    this.hiveConfMap.put(key.substring("hiveconf:".length()), info.getProperty(key));
                }
            }
        }
        this.isEmbeddedMode = this.connParams.isEmbeddedMode();
        if (this.isEmbeddedMode) {
            final EmbeddedThriftBinaryCLIService embeddedClient = new EmbeddedThriftBinaryCLIService();
            embeddedClient.init(new HiveConf());
            this.client = embeddedClient;
        }
        else {
            if (info.containsKey("user")) {
                this.sessConfMap.put("user", info.getProperty("user"));
                if (info.containsKey("password")) {
                    this.sessConfMap.put("password", info.getProperty("password"));
                }
            }
            if (info.containsKey("auth")) {
                this.sessConfMap.put("auth", info.getProperty("auth"));
            }
            this.openTransport();
            this.client = new TCLIService.Client(new TBinaryProtocol(this.transport));
        }
        this.supportedProtocols.add(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V1);
        this.supportedProtocols.add(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V2);
        this.supportedProtocols.add(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V3);
        this.supportedProtocols.add(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V4);
        this.supportedProtocols.add(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V5);
        this.supportedProtocols.add(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V6);
        this.supportedProtocols.add(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V7);
        this.supportedProtocols.add(TProtocolVersion.HIVE_CLI_SERVICE_PROTOCOL_V8);
        this.openSession();
    }
    
    private void openTransport() throws SQLException {
        while (true) {
            try {
                this.assumeSubject = "fromSubject".equals(this.sessConfMap.get("kerberosAuthType"));
                this.transport = (this.isHttpTransportMode() ? this.createHttpTransport() : this.createBinaryTransport());
                if (!this.transport.isOpen()) {
                    HiveConnection.LOG.info("Will try to open client transport with JDBC Uri: " + this.jdbcUriString);
                    this.transport.open();
                }
            }
            catch (TTransportException e) {
                HiveConnection.LOG.info("Could not open client transport with JDBC Uri: " + this.jdbcUriString);
                if (this.sessConfMap.get("serviceDiscoveryMode") != null && "zooKeeper".equalsIgnoreCase(this.sessConfMap.get("serviceDiscoveryMode"))) {
                    try {
                        Utils.updateConnParamsFromZooKeeper(this.connParams);
                    }
                    catch (ZooKeeperHiveClientException ze) {
                        throw new SQLException("Could not open client transport for any of the Server URI's in ZooKeeper: " + ze.getMessage(), " 08S01", ze);
                    }
                    this.jdbcUriString = this.connParams.getJdbcUriString();
                    this.host = this.connParams.getHost();
                    this.port = this.connParams.getPort();
                    HiveConnection.LOG.info("Will retry opening client transport");
                    continue;
                }
                HiveConnection.LOG.info("Transport Used for JDBC connection: " + this.sessConfMap.get("transportMode"));
                throw new SQLException("Could not open client transport with JDBC Uri: " + this.jdbcUriString + ": " + e.getMessage(), " 08S01", e);
            }
            break;
        }
    }
    
    private String getServerHttpUrl(final boolean useSsl) {
        final String schemeName = useSsl ? "https" : "http";
        String httpPath = this.sessConfMap.get("httpPath");
        if (httpPath == null) {
            httpPath = "/";
        }
        else if (!httpPath.startsWith("/")) {
            httpPath = "/" + httpPath;
        }
        return schemeName + "://" + this.host + ":" + this.port + httpPath;
    }
    
    private TTransport createHttpTransport() throws SQLException, TTransportException {
        final boolean useSsl = this.isSslConnection();
        final CloseableHttpClient httpClient = this.getHttpClient(useSsl);
        try {
            this.transport = new THttpClient(this.getServerHttpUrl(useSsl), httpClient);
            final TCLIService.Iface client = new TCLIService.Client(new TBinaryProtocol(this.transport));
            final TOpenSessionResp openResp = client.OpenSession(new TOpenSessionReq());
            if (openResp != null) {
                client.CloseSession(new TCloseSessionReq(openResp.getSessionHandle()));
            }
        }
        catch (TException e) {
            HiveConnection.LOG.info("JDBC Connection Parameters used : useSSL = " + useSsl + " , httpPath  = " + this.sessConfMap.get("httpPath") + " Authentication type = " + this.sessConfMap.get("auth"));
            final String msg = "Could not create http connection to " + this.jdbcUriString + ". " + e.getMessage();
            throw new TTransportException(msg, e);
        }
        return this.transport;
    }
    
    private CloseableHttpClient getHttpClient(final Boolean useSsl) throws SQLException {
        final boolean isCookieEnabled = this.sessConfMap.get("cookieAuth") == null || !"false".equalsIgnoreCase(this.sessConfMap.get("cookieAuth"));
        final String cookieName = (this.sessConfMap.get("cookieName") == null) ? "hive.server2.auth" : this.sessConfMap.get("cookieName");
        final CookieStore cookieStore = isCookieEnabled ? new BasicCookieStore() : null;
        final Map<String, String> additionalHttpHeaders = new HashMap<String, String>();
        for (final Map.Entry<String, String> entry : this.sessConfMap.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith("http.header.")) {
                additionalHttpHeaders.put(key.substring("http.header.".length()), entry.getValue());
            }
        }
        HttpRequestInterceptor requestInterceptor;
        if (this.isKerberosAuthMode()) {
            requestInterceptor = new HttpKerberosRequestInterceptor(this.sessConfMap.get("principal"), this.host, this.getServerHttpUrl(useSsl), this.assumeSubject, cookieStore, cookieName, useSsl, additionalHttpHeaders);
        }
        else {
            requestInterceptor = new HttpBasicAuthInterceptor(this.getUserName(), this.getPassword(), cookieStore, cookieName, useSsl, additionalHttpHeaders);
        }
        HttpClientBuilder httpClientBuilder;
        if (isCookieEnabled) {
            httpClientBuilder = HttpClients.custom().setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {
                @Override
                public boolean retryRequest(final HttpResponse response, final int executionCount, final HttpContext context) {
                    final int statusCode = response.getStatusLine().getStatusCode();
                    final boolean ret = statusCode == 401 && executionCount <= 1;
                    if (ret) {
                        context.setAttribute("hive.server2.retryserver", "true");
                    }
                    return ret;
                }
                
                @Override
                public long getRetryInterval() {
                    return 0L;
                }
            });
        }
        else {
            httpClientBuilder = HttpClientBuilder.create();
        }
        httpClientBuilder.addInterceptorFirst(requestInterceptor);
        if (useSsl) {
            final String useTwoWaySSL = this.sessConfMap.get("twoWay");
            final String sslTrustStorePath = this.sessConfMap.get("sslTrustStore");
            final String sslTrustStorePassword = this.sessConfMap.get("trustStorePassword");
            try {
                SSLConnectionSocketFactory socketFactory;
                if (useTwoWaySSL != null && useTwoWaySSL.equalsIgnoreCase("true")) {
                    socketFactory = this.getTwoWaySSLSocketFactory();
                }
                else if (sslTrustStorePath == null || sslTrustStorePath.isEmpty()) {
                    socketFactory = SSLConnectionSocketFactory.getSocketFactory();
                }
                else {
                    final KeyStore sslTrustStore = KeyStore.getInstance("JKS");
                    sslTrustStore.load(new FileInputStream(sslTrustStorePath), sslTrustStorePassword.toCharArray());
                    final SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(sslTrustStore, null).build();
                    socketFactory = new SSLConnectionSocketFactory(sslContext, new DefaultHostnameVerifier(null));
                }
                final Registry<ConnectionSocketFactory> registry = (Registry<ConnectionSocketFactory>)RegistryBuilder.create().register("https", socketFactory).build();
                httpClientBuilder.setConnectionManager(new BasicHttpClientConnectionManager(registry));
            }
            catch (Exception e) {
                final String msg = "Could not create an https connection to " + this.jdbcUriString + ". " + e.getMessage();
                throw new SQLException(msg, " 08S01", e);
            }
        }
        return httpClientBuilder.build();
    }
    
    private TTransport createBinaryTransport() throws SQLException, TTransportException {
        try {
            if (!"noSasl".equals(this.sessConfMap.get("auth"))) {
                final Map<String, String> saslProps = new HashMap<String, String>();
                SaslQOP saslQOP = SaslQOP.AUTH;
                if (this.sessConfMap.containsKey("saslQop")) {
                    try {
                        saslQOP = SaslQOP.fromString(this.sessConfMap.get("saslQop"));
                    }
                    catch (IllegalArgumentException e) {
                        throw new SQLException("Invalid saslQop parameter. " + e.getMessage(), "42000", e);
                    }
                    saslProps.put("javax.security.sasl.qop", saslQOP.toString());
                }
                else {
                    saslProps.put("javax.security.sasl.qop", "auth-conf,auth-int,auth");
                }
                saslProps.put("javax.security.sasl.server.authentication", "true");
                if (this.sessConfMap.containsKey("principal")) {
                    this.transport = KerberosSaslHelper.getKerberosTransport(this.sessConfMap.get("principal"), this.host, HiveAuthFactory.getSocketTransport(this.host, this.port, this.loginTimeout), saslProps, this.assumeSubject);
                }
                else {
                    final String tokenStr = this.getClientDelegationToken(this.sessConfMap);
                    if (tokenStr != null) {
                        this.transport = KerberosSaslHelper.getTokenTransport(tokenStr, this.host, HiveAuthFactory.getSocketTransport(this.host, this.port, this.loginTimeout), saslProps);
                    }
                    else {
                        final String userName = this.getUserName();
                        final String passwd = this.getPassword();
                        if (this.isSslConnection()) {
                            final String sslTrustStore = this.sessConfMap.get("sslTrustStore");
                            final String sslTrustStorePassword = this.sessConfMap.get("trustStorePassword");
                            if (sslTrustStore == null || sslTrustStore.isEmpty()) {
                                this.transport = HiveAuthFactory.getSSLSocket(this.host, this.port, this.loginTimeout);
                            }
                            else {
                                this.transport = HiveAuthFactory.getSSLSocket(this.host, this.port, this.loginTimeout, sslTrustStore, sslTrustStorePassword);
                            }
                        }
                        else {
                            this.transport = HiveAuthFactory.getSocketTransport(this.host, this.port, this.loginTimeout);
                        }
                        this.transport = PlainSaslHelper.getPlainTransport(userName, passwd, this.transport);
                    }
                }
            }
            else {
                this.transport = HiveAuthFactory.getSocketTransport(this.host, this.port, this.loginTimeout);
            }
        }
        catch (SaslException e2) {
            throw new SQLException("Could not create secure connection to " + this.jdbcUriString + ": " + e2.getMessage(), " 08S01", e2);
        }
        return this.transport;
    }
    
    SSLConnectionSocketFactory getTwoWaySSLSocketFactory() throws SQLException {
        SSLConnectionSocketFactory socketFactory = null;
        try {
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
            final String keyStorePath = this.sessConfMap.get("sslKeyStore");
            final String keyStorePassword = this.sessConfMap.get("keyStorePassword");
            final KeyStore sslKeyStore = KeyStore.getInstance("JKS");
            if (keyStorePath == null || keyStorePath.isEmpty()) {
                throw new IllegalArgumentException("sslKeyStore Not configured for 2 way SSL connection, keyStorePath param is empty");
            }
            sslKeyStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
            keyManagerFactory.init(sslKeyStore, keyStorePassword.toCharArray());
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            final String trustStorePath = this.sessConfMap.get("sslTrustStore");
            final String trustStorePassword = this.sessConfMap.get("trustStorePassword");
            final KeyStore sslTrustStore = KeyStore.getInstance("JKS");
            if (trustStorePath == null || trustStorePath.isEmpty()) {
                throw new IllegalArgumentException("sslTrustStore Not configured for 2 way SSL connection");
            }
            sslTrustStore.load(new FileInputStream(trustStorePath), trustStorePassword.toCharArray());
            trustManagerFactory.init(sslTrustStore);
            final SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            socketFactory = new SSLConnectionSocketFactory(context);
        }
        catch (Exception e) {
            throw new SQLException("Error while initializing 2 way ssl socket factory ", e);
        }
        return socketFactory;
    }
    
    private String getClientDelegationToken(final Map<String, String> jdbcConnConf) throws SQLException {
        String tokenStr = null;
        if ("delegationToken".equalsIgnoreCase(jdbcConnConf.get("auth"))) {
            try {
                tokenStr = org.apache.hadoop.hive.shims.Utils.getTokenStrForm("hiveserver2ClientToken");
            }
            catch (IOException e) {
                throw new SQLException("Error reading token ", e);
            }
        }
        return tokenStr;
    }
    
    private void openSession() throws SQLException {
        final TOpenSessionReq openReq = new TOpenSessionReq();
        final Map<String, String> openConf = new HashMap<String, String>();
        for (final Map.Entry<String, String> hiveConf : this.connParams.getHiveConfs().entrySet()) {
            openConf.put("set:hiveconf:" + hiveConf.getKey(), hiveConf.getValue());
        }
        for (final Map.Entry<String, String> hiveVar : this.connParams.getHiveVars().entrySet()) {
            openConf.put("set:hivevar:" + hiveVar.getKey(), hiveVar.getValue());
        }
        openConf.put("use:database", this.connParams.getDbName());
        final Map<String, String> sessVars = this.connParams.getSessionVars();
        if (sessVars.containsKey("hive.server2.proxy.user")) {
            openConf.put("hive.server2.proxy.user", sessVars.get("hive.server2.proxy.user"));
        }
        openReq.setConfiguration(openConf);
        if ("noSasl".equals(this.sessConfMap.get("auth"))) {
            openReq.setUsername(this.sessConfMap.get("user"));
            openReq.setPassword(this.sessConfMap.get("password"));
        }
        try {
            final TOpenSessionResp openResp = this.client.OpenSession(openReq);
            Utils.verifySuccess(openResp.getStatus());
            if (!this.supportedProtocols.contains(openResp.getServerProtocolVersion())) {
                throw new TException("Unsupported Hive2 protocol");
            }
            this.protocol = openResp.getServerProtocolVersion();
            this.sessHandle = openResp.getSessionHandle();
        }
        catch (TException e) {
            HiveConnection.LOG.error("Error opening session", e);
            throw new SQLException("Could not establish connection to " + this.jdbcUriString + ": " + e.getMessage(), " 08S01", e);
        }
        this.isClosed = false;
    }
    
    private String getUserName() {
        return this.getSessionValue("user", "anonymous");
    }
    
    private String getPassword() {
        return this.getSessionValue("password", "anonymous");
    }
    
    private boolean isSslConnection() {
        return "true".equalsIgnoreCase(this.sessConfMap.get("ssl"));
    }
    
    private boolean isKerberosAuthMode() {
        return !"noSasl".equals(this.sessConfMap.get("auth")) && this.sessConfMap.containsKey("principal");
    }
    
    private boolean isHttpTransportMode() {
        final String transportMode = this.sessConfMap.get("transportMode");
        return transportMode != null && transportMode.equalsIgnoreCase("http");
    }
    
    private String getSessionValue(final String varName, final String varDefault) {
        String varValue = this.sessConfMap.get(varName);
        if (varValue == null || varValue.isEmpty()) {
            varValue = varDefault;
        }
        return varValue;
    }
    
    private void setupLoginTimeout() {
        final long timeOut = TimeUnit.SECONDS.toMillis(DriverManager.getLoginTimeout());
        if (timeOut > 2147483647L) {
            this.loginTimeout = Integer.MAX_VALUE;
        }
        else {
            this.loginTimeout = (int)timeOut;
        }
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    public String getDelegationToken(final String owner, final String renewer) throws SQLException {
        final TGetDelegationTokenReq req = new TGetDelegationTokenReq(this.sessHandle, owner, renewer);
        try {
            final TGetDelegationTokenResp tokenResp = this.client.GetDelegationToken(req);
            Utils.verifySuccess(tokenResp.getStatus());
            return tokenResp.getDelegationToken();
        }
        catch (TException e) {
            throw new SQLException("Could not retrieve token: " + e.getMessage(), " 08S01", e);
        }
    }
    
    public void cancelDelegationToken(final String tokenStr) throws SQLException {
        final TCancelDelegationTokenReq cancelReq = new TCancelDelegationTokenReq(this.sessHandle, tokenStr);
        try {
            final TCancelDelegationTokenResp cancelResp = this.client.CancelDelegationToken(cancelReq);
            Utils.verifySuccess(cancelResp.getStatus());
        }
        catch (TException e) {
            throw new SQLException("Could not cancel token: " + e.getMessage(), " 08S01", e);
        }
    }
    
    public void renewDelegationToken(final String tokenStr) throws SQLException {
        final TRenewDelegationTokenReq cancelReq = new TRenewDelegationTokenReq(this.sessHandle, tokenStr);
        try {
            final TRenewDelegationTokenResp renewResp = this.client.RenewDelegationToken(cancelReq);
            Utils.verifySuccess(renewResp.getStatus());
        }
        catch (TException e) {
            throw new SQLException("Could not renew token: " + e.getMessage(), " 08S01", e);
        }
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.warningChain = null;
    }
    
    @Override
    public void close() throws SQLException {
        if (!this.isClosed) {
            final TCloseSessionReq closeReq = new TCloseSessionReq(this.sessHandle);
            try {
                this.client.CloseSession(closeReq);
            }
            catch (TException e) {
                throw new SQLException("Error while cleaning up the server resources", e);
            }
            finally {
                this.isClosed = true;
                if (this.transport != null) {
                    this.transport.close();
                }
            }
        }
    }
    
    @Override
    public void commit() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Array createArrayOf(final String arg0, final Object[] arg1) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Clob createClob() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Can't create Statement, connection is closed");
        }
        return new HiveStatement(this, this.client, this.sessHandle);
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        if (resultSetConcurrency != 1007) {
            throw new SQLException("Statement with resultset concurrency " + resultSetConcurrency + " is not supported", "HYC00");
        }
        if (resultSetType == 1005) {
            throw new SQLException("Statement with resultset type " + resultSetType + " is not supported", "HYC00");
        }
        return new HiveStatement(this, this.client, this.sessHandle, resultSetType == 1004);
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        return true;
    }
    
    @Override
    public String getCatalog() throws SQLException {
        return "";
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public int getHoldability() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Connection is closed");
        }
        return new HiveDatabaseMetaData(this, this.client, this.sessHandle);
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String getSchema() throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Connection is closed");
        }
        final Statement stmt = this.createStatement();
        final ResultSet res = stmt.executeQuery("SELECT current_database()");
        if (!res.next()) {
            throw new SQLException("Failed to get schema information");
        }
        final String schemaName = res.getString(1);
        res.close();
        stmt.close();
        return schemaName;
    }
    
    @Override
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.warningChain;
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.isClosed;
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return new HivePreparedStatement(this, this.client, this.sessHandle, sql);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return new HivePreparedStatement(this, this.client, this.sessHandle, sql);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return new HivePreparedStatement(this, this.client, this.sessHandle, sql);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void rollback() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        if (autoCommit) {
            throw new SQLException("enabling autocommit is not supported");
        }
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Connection is closed");
        }
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        throw new SQLClientInfoException("Method not supported", (Map<String, ClientInfoStatus>)null);
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        throw new SQLClientInfoException("Method not supported", (Map<String, ClientInfoStatus>)null);
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
        if (this.isClosed) {
            throw new SQLException("Connection is closed");
        }
        if (schema == null || schema.isEmpty()) {
            throw new SQLException("Schema name is null or empty");
        }
        final Statement stmt = this.createStatement();
        stmt.execute("use " + schema);
        stmt.close();
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException("Method not supported");
    }
    
    public TProtocolVersion getProtocol() {
        return this.protocol;
    }
    
    static {
        LOG = LogFactory.getLog(HiveConnection.class.getName());
    }
}
