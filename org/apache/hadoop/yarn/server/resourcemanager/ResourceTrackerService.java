// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.net.Node;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent;
import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import org.apache.hadoop.yarn.server.utils.YarnServerBuilderUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEventType;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeReconnectEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStartedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeImpl;
import org.apache.hadoop.yarn.server.api.records.NodeAction;
import org.apache.hadoop.util.VersionUtil;
import org.apache.hadoop.yarn.util.YarnVersionInfo;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import java.io.InputStream;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.yarn.server.resourcemanager.security.authorize.RMPolicyProvider;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.security.token.SecretManager;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.util.RackResolver;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import java.net.InetSocketAddress;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.api.ResourceTracker;
import org.apache.hadoop.service.AbstractService;

public class ResourceTrackerService extends AbstractService implements ResourceTracker
{
    private static final Log LOG;
    private static final RecordFactory recordFactory;
    private final RMContext rmContext;
    private final NodesListManager nodesListManager;
    private final NMLivelinessMonitor nmLivelinessMonitor;
    private final RMContainerTokenSecretManager containerTokenSecretManager;
    private final NMTokenSecretManagerInRM nmTokenSecretManager;
    private long nextHeartBeatInterval;
    private Server server;
    private InetSocketAddress resourceTrackerAddress;
    private String minimumNodeManagerVersion;
    private static final NodeHeartbeatResponse resync;
    private static final NodeHeartbeatResponse shutDown;
    private int minAllocMb;
    private int minAllocVcores;
    
    public ResourceTrackerService(final RMContext rmContext, final NodesListManager nodesListManager, final NMLivelinessMonitor nmLivelinessMonitor, final RMContainerTokenSecretManager containerTokenSecretManager, final NMTokenSecretManagerInRM nmTokenSecretManager) {
        super(ResourceTrackerService.class.getName());
        this.rmContext = rmContext;
        this.nodesListManager = nodesListManager;
        this.nmLivelinessMonitor = nmLivelinessMonitor;
        this.containerTokenSecretManager = containerTokenSecretManager;
        this.nmTokenSecretManager = nmTokenSecretManager;
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.resourceTrackerAddress = conf.getSocketAddr("yarn.resourcemanager.bind-host", "yarn.resourcemanager.resource-tracker.address", "0.0.0.0:8031", 8031);
        RackResolver.init(conf);
        this.nextHeartBeatInterval = conf.getLong("yarn.resourcemanager.nodemanagers.heartbeat-interval-ms", 1000L);
        if (this.nextHeartBeatInterval <= 0L) {
            throw new YarnRuntimeException("Invalid Configuration. yarn.resourcemanager.nodemanagers.heartbeat-interval-ms should be larger than 0.");
        }
        this.minAllocMb = conf.getInt("yarn.scheduler.minimum-allocation-mb", 1024);
        this.minAllocVcores = conf.getInt("yarn.scheduler.minimum-allocation-vcores", 1);
        this.minimumNodeManagerVersion = conf.get("yarn.resourcemanager.nodemanager.minimum.version", "NONE");
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        super.serviceStart();
        final Configuration conf = this.getConfig();
        final YarnRPC rpc = YarnRPC.create(conf);
        this.server = rpc.getServer(ResourceTracker.class, this, this.resourceTrackerAddress, conf, null, conf.getInt("yarn.resourcemanager.resource-tracker.client.thread-count", 50));
        if (conf.getBoolean("hadoop.security.authorization", false)) {
            final InputStream inputStream = this.rmContext.getConfigurationProvider().getConfigurationInputStream(conf, "hadoop-policy.xml");
            if (inputStream != null) {
                conf.addResource(inputStream);
            }
            this.refreshServiceAcls(conf, RMPolicyProvider.getInstance());
        }
        this.server.start();
        conf.updateConnectAddr("yarn.resourcemanager.bind-host", "yarn.resourcemanager.resource-tracker.address", "0.0.0.0:8031", this.server.getListenerAddress());
    }
    
    @Override
    protected void serviceStop() throws Exception {
        if (this.server != null) {
            this.server.stop();
        }
        super.serviceStop();
    }
    
    @VisibleForTesting
    void handleNMContainerStatus(final NMContainerStatus containerStatus, final NodeId nodeId) {
        final ApplicationAttemptId appAttemptId = containerStatus.getContainerId().getApplicationAttemptId();
        final RMApp rmApp = this.rmContext.getRMApps().get(appAttemptId.getApplicationId());
        if (rmApp == null) {
            ResourceTrackerService.LOG.error("Received finished container : " + containerStatus.getContainerId() + "for unknown application " + appAttemptId.getApplicationId() + " Skipping.");
            return;
        }
        if (rmApp.getApplicationSubmissionContext().getUnmanagedAM()) {
            if (ResourceTrackerService.LOG.isDebugEnabled()) {
                ResourceTrackerService.LOG.debug("Ignoring container completion status for unmanaged AM" + rmApp.getApplicationId());
            }
            return;
        }
        final RMAppAttempt rmAppAttempt = rmApp.getRMAppAttempt(appAttemptId);
        final Container masterContainer = rmAppAttempt.getMasterContainer();
        if (masterContainer.getId().equals(containerStatus.getContainerId()) && containerStatus.getContainerState() == ContainerState.COMPLETE) {
            final ContainerStatus status = ContainerStatus.newInstance(containerStatus.getContainerId(), containerStatus.getContainerState(), containerStatus.getDiagnostics(), containerStatus.getContainerExitStatus());
            final RMAppAttemptContainerFinishedEvent evt = new RMAppAttemptContainerFinishedEvent(appAttemptId, status, nodeId);
            this.rmContext.getDispatcher().getEventHandler().handle(evt);
        }
    }
    
