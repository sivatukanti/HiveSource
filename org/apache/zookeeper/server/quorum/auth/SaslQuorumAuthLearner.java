// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum.auth;

import org.slf4j.LoggerFactory;
import java.security.PrivilegedActionException;
import javax.security.auth.Subject;
import java.security.PrivilegedExceptionAction;
import org.apache.jute.Record;
import org.apache.jute.BinaryOutputArchive;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import org.apache.jute.InputArchive;
import java.io.InputStream;
import org.apache.jute.BinaryInputArchive;
import java.io.IOException;
import org.apache.zookeeper.server.quorum.QuorumAuthPacket;
import javax.security.sasl.SaslClient;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.apache.zookeeper.util.SecurityUtils;
import java.net.Socket;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.sasl.SaslException;
import javax.security.auth.callback.CallbackHandler;
import org.apache.zookeeper.SaslClientCallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.Configuration;
import org.apache.zookeeper.Login;
import org.slf4j.Logger;

public class SaslQuorumAuthLearner implements QuorumAuthLearner
{
    private static final Logger LOG;
    private final Login learnerLogin;
    private final boolean quorumRequireSasl;
    private final String quorumServicePrincipal;
    
    public SaslQuorumAuthLearner(final boolean quorumRequireSasl, final String quorumServicePrincipal, final String loginContext) throws SaslException {
        this.quorumRequireSasl = quorumRequireSasl;
        this.quorumServicePrincipal = quorumServicePrincipal;
        try {
            final AppConfigurationEntry[] entries = Configuration.getConfiguration().getAppConfigurationEntry(loginContext);
            if (entries == null || entries.length == 0) {
                throw new LoginException("SASL-authentication failed because the specified JAAS configuration section '" + loginContext + "' could not be found.");
            }
            (this.learnerLogin = new Login(loginContext, new SaslClientCallbackHandler(null, "QuorumLearner"))).startThreadIfNeeded();
        }
        catch (LoginException e) {
            throw new SaslException("Failed to initialize authentication mechanism using SASL", e);
        }
    }
    
    @Override
    public void authenticate(final Socket sock, final String hostName) throws IOException {
        if (!this.quorumRequireSasl) {
            SaslQuorumAuthLearner.LOG.info("Skipping SASL authentication as {}={}", "quorum.auth.learnerRequireSasl", this.quorumRequireSasl);
            return;
        }
        SaslClient sc = null;
        final String principalConfig = SecurityUtils.getServerPrincipal(this.quorumServicePrincipal, hostName);
        try {
            final DataOutputStream dout = new DataOutputStream(sock.getOutputStream());
            final DataInputStream din = new DataInputStream(sock.getInputStream());
            byte[] responseToken = new byte[0];
            sc = SecurityUtils.createSaslClient(this.learnerLogin.getSubject(), principalConfig, "zookeeper-quorum", "zk-quorum-sasl-md5", SaslQuorumAuthLearner.LOG, "QuorumLearner");
            if (sc.hasInitialResponse()) {
                responseToken = this.createSaslToken(new byte[0], sc, this.learnerLogin);
            }
            this.send(dout, responseToken);
            QuorumAuthPacket authPacket = this.receive(din);
            QuorumAuth.Status qpStatus = QuorumAuth.Status.getStatus(authPacket.getStatus());
            while (!sc.isComplete()) {
                switch (qpStatus) {
                    case SUCCESS: {
                        responseToken = this.createSaslToken(authPacket.getToken(), sc, this.learnerLogin);
                        if (responseToken != null) {
                            throw new SaslException("Protocol error: attempting to send response after completion. Server addr: " + sock.getRemoteSocketAddress());
                        }
                        continue;
                    }
                    case IN_PROGRESS: {
                        responseToken = this.createSaslToken(authPacket.getToken(), sc, this.learnerLogin);
                        this.send(dout, responseToken);
                        authPacket = this.receive(din);
                        qpStatus = QuorumAuth.Status.getStatus(authPacket.getStatus());
                        continue;
                    }
                    case ERROR: {
                        throw new SaslException("Authentication failed against server addr: " + sock.getRemoteSocketAddress());
                    }
                    default: {
                        SaslQuorumAuthLearner.LOG.warn("Unknown status:{}!", qpStatus);
                        throw new SaslException("Authentication failed against server addr: " + sock.getRemoteSocketAddress());
                    }
                }
            }
            this.checkAuthStatus(sock, qpStatus);
        }
        finally {
            if (sc != null) {
                try {
                    sc.dispose();
                }
                catch (SaslException e) {
                    SaslQuorumAuthLearner.LOG.error("SaslClient dispose() failed", e);
                }
            }
        }
    }
    
