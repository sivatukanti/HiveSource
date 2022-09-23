// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmnode;

import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.yarn.server.api.records.NodeHealthStatus;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeResourceUpdateSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent;
import java.util.Iterator;
import org.apache.hadoop.yarn.server.resourcemanager.NodesListManagerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.NodesListManagerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import org.apache.hadoop.yarn.state.MultipleArcTransition;
import java.util.EnumSet;
import org.apache.hadoop.yarn.state.SingleArcTransition;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.nodelabels.CommonNodeLabelsManager;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.api.records.ResourceOption;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppRunningOnNodeEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.ClusterMetrics;
import org.apache.hadoop.yarn.state.InvalidStateTransitonException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import java.util.HashSet;
import org.apache.hadoop.yarn.state.StateMachine;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.state.StateMachineFactory;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.Set;
import org.apache.hadoop.net.Node;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.event.EventHandler;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RMNodeImpl implements RMNode, EventHandler<RMNodeEvent>
{
    private static final Log LOG;
    private static final RecordFactory recordFactory;
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;
    private final ConcurrentLinkedQueue<UpdatedContainerInfo> nodeUpdateQueue;
    private volatile boolean nextHeartBeat;
    private final NodeId nodeId;
    private final RMContext context;
    private final String hostName;
    private final int commandPort;
    private int httpPort;
    private final String nodeAddress;
    private String httpAddress;
    private volatile Resource totalCapability;
    private final Node node;
    private String healthReport;
    private long lastHealthReportTime;
    private String nodeManagerVersion;
    private final Set<ContainerId> launchedContainers;
    private final Set<ContainerId> containersToClean;
    private final Set<ContainerId> containersToBeRemovedFromNM;
    private final List<ApplicationId> finishedApplications;
    private NodeHeartbeatResponse latestNodeHeartBeatResponse;
    private static final StateMachineFactory<RMNodeImpl, NodeState, RMNodeEventType, RMNodeEvent> stateMachineFactory;
    private final StateMachine<NodeState, RMNodeEventType, RMNodeEvent> stateMachine;
    
    public RMNodeImpl(final NodeId nodeId, final RMContext context, final String hostName, final int cmPort, final int httpPort, final Node node, final Resource capability, final String nodeManagerVersion) {
        this.nextHeartBeat = true;
        this.launchedContainers = new HashSet<ContainerId>();
        this.containersToClean = new TreeSet<ContainerId>(new BuilderUtils.ContainerIdComparator());
        this.containersToBeRemovedFromNM = new HashSet<ContainerId>();
        this.finishedApplications = new ArrayList<ApplicationId>();
        this.latestNodeHeartBeatResponse = RMNodeImpl.recordFactory.newRecordInstance(NodeHeartbeatResponse.class);
        this.nodeId = nodeId;
        this.context = context;
        this.hostName = hostName;
        this.commandPort = cmPort;
        this.httpPort = httpPort;
        this.totalCapability = capability;
        this.nodeAddress = hostName + ":" + cmPort;
        this.httpAddress = hostName + ":" + httpPort;
        this.node = node;
        this.healthReport = "Healthy";
        this.lastHealthReportTime = System.currentTimeMillis();
        this.nodeManagerVersion = nodeManagerVersion;
        this.latestNodeHeartBeatResponse.setResponseId(0);
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
        this.stateMachine = RMNodeImpl.stateMachineFactory.make(this);
        this.nodeUpdateQueue = new ConcurrentLinkedQueue<UpdatedContainerInfo>();
    }
    
    @Override
    public String toString() {
        return this.nodeId.toString();
    }
    
    @Override
    public String getHostName() {
        return this.hostName;
    }
    
    @Override
    public int getCommandPort() {
        return this.commandPort;
    }
    
    @Override
    public int getHttpPort() {
        return this.httpPort;
    }
    
    @Override
    public NodeId getNodeID() {
        return this.nodeId;
    }
    
    @Override
    public String getNodeAddress() {
        return this.nodeAddress;
    }
    
    @Override
    public String getHttpAddress() {
        return this.httpAddress;
    }
    
    @Override
    public Resource getTotalCapability() {
        return this.totalCapability;
    }
    
    @Override
    public String getRackName() {
        return this.node.getNetworkLocation();
    }
    
    @Override
    public Node getNode() {
        return this.node;
    }
    
    @Override
    public String getHealthReport() {
        this.readLock.lock();
        try {
            return this.healthReport;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public void setHealthReport(final String healthReport) {
        this.writeLock.lock();
        try {
            this.healthReport = healthReport;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public void setLastHealthReportTime(final long lastHealthReportTime) {
        this.writeLock.lock();
        try {
            this.lastHealthReportTime = lastHealthReportTime;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public long getLastHealthReportTime() {
        this.readLock.lock();
        try {
            return this.lastHealthReportTime;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getNodeManagerVersion() {
        return this.nodeManagerVersion;
    }
    
    @Override
    public NodeState getState() {
        this.readLock.lock();
        try {
            return this.stateMachine.getCurrentState();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public List<ApplicationId> getAppsToCleanup() {
        this.readLock.lock();
        try {
            return new ArrayList<ApplicationId>(this.finishedApplications);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public List<ContainerId> getContainersToCleanUp() {
        this.readLock.lock();
        try {
            return new ArrayList<ContainerId>(this.containersToClean);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void updateNodeHeartbeatResponseForCleanup(final NodeHeartbeatResponse response) {
        this.writeLock.lock();
        try {
            response.addAllContainersToCleanup(new ArrayList<ContainerId>(this.containersToClean));
            response.addAllApplicationsToCleanup(this.finishedApplications);
            response.addContainersToBeRemovedFromNM(new ArrayList<ContainerId>(this.containersToBeRemovedFromNM));
            this.containersToClean.clear();
            this.finishedApplications.clear();
            this.containersToBeRemovedFromNM.clear();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public NodeHeartbeatResponse getLastNodeHeartBeatResponse() {
        this.readLock.lock();
        try {
            return this.latestNodeHeartBeatResponse;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void handle(final RMNodeEvent event) {
        RMNodeImpl.LOG.debug("Processing " + event.getNodeId() + " of type " + ((AbstractEvent<Object>)event).getType());
        try {
            this.writeLock.lock();
            final NodeState oldState = this.getState();
            try {
                this.stateMachine.doTransition(event.getType(), event);
            }
            catch (InvalidStateTransitonException e) {
                RMNodeImpl.LOG.error("Can't handle this event at current state", e);
                RMNodeImpl.LOG.error("Invalid event " + ((AbstractEvent<Object>)event).getType() + " on Node  " + this.nodeId);
            }
            if (oldState != this.getState()) {
                RMNodeImpl.LOG.info(this.nodeId + " Node Transitioned from " + oldState + " to " + this.getState());
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    private void updateMetricsForRejoinedNode(final NodeState previousNodeState) {
        final ClusterMetrics metrics = ClusterMetrics.getMetrics();
        metrics.incrNumActiveNodes();
        switch (previousNodeState) {
            case LOST: {
                metrics.decrNumLostNMs();
                break;
            }
            case REBOOTED: {
                metrics.decrNumRebootedNMs();
                break;
            }
            case DECOMMISSIONED: {
                metrics.decrDecommisionedNMs();
                break;
            }
            case UNHEALTHY: {
                metrics.decrNumUnhealthyNMs();
                break;
            }
        }
    }
    
    private void updateMetricsForDeactivatedNode(final NodeState initialState, final NodeState finalState) {
        final ClusterMetrics metrics = ClusterMetrics.getMetrics();
        switch (initialState) {
            case RUNNING: {
                metrics.decrNumActiveNodes();
                break;
            }
            case UNHEALTHY: {
                metrics.decrNumUnhealthyNMs();
                break;
            }
        }
        switch (finalState) {
            case DECOMMISSIONED: {
                metrics.incrDecommisionedNMs();
                break;
            }
            case LOST: {
                metrics.incrNumLostNMs();
                break;
            }
            case REBOOTED: {
                metrics.incrNumRebootedNMs();
                break;
            }
            case UNHEALTHY: {
                metrics.incrNumUnhealthyNMs();
                break;
            }
        }
    }
    
    private static void handleRunningAppOnNode(final RMNodeImpl rmNode, final RMContext context, final ApplicationId appId, final NodeId nodeId) {
        final RMApp app = context.getRMApps().get(appId);
        if (null == app) {
            RMNodeImpl.LOG.warn("Cannot get RMApp by appId=" + appId + ", just added it to finishedApplications list for cleanup");
            rmNode.finishedApplications.add(appId);
            return;
        }
        context.getDispatcher().getEventHandler().handle(new RMAppRunningOnNodeEvent(appId, nodeId));
    }
    
    private static void updateNodeResourceFromEvent(final RMNodeImpl rmNode, final RMNodeResourceUpdateEvent event) {
        final ResourceOption resourceOption = event.getResourceOption();
        rmNode.totalCapability = resourceOption.getResource();
    }
    
    @Override
    public List<UpdatedContainerInfo> pullContainerUpdates() {
        final List<UpdatedContainerInfo> latestContainerInfoList = new ArrayList<UpdatedContainerInfo>();
        while (this.nodeUpdateQueue.peek() != null) {
            latestContainerInfoList.add(this.nodeUpdateQueue.poll());
        }
        this.nextHeartBeat = true;
        return latestContainerInfoList;
    }
    
    @VisibleForTesting
    public void setNextHeartBeat(final boolean nextHeartBeat) {
        this.nextHeartBeat = nextHeartBeat;
    }
    
    @VisibleForTesting
    public int getQueueSize() {
        return this.nodeUpdateQueue.size();
    }
    
    @VisibleForTesting
    public Set<ContainerId> getLaunchedContainers() {
        return this.launchedContainers;
    }
    
    @Override
    public Set<String> getNodeLabels() {
        if (this.context.getNodeLabelManager() == null) {
            return CommonNodeLabelsManager.EMPTY_STRING_SET;
        }
        return this.context.getNodeLabelManager().getLabelsOnNode(this.nodeId);
    }
    
    static {
        LOG = LogFactory.getLog(RMNodeImpl.class);
        recordFactory = RecordFactoryProvider.getRecordFactory(null);
        stateMachineFactory = new StateMachineFactory<RMNodeImpl, NodeState, RMNodeEventType, RMNodeEvent>(NodeState.NEW).addTransition(NodeState.NEW, NodeState.RUNNING, RMNodeEventType.STARTED, new AddNodeTransition()).addTransition(NodeState.NEW, NodeState.NEW, RMNodeEventType.RESOURCE_UPDATE, new UpdateNodeResourceWhenUnusableTransition()).addTransition(NodeState.RUNNING, EnumSet.of(NodeState.RUNNING, NodeState.UNHEALTHY), RMNodeEventType.STATUS_UPDATE, new StatusUpdateWhenHealthyTransition()).addTransition(NodeState.RUNNING, NodeState.DECOMMISSIONED, RMNodeEventType.DECOMMISSION, new DeactivateNodeTransition(NodeState.DECOMMISSIONED)).addTransition(NodeState.RUNNING, NodeState.LOST, RMNodeEventType.EXPIRE, new DeactivateNodeTransition(NodeState.LOST)).addTransition(NodeState.RUNNING, NodeState.REBOOTED, RMNodeEventType.REBOOTING, new DeactivateNodeTransition(NodeState.REBOOTED)).addTransition(NodeState.RUNNING, NodeState.RUNNING, RMNodeEventType.CLEANUP_APP, new CleanUpAppTransition()).addTransition(NodeState.RUNNING, NodeState.RUNNING, RMNodeEventType.CLEANUP_CONTAINER, new CleanUpContainerTransition()).addTransition(NodeState.RUNNING, NodeState.RUNNING, RMNodeEventType.FINISHED_CONTAINERS_PULLED_BY_AM, new AddContainersToBeRemovedFromNMTransition()).addTransition(NodeState.RUNNING, NodeState.RUNNING, RMNodeEventType.RECONNECTED, new ReconnectNodeTransition()).addTransition(NodeState.RUNNING, NodeState.RUNNING, RMNodeEventType.RESOURCE_UPDATE, new UpdateNodeResourceWhenRunningTransition()).addTransition(NodeState.REBOOTED, NodeState.REBOOTED, RMNodeEventType.RESOURCE_UPDATE, new UpdateNodeResourceWhenUnusableTransition()).addTransition(NodeState.DECOMMISSIONED, NodeState.DECOMMISSIONED, RMNodeEventType.RESOURCE_UPDATE, new UpdateNodeResourceWhenUnusableTransition()).addTransition(NodeState.DECOMMISSIONED, NodeState.DECOMMISSIONED, RMNodeEventType.FINISHED_CONTAINERS_PULLED_BY_AM, new AddContainersToBeRemovedFromNMTransition()).addTransition(NodeState.LOST, NodeState.LOST, RMNodeEventType.RESOURCE_UPDATE, new UpdateNodeResourceWhenUnusableTransition()).addTransition(NodeState.LOST, NodeState.LOST, RMNodeEventType.FINISHED_CONTAINERS_PULLED_BY_AM, new AddContainersToBeRemovedFromNMTransition()).addTransition(NodeState.UNHEALTHY, EnumSet.of(NodeState.UNHEALTHY, NodeState.RUNNING), RMNodeEventType.STATUS_UPDATE, new StatusUpdateWhenUnHealthyTransition()).addTransition(NodeState.UNHEALTHY, NodeState.DECOMMISSIONED, RMNodeEventType.DECOMMISSION, new DeactivateNodeTransition(NodeState.DECOMMISSIONED)).addTransition(NodeState.UNHEALTHY, NodeState.LOST, RMNodeEventType.EXPIRE, new DeactivateNodeTransition(NodeState.LOST)).addTransition(NodeState.UNHEALTHY, NodeState.REBOOTED, RMNodeEventType.REBOOTING, new DeactivateNodeTransition(NodeState.REBOOTED)).addTransition(NodeState.UNHEALTHY, NodeState.UNHEALTHY, RMNodeEventType.RECONNECTED, new ReconnectNodeTransition()).addTransition(NodeState.UNHEALTHY, NodeState.UNHEALTHY, RMNodeEventType.CLEANUP_APP, new CleanUpAppTransition()).addTransition(NodeState.UNHEALTHY, NodeState.UNHEALTHY, RMNodeEventType.CLEANUP_CONTAINER, new CleanUpContainerTransition()).addTransition(NodeState.UNHEALTHY, NodeState.UNHEALTHY, RMNodeEventType.RESOURCE_UPDATE, new UpdateNodeResourceWhenUnusableTransition()).addTransition(NodeState.UNHEALTHY, NodeState.UNHEALTHY, RMNodeEventType.FINISHED_CONTAINERS_PULLED_BY_AM, new AddContainersToBeRemovedFromNMTransition()).installTopology();
    }
    
    public static class AddNodeTransition implements SingleArcTransition<RMNodeImpl, RMNodeEvent>
    {
        @Override
        public void transition(final RMNodeImpl rmNode, final RMNodeEvent event) {
            final RMNodeStartedEvent startEvent = (RMNodeStartedEvent)event;
            List<NMContainerStatus> containers = null;
            final String host = rmNode.nodeId.getHost();
            if (rmNode.context.getInactiveRMNodes().containsKey(host)) {
                final RMNode previouRMNode = rmNode.context.getInactiveRMNodes().get(host);
                rmNode.context.getInactiveRMNodes().remove(host);
                rmNode.updateMetricsForRejoinedNode(previouRMNode.getState());
            }
            else {
                ClusterMetrics.getMetrics().incrNumActiveNodes();
                containers = startEvent.getNMContainerStatuses();
                if (containers != null && !containers.isEmpty()) {
                    for (final NMContainerStatus container : containers) {
                        if (container.getContainerState() == ContainerState.RUNNING) {
                            rmNode.launchedContainers.add(container.getContainerId());
                        }
                    }
                }
            }
            if (null != startEvent.getRunningApplications()) {
                for (final ApplicationId appId : startEvent.getRunningApplications()) {
                    handleRunningAppOnNode(rmNode, rmNode.context, appId, rmNode.nodeId);
                }
            }
            rmNode.context.getDispatcher().getEventHandler().handle(new NodeAddedSchedulerEvent(rmNode, containers));
            rmNode.context.getDispatcher().getEventHandler().handle(new NodesListManagerEvent(NodesListManagerEventType.NODE_USABLE, rmNode));
        }
    }
    
    public static class ReconnectNodeTransition implements SingleArcTransition<RMNodeImpl, RMNodeEvent>
    {
        @Override
        public void transition(final RMNodeImpl rmNode, final RMNodeEvent event) {
            final RMNodeReconnectEvent reconnectEvent = (RMNodeReconnectEvent)event;
            final RMNode newNode = reconnectEvent.getReconnectedNode();
            rmNode.nodeManagerVersion = newNode.getNodeManagerVersion();
            final List<ApplicationId> runningApps = reconnectEvent.getRunningApplications();
            final boolean noRunningApps = runningApps == null || runningApps.size() == 0;
            if (noRunningApps) {
                rmNode.nodeUpdateQueue.clear();
                rmNode.context.getDispatcher().getEventHandler().handle(new NodeRemovedSchedulerEvent(rmNode));
                if (rmNode.getHttpPort() == newNode.getHttpPort()) {
                    rmNode.getLastNodeHeartBeatResponse().setResponseId(0);
                    if (rmNode.getState() != NodeState.UNHEALTHY) {
                        rmNode.context.getDispatcher().getEventHandler().handle(new NodeAddedSchedulerEvent(newNode));
                    }
                }
                else {
                    switch (rmNode.getState()) {
                        case RUNNING: {
                            ClusterMetrics.getMetrics().decrNumActiveNodes();
                            break;
                        }
                        case UNHEALTHY: {
                            ClusterMetrics.getMetrics().decrNumUnhealthyNMs();
                            break;
                        }
                    }
                    rmNode.context.getRMNodes().put(newNode.getNodeID(), newNode);
                    rmNode.context.getDispatcher().getEventHandler().handle(new RMNodeStartedEvent(newNode.getNodeID(), null, null));
                }
            }
            else {
                rmNode.httpPort = newNode.getHttpPort();
                rmNode.httpAddress = newNode.getHttpAddress();
                rmNode.totalCapability = newNode.getTotalCapability();
                rmNode.getLastNodeHeartBeatResponse().setResponseId(0);
            }
            if (null != reconnectEvent.getRunningApplications()) {
                for (final ApplicationId appId : reconnectEvent.getRunningApplications()) {
                    handleRunningAppOnNode(rmNode, rmNode.context, appId, rmNode.nodeId);
                }
            }
            rmNode.context.getDispatcher().getEventHandler().handle(new NodesListManagerEvent(NodesListManagerEventType.NODE_USABLE, rmNode));
            if (rmNode.getState().equals(NodeState.RUNNING)) {
                rmNode.context.getDispatcher().getEventHandler().handle(new NodeResourceUpdateSchedulerEvent(rmNode, ResourceOption.newInstance(newNode.getTotalCapability(), -1)));
            }
        }
    }
    
    public static class UpdateNodeResourceWhenRunningTransition implements SingleArcTransition<RMNodeImpl, RMNodeEvent>
    {
        @Override
        public void transition(final RMNodeImpl rmNode, final RMNodeEvent event) {
            final RMNodeResourceUpdateEvent updateEvent = (RMNodeResourceUpdateEvent)event;
            updateNodeResourceFromEvent(rmNode, updateEvent);
            rmNode.context.getDispatcher().getEventHandler().handle(new NodeResourceUpdateSchedulerEvent(rmNode, updateEvent.getResourceOption()));
        }
    }
    
    public static class UpdateNodeResourceWhenUnusableTransition implements SingleArcTransition<RMNodeImpl, RMNodeEvent>
    {
        @Override
        public void transition(final RMNodeImpl rmNode, final RMNodeEvent event) {
            RMNodeImpl.LOG.warn("Try to update resource on a " + rmNode.getState().toString() + " node: " + rmNode.toString());
            updateNodeResourceFromEvent(rmNode, (RMNodeResourceUpdateEvent)event);
        }
    }
    
    public static class CleanUpAppTransition implements SingleArcTransition<RMNodeImpl, RMNodeEvent>
    {
        @Override
        public void transition(final RMNodeImpl rmNode, final RMNodeEvent event) {
            rmNode.finishedApplications.add(((RMNodeCleanAppEvent)event).getAppId());
        }
    }
    
    public static class CleanUpContainerTransition implements SingleArcTransition<RMNodeImpl, RMNodeEvent>
    {
        @Override
        public void transition(final RMNodeImpl rmNode, final RMNodeEvent event) {
            rmNode.containersToClean.add(((RMNodeCleanContainerEvent)event).getContainerId());
        }
    }
    
    public static class AddContainersToBeRemovedFromNMTransition implements SingleArcTransition<RMNodeImpl, RMNodeEvent>
    {
        @Override
        public void transition(final RMNodeImpl rmNode, final RMNodeEvent event) {
            rmNode.containersToBeRemovedFromNM.addAll(((RMNodeFinishedContainersPulledByAMEvent)event).getContainers());
        }
    }
    
    public static class DeactivateNodeTransition implements SingleArcTransition<RMNodeImpl, RMNodeEvent>
    {
        private final NodeState finalState;
        
        public DeactivateNodeTransition(final NodeState finalState) {
            this.finalState = finalState;
        }
        
        @Override
        public void transition(final RMNodeImpl rmNode, final RMNodeEvent event) {
            rmNode.nodeUpdateQueue.clear();
            final NodeState initialState = rmNode.getState();
            if (!initialState.equals(NodeState.UNHEALTHY)) {
                rmNode.context.getDispatcher().getEventHandler().handle(new NodeRemovedSchedulerEvent(rmNode));
            }
            rmNode.context.getDispatcher().getEventHandler().handle(new NodesListManagerEvent(NodesListManagerEventType.NODE_UNUSABLE, rmNode));
            rmNode.context.getRMNodes().remove(rmNode.nodeId);
            RMNodeImpl.LOG.info("Deactivating Node " + rmNode.nodeId + " as it is now " + this.finalState);
            rmNode.context.getInactiveRMNodes().put(rmNode.nodeId.getHost(), rmNode);
            rmNode.updateMetricsForDeactivatedNode(initialState, this.finalState);
        }
    }
    
    public static class StatusUpdateWhenHealthyTransition implements MultipleArcTransition<RMNodeImpl, RMNodeEvent, NodeState>
    {
        @Override
        public NodeState transition(final RMNodeImpl rmNode, final RMNodeEvent event) {
            final RMNodeStatusEvent statusEvent = (RMNodeStatusEvent)event;
            rmNode.latestNodeHeartBeatResponse = statusEvent.getLatestResponse();
            final NodeHealthStatus remoteNodeHealthStatus = statusEvent.getNodeHealthStatus();
            rmNode.setHealthReport(remoteNodeHealthStatus.getHealthReport());
            rmNode.setLastHealthReportTime(remoteNodeHealthStatus.getLastHealthReportTime());
            if (!remoteNodeHealthStatus.getIsNodeHealthy()) {
                RMNodeImpl.LOG.info("Node " + rmNode.nodeId + " reported UNHEALTHY with details: " + remoteNodeHealthStatus.getHealthReport());
                rmNode.nodeUpdateQueue.clear();
                rmNode.context.getDispatcher().getEventHandler().handle(new NodeRemovedSchedulerEvent(rmNode));
                rmNode.context.getDispatcher().getEventHandler().handle(new NodesListManagerEvent(NodesListManagerEventType.NODE_UNUSABLE, rmNode));
                rmNode.updateMetricsForDeactivatedNode(rmNode.getState(), NodeState.UNHEALTHY);
                return NodeState.UNHEALTHY;
            }
            final List<ContainerStatus> newlyLaunchedContainers = new ArrayList<ContainerStatus>();
            final List<ContainerStatus> completedContainers = new ArrayList<ContainerStatus>();
            for (final ContainerStatus remoteContainer : statusEvent.getContainers()) {
                final ContainerId containerId = remoteContainer.getContainerId();
                if (rmNode.containersToClean.contains(containerId)) {
                    RMNodeImpl.LOG.info("Container " + containerId + " already scheduled for " + "cleanup, no further processing");
                }
                else if (rmNode.finishedApplications.contains(containerId.getApplicationAttemptId().getApplicationId())) {
                    RMNodeImpl.LOG.info("Container " + containerId + " belongs to an application that is already killed," + " no further processing");
                }
                else if (remoteContainer.getState() == ContainerState.RUNNING) {
                    if (rmNode.launchedContainers.contains(containerId)) {
                        continue;
                    }
                    rmNode.launchedContainers.add(containerId);
                    newlyLaunchedContainers.add(remoteContainer);
                }
                else {
                    rmNode.launchedContainers.remove(containerId);
                    completedContainers.add(remoteContainer);
                }
            }
            if (newlyLaunchedContainers.size() != 0 || completedContainers.size() != 0) {
                rmNode.nodeUpdateQueue.add(new UpdatedContainerInfo(newlyLaunchedContainers, completedContainers));
            }
            if (rmNode.nextHeartBeat) {
                rmNode.nextHeartBeat = false;
                rmNode.context.getDispatcher().getEventHandler().handle(new NodeUpdateSchedulerEvent(rmNode));
            }
            if (UserGroupInformation.isSecurityEnabled()) {
                rmNode.context.getDelegationTokenRenewer().updateKeepAliveApplications(statusEvent.getKeepAliveAppIds());
            }
            return NodeState.RUNNING;
        }
    }
    
    public static class StatusUpdateWhenUnHealthyTransition implements MultipleArcTransition<RMNodeImpl, RMNodeEvent, NodeState>
    {
        @Override
        public NodeState transition(final RMNodeImpl rmNode, final RMNodeEvent event) {
            final RMNodeStatusEvent statusEvent = (RMNodeStatusEvent)event;
            rmNode.latestNodeHeartBeatResponse = statusEvent.getLatestResponse();
            final NodeHealthStatus remoteNodeHealthStatus = statusEvent.getNodeHealthStatus();
            rmNode.setHealthReport(remoteNodeHealthStatus.getHealthReport());
            rmNode.setLastHealthReportTime(remoteNodeHealthStatus.getLastHealthReportTime());
            if (remoteNodeHealthStatus.getIsNodeHealthy()) {
                rmNode.context.getDispatcher().getEventHandler().handle(new NodeAddedSchedulerEvent(rmNode));
                rmNode.context.getDispatcher().getEventHandler().handle(new NodesListManagerEvent(NodesListManagerEventType.NODE_USABLE, rmNode));
                rmNode.updateMetricsForRejoinedNode(NodeState.UNHEALTHY);
                return NodeState.RUNNING;
            }
            return NodeState.UNHEALTHY;
        }
    }
}
