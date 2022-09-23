// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.auth;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.hive.shims.HadoopShims;
import org.apache.hadoop.security.authorize.ProxyUsers;
import org.apache.hive.service.cli.HiveSQLException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import javax.net.ssl.SSLServerSocket;
import java.util.List;
import java.net.InetSocketAddress;
import org.apache.thrift.transport.TServerSocket;
import javax.net.ssl.SSLParameters;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.thrift.TProcessorFactory;
import org.apache.hive.service.cli.thrift.ThriftCLIService;
import javax.security.auth.login.LoginException;
import org.apache.thrift.transport.TTransportFactory;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.thrift.transport.TTransportException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.thrift.DBTokenStore;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.thrift.HadoopThriftAuthBridge;
import org.slf4j.Logger;

public class HiveAuthFactory
{
    private static final Logger LOG;
    private HadoopThriftAuthBridge.Server saslServer;
    private String authTypeStr;
    private final String transportMode;
    private final HiveConf conf;
    public static final String HS2_PROXY_USER = "hive.server2.proxy.user";
    public static final String HS2_CLIENT_TOKEN = "hiveserver2ClientToken";
    
    public HiveAuthFactory(final HiveConf conf) throws TTransportException {
        this.conf = conf;
        this.transportMode = conf.getVar(HiveConf.ConfVars.HIVE_SERVER2_TRANSPORT_MODE);
        this.authTypeStr = conf.getVar(HiveConf.ConfVars.HIVE_SERVER2_AUTHENTICATION);
        if ("http".equalsIgnoreCase(this.transportMode)) {
            if (this.authTypeStr == null) {
                this.authTypeStr = AuthTypes.NOSASL.getAuthName();
            }
        }
        else {
            if (this.authTypeStr == null) {
                this.authTypeStr = AuthTypes.NONE.getAuthName();
            }
            if (this.authTypeStr.equalsIgnoreCase(AuthTypes.KERBEROS.getAuthName())) {
                this.saslServer = ShimLoader.getHadoopThriftAuthBridge().createServer(conf.getVar(HiveConf.ConfVars.HIVE_SERVER2_KERBEROS_KEYTAB), conf.getVar(HiveConf.ConfVars.HIVE_SERVER2_KERBEROS_PRINCIPAL));
                try {
                    Object rawStore = null;
                    final String tokenStoreClass = conf.getVar(HiveConf.ConfVars.METASTORE_CLUSTER_DELEGATION_TOKEN_STORE_CLS);
                    if (tokenStoreClass.equals(DBTokenStore.class.getName())) {
                        final HiveMetaStore.HMSHandler baseHandler = new HiveMetaStore.HMSHandler("new db based metaserver", conf, true);
                        rawStore = baseHandler.getMS();
                    }
                    this.saslServer.startDelegationTokenSecretManager(conf, rawStore, HadoopThriftAuthBridge.Server.ServerMode.HIVESERVER2);
                }
                catch (MetaException | IOException ex2) {
                    final Exception ex;
                    final Exception e = ex;
                    throw new TTransportException("Failed to start token manager", e);
                }
            }
        }
    }
    
