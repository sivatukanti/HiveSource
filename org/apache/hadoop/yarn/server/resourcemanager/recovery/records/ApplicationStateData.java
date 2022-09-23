// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery.records;

import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ApplicationStateData
{
    public static ApplicationStateData newInstance(final long submitTime, final long startTime, final String user, final ApplicationSubmissionContext submissionContext, final RMAppState state, final String diagnostics, final long finishTime) {
        final ApplicationStateData appState = Records.newRecord(ApplicationStateData.class);
        appState.setSubmitTime(submitTime);
        appState.setStartTime(startTime);
        appState.setUser(user);
        appState.setApplicationSubmissionContext(submissionContext);
        appState.setState(state);
        appState.setDiagnostics(diagnostics);
        appState.setFinishTime(finishTime);
        return appState;
    }
    
    public static ApplicationStateData newInstance(final RMStateStore.ApplicationState appState) {
        return newInstance(appState.getSubmitTime(), appState.getStartTime(), appState.getUser(), appState.getApplicationSubmissionContext(), appState.getState(), appState.getDiagnostics(), appState.getFinishTime());
    }
    
    public abstract YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto getProto();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getSubmitTime();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setSubmitTime(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract long getStartTime();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setStartTime(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setUser(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getUser();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ApplicationSubmissionContext getApplicationSubmissionContext();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationSubmissionContext(final ApplicationSubmissionContext p0);
    
    public abstract RMAppState getState();
    
    public abstract void setState(final RMAppState p0);
    
    public abstract String getDiagnostics();
    
    public abstract void setDiagnostics(final String p0);
    
    public abstract long getFinishTime();
    
    public abstract void setFinishTime(final long p0);
}