    @Override
    public RegisterNodeManagerResponse registerNodeManager(final RegisterNodeManagerRequest request) throws YarnException, IOException {
        final NodeId nodeId = request.getNodeId();
        final String host = nodeId.getHost();
        final int cmPort = nodeId.getPort();
        final int httpPort = request.getHttpPort();
        final Resource capability = request.getResource();
        final String nodeManagerVersion = request.getNMVersion();
        final RegisterNodeManagerResponse response = ResourceTrackerService.recordFactory.newRecordInstance(RegisterNodeManagerResponse.class);
        if (!this.minimumNodeManagerVersion.equals("NONE")) {
            if (this.minimumNodeManagerVersion.equals("EqualToRM")) {
                this.minimumNodeManagerVersion = YarnVersionInfo.getVersion();
            }
            if (nodeManagerVersion == null || VersionUtil.compareVersions(nodeManagerVersion, this.minimumNodeManagerVersion) < 0) {
                final String message = "Disallowed NodeManager Version " + nodeManagerVersion + ", is less than the minimum version " + this.minimumNodeManagerVersion + " sending SHUTDOWN signal to " + "NodeManager.";
                ResourceTrackerService.LOG.info(message);
                response.setDiagnosticsMessage(message);
                response.setNodeAction(NodeAction.SHUTDOWN);
                return response;
            }
        }
        if (!this.nodesListManager.isValidNode(host)) {
            final String message = "Disallowed NodeManager from  " + host + ", Sending SHUTDOWN signal to the NodeManager.";
            ResourceTrackerService.LOG.info(message);
            response.setDiagnosticsMessage(message);
            response.setNodeAction(NodeAction.SHUTDOWN);
            return response;
        }
        if (capability.getMemory() < this.minAllocMb || capability.getVirtualCores() < this.minAllocVcores) {
            final String message = "NodeManager from  " + host + " doesn't satisfy minimum allocations, Sending SHUTDOWN" + " signal to the NodeManager.";
            ResourceTrackerService.LOG.info(message);
            response.setDiagnosticsMessage(message);
            response.setNodeAction(NodeAction.SHUTDOWN);
            return response;
        }
        response.setContainerTokenMasterKey(this.containerTokenSecretManager.getCurrentKey());
        response.setNMTokenMasterKey(this.nmTokenSecretManager.getCurrentKey());
        final RMNode rmNode = new RMNodeImpl(nodeId, this.rmContext, host, cmPort, httpPort, resolve(host), capability, nodeManagerVersion);
        final RMNode oldNode = this.rmContext.getRMNodes().putIfAbsent(nodeId, rmNode);
        if (oldNode == null) {
            this.rmContext.getDispatcher().getEventHandler().handle(new RMNodeStartedEvent(nodeId, request.getNMContainerStatuses(), request.getRunningApplications()));
        }
        else {
            ResourceTrackerService.LOG.info("Reconnect from the node at: " + host);
            this.nmLivelinessMonitor.unregister(nodeId);
            this.rmContext.getDispatcher().getEventHandler().handle(new RMNodeReconnectEvent(nodeId, rmNode, request.getRunningApplications()));
        }
        this.nmTokenSecretManager.removeNodeKey(nodeId);
        this.nmLivelinessMonitor.register(nodeId);
        if (!this.rmContext.isWorkPreservingRecoveryEnabled() && !request.getNMContainerStatuses().isEmpty()) {
            ResourceTrackerService.LOG.info("received container statuses on node manager register :" + request.getNMContainerStatuses());
            for (final NMContainerStatus status : request.getNMContainerStatuses()) {
                this.handleNMContainerStatus(status, nodeId);
            }
        }
        final String message2 = "NodeManager from node " + host + "(cmPort: " + cmPort + " httpPort: " + httpPort + ") " + "registered with capability: " + capability + ", assigned nodeId " + nodeId;
        ResourceTrackerService.LOG.info(message2);
        response.setNodeAction(NodeAction.NORMAL);
        response.setRMIdentifier(ResourceManager.getClusterTimeStamp());
        response.setRMVersion(YarnVersionInfo.getVersion());
        return response;
    }
    
