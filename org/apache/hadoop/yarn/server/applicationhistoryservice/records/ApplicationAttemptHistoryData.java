// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records;

import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class ApplicationAttemptHistoryData
{
    private ApplicationAttemptId applicationAttemptId;
    private String host;
    private int rpcPort;
    private String trackingURL;
    private String diagnosticsInfo;
    private FinalApplicationStatus finalApplicationStatus;
    private ContainerId masterContainerId;
    private YarnApplicationAttemptState yarnApplicationAttemptState;
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ApplicationAttemptHistoryData newInstance(final ApplicationAttemptId appAttemptId, final String host, final int rpcPort, final ContainerId masterContainerId, final String diagnosticsInfo, final String trackingURL, final FinalApplicationStatus finalApplicationStatus, final YarnApplicationAttemptState yarnApplicationAttemptState) {
        final ApplicationAttemptHistoryData appAttemptHD = new ApplicationAttemptHistoryData();
        appAttemptHD.setApplicationAttemptId(appAttemptId);
        appAttemptHD.setHost(host);
        appAttemptHD.setRPCPort(rpcPort);
        appAttemptHD.setMasterContainerId(masterContainerId);
        appAttemptHD.setDiagnosticsInfo(diagnosticsInfo);
        appAttemptHD.setTrackingURL(trackingURL);
        appAttemptHD.setFinalApplicationStatus(finalApplicationStatus);
        appAttemptHD.setYarnApplicationAttemptState(yarnApplicationAttemptState);
        return appAttemptHD;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.applicationAttemptId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setApplicationAttemptId(final ApplicationAttemptId applicationAttemptId) {
        this.applicationAttemptId = applicationAttemptId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public String getHost() {
        return this.host;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setHost(final String host) {
        this.host = host;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public int getRPCPort() {
        return this.rpcPort;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setRPCPort(final int rpcPort) {
        this.rpcPort = rpcPort;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public String getTrackingURL() {
        return this.trackingURL;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setTrackingURL(final String trackingURL) {
        this.trackingURL = trackingURL;
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
    public ContainerId getMasterContainerId() {
        return this.masterContainerId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setMasterContainerId(final ContainerId masterContainerId) {
        this.masterContainerId = masterContainerId;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public YarnApplicationAttemptState getYarnApplicationAttemptState() {
        return this.yarnApplicationAttemptState;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public void setYarnApplicationAttemptState(final YarnApplicationAttemptState yarnApplicationAttemptState) {
        this.yarnApplicationAttemptState = yarnApplicationAttemptState;
    }
}
