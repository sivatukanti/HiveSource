// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.security;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.security.SecureRandom;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.security.ContainerTokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;

public class BaseContainerTokenSecretManager extends SecretManager<ContainerTokenIdentifier>
{
    private static Log LOG;
    protected int serialNo;
    protected final ReadWriteLock readWriteLock;
    protected final Lock readLock;
    protected final Lock writeLock;
    protected MasterKeyData currentMasterKey;
    protected final long containerTokenExpiryInterval;
    
    public BaseContainerTokenSecretManager(final Configuration conf) {
        this.serialNo = new SecureRandom().nextInt();
        this.readWriteLock = new ReentrantReadWriteLock();
        this.readLock = this.readWriteLock.readLock();
        this.writeLock = this.readWriteLock.writeLock();
        this.containerTokenExpiryInterval = conf.getInt("yarn.resourcemanager.rm.container-allocation.expiry-interval-ms", 600000);
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
    
    public byte[] createPassword(final ContainerTokenIdentifier identifier) {
        if (BaseContainerTokenSecretManager.LOG.isDebugEnabled()) {
            BaseContainerTokenSecretManager.LOG.debug("Creating password for " + identifier.getContainerID() + " for user " + identifier.getUser() + " to be run on NM " + identifier.getNmHostAddress());
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
    public byte[] retrievePassword(final ContainerTokenIdentifier identifier) throws InvalidToken {
        this.readLock.lock();
        try {
            return this.retrievePasswordInternal(identifier, this.currentMasterKey);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    protected byte[] retrievePasswordInternal(final ContainerTokenIdentifier identifier, final MasterKeyData masterKey) throws InvalidToken {
        if (BaseContainerTokenSecretManager.LOG.isDebugEnabled()) {
            BaseContainerTokenSecretManager.LOG.debug("Retrieving password for " + identifier.getContainerID() + " for user " + identifier.getUser() + " to be run on NM " + identifier.getNmHostAddress());
        }
        return SecretManager.createPassword(identifier.getBytes(), masterKey.getSecretKey());
    }
    
    @Override
    public ContainerTokenIdentifier createIdentifier() {
        return new ContainerTokenIdentifier();
    }
    
    static {
        BaseContainerTokenSecretManager.LOG = LogFactory.getLog(BaseContainerTokenSecretManager.class);
    }
}
