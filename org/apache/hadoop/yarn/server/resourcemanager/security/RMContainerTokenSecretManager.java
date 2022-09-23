// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.security;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.security.ContainerTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.api.records.LogAggregationContext;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.TimerTask;
import org.apache.hadoop.conf.Configuration;
import java.util.Timer;
import org.apache.hadoop.yarn.server.security.MasterKeyData;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.security.BaseContainerTokenSecretManager;

public class RMContainerTokenSecretManager extends BaseContainerTokenSecretManager
{
    private static Log LOG;
    private MasterKeyData nextMasterKey;
    private final Timer timer;
    private final long rollingInterval;
    private final long activationDelay;
    
    public RMContainerTokenSecretManager(final Configuration conf) {
        super(conf);
        this.timer = new Timer();
        this.rollingInterval = conf.getLong("yarn.resourcemanager.container-tokens.master-key-rolling-interval-secs", 86400L) * 1000L;
        this.activationDelay = (long)(conf.getLong("yarn.nm.liveness-monitor.expiry-interval-ms", 600000L) * 1.5);
        RMContainerTokenSecretManager.LOG.info("ContainerTokenKeyRollingInterval: " + this.rollingInterval + "ms and ContainerTokenKeyActivationDelay: " + this.activationDelay + "ms");
        if (this.rollingInterval <= this.activationDelay * 2L) {
            throw new IllegalArgumentException("yarn.resourcemanager.container-tokens.master-key-rolling-interval-secs should be more than 2 X yarn.resourcemanager.container-tokens.master-key-rolling-interval-secs");
        }
    }
    
    public void start() {
        this.rollMasterKey();
        this.timer.scheduleAtFixedRate(new MasterKeyRoller(), this.rollingInterval, this.rollingInterval);
    }
    
    public void stop() {
        this.timer.cancel();
    }
    
    @InterfaceAudience.Private
    public void rollMasterKey() {
        super.writeLock.lock();
        try {
            RMContainerTokenSecretManager.LOG.info("Rolling master-key for container-tokens");
            if (this.currentMasterKey == null) {
                this.currentMasterKey = this.createNewMasterKey();
            }
            else {
                this.nextMasterKey = this.createNewMasterKey();
                RMContainerTokenSecretManager.LOG.info("Going to activate master-key with key-id " + this.nextMasterKey.getMasterKey().getKeyId() + " in " + this.activationDelay + "ms");
                this.timer.schedule(new NextKeyActivator(), this.activationDelay);
            }
        }
        finally {
            super.writeLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    public MasterKey getNextKey() {
        super.readLock.lock();
        try {
            if (this.nextMasterKey == null) {
                return null;
            }
            return this.nextMasterKey.getMasterKey();
        }
        finally {
            super.readLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    public void activateNextMasterKey() {
        super.writeLock.lock();
        try {
            RMContainerTokenSecretManager.LOG.info("Activating next master key with id: " + this.nextMasterKey.getMasterKey().getKeyId());
            this.currentMasterKey = this.nextMasterKey;
            this.nextMasterKey = null;
        }
        finally {
            super.writeLock.unlock();
        }
    }
    
    public Token createContainerToken(final ContainerId containerId, final NodeId nodeId, final String appSubmitter, final Resource capability, final Priority priority, final long createTime) {
        return this.createContainerToken(containerId, nodeId, appSubmitter, capability, priority, createTime, null);
    }
    
    public Token createContainerToken(final ContainerId containerId, final NodeId nodeId, final String appSubmitter, final Resource capability, final Priority priority, final long createTime, final LogAggregationContext logAggregationContext) {
        final long expiryTimeStamp = System.currentTimeMillis() + this.containerTokenExpiryInterval;
        this.readLock.lock();
        ContainerTokenIdentifier tokenIdentifier;
        byte[] password;
        try {
            tokenIdentifier = new ContainerTokenIdentifier(containerId, nodeId.toString(), appSubmitter, capability, expiryTimeStamp, this.currentMasterKey.getMasterKey().getKeyId(), ResourceManager.getClusterTimeStamp(), priority, createTime, logAggregationContext);
            password = this.createPassword(tokenIdentifier);
        }
        finally {
            this.readLock.unlock();
        }
        return BuilderUtils.newContainerToken(nodeId, password, tokenIdentifier);
    }
    
    static {
        RMContainerTokenSecretManager.LOG = LogFactory.getLog(RMContainerTokenSecretManager.class);
    }
    
    private class MasterKeyRoller extends TimerTask
    {
        @Override
        public void run() {
            RMContainerTokenSecretManager.this.rollMasterKey();
        }
    }
    
    private class NextKeyActivator extends TimerTask
    {
        @Override
        public void run() {
            RMContainerTokenSecretManager.this.activateNextMasterKey();
        }
    }
}
