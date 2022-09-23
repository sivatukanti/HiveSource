// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.utils;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.PreemptionMessage;
import org.apache.hadoop.yarn.api.records.AMCommand;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.io.Text;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceAudience;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.net.NetUtils;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.ContainerState;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.io.IOException;
import org.apache.hadoop.yarn.security.ContainerTokenIdentifier;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.util.ConverterUtils;
import java.net.URI;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.factories.RecordFactory;

public class BuilderUtils
{
    private static final RecordFactory recordFactory;
    
    public static LocalResource newLocalResource(final URL url, final LocalResourceType type, final LocalResourceVisibility visibility, final long size, final long timestamp) {
        final LocalResource resource = BuilderUtils.recordFactory.newRecordInstance(LocalResource.class);
        resource.setResource(url);
        resource.setType(type);
        resource.setVisibility(visibility);
        resource.setSize(size);
        resource.setTimestamp(timestamp);
        return resource;
    }
    
    public static LocalResource newLocalResource(final URI uri, final LocalResourceType type, final LocalResourceVisibility visibility, final long size, final long timestamp) {
        return newLocalResource(ConverterUtils.getYarnUrlFromURI(uri), type, visibility, size, timestamp);
    }
    
    public static ApplicationId newApplicationId(final RecordFactory recordFactory, final long clustertimestamp, final CharSequence id) {
        return ApplicationId.newInstance(clustertimestamp, Integer.valueOf(id.toString()));
    }
    
    public static ApplicationId newApplicationId(final RecordFactory recordFactory, final long clusterTimeStamp, final int id) {
        return ApplicationId.newInstance(clusterTimeStamp, id);
    }
    
    public static ApplicationId newApplicationId(final long clusterTimeStamp, final int id) {
        return ApplicationId.newInstance(clusterTimeStamp, id);
    }
    
    public static ApplicationAttemptId newApplicationAttemptId(final ApplicationId appId, final int attemptId) {
        return ApplicationAttemptId.newInstance(appId, attemptId);
    }
    
    public static ApplicationId convert(final long clustertimestamp, final CharSequence id) {
        return ApplicationId.newInstance(clustertimestamp, Integer.valueOf(id.toString()));
    }
    
    public static ContainerId newContainerId(final ApplicationAttemptId appAttemptId, final long containerId) {
        return ContainerId.newContainerId(appAttemptId, containerId);
    }
    
    public static ContainerId newContainerId(final int appId, final int appAttemptId, final long timestamp, final long id) {
        final ApplicationId applicationId = newApplicationId(timestamp, appId);
        final ApplicationAttemptId applicationAttemptId = newApplicationAttemptId(applicationId, appAttemptId);
        final ContainerId cId = newContainerId(applicationAttemptId, id);
        return cId;
    }
    
    public static Token newContainerToken(final ContainerId cId, final String host, final int port, final String user, final Resource r, final long expiryTime, final int masterKeyId, final byte[] password, final long rmIdentifier) throws IOException {
        final ContainerTokenIdentifier identifier = new ContainerTokenIdentifier(cId, host + ":" + port, user, r, expiryTime, masterKeyId, rmIdentifier, Priority.newInstance(0), 0L);
        return newContainerToken(newNodeId(host, port), password, identifier);
    }
    
    public static ContainerId newContainerId(final RecordFactory recordFactory, final ApplicationId appId, final ApplicationAttemptId appAttemptId, final int containerId) {
        return ContainerId.newContainerId(appAttemptId, containerId);
    }
    
    public static NodeId newNodeId(final String host, final int port) {
        return NodeId.newInstance(host, port);
    }
    
    public static NodeReport newNodeReport(final NodeId nodeId, final NodeState nodeState, final String httpAddress, final String rackName, final Resource used, final Resource capability, final int numContainers, final String healthReport, final long lastHealthReportTime) {
        return newNodeReport(nodeId, nodeState, httpAddress, rackName, used, capability, numContainers, healthReport, lastHealthReportTime, null);
    }
    
    public static NodeReport newNodeReport(final NodeId nodeId, final NodeState nodeState, final String httpAddress, final String rackName, final Resource used, final Resource capability, final int numContainers, final String healthReport, final long lastHealthReportTime, final Set<String> nodeLabels) {
        final NodeReport nodeReport = BuilderUtils.recordFactory.newRecordInstance(NodeReport.class);
        nodeReport.setNodeId(nodeId);
        nodeReport.setNodeState(nodeState);
        nodeReport.setHttpAddress(httpAddress);
        nodeReport.setRackName(rackName);
        nodeReport.setUsed(used);
        nodeReport.setCapability(capability);
        nodeReport.setNumContainers(numContainers);
        nodeReport.setHealthReport(healthReport);
        nodeReport.setLastHealthReportTime(lastHealthReportTime);
        nodeReport.setNodeLabels(nodeLabels);
        return nodeReport;
    }
    
