// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

import org.apache.hadoop.yarn.server.records.Version;
import java.util.Set;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.io.DataInputByteBuffer;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationAttemptStateData;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.AMRMTokenSecretManagerState;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import java.util.Collection;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.Map;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class MemoryRMStateStore extends RMStateStore
{
    RMState state;
    private long epoch;
    
    public MemoryRMStateStore() {
        this.state = new RMState();
        this.epoch = 0L;
    }
    
    @VisibleForTesting
    public RMState getState() {
        return this.state;
    }
    
    @Override
    public void checkVersion() throws Exception {
    }
    
    @Override
    public synchronized long getAndIncrementEpoch() throws Exception {
        final long currentEpoch = this.epoch;
        ++this.epoch;
        return currentEpoch;
    }
    
    @Override
    public synchronized RMState loadState() throws Exception {
        final RMState returnState = new RMState();
        returnState.appState.putAll(this.state.appState);
        returnState.rmSecretManagerState.getMasterKeyState().addAll(this.state.rmSecretManagerState.getMasterKeyState());
        returnState.rmSecretManagerState.getTokenState().putAll(this.state.rmSecretManagerState.getTokenState());
        returnState.rmSecretManagerState.dtSequenceNumber = this.state.rmSecretManagerState.dtSequenceNumber;
        returnState.amrmTokenSecretManagerState = ((this.state.amrmTokenSecretManagerState == null) ? null : AMRMTokenSecretManagerState.newInstance(this.state.amrmTokenSecretManagerState));
        return returnState;
    }
    
    public synchronized void initInternal(final Configuration conf) {
    }
    
    @Override
    protected synchronized void startInternal() throws Exception {
    }
    
    @Override
    protected synchronized void closeInternal() throws Exception {
    }
    
    public void storeApplicationStateInternal(final ApplicationId appId, final ApplicationStateData appStateData) throws Exception {
        final ApplicationState appState = new ApplicationState(appStateData.getSubmitTime(), appStateData.getStartTime(), appStateData.getApplicationSubmissionContext(), appStateData.getUser());
        this.state.appState.put(appId, appState);
    }
    
    public void updateApplicationStateInternal(final ApplicationId appId, final ApplicationStateData appStateData) throws Exception {
        final ApplicationState updatedAppState = new ApplicationState(appStateData.getSubmitTime(), appStateData.getStartTime(), appStateData.getApplicationSubmissionContext(), appStateData.getUser(), appStateData.getState(), appStateData.getDiagnostics(), appStateData.getFinishTime());
        MemoryRMStateStore.LOG.info("Updating final state " + appStateData.getState() + " for app: " + appId);
        if (this.state.appState.get(appId) != null) {
            updatedAppState.attempts.putAll(this.state.appState.get(appId).attempts);
        }
        this.state.appState.put(appId, updatedAppState);
    }
    
    public synchronized void storeApplicationAttemptStateInternal(final ApplicationAttemptId appAttemptId, final ApplicationAttemptStateData attemptStateData) throws Exception {
        Credentials credentials = null;
        if (attemptStateData.getAppAttemptTokens() != null) {
            final DataInputByteBuffer dibb = new DataInputByteBuffer();
            credentials = new Credentials();
            dibb.reset(attemptStateData.getAppAttemptTokens());
            credentials.readTokenStorageStream(dibb);
        }
        final ApplicationAttemptState attemptState = new ApplicationAttemptState(appAttemptId, attemptStateData.getMasterContainer(), credentials, attemptStateData.getStartTime(), attemptStateData.getMemorySeconds(), attemptStateData.getVcoreSeconds());
        final ApplicationState appState = this.state.getApplicationState().get(attemptState.getAttemptId().getApplicationId());
        if (appState == null) {
            throw new YarnRuntimeException("Application doesn't exist");
        }
        appState.attempts.put(attemptState.getAttemptId(), attemptState);
    }
    
    public synchronized void updateApplicationAttemptStateInternal(final ApplicationAttemptId appAttemptId, final ApplicationAttemptStateData attemptStateData) throws Exception {
        Credentials credentials = null;
        if (attemptStateData.getAppAttemptTokens() != null) {
            final DataInputByteBuffer dibb = new DataInputByteBuffer();
            credentials = new Credentials();
            dibb.reset(attemptStateData.getAppAttemptTokens());
            credentials.readTokenStorageStream(dibb);
        }
        final ApplicationAttemptState updatedAttemptState = new ApplicationAttemptState(appAttemptId, attemptStateData.getMasterContainer(), credentials, attemptStateData.getStartTime(), attemptStateData.getState(), attemptStateData.getFinalTrackingUrl(), attemptStateData.getDiagnostics(), attemptStateData.getFinalApplicationStatus(), attemptStateData.getAMContainerExitStatus(), attemptStateData.getFinishTime(), attemptStateData.getMemorySeconds(), attemptStateData.getVcoreSeconds());
        final ApplicationState appState = this.state.getApplicationState().get(updatedAttemptState.getAttemptId().getApplicationId());
        if (appState == null) {
            throw new YarnRuntimeException("Application doesn't exist");
        }
        MemoryRMStateStore.LOG.info("Updating final state " + updatedAttemptState.getState() + " for attempt: " + updatedAttemptState.getAttemptId());
        appState.attempts.put(updatedAttemptState.getAttemptId(), updatedAttemptState);
    }
    
    public synchronized void removeApplicationStateInternal(final ApplicationState appState) throws Exception {
        final ApplicationId appId = appState.getAppId();
        final ApplicationState removed = this.state.appState.remove(appId);
        if (removed == null) {
            throw new YarnRuntimeException("Removing non-exsisting application state");
        }
    }
    
    public synchronized void storeRMDelegationTokenAndSequenceNumberState(final RMDelegationTokenIdentifier rmDTIdentifier, final Long renewDate, final int latestSequenceNumber) throws Exception {
        final Map<RMDelegationTokenIdentifier, Long> rmDTState = this.state.rmSecretManagerState.getTokenState();
        if (rmDTState.containsKey(rmDTIdentifier)) {
            final IOException e = new IOException("RMDelegationToken: " + rmDTIdentifier + "is already stored.");
            MemoryRMStateStore.LOG.info("Error storing info for RMDelegationToken: " + rmDTIdentifier, e);
            throw e;
        }
        rmDTState.put(rmDTIdentifier, renewDate);
        this.state.rmSecretManagerState.dtSequenceNumber = latestSequenceNumber;
        MemoryRMStateStore.LOG.info("Store RMDT with sequence number " + rmDTIdentifier.getSequenceNumber() + ". And the latest sequence number is " + latestSequenceNumber);
    }
    
    public synchronized void removeRMDelegationTokenState(final RMDelegationTokenIdentifier rmDTIdentifier) throws Exception {
        final Map<RMDelegationTokenIdentifier, Long> rmDTState = this.state.rmSecretManagerState.getTokenState();
        rmDTState.remove(rmDTIdentifier);
        MemoryRMStateStore.LOG.info("Remove RMDT with sequence number " + rmDTIdentifier.getSequenceNumber());
    }
    
    @Override
    protected void updateRMDelegationTokenAndSequenceNumberInternal(final RMDelegationTokenIdentifier rmDTIdentifier, final Long renewDate, final int latestSequenceNumber) throws Exception {
        this.removeRMDelegationTokenState(rmDTIdentifier);
        this.storeRMDelegationTokenAndSequenceNumberState(rmDTIdentifier, renewDate, latestSequenceNumber);
        MemoryRMStateStore.LOG.info("Update RMDT with sequence number " + rmDTIdentifier.getSequenceNumber());
    }
    
    public synchronized void storeRMDTMasterKeyState(final DelegationKey delegationKey) throws Exception {
        final Set<DelegationKey> rmDTMasterKeyState = this.state.rmSecretManagerState.getMasterKeyState();
        if (rmDTMasterKeyState.contains(delegationKey)) {
            final IOException e = new IOException("RMDTMasterKey with keyID: " + delegationKey.getKeyId() + " is already stored");
            MemoryRMStateStore.LOG.info("Error storing info for RMDTMasterKey with keyID: " + delegationKey.getKeyId(), e);
            throw e;
        }
        this.state.getRMDTSecretManagerState().getMasterKeyState().add(delegationKey);
        MemoryRMStateStore.LOG.info("Store RMDT master key with key id: " + delegationKey.getKeyId() + ". Currently rmDTMasterKeyState size: " + rmDTMasterKeyState.size());
    }
    
    public synchronized void removeRMDTMasterKeyState(final DelegationKey delegationKey) throws Exception {
        MemoryRMStateStore.LOG.info("Remove RMDT master key with key id: " + delegationKey.getKeyId());
        final Set<DelegationKey> rmDTMasterKeyState = this.state.rmSecretManagerState.getMasterKeyState();
        rmDTMasterKeyState.remove(delegationKey);
    }
    
    @Override
    protected Version loadVersion() throws Exception {
        return null;
    }
    
    @Override
    protected void storeVersion() throws Exception {
    }
    
    @Override
    protected Version getCurrentVersion() {
        return null;
    }
    
    @Override
    public void storeOrUpdateAMRMTokenSecretManagerState(final AMRMTokenSecretManagerState amrmTokenSecretManagerState, final boolean isUpdate) {
        if (amrmTokenSecretManagerState != null) {
            this.state.amrmTokenSecretManagerState = AMRMTokenSecretManagerState.newInstance(amrmTokenSecretManagerState);
        }
    }
    
    @Override
    public void deleteStore() throws Exception {
    }
}
