// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.ArrayList;
import javax.security.sasl.Sasl;
import java.util.HashMap;
import java.util.List;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.sasl.AuthorizeCallback;
import javax.security.auth.callback.Callback;
import org.apache.hadoop.ipc.RetriableException;
import org.apache.hadoop.ipc.StandbyException;
import java.io.DataOutput;
import org.slf4j.LoggerFactory;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import java.security.Provider;
import java.security.Security;
import org.apache.hadoop.conf.Configuration;
import javax.security.sasl.SaslException;
import javax.security.auth.callback.CallbackHandler;
import java.security.PrivilegedExceptionAction;
import javax.security.sasl.SaslServer;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import java.util.Map;
import org.apache.hadoop.ipc.Server;
import java.io.IOException;
import javax.security.sasl.SaslServerFactory;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class SaslRpcServer
{
    public static final Logger LOG;
    public static final String SASL_DEFAULT_REALM = "default";
    private static SaslServerFactory saslFactory;
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public AuthMethod authMethod;
    public String mechanism;
    public String protocol;
    public String serverId;
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public SaslRpcServer(final AuthMethod authMethod) throws IOException {
        this.authMethod = authMethod;
        this.mechanism = authMethod.getMechanismName();
        switch (authMethod) {
            case SIMPLE: {}
            case TOKEN: {
                this.protocol = "";
                this.serverId = "default";
                break;
            }
            case KERBEROS: {
                final String fullName = UserGroupInformation.getCurrentUser().getUserName();
                if (SaslRpcServer.LOG.isDebugEnabled()) {
                    SaslRpcServer.LOG.debug("Kerberos principal name is " + fullName);
                }
                final String[] parts = fullName.split("[/@]", 3);
                this.protocol = parts[0];
                this.serverId = ((parts.length < 2) ? "" : parts[1]);
                break;
            }
            default: {
                throw new AccessControlException("Server does not support SASL " + authMethod);
            }
        }
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public SaslServer create(final Server.Connection connection, final Map<String, ?> saslProperties, final SecretManager<TokenIdentifier> secretManager) throws IOException, InterruptedException {
        UserGroupInformation ugi = null;
        CallbackHandler callback = null;
        switch (this.authMethod) {
            case TOKEN: {
                callback = new SaslDigestCallbackHandler(secretManager, connection);
                break;
            }
            case KERBEROS: {
                ugi = UserGroupInformation.getCurrentUser();
                if (this.serverId.isEmpty()) {
                    throw new AccessControlException("Kerberos principal name does NOT have the expected hostname part: " + ugi.getUserName());
                }
                callback = new SaslGssCallbackHandler();
                break;
            }
            default: {
                throw new AccessControlException("Server does not support SASL " + this.authMethod);
            }
        }
        SaslServer saslServer;
        if (ugi != null) {
            saslServer = ugi.doAs((PrivilegedExceptionAction<SaslServer>)new PrivilegedExceptionAction<SaslServer>() {
                @Override
                public SaslServer run() throws SaslException {
                    return SaslRpcServer.saslFactory.createSaslServer(SaslRpcServer.this.mechanism, SaslRpcServer.this.protocol, SaslRpcServer.this.serverId, saslProperties, callback);
                }
            });
        }
        else {
            saslServer = SaslRpcServer.saslFactory.createSaslServer(this.mechanism, this.protocol, this.serverId, saslProperties, callback);
        }
        if (saslServer == null) {
            throw new AccessControlException("Unable to find SASL server implementation for " + this.mechanism);
        }
        if (SaslRpcServer.LOG.isDebugEnabled()) {
            SaslRpcServer.LOG.debug("Created SASL server with mechanism = " + this.mechanism);
        }
        return saslServer;
    }
    
    public static void init(final Configuration conf) {
        Security.addProvider(new SaslPlainServer.SecurityProvider());
        SaslRpcServer.saslFactory = new FastSaslServerFactory(null);
    }
    
    static String encodeIdentifier(final byte[] identifier) {
        return new String(Base64.encodeBase64(identifier), StandardCharsets.UTF_8);
    }
    
    static byte[] decodeIdentifier(final String identifier) {
        return Base64.decodeBase64(identifier.getBytes(StandardCharsets.UTF_8));
    }
    
    public static <T extends TokenIdentifier> T getIdentifier(final String id, final SecretManager<T> secretManager) throws SecretManager.InvalidToken {
        final byte[] tokenId = decodeIdentifier(id);
        final T tokenIdentifier = secretManager.createIdentifier();
        try {
            tokenIdentifier.readFields(new DataInputStream(new ByteArrayInputStream(tokenId)));
        }
        catch (IOException e) {
            throw (SecretManager.InvalidToken)new SecretManager.InvalidToken("Can't de-serialize tokenIdentifier").initCause(e);
        }
        return tokenIdentifier;
    }
    
    static char[] encodePassword(final byte[] password) {
        return new String(Base64.encodeBase64(password), StandardCharsets.UTF_8).toCharArray();
    }
    
    public static String[] splitKerberosName(final String fullName) {
        return fullName.split("[/@]");
    }
    
    static {
        LOG = LoggerFactory.getLogger(SaslRpcServer.class);
    }
    
    public enum QualityOfProtection
    {
        AUTHENTICATION("auth"), 
        INTEGRITY("auth-int"), 
        PRIVACY("auth-conf");
        
        public final String saslQop;
        
        private QualityOfProtection(final String saslQop) {
            this.saslQop = saslQop;
        }
        
        public String getSaslQop() {
            return this.saslQop;
        }
    }
    
    @InterfaceStability.Evolving
    public enum AuthMethod
    {
        SIMPLE((byte)80, ""), 
        KERBEROS((byte)81, "GSSAPI"), 
        @Deprecated
        DIGEST((byte)82, "DIGEST-MD5"), 
        TOKEN((byte)82, "DIGEST-MD5"), 
        PLAIN((byte)83, "PLAIN");
        
        public final byte code;
        public final String mechanismName;
        private static final int FIRST_CODE;
        
        private AuthMethod(final byte code, final String mechanismName) {
            this.code = code;
            this.mechanismName = mechanismName;
        }
        
        private static AuthMethod valueOf(final byte code) {
            final int i = (code & 0xFF) - AuthMethod.FIRST_CODE;
            return (i < 0 || i >= values().length) ? null : values()[i];
        }
        
        public String getMechanismName() {
            return this.mechanismName;
        }
        
        public static AuthMethod read(final DataInput in) throws IOException {
            return valueOf(in.readByte());
        }
        
        public void write(final DataOutput out) throws IOException {
            out.write(this.code);
        }
        
        static {
            FIRST_CODE = values()[0].code;
        }
    }
    
    @InterfaceStability.Evolving
    public static class SaslDigestCallbackHandler implements CallbackHandler
    {
        private SecretManager<TokenIdentifier> secretManager;
        private Server.Connection connection;
        
        public SaslDigestCallbackHandler(final SecretManager<TokenIdentifier> secretManager, final Server.Connection connection) {
            this.secretManager = secretManager;
            this.connection = connection;
        }
        
        private char[] getPassword(final TokenIdentifier tokenid) throws SecretManager.InvalidToken, StandbyException, RetriableException, IOException {
            return SaslRpcServer.encodePassword(this.secretManager.retriableRetrievePassword(tokenid));
        }
        
        @Override
        public void handle(final Callback[] callbacks) throws SecretManager.InvalidToken, UnsupportedCallbackException, StandbyException, RetriableException, IOException {
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
                final TokenIdentifier tokenIdentifier = SaslRpcServer.getIdentifier(nc.getDefaultName(), this.secretManager);
                final char[] password = this.getPassword(tokenIdentifier);
                UserGroupInformation user = null;
                user = tokenIdentifier.getUser();
                this.connection.attemptingUser = user;
                if (SaslRpcServer.LOG.isDebugEnabled()) {
                    SaslRpcServer.LOG.debug("SASL server DIGEST-MD5 callback: setting password for client: " + tokenIdentifier.getUser());
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
                    if (SaslRpcServer.LOG.isDebugEnabled()) {
                        final UserGroupInformation logUser = SaslRpcServer.getIdentifier(authzid, this.secretManager).getUser();
                        final String username = (logUser == null) ? null : logUser.getUserName();
                        SaslRpcServer.LOG.debug("SASL server DIGEST-MD5 callback: setting canonicalized client ID: " + username);
                    }
                    ac.setAuthorizedID(authzid);
                }
            }
        }
    }
    
    @InterfaceStability.Evolving
    public static class SaslGssCallbackHandler implements CallbackHandler
    {
        @Override
        public void handle(final Callback[] callbacks) throws UnsupportedCallbackException {
            AuthorizeCallback ac = null;
            for (final Callback callback : callbacks) {
                if (!(callback instanceof AuthorizeCallback)) {
                    throw new UnsupportedCallbackException(callback, "Unrecognized SASL GSSAPI Callback");
                }
                ac = (AuthorizeCallback)callback;
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
                    if (SaslRpcServer.LOG.isDebugEnabled()) {
                        SaslRpcServer.LOG.debug("SASL server GSSAPI callback: setting canonicalized client ID: " + authzid);
                    }
                    ac.setAuthorizedID(authzid);
                }
            }
        }
    }
    
    private static class FastSaslServerFactory implements SaslServerFactory
    {
        private final Map<String, List<SaslServerFactory>> factoryCache;
        
        FastSaslServerFactory(final Map<String, ?> props) {
            this.factoryCache = new HashMap<String, List<SaslServerFactory>>();
            final Enumeration<SaslServerFactory> factories = Sasl.getSaslServerFactories();
            while (factories.hasMoreElements()) {
                final SaslServerFactory factory = factories.nextElement();
                for (final String mech : factory.getMechanismNames(props)) {
                    if (!this.factoryCache.containsKey(mech)) {
                        this.factoryCache.put(mech, new ArrayList<SaslServerFactory>());
                    }
                    this.factoryCache.get(mech).add(factory);
                }
            }
        }
        
        @Override
        public SaslServer createSaslServer(final String mechanism, final String protocol, final String serverName, final Map<String, ?> props, final CallbackHandler cbh) throws SaslException {
            SaslServer saslServer = null;
            final List<SaslServerFactory> factories = this.factoryCache.get(mechanism);
            if (factories != null) {
                for (final SaslServerFactory factory : factories) {
                    saslServer = factory.createSaslServer(mechanism, protocol, serverName, props, cbh);
                    if (saslServer != null) {
                        break;
                    }
                }
            }
            return saslServer;
        }
        
        @Override
        public String[] getMechanismNames(final Map<String, ?> props) {
            return this.factoryCache.keySet().toArray(new String[0]);
        }
    }
}
