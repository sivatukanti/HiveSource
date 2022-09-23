// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.client;

import org.apache.zookeeper.data.Stat;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.Watcher;
import java.io.IOException;
import org.apache.zookeeper.AsyncCallback;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.SetSASLResponse;
import org.apache.zookeeper.proto.GetSASLRequest;
import java.security.PrivilegedActionException;
import javax.security.auth.Subject;
import java.security.PrivilegedExceptionAction;
import javax.security.sasl.SaslException;
import org.apache.zookeeper.ClientCnxn;
import org.apache.zookeeper.util.SecurityUtils;
import javax.security.auth.callback.CallbackHandler;
import org.apache.zookeeper.SaslClientCallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.Configuration;
import javax.security.sasl.SaslClient;
import org.apache.zookeeper.Login;
import org.slf4j.Logger;

public class ZooKeeperSaslClient
{
    public static final String LOGIN_CONTEXT_NAME_KEY = "zookeeper.sasl.clientconfig";
    public static final String ENABLE_CLIENT_SASL_KEY = "zookeeper.sasl.client";
    public static final String ENABLE_CLIENT_SASL_DEFAULT = "true";
    private static volatile boolean initializedLogin;
    private static final Logger LOG;
    private static Login login;
    private SaslClient saslClient;
    private boolean isSASLConfigured;
    private byte[] saslToken;
    private SaslState saslState;
    private boolean gotLastPacket;
    private final String configStatus;
    
    public static boolean isEnabled() {
        return Boolean.valueOf(System.getProperty("zookeeper.sasl.client", "true"));
    }
    
    public SaslState getSaslState() {
        return this.saslState;
    }
    
    public String getLoginContext() {
        if (ZooKeeperSaslClient.login != null) {
            return ZooKeeperSaslClient.login.getLoginContextName();
        }
        return null;
    }
    
    public ZooKeeperSaslClient(final String serverPrincipal) throws LoginException {
        this.isSASLConfigured = true;
        this.saslToken = new byte[0];
        this.saslState = SaslState.INITIAL;
        this.gotLastPacket = false;
        final String clientSection = System.getProperty("zookeeper.sasl.clientconfig", "Client");
        AppConfigurationEntry[] entries = null;
        RuntimeException runtimeException = null;
        try {
            entries = Configuration.getConfiguration().getAppConfigurationEntry(clientSection);
        }
        catch (SecurityException e) {
            runtimeException = e;
        }
        catch (IllegalArgumentException e2) {
            runtimeException = e2;
        }
        if (entries != null) {
            this.configStatus = "Will attempt to SASL-authenticate using Login Context section '" + clientSection + "'";
            this.saslClient = this.createSaslClient(serverPrincipal, clientSection);
        }
        else {
            this.saslState = SaslState.FAILED;
            final String explicitClientSection = System.getProperty("zookeeper.sasl.clientconfig");
            if (explicitClientSection != null) {
                if (runtimeException != null) {
                    throw new LoginException("Zookeeper client cannot authenticate using the " + explicitClientSection + " section of the supplied JAAS configuration: '" + System.getProperty("java.security.auth.login.config") + "' because of a RuntimeException: " + runtimeException);
                }
                throw new LoginException("Client cannot SASL-authenticate because the specified JAAS configuration section '" + explicitClientSection + "' could not be found.");
            }
            else {
                String msg = "Will not attempt to authenticate using SASL ";
                if (runtimeException != null) {
                    msg = msg + "(" + runtimeException + ")";
                }
                else {
                    msg += "(unknown error)";
                }
                this.configStatus = msg;
                this.isSASLConfigured = false;
                if (System.getProperty("java.security.auth.login.config") != null) {
                    if (runtimeException != null) {
                        throw new LoginException("Zookeeper client cannot authenticate using the '" + System.getProperty("zookeeper.sasl.clientconfig", "Client") + "' section of the supplied JAAS configuration: '" + System.getProperty("java.security.auth.login.config") + "' because of a RuntimeException: " + runtimeException);
                    }
                    throw new LoginException("No JAAS configuration section named '" + System.getProperty("zookeeper.sasl.clientconfig", "Client") + "' was found in specified JAAS configuration file: '" + System.getProperty("java.security.auth.login.config") + "'.");
                }
            }
        }
    }
    
    public String getConfigStatus() {
        return this.configStatus;
    }
    
    public boolean isComplete() {
        return this.saslState == SaslState.COMPLETE;
    }
    
    public boolean isFailed() {
        return this.saslState == SaslState.FAILED;
    }
    
