// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice;

import org.apache.commons.logging.LogFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerHistoryData;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptHistoryData;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.Token;
import java.util.Iterator;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationHistoryData;
import java.util.HashMap;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.Map;
import java.io.IOException;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.service.AbstractService;

public class ApplicationHistoryManagerImpl extends AbstractService implements ApplicationHistoryManager
{
    private static final Log LOG;
    private static final String UNAVAILABLE = "N/A";
    private ApplicationHistoryStore historyStore;
    private String serverHttpAddress;
    
    public ApplicationHistoryManagerImpl() {
        super(ApplicationHistoryManagerImpl.class.getName());
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        ApplicationHistoryManagerImpl.LOG.info("ApplicationHistory Init");
        (this.historyStore = this.createApplicationHistoryStore(conf)).init(conf);
        this.serverHttpAddress = WebAppUtils.getHttpSchemePrefix(conf) + WebAppUtils.getAHSWebAppURLWithoutScheme(conf);
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        ApplicationHistoryManagerImpl.LOG.info("Starting ApplicationHistory");
        this.historyStore.start();
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        ApplicationHistoryManagerImpl.LOG.info("Stopping ApplicationHistory");
        this.historyStore.stop();
        super.serviceStop();
    }
    
    protected ApplicationHistoryStore createApplicationHistoryStore(final Configuration conf) {
        return ReflectionUtils.newInstance(conf.getClass("yarn.timeline-service.generic-application-history.store-class", FileSystemApplicationHistoryStore.class, ApplicationHistoryStore.class), conf);
    }
    
    @Override
    public ContainerReport getAMContainer(final ApplicationAttemptId appAttemptId) throws IOException {
        final ApplicationReport app = this.getApplication(appAttemptId.getApplicationId());
        return this.convertToContainerReport(this.historyStore.getAMContainer(appAttemptId), (app == null) ? null : app.getUser());
    }
    
    @Override
    public Map<ApplicationId, ApplicationReport> getAllApplications() throws IOException {
        final Map<ApplicationId, ApplicationHistoryData> histData = this.historyStore.getAllApplications();
        final HashMap<ApplicationId, ApplicationReport> applicationsReport = new HashMap<ApplicationId, ApplicationReport>();
        for (final Map.Entry<ApplicationId, ApplicationHistoryData> entry : histData.entrySet()) {
            applicationsReport.put(entry.getKey(), this.convertToApplicationReport(entry.getValue()));
        }
        return applicationsReport;
    }
    
    @Override
    public ApplicationReport getApplication(final ApplicationId appId) throws IOException {
        return this.convertToApplicationReport(this.historyStore.getApplication(appId));
    }
    
    private ApplicationReport convertToApplicationReport(final ApplicationHistoryData appHistory) throws IOException {
        ApplicationAttemptId currentApplicationAttemptId = null;
        String trackingUrl = "N/A";
        String host = "N/A";
        int rpcPort = -1;
        final ApplicationAttemptHistoryData lastAttempt = this.getLastAttempt(appHistory.getApplicationId());
        if (lastAttempt != null) {
            currentApplicationAttemptId = lastAttempt.getApplicationAttemptId();
            trackingUrl = lastAttempt.getTrackingURL();
            host = lastAttempt.getHost();
            rpcPort = lastAttempt.getRPCPort();
        }
        return ApplicationReport.newInstance(appHistory.getApplicationId(), currentApplicationAttemptId, appHistory.getUser(), appHistory.getQueue(), appHistory.getApplicationName(), host, rpcPort, null, appHistory.getYarnApplicationState(), appHistory.getDiagnosticsInfo(), trackingUrl, appHistory.getStartTime(), appHistory.getFinishTime(), appHistory.getFinalApplicationStatus(), null, "", 100.0f, appHistory.getApplicationType(), null);
    }
    
