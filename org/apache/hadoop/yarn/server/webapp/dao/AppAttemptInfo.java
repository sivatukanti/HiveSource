// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webapp.dao;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "appAttempt")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppAttemptInfo
{
    protected String appAttemptId;
    protected String host;
    protected int rpcPort;
    protected String trackingUrl;
    protected String originalTrackingUrl;
    protected String diagnosticsInfo;
    protected YarnApplicationAttemptState appAttemptState;
    protected String amContainerId;
    
    public AppAttemptInfo() {
    }
    
    public AppAttemptInfo(final ApplicationAttemptReport appAttempt) {
        this.appAttemptId = appAttempt.getApplicationAttemptId().toString();
        this.host = appAttempt.getHost();
        this.rpcPort = appAttempt.getRpcPort();
        this.trackingUrl = appAttempt.getTrackingUrl();
        this.originalTrackingUrl = appAttempt.getOriginalTrackingUrl();
        this.diagnosticsInfo = appAttempt.getDiagnostics();
        this.appAttemptState = appAttempt.getYarnApplicationAttemptState();
        if (appAttempt.getAMContainerId() != null) {
            this.amContainerId = appAttempt.getAMContainerId().toString();
        }
    }
    
    public String getAppAttemptId() {
        return this.appAttemptId;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getRpcPort() {
        return this.rpcPort;
    }
    
    public String getTrackingUrl() {
        return this.trackingUrl;
    }
    
    public String getOriginalTrackingUrl() {
        return this.originalTrackingUrl;
    }
    
    public String getDiagnosticsInfo() {
        return this.diagnosticsInfo;
    }
    
    public YarnApplicationAttemptState getAppAttemptState() {
        return this.appAttemptState;
    }
    
    public String getAmContainerId() {
        return this.amContainerId;
    }
}
