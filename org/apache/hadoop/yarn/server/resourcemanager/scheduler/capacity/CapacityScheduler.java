// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.apache.hadoop.yarn.event.AbstractEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.event.Event;
import java.util.HashSet;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import java.util.EnumSet;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.QueueEntitlement;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerDynamicEditException;
import org.apache.hadoop.yarn.api.records.ReservationId;
import java.io.InputStream;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.ContainerExpiredSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeResourceUpdateSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ResourceOption;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.UpdatedContainerInfo;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Allocation;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerState;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Queue;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueNotFoundException;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppRejectedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import org.apache.hadoop.yarn.server.utils.Lock;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.base.Preconditions;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplication;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import java.util.concurrent.ConcurrentHashMap;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import java.util.Random;
import org.apache.hadoop.security.Groups;
import java.util.List;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import java.util.Comparator;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.PreemptableResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.AbstractYarnScheduler;

@InterfaceAudience.LimitedPrivate({ "yarn" })
@InterfaceStability.Evolving
public class CapacityScheduler extends AbstractYarnScheduler<FiCaSchedulerApp, FiCaSchedulerNode> implements PreemptableResourceScheduler, CapacitySchedulerContext, Configurable
{
    private static final Log LOG;
    private CSQueue root;
    protected final long THREAD_JOIN_TIMEOUT_MS = 1000L;
    static final Comparator<CSQueue> queueComparator;
    static final Comparator<FiCaSchedulerApp> applicationComparator;
    private CapacitySchedulerConfiguration conf;
    private Configuration yarnConf;
    private Map<String, CSQueue> queues;
    private AtomicInteger numNodeManagers;
    private ResourceCalculator calculator;
    private boolean usePortForNodeName;
    private boolean scheduleAsynchronously;
    private AsyncScheduleThread asyncSchedulerThread;
    private RMNodeLabelsManager labelManager;
    private long asyncScheduleInterval;
    private static final String ASYNC_SCHEDULER_INTERVAL = "yarn.scheduler.capacity.schedule-asynchronously.scheduling-interval-ms";
    private static final long DEFAULT_ASYNC_SCHEDULER_INTERVAL = 5L;
    private boolean overrideWithQueueMappings;
    private List<CapacitySchedulerConfiguration.QueueMapping> mappings;
    private Groups groups;
    private static final Random random;
    @InterfaceAudience.Private
    public static final String ROOT_QUEUE = "yarn.scheduler.capacity.root";
    private static final QueueHook noop;
    private static final String CURRENT_USER_MAPPING = "%user";
    private static final String PRIMARY_GROUP_MAPPING = "%primary_group";
    
    @Override
    public void setConf(final Configuration conf) {
        this.yarnConf = conf;
    }
    
    private void validateConf(final Configuration conf) {
        final int minMem = conf.getInt("yarn.scheduler.minimum-allocation-mb", 1024);
        final int maxMem = conf.getInt("yarn.scheduler.maximum-allocation-mb", 8192);
        if (minMem <= 0 || minMem > maxMem) {
            throw new YarnRuntimeException("Invalid resource scheduler memory allocation configuration, yarn.scheduler.minimum-allocation-mb=" + minMem + ", " + "yarn.scheduler.maximum-allocation-mb" + "=" + maxMem + ", min and max should be greater than 0" + ", max should be no smaller than min.");
        }
        final int minVcores = conf.getInt("yarn.scheduler.minimum-allocation-vcores", 1);
        final int maxVcores = conf.getInt("yarn.scheduler.maximum-allocation-vcores", 4);
        if (minVcores <= 0 || minVcores > maxVcores) {
            throw new YarnRuntimeException("Invalid resource scheduler vcores allocation configuration, yarn.scheduler.minimum-allocation-vcores=" + minVcores + ", " + "yarn.scheduler.maximum-allocation-vcores" + "=" + maxVcores + ", min and max should be greater than 0" + ", max should be no smaller than min.");
        }
    }
    
    @Override
    public Configuration getConf() {
        return this.yarnConf;
    }
    
    @VisibleForTesting
    public synchronized String getMappedQueueForTest(final String user) throws IOException {
        return this.getMappedQueue(user);
    }
    
    public CapacityScheduler() {
        super(CapacityScheduler.class.getName());
        this.queues = new ConcurrentHashMap<String, CSQueue>();
        this.numNodeManagers = new AtomicInteger(0);
        this.overrideWithQueueMappings = false;
        this.mappings = null;
    }
    
    @Override
    public QueueMetrics getRootQueueMetrics() {
        return this.root.getMetrics();
    }
    
    public CSQueue getRootQueue() {
        return this.root;
    }
    
    @Override
    public CapacitySchedulerConfiguration getConfiguration() {
        return this.conf;
    }
    
    @Override
    public RMContainerTokenSecretManager getContainerTokenSecretManager() {
        return this.rmContext.getContainerTokenSecretManager();
    }
    
    @Override
    public Comparator<FiCaSchedulerApp> getApplicationComparator() {
        return CapacityScheduler.applicationComparator;
    }
    
    @Override
    public ResourceCalculator getResourceCalculator() {
        return this.calculator;
    }
    
    @Override
    public Comparator<CSQueue> getQueueComparator() {
        return CapacityScheduler.queueComparator;
    }
    
    @Override
    public int getNumClusterNodes() {
        return this.numNodeManagers.get();
    }
    
    @Override
    public synchronized RMContext getRMContext() {
        return this.rmContext;
    }
    
    @Override
    public synchronized void setRMContext(final RMContext rmContext) {
        this.rmContext = rmContext;
    }
    