    private SaslClient createSaslClient(final String servicePrincipal, final String loginContext) throws LoginException {
        try {
            if (!ZooKeeperSaslClient.initializedLogin) {
                synchronized (ZooKeeperSaslClient.class) {
                    if (ZooKeeperSaslClient.login == null) {
                        if (ZooKeeperSaslClient.LOG.isDebugEnabled()) {
                            ZooKeeperSaslClient.LOG.debug("JAAS loginContext is: " + loginContext);
                        }
                        (ZooKeeperSaslClient.login = new Login(loginContext, new SaslClientCallbackHandler(null, "Client"))).startThreadIfNeeded();
                        ZooKeeperSaslClient.initializedLogin = true;
                    }
                }
            }
            return SecurityUtils.createSaslClient(ZooKeeperSaslClient.login.getSubject(), servicePrincipal, "zookeeper", "zk-sasl-md5", ZooKeeperSaslClient.LOG, "Client");
        }
        catch (LoginException e) {
            throw e;
        }
        catch (Exception e2) {
            ZooKeeperSaslClient.LOG.error("Exception while trying to create SASL client: " + e2);
            return null;
        }
    }
    
    public void respondToServer(final byte[] serverToken, final ClientCnxn cnxn) {
        if (this.saslClient == null) {
            ZooKeeperSaslClient.LOG.error("saslClient is unexpectedly null. Cannot respond to server's SASL message; ignoring.");
            return;
        }
        if (!this.saslClient.isComplete()) {
            try {
                this.saslToken = this.createSaslToken(serverToken);
                if (this.saslToken != null) {
                    this.sendSaslPacket(this.saslToken, cnxn);
                }
            }
            catch (SaslException e) {
                ZooKeeperSaslClient.LOG.error("SASL authentication failed using login context '" + this.getLoginContext() + "' with exception: {}", e);
                this.saslState = SaslState.FAILED;
                this.gotLastPacket = true;
            }
        }
        if (this.saslClient.isComplete()) {
            if (serverToken == null && this.saslClient.getMechanismName().equals("GSSAPI")) {
                this.gotLastPacket = true;
            }
            if (!this.saslClient.getMechanismName().equals("GSSAPI")) {
                this.gotLastPacket = true;
            }
            cnxn.enableWrite();
        }
    }
    
    private byte[] createSaslToken() throws SaslException {
        this.saslState = SaslState.INTERMEDIATE;
        return this.createSaslToken(this.saslToken);
    }
    
    private byte[] createSaslToken(final byte[] saslToken) throws SaslException {
        if (saslToken == null) {
            this.saslState = SaslState.FAILED;
            throw new SaslException("Error in authenticating with a Zookeeper Quorum member: the quorum member's saslToken is null.");
        }
        final Subject subject = ZooKeeperSaslClient.login.getSubject();
        if (subject != null) {
            synchronized (ZooKeeperSaslClient.login) {
                try {
                    final byte[] retval = Subject.doAs(subject, (PrivilegedExceptionAction<byte[]>)new PrivilegedExceptionAction<byte[]>() {
                        @Override
                        public byte[] run() throws SaslException {
                            ZooKeeperSaslClient.LOG.debug("saslClient.evaluateChallenge(len=" + saslToken.length + ")");
                            return ZooKeeperSaslClient.this.saslClient.evaluateChallenge(saslToken);
                        }
                    });
                    return retval;
                }
                catch (PrivilegedActionException e) {
                    String error = "An error: (" + e + ") occurred when evaluating Zookeeper Quorum Member's  received SASL token.";
                    final String UNKNOWN_SERVER_ERROR_TEXT = "(Mechanism level: Server not found in Kerberos database (7) - UNKNOWN_SERVER)";
                    if (e.toString().indexOf("(Mechanism level: Server not found in Kerberos database (7) - UNKNOWN_SERVER)") > -1) {
                        error += " This may be caused by Java's being unable to resolve the Zookeeper Quorum Member's hostname correctly. You may want to try to adding '-Dsun.net.spi.nameservice.provider.1=dns,sun' to your client's JVMFLAGS environment.";
                    }
                    error += " Zookeeper Client will go to AUTH_FAILED state.";
                    ZooKeeperSaslClient.LOG.error(error);
                    this.saslState = SaslState.FAILED;
                    throw new SaslException(error);
                }
            }
        }
        throw new SaslException("Cannot make SASL token without subject defined. For diagnosis, please look for WARNs and ERRORs in your log related to the Login class.");
    }
    
