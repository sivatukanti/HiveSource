// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import com.google.common.collect.Sets;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.InvalidResourceRequestException;
import org.apache.hadoop.yarn.util.resource.Resources;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class SchedulerUtils
{
    private static final RecordFactory recordFactory;
    public static final String RELEASED_CONTAINER = "Container released by application";
    public static final String LOST_CONTAINER = "Container released on a *lost* node";
    public static final String PREEMPTED_CONTAINER = "Container preempted by scheduler";
    public static final String COMPLETED_APPLICATION = "Container of a completed application";
    public static final String EXPIRED_CONTAINER = "Container expired since it was unused";
    public static final String UNRESERVED_CONTAINER = "Container reservation no longer required.";
    
    public static ContainerStatus createAbnormalContainerStatus(final ContainerId containerId, final String diagnostics) {
        return createAbnormalContainerStatus(containerId, -100, diagnostics);
    }
    
    public static ContainerStatus createPreemptedContainerStatus(final ContainerId containerId, final String diagnostics) {
        return createAbnormalContainerStatus(containerId, -102, diagnostics);
    }
    
    private static ContainerStatus createAbnormalContainerStatus(final ContainerId containerId, final int exitStatus, final String diagnostics) {
        final ContainerStatus containerStatus = SchedulerUtils.recordFactory.newRecordInstance(ContainerStatus.class);
        containerStatus.setContainerId(containerId);
        containerStatus.setDiagnostics(diagnostics);
        containerStatus.setExitStatus(exitStatus);
        containerStatus.setState(ContainerState.COMPLETE);
        return containerStatus;
    }
    
    public static void normalizeRequests(final List<ResourceRequest> asks, final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource minimumResource, final Resource maximumResource) {
        for (final ResourceRequest ask : asks) {
            normalizeRequest(ask, resourceCalculator, clusterResource, minimumResource, maximumResource, minimumResource);
        }
    }
    
    public static void normalizeRequest(final ResourceRequest ask, final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource minimumResource, final Resource maximumResource) {
        final Resource normalized = Resources.normalize(resourceCalculator, ask.getCapability(), minimumResource, maximumResource, minimumResource);
        ask.setCapability(normalized);
    }
    
    public static void normalizeRequests(final List<ResourceRequest> asks, final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource minimumResource, final Resource maximumResource, final Resource incrementResource) {
        for (final ResourceRequest ask : asks) {
            normalizeRequest(ask, resourceCalculator, clusterResource, minimumResource, maximumResource, incrementResource);
        }
    }
    
    public static void normalizeRequest(final ResourceRequest ask, final ResourceCalculator resourceCalculator, final Resource clusterResource, final Resource minimumResource, final Resource maximumResource, final Resource incrementResource) {
        final Resource normalized = Resources.normalize(resourceCalculator, ask.getCapability(), minimumResource, maximumResource, incrementResource);
        ask.setCapability(normalized);
    }
    
    public static void validateResourceRequest(final ResourceRequest resReq, final Resource maximumResource, final String queueName, final YarnScheduler scheduler) throws InvalidResourceRequestException {
        if (resReq.getCapability().getMemory() < 0 || resReq.getCapability().getMemory() > maximumResource.getMemory()) {
            throw new InvalidResourceRequestException("Invalid resource request, requested memory < 0, or requested memory > max configured, requestedMemory=" + resReq.getCapability().getMemory() + ", maxMemory=" + maximumResource.getMemory());
        }
        if (resReq.getCapability().getVirtualCores() < 0 || resReq.getCapability().getVirtualCores() > maximumResource.getVirtualCores()) {
            throw new InvalidResourceRequestException("Invalid resource request, requested virtual cores < 0, or requested virtual cores > max configured, requestedVirtualCores=" + resReq.getCapability().getVirtualCores() + ", maxVirtualCores=" + maximumResource.getVirtualCores());
        }
        QueueInfo queueInfo = null;
        try {
            queueInfo = scheduler.getQueueInfo(queueName, false, false);
        }
        catch (IOException ex) {}
        String labelExp = resReq.getNodeLabelExpression();
        if (labelExp == null && queueInfo != null) {
            labelExp = queueInfo.getDefaultNodeLabelExpression();
            resReq.setNodeLabelExpression(labelExp);
        }
        if (labelExp != null && !labelExp.trim().isEmpty() && queueInfo != null && !checkQueueLabelExpression(queueInfo.getAccessibleNodeLabels(), labelExp)) {
            throw new InvalidResourceRequestException("Invalid resource request, queue=" + queueInfo.getQueueName() + " doesn't have permission to access all labels " + "in resource request. labelExpression of resource request=" + labelExp + ". Queue labels=" + ((queueInfo.getAccessibleNodeLabels() == null) ? "" : StringUtils.join(queueInfo.getAccessibleNodeLabels().iterator(), ',')));
        }
    }
    
    public static boolean checkQueueAccessToNode(final Set<String> queueLabels, final Set<String> nodeLabels) {
        return (queueLabels != null && queueLabels.contains("*")) || (nodeLabels == null || nodeLabels.isEmpty()) || (queueLabels != null && Sets.intersection(queueLabels, nodeLabels).size() > 0);
    }
    
    public static void checkIfLabelInClusterNodeLabels(final RMNodeLabelsManager mgr, final Set<String> labels) throws IOException {
        if (mgr != null) {
            if (labels != null) {
                for (final String label : labels) {
                    if (!label.equals("*") && !mgr.containsNodeLabel(label)) {
                        throw new IOException("NodeLabelManager doesn't include label = " + label + ", please check.");
                    }
                }
            }
            return;
        }
        if (labels != null && !labels.isEmpty()) {
            throw new IOException("NodeLabelManager is null, please check");
        }
    }
    
    public static boolean checkNodeLabelExpression(final Set<String> nodeLabels, final String labelExpression) {
        if ((labelExpression == null || labelExpression.trim().isEmpty()) && !nodeLabels.isEmpty()) {
            return false;
        }
        if (labelExpression != null) {
            for (final String str : labelExpression.split("&&")) {
                if (!str.trim().isEmpty() && (nodeLabels == null || !nodeLabels.contains(str.trim()))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean checkQueueLabelExpression(final Set<String> queueLabels, final String labelExpression) {
        if (queueLabels != null && queueLabels.contains("*")) {
            return true;
        }
        if (labelExpression == null) {
            return true;
        }
        for (final String str : labelExpression.split("&&")) {
            if (!str.trim().isEmpty() && (queueLabels == null || !queueLabels.contains(str.trim()))) {
                return false;
            }
        }
        return true;
    }
    
    static {
        recordFactory = RecordFactoryProvider.getRecordFactory(null);
    }
}