    private synchronized void initScheduler(final Configuration configuration) throws IOException {
        this.validateConf(this.conf = this.loadCapacitySchedulerConfiguration(configuration));
        this.minimumAllocation = this.conf.getMinimumAllocation();
        this.maximumAllocation = this.conf.getMaximumAllocation();
        this.calculator = this.conf.getResourceCalculator();
        this.usePortForNodeName = this.conf.getUsePortForNodeName();
        this.applications = new ConcurrentHashMap<ApplicationId, SchedulerApplication<T>>();
        this.labelManager = this.rmContext.getNodeLabelManager();
        this.initializeQueues(this.conf);
        this.scheduleAsynchronously = this.conf.getScheduleAynschronously();
        this.asyncScheduleInterval = this.conf.getLong("yarn.scheduler.capacity.schedule-asynchronously.scheduling-interval-ms", 5L);
        if (this.scheduleAsynchronously) {
            this.asyncSchedulerThread = new AsyncScheduleThread(this);
        }
        CapacityScheduler.LOG.info("Initialized CapacityScheduler with calculator=" + this.getResourceCalculator().getClass() + ", " + "minimumAllocation=<" + this.getMinimumResourceCapability() + ">, " + "maximumAllocation=<" + this.getMaximumResourceCapability() + ">, " + "asynchronousScheduling=" + this.scheduleAsynchronously + ", " + "asyncScheduleInterval=" + this.asyncScheduleInterval + "ms");
    }
    
    private synchronized void startSchedulerThreads() {
        if (this.scheduleAsynchronously) {
            Preconditions.checkNotNull(this.asyncSchedulerThread, (Object)"asyncSchedulerThread is null");
            this.asyncSchedulerThread.start();
        }
    }
    
    @Override
    public void serviceInit(final Configuration conf) throws Exception {
        final Configuration configuration = new Configuration(conf);
        this.initScheduler(configuration);
        super.serviceInit(conf);
    }
    
    public void serviceStart() throws Exception {
        this.startSchedulerThreads();
        super.serviceStart();
    }
    
    public void serviceStop() throws Exception {
        synchronized (this) {
            if (this.scheduleAsynchronously && this.asyncSchedulerThread != null) {
                this.asyncSchedulerThread.interrupt();
                this.asyncSchedulerThread.join(1000L);
            }
        }
        super.serviceStop();
    }
    
    @Override
    public synchronized void reinitialize(final Configuration conf, final RMContext rmContext) throws IOException {
        final Configuration configuration = new Configuration(conf);
        final CapacitySchedulerConfiguration oldConf = this.conf;
        this.validateConf(this.conf = this.loadCapacitySchedulerConfiguration(configuration));
        try {
            CapacityScheduler.LOG.info("Re-initializing queues...");
            this.reinitializeQueues(this.conf);
        }
        catch (Throwable t) {
            this.conf = oldConf;
            throw new IOException("Failed to re-init queues", t);
        }
    }
    
    long getAsyncScheduleInterval() {
        return this.asyncScheduleInterval;
    }
    
    static void schedule(final CapacityScheduler cs) {
        int current = 0;
        final Collection<FiCaSchedulerNode> nodes = cs.getAllNodes().values();
        final int start = CapacityScheduler.random.nextInt(nodes.size());
        for (final FiCaSchedulerNode node : nodes) {
            if (current++ >= start) {
                cs.allocateContainersToNode(node);
            }
        }
        for (final FiCaSchedulerNode node : nodes) {
            cs.allocateContainersToNode(node);
        }
        try {
            Thread.sleep(cs.getAsyncScheduleInterval());
        }
        catch (InterruptedException ex) {}
    }
    
    private void initializeQueueMappings() throws IOException {
        this.overrideWithQueueMappings = this.conf.getOverrideWithQueueMappings();
        CapacityScheduler.LOG.info("Initialized queue mappings, override: " + this.overrideWithQueueMappings);
        final List<CapacitySchedulerConfiguration.QueueMapping> newMappings = this.conf.getQueueMappings();
        for (final CapacitySchedulerConfiguration.QueueMapping mapping : newMappings) {
            if (!mapping.queue.equals("%user") && !mapping.queue.equals("%primary_group")) {
                final CSQueue queue = this.queues.get(mapping.queue);
                if (queue == null || !(queue instanceof LeafQueue)) {
                    throw new IOException("mapping contains invalid or non-leaf queue " + mapping.queue);
                }
                continue;
            }
        }
        this.mappings = newMappings;
        if (this.mappings.size() > 0) {
            this.groups = new Groups(this.conf);
        }
    }
    
    @Lock({ CapacityScheduler.class })
    private void initializeQueues(final CapacitySchedulerConfiguration conf) throws IOException {
        this.root = parseQueue(this, conf, null, "root", this.queues, this.queues, CapacityScheduler.noop);
        this.labelManager.reinitializeQueueLabels(this.getQueueToLabels());
        CapacityScheduler.LOG.info("Initialized root queue " + this.root);
        this.initializeQueueMappings();
    }
    
    @Lock({ CapacityScheduler.class })
    private void reinitializeQueues(final CapacitySchedulerConfiguration conf) throws IOException {
        final Map<String, CSQueue> newQueues = new HashMap<String, CSQueue>();
        final CSQueue newRoot = parseQueue(this, conf, null, "root", newQueues, this.queues, CapacityScheduler.noop);
        this.validateExistingQueues(this.queues, newQueues);
        this.addNewQueues(this.queues, newQueues);
        this.root.reinitialize(newRoot, this.clusterResource);
        this.initializeQueueMappings();
        this.root.updateClusterResource(this.clusterResource);
        this.labelManager.reinitializeQueueLabels(this.getQueueToLabels());
    }
    
    private Map<String, Set<String>> getQueueToLabels() {
        final Map<String, Set<String>> queueToLabels = new HashMap<String, Set<String>>();
        for (final CSQueue queue : this.queues.values()) {
            queueToLabels.put(queue.getQueueName(), queue.getAccessibleNodeLabels());
        }
        return queueToLabels;
    }
    
    @Lock({ CapacityScheduler.class })
    private void validateExistingQueues(final Map<String, CSQueue> queues, final Map<String, CSQueue> newQueues) throws IOException {
        for (final Map.Entry<String, CSQueue> e : queues.entrySet()) {
            if (!(e.getValue() instanceof ReservationQueue) && !newQueues.containsKey(e.getKey())) {
                throw new IOException(e.getKey() + " cannot be found during refresh!");
            }
        }
    }
    
