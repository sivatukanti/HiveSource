// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records;

import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ApplicationHistoryData
{
    private ApplicationId applicationId;
    private String applicationName;
    private String applicationType;
    private String user;
    private String queue;
    private long submitTime;
    private long startTime;
    private long finishTime;
    private String diagnosticsInfo;
    private FinalApplicationStatus finalApplicationStatus;
    private YarnApplicationState yarnApplicationState;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ApplicationHistoryData newInstance(final ApplicationId applicationId, final String applicationName, final String applicationType, final String queue, final String user, final long submitTime, final long startTime, final long finishTime, final String diagnosticsInfo, final FinalApplicationStatus finalApplicationStatus, final YarnApplicationState yarnApplicationState) {
        final ApplicationHistoryData appHD = new ApplicationHistoryData();
        appHD.setApplicationId(applicationId);
        appHD.setApplicationName(applicationName);
        appHD.setApplicationType(applicationType);
        appHD.setQueue(queue);
        appHD.setUser(user);
        appHD.setSubmitTime(submitTime);
        appHD.setStartTime(startTime);
        appHD.setFinishTime(finishTime);
        appHD.setDiagnosticsInfo(diagnosticsInfo);
        appHD.setFinalApplicationStatus(finalApplicationStatus);
        appHD.setYarnApplicationState(yarnApplicationState);
        return appHD;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public ApplicationId getApplicationId() {
        return this.applicationId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setApplicationId(final ApplicationId applicationId) {
        this.applicationId = applicationId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public String getApplicationName() {
        return this.applicationName;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setApplicationName(final String applicationName) {
        this.applicationName = applicationName;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public String getApplicationType() {
        return this.applicationType;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setApplicationType(final String applicationType) {
        this.applicationType = applicationType;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public String getUser() {
        return this.user;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setUser(final String user) {
        this.user = user;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public String getQueue() {
        return this.queue;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setQueue(final String queue) {
        this.queue = queue;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public long getSubmitTime() {
        return this.submitTime;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setSubmitTime(final long submitTime) {
        this.submitTime = submitTime;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public long getStartTime() {
        return this.startTime;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public long getFinishTime() {
        return this.finishTime;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setFinishTime(final long finishTime) {
        this.finishTime = finishTime;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public String getDiagnosticsInfo() {
        return this.diagnosticsInfo;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setDiagnosticsInfo(final String diagnosticsInfo) {
        this.diagnosticsInfo = diagnosticsInfo;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public FinalApplicationStatus getFinalApplicationStatus() {
        return this.finalApplicationStatus;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setFinalApplicationStatus(final FinalApplicationStatus finalApplicationStatus) {
        this.finalApplicationStatus = finalApplicationStatus;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public YarnApplicationState getYarnApplicationState() {
        return this.yarnApplicationState;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setYarnApplicationState(final YarnApplicationState yarnApplicationState) {
        this.yarnApplicationState = yarnApplicationState;
    }
}
