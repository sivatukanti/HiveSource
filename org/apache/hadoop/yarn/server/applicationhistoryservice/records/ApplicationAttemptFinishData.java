// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ApplicationAttemptFinishData
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ApplicationAttemptFinishData newInstance(final ApplicationAttemptId appAttemptId, final String diagnosticsInfo, final String trackingURL, final FinalApplicationStatus finalApplicationStatus, final YarnApplicationAttemptState yarnApplicationAttemptState) {
        final ApplicationAttemptFinishData appAttemptFD = Records.newRecord(ApplicationAttemptFinishData.class);
        appAttemptFD.setApplicationAttemptId(appAttemptId);
        appAttemptFD.setDiagnosticsInfo(diagnosticsInfo);
        appAttemptFD.setTrackingURL(trackingURL);
        appAttemptFD.setFinalApplicationStatus(finalApplicationStatus);
        appAttemptFD.setYarnApplicationAttemptState(yarnApplicationAttemptState);
        return appAttemptFD;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ApplicationAttemptId getApplicationAttemptId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationAttemptId(final ApplicationAttemptId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getTrackingURL();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setTrackingURL(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getDiagnosticsInfo();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setDiagnosticsInfo(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract FinalApplicationStatus getFinalApplicationStatus();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setFinalApplicationStatus(final FinalApplicationStatus p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract YarnApplicationAttemptState getYarnApplicationAttemptState();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setYarnApplicationAttemptState(final YarnApplicationAttemptState p0);
}
