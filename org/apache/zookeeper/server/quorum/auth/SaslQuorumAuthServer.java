// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum.auth;

import org.slf4j.LoggerFactory;
import org.apache.jute.Record;
import org.apache.jute.BinaryOutputArchive;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import org.apache.jute.InputArchive;
import java.io.InputStream;
import org.apache.jute.BinaryInputArchive;
import org.apache.zookeeper.server.quorum.QuorumAuthPacket;
import javax.security.sasl.SaslServer;
import java.io.IOException;
import org.apache.zookeeper.util.SecurityUtils;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.Socket;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.sasl.SaslException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.login.Configuration;
import java.util.Set;
import org.apache.zookeeper.Login;
import org.slf4j.Logger;

public class SaslQuorumAuthServer implements QuorumAuthServer
{
    private static final Logger LOG;
    private static final int MAX_RETRIES = 5;
    private final Login serverLogin;
    private final boolean quorumRequireSasl;
    
    public SaslQuorumAuthServer(final boolean quorumRequireSasl, final String loginContext, final Set<String> authzHosts) throws SaslException {
        this.quorumRequireSasl = quorumRequireSasl;
        try {
            final AppConfigurationEntry[] entries = Configuration.getConfiguration().getAppConfigurationEntry(loginContext);
            if (entries == null || entries.length == 0) {
                throw new LoginException("SASL-authentication failed because the specified JAAS configuration section '" + loginContext + "' could not be found.");
            }
            final SaslQuorumServerCallbackHandler saslServerCallbackHandler = new SaslQuorumServerCallbackHandler(Configuration.getConfiguration(), loginContext, authzHosts);
            (this.serverLogin = new Login(loginContext, saslServerCallbackHandler)).startThreadIfNeeded();
        }
        catch (Throwable e) {
            throw new SaslException("Failed to initialize authentication mechanism using SASL", e);
        }
    }
    
    @Override
    public void authenticate(final Socket sock, final DataInputStream din) throws SaslException {
        DataOutputStream dout = null;
        SaslServer ss = null;
        try {
            if (!QuorumAuth.nextPacketIsAuth(din)) {
                if (this.quorumRequireSasl) {
                    throw new SaslException("Learner " + sock.getRemoteSocketAddress() + " not trying to authenticate and authentication is required");
                }
            }
            else {
                byte[] token = this.receive(din);
                int tries = 0;
                dout = new DataOutputStream(sock.getOutputStream());
                byte[] challenge = null;
                ss = SecurityUtils.createSaslServer(this.serverLogin.getSubject(), "zookeeper-quorum", "zk-quorum-sasl-md5", this.serverLogin.callbackHandler, SaslQuorumAuthServer.LOG);
                while (!ss.isComplete()) {
                    challenge = ss.evaluateResponse(token);
                    if (!ss.isComplete()) {
                        if (++tries > 5) {
                            this.send(dout, challenge, QuorumAuth.Status.ERROR);
                            SaslQuorumAuthServer.LOG.warn("Failed to authenticate using SASL, server addr: {}, retries={} exceeded.", sock.getRemoteSocketAddress(), tries);
                            break;
                        }
                        this.send(dout, challenge, QuorumAuth.Status.IN_PROGRESS);
                        token = this.receive(din);
                    }
                }
                if (ss.isComplete()) {
                    this.send(dout, challenge, QuorumAuth.Status.SUCCESS);
                    SaslQuorumAuthServer.LOG.info("Successfully completed the authentication using SASL. learner addr: {}", sock.getRemoteSocketAddress());
                }
            }
        }
        catch (Exception e) {
            try {
                if (dout != null) {
                    this.send(dout, new byte[0], QuorumAuth.Status.ERROR);
                }
            }
            catch (IOException ioe) {
                SaslQuorumAuthServer.LOG.warn("Exception while sending failed status", ioe);
            }
            if (this.quorumRequireSasl) {
                SaslQuorumAuthServer.LOG.error("Failed to authenticate using SASL", e);
                throw new SaslException("Failed to authenticate using SASL: " + e.getMessage());
            }
            SaslQuorumAuthServer.LOG.warn("Failed to authenticate using SASL", e);
            SaslQuorumAuthServer.LOG.warn("Maintaining learner connection despite SASL authentication failure. server addr: {}, {}: {}", sock.getRemoteSocketAddress(), "quorum.auth.serverRequireSasl", this.quorumRequireSasl);
        }
        finally {
            if (ss != null) {
                try {
                    ss.dispose();
                }
                catch (SaslException e2) {
                    SaslQuorumAuthServer.LOG.error("SaslServer dispose() failed", e2);
                }
            }
        }
    }
    
    private byte[] receive(final DataInputStream din) throws IOException {
        final QuorumAuthPacket authPacket = new QuorumAuthPacket();
        final BinaryInputArchive bia = BinaryInputArchive.getArchive(din);
        authPacket.deserialize(bia, "qpconnect");
        return authPacket.getToken();
    }
    
    private void send(final DataOutputStream dout, final byte[] challenge, final QuorumAuth.Status s) throws IOException {
        final BufferedOutputStream bufferedOutput = new BufferedOutputStream(dout);
        final BinaryOutputArchive boa = BinaryOutputArchive.getArchive(bufferedOutput);
        QuorumAuthPacket authPacket;
        if (challenge == null && s != QuorumAuth.Status.SUCCESS) {
            authPacket = QuorumAuth.createPacket(QuorumAuth.Status.IN_PROGRESS, null);
        }
        else {
            authPacket = QuorumAuth.createPacket(s, challenge);
        }
        boa.writeRecord(authPacket, "qpconnect");
        bufferedOutput.flush();
    }
    
    static {
        LOG = LoggerFactory.getLogger(SaslQuorumAuthServer.class);
    }
}
