// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.security;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.token.Token;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.TimerTask;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.AMRMTokenSecretManagerState;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.security.SecureRandom;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Set;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import java.util.Timer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.hadoop.yarn.server.security.MasterKeyData;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;

public class AMRMTokenSecretManager extends SecretManager<AMRMTokenIdentifier>
{
    private static final Log LOG;
    private int serialNo;
    private MasterKeyData nextMasterKey;
    private MasterKeyData currentMasterKey;
    private final ReadWriteLock readWriteLock;
    private final Lock readLock;
    private final Lock writeLock;
    private final Timer timer;
    private final long rollingInterval;
    private final long activationDelay;
    private RMContext rmContext;
    private final Set<ApplicationAttemptId> appAttemptSet;
    
    public AMRMTokenSecretManager(final Configuration conf, final RMContext rmContext) {
        this.serialNo = new SecureRandom().nextInt();
        this.readWriteLock = new ReentrantReadWriteLock();
        this.readLock = this.readWriteLock.readLock();
        this.writeLock = this.readWriteLock.writeLock();
        this.appAttemptSet = new HashSet<ApplicationAttemptId>();
        this.rmContext = rmContext;
        this.timer = new Timer();
        this.rollingInterval = conf.getLong("yarn.resourcemanager.am-rm-tokens.master-key-rolling-interval-secs", 86400L) * 1000L;
        this.activationDelay = (long)(conf.getLong("yarn.am.liveness-monitor.expiry-interval-ms", 600000L) * 1.5);
        AMRMTokenSecretManager.LOG.info("AMRMTokenKeyRollingInterval: " + this.rollingInterval + "ms and AMRMTokenKeyActivationDelay: " + this.activationDelay + " ms");
        if (this.rollingInterval <= this.activationDelay * 2L) {
            throw new IllegalArgumentException("yarn.resourcemanager.am-rm-tokens.master-key-rolling-interval-secs should be more than 2 X yarn.am.liveness-monitor.expiry-interval-ms");
        }
    }
    
    public void start() {
        if (this.currentMasterKey == null) {
            this.currentMasterKey = this.createNewMasterKey();
            final AMRMTokenSecretManagerState state = AMRMTokenSecretManagerState.newInstance(this.currentMasterKey.getMasterKey(), null);
            this.rmContext.getStateStore().storeOrUpdateAMRMTokenSecretManagerState(state, false);
        }
        this.timer.scheduleAtFixedRate(new MasterKeyRoller(), this.rollingInterval, this.rollingInterval);
    }
    
    public void stop() {
        this.timer.cancel();
    }
    
