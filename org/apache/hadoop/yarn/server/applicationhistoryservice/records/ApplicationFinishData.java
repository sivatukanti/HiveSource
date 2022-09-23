// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ApplicationFinishData
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ApplicationFinishData newInstance(final ApplicationId applicationId, final long finishTime, final String diagnosticsInfo, final FinalApplicationStatus finalApplicationStatus, final YarnApplicationState yarnApplicationState) {
        final ApplicationFinishData appFD = Records.newRecord(ApplicationFinishData.class);
        appFD.setApplicationId(applicationId);
        appFD.setFinishTime(finishTime);
        appFD.setDiagnosticsInfo(diagnosticsInfo);
        appFD.setFinalApplicationStatus(finalApplicationStatus);
        appFD.setYarnApplicationState(yarnApplicationState);
        return appFD;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ApplicationId getApplicationId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setApplicationId(final ApplicationId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getFinishTime();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setFinishTime(final long p0);
    
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
    public abstract YarnApplicationState getYarnApplicationState();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setYarnApplicationState(final YarnApplicationState p0);
}
