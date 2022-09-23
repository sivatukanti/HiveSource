// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.thrift;

import java.security.PrivilegedAction;
import java.net.Socket;
import javax.security.sasl.SaslServer;
import org.apache.hadoop.fs.FileSystem;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import javax.security.sasl.AuthorizeCallback;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.hive.shims.Utils;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.security.authorize.ProxyUsers;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TProcessor;
import org.apache.thrift.transport.TSaslServerTransport;
import org.apache.thrift.transport.TTransportFactory;
import java.net.InetAddress;
import org.apache.commons.codec.binary.Base64;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.auth.callback.Callback;
import javax.security.sasl.SaslException;
import org.apache.hadoop.hive.thrift.client.TUGIAssumingTransport;
import javax.security.auth.callback.CallbackHandler;
import org.apache.thrift.transport.TSaslClientTransport;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.thrift.transport.TTransport;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import java.util.Locale;
import org.apache.hadoop.security.SaslRpcServer;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.thrift.transport.TTransportException;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.commons.logging.Log;

public class HadoopThriftAuthBridge
{
    private static final Log LOG;
    
    public Client createClient() {
        return new Client();
    }
    
    public Client createClientWithConf(final String authMethod) {
        UserGroupInformation ugi;
        try {
            ugi = UserGroupInformation.getLoginUser();
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to get current login user: " + e, e);
        }
        if (this.loginUserHasCurrentAuthMethod(ugi, authMethod)) {
            HadoopThriftAuthBridge.LOG.debug("Not setting UGI conf as passed-in authMethod of " + authMethod + " = current.");
            return new Client();
        }
        HadoopThriftAuthBridge.LOG.debug("Setting UGI conf as passed-in authMethod of " + authMethod + " != current.");
        final Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", authMethod);
        UserGroupInformation.setConfiguration(conf);
        return new Client();
    }
    
    public Server createServer(final String keytabFile, final String principalConf) throws TTransportException {
        return new Server(keytabFile, principalConf);
    }
    
    public String getServerPrincipal(final String principalConfig, final String host) throws IOException {
        final String serverPrincipal = SecurityUtil.getServerPrincipal(principalConfig, host);
        final String[] names = SaslRpcServer.splitKerberosName(serverPrincipal);
        if (names.length != 3) {
            throw new IOException("Kerberos principal name does NOT have the expected hostname part: " + serverPrincipal);
        }
        return serverPrincipal;
    }
    
    public UserGroupInformation getCurrentUGIWithConf(final String authMethod) throws IOException {
        UserGroupInformation ugi;
        try {
            ugi = UserGroupInformation.getCurrentUser();
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to get current user: " + e, e);
        }
        if (this.loginUserHasCurrentAuthMethod(ugi, authMethod)) {
            HadoopThriftAuthBridge.LOG.debug("Not setting UGI conf as passed-in authMethod of " + authMethod + " = current.");
            return ugi;
        }
        HadoopThriftAuthBridge.LOG.debug("Setting UGI conf as passed-in authMethod of " + authMethod + " != current.");
        final Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", authMethod);
        UserGroupInformation.setConfiguration(conf);
        return UserGroupInformation.getCurrentUser();
    }
    