    @Override
    public NodeHeartbeatResponse nodeHeartbeat(final NodeHeartbeatRequest request) throws YarnException, IOException {
        final NodeStatus remoteNodeStatus = request.getNodeStatus();
        final NodeId nodeId = remoteNodeStatus.getNodeId();
        final RMNode rmNode = this.rmContext.getRMNodes().get(nodeId);
        if (rmNode == null) {
            final String message = "Node not found resyncing " + remoteNodeStatus.getNodeId();
            ResourceTrackerService.LOG.info(message);
            ResourceTrackerService.resync.setDiagnosticsMessage(message);
            return ResourceTrackerService.resync;
        }
        this.nmLivelinessMonitor.receivedPing(nodeId);
        if (!this.nodesListManager.isValidNode(rmNode.getHostName())) {
            final String message = "Disallowed NodeManager nodeId: " + nodeId + " hostname: " + rmNode.getNodeAddress();
            ResourceTrackerService.LOG.info(message);
            ResourceTrackerService.shutDown.setDiagnosticsMessage(message);
            this.rmContext.getDispatcher().getEventHandler().handle(new RMNodeEvent(nodeId, RMNodeEventType.DECOMMISSION));
            return ResourceTrackerService.shutDown;
        }
        final NodeHeartbeatResponse lastNodeHeartbeatResponse = rmNode.getLastNodeHeartBeatResponse();
        if (remoteNodeStatus.getResponseId() + 1 == lastNodeHeartbeatResponse.getResponseId()) {
            ResourceTrackerService.LOG.info("Received duplicate heartbeat from node " + rmNode.getNodeAddress() + " responseId=" + remoteNodeStatus.getResponseId());
            return lastNodeHeartbeatResponse;
        }
        if (remoteNodeStatus.getResponseId() + 1 < lastNodeHeartbeatResponse.getResponseId()) {
            final String message2 = "Too far behind rm response id:" + lastNodeHeartbeatResponse.getResponseId() + " nm response id:" + remoteNodeStatus.getResponseId();
            ResourceTrackerService.LOG.info(message2);
            ResourceTrackerService.resync.setDiagnosticsMessage(message2);
            this.rmContext.getDispatcher().getEventHandler().handle(new RMNodeEvent(nodeId, RMNodeEventType.REBOOTING));
            return ResourceTrackerService.resync;
        }
        final NodeHeartbeatResponse nodeHeartBeatResponse = YarnServerBuilderUtils.newNodeHeartbeatResponse(lastNodeHeartbeatResponse.getResponseId() + 1, NodeAction.NORMAL, null, null, null, null, this.nextHeartBeatInterval);
        rmNode.updateNodeHeartbeatResponseForCleanup(nodeHeartBeatResponse);
        this.populateKeys(request, nodeHeartBeatResponse);
        final ConcurrentMap<ApplicationId, ByteBuffer> systemCredentials = this.rmContext.getSystemCredentialsForApps();
        if (!systemCredentials.isEmpty()) {
            nodeHeartBeatResponse.setSystemCredentialsForApps(systemCredentials);
        }
        this.rmContext.getDispatcher().getEventHandler().handle(new RMNodeStatusEvent(nodeId, remoteNodeStatus.getNodeHealthStatus(), remoteNodeStatus.getContainersStatuses(), remoteNodeStatus.getKeepAliveApplications(), nodeHeartBeatResponse));
        return nodeHeartBeatResponse;
    }
    
    private void populateKeys(final NodeHeartbeatRequest request, final NodeHeartbeatResponse nodeHeartBeatResponse) {
        MasterKey nextMasterKeyForNode = this.containerTokenSecretManager.getNextKey();
        if (nextMasterKeyForNode != null && request.getLastKnownContainerTokenMasterKey().getKeyId() != nextMasterKeyForNode.getKeyId()) {
            nodeHeartBeatResponse.setContainerTokenMasterKey(nextMasterKeyForNode);
        }
        nextMasterKeyForNode = this.nmTokenSecretManager.getNextKey();
        if (nextMasterKeyForNode != null && request.getLastKnownNMTokenMasterKey().getKeyId() != nextMasterKeyForNode.getKeyId()) {
            nodeHeartBeatResponse.setNMTokenMasterKey(nextMasterKeyForNode);
        }
    }
    
    public static Node resolve(final String hostName) {
        return RackResolver.resolve(hostName);
    }
    
    void refreshServiceAcls(final Configuration configuration, final PolicyProvider policyProvider) {
        this.server.refreshServiceAclWithLoadedConfiguration(configuration, policyProvider);
    }
    
    @VisibleForTesting
    public Server getServer() {
        return this.server;
    }
    
    static {
        LOG = LogFactory.getLog(ResourceTrackerService.class);
        recordFactory = RecordFactoryProvider.getRecordFactory(null);
        resync = ResourceTrackerService.recordFactory.newRecordInstance(NodeHeartbeatResponse.class);
        shutDown = ResourceTrackerService.recordFactory.newRecordInstance(NodeHeartbeatResponse.class);
        ResourceTrackerService.resync.setNodeAction(NodeAction.RESYNC);
        ResourceTrackerService.shutDown.setNodeAction(NodeAction.SHUTDOWN);
    }
}