    @Lock({ CapacityScheduler.class })
    private void addNewQueues(final Map<String, CSQueue> queues, final Map<String, CSQueue> newQueues) {
        for (final Map.Entry<String, CSQueue> e : newQueues.entrySet()) {
            final String queueName = e.getKey();
            final CSQueue queue = e.getValue();
            if (!queues.containsKey(queueName)) {
                queues.put(queueName, queue);
            }
        }
    }
    
    @Lock({ CapacityScheduler.class })
    static CSQueue parseQueue(final CapacitySchedulerContext csContext, final CapacitySchedulerConfiguration conf, final CSQueue parent, final String queueName, final Map<String, CSQueue> queues, final Map<String, CSQueue> oldQueues, final QueueHook hook) throws IOException {
        final String fullQueueName = (parent == null) ? queueName : (parent.getQueuePath() + "." + queueName);
        final String[] childQueueNames = conf.getQueues(fullQueueName);
        final boolean isReservableQueue = conf.isReservable(fullQueueName);
        CSQueue queue;
        if (childQueueNames == null || childQueueNames.length == 0) {
            if (null == parent) {
                throw new IllegalStateException("Queue configuration missing child queue names for " + queueName);
            }
            if (isReservableQueue) {
                queue = new PlanQueue(csContext, queueName, parent, oldQueues.get(queueName));
            }
            else {
                queue = new LeafQueue(csContext, queueName, parent, oldQueues.get(queueName));
                queue = hook.hook(queue);
            }
        }
        else {
            if (isReservableQueue) {
                throw new IllegalStateException("Only Leaf Queues can be reservable for " + queueName);
            }
            final ParentQueue parentQueue = new ParentQueue(csContext, queueName, parent, oldQueues.get(queueName));
            queue = hook.hook(parentQueue);
            final List<CSQueue> childQueues = new ArrayList<CSQueue>();
            for (final String childQueueName : childQueueNames) {
                final CSQueue childQueue = parseQueue(csContext, conf, queue, childQueueName, queues, oldQueues, hook);
                childQueues.add(childQueue);
            }
            parentQueue.setChildQueues(childQueues);
        }
        if (queue instanceof LeafQueue && queues.containsKey(queueName) && queues.get(queueName) instanceof LeafQueue) {
            throw new IOException("Two leaf queues were named " + queueName + ". Leaf queue names must be distinct");
        }
        queues.put(queueName, queue);
        CapacityScheduler.LOG.info("Initialized queue: " + queue);
        return queue;
    }
    
    public synchronized CSQueue getQueue(final String queueName) {
        if (queueName == null) {
            return null;
        }
        return this.queues.get(queueName);
    }
    
    private String getMappedQueue(final String user) throws IOException {
        for (final CapacitySchedulerConfiguration.QueueMapping mapping : this.mappings) {
            if (mapping.type == CapacitySchedulerConfiguration.QueueMapping.MappingType.USER) {
                if (mapping.source.equals("%user")) {
                    if (mapping.queue.equals("%user")) {
                        return user;
                    }
                    if (mapping.queue.equals("%primary_group")) {
                        return this.groups.getGroups(user).get(0);
                    }
                    return mapping.queue;
                }
                else if (user.equals(mapping.source)) {
                    return mapping.queue;
                }
            }
            if (mapping.type == CapacitySchedulerConfiguration.QueueMapping.MappingType.GROUP) {
                for (final String userGroups : this.groups.getGroups(user)) {
                    if (userGroups.equals(mapping.source)) {
                        return mapping.queue;
                    }
                }
            }
        }
        return null;
    }
    