    private void checkAuthStatus(final Socket sock, final QuorumAuth.Status qpStatus) throws SaslException {
        if (qpStatus == QuorumAuth.Status.SUCCESS) {
            SaslQuorumAuthLearner.LOG.info("Successfully completed the authentication using SASL. server addr: {}, status: {}", sock.getRemoteSocketAddress(), qpStatus);
            return;
        }
        throw new SaslException("Authentication failed against server addr: " + sock.getRemoteSocketAddress() + ", qpStatus: " + qpStatus);
    }
    
    private QuorumAuthPacket receive(final DataInputStream din) throws IOException {
        final QuorumAuthPacket authPacket = new QuorumAuthPacket();
        final BinaryInputArchive bia = BinaryInputArchive.getArchive(din);
        authPacket.deserialize(bia, "qpconnect");
        return authPacket;
    }
    
    private void send(final DataOutputStream dout, final byte[] response) throws IOException {
        final BufferedOutputStream bufferedOutput = new BufferedOutputStream(dout);
        final BinaryOutputArchive boa = BinaryOutputArchive.getArchive(bufferedOutput);
        final QuorumAuthPacket authPacket = QuorumAuth.createPacket(QuorumAuth.Status.IN_PROGRESS, response);
        boa.writeRecord(authPacket, "qpconnect");
        bufferedOutput.flush();
    }
    
    private byte[] createSaslToken(final byte[] saslToken, final SaslClient saslClient, final Login login) throws SaslException {
        if (saslToken == null) {
            throw new SaslException("Error in authenticating with a Zookeeper Quorum member: the quorum member's saslToken is null.");
        }
        if (login.getSubject() != null) {
            synchronized (login) {
                try {
                    final byte[] retval = Subject.doAs(login.getSubject(), (PrivilegedExceptionAction<byte[]>)new PrivilegedExceptionAction<byte[]>() {
                        @Override
                        public byte[] run() throws SaslException {
                            SaslQuorumAuthLearner.LOG.debug("saslClient.evaluateChallenge(len=" + saslToken.length + ")");
                            return saslClient.evaluateChallenge(saslToken);
                        }
                    });
                    return retval;
                }
                catch (PrivilegedActionException e) {
                    String error = "An error: (" + e + ") occurred when evaluating Zookeeper Quorum Member's  received SASL token.";
                    final String UNKNOWN_SERVER_ERROR_TEXT = "(Mechanism level: Server not found in Kerberos database (7) - UNKNOWN_SERVER)";
                    if (e.toString().indexOf("(Mechanism level: Server not found in Kerberos database (7) - UNKNOWN_SERVER)") > -1) {
                        error += " This may be caused by Java's being unable to resolve the Zookeeper Quorum Member's hostname correctly. You may want to try to adding '-Dsun.net.spi.nameservice.provider.1=dns,sun' to your server's JVMFLAGS environment.";
                    }
                    SaslQuorumAuthLearner.LOG.error(error);
                    throw new SaslException(error);
                }
            }
        }
        throw new SaslException("Cannot make SASL token without subject defined. For diagnosis, please look for WARNs and ERRORs in your log related to the Login class.");
    }
    
    static {
        LOG = LoggerFactory.getLogger(SaslQuorumAuthLearner.class);
    }
}