    public void applicationMasterFinished(final ApplicationAttemptId appAttemptId) {
        this.writeLock.lock();
        try {
            AMRMTokenSecretManager.LOG.info("Application finished, removing password for " + appAttemptId);
            this.appAttemptSet.remove(appAttemptId);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    void rollMasterKey() {
        this.writeLock.lock();
        try {
            AMRMTokenSecretManager.LOG.info("Rolling master-key for amrm-tokens");
            this.nextMasterKey = this.createNewMasterKey();
            final AMRMTokenSecretManagerState state = AMRMTokenSecretManagerState.newInstance(this.currentMasterKey.getMasterKey(), this.nextMasterKey.getMasterKey());
            this.rmContext.getStateStore().storeOrUpdateAMRMTokenSecretManagerState(state, true);
            this.timer.schedule(new NextKeyActivator(), this.activationDelay);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public void activateNextMasterKey() {
        this.writeLock.lock();
        try {
            AMRMTokenSecretManager.LOG.info("Activating next master key with id: " + this.nextMasterKey.getMasterKey().getKeyId());
            this.currentMasterKey = this.nextMasterKey;
            this.nextMasterKey = null;
            final AMRMTokenSecretManagerState state = AMRMTokenSecretManagerState.newInstance(this.currentMasterKey.getMasterKey(), null);
            this.rmContext.getStateStore().storeOrUpdateAMRMTokenSecretManagerState(state, true);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public MasterKeyData createNewMasterKey() {
        this.writeLock.lock();
        try {
            return new MasterKeyData(this.serialNo++, this.generateSecret());
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public Token<AMRMTokenIdentifier> createAndGetAMRMToken(final ApplicationAttemptId appAttemptId) {
        this.writeLock.lock();
        try {
            AMRMTokenSecretManager.LOG.info("Create AMRMToken for ApplicationAttempt: " + appAttemptId);
            final AMRMTokenIdentifier identifier = new AMRMTokenIdentifier(appAttemptId, this.getMasterKey().getMasterKey().getKeyId());
            final byte[] password = this.createPassword(identifier);
            this.appAttemptSet.add(appAttemptId);
            return new Token<AMRMTokenIdentifier>(identifier.getBytes(), password, identifier.getKind(), new Text());
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @VisibleForTesting
    public MasterKeyData getMasterKey() {
        this.readLock.lock();
        try {
            return (this.nextMasterKey == null) ? this.currentMasterKey : this.nextMasterKey;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public void addPersistedPassword(final Token<AMRMTokenIdentifier> token) throws IOException {
        this.writeLock.lock();
        try {
            final AMRMTokenIdentifier identifier = token.decodeIdentifier();
            AMRMTokenSecretManager.LOG.debug("Adding password for " + identifier.getApplicationAttemptId());
            this.appAttemptSet.add(identifier.getApplicationAttemptId());
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public byte[] retrievePassword(final AMRMTokenIdentifier identifier) throws InvalidToken {
        this.readLock.lock();
        try {
            final ApplicationAttemptId applicationAttemptId = identifier.getApplicationAttemptId();
            if (AMRMTokenSecretManager.LOG.isDebugEnabled()) {
                AMRMTokenSecretManager.LOG.debug("Trying to retrieve password for " + applicationAttemptId);
            }
            if (!this.appAttemptSet.contains(applicationAttemptId)) {
                throw new InvalidToken(applicationAttemptId + " not found in AMRMTokenSecretManager.");
            }
            if (identifier.getKeyId() == this.currentMasterKey.getMasterKey().getKeyId()) {
                return SecretManager.createPassword(identifier.getBytes(), this.currentMasterKey.getSecretKey());
            }
            if (this.nextMasterKey != null && identifier.getKeyId() == this.nextMasterKey.getMasterKey().getKeyId()) {
                return SecretManager.createPassword(identifier.getBytes(), this.nextMasterKey.getSecretKey());
            }
            throw new InvalidToken("Invalid AMRMToken from " + applicationAttemptId);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public AMRMTokenIdentifier createIdentifier() {
        return new AMRMTokenIdentifier();
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public MasterKeyData getCurrnetMasterKeyData() {
        this.readLock.lock();
        try {
            return this.currentMasterKey;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public MasterKeyData getNextMasterKeyData() {
        this.readLock.lock();
        try {
            return this.nextMasterKey;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    @Override
    protected byte[] createPassword(final AMRMTokenIdentifier identifier) {
        this.readLock.lock();
        try {
            final ApplicationAttemptId applicationAttemptId = identifier.getApplicationAttemptId();
            AMRMTokenSecretManager.LOG.info("Creating password for " + applicationAttemptId);
            return SecretManager.createPassword(identifier.getBytes(), this.getMasterKey().getSecretKey());
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public void recover(final RMStateStore.RMState state) {
        if (state.getAMRMTokenSecretManagerState() != null) {
            final MasterKey currentKey = state.getAMRMTokenSecretManagerState().getCurrentMasterKey();
            this.currentMasterKey = new MasterKeyData(currentKey, SecretManager.createSecretKey(currentKey.getBytes().array()));
            final MasterKey nextKey = state.getAMRMTokenSecretManagerState().getNextMasterKey();
            if (nextKey != null) {
                this.nextMasterKey = new MasterKeyData(nextKey, SecretManager.createSecretKey(nextKey.getBytes().array()));
                this.timer.schedule(new NextKeyActivator(), this.activationDelay);
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(AMRMTokenSecretManager.class);
    }
    
    private class MasterKeyRoller extends TimerTask
    {
        @Override
        public void run() {
            AMRMTokenSecretManager.this.rollMasterKey();
        }
    }
    
    private class NextKeyActivator extends TimerTask
    {
        @Override
        public void run() {
            AMRMTokenSecretManager.this.activateNextMasterKey();
        }
    }
}
