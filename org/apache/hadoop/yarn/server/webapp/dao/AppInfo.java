// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webapp.dao;

import org.apache.hadoop.yarn.util.Times;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "app")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppInfo
{
    protected String appId;
    protected String currentAppAttemptId;
    protected String user;
    protected String name;
    protected String queue;
    protected String type;
    protected String host;
    protected int rpcPort;
    protected YarnApplicationState appState;
    protected float progress;
    protected String diagnosticsInfo;
    protected String originalTrackingUrl;
    protected String trackingUrl;
    protected FinalApplicationStatus finalAppStatus;
    protected long submittedTime;
    protected long startedTime;
    protected long finishedTime;
    protected long elapsedTime;
    
    public AppInfo() {
    }
    
    public AppInfo(final ApplicationReport app) {
        this.appId = app.getApplicationId().toString();
        if (app.getCurrentApplicationAttemptId() != null) {
            this.currentAppAttemptId = app.getCurrentApplicationAttemptId().toString();
        }
        this.user = app.getUser();
        this.queue = app.getQueue();
        this.name = app.getName();
        this.type = app.getApplicationType();
        this.host = app.getHost();
        this.rpcPort = app.getRpcPort();
        this.appState = app.getYarnApplicationState();
        this.diagnosticsInfo = app.getDiagnostics();
        this.trackingUrl = app.getTrackingUrl();
        this.originalTrackingUrl = app.getOriginalTrackingUrl();
        this.submittedTime = app.getStartTime();
        this.startedTime = app.getStartTime();
        this.finishedTime = app.getFinishTime();
        this.elapsedTime = Times.elapsed(this.startedTime, this.finishedTime);
        this.finalAppStatus = app.getFinalApplicationStatus();
        this.progress = app.getProgress();
    }
    
    public String getAppId() {
        return this.appId;
    }
    
    public String getCurrentAppAttemptId() {
        return this.currentAppAttemptId;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getQueue() {
        return this.queue;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getRpcPort() {
        return this.rpcPort;
    }
    
    public YarnApplicationState getAppState() {
        return this.appState;
    }
    
    public float getProgress() {
        return this.progress;
    }
    
    public String getDiagnosticsInfo() {
        return this.diagnosticsInfo;
    }
    
    public String getOriginalTrackingUrl() {
        return this.originalTrackingUrl;
    }
    
    public String getTrackingUrl() {
        return this.trackingUrl;
    }
    
    public FinalApplicationStatus getFinalAppStatus() {
        return this.finalAppStatus;
    }
    
    public long getSubmittedTime() {
        return this.submittedTime;
    }
    
    public long getStartedTime() {
        return this.startedTime;
    }
    
    public long getFinishedTime() {
        return this.finishedTime;
    }
    
    public long getElapsedTime() {
        return this.elapsedTime;
    }
}
