// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ApplicationAttemptStartData
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ApplicationAttemptStartData newInstance(final ApplicationAttemptId appAttemptId, final String host, final int rpcPort, final ContainerId masterContainerId) {
        final ApplicationAttemptStartData appAttemptSD = Records.newRecord(ApplicationAttemptStartData.class);
        appAttemptSD.setApplicationAttemptId(appAttemptId);
        appAttemptSD.setHost(host);
        appAttemptSD.setRPCPort(rpcPort);
        appAttemptSD.setMasterContainerId(masterContainerId);
        return appAttemptSD;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ApplicationAttemptId getApplicationAttemptId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationAttemptId(final ApplicationAttemptId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getHost();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setHost(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract int getRPCPort();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setRPCPort(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ContainerId getMasterContainerId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setMasterContainerId(final ContainerId p0);
}
