// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.security;

import org.apache.commons.logging.LogFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.api.records.Container;
import java.util.Iterator;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.TimerTask;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.HashSet;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Timer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.security.MasterKeyData;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.security.BaseNMTokenSecretManager;

public class NMTokenSecretManagerInRM extends BaseNMTokenSecretManager
{
    private static Log LOG;
    private MasterKeyData nextMasterKey;
    private Configuration conf;
    private final Timer timer;
    private final long rollingInterval;
    private final long activationDelay;
    private final ConcurrentHashMap<ApplicationAttemptId, HashSet<NodeId>> appAttemptToNodeKeyMap;
    
    public NMTokenSecretManagerInRM(final Configuration conf) {
        this.conf = conf;
        this.timer = new Timer();
        this.rollingInterval = this.conf.getLong("yarn.resourcemanager.nm-tokens.master-key-rolling-interval-secs", 86400L) * 1000L;
        this.activationDelay = (long)(conf.getLong("yarn.nm.liveness-monitor.expiry-interval-ms", 600000L) * 1.5);
        NMTokenSecretManagerInRM.LOG.info("NMTokenKeyRollingInterval: " + this.rollingInterval + "ms and NMTokenKeyActivationDelay: " + this.activationDelay + "ms");
        if (this.rollingInterval <= this.activationDelay * 2L) {
            throw new IllegalArgumentException("yarn.resourcemanager.nm-tokens.master-key-rolling-interval-secs should be more than 2 X yarn.nm.liveness-monitor.expiry-interval-ms");
        }
        this.appAttemptToNodeKeyMap = new ConcurrentHashMap<ApplicationAttemptId, HashSet<NodeId>>();
    }
    
    @InterfaceAudience.Private
    public void rollMasterKey() {
        super.writeLock.lock();
        try {
            NMTokenSecretManagerInRM.LOG.info("Rolling master-key for nm-tokens");
            if (this.currentMasterKey == null) {
                this.currentMasterKey = this.createNewMasterKey();
            }
            else {
                this.nextMasterKey = this.createNewMasterKey();
                NMTokenSecretManagerInRM.LOG.info("Going to activate master-key with key-id " + this.nextMasterKey.getMasterKey().getKeyId() + " in " + this.activationDelay + "ms");
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
            NMTokenSecretManagerInRM.LOG.info("Activating next master key with id: " + this.nextMasterKey.getMasterKey().getKeyId());
            this.currentMasterKey = this.nextMasterKey;
            this.nextMasterKey = null;
            this.clearApplicationNMTokenKeys();
        }
        finally {
            super.writeLock.unlock();
        }
    }
    
    public void clearNodeSetForAttempt(final ApplicationAttemptId attemptId) {
        super.writeLock.lock();
        try {
            final HashSet<NodeId> nodeSet = this.appAttemptToNodeKeyMap.get(attemptId);
            if (nodeSet != null) {
                NMTokenSecretManagerInRM.LOG.info("Clear node set for " + attemptId);
                nodeSet.clear();
            }
        }
        finally {
            super.writeLock.unlock();
        }
    }
    
    private void clearApplicationNMTokenKeys() {
        final Iterator<HashSet<NodeId>> nodeSetI = this.appAttemptToNodeKeyMap.values().iterator();
        while (nodeSetI.hasNext()) {
            nodeSetI.next().clear();
        }
    }
    
    public void start() {
        this.rollMasterKey();
        this.timer.scheduleAtFixedRate(new MasterKeyRoller(), this.rollingInterval, this.rollingInterval);
    }
    
    public void stop() {
        this.timer.cancel();
    }
    
    public NMToken createAndGetNMToken(final String applicationSubmitter, final ApplicationAttemptId appAttemptId, final Container container) {
        try {
            this.readLock.lock();
            final HashSet<NodeId> nodeSet = this.appAttemptToNodeKeyMap.get(appAttemptId);
            NMToken nmToken = null;
            if (nodeSet != null && !nodeSet.contains(container.getNodeId())) {
                NMTokenSecretManagerInRM.LOG.info("Sending NMToken for nodeId : " + container.getNodeId() + " for container : " + container.getId());
                final Token token = this.createNMToken(container.getId().getApplicationAttemptId(), container.getNodeId(), applicationSubmitter);
                nmToken = NMToken.newInstance(container.getNodeId(), token);
                nodeSet.add(container.getNodeId());
            }
            return nmToken;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public void registerApplicationAttempt(final ApplicationAttemptId appAttemptId) {
        try {
            this.writeLock.lock();
            this.appAttemptToNodeKeyMap.put(appAttemptId, new HashSet<NodeId>());
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public boolean isApplicationAttemptRegistered(final ApplicationAttemptId appAttemptId) {
        try {
            this.readLock.lock();
            return this.appAttemptToNodeKeyMap.containsKey(appAttemptId);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public boolean isApplicationAttemptNMTokenPresent(final ApplicationAttemptId appAttemptId, final NodeId nodeId) {
        try {
            this.readLock.lock();
            final HashSet<NodeId> nodes = this.appAttemptToNodeKeyMap.get(appAttemptId);
            return nodes != null && nodes.contains(nodeId);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public void unregisterApplicationAttempt(final ApplicationAttemptId appAttemptId) {
        try {
            this.writeLock.lock();
            this.appAttemptToNodeKeyMap.remove(appAttemptId);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public void removeNodeKey(final NodeId nodeId) {
        try {
            this.writeLock.lock();
            final Iterator<HashSet<NodeId>> appNodeKeySetIterator = this.appAttemptToNodeKeyMap.values().iterator();
            while (appNodeKeySetIterator.hasNext()) {
                appNodeKeySetIterator.next().remove(nodeId);
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    static {
        NMTokenSecretManagerInRM.LOG = LogFactory.getLog(NMTokenSecretManagerInRM.class);
    }
    
    private class MasterKeyRoller extends TimerTask
    {
        @Override
        public void run() {
            NMTokenSecretManagerInRM.this.rollMasterKey();
        }
    }
    
    private class NextKeyActivator extends TimerTask
    {
        @Override
        public void run() {
            NMTokenSecretManagerInRM.this.activateNextMasterKey();
        }
    }
}