    public Map<String, String> getSaslProperties() {
        final Map<String, String> saslProps = new HashMap<String, String>();
        final SaslQOP saslQOP = SaslQOP.fromString(this.conf.getVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_SASL_QOP));
        saslProps.put("javax.security.sasl.qop", saslQOP.toString());
        saslProps.put("javax.security.sasl.server.authentication", "true");
        return saslProps;
    }
    
    public TTransportFactory getAuthTransFactory() throws LoginException {
        if (this.authTypeStr.equalsIgnoreCase(AuthTypes.KERBEROS.getAuthName())) {
            try {
                final TTransportFactory transportFactory = this.saslServer.createTransportFactory(this.getSaslProperties());
                return transportFactory;
            }
            catch (TTransportException e) {
                throw new LoginException(e.getMessage());
            }
        }
        TTransportFactory transportFactory;
        if (this.authTypeStr.equalsIgnoreCase(AuthTypes.NONE.getAuthName())) {
            transportFactory = PlainSaslHelper.getPlainTransportFactory(this.authTypeStr);
        }
        else if (this.authTypeStr.equalsIgnoreCase(AuthTypes.LDAP.getAuthName())) {
            transportFactory = PlainSaslHelper.getPlainTransportFactory(this.authTypeStr);
        }
        else if (this.authTypeStr.equalsIgnoreCase(AuthTypes.PAM.getAuthName())) {
            transportFactory = PlainSaslHelper.getPlainTransportFactory(this.authTypeStr);
        }
        else if (this.authTypeStr.equalsIgnoreCase(AuthTypes.NOSASL.getAuthName())) {
            transportFactory = new TTransportFactory();
        }
        else {
            if (!this.authTypeStr.equalsIgnoreCase(AuthTypes.CUSTOM.getAuthName())) {
                throw new LoginException("Unsupported authentication type " + this.authTypeStr);
            }
            transportFactory = PlainSaslHelper.getPlainTransportFactory(this.authTypeStr);
        }
        return transportFactory;
    }
    
    public TProcessorFactory getAuthProcFactory(final ThriftCLIService service) throws LoginException {
        if (this.authTypeStr.equalsIgnoreCase(AuthTypes.KERBEROS.getAuthName())) {
            return KerberosSaslHelper.getKerberosProcessorFactory(this.saslServer, service);
        }
        return PlainSaslHelper.getPlainProcessorFactory(service);
    }
    
    public String getRemoteUser() {
        return (this.saslServer == null) ? null : this.saslServer.getRemoteUser();
    }
    
    public String getIpAddress() {
        if (this.saslServer == null || this.saslServer.getRemoteAddress() == null) {
            return null;
        }
        return this.saslServer.getRemoteAddress().getHostAddress();
    }
    
    public static void loginFromKeytab(final HiveConf hiveConf) throws IOException {
        final String principal = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_KERBEROS_PRINCIPAL);
        final String keyTabFile = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_KERBEROS_KEYTAB);
        if (principal.isEmpty() || keyTabFile.isEmpty()) {
            throw new IOException("HiveServer2 Kerberos principal or keytab is not correctly configured");
        }
        UserGroupInformation.loginUserFromKeytab(SecurityUtil.getServerPrincipal(principal, "0.0.0.0"), keyTabFile);
    }
    
    public static UserGroupInformation loginFromSpnegoKeytabAndReturnUGI(final HiveConf hiveConf) throws IOException {
        final String principal = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_SPNEGO_PRINCIPAL);
        final String keyTabFile = hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_SPNEGO_KEYTAB);
        if (principal.isEmpty() || keyTabFile.isEmpty()) {
            throw new IOException("HiveServer2 SPNEGO principal or keytab is not correctly configured");
        }
        return UserGroupInformation.loginUserFromKeytabAndReturnUGI(SecurityUtil.getServerPrincipal(principal, "0.0.0.0"), keyTabFile);
    }
    
    public static TTransport getSocketTransport(final String host, final int port, final int loginTimeout) {
        return new TSocket(host, port, loginTimeout);
    }
    
    public static TTransport getSSLSocket(final String host, final int port, final int loginTimeout) throws TTransportException {
        final TSocket tSSLSocket = TSSLTransportFactory.getClientSocket(host, port, loginTimeout);
        return getSSLSocketWithHttps(tSSLSocket);
    }
    
    public static TTransport getSSLSocket(final String host, final int port, final int loginTimeout, final String trustStorePath, final String trustStorePassWord) throws TTransportException {
        final TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
        params.setTrustStore(trustStorePath, trustStorePassWord);
        params.requireClientAuth(true);
        final TSocket tSSLSocket = TSSLTransportFactory.getClientSocket(host, port, loginTimeout, params);
        return getSSLSocketWithHttps(tSSLSocket);
    }
    
    private static TSocket getSSLSocketWithHttps(final TSocket tSSLSocket) throws TTransportException {
        final SSLSocket sslSocket = (SSLSocket)tSSLSocket.getSocket();
        final SSLParameters sslParams = sslSocket.getSSLParameters();
        sslParams.setEndpointIdentificationAlgorithm("HTTPS");
        sslSocket.setSSLParameters(sslParams);
        return new TSocket(sslSocket);
    }
    
    public static TServerSocket getServerSocket(final String hiveHost, final int portNum) throws TTransportException {
        InetSocketAddress serverAddress;
        if (hiveHost == null || hiveHost.isEmpty()) {
            serverAddress = new InetSocketAddress(portNum);
        }
        else {
            serverAddress = new InetSocketAddress(hiveHost, portNum);
        }
        return new TServerSocket(serverAddress);
    }
    
    public static TServerSocket getServerSSLSocket(final String hiveHost, final int portNum, final String keyStorePath, final String keyStorePassWord, final List<String> sslVersionBlacklist) throws TTransportException, UnknownHostException {
        final TSSLTransportFactory.TSSLTransportParameters params = new TSSLTransportFactory.TSSLTransportParameters();
        params.setKeyStore(keyStorePath, keyStorePassWord);
        InetSocketAddress serverAddress;
        if (hiveHost == null || hiveHost.isEmpty()) {
            serverAddress = new InetSocketAddress(portNum);
        }
        else {
            serverAddress = new InetSocketAddress(hiveHost, portNum);
        }
        final TServerSocket thriftServerSocket = TSSLTransportFactory.getServerSocket(portNum, 0, serverAddress.getAddress(), params);
        if (thriftServerSocket.getServerSocket() instanceof SSLServerSocket) {
            final List<String> sslVersionBlacklistLocal = new ArrayList<String>();
            for (final String sslVersion : sslVersionBlacklist) {
                sslVersionBlacklistLocal.add(sslVersion.trim().toLowerCase());
            }
            final SSLServerSocket sslServerSocket = (SSLServerSocket)thriftServerSocket.getServerSocket();
            final List<String> enabledProtocols = new ArrayList<String>();
            for (final String protocol : sslServerSocket.getEnabledProtocols()) {
                if (sslVersionBlacklistLocal.contains(protocol.toLowerCase())) {
                    HiveAuthFactory.LOG.debug("Disabling SSL Protocol: " + protocol);
                }
                else {
                    enabledProtocols.add(protocol);
                }
            }
            sslServerSocket.setEnabledProtocols(enabledProtocols.toArray(new String[0]));
            HiveAuthFactory.LOG.info("SSL Server Socket Enabled Protocols: " + Arrays.toString(sslServerSocket.getEnabledProtocols()));
        }
        return thriftServerSocket;
    }
    
    public String getDelegationToken(final String owner, final String renewer) throws HiveSQLException {
        if (this.saslServer == null) {
            throw new HiveSQLException("Delegation token only supported over kerberos authentication", "08S01");
        }
        try {
            final String tokenStr = this.saslServer.getDelegationTokenWithService(owner, renewer, "hiveserver2ClientToken");
            if (tokenStr == null || tokenStr.isEmpty()) {
                throw new HiveSQLException("Received empty retrieving delegation token for user " + owner, "08S01");
            }
            return tokenStr;
        }
        catch (IOException e) {
            throw new HiveSQLException("Error retrieving delegation token for user " + owner, "08S01", e);
        }
        catch (InterruptedException e2) {
            throw new HiveSQLException("delegation token retrieval interrupted", "08S01", e2);
        }
    }
    
    public void cancelDelegationToken(final String delegationToken) throws HiveSQLException {
        if (this.saslServer == null) {
            throw new HiveSQLException("Delegation token only supported over kerberos authentication", "08S01");
        }
        try {
            this.saslServer.cancelDelegationToken(delegationToken);
        }
        catch (IOException e) {
            throw new HiveSQLException("Error canceling delegation token " + delegationToken, "08S01", e);
        }
    }
    
    public void renewDelegationToken(final String delegationToken) throws HiveSQLException {
        if (this.saslServer == null) {
            throw new HiveSQLException("Delegation token only supported over kerberos authentication", "08S01");
        }
        try {
            this.saslServer.renewDelegationToken(delegationToken);
        }
        catch (IOException e) {
            throw new HiveSQLException("Error renewing delegation token " + delegationToken, "08S01", e);
        }
    }
    
    public String getUserFromToken(final String delegationToken) throws HiveSQLException {
        if (this.saslServer == null) {
            throw new HiveSQLException("Delegation token only supported over kerberos authentication", "08S01");
        }
        try {
            return this.saslServer.getUserFromToken(delegationToken);
        }
        catch (IOException e) {
            throw new HiveSQLException("Error extracting user from delegation token " + delegationToken, "08S01", e);
        }
    }
    
    public static void verifyProxyAccess(final String realUser, final String proxyUser, final String ipAddress, final HiveConf hiveConf) throws HiveSQLException {
        try {
            UserGroupInformation sessionUgi;
            if (UserGroupInformation.isSecurityEnabled()) {
                final HadoopShims.KerberosNameShim kerbName = ShimLoader.getHadoopShims().getKerberosNameShim(realUser);
                sessionUgi = UserGroupInformation.createProxyUser(kerbName.getServiceName(), UserGroupInformation.getLoginUser());
            }
            else {
                sessionUgi = UserGroupInformation.createRemoteUser(realUser);
            }
            if (!proxyUser.equalsIgnoreCase(realUser)) {
                ProxyUsers.refreshSuperUserGroupsConfiguration(hiveConf);
                ProxyUsers.authorize(UserGroupInformation.createProxyUser(proxyUser, sessionUgi), ipAddress, hiveConf);
            }
        }
        catch (IOException e) {
            throw new HiveSQLException("Failed to validate proxy privilege of " + realUser + " for " + proxyUser, "08S01", e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(HiveAuthFactory.class);
    }
    
    public enum AuthTypes
    {
        NOSASL("NOSASL"), 
        NONE("NONE"), 
        LDAP("LDAP"), 
        KERBEROS("KERBEROS"), 
        CUSTOM("CUSTOM"), 
        PAM("PAM");
        
        private final String authType;
        
        private AuthTypes(final String authType) {
            this.authType = authType;
        }
        
        public String getAuthName() {
            return this.authType;
        }
    }
}
