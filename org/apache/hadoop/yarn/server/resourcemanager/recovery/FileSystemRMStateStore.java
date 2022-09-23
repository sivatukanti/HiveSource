// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.yarn.security.client.YARNDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationAttemptStateData;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.RMDelegationTokenIdentifierData;
import java.io.DataInput;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.apache.hadoop.fs.PathFilter;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.List;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.ApplicationAttemptStateDataPBImpl;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.ApplicationStateDataPBImpl;
import org.apache.hadoop.yarn.util.ConverterUtils;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.AMRMTokenSecretManagerState;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.AMRMTokenSecretManagerStatePBImpl;
import java.io.FileNotFoundException;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.Epoch;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb.EpochPBImpl;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.yarn.server.records.impl.pb.VersionPBImpl;
import org.apache.hadoop.yarn.proto.YarnServerCommonProtos;
import org.apache.hadoop.conf.Configuration;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.yarn.server.records.Version;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FileSystemRMStateStore extends RMStateStore
{
    public static final Log LOG;
    protected static final String ROOT_DIR_NAME = "FSRMStateRoot";
    protected static final Version CURRENT_VERSION_INFO;
    protected static final String AMRMTOKEN_SECRET_MANAGER_NODE = "AMRMTokenSecretManagerNode";
    protected FileSystem fs;
    private Path rootDirPath;
    @InterfaceAudience.Private
    @VisibleForTesting
    Path rmDTSecretManagerRoot;
    private Path rmAppRoot;
    private Path dtSequenceNumberPath;
    @VisibleForTesting
    Path fsWorkingPath;
    Path amrmTokenSecretManagerRoot;
    
    public FileSystemRMStateStore() {
        this.dtSequenceNumberPath = null;
    }
    
    public synchronized void initInternal(final Configuration conf) throws Exception {
        this.fsWorkingPath = new Path(conf.get("yarn.resourcemanager.fs.state-store.uri"));
        this.rootDirPath = new Path(this.fsWorkingPath, "FSRMStateRoot");
        this.rmDTSecretManagerRoot = new Path(this.rootDirPath, "RMDTSecretManagerRoot");
        this.rmAppRoot = new Path(this.rootDirPath, "RMAppRoot");
        this.amrmTokenSecretManagerRoot = new Path(this.rootDirPath, "AMRMTokenSecretManagerRoot");
    }
    
    @Override
    protected synchronized void startInternal() throws Exception {
        final Configuration conf = new Configuration(this.getConfig());
        conf.setBoolean("dfs.client.retry.policy.enabled", true);
        final String retryPolicy = conf.get("yarn.resourcemanager.fs.state-store.retry-policy-spec", "2000, 500");
        conf.set("dfs.client.retry.policy.spec", retryPolicy);
        (this.fs = this.fsWorkingPath.getFileSystem(conf)).mkdirs(this.rmDTSecretManagerRoot);
        this.fs.mkdirs(this.rmAppRoot);
        this.fs.mkdirs(this.amrmTokenSecretManagerRoot);
    }
    
    @Override
    protected synchronized void closeInternal() throws Exception {
        this.fs.close();
    }
    
    @Override
    protected Version getCurrentVersion() {
        return FileSystemRMStateStore.CURRENT_VERSION_INFO;
    }
    
    @Override
    protected synchronized Version loadVersion() throws Exception {
        final Path versionNodePath = this.getNodePath(this.rootDirPath, "RMVersionNode");
        if (this.fs.exists(versionNodePath)) {
            final FileStatus status = this.fs.getFileStatus(versionNodePath);
            final byte[] data = this.readFile(versionNodePath, status.getLen());
            final Version version = new VersionPBImpl(YarnServerCommonProtos.VersionProto.parseFrom(data));
            return version;
        }
        return null;
    }
    
    @Override
    protected synchronized void storeVersion() throws Exception {
        final Path versionNodePath = this.getNodePath(this.rootDirPath, "RMVersionNode");
        final byte[] data = ((VersionPBImpl)FileSystemRMStateStore.CURRENT_VERSION_INFO).getProto().toByteArray();
        if (this.fs.exists(versionNodePath)) {
            this.updateFile(versionNodePath, data);
        }
        else {
            this.writeFile(versionNodePath, data);
        }
    }
    
    @Override
    public synchronized long getAndIncrementEpoch() throws Exception {
        final Path epochNodePath = this.getNodePath(this.rootDirPath, "EpochNode");
        long currentEpoch = 0L;
        if (this.fs.exists(epochNodePath)) {
            final FileStatus status = this.fs.getFileStatus(epochNodePath);
            final byte[] data = this.readFile(epochNodePath, status.getLen());
            final Epoch epoch = new EpochPBImpl(YarnServerResourceManagerRecoveryProtos.EpochProto.parseFrom(data));
            currentEpoch = epoch.getEpoch();
            final byte[] storeData = Epoch.newInstance(currentEpoch + 1L).getProto().toByteArray();
            this.updateFile(epochNodePath, storeData);
        }
        else {
            final byte[] storeData2 = Epoch.newInstance(currentEpoch + 1L).getProto().toByteArray();
            this.writeFile(epochNodePath, storeData2);
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
        this.checkAndResumeUpdateOperation(this.amrmTokenSecretManagerRoot);
        final Path amrmTokenSecretManagerStateDataDir = new Path(this.amrmTokenSecretManagerRoot, "AMRMTokenSecretManagerNode");
        FileStatus status;
        try {
            status = this.fs.getFileStatus(amrmTokenSecretManagerStateDataDir);
            assert status.isFile();
        }
        catch (FileNotFoundException ex) {
            return;
        }
        final byte[] data = this.readFile(amrmTokenSecretManagerStateDataDir, status.getLen());
        final AMRMTokenSecretManagerStatePBImpl stateData = new AMRMTokenSecretManagerStatePBImpl(YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto.parseFrom(data));
        rmState.amrmTokenSecretManagerState = AMRMTokenSecretManagerState.newInstance(stateData.getCurrentMasterKey(), stateData.getNextMasterKey());
    }
    
    private void loadRMAppState(final RMState rmState) throws Exception {
        try {
            final List<ApplicationAttemptState> attempts = new ArrayList<ApplicationAttemptState>();
            for (final FileStatus appDir : this.fs.listStatus(this.rmAppRoot)) {
                this.checkAndResumeUpdateOperation(appDir.getPath());
                for (final FileStatus childNodeStatus : this.fs.listStatus(appDir.getPath())) {
                    assert childNodeStatus.isFile();
                    final String childNodeName = childNodeStatus.getPath().getName();
                    if (!this.checkAndRemovePartialRecord(childNodeStatus.getPath())) {
                        final byte[] childData = this.readFile(childNodeStatus.getPath(), childNodeStatus.getLen());
                        if (childNodeName.startsWith("application_")) {
                            if (FileSystemRMStateStore.LOG.isDebugEnabled()) {
                                FileSystemRMStateStore.LOG.debug("Loading application from node: " + childNodeName);
                            }
                            final ApplicationId appId = ConverterUtils.toApplicationId(childNodeName);
                            final ApplicationStateDataPBImpl appStateData = new ApplicationStateDataPBImpl(YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto.parseFrom(childData));
                            final ApplicationState appState = new ApplicationState(appStateData.getSubmitTime(), appStateData.getStartTime(), appStateData.getApplicationSubmissionContext(), appStateData.getUser(), appStateData.getState(), appStateData.getDiagnostics(), appStateData.getFinishTime());
                            assert appId.equals(appState.context.getApplicationId());
                            rmState.appState.put(appId, appState);
                        }
                        else if (childNodeName.startsWith("appattempt_")) {
                            if (FileSystemRMStateStore.LOG.isDebugEnabled()) {
                                FileSystemRMStateStore.LOG.debug("Loading application attempt from node: " + childNodeName);
                            }
                            final ApplicationAttemptId attemptId = ConverterUtils.toApplicationAttemptId(childNodeName);
                            final ApplicationAttemptStateDataPBImpl attemptStateData = new ApplicationAttemptStateDataPBImpl(YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto.parseFrom(childData));
                            Credentials credentials = null;
                            if (attemptStateData.getAppAttemptTokens() != null) {
                                credentials = new Credentials();
                                final DataInputByteBuffer dibb = new DataInputByteBuffer();
                                dibb.reset(attemptStateData.getAppAttemptTokens());
                                credentials.readTokenStorageStream(dibb);
                            }
                            final ApplicationAttemptState attemptState = new ApplicationAttemptState(attemptId, attemptStateData.getMasterContainer(), credentials, attemptStateData.getStartTime(), attemptStateData.getState(), attemptStateData.getFinalTrackingUrl(), attemptStateData.getDiagnostics(), attemptStateData.getFinalApplicationStatus(), attemptStateData.getAMContainerExitStatus(), attemptStateData.getFinishTime(), attemptStateData.getMemorySeconds(), attemptStateData.getVcoreSeconds());
                            assert attemptId.equals(attemptState.getAttemptId());
                            attempts.add(attemptState);
                        }
                        else {
                            FileSystemRMStateStore.LOG.info("Unknown child node with name: " + childNodeName);
                        }
                    }
                }
            }
            for (final ApplicationAttemptState attemptState2 : attempts) {
                final ApplicationId appId2 = attemptState2.getAttemptId().getApplicationId();
                final ApplicationState appState2 = rmState.appState.get(appId2);
                assert appState2 != null;
                appState2.attempts.put(attemptState2.getAttemptId(), attemptState2);
            }
            FileSystemRMStateStore.LOG.info("Done loading applications from FS state store");
        }
        catch (Exception e) {
            FileSystemRMStateStore.LOG.error("Failed to load state.", e);
            throw e;
        }
    }
    
    private boolean checkAndRemovePartialRecord(final Path record) throws IOException {
        if (record.getName().endsWith(".tmp")) {
            FileSystemRMStateStore.LOG.error("incomplete rm state store entry found :" + record);
            this.fs.delete(record, false);
            return true;
        }
        return false;
    }
    
    private void checkAndResumeUpdateOperation(final Path path) throws Exception {
        final FileStatus[] arr$;
        final FileStatus[] newChildNodes = arr$ = this.fs.listStatus(path, new PathFilter() {
            @Override
            public boolean accept(final Path path) {
                return path.getName().endsWith(".new");
            }
        });
        for (final FileStatus newChildNodeStatus : arr$) {
            assert newChildNodeStatus.isFile();
            final String newChildNodeName = newChildNodeStatus.getPath().getName();
            final String childNodeName = newChildNodeName.substring(0, newChildNodeName.length() - ".new".length());
            final Path childNodePath = new Path(newChildNodeStatus.getPath().getParent(), childNodeName);
            this.replaceFile(newChildNodeStatus.getPath(), childNodePath);
        }
    }
    
    private void loadRMDTSecretManagerState(final RMState rmState) throws Exception {
        this.checkAndResumeUpdateOperation(this.rmDTSecretManagerRoot);
        final FileStatus[] arr$;
        final FileStatus[] childNodes = arr$ = this.fs.listStatus(this.rmDTSecretManagerRoot);
        for (final FileStatus childNodeStatus : arr$) {
            assert childNodeStatus.isFile();
            final String childNodeName = childNodeStatus.getPath().getName();
            if (!this.checkAndRemovePartialRecord(childNodeStatus.getPath())) {
                if (childNodeName.startsWith("RMDTSequenceNumber_")) {
                    rmState.rmSecretManagerState.dtSequenceNumber = Integer.parseInt(childNodeName.split("_")[1]);
                }
                else {
                    final Path childNodePath = this.getNodePath(this.rmDTSecretManagerRoot, childNodeName);
                    final byte[] childData = this.readFile(childNodePath, childNodeStatus.getLen());
                    final ByteArrayInputStream is = new ByteArrayInputStream(childData);
                    final DataInputStream fsIn = new DataInputStream(is);
                    if (childNodeName.startsWith("DelegationKey_")) {
                        final DelegationKey key = new DelegationKey();
                        key.readFields(fsIn);
                        rmState.rmSecretManagerState.masterKeyState.add(key);
                        if (FileSystemRMStateStore.LOG.isDebugEnabled()) {
                            FileSystemRMStateStore.LOG.debug("Loaded delegation key: keyId=" + key.getKeyId() + ", expirationDate=" + key.getExpiryDate());
                        }
                    }
                    else if (childNodeName.startsWith("RMDelegationToken_")) {
                        final RMDelegationTokenIdentifierData identifierData = new RMDelegationTokenIdentifierData();
                        identifierData.readFields(fsIn);
                        final RMDelegationTokenIdentifier identifier = identifierData.getTokenIdentifier();
                        final long renewDate = identifierData.getRenewDate();
                        rmState.rmSecretManagerState.delegationTokenState.put(identifier, renewDate);
                        if (FileSystemRMStateStore.LOG.isDebugEnabled()) {
                            FileSystemRMStateStore.LOG.debug("Loaded RMDelegationTokenIdentifier: " + identifier + " renewDate=" + renewDate);
                        }
                    }
                    else {
                        FileSystemRMStateStore.LOG.warn("Unknown file for recovering RMDelegationTokenSecretManager");
                    }
                    fsIn.close();
                }
            }
        }
    }
    
    public synchronized void storeApplicationStateInternal(final ApplicationId appId, final ApplicationStateData appStateDataPB) throws Exception {
        final String appIdStr = appId.toString();
        final Path appDirPath = this.getAppDir(this.rmAppRoot, appIdStr);
        this.fs.mkdirs(appDirPath);
        final Path nodeCreatePath = this.getNodePath(appDirPath, appIdStr);
        FileSystemRMStateStore.LOG.info("Storing info for app: " + appId + " at: " + nodeCreatePath);
        final byte[] appStateData = appStateDataPB.getProto().toByteArray();
        try {
            this.writeFile(nodeCreatePath, appStateData);
        }
        catch (Exception e) {
            FileSystemRMStateStore.LOG.info("Error storing info for app: " + appId, e);
            throw e;
        }
    }
    
    public synchronized void updateApplicationStateInternal(final ApplicationId appId, final ApplicationStateData appStateDataPB) throws Exception {
        final String appIdStr = appId.toString();
        final Path appDirPath = this.getAppDir(this.rmAppRoot, appIdStr);
        final Path nodeCreatePath = this.getNodePath(appDirPath, appIdStr);
        FileSystemRMStateStore.LOG.info("Updating info for app: " + appId + " at: " + nodeCreatePath);
        final byte[] appStateData = appStateDataPB.getProto().toByteArray();
        try {
            this.updateFile(nodeCreatePath, appStateData);
        }
        catch (Exception e) {
            FileSystemRMStateStore.LOG.info("Error updating info for app: " + appId, e);
            throw e;
        }
    }
    
    public synchronized void storeApplicationAttemptStateInternal(final ApplicationAttemptId appAttemptId, final ApplicationAttemptStateData attemptStateDataPB) throws Exception {
        final Path appDirPath = this.getAppDir(this.rmAppRoot, appAttemptId.getApplicationId().toString());
        final Path nodeCreatePath = this.getNodePath(appDirPath, appAttemptId.toString());
        FileSystemRMStateStore.LOG.info("Storing info for attempt: " + appAttemptId + " at: " + nodeCreatePath);
        final byte[] attemptStateData = attemptStateDataPB.getProto().toByteArray();
        try {
            this.writeFile(nodeCreatePath, attemptStateData);
        }
        catch (Exception e) {
            FileSystemRMStateStore.LOG.info("Error storing info for attempt: " + appAttemptId, e);
            throw e;
        }
    }
    
    public synchronized void updateApplicationAttemptStateInternal(final ApplicationAttemptId appAttemptId, final ApplicationAttemptStateData attemptStateDataPB) throws Exception {
        final Path appDirPath = this.getAppDir(this.rmAppRoot, appAttemptId.getApplicationId().toString());
        final Path nodeCreatePath = this.getNodePath(appDirPath, appAttemptId.toString());
        FileSystemRMStateStore.LOG.info("Updating info for attempt: " + appAttemptId + " at: " + nodeCreatePath);
        final byte[] attemptStateData = attemptStateDataPB.getProto().toByteArray();
        try {
            this.updateFile(nodeCreatePath, attemptStateData);
        }
        catch (Exception e) {
            FileSystemRMStateStore.LOG.info("Error updating info for attempt: " + appAttemptId, e);
            throw e;
        }
    }
    
    public synchronized void removeApplicationStateInternal(final ApplicationState appState) throws Exception {
        final String appId = appState.getAppId().toString();
        final Path nodeRemovePath = this.getAppDir(this.rmAppRoot, appId);
        FileSystemRMStateStore.LOG.info("Removing info for app: " + appId + " at: " + nodeRemovePath);
        this.deleteFile(nodeRemovePath);
    }
    
    public synchronized void storeRMDelegationTokenAndSequenceNumberState(final RMDelegationTokenIdentifier identifier, final Long renewDate, final int latestSequenceNumber) throws Exception {
        this.storeOrUpdateRMDelegationTokenAndSequenceNumberState(identifier, renewDate, latestSequenceNumber, false);
    }
    
    public synchronized void removeRMDelegationTokenState(final RMDelegationTokenIdentifier identifier) throws Exception {
        final Path nodeCreatePath = this.getNodePath(this.rmDTSecretManagerRoot, "RMDelegationToken_" + identifier.getSequenceNumber());
        FileSystemRMStateStore.LOG.info("Removing RMDelegationToken_" + identifier.getSequenceNumber());
        this.deleteFile(nodeCreatePath);
    }
    
    @Override
    protected void updateRMDelegationTokenAndSequenceNumberInternal(final RMDelegationTokenIdentifier rmDTIdentifier, final Long renewDate, final int latestSequenceNumber) throws Exception {
        this.storeOrUpdateRMDelegationTokenAndSequenceNumberState(rmDTIdentifier, renewDate, latestSequenceNumber, true);
    }
    
    private void storeOrUpdateRMDelegationTokenAndSequenceNumberState(final RMDelegationTokenIdentifier identifier, final Long renewDate, final int latestSequenceNumber, final boolean isUpdate) throws Exception {
        final Path nodeCreatePath = this.getNodePath(this.rmDTSecretManagerRoot, "RMDelegationToken_" + identifier.getSequenceNumber());
        final RMDelegationTokenIdentifierData identifierData = new RMDelegationTokenIdentifierData(identifier, renewDate);
        if (isUpdate) {
            FileSystemRMStateStore.LOG.info("Updating RMDelegationToken_" + identifier.getSequenceNumber());
            this.updateFile(nodeCreatePath, identifierData.toByteArray());
        }
        else {
            FileSystemRMStateStore.LOG.info("Storing RMDelegationToken_" + identifier.getSequenceNumber());
            this.writeFile(nodeCreatePath, identifierData.toByteArray());
        }
        final Path latestSequenceNumberPath = this.getNodePath(this.rmDTSecretManagerRoot, "RMDTSequenceNumber_" + latestSequenceNumber);
        FileSystemRMStateStore.LOG.info("Storing RMDTSequenceNumber_" + latestSequenceNumber);
        if (this.dtSequenceNumberPath == null) {
            if (!this.createFile(latestSequenceNumberPath)) {
                throw new Exception("Failed to create " + latestSequenceNumberPath);
            }
        }
        else if (!this.renameFile(this.dtSequenceNumberPath, latestSequenceNumberPath)) {
            throw new Exception("Failed to rename " + this.dtSequenceNumberPath);
        }
        this.dtSequenceNumberPath = latestSequenceNumberPath;
    }
    
    public synchronized void storeRMDTMasterKeyState(final DelegationKey masterKey) throws Exception {
        final Path nodeCreatePath = this.getNodePath(this.rmDTSecretManagerRoot, "DelegationKey_" + masterKey.getKeyId());
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final DataOutputStream fsOut = new DataOutputStream(os);
        FileSystemRMStateStore.LOG.info("Storing RMDelegationKey_" + masterKey.getKeyId());
        masterKey.write(fsOut);
        this.writeFile(nodeCreatePath, os.toByteArray());
        fsOut.close();
    }
    
    public synchronized void removeRMDTMasterKeyState(final DelegationKey masterKey) throws Exception {
        final Path nodeCreatePath = this.getNodePath(this.rmDTSecretManagerRoot, "DelegationKey_" + masterKey.getKeyId());
        FileSystemRMStateStore.LOG.info("Removing RMDelegationKey_" + masterKey.getKeyId());
        this.deleteFile(nodeCreatePath);
    }
    
    @Override
    public synchronized void deleteStore() throws IOException {
        if (this.fs.exists(this.rootDirPath)) {
            this.fs.delete(this.rootDirPath, true);
        }
    }
    
    private Path getAppDir(final Path root, final String appId) {
        return this.getNodePath(root, appId);
    }
    
    private void deleteFile(final Path deletePath) throws Exception {
        if (!this.fs.delete(deletePath, true)) {
            throw new Exception("Failed to delete " + deletePath);
        }
    }
    
    private byte[] readFile(final Path inputPath, final long len) throws Exception {
        FSDataInputStream fsIn = null;
        try {
            fsIn = this.fs.open(inputPath);
            final byte[] data = new byte[(int)len];
            fsIn.readFully(data);
            return data;
        }
        finally {
            IOUtils.cleanup(FileSystemRMStateStore.LOG, fsIn);
        }
    }
    
    private void writeFile(final Path outputPath, final byte[] data) throws Exception {
        final Path tempPath = new Path(outputPath.getParent(), outputPath.getName() + ".tmp");
        FSDataOutputStream fsOut = null;
        try {
            fsOut = this.fs.create(tempPath, true);
            fsOut.write(data);
            fsOut.close();
            fsOut = null;
            this.fs.rename(tempPath, outputPath);
        }
        finally {
            IOUtils.cleanup(FileSystemRMStateStore.LOG, fsOut);
        }
    }
    
    protected void updateFile(final Path outputPath, final byte[] data) throws Exception {
        final Path newPath = new Path(outputPath.getParent(), outputPath.getName() + ".new");
        this.writeFile(newPath, data);
        this.replaceFile(newPath, outputPath);
    }
    
    protected void replaceFile(final Path srcPath, final Path dstPath) throws Exception {
        if (this.fs.exists(dstPath)) {
            this.deleteFile(dstPath);
        }
        else {
            FileSystemRMStateStore.LOG.info("File doesn't exist. Skip deleting the file " + dstPath);
        }
        this.fs.rename(srcPath, dstPath);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    boolean renameFile(final Path src, final Path dst) throws Exception {
        return this.fs.rename(src, dst);
    }
    
    private boolean createFile(final Path newFile) throws Exception {
        return this.fs.createNewFile(newFile);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    Path getNodePath(final Path root, final String nodeName) {
        return new Path(root, nodeName);
    }
    
    @Override
    public synchronized void storeOrUpdateAMRMTokenSecretManagerState(final AMRMTokenSecretManagerState amrmTokenSecretManagerState, final boolean isUpdate) {
        final Path nodeCreatePath = this.getNodePath(this.amrmTokenSecretManagerRoot, "AMRMTokenSecretManagerNode");
        final AMRMTokenSecretManagerState data = AMRMTokenSecretManagerState.newInstance(amrmTokenSecretManagerState);
        final byte[] stateData = data.getProto().toByteArray();
        try {
            if (isUpdate) {
                this.updateFile(nodeCreatePath, stateData);
            }
            else {
                this.writeFile(nodeCreatePath, stateData);
            }
        }
        catch (Exception ex) {
            FileSystemRMStateStore.LOG.info("Error storing info for AMRMTokenSecretManager", ex);
            this.notifyStoreOperationFailed(ex);
        }
    }
    
    static {
        LOG = LogFactory.getLog(FileSystemRMStateStore.class);
        CURRENT_VERSION_INFO = Version.newInstance(1, 2);
    }
}