    private boolean loginUserHasCurrentAuthMethod(final UserGroupInformation ugi, final String sAuthMethod) {
        UserGroupInformation.AuthenticationMethod authMethod;
        try {
            authMethod = Enum.valueOf(UserGroupInformation.AuthenticationMethod.class, sAuthMethod.toUpperCase(Locale.ENGLISH));
        }
        catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Invalid attribute value for hadoop.security.authentication of " + sAuthMethod, iae);
        }
        HadoopThriftAuthBridge.LOG.debug("Current authMethod = " + ugi.getAuthenticationMethod());
        return ugi.getAuthenticationMethod().equals(authMethod);
    }
    
    public Map<String, String> getHadoopSaslProperties(final Configuration conf) {
        SaslRpcServer.init(conf);
        return (Map<String, String>)SaslRpcServer.SASL_PROPS;
    }
    
    static {
        LOG = LogFactory.getLog(HadoopThriftAuthBridge.class);
    }
    
    public static class Client
    {
        public TTransport createClientTransport(final String principalConfig, final String host, final String methodStr, final String tokenStrForm, final TTransport underlyingTransport, final Map<String, String> saslProps) throws IOException {
            final SaslRpcServer.AuthMethod method = Enum.valueOf(SaslRpcServer.AuthMethod.class, methodStr);
            TTransport saslTransport = null;
            switch (method) {
                case DIGEST: {
                    final Token<DelegationTokenIdentifier> t = new Token<DelegationTokenIdentifier>();
                    t.decodeFromUrlString(tokenStrForm);
                    saslTransport = new TSaslClientTransport(method.getMechanismName(), null, null, "default", saslProps, new SaslClientCallbackHandler(t), underlyingTransport);
                    return new TUGIAssumingTransport(saslTransport, UserGroupInformation.getCurrentUser());
                }
                case KERBEROS: {
                    final String serverPrincipal = SecurityUtil.getServerPrincipal(principalConfig, host);
                    final String[] names = SaslRpcServer.splitKerberosName(serverPrincipal);
                    if (names.length != 3) {
                        throw new IOException("Kerberos principal name does NOT have the expected hostname part: " + serverPrincipal);
                    }
                    try {
                        saslTransport = new TSaslClientTransport(method.getMechanismName(), null, names[0], names[1], saslProps, null, underlyingTransport);
                        return new TUGIAssumingTransport(saslTransport, UserGroupInformation.getCurrentUser());
                    }
                    catch (SaslException se) {
                        throw new IOException("Could not instantiate SASL transport", se);
                    }
                    break;
                }
            }
            throw new IOException("Unsupported authentication method: " + method);
        }
        
        private static class SaslClientCallbackHandler implements CallbackHandler
        {
            private final String userName;
            private final char[] userPassword;
            
            public SaslClientCallbackHandler(final Token<? extends TokenIdentifier> token) {
                this.userName = encodeIdentifier(token.getIdentifier());
                this.userPassword = encodePassword(token.getPassword());
            }
            
            @Override
            public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {
                NameCallback nc = null;
                PasswordCallback pc = null;
                RealmCallback rc = null;
                for (final Callback callback : callbacks) {
                    if (!(callback instanceof RealmChoiceCallback)) {
                        if (callback instanceof NameCallback) {
                            nc = (NameCallback)callback;
                        }
                        else if (callback instanceof PasswordCallback) {
                            pc = (PasswordCallback)callback;
                        }
                        else {
                            if (!(callback instanceof RealmCallback)) {
                                throw new UnsupportedCallbackException(callback, "Unrecognized SASL client callback");
                            }
                            rc = (RealmCallback)callback;
                        }
                    }
                }
                if (nc != null) {
                    if (HadoopThriftAuthBridge.LOG.isDebugEnabled()) {
                        HadoopThriftAuthBridge.LOG.debug("SASL client callback: setting username: " + this.userName);
                    }
                    nc.setName(this.userName);
                }
                if (pc != null) {
                    if (HadoopThriftAuthBridge.LOG.isDebugEnabled()) {
                        HadoopThriftAuthBridge.LOG.debug("SASL client callback: setting userPassword");
                    }
                    pc.setPassword(this.userPassword);
                }
                if (rc != null) {
                    if (HadoopThriftAuthBridge.LOG.isDebugEnabled()) {
                        HadoopThriftAuthBridge.LOG.debug("SASL client callback: setting realm: " + rc.getDefaultText());
                    }
                    rc.setText(rc.getDefaultText());
                }
            }
            
            static String encodeIdentifier(final byte[] identifier) {
                return new String(Base64.encodeBase64(identifier));
            }
            
            static char[] encodePassword(final byte[] password) {
                return new String(Base64.encodeBase64(password)).toCharArray();
            }
        }
    }
    
    public static class Server
    {
        public static final String DELEGATION_TOKEN_GC_INTERVAL = "hive.cluster.delegation.token.gc-interval";
        private static final long DELEGATION_TOKEN_GC_INTERVAL_DEFAULT = 3600000L;
        public static final String DELEGATION_KEY_UPDATE_INTERVAL_KEY = "hive.cluster.delegation.key.update-interval";
        public static final long DELEGATION_KEY_UPDATE_INTERVAL_DEFAULT = 86400000L;
        public static final String DELEGATION_TOKEN_RENEW_INTERVAL_KEY = "hive.cluster.delegation.token.renew-interval";
        public static final long DELEGATION_TOKEN_RENEW_INTERVAL_DEFAULT = 86400000L;
        public static final String DELEGATION_TOKEN_MAX_LIFETIME_KEY = "hive.cluster.delegation.token.max-lifetime";
        public static final long DELEGATION_TOKEN_MAX_LIFETIME_DEFAULT = 604800000L;
        public static final String DELEGATION_TOKEN_STORE_CLS = "hive.cluster.delegation.token.store.class";
        public static final String DELEGATION_TOKEN_STORE_ZK_CONNECT_STR = "hive.cluster.delegation.token.store.zookeeper.connectString";
        public static final String DELEGATION_TOKEN_STORE_ZK_CONNECT_STR_ALTERNATE = "hive.zookeeper.quorum";
        public static final String DELEGATION_TOKEN_STORE_ZK_CONNECT_TIMEOUTMILLIS = "hive.cluster.delegation.token.store.zookeeper.connectTimeoutMillis";
        public static final String DELEGATION_TOKEN_STORE_ZK_ZNODE = "hive.cluster.delegation.token.store.zookeeper.znode";
        public static final String DELEGATION_TOKEN_STORE_ZK_ACL = "hive.cluster.delegation.token.store.zookeeper.acl";
        public static final String DELEGATION_TOKEN_STORE_ZK_ZNODE_DEFAULT = "/hivedelegation";
        protected final UserGroupInformation realUgi;
        protected DelegationTokenSecretManager secretManager;
        static final ThreadLocal<InetAddress> remoteAddress;
        static final ThreadLocal<UserGroupInformation.AuthenticationMethod> authenticationMethod;
        private static ThreadLocal<String> remoteUser;
        
        public Server() throws TTransportException {
            try {
                this.realUgi = UserGroupInformation.getCurrentUser();
            }
            catch (IOException ioe) {
                throw new TTransportException(ioe);
            }
        }
        
        protected Server(final String keytabFile, final String principalConf) throws TTransportException {
            if (keytabFile == null || keytabFile.isEmpty()) {
                throw new TTransportException("No keytab specified");
            }
            if (principalConf == null || principalConf.isEmpty()) {
                throw new TTransportException("No principal specified");
            }
            try {
                final String kerberosName = SecurityUtil.getServerPrincipal(principalConf, "0.0.0.0");
                UserGroupInformation.loginUserFromKeytab(kerberosName, keytabFile);
                this.realUgi = UserGroupInformation.getLoginUser();
                assert this.realUgi.isFromKeytab();
            }
            catch (IOException ioe) {
                throw new TTransportException(ioe);
            }
        }
        
        public TTransportFactory createTransportFactory(final Map<String, String> saslProps) throws TTransportException {
            final String kerberosName = this.realUgi.getUserName();
            final String[] names = SaslRpcServer.splitKerberosName(kerberosName);
            if (names.length != 3) {
                throw new TTransportException("Kerberos principal should have 3 parts: " + kerberosName);
            }
            final TSaslServerTransport.Factory transFactory = new TSaslServerTransport.Factory();
            transFactory.addServerDefinition(SaslRpcServer.AuthMethod.KERBEROS.getMechanismName(), names[0], names[1], saslProps, new SaslRpcServer.SaslGssCallbackHandler());
            transFactory.addServerDefinition(SaslRpcServer.AuthMethod.DIGEST.getMechanismName(), null, "default", saslProps, new SaslDigestCallbackHandler(this.secretManager));
            return new TUGIAssumingTransportFactory(transFactory, this.realUgi);
        }
        
        public TProcessor wrapProcessor(final TProcessor processor) {
            return new TUGIAssumingProcessor(processor, this.secretManager, true);
        }
        
        public TProcessor wrapNonAssumingProcessor(final TProcessor processor) {
            return new TUGIAssumingProcessor(processor, this.secretManager, false);
        }
        
        protected DelegationTokenStore getTokenStore(final Configuration conf) throws IOException {
            final String tokenStoreClassName = conf.get("hive.cluster.delegation.token.store.class", "");
            if (StringUtils.isBlank(tokenStoreClassName)) {
                return new MemoryTokenStore();
            }
            try {
                final Class<? extends DelegationTokenStore> storeClass = Class.forName(tokenStoreClassName).asSubclass(DelegationTokenStore.class);
                return ReflectionUtils.newInstance(storeClass, conf);
            }
            catch (ClassNotFoundException e) {
                throw new IOException("Error initializing delegation token store: " + tokenStoreClassName, e);
            }
        }
        
        public void startDelegationTokenSecretManager(final Configuration conf, final Object rawStore, final ServerMode smode) throws IOException {
            final long secretKeyInterval = conf.getLong("hive.cluster.delegation.key.update-interval", 86400000L);
            final long tokenMaxLifetime = conf.getLong("hive.cluster.delegation.token.max-lifetime", 604800000L);
            final long tokenRenewInterval = conf.getLong("hive.cluster.delegation.token.renew-interval", 86400000L);
            final long tokenGcInterval = conf.getLong("hive.cluster.delegation.token.gc-interval", 3600000L);
            final DelegationTokenStore dts = this.getTokenStore(conf);
            dts.init(rawStore, smode);
            (this.secretManager = new TokenStoreDelegationTokenSecretManager(secretKeyInterval, tokenMaxLifetime, tokenRenewInterval, tokenGcInterval, dts)).startThreads();
        }
        
        public String getDelegationToken(final String owner, final String renewer) throws IOException, InterruptedException {
            if (!Server.authenticationMethod.get().equals(UserGroupInformation.AuthenticationMethod.KERBEROS)) {
                throw new AuthorizationException("Delegation Token can be issued only with kerberos authentication. Current AuthenticationMethod: " + Server.authenticationMethod.get());
            }
            final UserGroupInformation currUser = UserGroupInformation.getCurrentUser();
            UserGroupInformation ownerUgi = UserGroupInformation.createRemoteUser(owner);
            if (!ownerUgi.getShortUserName().equals(currUser.getShortUserName())) {
                ownerUgi = UserGroupInformation.createProxyUser(owner, UserGroupInformation.getCurrentUser());
                final InetAddress remoteAddr = this.getRemoteAddress();
                ProxyUsers.authorize(ownerUgi, remoteAddr.getHostAddress(), null);
            }
            return ownerUgi.doAs((PrivilegedExceptionAction<String>)new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws IOException {
                    return Server.this.secretManager.getDelegationToken(renewer);
                }
            });
        }
        
        public String getDelegationTokenWithService(final String owner, final String renewer, final String service) throws IOException, InterruptedException {
            final String token = this.getDelegationToken(owner, renewer);
            return Utils.addServiceToToken(token, service);
        }
        
        public long renewDelegationToken(final String tokenStrForm) throws IOException {
            if (!Server.authenticationMethod.get().equals(UserGroupInformation.AuthenticationMethod.KERBEROS)) {
                throw new AuthorizationException("Delegation Token can be issued only with kerberos authentication. Current AuthenticationMethod: " + Server.authenticationMethod.get());
            }
            return this.secretManager.renewDelegationToken(tokenStrForm);
        }
        
        public String getUserFromToken(final String tokenStr) throws IOException {
            return this.secretManager.getUserFromToken(tokenStr);
        }
        
        public void cancelDelegationToken(final String tokenStrForm) throws IOException {
            this.secretManager.cancelDelegationToken(tokenStrForm);
        }
        
        public InetAddress getRemoteAddress() {
            return Server.remoteAddress.get();
        }
        
        public String getRemoteUser() {
            return Server.remoteUser.get();
        }
        
        static {
            remoteAddress = new ThreadLocal<InetAddress>() {
                @Override
                protected synchronized InetAddress initialValue() {
                    return null;
                }
            };
            authenticationMethod = new ThreadLocal<UserGroupInformation.AuthenticationMethod>() {
                @Override
                protected synchronized UserGroupInformation.AuthenticationMethod initialValue() {
                    return UserGroupInformation.AuthenticationMethod.TOKEN;
                }
            };
            Server.remoteUser = new ThreadLocal<String>() {
                @Override
                protected synchronized String initialValue() {
                    return null;
                }
            };
        }
        
        public enum ServerMode
        {
            HIVESERVER2, 
            METASTORE;
        }
        
        static class SaslDigestCallbackHandler implements CallbackHandler
        {
            private final DelegationTokenSecretManager secretManager;
            
            public SaslDigestCallbackHandler(final DelegationTokenSecretManager secretManager) {
                this.secretManager = secretManager;
            }
            
            private char[] getPassword(final DelegationTokenIdentifier tokenid) throws SecretManager.InvalidToken {
                return this.encodePassword(this.secretManager.retrievePassword(tokenid));
            }
            
            private char[] encodePassword(final byte[] password) {
                return new String(Base64.encodeBase64(password)).toCharArray();
            }
            
            @Override
            public void handle(final Callback[] callbacks) throws SecretManager.InvalidToken, UnsupportedCallbackException {
                NameCallback nc = null;
                PasswordCallback pc = null;
                AuthorizeCallback ac = null;
                for (final Callback callback : callbacks) {
                    if (callback instanceof AuthorizeCallback) {
                        ac = (AuthorizeCallback)callback;
                    }
                    else if (callback instanceof NameCallback) {
                        nc = (NameCallback)callback;
                    }
                    else if (callback instanceof PasswordCallback) {
                        pc = (PasswordCallback)callback;
                    }
                    else if (!(callback instanceof RealmCallback)) {
                        throw new UnsupportedCallbackException(callback, "Unrecognized SASL DIGEST-MD5 Callback");
                    }
                }
                if (pc != null) {
                    final DelegationTokenIdentifier tokenIdentifier = SaslRpcServer.getIdentifier(nc.getDefaultName(), (SecretManager<DelegationTokenIdentifier>)this.secretManager);
                    final char[] password = this.getPassword(tokenIdentifier);
                    if (HadoopThriftAuthBridge.LOG.isDebugEnabled()) {
                        HadoopThriftAuthBridge.LOG.debug("SASL server DIGEST-MD5 callback: setting password for client: " + tokenIdentifier.getUser());
                    }
                    pc.setPassword(password);
                }
                if (ac != null) {
                    final String authid = ac.getAuthenticationID();
                    final String authzid = ac.getAuthorizationID();
                    if (authid.equals(authzid)) {
                        ac.setAuthorized(true);
                    }
                    else {
                        ac.setAuthorized(false);
                    }
                    if (ac.isAuthorized()) {
                        if (HadoopThriftAuthBridge.LOG.isDebugEnabled()) {
                            final String username = SaslRpcServer.getIdentifier(authzid, (SecretManager<DelegationTokenIdentifier>)this.secretManager).getUser().getUserName();
                            HadoopThriftAuthBridge.LOG.debug("SASL server DIGEST-MD5 callback: setting canonicalized client ID: " + username);
                        }
                        ac.setAuthorizedID(authzid);
                    }
                }
            }
        }
        
        protected class TUGIAssumingProcessor implements TProcessor
        {
            final TProcessor wrapped;
            DelegationTokenSecretManager secretManager;
            boolean useProxy;
            
            TUGIAssumingProcessor(final TProcessor wrapped, final DelegationTokenSecretManager secretManager, final boolean useProxy) {
                this.wrapped = wrapped;
                this.secretManager = secretManager;
                this.useProxy = useProxy;
            }
            
            @Override
            public boolean process(final TProtocol inProt, final TProtocol outProt) throws TException {
                final TTransport trans = inProt.getTransport();
                if (!(trans instanceof TSaslServerTransport)) {
                    throw new TException("Unexpected non-SASL transport " + trans.getClass());
                }
                final TSaslServerTransport saslTrans = (TSaslServerTransport)trans;
                final SaslServer saslServer = saslTrans.getSaslServer();
                final String authId = saslServer.getAuthorizationID();
                Server.authenticationMethod.set(UserGroupInformation.AuthenticationMethod.KERBEROS);
                HadoopThriftAuthBridge.LOG.debug("AUTH ID ======>" + authId);
                String endUser = authId;
                if (saslServer.getMechanismName().equals("DIGEST-MD5")) {
                    try {
                        final TokenIdentifier tokenId = SaslRpcServer.getIdentifier(authId, (SecretManager<TokenIdentifier>)this.secretManager);
                        endUser = tokenId.getUser().getUserName();
                        Server.authenticationMethod.set(UserGroupInformation.AuthenticationMethod.TOKEN);
                    }
                    catch (SecretManager.InvalidToken e) {
                        throw new TException(e.getMessage());
                    }
                }
                final Socket socket = ((TSocket)saslTrans.getUnderlyingTransport()).getSocket();
                Server.remoteAddress.set(socket.getInetAddress());
                UserGroupInformation clientUgi = null;
                try {
                    if (this.useProxy) {
                        clientUgi = UserGroupInformation.createProxyUser(endUser, UserGroupInformation.getLoginUser());
                        Server.remoteUser.set(clientUgi.getShortUserName());
                        HadoopThriftAuthBridge.LOG.debug("Set remoteUser :" + Server.remoteUser.get());
                        return clientUgi.doAs((PrivilegedExceptionAction<Boolean>)new PrivilegedExceptionAction<Boolean>() {
                            @Override
                            public Boolean run() {
                                try {
                                    return TUGIAssumingProcessor.this.wrapped.process(inProt, outProt);
                                }
                                catch (TException te) {
                                    throw new RuntimeException(te);
                                }
                            }
                        });
                    }
                    final UserGroupInformation endUserUgi = UserGroupInformation.createRemoteUser(endUser);
                    Server.remoteUser.set(endUserUgi.getShortUserName());
                    HadoopThriftAuthBridge.LOG.debug("Set remoteUser :" + Server.remoteUser.get() + ", from endUser :" + endUser);
                    return this.wrapped.process(inProt, outProt);
                }
                catch (RuntimeException rte) {
                    if (rte.getCause() instanceof TException) {
                        throw (TException)rte.getCause();
                    }
                    throw rte;
                }
                catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
                catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
                finally {
                    if (clientUgi != null) {
                        try {
                            FileSystem.closeAllForUGI(clientUgi);
                        }
                        catch (IOException exception) {
                            HadoopThriftAuthBridge.LOG.error("Could not clean up file-system handles for UGI: " + clientUgi, exception);
                        }
                    }
                }
            }
        }
        
        static class TUGIAssumingTransportFactory extends TTransportFactory
        {
            private final UserGroupInformation ugi;
            private final TTransportFactory wrapped;
            
            public TUGIAssumingTransportFactory(final TTransportFactory wrapped, final UserGroupInformation ugi) {
                assert wrapped != null;
                assert ugi != null;
                this.wrapped = wrapped;
                this.ugi = ugi;
            }
            
            @Override
            public TTransport getTransport(final TTransport trans) {
                return this.ugi.doAs((PrivilegedAction<TTransport>)new PrivilegedAction<TTransport>() {
                    @Override
                    public TTransport run() {
                        return TUGIAssumingTransportFactory.this.wrapped.getTransport(trans);
                    }
                });
            }
        }
    }
}