    private ApplicationAttemptHistoryData getLastAttempt(final ApplicationId appId) throws IOException {
        final Map<ApplicationAttemptId, ApplicationAttemptHistoryData> attempts = this.historyStore.getApplicationAttempts(appId);
        ApplicationAttemptId prevMaxAttemptId = null;
        for (final ApplicationAttemptId attemptId : attempts.keySet()) {
            if (prevMaxAttemptId == null) {
                prevMaxAttemptId = attemptId;
            }
            else {
                if (prevMaxAttemptId.getAttemptId() >= attemptId.getAttemptId()) {
                    continue;
                }
                prevMaxAttemptId = attemptId;
            }
        }
        return attempts.get(prevMaxAttemptId);
    }
    
    private ApplicationAttemptReport convertToApplicationAttemptReport(final ApplicationAttemptHistoryData appAttemptHistory) {
        return ApplicationAttemptReport.newInstance(appAttemptHistory.getApplicationAttemptId(), appAttemptHistory.getHost(), appAttemptHistory.getRPCPort(), appAttemptHistory.getTrackingURL(), null, appAttemptHistory.getDiagnosticsInfo(), appAttemptHistory.getYarnApplicationAttemptState(), appAttemptHistory.getMasterContainerId());
    }
    
    @Override
    public ApplicationAttemptReport getApplicationAttempt(final ApplicationAttemptId appAttemptId) throws IOException {
        return this.convertToApplicationAttemptReport(this.historyStore.getApplicationAttempt(appAttemptId));
    }
    
    @Override
    public Map<ApplicationAttemptId, ApplicationAttemptReport> getApplicationAttempts(final ApplicationId appId) throws IOException {
        final Map<ApplicationAttemptId, ApplicationAttemptHistoryData> histData = this.historyStore.getApplicationAttempts(appId);
        final HashMap<ApplicationAttemptId, ApplicationAttemptReport> applicationAttemptsReport = new HashMap<ApplicationAttemptId, ApplicationAttemptReport>();
        for (final Map.Entry<ApplicationAttemptId, ApplicationAttemptHistoryData> entry : histData.entrySet()) {
            applicationAttemptsReport.put(entry.getKey(), this.convertToApplicationAttemptReport(entry.getValue()));
        }
        return applicationAttemptsReport;
    }
    
    @Override
    public ContainerReport getContainer(final ContainerId containerId) throws IOException {
        final ApplicationReport app = this.getApplication(containerId.getApplicationAttemptId().getApplicationId());
        return this.convertToContainerReport(this.historyStore.getContainer(containerId), (app == null) ? null : app.getUser());
    }
    
    private ContainerReport convertToContainerReport(final ContainerHistoryData containerHistory, final String user) {
        final String logUrl = WebAppUtils.getAggregatedLogURL(this.serverHttpAddress, containerHistory.getAssignedNode().toString(), containerHistory.getContainerId().toString(), containerHistory.getContainerId().toString(), user);
        return ContainerReport.newInstance(containerHistory.getContainerId(), containerHistory.getAllocatedResource(), containerHistory.getAssignedNode(), containerHistory.getPriority(), containerHistory.getStartTime(), containerHistory.getFinishTime(), containerHistory.getDiagnosticsInfo(), logUrl, containerHistory.getContainerExitStatus(), containerHistory.getContainerState());
    }
    
    @Override
    public Map<ContainerId, ContainerReport> getContainers(final ApplicationAttemptId appAttemptId) throws IOException {
        final ApplicationReport app = this.getApplication(appAttemptId.getApplicationId());
        final Map<ContainerId, ContainerHistoryData> histData = this.historyStore.getContainers(appAttemptId);
        final HashMap<ContainerId, ContainerReport> containersReport = new HashMap<ContainerId, ContainerReport>();
        for (final Map.Entry<ContainerId, ContainerHistoryData> entry : histData.entrySet()) {
            containersReport.put(entry.getKey(), this.convertToContainerReport(entry.getValue(), (app == null) ? null : app.getUser()));
        }
        return containersReport;
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public ApplicationHistoryStore getHistoryStore() {
        return this.historyStore;
    }
    
    static {
        LOG = LogFactory.getLog(ApplicationHistoryManagerImpl.class);
    }
}
