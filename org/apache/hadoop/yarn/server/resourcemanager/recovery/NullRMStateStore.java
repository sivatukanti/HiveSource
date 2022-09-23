// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.AMRMTokenSecretManagerState;
import org.apache.hadoop.yarn.server.records.Version;
import org.apache.hadoop.security.token.delegation.DelegationKey;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationAttemptStateData;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
public class NullRMStateStore extends RMStateStore
{
    @Override
    protected void initInternal(final Configuration conf) throws Exception {
    }
    
    @Override
    protected void startInternal() throws Exception {
    }
    
    @Override
    protected void closeInternal() throws Exception {
    }
    
    @Override
    public synchronized long getAndIncrementEpoch() throws Exception {
        return 0L;
    }
    
    @Override
    public RMState loadState() throws Exception {
        throw new UnsupportedOperationException("Cannot load state from null store");
    }
    
    @Override
    protected void storeApplicationStateInternal(final ApplicationId appId, final ApplicationStateData appStateData) throws Exception {
    }
    
    @Override
    protected void storeApplicationAttemptStateInternal(final ApplicationAttemptId attemptId, final ApplicationAttemptStateData attemptStateData) throws Exception {
    }
    
    @Override
    protected void removeApplicationStateInternal(final ApplicationState appState) throws Exception {
    }
    
    public void storeRMDelegationTokenAndSequenceNumberState(final RMDelegationTokenIdentifier rmDTIdentifier, final Long renewDate, final int latestSequenceNumber) throws Exception {
    }
    
    public void removeRMDelegationTokenState(final RMDelegationTokenIdentifier rmDTIdentifier) throws Exception {
    }
    
    @Override
    protected void updateRMDelegationTokenAndSequenceNumberInternal(final RMDelegationTokenIdentifier rmDTIdentifier, final Long renewDate, final int latestSequenceNumber) throws Exception {
    }
    
    public void storeRMDTMasterKeyState(final DelegationKey delegationKey) throws Exception {
    }
    
    public void removeRMDTMasterKeyState(final DelegationKey delegationKey) throws Exception {
    }
    
    @Override
    protected void updateApplicationStateInternal(final ApplicationId appId, final ApplicationStateData appStateData) throws Exception {
    }
    
    @Override
    protected void updateApplicationAttemptStateInternal(final ApplicationAttemptId attemptId, final ApplicationAttemptStateData attemptStateData) throws Exception {
    }
    
    @Override
    public void checkVersion() throws Exception {
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
    public void storeOrUpdateAMRMTokenSecretManagerState(final AMRMTokenSecretManagerState state, final boolean isUpdate) {
    }
    
    @Override
    public void deleteStore() throws Exception {
    }
}
