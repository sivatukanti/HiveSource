// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmcontainer;

import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeCleanContainerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerAllocatedEvent;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppRunningOnNodeEvent;
import org.apache.hadoop.yarn.state.MultipleArcTransition;
import java.util.Set;
import java.util.EnumSet;
import org.apache.hadoop.yarn.state.SingleArcTransition;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.state.InvalidStateTransitonException;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.yarn.state.StateMachine;
import org.apache.hadoop.yarn.state.StateMachineFactory;
import org.apache.commons.logging.Log;

public class RMContainerImpl implements RMContainer
{
    private static final Log LOG;
    private static final StateMachineFactory<RMContainerImpl, RMContainerState, RMContainerEventType, RMContainerEvent> stateMachineFactory;
    private final StateMachine<RMContainerState, RMContainerEventType, RMContainerEvent> stateMachine;
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;
    private final ContainerId containerId;
    private final ApplicationAttemptId appAttemptId;
    private final NodeId nodeId;
    private final Container container;
    private final RMContext rmContext;
    private final EventHandler eventHandler;
    private final ContainerAllocationExpirer containerAllocationExpirer;
    private final String user;
    private Resource reservedResource;
    private NodeId reservedNode;
    private Priority reservedPriority;
    private long creationTime;
    private long finishTime;
    private ContainerStatus finishedStatus;
    private boolean isAMContainer;
    private List<ResourceRequest> resourceRequests;
    
    public RMContainerImpl(final Container container, final ApplicationAttemptId appAttemptId, final NodeId nodeId, final String user, final RMContext rmContext) {
        this(container, appAttemptId, nodeId, user, rmContext, System.currentTimeMillis());
    }
    
    public RMContainerImpl(final Container container, final ApplicationAttemptId appAttemptId, final NodeId nodeId, final String user, final RMContext rmContext, final long creationTime) {
        this.stateMachine = RMContainerImpl.stateMachineFactory.make(this);
        this.containerId = container.getId();
        this.nodeId = nodeId;
        this.container = container;
        this.appAttemptId = appAttemptId;
        this.user = user;
        this.creationTime = creationTime;
        this.rmContext = rmContext;
        this.eventHandler = rmContext.getDispatcher().getEventHandler();
        this.containerAllocationExpirer = rmContext.getContainerAllocationExpirer();
        this.isAMContainer = false;
        this.resourceRequests = null;
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
        rmContext.getRMApplicationHistoryWriter().containerStarted(this);
        rmContext.getSystemMetricsPublisher().containerCreated(this, this.creationTime);
    }
    
    @Override
    public ContainerId getContainerId() {
        return this.containerId;
    }
    
    @Override
    public ApplicationAttemptId getApplicationAttemptId() {
        return this.appAttemptId;
    }
    
    @Override
    public Container getContainer() {
        return this.container;
    }
    
