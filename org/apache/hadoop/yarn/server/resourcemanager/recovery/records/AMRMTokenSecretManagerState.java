// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery.records;

import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class AMRMTokenSecretManagerState
{
    public static AMRMTokenSecretManagerState newInstance(final MasterKey currentMasterKey, final MasterKey nextMasterKey) {
        final AMRMTokenSecretManagerState data = Records.newRecord(AMRMTokenSecretManagerState.class);
        data.setCurrentMasterKey(currentMasterKey);
        data.setNextMasterKey(nextMasterKey);
        return data;
    }
    
    public static AMRMTokenSecretManagerState newInstance(final AMRMTokenSecretManagerState state) {
        final AMRMTokenSecretManagerState data = Records.newRecord(AMRMTokenSecretManagerState.class);
        data.setCurrentMasterKey(state.getCurrentMasterKey());
        data.setNextMasterKey(state.getNextMasterKey());
        return data;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract MasterKey getCurrentMasterKey();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setCurrentMasterKey(final MasterKey p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract MasterKey getNextMasterKey();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setNextMasterKey(final MasterKey p0);
    
    public abstract YarnServerResourceManagerRecoveryProtos.AMRMTokenSecretManagerStateProto getProto();
}
