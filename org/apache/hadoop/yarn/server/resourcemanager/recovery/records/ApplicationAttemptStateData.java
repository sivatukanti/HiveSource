// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery.records;

import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;
import java.io.IOException;
import org.apache.hadoop.security.Credentials;
import java.io.DataOutputStream;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ApplicationAttemptStateData
{
    public static ApplicationAttemptStateData newInstance(final ApplicationAttemptId attemptId, final Container container, final ByteBuffer attemptTokens, final long startTime, final RMAppAttemptState finalState, final String finalTrackingUrl, final String diagnostics, final FinalApplicationStatus amUnregisteredFinalStatus, final int exitStatus, final long finishTime, final long memorySeconds, final long vcoreSeconds) {
        final ApplicationAttemptStateData attemptStateData = Records.newRecord(ApplicationAttemptStateData.class);
        attemptStateData.setAttemptId(attemptId);
        attemptStateData.setMasterContainer(container);
        attemptStateData.setAppAttemptTokens(attemptTokens);
        attemptStateData.setState(finalState);
        attemptStateData.setFinalTrackingUrl(finalTrackingUrl);
        attemptStateData.setDiagnostics(diagnostics);
        attemptStateData.setStartTime(startTime);
        attemptStateData.setFinalApplicationStatus(amUnregisteredFinalStatus);
        attemptStateData.setAMContainerExitStatus(exitStatus);
        attemptStateData.setFinishTime(finishTime);
        attemptStateData.setMemorySeconds(memorySeconds);
        attemptStateData.setVcoreSeconds(vcoreSeconds);
        return attemptStateData;
    }
    
    public static ApplicationAttemptStateData newInstance(final RMStateStore.ApplicationAttemptState attemptState) throws IOException {
        final Credentials credentials = attemptState.getAppAttemptCredentials();
        ByteBuffer appAttemptTokens = null;
        if (credentials != null) {
            final DataOutputBuffer dob = new DataOutputBuffer();
            credentials.writeTokenStorageToStream(dob);
            appAttemptTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        }
        return newInstance(attemptState.getAttemptId(), attemptState.getMasterContainer(), appAttemptTokens, attemptState.getStartTime(), attemptState.getState(), attemptState.getFinalTrackingUrl(), attemptState.getDiagnostics(), attemptState.getFinalApplicationStatus(), attemptState.getAMContainerExitStatus(), attemptState.getFinishTime(), attemptState.getMemorySeconds(), attemptState.getVcoreSeconds());
    }
    
    public abstract YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto getProto();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ApplicationAttemptId getAttemptId();
    
    public abstract void setAttemptId(final ApplicationAttemptId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract Container getMasterContainer();
    
    public abstract void setMasterContainer(final Container p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ByteBuffer getAppAttemptTokens();
    
    public abstract void setAppAttemptTokens(final ByteBuffer p0);
    
    public abstract RMAppAttemptState getState();
    
    public abstract void setState(final RMAppAttemptState p0);
    
    public abstract String getFinalTrackingUrl();
    
    public abstract void setFinalTrackingUrl(final String p0);
    
    public abstract String getDiagnostics();
    
    public abstract void setDiagnostics(final String p0);
    
    public abstract long getStartTime();
    
    public abstract void setStartTime(final long p0);
    
    public abstract FinalApplicationStatus getFinalApplicationStatus();
    
    public abstract void setFinalApplicationStatus(final FinalApplicationStatus p0);
    
    public abstract int getAMContainerExitStatus();
    
    public abstract void setAMContainerExitStatus(final int p0);
    
    public abstract long getFinishTime();
    
    public abstract void setFinishTime(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getMemorySeconds();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setMemorySeconds(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getVcoreSeconds();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setVcoreSeconds(final long p0);
}