    public static ContainerStatus newContainerStatus(final ContainerId containerId, final ContainerState containerState, final String diagnostics, final int exitStatus) {
        final ContainerStatus containerStatus = BuilderUtils.recordFactory.newRecordInstance(ContainerStatus.class);
        containerStatus.setState(containerState);
        containerStatus.setContainerId(containerId);
        containerStatus.setDiagnostics(diagnostics);
        containerStatus.setExitStatus(exitStatus);
        return containerStatus;
    }
    
    public static Container newContainer(final ContainerId containerId, final NodeId nodeId, final String nodeHttpAddress, final Resource resource, final Priority priority, final Token containerToken) {
        final Container container = BuilderUtils.recordFactory.newRecordInstance(Container.class);
        container.setId(containerId);
        container.setNodeId(nodeId);
        container.setNodeHttpAddress(nodeHttpAddress);
        container.setResource(resource);
        container.setPriority(priority);
        container.setContainerToken(containerToken);
        return container;
    }
    
    public static <T extends Token> T newToken(final Class<T> tokenClass, final byte[] identifier, final String kind, final byte[] password, final String service) {
        final T token = BuilderUtils.recordFactory.newRecordInstance(tokenClass);
        token.setIdentifier(ByteBuffer.wrap(identifier));
        token.setKind(kind);
        token.setPassword(ByteBuffer.wrap(password));
        token.setService(service);
        return token;
    }
    
    public static Token newDelegationToken(final byte[] identifier, final String kind, final byte[] password, final String service) {
        return newToken(Token.class, identifier, kind, password, service);
    }
    
    public static Token newClientToAMToken(final byte[] identifier, final String kind, final byte[] password, final String service) {
        return newToken(Token.class, identifier, kind, password, service);
    }
    
