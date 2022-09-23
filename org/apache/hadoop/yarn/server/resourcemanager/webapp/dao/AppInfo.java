// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppMetrics;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Times;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import com.google.common.base.Joiner;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "app")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppInfo
{
    @XmlTransient
    protected String appIdNum;
    @XmlTransient
    protected boolean trackingUrlIsNotReady;
    @XmlTransient
    protected String trackingUrlPretty;
    @XmlTransient
    protected boolean amContainerLogsExist;
    @XmlTransient
    protected ApplicationId applicationId;
    @XmlTransient
    private String schemePrefix;
    protected String id;
    protected String user;
    protected String name;
    protected String queue;
    protected YarnApplicationState state;
    protected FinalApplicationStatus finalStatus;
    protected float progress;
    protected String trackingUI;
    protected String trackingUrl;
    protected String diagnostics;
    protected long clusterId;
    protected String applicationType;
    protected String applicationTags;
    protected long startedTime;
    protected long finishedTime;
    protected long elapsedTime;
    protected String amContainerLogs;
    protected String amHostHttpAddress;
    protected int allocatedMB;
    protected int allocatedVCores;
    protected int runningContainers;
    protected long memorySeconds;
    protected long vcoreSeconds;
    protected int preemptedResourceMB;
    protected int preemptedResourceVCores;
    protected int numNonAMContainerPreempted;
    protected int numAMContainerPreempted;
    
    public AppInfo() {
        this.amContainerLogsExist = false;
        this.applicationTags = "";
    }
    
    public AppInfo(final RMApp app, final Boolean hasAccess, final String schemePrefix) {
        this.amContainerLogsExist = false;
        this.applicationTags = "";
        this.schemePrefix = schemePrefix;
        if (app != null) {
            final String trackingUrl = app.getTrackingUrl();
            this.state = app.createApplicationState();
            this.trackingUrlIsNotReady = (trackingUrl == null || trackingUrl.isEmpty() || YarnApplicationState.NEW == this.state || YarnApplicationState.NEW_SAVING == this.state || YarnApplicationState.SUBMITTED == this.state || YarnApplicationState.ACCEPTED == this.state);
            this.trackingUI = (this.trackingUrlIsNotReady ? "UNASSIGNED" : ((app.getFinishTime() == 0L) ? "ApplicationMaster" : "History"));
            if (!this.trackingUrlIsNotReady) {
                this.trackingUrl = WebAppUtils.getURLWithScheme(schemePrefix, trackingUrl);
                this.trackingUrlPretty = this.trackingUrl;
            }
            else {
                this.trackingUrlPretty = "UNASSIGNED";
            }
            this.applicationId = app.getApplicationId();
            this.applicationType = app.getApplicationType();
            this.appIdNum = String.valueOf(app.getApplicationId().getId());
            this.id = app.getApplicationId().toString();
            this.user = app.getUser().toString();
            this.name = app.getName().toString();
            this.queue = app.getQueue().toString();
            this.progress = app.getProgress() * 100.0f;
            this.diagnostics = app.getDiagnostics().toString();
            if (this.diagnostics == null || this.diagnostics.isEmpty()) {
                this.diagnostics = "";
            }
            if (app.getApplicationTags() != null && !app.getApplicationTags().isEmpty()) {
                this.applicationTags = Joiner.on(',').join(app.getApplicationTags());
            }
            this.finalStatus = app.getFinalApplicationStatus();
            this.clusterId = ResourceManager.getClusterTimeStamp();
            if (hasAccess) {
                this.startedTime = app.getStartTime();
                this.finishedTime = app.getFinishTime();
                this.elapsedTime = Times.elapsed(app.getStartTime(), app.getFinishTime());
                final RMAppAttempt attempt = app.getCurrentAppAttempt();
                if (attempt != null) {
                    final Container masterContainer = attempt.getMasterContainer();
                    if (masterContainer != null) {
                        this.amContainerLogsExist = true;
                        this.amContainerLogs = WebAppUtils.getRunningLogURL(schemePrefix + masterContainer.getNodeHttpAddress(), ConverterUtils.toString(masterContainer.getId()), app.getUser());
                        this.amHostHttpAddress = masterContainer.getNodeHttpAddress();
                    }
                    final ApplicationResourceUsageReport resourceReport = attempt.getApplicationResourceUsageReport();
                    if (resourceReport != null) {
                        final Resource usedResources = resourceReport.getUsedResources();
                        this.allocatedMB = usedResources.getMemory();
                        this.allocatedVCores = usedResources.getVirtualCores();
                        this.runningContainers = resourceReport.getNumUsedContainers();
                    }
                }
            }
            final RMAppMetrics appMetrics = app.getRMAppMetrics();
            this.numAMContainerPreempted = appMetrics.getNumAMContainersPreempted();
            this.preemptedResourceMB = appMetrics.getResourcePreempted().getMemory();
            this.numNonAMContainerPreempted = appMetrics.getNumNonAMContainersPreempted();
            this.preemptedResourceVCores = appMetrics.getResourcePreempted().getVirtualCores();
            this.memorySeconds = appMetrics.getMemorySeconds();
            this.vcoreSeconds = appMetrics.getVcoreSeconds();
        }
    }
    
    public boolean isTrackingUrlReady() {
        return !this.trackingUrlIsNotReady;
    }
    
    public ApplicationId getApplicationId() {
        return this.applicationId;
    }
    
    public String getAppId() {
        return this.id;
    }
    
    public String getAppIdNum() {
        return this.appIdNum;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getQueue() {
        return this.queue;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getState() {
        return this.state.toString();
    }
    
    public float getProgress() {
        return this.progress;
    }
    
    public String getTrackingUI() {
        return this.trackingUI;
    }
    
    public String getNote() {
        return this.diagnostics;
    }
    
    public String getFinalStatus() {
        return this.finalStatus.toString();
    }
    
    public String getTrackingUrl() {
        return this.trackingUrl;
    }
    
    public String getTrackingUrlPretty() {
        return this.trackingUrlPretty;
    }
    
    public long getStartTime() {
        return this.startedTime;
    }
    
    public long getFinishTime() {
        return this.finishedTime;
    }
    
    public long getElapsedTime() {
        return this.elapsedTime;
    }
    
    public String getAMContainerLogs() {
        return this.amContainerLogs;
    }
    
    public String getAMHostHttpAddress() {
        return this.amHostHttpAddress;
    }
    
    public boolean amContainerLogsExist() {
        return this.amContainerLogsExist;
    }
    
    public long getClusterId() {
        return this.clusterId;
    }
    
    public String getApplicationType() {
        return this.applicationType;
    }
    
    public String getApplicationTags() {
        return this.applicationTags;
    }
    
    public int getRunningContainers() {
        return this.runningContainers;
    }
    
    public int getAllocatedMB() {
        return this.allocatedMB;
    }
    
    public int getAllocatedVCores() {
        return this.allocatedVCores;
    }
    
    public int getPreemptedMB() {
        return this.preemptedResourceMB;
    }
    
    public int getPreemptedVCores() {
        return this.preemptedResourceVCores;
    }
    
    public int getNumNonAMContainersPreempted() {
        return this.numNonAMContainerPreempted;
    }
    
    public int getNumAMContainersPreempted() {
        return this.numAMContainerPreempted;
    }
    
    public long getMemorySeconds() {
        return this.memorySeconds;
    }
    
    public long getVcoreSeconds() {
        return this.vcoreSeconds;
    }
}
