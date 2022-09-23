// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

import org.apache.hadoop.util.StringUtils;
import org.apache.commons.logging.LogFactory;
import java.util.Collection;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;
import java.io.DataOutput;
import org.apache.hadoop.yarn.security.client.YARNDelegationTokenIdentifier;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationAttemptStateData;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.ApplicationAttemptStateDataPBImpl;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.ApplicationStateDataPBImpl;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.RMDelegationTokenIdentifierData;
import java.io.DataInput;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.AMRMTokenSecretManagerState;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.AMRMTokenSecretManagerStatePBImpl;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.Epoch;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.EpochPBImpl;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.apache.hadoop.yarn.server.records.impl.pb.VersionPBImpl;
import java.io.IOException;
import java.util.Collections;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.hadoop.yarn.server.resourcemanager.RMZKUtils;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import org.apache.zookeeper.data.Id;
import org.apache.hadoop.yarn.conf.HAUtil;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.ZooKeeper;
import org.apache.hadoop.util.ZKUtil;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.server.records.Version;
import java.security.SecureRandom;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ZKRMStateStore extends RMStateStore
{
    public static final Log LOG;
    private final SecureRandom random;
    protected static final String ROOT_ZNODE_NAME = "ZKRMStateRoot";
    protected static final Version CURRENT_VERSION_INFO;
    private static final String RM_DELEGATION_TOKENS_ROOT_ZNODE_NAME = "RMDelegationTokensRoot";
    private static final String RM_DT_SEQUENTIAL_NUMBER_ZNODE_NAME = "RMDTSequentialNumber";
    private static final String RM_DT_MASTER_KEYS_ROOT_ZNODE_NAME = "RMDTMasterKeysRoot";
    private int numRetries;
    private String zkHostPort;
    private int zkSessionTimeout;
    @VisibleForTesting
    long zkRetryInterval;
    private List<ACL> zkAcl;
    private List<ZKUtil.ZKAuthInfo> zkAuths;
    private String zkRootNodePath;
    private String rmAppRoot;
    private String rmDTSecretManagerRoot;
    private String dtMasterKeysRootPath;
    private String delegationTokensRootPath;
    private String dtSequenceNumberPath;
    private String amrmTokenSecretManagerRoot;
    @VisibleForTesting
    protected String znodeWorkingPath;
    @VisibleForTesting
    protected ZooKeeper zkClient;
    private ZooKeeper oldZkClient;
    private static final String FENCING_LOCK = "RM_ZK_FENCING_LOCK";
    private String fencingNodePath;
    private Op createFencingNodePathOp;
    private Op deleteFencingNodePathOp;
    private Thread verifyActiveStatusThread;
    private String zkRootNodeUsername;
    private final String zkRootNodePassword;
    @VisibleForTesting
    List<ACL> zkRootNodeAcl;
    private boolean useDefaultFencingScheme;
    public static final int CREATE_DELETE_PERMS = 12;
    private final String zkRootNodeAuthScheme;
    
    public ZKRMStateStore() {
        this.random = new SecureRandom();
        this.zkHostPort = null;
        this.zkRootNodePassword = Long.toString(this.random.nextLong());
        this.useDefaultFencingScheme = false;
        this.zkRootNodeAuthScheme = new DigestAuthenticationProvider().getScheme();
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    protected List<ACL> constructZkRootNodeACL(final Configuration conf, final List<ACL> sourceACLs) throws NoSuchAlgorithmException {
        final List<ACL> zkRootNodeAcl = new ArrayList<ACL>();
        for (final ACL acl : sourceACLs) {
            zkRootNodeAcl.add(new ACL(ZKUtil.removeSpecificPerms(acl.getPerms(), 12), acl.getId()));
        }
        this.zkRootNodeUsername = HAUtil.getConfValueForRMInstance("yarn.resourcemanager.address", "0.0.0.0:8032", conf);
        final Id rmId = new Id(this.zkRootNodeAuthScheme, DigestAuthenticationProvider.generateDigest(this.zkRootNodeUsername + ":" + this.zkRootNodePassword));
        zkRootNodeAcl.add(new ACL(12, rmId));
        return zkRootNodeAcl;
    }
    
    public synchronized void initInternal(final Configuration conf) throws Exception {
        this.zkHostPort = conf.get("yarn.resourcemanager.zk-address");
        if (this.zkHostPort == null) {
            throw new YarnRuntimeException("No server address specified for zookeeper state store for Resource Manager recovery. yarn.resourcemanager.zk-address is not configured.");
        }
        this.numRetries = conf.getInt("yarn.resourcemanager.zk-num-retries", 1000);
        this.znodeWorkingPath = conf.get("yarn.resourcemanager.zk-state-store.parent-path", "/rmstore");
        this.zkSessionTimeout = conf.getInt("yarn.resourcemanager.zk-timeout-ms", 10000);
        if (HAUtil.isHAEnabled(conf)) {
            this.zkRetryInterval = this.zkSessionTimeout / this.numRetries;
        }
        else {
            this.zkRetryInterval = conf.getLong("yarn.resourcemanager.zk-retry-interval-ms", 1000L);
        }
        this.zkAcl = RMZKUtils.getZKAcls(conf);
        this.zkAuths = RMZKUtils.getZKAuths(conf);
        this.zkRootNodePath = this.getNodePath(this.znodeWorkingPath, "ZKRMStateRoot");
        this.rmAppRoot = this.getNodePath(this.zkRootNodePath, "RMAppRoot");
        this.fencingNodePath = this.getNodePath(this.zkRootNodePath, "RM_ZK_FENCING_LOCK");
        this.createFencingNodePathOp = Op.create(this.fencingNodePath, new byte[0], this.zkAcl, CreateMode.PERSISTENT);
        this.deleteFencingNodePathOp = Op.delete(this.fencingNodePath, -1);
        Label_0258: {
            if (HAUtil.isHAEnabled(conf)) {
                String zkRootNodeAclConf = HAUtil.getConfValueForRMInstance("yarn.resourcemanager.zk-state-store.root-node.acl", conf);
                if (zkRootNodeAclConf != null) {
                    zkRootNodeAclConf = ZKUtil.resolveConfIndirection(zkRootNodeAclConf);
                    try {
                        this.zkRootNodeAcl = ZKUtil.parseACLs(zkRootNodeAclConf);
                        break Label_0258;
                    }
                    catch (ZKUtil.BadAclFormatException bafe) {
                        ZKRMStateStore.LOG.error("Invalid format for yarn.resourcemanager.zk-state-store.root-node.acl");
                        throw bafe;
                    }
                }
                this.useDefaultFencingScheme = true;
                this.zkRootNodeAcl = this.constructZkRootNodeACL(conf, this.zkAcl);
            }
        }
        this.rmDTSecretManagerRoot = this.getNodePath(this.zkRootNodePath, "RMDTSecretManagerRoot");
        this.dtMasterKeysRootPath = this.getNodePath(this.rmDTSecretManagerRoot, "RMDTMasterKeysRoot");
        this.delegationTokensRootPath = this.getNodePath(this.rmDTSecretManagerRoot, "RMDelegationTokensRoot");
        this.dtSequenceNumberPath = this.getNodePath(this.rmDTSecretManagerRoot, "RMDTSequentialNumber");
        this.amrmTokenSecretManagerRoot = this.getNodePath(this.zkRootNodePath, "AMRMTokenSecretManagerRoot");
    }
    
    public synchronized void startInternal() throws Exception {
        this.createConnection();
        this.createRootDir(this.znodeWorkingPath);
        this.createRootDir(this.zkRootNodePath);
        if (HAUtil.isHAEnabled(this.getConfig())) {
            this.fence();
            (this.verifyActiveStatusThread = new VerifyActiveStatusThread()).start();
        }
        this.createRootDir(this.rmAppRoot);
        this.createRootDir(this.rmDTSecretManagerRoot);
        this.createRootDir(this.dtMasterKeysRootPath);
        this.createRootDir(this.delegationTokensRootPath);
        this.createRootDir(this.dtSequenceNumberPath);
        this.createRootDir(this.amrmTokenSecretManagerRoot);
    }
    
    private void createRootDir(final String rootPath) throws Exception {
        new ZKAction<String>() {
            public String run() throws KeeperException, InterruptedException {
                try {
                    return ZKRMStateStore.this.zkClient.create(rootPath, null, ZKRMStateStore.this.zkAcl, CreateMode.PERSISTENT);
                }
                catch (KeeperException ke) {
                    if (ke.code() == KeeperException.Code.NODEEXISTS) {
                        ZKRMStateStore.LOG.debug(rootPath + "znode already exists!");
                        return null;
                    }
                    throw ke;
                }
            }
        }.runWithRetries();
    }
    
    private void logRootNodeAcls(final String prefix) throws Exception {
        final Stat getStat = new Stat();
        final List<ACL> getAcls = this.getACLWithRetries(this.zkRootNodePath, getStat);
        final StringBuilder builder = new StringBuilder();
        builder.append(prefix);
        for (final ACL acl : getAcls) {
            builder.append(acl.toString());
        }
        builder.append(getStat.toString());
        ZKRMStateStore.LOG.debug(builder.toString());
    }
    
    private synchronized void fence() throws Exception {
        if (ZKRMStateStore.LOG.isTraceEnabled()) {
            this.logRootNodeAcls("Before fencing\n");
        }
        new ZKAction<Void>() {
            public Void run() throws KeeperException, InterruptedException {
                ZKRMStateStore.this.zkClient.setACL(ZKRMStateStore.this.zkRootNodePath, ZKRMStateStore.this.zkRootNodeAcl, -1);
                return null;
            }
        }.runWithRetries();
        new ZKAction<Void>() {
            public Void run() throws KeeperException, InterruptedException {
                try {
                    ZKRMStateStore.this.zkClient.multi(Collections.singletonList(ZKRMStateStore.this.deleteFencingNodePathOp));
                }
                catch (KeeperException.NoNodeException nne) {
                    ZKRMStateStore.LOG.info("Fencing node " + ZKRMStateStore.this.fencingNodePath + " doesn't exist to delete");
                }
                return null;
            }
        }.runWithRetries();
        if (ZKRMStateStore.LOG.isTraceEnabled()) {
            this.logRootNodeAcls("After fencing\n");
        }
    }
    
    private synchronized void closeZkClients() throws IOException {
        if (this.zkClient != null) {
            try {
                this.zkClient.close();
            }
            catch (InterruptedException e) {
                throw new IOException("Interrupted while closing ZK", e);
            }
            this.zkClient = null;
        }
        if (this.oldZkClient != null) {
            try {
                this.oldZkClient.close();
            }
            catch (InterruptedException e) {
                throw new IOException("Interrupted while closing old ZK", e);
            }
            this.oldZkClient = null;
        }
    }
    
    @Override
    protected synchronized void closeInternal() throws Exception {
        if (this.verifyActiveStatusThread != null) {
            this.verifyActiveStatusThread.interrupt();
            this.verifyActiveStatusThread.join(1000L);
        }
        this.closeZkClients();
    }
    
    @Override
    protected Version getCurrentVersion() {
        return ZKRMStateStore.CURRENT_VERSION_INFO;
    }
    
    @Override
    protected synchronized void storeVersion() throws Exception {
        final String versionNodePath = this.getNodePath(this.zkRootNodePath, "RMVersionNode");
        final byte[] data = ((VersionPBImpl)ZKRMStateStore.CURRENT_VERSION_INFO).getProto().toByteArray();
        if (this.existsWithRetries(versionNodePath, true) != null) {
            this.setDataWithRetries(versionNodePath, data, -1);
        }
        else {
            this.createWithRetries(versionNodePath, data, this.zkAcl, CreateMode.PERSISTENT);
        }
    }
    
    @Override
    protected synchronized Version loadVersion() throws Exception {
        final String versionNodePath = this.getNodePath(this.zkRootNodePath, "RMVersionNode");
        if (this.existsWithRetries(versionNodePath, true) != null) {
            final byte[] data = this.getDataWithRetries(versionNodePath, true);
            final Version version = new VersionPBImpl(YarnServerCommonProtos.VersionProto.parseFrom(data));
            return version;
        }
        return null;
    }
    
    @Override
    public synchronized long getAndIncrementEpoch() throws Exception {
        final String epochNodePath = this.getNodePath(this.zkRootNodePath, "EpochNode");
        long currentEpoch = 0L;
        if (this.existsWithRetries(epochNodePath, true) != null) {
            final byte[] data = this.getDataWithRetries(epochNodePath, true);
            final Epoch epoch = new EpochPBImpl(YarnServerResourceManagerRecoveryProtos.EpochProto.parseFrom(data));
            currentEpoch = epoch.getEpoch();
            final byte[] storeData = Epoch.newInstance(currentEpoch + 1L).getProto().toByteArray();
            this.setDataWithRetries(epochNodePath, storeData, -1);
        }
        else {
            final byte[] storeData2 = Epoch.newInstance(currentEpoch + 1L).getProto().toByteArray();
            this.createWithRetries(epochNodePath, storeData2, this.zkAcl, CreateMode.PERSISTENT);
        }
        return currentEpoch;
    }
    
    @Override
    public synchronized RMState loadState() throws Exception {
        final RMState rmState = new RMState();
        this.loadRMDTSecretManagerState(rmState);
        this.loadRMAppState(rmState);
        this.loadAMRMTokenSecretManagerState(rmState);
        return rmState;
    }
    
    private void loadAMRMTokenSecretManagerState(final RMState rmState) throws Exception {
        final byte[] data = this.getDataWithRetries(this.amrmTokenSecretManagerRoot, true);
        if (data == null) {
            ZKRMStateStore.LOG.warn("There is no data saved");
            return;
        }
        final AMRMTokenSecretManagerStatePBImpl stateData = new AMRMTokenSecretManagerStatePBImpl(YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto.parseFrom(data));
        rmState.amrmTokenSecretManagerState = AMRMTokenSecretManagerState.newInstance(stateData.getCurrentMasterKey(), stateData.getNextMasterKey());
    }
    
    private synchronized void loadRMDTSecretManagerState(final RMState rmState) throws Exception {
        this.loadRMDelegationKeyState(rmState);
        this.loadRMSequentialNumberState(rmState);
        this.loadRMDelegationTokenState(rmState);
    }
    
    private void loadRMDelegationKeyState(final RMState rmState) throws Exception {
        final List<String> childNodes = this.getChildrenWithRetries(this.dtMasterKeysRootPath, true);
        for (final String childNodeName : childNodes) {
            final String childNodePath = this.getNodePath(this.dtMasterKeysRootPath, childNodeName);
            final byte[] childData = this.getDataWithRetries(childNodePath, true);
            if (childData == null) {
                ZKRMStateStore.LOG.warn("Content of " + childNodePath + " is broken.");
            }
            else {
                final ByteArrayInputStream is = new ByteArrayInputStream(childData);
                final DataInputStream fsIn = new DataInputStream(is);
                try {
                    if (!childNodeName.startsWith("DelegationKey_")) {
                        continue;
                    }
                    final DelegationKey key = new DelegationKey();
                    key.readFields(fsIn);
                    rmState.rmSecretManagerState.masterKeyState.add(key);
                    if (!ZKRMStateStore.LOG.isDebugEnabled()) {
                        continue;
                    }
                    ZKRMStateStore.LOG.debug("Loaded delegation key: keyId=" + key.getKeyId() + ", expirationDate=" + key.getExpiryDate());
                }
                finally {
                    is.close();
                }
            }
        }
    }
    
    private void loadRMSequentialNumberState(final RMState rmState) throws Exception {
        final byte[] seqData = this.getDataWithRetries(this.dtSequenceNumberPath, false);
        if (seqData != null) {
            final ByteArrayInputStream seqIs = new ByteArrayInputStream(seqData);
            final DataInputStream seqIn = new DataInputStream(seqIs);
            try {
                rmState.rmSecretManagerState.dtSequenceNumber = seqIn.readInt();
            }
            finally {
                seqIn.close();
            }
        }
    }
    
    private void loadRMDelegationTokenState(final RMState rmState) throws Exception {
        final List<String> childNodes = this.getChildrenWithRetries(this.delegationTokensRootPath, true);
        for (final String childNodeName : childNodes) {
            final String childNodePath = this.getNodePath(this.delegationTokensRootPath, childNodeName);
            final byte[] childData = this.getDataWithRetries(childNodePath, true);
            if (childData == null) {
                ZKRMStateStore.LOG.warn("Content of " + childNodePath + " is broken.");
            }
            else {
                final ByteArrayInputStream is = new ByteArrayInputStream(childData);
                final DataInputStream fsIn = new DataInputStream(is);
                try {
                    if (!childNodeName.startsWith("RMDelegationToken_")) {
                        continue;
                    }
                    final RMDelegationTokenIdentifierData identifierData = new RMDelegationTokenIdentifierData();
                    identifierData.readFields(fsIn);
                    final RMDelegationTokenIdentifier identifier = identifierData.getTokenIdentifier();
                    final long renewDate = identifierData.getRenewDate();
                    rmState.rmSecretManagerState.delegationTokenState.put(identifier, renewDate);
                    if (!ZKRMStateStore.LOG.isDebugEnabled()) {
                        continue;
                    }
                    ZKRMStateStore.LOG.debug("Loaded RMDelegationTokenIdentifier: " + identifier + " renewDate=" + renewDate);
                }
                finally {
                    is.close();
                }
            }
        }
    }
    
    private synchronized void loadRMAppState(final RMState rmState) throws Exception {
        final List<String> childNodes = this.getChildrenWithRetries(this.rmAppRoot, true);
        for (final String childNodeName : childNodes) {
            final String childNodePath = this.getNodePath(this.rmAppRoot, childNodeName);
            final byte[] childData = this.getDataWithRetries(childNodePath, true);
            if (childNodeName.startsWith("application_")) {
                if (ZKRMStateStore.LOG.isDebugEnabled()) {
                    ZKRMStateStore.LOG.debug("Loading application from znode: " + childNodeName);
                }
                final ApplicationId appId = ConverterUtils.toApplicationId(childNodeName);
                final ApplicationStateDataPBImpl appStateData = new ApplicationStateDataPBImpl(YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto.parseFrom(childData));
                final ApplicationState appState = new ApplicationState(appStateData.getSubmitTime(), appStateData.getStartTime(), appStateData.getApplicationSubmissionContext(), appStateData.getUser(), appStateData.getState(), appStateData.getDiagnostics(), appStateData.getFinishTime());
                if (!appId.equals(appState.context.getApplicationId())) {
                    throw new YarnRuntimeException("The child node name is different from the application id");
                }
                rmState.appState.put(appId, appState);
                this.loadApplicationAttemptState(appState, appId);
            }
            else {
                ZKRMStateStore.LOG.info("Unknown child node with name: " + childNodeName);
            }
        }
    }
    
    private void loadApplicationAttemptState(final ApplicationState appState, final ApplicationId appId) throws Exception {
        final String appPath = this.getNodePath(this.rmAppRoot, appId.toString());
        final List<String> attempts = this.getChildrenWithRetries(appPath, false);
        for (final String attemptIDStr : attempts) {
            if (attemptIDStr.startsWith("appattempt_")) {
                final String attemptPath = this.getNodePath(appPath, attemptIDStr);
                final byte[] attemptData = this.getDataWithRetries(attemptPath, true);
                final ApplicationAttemptId attemptId = ConverterUtils.toApplicationAttemptId(attemptIDStr);
                final ApplicationAttemptStateDataPBImpl attemptStateData = new ApplicationAttemptStateDataPBImpl(YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto.parseFrom(attemptData));
                Credentials credentials = null;
                if (attemptStateData.getAppAttemptTokens() != null) {
                    credentials = new Credentials();
                    final DataInputByteBuffer dibb = new DataInputByteBuffer();
                    dibb.reset(attemptStateData.getAppAttemptTokens());
                    credentials.readTokenStorageStream(dibb);
                }
                final ApplicationAttemptState attemptState = new ApplicationAttemptState(attemptId, attemptStateData.getMasterContainer(), credentials, attemptStateData.getStartTime(), attemptStateData.getState(), attemptStateData.getFinalTrackingUrl(), attemptStateData.getDiagnostics(), attemptStateData.getFinalApplicationStatus(), attemptStateData.getAMContainerExitStatus(), attemptStateData.getFinishTime(), attemptStateData.getMemorySeconds(), attemptStateData.getVcoreSeconds());
                appState.attempts.put(attemptState.getAttemptId(), attemptState);
            }
        }
        ZKRMStateStore.LOG.debug("Done loading applications from ZK state store");
    }
    
    public synchronized void storeApplicationStateInternal(final ApplicationId appId, final ApplicationStateData appStateDataPB) throws Exception {
        final String nodeCreatePath = this.getNodePath(this.rmAppRoot, appId.toString());
        if (ZKRMStateStore.LOG.isDebugEnabled()) {
            ZKRMStateStore.LOG.debug("Storing info for app: " + appId + " at: " + nodeCreatePath);
        }
        final byte[] appStateData = appStateDataPB.getProto().toByteArray();
        this.createWithRetries(nodeCreatePath, appStateData, this.zkAcl, CreateMode.PERSISTENT);
    }
    
    public synchronized void updateApplicationStateInternal(final ApplicationId appId, final ApplicationStateData appStateDataPB) throws Exception {
        final String nodeUpdatePath = this.getNodePath(this.rmAppRoot, appId.toString());
        if (ZKRMStateStore.LOG.isDebugEnabled()) {
            ZKRMStateStore.LOG.debug("Storing final state info for app: " + appId + " at: " + nodeUpdatePath);
        }
        final byte[] appStateData = appStateDataPB.getProto().toByteArray();
        if (this.existsWithRetries(nodeUpdatePath, true) != null) {
            this.setDataWithRetries(nodeUpdatePath, appStateData, -1);
        }
        else {
            this.createWithRetries(nodeUpdatePath, appStateData, this.zkAcl, CreateMode.PERSISTENT);
            ZKRMStateStore.LOG.debug(appId + " znode didn't exist. Created a new znode to" + " update the application state.");
        }
    }
    
    public synchronized void storeApplicationAttemptStateInternal(final ApplicationAttemptId appAttemptId, final ApplicationAttemptStateData attemptStateDataPB) throws Exception {
        final String appDirPath = this.getNodePath(this.rmAppRoot, appAttemptId.getApplicationId().toString());
        final String nodeCreatePath = this.getNodePath(appDirPath, appAttemptId.toString());
        if (ZKRMStateStore.LOG.isDebugEnabled()) {
            ZKRMStateStore.LOG.debug("Storing info for attempt: " + appAttemptId + " at: " + nodeCreatePath);
        }
        final byte[] attemptStateData = attemptStateDataPB.getProto().toByteArray();
        this.createWithRetries(nodeCreatePath, attemptStateData, this.zkAcl, CreateMode.PERSISTENT);
    }
    
    public synchronized void updateApplicationAttemptStateInternal(final ApplicationAttemptId appAttemptId, final ApplicationAttemptStateData attemptStateDataPB) throws Exception {
        final String appIdStr = appAttemptId.getApplicationId().toString();
        final String appAttemptIdStr = appAttemptId.toString();
        final String appDirPath = this.getNodePath(this.rmAppRoot, appIdStr);
        final String nodeUpdatePath = this.getNodePath(appDirPath, appAttemptIdStr);
        if (ZKRMStateStore.LOG.isDebugEnabled()) {
            ZKRMStateStore.LOG.debug("Storing final state info for attempt: " + appAttemptIdStr + " at: " + nodeUpdatePath);
        }
        final byte[] attemptStateData = attemptStateDataPB.getProto().toByteArray();
        if (this.existsWithRetries(nodeUpdatePath, true) != null) {
            this.setDataWithRetries(nodeUpdatePath, attemptStateData, -1);
        }
        else {
            this.createWithRetries(nodeUpdatePath, attemptStateData, this.zkAcl, CreateMode.PERSISTENT);
            ZKRMStateStore.LOG.debug(appAttemptId + " znode didn't exist. Created a new znode to" + " update the application attempt state.");
        }
    }
    
    public synchronized void removeApplicationStateInternal(final ApplicationState appState) throws Exception {
        final String appId = appState.getAppId().toString();
        final String appIdRemovePath = this.getNodePath(this.rmAppRoot, appId);
        final ArrayList<Op> opList = new ArrayList<Op>();
        for (final ApplicationAttemptId attemptId : appState.attempts.keySet()) {
            final String attemptRemovePath = this.getNodePath(appIdRemovePath, attemptId.toString());
            opList.add(Op.delete(attemptRemovePath, -1));
        }
        opList.add(Op.delete(appIdRemovePath, -1));
        if (ZKRMStateStore.LOG.isDebugEnabled()) {
            ZKRMStateStore.LOG.debug("Removing info for app: " + appId + " at: " + appIdRemovePath + " and its attempts.");
        }
        this.doMultiWithRetries(opList);
    }
    
    @Override
    protected synchronized void storeRMDelegationTokenAndSequenceNumberState(final RMDelegationTokenIdentifier rmDTIdentifier, final Long renewDate, final int latestSequenceNumber) throws Exception {
        final ArrayList<Op> opList = new ArrayList<Op>();
        this.addStoreOrUpdateOps(opList, rmDTIdentifier, renewDate, latestSequenceNumber, false);
        this.doMultiWithRetries(opList);
    }
    
    @Override
    protected synchronized void removeRMDelegationTokenState(final RMDelegationTokenIdentifier rmDTIdentifier) throws Exception {
        final ArrayList<Op> opList = new ArrayList<Op>();
        final String nodeRemovePath = this.getNodePath(this.delegationTokensRootPath, "RMDelegationToken_" + rmDTIdentifier.getSequenceNumber());
        if (ZKRMStateStore.LOG.isDebugEnabled()) {
            ZKRMStateStore.LOG.debug("Removing RMDelegationToken_" + rmDTIdentifier.getSequenceNumber());
        }
        if (this.existsWithRetries(nodeRemovePath, true) != null) {
            opList.add(Op.delete(nodeRemovePath, -1));
        }
        else {
            ZKRMStateStore.LOG.debug("Attempted to delete a non-existing znode " + nodeRemovePath);
        }
        this.doMultiWithRetries(opList);
    }
    
    @Override
    protected void updateRMDelegationTokenAndSequenceNumberInternal(final RMDelegationTokenIdentifier rmDTIdentifier, final Long renewDate, final int latestSequenceNumber) throws Exception {
        final ArrayList<Op> opList = new ArrayList<Op>();
        final String nodeRemovePath = this.getNodePath(this.delegationTokensRootPath, "RMDelegationToken_" + rmDTIdentifier.getSequenceNumber());
        if (this.existsWithRetries(nodeRemovePath, true) == null) {
            this.addStoreOrUpdateOps(opList, rmDTIdentifier, renewDate, latestSequenceNumber, false);
            ZKRMStateStore.LOG.debug("Attempted to update a non-existing znode " + nodeRemovePath);
        }
        else {
            this.addStoreOrUpdateOps(opList, rmDTIdentifier, renewDate, latestSequenceNumber, true);
        }
        this.doMultiWithRetries(opList);
    }
    
    private void addStoreOrUpdateOps(final ArrayList<Op> opList, final RMDelegationTokenIdentifier rmDTIdentifier, final Long renewDate, final int latestSequenceNumber, final boolean isUpdate) throws Exception {
        final String nodeCreatePath = this.getNodePath(this.delegationTokensRootPath, "RMDelegationToken_" + rmDTIdentifier.getSequenceNumber());
        final ByteArrayOutputStream seqOs = new ByteArrayOutputStream();
        final DataOutputStream seqOut = new DataOutputStream(seqOs);
        final RMDelegationTokenIdentifierData identifierData = new RMDelegationTokenIdentifierData(rmDTIdentifier, renewDate);
        try {
            if (ZKRMStateStore.LOG.isDebugEnabled()) {
                ZKRMStateStore.LOG.debug((isUpdate ? "Storing " : "Updating ") + "RMDelegationToken_" + rmDTIdentifier.getSequenceNumber());
            }
            if (isUpdate) {
                opList.add(Op.setData(nodeCreatePath, identifierData.toByteArray(), -1));
            }
            else {
                opList.add(Op.create(nodeCreatePath, identifierData.toByteArray(), this.zkAcl, CreateMode.PERSISTENT));
            }
            seqOut.writeInt(latestSequenceNumber);
            if (ZKRMStateStore.LOG.isDebugEnabled()) {
                ZKRMStateStore.LOG.debug((isUpdate ? "Storing " : "Updating ") + this.dtSequenceNumberPath + ". SequenceNumber: " + latestSequenceNumber);
            }
            opList.add(Op.setData(this.dtSequenceNumberPath, seqOs.toByteArray(), -1));
        }
        finally {
            seqOs.close();
        }
    }
    
    @Override
    protected synchronized void storeRMDTMasterKeyState(final DelegationKey delegationKey) throws Exception {
        final String nodeCreatePath = this.getNodePath(this.dtMasterKeysRootPath, "DelegationKey_" + delegationKey.getKeyId());
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final DataOutputStream fsOut = new DataOutputStream(os);
        if (ZKRMStateStore.LOG.isDebugEnabled()) {
            ZKRMStateStore.LOG.debug("Storing RMDelegationKey_" + delegationKey.getKeyId());
        }
        delegationKey.write(fsOut);
        try {
            this.createWithRetries(nodeCreatePath, os.toByteArray(), this.zkAcl, CreateMode.PERSISTENT);
        }
        finally {
            os.close();
        }
    }
    
    @Override
    protected synchronized void removeRMDTMasterKeyState(final DelegationKey delegationKey) throws Exception {
        final String nodeRemovePath = this.getNodePath(this.dtMasterKeysRootPath, "DelegationKey_" + delegationKey.getKeyId());
        if (ZKRMStateStore.LOG.isDebugEnabled()) {
            ZKRMStateStore.LOG.debug("Removing RMDelegationKey_" + delegationKey.getKeyId());
        }
        if (this.existsWithRetries(nodeRemovePath, true) != null) {
            this.doMultiWithRetries(Op.delete(nodeRemovePath, -1));
        }
        else {
            ZKRMStateStore.LOG.debug("Attempted to delete a non-existing znode " + nodeRemovePath);
        }
    }
    
    @Override
    public synchronized void deleteStore() throws Exception {
        if (this.existsWithRetries(this.zkRootNodePath, true) != null) {
            this.deleteWithRetries(this.zkRootNodePath, true);
        }
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    public synchronized void processWatchEvent(final WatchedEvent event) throws Exception {
        final Watcher.Event.EventType eventType = event.getType();
        ZKRMStateStore.LOG.info("Watcher event type: " + eventType + " with state:" + event.getState() + " for path:" + event.getPath() + " for " + this);
        if (eventType == Watcher.Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected: {
                    ZKRMStateStore.LOG.info("ZKRMStateStore Session connected");
                    if (this.oldZkClient != null) {
                        this.zkClient = this.oldZkClient;
                        this.oldZkClient = null;
                        this.notifyAll();
                        ZKRMStateStore.LOG.info("ZKRMStateStore Session restored");
                        break;
                    }
                    break;
                }
                case Disconnected: {
                    ZKRMStateStore.LOG.info("ZKRMStateStore Session disconnected");
                    this.oldZkClient = this.zkClient;
                    this.zkClient = null;
                    break;
                }
                case Expired: {
                    ZKRMStateStore.LOG.info("ZKRMStateStore Session expired");
                    this.createConnection();
                    break;
                }
                default: {
                    ZKRMStateStore.LOG.error("Unexpected Zookeeper watch event state: " + event.getState());
                    break;
                }
            }
        }
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    String getNodePath(final String root, final String nodeName) {
        return root + "/" + nodeName;
    }
    
    private synchronized void doMultiWithRetries(final List<Op> opList) throws Exception {
        final List<Op> execOpList = new ArrayList<Op>(opList.size() + 2);
        execOpList.add(this.createFencingNodePathOp);
        execOpList.addAll(opList);
        execOpList.add(this.deleteFencingNodePathOp);
        new ZKAction<Void>() {
            public Void run() throws KeeperException, InterruptedException {
                ZKRMStateStore.this.zkClient.multi(execOpList);
                return null;
            }
        }.runWithRetries();
    }
    
    private void doMultiWithRetries(final Op op) throws Exception {
        this.doMultiWithRetries(Collections.singletonList(op));
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    public void createWithRetries(final String path, final byte[] data, final List<ACL> acl, final CreateMode mode) throws Exception {
        this.doMultiWithRetries(Op.create(path, data, acl, mode));
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    public void setDataWithRetries(final String path, final byte[] data, final int version) throws Exception {
        this.doMultiWithRetries(Op.setData(path, data, version));
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    public byte[] getDataWithRetries(final String path, final boolean watch) throws Exception {
        return new ZKAction<byte[]>() {
            public byte[] run() throws KeeperException, InterruptedException {
                return ZKRMStateStore.this.zkClient.getData(path, watch, null);
            }
        }.runWithRetries();
    }
    
    private List<ACL> getACLWithRetries(final String path, final Stat stat) throws Exception {
        return new ZKAction<List<ACL>>() {
            public List<ACL> run() throws KeeperException, InterruptedException {
                return ZKRMStateStore.this.zkClient.getACL(path, stat);
            }
        }.runWithRetries();
    }
    
    private List<String> getChildrenWithRetries(final String path, final boolean watch) throws Exception {
        return new ZKAction<List<String>>() {
            @Override
            List<String> run() throws KeeperException, InterruptedException {
                return ZKRMStateStore.this.zkClient.getChildren(path, watch);
            }
        }.runWithRetries();
    }
    
    private Stat existsWithRetries(final String path, final boolean watch) throws Exception {
        return new ZKAction<Stat>() {
            @Override
            Stat run() throws KeeperException, InterruptedException {
                return ZKRMStateStore.this.zkClient.exists(path, watch);
            }
        }.runWithRetries();
    }
    
    private void deleteWithRetries(final String path, final boolean watch) throws Exception {
        new ZKAction<Void>() {
            @Override
            Void run() throws KeeperException, InterruptedException {
                ZKRMStateStore.this.recursiveDeleteWithRetriesHelper(path, watch);
                return null;
            }
        }.runWithRetries();
    }
    
    private void recursiveDeleteWithRetriesHelper(final String path, final boolean watch) throws KeeperException, InterruptedException {
        final List<String> children = this.zkClient.getChildren(path, watch);
        for (final String child : children) {
            this.recursiveDeleteWithRetriesHelper(path + "/" + child, false);
        }
        this.zkClient.delete(path, -1);
    }
    
    private synchronized void createConnection() throws IOException, InterruptedException {
        this.closeZkClients();
        for (int retries = 0; retries < this.numRetries && this.zkClient == null; ++retries) {
            try {
                this.zkClient = this.getNewZooKeeper();
                for (final ZKUtil.ZKAuthInfo zkAuth : this.zkAuths) {
                    this.zkClient.addAuthInfo(zkAuth.getScheme(), zkAuth.getAuth());
                }
                if (this.useDefaultFencingScheme) {
                    this.zkClient.addAuthInfo(this.zkRootNodeAuthScheme, (this.zkRootNodeUsername + ":" + this.zkRootNodePassword).getBytes());
                }
            }
            catch (IOException ioe) {
                ZKRMStateStore.LOG.info("Failed to connect to the ZooKeeper on attempt - " + (retries + 1));
                ioe.printStackTrace();
            }
        }
        if (this.zkClient == null) {
            ZKRMStateStore.LOG.error("Unable to connect to Zookeeper");
            throw new YarnRuntimeException("Unable to connect to Zookeeper");
        }
        this.notifyAll();
        ZKRMStateStore.LOG.info("Created new ZK connection");
    }
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    @VisibleForTesting
    protected synchronized ZooKeeper getNewZooKeeper() throws IOException, InterruptedException {
        final ZooKeeper zk = new ZooKeeper(this.zkHostPort, this.zkSessionTimeout, null);
        zk.register(new ForwardingWatcher());
        return zk;
    }
    
    @Override
    public synchronized void storeOrUpdateAMRMTokenSecretManagerState(final AMRMTokenSecretManagerState amrmTokenSecretManagerState, final boolean isUpdate) {
        final AMRMTokenSecretManagerState data = AMRMTokenSecretManagerState.newInstance(amrmTokenSecretManagerState);
        final byte[] stateData = data.getProto().toByteArray();
        try {
            this.setDataWithRetries(this.amrmTokenSecretManagerRoot, stateData, -1);
        }
        catch (Exception ex) {
            ZKRMStateStore.LOG.info("Error storing info for AMRMTokenSecretManager", ex);
            this.notifyStoreOperationFailed(ex);
        }
    }
    
    static {
        LOG = LogFactory.getLog(ZKRMStateStore.class);
        CURRENT_VERSION_INFO = Version.newInstance(1, 2);
    }
    
    private final class ForwardingWatcher implements Watcher
    {
        @Override
        public void process(final WatchedEvent event) {
            try {
                ZKRMStateStore.this.processWatchEvent(event);
            }
            catch (Throwable t) {
                ZKRMStateStore.LOG.error("Failed to process watcher event " + event + ": " + StringUtils.stringifyException(t));
            }
        }
    }
    
    private class VerifyActiveStatusThread extends Thread
    {
        private List<Op> emptyOpList;
        
        VerifyActiveStatusThread() {
            super(VerifyActiveStatusThread.class.getName());
            this.emptyOpList = new ArrayList<Op>();
        }
        
        @Override
        public void run() {
            try {
                while (true) {
                    ZKRMStateStore.this.doMultiWithRetries(this.emptyOpList);
                    Thread.sleep(ZKRMStateStore.this.zkSessionTimeout);
                }
            }
            catch (InterruptedException ie) {
                ZKRMStateStore.LOG.info(VerifyActiveStatusThread.class.getName() + " thread " + "interrupted! Exiting!");
            }
            catch (Exception e) {
                ZKRMStateStore.this.notifyStoreOperationFailed(new StoreFencedException());
            }
        }
    }
    
    private abstract class ZKAction<T>
    {
        abstract T run() throws KeeperException, InterruptedException;
        
        T runWithCheck() throws Exception {
            final long startTime = System.currentTimeMillis();
            synchronized (ZKRMStateStore.this) {
                while (ZKRMStateStore.this.zkClient == null) {
                    ZKRMStateStore.this.wait(ZKRMStateStore.this.zkSessionTimeout);
                    if (ZKRMStateStore.this.zkClient != null) {
                        break;
                    }
                    if (System.currentTimeMillis() - startTime > ZKRMStateStore.this.zkSessionTimeout) {
                        throw new IOException("Wait for ZKClient creation timed out");
                    }
                }
                return this.run();
            }
        }
        
        private boolean shouldRetry(final KeeperException.Code code) {
            switch (code) {
                case CONNECTIONLOSS:
                case OPERATIONTIMEOUT: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        
        T runWithRetries() throws Exception {
            int retry = 0;
            try {
                return this.runWithCheck();
            }
            catch (KeeperException.NoAuthException nae) {
                if (HAUtil.isHAEnabled(ZKRMStateStore.this.getConfig())) {
                    throw new StoreFencedException();
                }
                return this.runWithCheck();
            }
            catch (KeeperException ke) {
                if (ke.code() == KeeperException.Code.NODEEXISTS) {
                    ZKRMStateStore.LOG.info("znode already exists!");
                    return null;
                }
                ZKRMStateStore.LOG.info("Exception while executing a ZK operation.", ke);
                if (this.shouldRetry(ke.code()) && ++retry < ZKRMStateStore.this.numRetries) {
                    ZKRMStateStore.LOG.info("Retrying operation on ZK. Retry no. " + retry);
                    Thread.sleep(ZKRMStateStore.this.zkRetryInterval);
                    return this.runWithCheck();
                }
                ZKRMStateStore.LOG.info("Maxed out ZK retries. Giving up!");
                throw ke;
            }
        }
    }
}
