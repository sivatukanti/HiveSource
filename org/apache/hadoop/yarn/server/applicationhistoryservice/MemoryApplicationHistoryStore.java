// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice;

import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerFinishData;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerStartData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptFinishData;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptStartData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationFinishData;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationStartData;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerHistoryData;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptHistoryData;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationHistoryData;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class MemoryApplicationHistoryStore extends AbstractService implements ApplicationHistoryStore
{
    private final ConcurrentMap<ApplicationId, ApplicationHistoryData> applicationData;
    private final ConcurrentMap<ApplicationId, ConcurrentMap<ApplicationAttemptId, ApplicationAttemptHistoryData>> applicationAttemptData;
    private final ConcurrentMap<ApplicationAttemptId, ConcurrentMap<ContainerId, ContainerHistoryData>> containerData;
    
    public MemoryApplicationHistoryStore() {
        super(MemoryApplicationHistoryStore.class.getName());
        this.applicationData = new ConcurrentHashMap<ApplicationId, ApplicationHistoryData>();
        this.applicationAttemptData = new ConcurrentHashMap<ApplicationId, ConcurrentMap<ApplicationAttemptId, ApplicationAttemptHistoryData>>();
        this.containerData = new ConcurrentHashMap<ApplicationAttemptId, ConcurrentMap<ContainerId, ContainerHistoryData>>();
    }
    
    @Override
    public Map<ApplicationId, ApplicationHistoryData> getAllApplications() {
        return new HashMap<ApplicationId, ApplicationHistoryData>(this.applicationData);
    }
    
    @Override
    public ApplicationHistoryData getApplication(final ApplicationId appId) {
        return this.applicationData.get(appId);
    }
    
    @Override
    public Map<ApplicationAttemptId, ApplicationAttemptHistoryData> getApplicationAttempts(final ApplicationId appId) {
        final ConcurrentMap<ApplicationAttemptId, ApplicationAttemptHistoryData> subMap = this.applicationAttemptData.get(appId);
        if (subMap == null) {
            return Collections.emptyMap();
        }
        return new HashMap<ApplicationAttemptId, ApplicationAttemptHistoryData>(subMap);
    }
    
    @Override
    public ApplicationAttemptHistoryData getApplicationAttempt(final ApplicationAttemptId appAttemptId) {
        final ConcurrentMap<ApplicationAttemptId, ApplicationAttemptHistoryData> subMap = this.applicationAttemptData.get(appAttemptId.getApplicationId());
        if (subMap == null) {
            return null;
        }
        return subMap.get(appAttemptId);
    }
    
    @Override
    public ContainerHistoryData getAMContainer(final ApplicationAttemptId appAttemptId) {
        final ApplicationAttemptHistoryData appAttempt = this.getApplicationAttempt(appAttemptId);
        if (appAttempt == null || appAttempt.getMasterContainerId() == null) {
            return null;
        }
        return this.getContainer(appAttempt.getMasterContainerId());
    }
    
    @Override
    public ContainerHistoryData getContainer(final ContainerId containerId) {
        final Map<ContainerId, ContainerHistoryData> subMap = this.containerData.get(containerId.getApplicationAttemptId());
        if (subMap == null) {
            return null;
        }
        return subMap.get(containerId);
    }
    
    @Override
    public Map<ContainerId, ContainerHistoryData> getContainers(final ApplicationAttemptId appAttemptId) throws IOException {
        final ConcurrentMap<ContainerId, ContainerHistoryData> subMap = this.containerData.get(appAttemptId);
        if (subMap == null) {
            return Collections.emptyMap();
        }
        return new HashMap<ContainerId, ContainerHistoryData>(subMap);
    }
    
    @Override
    public void applicationStarted(final ApplicationStartData appStart) throws IOException {
        final ApplicationHistoryData oldData = this.applicationData.putIfAbsent(appStart.getApplicationId(), ApplicationHistoryData.newInstance(appStart.getApplicationId(), appStart.getApplicationName(), appStart.getApplicationType(), appStart.getQueue(), appStart.getUser(), appStart.getSubmitTime(), appStart.getStartTime(), Long.MAX_VALUE, null, null, null));
        if (oldData != null) {
            throw new IOException("The start information of application " + appStart.getApplicationId() + " is already stored.");
        }
    }
    
    @Override
    public void applicationFinished(final ApplicationFinishData appFinish) throws IOException {
        final ApplicationHistoryData data = this.applicationData.get(appFinish.getApplicationId());
        if (data == null) {
            throw new IOException("The finish information of application " + appFinish.getApplicationId() + " is stored before the start" + " information.");
        }
        if (data.getYarnApplicationState() != null) {
            throw new IOException("The finish information of application " + appFinish.getApplicationId() + " is already stored.");
        }
        data.setFinishTime(appFinish.getFinishTime());
        data.setDiagnosticsInfo(appFinish.getDiagnosticsInfo());
        data.setFinalApplicationStatus(appFinish.getFinalApplicationStatus());
        data.setYarnApplicationState(appFinish.getYarnApplicationState());
    }
    
    @Override
    public void applicationAttemptStarted(final ApplicationAttemptStartData appAttemptStart) throws IOException {
        final ConcurrentMap<ApplicationAttemptId, ApplicationAttemptHistoryData> subMap = this.getSubMap(appAttemptStart.getApplicationAttemptId().getApplicationId());
        final ApplicationAttemptHistoryData oldData = subMap.putIfAbsent(appAttemptStart.getApplicationAttemptId(), ApplicationAttemptHistoryData.newInstance(appAttemptStart.getApplicationAttemptId(), appAttemptStart.getHost(), appAttemptStart.getRPCPort(), appAttemptStart.getMasterContainerId(), null, null, null, null));
        if (oldData != null) {
            throw new IOException("The start information of application attempt " + appAttemptStart.getApplicationAttemptId() + " is already stored.");
        }
    }
    
    @Override
    public void applicationAttemptFinished(final ApplicationAttemptFinishData appAttemptFinish) throws IOException {
        final ConcurrentMap<ApplicationAttemptId, ApplicationAttemptHistoryData> subMap = this.getSubMap(appAttemptFinish.getApplicationAttemptId().getApplicationId());
        final ApplicationAttemptHistoryData data = subMap.get(appAttemptFinish.getApplicationAttemptId());
        if (data == null) {
            throw new IOException("The finish information of application attempt " + appAttemptFinish.getApplicationAttemptId() + " is stored before" + " the start information.");
        }
        if (data.getYarnApplicationAttemptState() != null) {
            throw new IOException("The finish information of application attempt " + appAttemptFinish.getApplicationAttemptId() + " is already stored.");
        }
        data.setTrackingURL(appAttemptFinish.getTrackingURL());
        data.setDiagnosticsInfo(appAttemptFinish.getDiagnosticsInfo());
        data.setFinalApplicationStatus(appAttemptFinish.getFinalApplicationStatus());
        data.setYarnApplicationAttemptState(appAttemptFinish.getYarnApplicationAttemptState());
    }
    
    private ConcurrentMap<ApplicationAttemptId, ApplicationAttemptHistoryData> getSubMap(final ApplicationId appId) {
        this.applicationAttemptData.putIfAbsent(appId, new ConcurrentHashMap<ApplicationAttemptId, ApplicationAttemptHistoryData>());
        return this.applicationAttemptData.get(appId);
    }
    
    @Override
    public void containerStarted(final ContainerStartData containerStart) throws IOException {
        final ConcurrentMap<ContainerId, ContainerHistoryData> subMap = this.getSubMap(containerStart.getContainerId().getApplicationAttemptId());
        final ContainerHistoryData oldData = subMap.putIfAbsent(containerStart.getContainerId(), ContainerHistoryData.newInstance(containerStart.getContainerId(), containerStart.getAllocatedResource(), containerStart.getAssignedNode(), containerStart.getPriority(), containerStart.getStartTime(), Long.MAX_VALUE, null, Integer.MAX_VALUE, null));
        if (oldData != null) {
            throw new IOException("The start information of container " + containerStart.getContainerId() + " is already stored.");
        }
    }
    
    @Override
    public void containerFinished(final ContainerFinishData containerFinish) throws IOException {
        final ConcurrentMap<ContainerId, ContainerHistoryData> subMap = this.getSubMap(containerFinish.getContainerId().getApplicationAttemptId());
        final ContainerHistoryData data = subMap.get(containerFinish.getContainerId());
        if (data == null) {
            throw new IOException("The finish information of container " + containerFinish.getContainerId() + " is stored before" + " the start information.");
        }
        if (data.getContainerState() != null) {
            throw new IOException("The finish information of container " + containerFinish.getContainerId() + " is already stored.");
        }
        data.setFinishTime(containerFinish.getFinishTime());
        data.setDiagnosticsInfo(containerFinish.getDiagnosticsInfo());
        data.setContainerExitStatus(containerFinish.getContainerExitStatus());
        data.setContainerState(containerFinish.getContainerState());
    }
    
    private ConcurrentMap<ContainerId, ContainerHistoryData> getSubMap(final ApplicationAttemptId appAttemptId) {
        this.containerData.putIfAbsent(appAttemptId, new ConcurrentHashMap<ContainerId, ContainerHistoryData>());
        return this.containerData.get(appAttemptId);
    }
}
