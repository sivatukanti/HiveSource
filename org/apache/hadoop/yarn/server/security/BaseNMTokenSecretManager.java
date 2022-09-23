// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.security;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.security.token.TokenIdentifier;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.security.SecureRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.security.NMTokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;

public class BaseNMTokenSecretManager extends SecretManager<NMTokenIdentifier>
{
    private static Log LOG;
    protected int serialNo;
    protected final ReadWriteLock readWriteLock;
    protected final Lock readLock;
    protected final Lock writeLock;
    protected MasterKeyData currentMasterKey;
    
    public BaseNMTokenSecretManager() {
        this.serialNo = new SecureRandom().nextInt();
        this.readWriteLock = new ReentrantReadWriteLock();
        this.readLock = this.readWriteLock.readLock();
        this.writeLock = this.readWriteLock.writeLock();
    }
    
    protected MasterKeyData createNewMasterKey() {
        this.writeLock.lock();
        try {
            return new MasterKeyData(this.serialNo++, this.generateSecret());
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    public MasterKey getCurrentKey() {
        this.readLock.lock();
        try {
            return this.currentMasterKey.getMasterKey();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    protected byte[] createPassword(final NMTokenIdentifier identifier) {
        if (BaseNMTokenSecretManager.LOG.isDebugEnabled()) {
            BaseNMTokenSecretManager.LOG.debug("creating password for " + identifier.getApplicationAttemptId() + " for user " + identifier.getApplicationSubmitter() + " to run on NM " + identifier.getNodeId());
        }
        this.readLock.lock();
        try {
            return SecretManager.createPassword(identifier.getBytes(), this.currentMasterKey.getSecretKey());
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public byte[] retrievePassword(final NMTokenIdentifier identifier) throws InvalidToken {
        this.readLock.lock();
        try {
            return this.retrivePasswordInternal(identifier, this.currentMasterKey);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    protected byte[] retrivePasswordInternal(final NMTokenIdentifier identifier, final MasterKeyData masterKey) {
        if (BaseNMTokenSecretManager.LOG.isDebugEnabled()) {
            BaseNMTokenSecretManager.LOG.debug("creating password for " + identifier.getApplicationAttemptId() + " for user " + identifier.getApplicationSubmitter() + " to run on NM " + identifier.getNodeId());
        }
        return SecretManager.createPassword(identifier.getBytes(), masterKey.getSecretKey());
    }
    
    @Override
    public NMTokenIdentifier createIdentifier() {
        return new NMTokenIdentifier();
    }
    
    public Token createNMToken(final ApplicationAttemptId applicationAttemptId, final NodeId nodeId, final String applicationSubmitter) {
        this.readLock.lock();
        NMTokenIdentifier identifier;
        byte[] password;
        try {
            identifier = new NMTokenIdentifier(applicationAttemptId, nodeId, applicationSubmitter, this.currentMasterKey.getMasterKey().getKeyId());
            password = this.createPassword(identifier);
        }
        finally {
            this.readLock.unlock();
        }
        return newInstance(password, identifier);
    }
    
    public static Token newInstance(final byte[] password, final NMTokenIdentifier identifier) {
        final NodeId nodeId = identifier.getNodeId();
        final InetSocketAddress addr = NetUtils.createSocketAddrForHost(nodeId.getHost(), nodeId.getPort());
        final Token nmToken = Token.newInstance(identifier.getBytes(), NMTokenIdentifier.KIND.toString(), password, SecurityUtil.buildTokenService(addr).toString());
        return nmToken;
    }
    
    static {
        BaseNMTokenSecretManager.LOG = LogFactory.getLog(BaseNMTokenSecretManager.class);
    }
}
