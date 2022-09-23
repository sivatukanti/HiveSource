// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum.auth;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.DataInput;
import org.apache.jute.BinaryInputArchive;
import java.io.DataInputStream;
import org.apache.zookeeper.server.quorum.QuorumAuthPacket;
import org.slf4j.Logger;

public class QuorumAuth
{
    private static final Logger LOG;
    public static final String QUORUM_SASL_AUTH_ENABLED = "quorum.auth.enableSasl";
    public static final String QUORUM_SERVER_SASL_AUTH_REQUIRED = "quorum.auth.serverRequireSasl";
    public static final String QUORUM_LEARNER_SASL_AUTH_REQUIRED = "quorum.auth.learnerRequireSasl";
    public static final String QUORUM_KERBEROS_SERVICE_PRINCIPAL = "quorum.auth.kerberos.servicePrincipal";
    public static final String QUORUM_KERBEROS_SERVICE_PRINCIPAL_DEFAULT_VALUE = "zkquorum/localhost";
    public static final String QUORUM_LEARNER_SASL_LOGIN_CONTEXT = "quorum.auth.learner.saslLoginContext";
    public static final String QUORUM_LEARNER_SASL_LOGIN_CONTEXT_DFAULT_VALUE = "QuorumLearner";
    public static final String QUORUM_SERVER_SASL_LOGIN_CONTEXT = "quorum.auth.server.saslLoginContext";
    public static final String QUORUM_SERVER_SASL_LOGIN_CONTEXT_DFAULT_VALUE = "QuorumServer";
    static final String QUORUM_SERVER_PROTOCOL_NAME = "zookeeper-quorum";
    static final String QUORUM_SERVER_SASL_DIGEST = "zk-quorum-sasl-md5";
    static final String QUORUM_AUTH_MESSAGE_TAG = "qpconnect";
    public static final long QUORUM_AUTH_MAGIC_NUMBER = 6855662812065295820L;
    
    public static QuorumAuthPacket createPacket(final Status status, final byte[] response) {
        return new QuorumAuthPacket(6855662812065295820L, status.status(), response);
    }
    
    public static boolean nextPacketIsAuth(final DataInputStream din) throws IOException {
        din.mark(32);
        final BinaryInputArchive bia = new BinaryInputArchive(din);
        final boolean firstIsAuth = bia.readLong("NO_TAG") == 6855662812065295820L;
        din.reset();
        return firstIsAuth;
    }
    
    static {
        LOG = LoggerFactory.getLogger(QuorumAuth.class);
    }
    
    public enum Status
    {
        IN_PROGRESS(0), 
        SUCCESS(1), 
        ERROR(-1);
        
        private int status;
        
        private Status(final int status) {
            this.status = status;
        }
        
        static Status getStatus(final int status) {
            switch (status) {
                case 0: {
                    return Status.IN_PROGRESS;
                }
                case 1: {
                    return Status.SUCCESS;
                }
                case -1: {
                    return Status.ERROR;
                }
                default: {
                    QuorumAuth.LOG.error("Unknown status:{}!", (Object)status);
                    assert false : "Unknown status!";
                    return Status.ERROR;
                }
            }
        }
        
        int status() {
            return this.status;
        }
    }
}