    @Override
    public RMContainerState getState() {
        this.readLock.lock();
        try {
            return this.stateMachine.getCurrentState();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Resource getReservedResource() {
        return this.reservedResource;
    }
    
    @Override
    public NodeId getReservedNode() {
        return this.reservedNode;
    }
    
    @Override
    public Priority getReservedPriority() {
        return this.reservedPriority;
    }
    
    @Override
    public Resource getAllocatedResource() {
        return this.container.getResource();
    }
    
    @Override
    public NodeId getAllocatedNode() {
        return this.container.getNodeId();
    }
    
    @Override
    public Priority getAllocatedPriority() {
        return this.container.getPriority();
    }
    
    @Override
    public long getCreationTime() {
        return this.creationTime;
    }
    
    @Override
    public long getFinishTime() {
        try {
            this.readLock.lock();
            return this.finishTime;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getDiagnosticsInfo() {
        try {
            this.readLock.lock();
            if (this.getFinishedStatus() != null) {
                return this.getFinishedStatus().getDiagnostics();
            }
            return null;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public String getLogURL() {
        try {
            this.readLock.lock();
            return WebAppUtils.getRunningLogURL("//" + this.container.getNodeHttpAddress(), ConverterUtils.toString(this.containerId), this.user);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public int getContainerExitStatus() {
        try {
            this.readLock.lock();
            if (this.getFinishedStatus() != null) {
                return this.getFinishedStatus().getExitStatus();
            }
            return 0;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public ContainerState getContainerState() {
        try {
            this.readLock.lock();
            if (this.getFinishedStatus() != null) {
                return this.getFinishedStatus().getState();
            }
            return ContainerState.RUNNING;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public List<ResourceRequest> getResourceRequests() {
        try {
            this.readLock.lock();
            return this.resourceRequests;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public void setResourceRequests(final List<ResourceRequest> requests) {
        try {
            this.writeLock.lock();
            this.resourceRequests = requests;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public String toString() {
        return this.containerId.toString();
    }
    
    @Override
    public boolean isAMContainer() {
        try {
            this.readLock.lock();
            return this.isAMContainer;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public void setAMContainer(final boolean isAMContainer) {
        try {
            this.writeLock.lock();
            this.isAMContainer = isAMContainer;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void handle(final RMContainerEvent event) {
        RMContainerImpl.LOG.debug("Processing " + event.getContainerId() + " of type " + ((AbstractEvent<Object>)event).getType());
        try {
            this.writeLock.lock();
            final RMContainerState oldState = this.getState();
            try {
                this.stateMachine.doTransition(event.getType(), event);
            }
            catch (InvalidStateTransitonException e) {
                RMContainerImpl.LOG.error("Can't handle this event at current state", e);
                RMContainerImpl.LOG.error("Invalid event " + ((AbstractEvent<Object>)event).getType() + " on container " + this.containerId);
            }
            if (oldState != this.getState()) {
                RMContainerImpl.LOG.info(event.getContainerId() + " Container Transitioned from " + oldState + " to " + this.getState());
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public ContainerStatus getFinishedStatus() {
        return this.finishedStatus;
    }
    
    @Override
    public ContainerReport createContainerReport() {
        this.readLock.lock();
        ContainerReport containerReport = null;
        try {
            containerReport = ContainerReport.newInstance(this.getContainerId(), this.getAllocatedResource(), this.getAllocatedNode(), this.getAllocatedPriority(), this.getCreationTime(), this.getFinishTime(), this.getDiagnosticsInfo(), this.getLogURL(), this.getContainerExitStatus(), this.getContainerState());
        }
        finally {
            this.readLock.unlock();
        }
        return containerReport;
    }
    
    static {
        LOG = LogFactory.getLog(RMContainerImpl.class);
        stateMachineFactory = new StateMachineFactory<RMContainerImpl, RMContainerState, RMContainerEventType, RMContainerEvent>(RMContainerState.NEW).addTransition(RMContainerState.NEW, RMContainerState.ALLOCATED, RMContainerEventType.START, new ContainerStartedTransition()).addTransition(RMContainerState.NEW, RMContainerState.KILLED, RMContainerEventType.KILL).addTransition(RMContainerState.NEW, RMContainerState.RESERVED, RMContainerEventType.RESERVED, new ContainerReservedTransition()).addTransition(RMContainerState.NEW, EnumSet.of(RMContainerState.RUNNING, RMContainerState.COMPLETED), RMContainerEventType.RECOVER, new ContainerRecoveredTransition()).addTransition(RMContainerState.RESERVED, RMContainerState.RESERVED, RMContainerEventType.RESERVED, new ContainerReservedTransition()).addTransition(RMContainerState.RESERVED, RMContainerState.ALLOCATED, RMContainerEventType.START, new ContainerStartedTransition()).addTransition(RMContainerState.RESERVED, RMContainerState.KILLED, RMContainerEventType.KILL).addTransition(RMContainerState.RESERVED, RMContainerState.RELEASED, RMContainerEventType.RELEASED).addTransition(RMContainerState.ALLOCATED, RMContainerState.ACQUIRED, RMContainerEventType.ACQUIRED, new AcquiredTransition()).addTransition(RMContainerState.ALLOCATED, RMContainerState.EXPIRED, RMContainerEventType.EXPIRE, new FinishedTransition()).addTransition(RMContainerState.ALLOCATED, RMContainerState.KILLED, RMContainerEventType.KILL, new FinishedTransition()).addTransition(RMContainerState.ACQUIRED, RMContainerState.RUNNING, RMContainerEventType.LAUNCHED, new LaunchedTransition()).addTransition(RMContainerState.ACQUIRED, RMContainerState.COMPLETED, RMContainerEventType.FINISHED, new ContainerFinishedAtAcquiredState()).addTransition(RMContainerState.ACQUIRED, RMContainerState.RELEASED, RMContainerEventType.RELEASED, new KillTransition()).addTransition(RMContainerState.ACQUIRED, RMContainerState.EXPIRED, RMContainerEventType.EXPIRE, new KillTransition()).addTransition(RMContainerState.ACQUIRED, RMContainerState.KILLED, RMContainerEventType.KILL, new KillTransition()).addTransition(RMContainerState.RUNNING, RMContainerState.COMPLETED, RMContainerEventType.FINISHED, new FinishedTransition()).addTransition(RMContainerState.RUNNING, RMContainerState.KILLED, RMContainerEventType.KILL, new KillTransition()).addTransition(RMContainerState.RUNNING, RMContainerState.RELEASED, RMContainerEventType.RELEASED, new KillTransition()).addTransition(RMContainerState.RUNNING, RMContainerState.RUNNING, RMContainerEventType.EXPIRE).addTransition(RMContainerState.COMPLETED, RMContainerState.COMPLETED, EnumSet.of(RMContainerEventType.EXPIRE, RMContainerEventType.RELEASED, RMContainerEventType.KILL)).addTransition(RMContainerState.EXPIRED, RMContainerState.EXPIRED, EnumSet.of(RMContainerEventType.RELEASED, RMContainerEventType.KILL)).addTransition(RMContainerState.RELEASED, RMContainerState.RELEASED, EnumSet.of(RMContainerEventType.EXPIRE, RMContainerEventType.RELEASED, RMContainerEventType.KILL, RMContainerEventType.FINISHED)).addTransition(RMContainerState.KILLED, RMContainerState.KILLED, EnumSet.of(RMContainerEventType.EXPIRE, RMContainerEventType.RELEASED, RMContainerEventType.KILL, RMContainerEventType.FINISHED)).installTopology();
    }
    
    private static class BaseTransition implements SingleArcTransition<RMContainerImpl, RMContainerEvent>
    {
        @Override
        public void transition(final RMContainerImpl cont, final RMContainerEvent event) {
        }
    }
    
    private static final class ContainerRecoveredTransition implements MultipleArcTransition<RMContainerImpl, RMContainerEvent, RMContainerState>
    {
        @Override
        public RMContainerState transition(final RMContainerImpl container, final RMContainerEvent event) {
            final NMContainerStatus report = ((RMContainerRecoverEvent)event).getContainerReport();
            if (report.getContainerState().equals(ContainerState.COMPLETE)) {
                final ContainerStatus status = ContainerStatus.newInstance(report.getContainerId(), report.getContainerState(), report.getDiagnostics(), report.getContainerExitStatus());
                new FinishedTransition().transition(container, new RMContainerFinishedEvent(container.containerId, status, RMContainerEventType.FINISHED));
                return RMContainerState.COMPLETED;
            }
            if (report.getContainerState().equals(ContainerState.RUNNING)) {
                container.eventHandler.handle(new RMAppRunningOnNodeEvent(container.getApplicationAttemptId().getApplicationId(), container.nodeId));
                return RMContainerState.RUNNING;
            }
            RMContainerImpl.LOG.warn("RMContainer received unexpected recover event with container state " + report.getContainerState() + " while recovering.");
            return RMContainerState.RUNNING;
        }
    }
    
    private static final class ContainerReservedTransition extends BaseTransition
    {
        @Override
        public void transition(final RMContainerImpl container, final RMContainerEvent event) {
            final RMContainerReservedEvent e = (RMContainerReservedEvent)event;
            container.reservedResource = e.getReservedResource();
            container.reservedNode = e.getReservedNode();
            container.reservedPriority = e.getReservedPriority();
        }
    }
    
    private static final class ContainerStartedTransition extends BaseTransition
    {
        @Override
        public void transition(final RMContainerImpl container, final RMContainerEvent event) {
            container.eventHandler.handle(new RMAppAttemptContainerAllocatedEvent(container.appAttemptId));
        }
    }
    
    private static final class AcquiredTransition extends BaseTransition
    {
        @Override
        public void transition(final RMContainerImpl container, final RMContainerEvent event) {
            container.setResourceRequests(null);
            container.containerAllocationExpirer.register(container.getContainerId());
            container.eventHandler.handle(new RMAppRunningOnNodeEvent(container.getApplicationAttemptId().getApplicationId(), container.nodeId));
        }
    }
    
    private static final class LaunchedTransition extends BaseTransition
    {
        @Override
        public void transition(final RMContainerImpl container, final RMContainerEvent event) {
            container.containerAllocationExpirer.unregister(container.getContainerId());
        }
    }
    
    private static class FinishedTransition extends BaseTransition
    {
        @Override
        public void transition(final RMContainerImpl container, final RMContainerEvent event) {
            final RMContainerFinishedEvent finishedEvent = (RMContainerFinishedEvent)event;
            container.finishTime = System.currentTimeMillis();
            container.finishedStatus = finishedEvent.getRemoteContainerStatus();
            updateAttemptMetrics(container);
            container.eventHandler.handle(new RMAppAttemptContainerFinishedEvent(container.appAttemptId, finishedEvent.getRemoteContainerStatus(), container.getAllocatedNode()));
            container.rmContext.getRMApplicationHistoryWriter().containerFinished(container);
            container.rmContext.getSystemMetricsPublisher().containerFinished(container, container.finishTime);
        }
        
        private static void updateAttemptMetrics(final RMContainerImpl container) {
            final Resource resource = container.getContainer().getResource();
            final RMAppAttempt rmAttempt = container.rmContext.getRMApps().get(container.getApplicationAttemptId().getApplicationId()).getCurrentAppAttempt();
            if (-102 == container.finishedStatus.getExitStatus()) {
                rmAttempt.getRMAppAttemptMetrics().updatePreemptionInfo(resource, container);
            }
            if (rmAttempt != null) {
                final long usedMillis = container.finishTime - container.creationTime;
                final long memorySeconds = resource.getMemory() * usedMillis / 1000L;
                final long vcoreSeconds = resource.getVirtualCores() * usedMillis / 1000L;
                rmAttempt.getRMAppAttemptMetrics().updateAggregateAppResourceUsage(memorySeconds, vcoreSeconds);
            }
        }
    }
    
    private static final class ContainerFinishedAtAcquiredState extends FinishedTransition
    {
        @Override
        public void transition(final RMContainerImpl container, final RMContainerEvent event) {
            container.containerAllocationExpirer.unregister(container.getContainerId());
            super.transition(container, event);
        }
    }
    
    private static final class KillTransition extends FinishedTransition
    {
        @Override
        public void transition(final RMContainerImpl container, final RMContainerEvent event) {
            container.containerAllocationExpirer.unregister(container.getContainerId());
            container.eventHandler.handle(new RMNodeCleanContainerEvent(container.nodeId, container.containerId));
            super.transition(container, event);
        }
    }
}
