// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import java.util.Set;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class ApplicationReport
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ApplicationReport newInstance(final ApplicationId applicationId, final ApplicationAttemptId applicationAttemptId, final String user, final String queue, final String name, final String host, final int rpcPort, final Token clientToAMToken, final YarnApplicationState state, final String diagnostics, final String url, final long startTime, final long finishTime, final FinalApplicationStatus finalStatus, final ApplicationResourceUsageReport appResources, final String origTrackingUrl, final float progress, final String applicationType, final Token amRmToken) {
        final ApplicationReport report = Records.newRecord(ApplicationReport.class);
        report.setApplicationId(applicationId);
        report.setCurrentApplicationAttemptId(applicationAttemptId);
        report.setUser(user);
        report.setQueue(queue);
        report.setName(name);
        report.setHost(host);
        report.setRpcPort(rpcPort);
        report.setClientToAMToken(clientToAMToken);
        report.setYarnApplicationState(state);
        report.setDiagnostics(diagnostics);
        report.setTrackingUrl(url);
        report.setStartTime(startTime);
        report.setFinishTime(finishTime);
        report.setFinalApplicationStatus(finalStatus);
        report.setApplicationResourceUsageReport(appResources);
        report.setOriginalTrackingUrl(origTrackingUrl);
        report.setProgress(progress);
        report.setApplicationType(applicationType);
        report.setAMRMToken(amRmToken);
        return report;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ApplicationId getApplicationId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationId(final ApplicationId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ApplicationAttemptId getCurrentApplicationAttemptId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setCurrentApplicationAttemptId(final ApplicationAttemptId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getUser();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setUser(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getQueue();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setQueue(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getName();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setName(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getHost();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setHost(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getRpcPort();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setRpcPort(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Token getClientToAMToken();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setClientToAMToken(final Token p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract YarnApplicationState getYarnApplicationState();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setYarnApplicationState(final YarnApplicationState p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getDiagnostics();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setDiagnostics(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getTrackingUrl();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setTrackingUrl(final String p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract String getOriginalTrackingUrl();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setOriginalTrackingUrl(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract long getStartTime();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setStartTime(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract long getFinishTime();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setFinishTime(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract FinalApplicationStatus getFinalApplicationStatus();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setFinalApplicationStatus(final FinalApplicationStatus p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract ApplicationResourceUsageReport getApplicationResourceUsageReport();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationResourceUsageReport(final ApplicationResourceUsageReport p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract float getProgress();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setProgress(final float p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getApplicationType();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationType(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Set<String> getApplicationTags();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationTags(final Set<String> p0);
    
    @InterfaceAudience.Private
    @InterfaceStability.Stable
    public abstract void setAMRMToken(final Token p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract Token getAMRMToken();
}
