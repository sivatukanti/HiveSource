// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ApplicationAttemptReport
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ApplicationAttemptReport newInstance(final ApplicationAttemptId applicationAttemptId, final String host, final int rpcPort, final String url, final String oUrl, final String diagnostics, final YarnApplicationAttemptState state, final ContainerId amContainerId) {
        final ApplicationAttemptReport report = Records.newRecord(ApplicationAttemptReport.class);
        report.setApplicationAttemptId(applicationAttemptId);
        report.setHost(host);
        report.setRpcPort(rpcPort);
        report.setTrackingUrl(url);
        report.setOriginalTrackingUrl(oUrl);
        report.setDiagnostics(diagnostics);
        report.setYarnApplicationAttemptState(state);
        report.setAMContainerId(amContainerId);
        return report;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract YarnApplicationAttemptState getYarnApplicationAttemptState();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setYarnApplicationAttemptState(final YarnApplicationAttemptState p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract int getRpcPort();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setRpcPort(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getHost();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setHost(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getDiagnostics();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setDiagnostics(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getTrackingUrl();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setTrackingUrl(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract String getOriginalTrackingUrl();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setOriginalTrackingUrl(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ApplicationAttemptId getApplicationAttemptId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setApplicationAttemptId(final ApplicationAttemptId p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ContainerId getAMContainerId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setAMContainerId(final ContainerId p0);
}