    public static Token newAMRMToken(final byte[] identifier, final String kind, final byte[] password, final String service) {
        return newToken(Token.class, identifier, kind, password, service);
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public static Token newContainerToken(final NodeId nodeId, final byte[] password, final ContainerTokenIdentifier tokenIdentifier) {
        final InetSocketAddress addr = NetUtils.createSocketAddrForHost(nodeId.getHost(), nodeId.getPort());
        final Token containerToken = newToken(Token.class, tokenIdentifier.getBytes(), ContainerTokenIdentifier.KIND.toString(), password, SecurityUtil.buildTokenService(addr).toString());
        return containerToken;
    }
    
    public static ContainerTokenIdentifier newContainerTokenIdentifier(final Token containerToken) throws IOException {
        final org.apache.hadoop.security.token.Token<ContainerTokenIdentifier> token = new org.apache.hadoop.security.token.Token<ContainerTokenIdentifier>(containerToken.getIdentifier().array(), containerToken.getPassword().array(), new Text(containerToken.getKind()), new Text(containerToken.getService()));
        return token.decodeIdentifier();
    }
    
    public static ContainerLaunchContext newContainerLaunchContext(final Map<String, LocalResource> localResources, final Map<String, String> environment, final List<String> commands, final Map<String, ByteBuffer> serviceData, final ByteBuffer tokens, final Map<ApplicationAccessType, String> acls) {
        final ContainerLaunchContext container = BuilderUtils.recordFactory.newRecordInstance(ContainerLaunchContext.class);
        container.setLocalResources(localResources);
        container.setEnvironment(environment);
        container.setCommands(commands);
        container.setServiceData(serviceData);
        container.setTokens(tokens);
        container.setApplicationACLs(acls);
        return container;
    }
    
    public static Priority newPriority(final int p) {
        final Priority priority = BuilderUtils.recordFactory.newRecordInstance(Priority.class);
        priority.setPriority(p);
        return priority;
    }
    
    public static ResourceRequest newResourceRequest(final Priority priority, final String hostName, final Resource capability, final int numContainers) {
        final ResourceRequest request = BuilderUtils.recordFactory.newRecordInstance(ResourceRequest.class);
        request.setPriority(priority);
        request.setResourceName(hostName);
        request.setCapability(capability);
        request.setNumContainers(numContainers);
        return request;
    }
    
    public static ResourceRequest newResourceRequest(final ResourceRequest r) {
        final ResourceRequest request = BuilderUtils.recordFactory.newRecordInstance(ResourceRequest.class);
        request.setPriority(r.getPriority());
        request.setResourceName(r.getResourceName());
        request.setCapability(r.getCapability());
        request.setNumContainers(r.getNumContainers());
        return request;
    }
    
    public static ApplicationReport newApplicationReport(final ApplicationId applicationId, final ApplicationAttemptId applicationAttemptId, final String user, final String queue, final String name, final String host, final int rpcPort, final Token clientToAMToken, final YarnApplicationState state, final String diagnostics, final String url, final long startTime, final long finishTime, final FinalApplicationStatus finalStatus, final ApplicationResourceUsageReport appResources, final String origTrackingUrl, final float progress, final String appType, final Token amRmToken, final Set<String> tags) {
        final ApplicationReport report = BuilderUtils.recordFactory.newRecordInstance(ApplicationReport.class);
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
        report.setApplicationType(appType);
        report.setAMRMToken(amRmToken);
        report.setApplicationTags(tags);
        return report;
    }
    
    public static ApplicationSubmissionContext newApplicationSubmissionContext(final ApplicationId applicationId, final String applicationName, final String queue, final Priority priority, final ContainerLaunchContext amContainer, final boolean isUnmanagedAM, final boolean cancelTokensWhenComplete, final int maxAppAttempts, final Resource resource, final String applicationType) {
        final ApplicationSubmissionContext context = BuilderUtils.recordFactory.newRecordInstance(ApplicationSubmissionContext.class);
        context.setApplicationId(applicationId);
        context.setApplicationName(applicationName);
        context.setQueue(queue);
        context.setPriority(priority);
        context.setAMContainerSpec(amContainer);
        context.setUnmanagedAM(isUnmanagedAM);
        context.setCancelTokensWhenComplete(cancelTokensWhenComplete);
        context.setMaxAppAttempts(maxAppAttempts);
        context.setResource(resource);
        context.setApplicationType(applicationType);
        return context;
    }
    
    public static ApplicationSubmissionContext newApplicationSubmissionContext(final ApplicationId applicationId, final String applicationName, final String queue, final Priority priority, final ContainerLaunchContext amContainer, final boolean isUnmanagedAM, final boolean cancelTokensWhenComplete, final int maxAppAttempts, final Resource resource) {
        return newApplicationSubmissionContext(applicationId, applicationName, queue, priority, amContainer, isUnmanagedAM, cancelTokensWhenComplete, maxAppAttempts, resource, null);
    }
    
    public static ApplicationResourceUsageReport newApplicationResourceUsageReport(final int numUsedContainers, final int numReservedContainers, final Resource usedResources, final Resource reservedResources, final Resource neededResources, final long memorySeconds, final long vcoreSeconds) {
        final ApplicationResourceUsageReport report = BuilderUtils.recordFactory.newRecordInstance(ApplicationResourceUsageReport.class);
        report.setNumUsedContainers(numUsedContainers);
        report.setNumReservedContainers(numReservedContainers);
        report.setUsedResources(usedResources);
        report.setReservedResources(reservedResources);
        report.setNeededResources(neededResources);
        report.setMemorySeconds(memorySeconds);
        report.setVcoreSeconds(vcoreSeconds);
        return report;
    }
    
    public static Resource newResource(final int memory, final int vCores) {
        final Resource resource = BuilderUtils.recordFactory.newRecordInstance(Resource.class);
        resource.setMemory(memory);
        resource.setVirtualCores(vCores);
        return resource;
    }
    
    public static URL newURL(final String scheme, final String host, final int port, final String file) {
        final URL url = BuilderUtils.recordFactory.newRecordInstance(URL.class);
        url.setScheme(scheme);
        url.setHost(host);
        url.setPort(port);
        url.setFile(file);
        return url;
    }
    
    public static AllocateResponse newAllocateResponse(final int responseId, final List<ContainerStatus> completedContainers, final List<Container> allocatedContainers, final List<NodeReport> updatedNodes, final Resource availResources, final AMCommand command, final int numClusterNodes, final PreemptionMessage preempt) {
        final AllocateResponse response = BuilderUtils.recordFactory.newRecordInstance(AllocateResponse.class);
        response.setNumClusterNodes(numClusterNodes);
        response.setResponseId(responseId);
        response.setCompletedContainersStatuses(completedContainers);
        response.setAllocatedContainers(allocatedContainers);
        response.setUpdatedNodes(updatedNodes);
        response.setAvailableResources(availResources);
        response.setAMCommand(command);
        response.setPreemptionMessage(preempt);
        return response;
    }
    
    static {
        recordFactory = RecordFactoryProvider.getRecordFactory(null);
    }
    
    public static class ApplicationIdComparator implements Comparator<ApplicationId>, Serializable
    {
        @Override
        public int compare(final ApplicationId a1, final ApplicationId a2) {
            return a1.compareTo(a2);
        }
    }
    
    public static class ContainerIdComparator implements Comparator<ContainerId>, Serializable
    {
        @Override
        public int compare(final ContainerId c1, final ContainerId c2) {
            return c1.compareTo(c2);
        }
    }
}