    private void sendSaslPacket(final byte[] saslToken, final ClientCnxn cnxn) throws SaslException {
        if (ZooKeeperSaslClient.LOG.isDebugEnabled()) {
            ZooKeeperSaslClient.LOG.debug("ClientCnxn:sendSaslPacket:length=" + saslToken.length);
        }
        final GetSASLRequest request = new GetSASLRequest();
        request.setToken(saslToken);
        final SetSASLResponse response = new SetSASLResponse();
        final ServerSaslResponseCallback cb = new ServerSaslResponseCallback();
        try {
            cnxn.sendPacket(request, response, cb, 102);
        }
        catch (IOException e) {
            throw new SaslException("Failed to send SASL packet to server.", e);
        }
    }
    
    private void sendSaslPacket(final ClientCnxn cnxn) throws SaslException {
        if (ZooKeeperSaslClient.LOG.isDebugEnabled()) {
            ZooKeeperSaslClient.LOG.debug("ClientCnxn:sendSaslPacket:length=" + this.saslToken.length);
        }
        final GetSASLRequest request = new GetSASLRequest();
        request.setToken(this.createSaslToken());
        final SetSASLResponse response = new SetSASLResponse();
        final ServerSaslResponseCallback cb = new ServerSaslResponseCallback();
        try {
            cnxn.sendPacket(request, response, cb, 102);
        }
        catch (IOException e) {
            throw new SaslException("Failed to send SASL packet to server due to IOException:", e);
        }
    }
    
    public Watcher.Event.KeeperState getKeeperState() {
        if (this.saslClient != null) {
            if (this.saslState == SaslState.FAILED) {
                return Watcher.Event.KeeperState.AuthFailed;
            }
            if (this.saslClient.isComplete() && this.saslState == SaslState.INTERMEDIATE) {
                this.saslState = SaslState.COMPLETE;
                return Watcher.Event.KeeperState.SaslAuthenticated;
            }
        }
        return null;
    }
    
    public void initialize(final ClientCnxn cnxn) throws SaslException {
        if (this.saslClient == null) {
            this.saslState = SaslState.FAILED;
            throw new SaslException("saslClient failed to initialize properly: it's null.");
        }
        if (this.saslState == SaslState.INITIAL) {
            if (this.saslClient.hasInitialResponse()) {
                this.sendSaslPacket(cnxn);
            }
            else {
                final byte[] emptyToken = new byte[0];
                this.sendSaslPacket(emptyToken, cnxn);
            }
            this.saslState = SaslState.INTERMEDIATE;
        }
    }
    
    public boolean clientTunneledAuthenticationInProgress() {
        if (!this.isSASLConfigured) {
            return false;
        }
        try {
            if (System.getProperty("java.security.auth.login.config") != null || (Configuration.getConfiguration() != null && Configuration.getConfiguration().getAppConfigurationEntry(System.getProperty("zookeeper.sasl.clientconfig", "Client")) != null)) {
                if (!this.isComplete() && !this.isFailed()) {
                    return true;
                }
                if ((this.isComplete() || this.isFailed()) && !this.gotLastPacket) {
                    return true;
                }
            }
            return false;
        }
        catch (SecurityException e) {
            if (ZooKeeperSaslClient.LOG.isDebugEnabled()) {
                ZooKeeperSaslClient.LOG.debug("Could not retrieve login configuration: " + e);
            }
            return false;
        }
    }
    
    static {
        ZooKeeperSaslClient.initializedLogin = false;
        LOG = LoggerFactory.getLogger(ZooKeeperSaslClient.class);
        ZooKeeperSaslClient.login = null;
    }
    
    public enum SaslState
    {
        INITIAL, 
        INTERMEDIATE, 
        COMPLETE, 
        FAILED;
    }
    
    public static class ServerSaslResponseCallback implements AsyncCallback.DataCallback
    {
        @Override
        public void processResult(final int rc, final String path, final Object ctx, final byte[] data, final Stat stat) {
            final ZooKeeperSaslClient client = ((ClientCnxn)ctx).zooKeeperSaslClient;
            if (client == null) {
                ZooKeeperSaslClient.LOG.warn("sasl client was unexpectedly null: cannot respond to Zookeeper server.");
                return;
            }
            byte[] usedata;
            if ((usedata = data) != null) {
                ZooKeeperSaslClient.LOG.debug("ServerSaslResponseCallback(): saslToken server response: (length=" + usedata.length + ")");
            }
            else {
                usedata = new byte[0];
                ZooKeeperSaslClient.LOG.debug("ServerSaslResponseCallback(): using empty data[] as server response (length=" + usedata.length + ")");
            }
            client.respondToServer(usedata, (ClientCnxn)ctx);
        }
    }
}