    private synchronized void addApplication(final ApplicationId applicationId, String queueName, final String user, final boolean isAppRecovering) {
        if (this.mappings != null && this.mappings.size() > 0) {
            try {
                final String mappedQueue = this.getMappedQueue(user);
                if (mappedQueue != null && (queueName.equals("default") || this.overrideWithQueueMappings)) {
                    CapacityScheduler.LOG.info("Application " + applicationId + " user " + user + " mapping [" + queueName + "] to [" + mappedQueue + "] override " + this.overrideWithQueueMappings);
                    queueName = mappedQueue;
                    final RMApp rmApp = this.rmContext.getRMApps().get(applicationId);
                    rmApp.setQueue(queueName);
                }
            }
            catch (IOException ioex) {
                final String message = "Failed to submit application " + applicationId + " submitted by user " + user + " reason: " + ioex.getMessage();
                this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(applicationId, message));
                return;
            }
        }
        final CSQueue queue = this.getQueue(queueName);
        if (queue == null) {
            if (isAppRecovering) {
                final String queueErrorMsg = "Queue named " + queueName + " missing during application recovery." + " Queue removal during recovery is not presently supported by the" + " capacity scheduler, please restart with all queues configured" + " which were present before shutdown/restart.";
                CapacityScheduler.LOG.fatal(queueErrorMsg);
                throw new QueueNotFoundException(queueErrorMsg);
            }
            final String message = "Application " + applicationId + " submitted by user " + user + " to unknown queue: " + queueName;
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(applicationId, message));
        }
        else {
            if (!(queue instanceof LeafQueue)) {
                final String message = "Application " + applicationId + " submitted by user " + user + " to non-leaf queue: " + queueName;
                this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(applicationId, message));
                return;
            }
            try {
                queue.submitApplication(applicationId, user, queueName);
            }
            catch (AccessControlException ace) {
                CapacityScheduler.LOG.info("Failed to submit application " + applicationId + " to queue " + queueName + " from user " + user, ace);
                this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(applicationId, ace.toString()));
                return;
            }
            queue.getMetrics().submitApp(user);
            final SchedulerApplication<FiCaSchedulerApp> application = new SchedulerApplication<FiCaSchedulerApp>(queue, user);
            this.applications.put(applicationId, (SchedulerApplication<T>)application);
            CapacityScheduler.LOG.info("Accepted application " + applicationId + " from user: " + user + ", in queue: " + queueName);
            if (isAppRecovering) {
                if (CapacityScheduler.LOG.isDebugEnabled()) {
                    CapacityScheduler.LOG.debug(applicationId + " is recovering. Skip notifying APP_ACCEPTED");
                }
            }
            else {
                this.rmContext.getDispatcher().getEventHandler().handle(new RMAppEvent(applicationId, RMAppEventType.APP_ACCEPTED));
            }
        }
    }
    
    private synchronized void addApplicationAttempt(final ApplicationAttemptId applicationAttemptId, final boolean transferStateFromPreviousAttempt, final boolean isAttemptRecovering) {
        final SchedulerApplication<FiCaSchedulerApp> application = (SchedulerApplication<FiCaSchedulerApp>)this.applications.get(applicationAttemptId.getApplicationId());
        final CSQueue queue = (CSQueue)application.getQueue();
        final FiCaSchedulerApp attempt = new FiCaSchedulerApp(applicationAttemptId, application.getUser(), queue, queue.getActiveUsersManager(), this.rmContext);
        if (transferStateFromPreviousAttempt) {
            attempt.transferStateFromPreviousAttempt(application.getCurrentAppAttempt());
        }
        application.setCurrentAppAttempt(attempt);
        queue.submitApplicationAttempt(attempt, application.getUser());
        CapacityScheduler.LOG.info("Added Application Attempt " + applicationAttemptId + " to scheduler from user " + application.getUser() + " in queue " + queue.getQueueName());
        if (isAttemptRecovering) {
            if (CapacityScheduler.LOG.isDebugEnabled()) {
                CapacityScheduler.LOG.debug(applicationAttemptId + " is recovering. Skipping notifying ATTEMPT_ADDED");
            }
        }
        else {
            this.rmContext.getDispatcher().getEventHandler().handle(new RMAppAttemptEvent(applicationAttemptId, RMAppAttemptEventType.ATTEMPT_ADDED));
        }
    }
    
    private synchronized void doneApplication(final ApplicationId applicationId, final RMAppState finalState) {
        final SchedulerApplication<FiCaSchedulerApp> application = (SchedulerApplication<FiCaSchedulerApp>)this.applications.get(applicationId);
        if (application == null) {
            CapacityScheduler.LOG.warn("Couldn't find application " + applicationId);
            return;
        }
        final CSQueue queue = (CSQueue)application.getQueue();
        if (!(queue instanceof LeafQueue)) {
            CapacityScheduler.LOG.error("Cannot finish application from non-leaf queue: " + queue.getQueueName());
        }
        else {
            queue.finishApplication(applicationId, application.getUser());
        }
        application.stop(finalState);
        this.applications.remove(applicationId);
    }
    
    private synchronized void doneApplicationAttempt(final ApplicationAttemptId applicationAttemptId, final RMAppAttemptState rmAppAttemptFinalState, final boolean keepContainers) {
        CapacityScheduler.LOG.info("Application Attempt " + applicationAttemptId + " is done." + " finalState=" + rmAppAttemptFinalState);
        final FiCaSchedulerApp attempt = this.getApplicationAttempt(applicationAttemptId);
        final SchedulerApplication<FiCaSchedulerApp> application = (SchedulerApplication<FiCaSchedulerApp>)this.applications.get(applicationAttemptId.getApplicationId());
        if (application == null || attempt == null) {
            CapacityScheduler.LOG.info("Unknown application " + applicationAttemptId + " has completed!");
            return;
        }
        for (final RMContainer rmContainer : attempt.getLiveContainers()) {
            if (keepContainers && rmContainer.getState().equals(RMContainerState.RUNNING)) {
                CapacityScheduler.LOG.info("Skip killing " + rmContainer.getContainerId());
            }
            else {
                this.completedContainer(rmContainer, SchedulerUtils.createAbnormalContainerStatus(rmContainer.getContainerId(), "Container of a completed application"), RMContainerEventType.KILL);
            }
        }
        for (final RMContainer rmContainer : attempt.getReservedContainers()) {
            this.completedContainer(rmContainer, SchedulerUtils.createAbnormalContainerStatus(rmContainer.getContainerId(), "Application Complete"), RMContainerEventType.KILL);
        }
        attempt.stop(rmAppAttemptFinalState);
        final String queueName = attempt.getQueue().getQueueName();
        final CSQueue queue = this.queues.get(queueName);
        if (!(queue instanceof LeafQueue)) {
            CapacityScheduler.LOG.error("Cannot finish application from non-leaf queue: " + queueName);
        }
        else {
            queue.finishApplicationAttempt(attempt, queue.getQueueName());
        }
    }
    
    @Lock({ Lock.NoLock.class })
    @Override
    public Allocation allocate(final ApplicationAttemptId applicationAttemptId, final List<ResourceRequest> ask, final List<ContainerId> release, final List<String> blacklistAdditions, final List<String> blacklistRemovals) {
        final FiCaSchedulerApp application = this.getApplicationAttempt(applicationAttemptId);
        if (application == null) {
            CapacityScheduler.LOG.info("Calling allocate on removed or non existant application " + applicationAttemptId);
            return CapacityScheduler.EMPTY_ALLOCATION;
        }
        SchedulerUtils.normalizeRequests(ask, this.getResourceCalculator(), this.getClusterResource(), this.getMinimumResourceCapability(), this.maximumAllocation);
        this.releaseContainers(release, application);
        synchronized (application) {
            if (application.isStopped()) {
                CapacityScheduler.LOG.info("Calling allocate on a stopped application " + applicationAttemptId);
                return CapacityScheduler.EMPTY_ALLOCATION;
            }
            if (!ask.isEmpty()) {
                if (CapacityScheduler.LOG.isDebugEnabled()) {
                    CapacityScheduler.LOG.debug("allocate: pre-update applicationAttemptId=" + applicationAttemptId + " application=" + application);
                }
                application.showRequests();
                application.updateResourceRequests(ask);
                CapacityScheduler.LOG.debug("allocate: post-update");
                application.showRequests();
            }
            if (CapacityScheduler.LOG.isDebugEnabled()) {
                CapacityScheduler.LOG.debug("allocate: applicationAttemptId=" + applicationAttemptId + " #ask=" + ask.size());
            }
            application.updateBlacklist(blacklistAdditions, blacklistRemovals);
            return application.getAllocation(this.getResourceCalculator(), this.clusterResource, this.getMinimumResourceCapability());
        }
    }
    
    @Lock({ Lock.NoLock.class })
    @Override
    public QueueInfo getQueueInfo(final String queueName, final boolean includeChildQueues, final boolean recursive) throws IOException {
        CSQueue queue = null;
        synchronized (this) {
            queue = this.queues.get(queueName);
        }
        if (queue == null) {
            throw new IOException("Unknown queue: " + queueName);
        }
        return queue.getQueueInfo(includeChildQueues, recursive);
    }
    
    @Lock({ Lock.NoLock.class })
    @Override
    public List<QueueUserACLInfo> getQueueUserAclInfo() {
        UserGroupInformation user = null;
        try {
            user = UserGroupInformation.getCurrentUser();
        }
        catch (IOException ioe) {
            return new ArrayList<QueueUserACLInfo>();
        }
        return this.root.getQueueUserAclInfo(user);
    }
    
    private synchronized void nodeUpdate(final RMNode nm) {
        if (CapacityScheduler.LOG.isDebugEnabled()) {
            CapacityScheduler.LOG.debug("nodeUpdate: " + nm + " clusterResources: " + this.clusterResource);
        }
        final FiCaSchedulerNode node = this.getNode(nm.getNodeID());
        final List<UpdatedContainerInfo> containerInfoList = nm.pullContainerUpdates();
        final List<ContainerStatus> newlyLaunchedContainers = new ArrayList<ContainerStatus>();
        final List<ContainerStatus> completedContainers = new ArrayList<ContainerStatus>();
        for (final UpdatedContainerInfo containerInfo : containerInfoList) {
            newlyLaunchedContainers.addAll(containerInfo.getNewlyLaunchedContainers());
            completedContainers.addAll(containerInfo.getCompletedContainers());
        }
        for (final ContainerStatus launchedContainer : newlyLaunchedContainers) {
            this.containerLaunchedOnNode(launchedContainer.getContainerId(), node);
        }
        for (final ContainerStatus completedContainer : completedContainers) {
            final ContainerId containerId = completedContainer.getContainerId();
            CapacityScheduler.LOG.debug("Container FINISHED: " + containerId);
            this.completedContainer(this.getRMContainer(containerId), completedContainer, RMContainerEventType.FINISHED);
        }
        if (CapacityScheduler.LOG.isDebugEnabled()) {
            CapacityScheduler.LOG.debug("Node being looked for scheduling " + nm + " availableResource: " + node.getAvailableResource());
        }
    }
    
    private synchronized void updateNodeAndQueueResource(final RMNode nm, final ResourceOption resourceOption) {
        this.updateNodeResource(nm, resourceOption);
        this.root.updateClusterResource(this.clusterResource);
    }
    
    private synchronized void allocateContainersToNode(final FiCaSchedulerNode node) {
        if (this.rmContext.isWorkPreservingRecoveryEnabled() && !this.rmContext.isSchedulerReadyForAllocatingContainers()) {
            return;
        }
        final RMContainer reservedContainer = node.getReservedContainer();
        if (reservedContainer != null) {
            final FiCaSchedulerApp reservedApplication = ((AbstractYarnScheduler<FiCaSchedulerApp, N>)this).getCurrentAttemptForContainer(reservedContainer.getContainerId());
            CapacityScheduler.LOG.info("Trying to fulfill reservation for application " + reservedApplication.getApplicationId() + " on node: " + node.getNodeID());
            final LeafQueue queue = (LeafQueue)reservedApplication.getQueue();
            final CSAssignment assignment = queue.assignContainers(this.clusterResource, node, false);
            final RMContainer excessReservation = assignment.getExcessReservation();
            if (excessReservation != null) {
                final Container container = excessReservation.getContainer();
                queue.completedContainer(this.clusterResource, assignment.getApplication(), node, excessReservation, SchedulerUtils.createAbnormalContainerStatus(container.getId(), "Container reservation no longer required."), RMContainerEventType.RELEASED, null, true);
            }
        }
        if (node.getReservedContainer() == null) {
            if (this.calculator.computeAvailableContainers(node.getAvailableResource(), this.minimumAllocation) > 0) {
                if (CapacityScheduler.LOG.isDebugEnabled()) {
                    CapacityScheduler.LOG.debug("Trying to schedule on node: " + node.getNodeName() + ", available: " + node.getAvailableResource());
                }
                this.root.assignContainers(this.clusterResource, node, false);
            }
        }
        else {
            CapacityScheduler.LOG.info("Skipping scheduling since node " + node.getNodeID() + " is reserved by application " + node.getReservedContainer().getContainerId().getApplicationAttemptId());
        }
    }
    
    @Override
    public void handle(final SchedulerEvent event) {
        switch (event.getType()) {
            case NODE_ADDED: {
                final NodeAddedSchedulerEvent nodeAddedEvent = (NodeAddedSchedulerEvent)event;
                this.addNode(nodeAddedEvent.getAddedRMNode());
                this.recoverContainersOnNode(nodeAddedEvent.getContainerReports(), nodeAddedEvent.getAddedRMNode());
                break;
            }
            case NODE_REMOVED: {
                final NodeRemovedSchedulerEvent nodeRemovedEvent = (NodeRemovedSchedulerEvent)event;
                this.removeNode(nodeRemovedEvent.getRemovedRMNode());
                break;
            }
            case NODE_RESOURCE_UPDATE: {
                final NodeResourceUpdateSchedulerEvent nodeResourceUpdatedEvent = (NodeResourceUpdateSchedulerEvent)event;
                this.updateNodeAndQueueResource(nodeResourceUpdatedEvent.getRMNode(), nodeResourceUpdatedEvent.getResourceOption());
                break;
            }
            case NODE_UPDATE: {
                final NodeUpdateSchedulerEvent nodeUpdatedEvent = (NodeUpdateSchedulerEvent)event;
                final RMNode node = nodeUpdatedEvent.getRMNode();
                this.nodeUpdate(node);
                if (!this.scheduleAsynchronously) {
                    this.allocateContainersToNode(this.getNode(node.getNodeID()));
                }
                break;
            }
            case APP_ADDED: {
                final AppAddedSchedulerEvent appAddedEvent = (AppAddedSchedulerEvent)event;
                final String queueName = this.resolveReservationQueueName(appAddedEvent.getQueue(), appAddedEvent.getApplicationId(), appAddedEvent.getReservationID());
                if (queueName != null) {
                    this.addApplication(appAddedEvent.getApplicationId(), queueName, appAddedEvent.getUser(), appAddedEvent.getIsAppRecovering());
                }
                break;
            }
            case APP_REMOVED: {
                final AppRemovedSchedulerEvent appRemovedEvent = (AppRemovedSchedulerEvent)event;
                this.doneApplication(appRemovedEvent.getApplicationID(), appRemovedEvent.getFinalState());
                break;
            }
            case APP_ATTEMPT_ADDED: {
                final AppAttemptAddedSchedulerEvent appAttemptAddedEvent = (AppAttemptAddedSchedulerEvent)event;
                this.addApplicationAttempt(appAttemptAddedEvent.getApplicationAttemptId(), appAttemptAddedEvent.getTransferStateFromPreviousAttempt(), appAttemptAddedEvent.getIsAttemptRecovering());
                break;
            }
            case APP_ATTEMPT_REMOVED: {
                final AppAttemptRemovedSchedulerEvent appAttemptRemovedEvent = (AppAttemptRemovedSchedulerEvent)event;
                this.doneApplicationAttempt(appAttemptRemovedEvent.getApplicationAttemptID(), appAttemptRemovedEvent.getFinalAttemptState(), appAttemptRemovedEvent.getKeepContainersAcrossAppAttempts());
                break;
            }
            case CONTAINER_EXPIRED: {
                final ContainerExpiredSchedulerEvent containerExpiredEvent = (ContainerExpiredSchedulerEvent)event;
                final ContainerId containerId = containerExpiredEvent.getContainerId();
                this.completedContainer(this.getRMContainer(containerId), SchedulerUtils.createAbnormalContainerStatus(containerId, "Container expired since it was unused"), RMContainerEventType.EXPIRE);
                break;
            }
            default: {
                CapacityScheduler.LOG.error("Invalid eventtype " + ((AbstractEvent<Object>)event).getType() + ". Ignoring!");
                break;
            }
        }
    }
    
    private synchronized void addNode(final RMNode nodeManager) {
        if (this.labelManager != null) {
            this.labelManager.activateNode(nodeManager.getNodeID(), nodeManager.getTotalCapability());
        }
        this.nodes.put(nodeManager.getNodeID(), (N)new FiCaSchedulerNode(nodeManager, this.usePortForNodeName));
        Resources.addTo(this.clusterResource, nodeManager.getTotalCapability());
        this.root.updateClusterResource(this.clusterResource);
        final int numNodes = this.numNodeManagers.incrementAndGet();
        CapacityScheduler.LOG.info("Added node " + nodeManager.getNodeAddress() + " clusterResource: " + this.clusterResource);
        if (this.scheduleAsynchronously && numNodes == 1) {
            this.asyncSchedulerThread.beginSchedule();
        }
    }
    
    private synchronized void removeNode(final RMNode nodeInfo) {
        if (this.labelManager != null) {
            this.labelManager.deactivateNode(nodeInfo.getNodeID());
        }
        final FiCaSchedulerNode node = (FiCaSchedulerNode)this.nodes.get(nodeInfo.getNodeID());
        if (node == null) {
            return;
        }
        Resources.subtractFrom(this.clusterResource, node.getRMNode().getTotalCapability());
        this.root.updateClusterResource(this.clusterResource);
        final int numNodes = this.numNodeManagers.decrementAndGet();
        if (this.scheduleAsynchronously && numNodes == 0) {
            this.asyncSchedulerThread.suspendSchedule();
        }
        final List<RMContainer> runningContainers = node.getRunningContainers();
        for (final RMContainer container : runningContainers) {
            this.completedContainer(container, SchedulerUtils.createAbnormalContainerStatus(container.getContainerId(), "Container released on a *lost* node"), RMContainerEventType.KILL);
        }
        final RMContainer reservedContainer = node.getReservedContainer();
        if (reservedContainer != null) {
            this.completedContainer(reservedContainer, SchedulerUtils.createAbnormalContainerStatus(reservedContainer.getContainerId(), "Container released on a *lost* node"), RMContainerEventType.KILL);
        }
        this.nodes.remove(nodeInfo.getNodeID());
        CapacityScheduler.LOG.info("Removed node " + nodeInfo.getNodeAddress() + " clusterResource: " + this.clusterResource);
    }
    
    @Lock({ CapacityScheduler.class })
    @Override
    protected synchronized void completedContainer(final RMContainer rmContainer, final ContainerStatus containerStatus, final RMContainerEventType event) {
        if (rmContainer == null) {
            CapacityScheduler.LOG.info("Null container completed...");
            return;
        }
        final Container container = rmContainer.getContainer();
        final FiCaSchedulerApp application = ((AbstractYarnScheduler<FiCaSchedulerApp, N>)this).getCurrentAttemptForContainer(container.getId());
        final ApplicationId appId = container.getId().getApplicationAttemptId().getApplicationId();
        if (application == null) {
            CapacityScheduler.LOG.info("Container " + container + " of" + " unknown application " + appId + " completed with event " + event);
            return;
        }
        final FiCaSchedulerNode node = this.getNode(container.getNodeId());
        final LeafQueue queue = (LeafQueue)application.getQueue();
        queue.completedContainer(this.clusterResource, application, node, rmContainer, containerStatus, event, null, true);
        CapacityScheduler.LOG.info("Application attempt " + application.getApplicationAttemptId() + " released container " + container.getId() + " on node: " + node + " with event: " + event);
    }
    
    @Lock({ Lock.NoLock.class })
    @VisibleForTesting
    @Override
    public FiCaSchedulerApp getApplicationAttempt(final ApplicationAttemptId applicationAttemptId) {
        return super.getApplicationAttempt(applicationAttemptId);
    }
    
    @Lock({ Lock.NoLock.class })
    @Override
    public FiCaSchedulerNode getNode(final NodeId nodeId) {
        return (FiCaSchedulerNode)this.nodes.get(nodeId);
    }
    
    @Lock({ Lock.NoLock.class })
    Map<NodeId, FiCaSchedulerNode> getAllNodes() {
        return (Map<NodeId, FiCaSchedulerNode>)this.nodes;
    }
    
    @Lock({ Lock.NoLock.class })
    @Override
    public void recover(final RMStateStore.RMState state) throws Exception {
    }
    
    @Override
    public void dropContainerReservation(final RMContainer container) {
        if (CapacityScheduler.LOG.isDebugEnabled()) {
            CapacityScheduler.LOG.debug("DROP_RESERVATION:" + container.toString());
        }
        this.completedContainer(container, SchedulerUtils.createAbnormalContainerStatus(container.getContainerId(), "Container reservation no longer required."), RMContainerEventType.KILL);
    }
    
    @Override
    public void preemptContainer(final ApplicationAttemptId aid, final RMContainer cont) {
        if (CapacityScheduler.LOG.isDebugEnabled()) {
            CapacityScheduler.LOG.debug("PREEMPT_CONTAINER: application:" + aid.toString() + " container: " + cont.toString());
        }
        final FiCaSchedulerApp app = this.getApplicationAttempt(aid);
        if (app != null) {
            app.addPreemptContainer(cont.getContainerId());
        }
    }
    
    @Override
    public void killContainer(final RMContainer cont) {
        if (CapacityScheduler.LOG.isDebugEnabled()) {
            CapacityScheduler.LOG.debug("KILL_CONTAINER: container" + cont.toString());
        }
        this.recoverResourceRequestForContainer(cont);
        this.completedContainer(cont, SchedulerUtils.createPreemptedContainerStatus(cont.getContainerId(), "Container preempted by scheduler"), RMContainerEventType.KILL);
    }
    
    @Override
    public synchronized boolean checkAccess(final UserGroupInformation callerUGI, final QueueACL acl, final String queueName) {
        final CSQueue queue = this.getQueue(queueName);
        if (queue == null) {
            if (CapacityScheduler.LOG.isDebugEnabled()) {
                CapacityScheduler.LOG.debug("ACL not found for queue access-type " + acl + " for queue " + queueName);
            }
            return false;
        }
        return queue.hasAccess(acl, callerUGI);
    }
    
    @Override
    public List<ApplicationAttemptId> getAppsInQueue(final String queueName) {
        final CSQueue queue = this.queues.get(queueName);
        if (queue == null) {
            return null;
        }
        final List<ApplicationAttemptId> apps = new ArrayList<ApplicationAttemptId>();
        queue.collectSchedulerApplications(apps);
        return apps;
    }
    
    private CapacitySchedulerConfiguration loadCapacitySchedulerConfiguration(final Configuration configuration) throws IOException {
        try {
            final InputStream CSInputStream = this.rmContext.getConfigurationProvider().getConfigurationInputStream(configuration, "capacity-scheduler.xml");
            if (CSInputStream != null) {
                configuration.addResource(CSInputStream);
                return new CapacitySchedulerConfiguration(configuration, false);
            }
            return new CapacitySchedulerConfiguration(configuration, true);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }
    
    private synchronized String resolveReservationQueueName(String queueName, final ApplicationId applicationId, final ReservationId reservationID) {
        CSQueue queue = this.getQueue(queueName);
        if (queue == null || !(queue instanceof PlanQueue)) {
            return queueName;
        }
        if (reservationID != null) {
            final String resQName = reservationID.toString();
            queue = this.getQueue(resQName);
            if (queue == null) {
                final String message = "Application " + applicationId + " submitted to a reservation which is not yet currently active: " + resQName;
                this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(applicationId, message));
                return null;
            }
            if (!queue.getParent().getQueueName().equals(queueName)) {
                final String message = "Application: " + applicationId + " submitted to a reservation " + resQName + " which does not belong to the specified queue: " + queueName;
                this.rmContext.getDispatcher().getEventHandler().handle(new RMAppRejectedEvent(applicationId, message));
                return null;
            }
            queueName = resQName;
        }
        else {
            queueName += "-default";
        }
        return queueName;
    }
    
    @Override
    public synchronized void removeQueue(final String queueName) throws SchedulerDynamicEditException {
        CapacityScheduler.LOG.info("Removing queue: " + queueName);
        final CSQueue q = this.getQueue(queueName);
        if (!(q instanceof ReservationQueue)) {
            throw new SchedulerDynamicEditException("The queue that we are asked to remove (" + queueName + ") is not a ReservationQueue");
        }
        final ReservationQueue disposableLeafQueue = (ReservationQueue)q;
        if (disposableLeafQueue.getNumApplications() > 0) {
            throw new SchedulerDynamicEditException("The queue " + queueName + " is not empty " + disposableLeafQueue.getApplications().size() + " active apps " + disposableLeafQueue.pendingApplications.size() + " pending apps");
        }
        ((PlanQueue)disposableLeafQueue.getParent()).removeChildQueue(q);
        this.queues.remove(queueName);
        CapacityScheduler.LOG.info("Removal of ReservationQueue " + queueName + " has succeeded");
    }
    
    @Override
    public synchronized void addQueue(final Queue queue) throws SchedulerDynamicEditException {
        if (!(queue instanceof ReservationQueue)) {
            throw new SchedulerDynamicEditException("Queue " + queue.getQueueName() + " is not a ReservationQueue");
        }
        final ReservationQueue newQueue = (ReservationQueue)queue;
        if (newQueue.getParent() == null || !(newQueue.getParent() instanceof PlanQueue)) {
            throw new SchedulerDynamicEditException("ParentQueue for " + newQueue.getQueueName() + " is not properly set (should be set and be a PlanQueue)");
        }
        final PlanQueue parentPlan = (PlanQueue)newQueue.getParent();
        final String queuename = newQueue.getQueueName();
        parentPlan.addChildQueue(newQueue);
        this.queues.put(queuename, newQueue);
        CapacityScheduler.LOG.info("Creation of ReservationQueue " + newQueue + " succeeded");
    }
    
    @Override
    public synchronized void setEntitlement(final String inQueue, final QueueEntitlement entitlement) throws SchedulerDynamicEditException, YarnException {
        final LeafQueue queue = this.getAndCheckLeafQueue(inQueue);
        final ParentQueue parent = (ParentQueue)queue.getParent();
        if (!(queue instanceof ReservationQueue)) {
            throw new SchedulerDynamicEditException("Entitlement can not be modified dynamically since queue " + inQueue + " is not a ReservationQueue");
        }
        if (!(parent instanceof PlanQueue)) {
            throw new SchedulerDynamicEditException("The parent of ReservationQueue " + inQueue + " must be an PlanQueue");
        }
        final ReservationQueue newQueue = (ReservationQueue)queue;
        final float sumChilds = ((PlanQueue)parent).sumOfChildCapacities();
        final float newChildCap = sumChilds - queue.getCapacity() + entitlement.getCapacity();
        if (newChildCap < 0.0f || newChildCap >= 1.0001f) {
            throw new SchedulerDynamicEditException("Sum of child queues would exceed 100% for PlanQueue: " + parent.getQueueName());
        }
        if (Math.abs(entitlement.getCapacity() - queue.getCapacity()) == 0.0f && Math.abs(entitlement.getMaxCapacity() - queue.getMaximumCapacity()) == 0.0f) {
            return;
        }
        newQueue.setEntitlement(entitlement);
        CapacityScheduler.LOG.info("Set entitlement for ReservationQueue " + inQueue + "  to " + queue.getCapacity() + " request was (" + entitlement.getCapacity() + ")");
    }
    
    @Override
    public synchronized String moveApplication(final ApplicationId appId, final String targetQueueName) throws YarnException {
        final FiCaSchedulerApp app = this.getApplicationAttempt(ApplicationAttemptId.newInstance(appId, 0));
        final String sourceQueueName = app.getQueue().getQueueName();
        final LeafQueue source = this.getAndCheckLeafQueue(sourceQueueName);
        final String destQueueName = this.handleMoveToPlanQueue(targetQueueName);
        final LeafQueue dest = this.getAndCheckLeafQueue(destQueueName);
        final String user = app.getUser();
        try {
            dest.submitApplication(appId, user, destQueueName);
        }
        catch (AccessControlException e) {
            throw new YarnException(e);
        }
        for (final RMContainer rmContainer : app.getLiveContainers()) {
            source.detachContainer(this.clusterResource, app, rmContainer);
            dest.attachContainer(this.clusterResource, app, rmContainer);
        }
        source.finishApplicationAttempt(app, sourceQueueName);
        source.getParent().finishApplication(appId, app.getUser());
        app.move(dest);
        dest.submitApplicationAttempt(app, user);
        this.applications.get(appId).setQueue(dest);
        CapacityScheduler.LOG.info("App: " + app.getApplicationId() + " successfully moved from " + sourceQueueName + " to: " + destQueueName);
        return targetQueueName;
    }
    
    private LeafQueue getAndCheckLeafQueue(final String queue) throws YarnException {
        final CSQueue ret = this.getQueue(queue);
        if (ret == null) {
            throw new YarnException("The specified Queue: " + queue + " doesn't exist");
        }
        if (!(ret instanceof LeafQueue)) {
            throw new YarnException("The specified Queue: " + queue + " is not a Leaf Queue. Move is supported only for Leaf Queues.");
        }
        return (LeafQueue)ret;
    }
    
    @Override
    public EnumSet<YarnServiceProtos.SchedulerResourceTypes> getSchedulingResourceTypes() {
        if (this.calculator.getClass().getName().equals(DefaultResourceCalculator.class.getName())) {
            return EnumSet.of(YarnServiceProtos.SchedulerResourceTypes.MEMORY);
        }
        return EnumSet.of(YarnServiceProtos.SchedulerResourceTypes.MEMORY, YarnServiceProtos.SchedulerResourceTypes.CPU);
    }
    
    private String handleMoveToPlanQueue(String targetQueueName) {
        final CSQueue dest = this.getQueue(targetQueueName);
        if (dest != null && dest instanceof PlanQueue) {
            targetQueueName += "-default";
        }
        return targetQueueName;
    }
    
    @Override
    public Set<String> getPlanQueues() {
        final Set<String> ret = new HashSet<String>();
        for (final Map.Entry<String, CSQueue> l : this.queues.entrySet()) {
            if (l.getValue() instanceof PlanQueue) {
                ret.add(l.getKey());
            }
        }
        return ret;
    }
    
    static {
        LOG = LogFactory.getLog(CapacityScheduler.class);
        queueComparator = new Comparator<CSQueue>() {
            @Override
            public int compare(final CSQueue q1, final CSQueue q2) {
                if (q1.getUsedCapacity() < q2.getUsedCapacity()) {
                    return -1;
                }
                if (q1.getUsedCapacity() > q2.getUsedCapacity()) {
                    return 1;
                }
                return q1.getQueuePath().compareTo(q2.getQueuePath());
            }
        };
        applicationComparator = new Comparator<FiCaSchedulerApp>() {
            @Override
            public int compare(final FiCaSchedulerApp a1, final FiCaSchedulerApp a2) {
                return a1.getApplicationId().compareTo(a2.getApplicationId());
            }
        };
        random = new Random(System.currentTimeMillis());
        noop = new QueueHook();
    }
    
    static class AsyncScheduleThread extends Thread
    {
        private final CapacityScheduler cs;
        private AtomicBoolean runSchedules;
        
        public AsyncScheduleThread(final CapacityScheduler cs) {
            this.runSchedules = new AtomicBoolean(false);
            this.cs = cs;
            this.setDaemon(true);
        }
        
        @Override
        public void run() {
            while (true) {
                if (!this.runSchedules.get()) {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException ie) {}
                }
                else {
                    CapacityScheduler.schedule(this.cs);
                }
            }
        }
        
        public void beginSchedule() {
            this.runSchedules.set(true);
        }
        
        public void suspendSchedule() {
            this.runSchedules.set(false);
        }
    }
    
    static class QueueHook
    {
        public CSQueue hook(final CSQueue queue) {
            return queue;
        }
    }
}
