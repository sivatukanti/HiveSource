// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerState;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.server.utils.Lock;
import java.util.Collection;
import com.google.common.collect.Sets;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.NodeType;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerAppUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import java.util.HashSet;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import java.util.Collections;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.Iterator;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import java.io.IOException;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.QueueState;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.List;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import java.util.ArrayList;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import java.util.HashMap;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Map;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class LeafQueue extends AbstractCSQueue
{
    private static final Log LOG;
    private float absoluteUsedCapacity;
    private int userLimit;
    private float userLimitFactor;
    protected int maxApplications;
    protected int maxApplicationsPerUser;
    private float maxAMResourcePerQueuePercent;
    private int maxActiveApplications;
    private int maxActiveAppsUsingAbsCap;
    private int maxActiveApplicationsPerUser;
    private int nodeLocalityDelay;
    Set<FiCaSchedulerApp> activeApplications;
    Map<ApplicationAttemptId, FiCaSchedulerApp> applicationAttemptMap;
    Set<FiCaSchedulerApp> pendingApplications;
    private final float minimumAllocationFactor;
    private Map<String, User> users;
    private final RecordFactory recordFactory;
    private CapacitySchedulerContext scheduler;
    private final ActiveUsersManager activeUsersManager;
    private Resource lastClusterResource;
    private final QueueHeadroomInfo queueHeadroomInfo;
    private static final CSAssignment NULL_ASSIGNMENT;
    private static final CSAssignment SKIP_ASSIGNMENT;
    
    public LeafQueue(final CapacitySchedulerContext cs, final String queueName, final CSQueue parent, final CSQueue old) throws IOException {
        super(cs, queueName, parent, old);
        this.absoluteUsedCapacity = 0.0f;
        this.applicationAttemptMap = new HashMap<ApplicationAttemptId, FiCaSchedulerApp>();
        this.users = new HashMap<String, User>();
        this.recordFactory = RecordFactoryProvider.getRecordFactory(null);
        this.lastClusterResource = Resources.none();
        this.queueHeadroomInfo = new QueueHeadroomInfo();
        this.scheduler = cs;
        this.activeUsersManager = new ActiveUsersManager(this.metrics);
        this.minimumAllocationFactor = Resources.ratio(this.resourceCalculator, Resources.subtract(this.maximumAllocation, this.minimumAllocation), this.maximumAllocation);
        final float capacity = this.getCapacityFromConf();
        final float absoluteCapacity = parent.getAbsoluteCapacity() * capacity;
        final float maximumCapacity = cs.getConfiguration().getMaximumCapacity(this.getQueuePath()) / 100.0f;
        final float absoluteMaxCapacity = CSQueueUtils.computeAbsoluteMaximumCapacity(maximumCapacity, parent);
        final int userLimit = cs.getConfiguration().getUserLimit(this.getQueuePath());
        final float userLimitFactor = cs.getConfiguration().getUserLimitFactor(this.getQueuePath());
        int maxApplications = cs.getConfiguration().getMaximumApplicationsPerQueue(this.getQueuePath());
        if (maxApplications < 0) {
            final int maxSystemApps = cs.getConfiguration().getMaximumSystemApplications();
            maxApplications = (int)(maxSystemApps * absoluteCapacity);
        }
        this.maxApplicationsPerUser = (int)(maxApplications * (userLimit / 100.0f) * userLimitFactor);
        final float maxAMResourcePerQueuePercent = cs.getConfiguration().getMaximumApplicationMasterResourcePerQueuePercent(this.getQueuePath());
        final int maxActiveApplications = CSQueueUtils.computeMaxActiveApplications(this.resourceCalculator, cs.getClusterResource(), this.minimumAllocation, maxAMResourcePerQueuePercent, absoluteMaxCapacity);
        this.maxActiveAppsUsingAbsCap = CSQueueUtils.computeMaxActiveApplications(this.resourceCalculator, cs.getClusterResource(), this.minimumAllocation, maxAMResourcePerQueuePercent, absoluteCapacity);
        final int maxActiveApplicationsPerUser = CSQueueUtils.computeMaxActiveApplicationsPerUser(this.maxActiveAppsUsingAbsCap, userLimit, userLimitFactor);
        this.queueInfo.setChildQueues(new ArrayList<QueueInfo>());
        final QueueState state = cs.getConfiguration().getState(this.getQueuePath());
        final Map<QueueACL, AccessControlList> acls = cs.getConfiguration().getAcls(this.getQueuePath());
        this.setupQueueConfigs(cs.getClusterResource(), capacity, absoluteCapacity, maximumCapacity, absoluteMaxCapacity, userLimit, userLimitFactor, maxApplications, maxAMResourcePerQueuePercent, this.maxApplicationsPerUser, maxActiveApplications, maxActiveApplicationsPerUser, state, acls, cs.getConfiguration().getNodeLocalityDelay(), this.accessibleLabels, this.defaultLabelExpression, this.capacitiyByNodeLabels, this.maxCapacityByNodeLabels, cs.getConfiguration().getReservationContinueLook());
        if (LeafQueue.LOG.isDebugEnabled()) {
            LeafQueue.LOG.debug("LeafQueue: name=" + queueName + ", fullname=" + this.getQueuePath());
        }
        final Comparator<FiCaSchedulerApp> applicationComparator = cs.getApplicationComparator();
        this.pendingApplications = new TreeSet<FiCaSchedulerApp>(applicationComparator);
        this.activeApplications = new TreeSet<FiCaSchedulerApp>(applicationComparator);
    }
    
    protected float getCapacityFromConf() {
        return this.scheduler.getConfiguration().getCapacity(this.getQueuePath()) / 100.0f;
    }
    
    protected synchronized void setupQueueConfigs(final Resource clusterResource, final float capacity, final float absoluteCapacity, final float maximumCapacity, final float absoluteMaxCapacity, final int userLimit, final float userLimitFactor, final int maxApplications, final float maxAMResourcePerQueuePercent, final int maxApplicationsPerUser, final int maxActiveApplications, final int maxActiveApplicationsPerUser, final QueueState state, final Map<QueueACL, AccessControlList> acls, final int nodeLocalityDelay, final Set<String> labels, final String defaultLabelExpression, final Map<String, Float> capacitieByLabel, final Map<String, Float> maximumCapacitiesByLabel, final boolean revervationContinueLooking) throws IOException {
        super.setupQueueConfigs(clusterResource, capacity, absoluteCapacity, maximumCapacity, absoluteMaxCapacity, state, acls, labels, defaultLabelExpression, capacitieByLabel, maximumCapacitiesByLabel, revervationContinueLooking);
        CSQueueUtils.checkMaxCapacity(this.getQueueName(), capacity, maximumCapacity);
        final float absCapacity = this.getParent().getAbsoluteCapacity() * capacity;
        CSQueueUtils.checkAbsoluteCapacity(this.getQueueName(), absCapacity, absoluteMaxCapacity);
        this.absoluteCapacity = absCapacity;
        this.userLimit = userLimit;
        this.userLimitFactor = userLimitFactor;
        this.maxApplications = maxApplications;
        this.maxAMResourcePerQueuePercent = maxAMResourcePerQueuePercent;
        this.maxApplicationsPerUser = maxApplicationsPerUser;
        this.maxActiveApplications = maxActiveApplications;
        this.maxActiveApplicationsPerUser = maxActiveApplicationsPerUser;
        if (!SchedulerUtils.checkQueueLabelExpression(this.accessibleLabels, this.defaultLabelExpression)) {
            throw new IOException("Invalid default label expression of  queue=" + this.queueInfo.getQueueName() + " doesn't have permission to access all labels " + "in default label expression. labelExpression of resource request=" + ((this.defaultLabelExpression == null) ? "" : this.defaultLabelExpression) + ". Queue labels=" + ((this.queueInfo.getAccessibleNodeLabels() == null) ? "" : StringUtils.join(this.queueInfo.getAccessibleNodeLabels().iterator(), ',')));
        }
        this.nodeLocalityDelay = nodeLocalityDelay;
        final StringBuilder aclsString = new StringBuilder();
        for (final Map.Entry<QueueACL, AccessControlList> e : acls.entrySet()) {
            aclsString.append(e.getKey() + ":" + e.getValue().getAclString());
        }
        final StringBuilder labelStrBuilder = new StringBuilder();
        if (labels != null) {
            for (final String s : labels) {
                labelStrBuilder.append(s);
                labelStrBuilder.append(",");
            }
        }
        LeafQueue.LOG.info("Initializing " + this.queueName + "\n" + "capacity = " + capacity + " [= (float) configuredCapacity / 100 ]" + "\n" + "asboluteCapacity = " + absoluteCapacity + " [= parentAbsoluteCapacity * capacity ]" + "\n" + "maxCapacity = " + maximumCapacity + " [= configuredMaxCapacity ]" + "\n" + "absoluteMaxCapacity = " + absoluteMaxCapacity + " [= 1.0 maximumCapacity undefined, " + "(parentAbsoluteMaxCapacity * maximumCapacity) / 100 otherwise ]" + "\n" + "userLimit = " + userLimit + " [= configuredUserLimit ]" + "\n" + "userLimitFactor = " + userLimitFactor + " [= configuredUserLimitFactor ]" + "\n" + "maxApplications = " + maxApplications + " [= configuredMaximumSystemApplicationsPerQueue or" + " (int)(configuredMaximumSystemApplications * absoluteCapacity)]" + "\n" + "maxApplicationsPerUser = " + maxApplicationsPerUser + " [= (int)(maxApplications * (userLimit / 100.0f) * " + "userLimitFactor) ]" + "\n" + "maxActiveApplications = " + maxActiveApplications + " [= max(" + "(int)ceil((clusterResourceMemory / minimumAllocation) * " + "maxAMResourcePerQueuePercent * absoluteMaxCapacity)," + "1) ]" + "\n" + "maxActiveAppsUsingAbsCap = " + this.maxActiveAppsUsingAbsCap + " [= max(" + "(int)ceil((clusterResourceMemory / minimumAllocation) *" + "maxAMResourcePercent * absoluteCapacity)," + "1) ]" + "\n" + "maxActiveApplicationsPerUser = " + maxActiveApplicationsPerUser + " [= max(" + "(int)(maxActiveApplications * (userLimit / 100.0f) * " + "userLimitFactor)," + "1) ]" + "\n" + "usedCapacity = " + this.usedCapacity + " [= usedResourcesMemory / " + "(clusterResourceMemory * absoluteCapacity)]" + "\n" + "absoluteUsedCapacity = " + this.absoluteUsedCapacity + " [= usedResourcesMemory / clusterResourceMemory]" + "\n" + "maxAMResourcePerQueuePercent = " + maxAMResourcePerQueuePercent + " [= configuredMaximumAMResourcePercent ]" + "\n" + "minimumAllocationFactor = " + this.minimumAllocationFactor + " [= (float)(maximumAllocationMemory - minimumAllocationMemory) / " + "maximumAllocationMemory ]" + "\n" + "numContainers = " + this.numContainers + " [= currentNumContainers ]" + "\n" + "state = " + state + " [= configuredState ]" + "\n" + "acls = " + (Object)aclsString + " [= configuredAcls ]" + "\n" + "nodeLocalityDelay = " + nodeLocalityDelay + "\n" + "labels=" + labelStrBuilder.toString() + "\n" + "nodeLocalityDelay = " + nodeLocalityDelay + "\n" + "reservationsContinueLooking = " + this.reservationsContinueLooking + "\n");
    }
    
    @Override
    public String getQueuePath() {
        return this.getParent().getQueuePath() + "." + this.getQueueName();
    }
    
    @InterfaceAudience.Private
    public float getMinimumAllocationFactor() {
        return this.minimumAllocationFactor;
    }
    
    @InterfaceAudience.Private
    public float getMaxAMResourcePerQueuePercent() {
        return this.maxAMResourcePerQueuePercent;
    }
    
    public int getMaxApplications() {
        return this.maxApplications;
    }
    
    public synchronized int getMaxApplicationsPerUser() {
        return this.maxApplicationsPerUser;
    }
    
    public synchronized int getMaximumActiveApplications() {
        return this.maxActiveApplications;
    }
    
    public synchronized int getMaximumActiveApplicationsPerUser() {
        return this.maxActiveApplicationsPerUser;
    }
    
    @Override
    public ActiveUsersManager getActiveUsersManager() {
        return this.activeUsersManager;
    }
    
    @Override
    public List<CSQueue> getChildQueues() {
        return null;
    }
    
    synchronized void setUserLimit(final int userLimit) {
        this.userLimit = userLimit;
    }
    
    synchronized void setUserLimitFactor(final float userLimitFactor) {
        this.userLimitFactor = userLimitFactor;
    }
    
    @Override
    public synchronized int getNumApplications() {
        return this.getNumPendingApplications() + this.getNumActiveApplications();
    }
    
    public synchronized int getNumPendingApplications() {
        return this.pendingApplications.size();
    }
    
    public synchronized int getNumActiveApplications() {
        return this.activeApplications.size();
    }
    
    @InterfaceAudience.Private
    public synchronized int getNumApplications(final String user) {
        return this.getUser(user).getTotalApplications();
    }
    
    @InterfaceAudience.Private
    public synchronized int getNumPendingApplications(final String user) {
        return this.getUser(user).getPendingApplications();
    }
    
    @InterfaceAudience.Private
    public synchronized int getNumActiveApplications(final String user) {
        return this.getUser(user).getActiveApplications();
    }
    
    @Override
    public synchronized int getNumContainers() {
        return this.numContainers;
    }
    
    @Override
    public synchronized QueueState getState() {
        return this.state;
    }
    
    @InterfaceAudience.Private
    public synchronized int getUserLimit() {
        return this.userLimit;
    }
    
    @InterfaceAudience.Private
    public synchronized float getUserLimitFactor() {
        return this.userLimitFactor;
    }
    
    @Override
    public synchronized QueueInfo getQueueInfo(final boolean includeChildQueues, final boolean recursive) {
        this.queueInfo.setCurrentCapacity(this.usedCapacity);
        return this.queueInfo;
    }
    
    @Override
    public synchronized List<QueueUserACLInfo> getQueueUserAclInfo(final UserGroupInformation user) {
        final QueueUserACLInfo userAclInfo = this.recordFactory.newRecordInstance(QueueUserACLInfo.class);
        final List<QueueACL> operations = new ArrayList<QueueACL>();
        for (final QueueACL operation : QueueACL.values()) {
            if (this.hasAccess(operation, user)) {
                operations.add(operation);
            }
        }
        userAclInfo.setQueueName(this.getQueueName());
        userAclInfo.setUserAcls(operations);
        return Collections.singletonList(userAclInfo);
    }
    
    @InterfaceAudience.Private
    public int getNodeLocalityDelay() {
        return this.nodeLocalityDelay;
    }
    
    @Override
    public String toString() {
        return this.queueName + ": " + "capacity=" + this.capacity + ", " + "absoluteCapacity=" + this.absoluteCapacity + ", " + "usedResources=" + this.usedResources + ", " + "usedCapacity=" + this.getUsedCapacity() + ", " + "absoluteUsedCapacity=" + this.getAbsoluteUsedCapacity() + ", " + "numApps=" + this.getNumApplications() + ", " + "numContainers=" + this.getNumContainers();
    }
    
    @VisibleForTesting
    public synchronized void setNodeLabelManager(final RMNodeLabelsManager mgr) {
        this.labelManager = mgr;
    }
    
    @VisibleForTesting
    public synchronized User getUser(final String userName) {
        User user = this.users.get(userName);
        if (user == null) {
            user = new User();
            this.users.put(userName, user);
        }
        return user;
    }
    
    public synchronized ArrayList<UserInfo> getUsers() {
        final ArrayList<UserInfo> usersToReturn = new ArrayList<UserInfo>();
        for (final Map.Entry<String, User> entry : this.users.entrySet()) {
            usersToReturn.add(new UserInfo(entry.getKey(), Resources.clone(entry.getValue().consumed), entry.getValue().getActiveApplications(), entry.getValue().getPendingApplications()));
        }
        return usersToReturn;
    }
    
    @Override
    public synchronized void reinitialize(final CSQueue newlyParsedQueue, final Resource clusterResource) throws IOException {
        if (!(newlyParsedQueue instanceof LeafQueue) || !newlyParsedQueue.getQueuePath().equals(this.getQueuePath())) {
            throw new IOException("Trying to reinitialize " + this.getQueuePath() + " from " + newlyParsedQueue.getQueuePath());
        }
        final LeafQueue newlyParsedLeafQueue = (LeafQueue)newlyParsedQueue;
        this.setupQueueConfigs(clusterResource, newlyParsedLeafQueue.capacity, newlyParsedLeafQueue.absoluteCapacity, newlyParsedLeafQueue.maximumCapacity, newlyParsedLeafQueue.absoluteMaxCapacity, newlyParsedLeafQueue.userLimit, newlyParsedLeafQueue.userLimitFactor, newlyParsedLeafQueue.maxApplications, newlyParsedLeafQueue.maxAMResourcePerQueuePercent, newlyParsedLeafQueue.getMaxApplicationsPerUser(), newlyParsedLeafQueue.getMaximumActiveApplications(), newlyParsedLeafQueue.getMaximumActiveApplicationsPerUser(), newlyParsedLeafQueue.state, newlyParsedLeafQueue.acls, newlyParsedLeafQueue.getNodeLocalityDelay(), newlyParsedLeafQueue.accessibleLabels, newlyParsedLeafQueue.defaultLabelExpression, newlyParsedLeafQueue.capacitiyByNodeLabels, newlyParsedLeafQueue.maxCapacityByNodeLabels, newlyParsedLeafQueue.reservationsContinueLooking);
        this.activateApplications();
    }
    
    @Override
    public void submitApplicationAttempt(final FiCaSchedulerApp application, final String userName) {
        synchronized (this) {
            final User user = this.getUser(userName);
            this.addApplicationAttempt(application, user);
        }
        if (application.isPending()) {
            this.metrics.submitAppAttempt(userName);
        }
        this.getParent().submitApplicationAttempt(application, userName);
    }
    
    @Override
    public void submitApplication(final ApplicationId applicationId, final String userName, final String queue) throws AccessControlException {
        final UserGroupInformation userUgi = UserGroupInformation.createRemoteUser(userName);
        if (!this.hasAccess(QueueACL.SUBMIT_APPLICATIONS, userUgi) && !this.hasAccess(QueueACL.ADMINISTER_QUEUE, userUgi)) {
            throw new AccessControlException("User " + userName + " cannot submit" + " applications to queue " + this.getQueuePath());
        }
        User user = null;
        synchronized (this) {
            if (this.getState() != QueueState.RUNNING) {
                final String msg = "Queue " + this.getQueuePath() + " is STOPPED. Cannot accept submission of application: " + applicationId;
                LeafQueue.LOG.info(msg);
                throw new AccessControlException(msg);
            }
            if (this.getNumApplications() >= this.getMaxApplications()) {
                final String msg = "Queue " + this.getQueuePath() + " already has " + this.getNumApplications() + " applications," + " cannot accept submission of application: " + applicationId;
                LeafQueue.LOG.info(msg);
                throw new AccessControlException(msg);
            }
            user = this.getUser(userName);
            if (user.getTotalApplications() >= this.getMaxApplicationsPerUser()) {
                final String msg = "Queue " + this.getQueuePath() + " already has " + user.getTotalApplications() + " applications from user " + userName + " cannot accept submission of application: " + applicationId;
                LeafQueue.LOG.info(msg);
                throw new AccessControlException(msg);
            }
        }
        try {
            this.getParent().submitApplication(applicationId, userName, queue);
        }
        catch (AccessControlException ace) {
            LeafQueue.LOG.info("Failed to submit application to parent-queue: " + this.getParent().getQueuePath(), ace);
            throw ace;
        }
    }
    
    private synchronized void activateApplications() {
        final Iterator<FiCaSchedulerApp> i = this.pendingApplications.iterator();
        while (i.hasNext()) {
            final FiCaSchedulerApp application = i.next();
            if (this.getNumActiveApplications() >= this.getMaximumActiveApplications()) {
                break;
            }
            final User user = this.getUser(application.getUser());
            if (user.getActiveApplications() >= this.getMaximumActiveApplicationsPerUser()) {
                continue;
            }
            user.activateApplication();
            this.activeApplications.add(application);
            i.remove();
            LeafQueue.LOG.info("Application " + application.getApplicationId() + " from user: " + application.getUser() + " activated in queue: " + this.getQueueName());
        }
    }
    
    private synchronized void addApplicationAttempt(final FiCaSchedulerApp application, final User user) {
        user.submitApplication();
        this.pendingApplications.add(application);
        this.applicationAttemptMap.put(application.getApplicationAttemptId(), application);
        this.activateApplications();
        LeafQueue.LOG.info("Application added - appId: " + application.getApplicationId() + " user: " + user + "," + " leaf-queue: " + this.getQueueName() + " #user-pending-applications: " + user.getPendingApplications() + " #user-active-applications: " + user.getActiveApplications() + " #queue-pending-applications: " + this.getNumPendingApplications() + " #queue-active-applications: " + this.getNumActiveApplications());
    }
    
    @Override
    public void finishApplication(final ApplicationId application, final String user) {
        this.activeUsersManager.deactivateApplication(user, application);
        this.getParent().finishApplication(application, user);
    }
    
    @Override
    public void finishApplicationAttempt(final FiCaSchedulerApp application, final String queue) {
        synchronized (this) {
            this.removeApplicationAttempt(application, this.getUser(application.getUser()));
        }
        this.getParent().finishApplicationAttempt(application, queue);
    }
    
    public synchronized void removeApplicationAttempt(final FiCaSchedulerApp application, final User user) {
        final boolean wasActive = this.activeApplications.remove(application);
        if (!wasActive) {
            this.pendingApplications.remove(application);
        }
        this.applicationAttemptMap.remove(application.getApplicationAttemptId());
        user.finishApplication(wasActive);
        if (user.getTotalApplications() == 0) {
            this.users.remove(application.getUser());
        }
        this.activateApplications();
        LeafQueue.LOG.info("Application removed - appId: " + application.getApplicationId() + " user: " + application.getUser() + " queue: " + this.getQueueName() + " #user-pending-applications: " + user.getPendingApplications() + " #user-active-applications: " + user.getActiveApplications() + " #queue-pending-applications: " + this.getNumPendingApplications() + " #queue-active-applications: " + this.getNumActiveApplications());
    }
    
    private synchronized FiCaSchedulerApp getApplication(final ApplicationAttemptId applicationAttemptId) {
        return this.applicationAttemptMap.get(applicationAttemptId);
    }
    
    private static Set<String> getRequestLabelSetByExpression(final String labelExpression) {
        final Set<String> labels = new HashSet<String>();
        if (null == labelExpression) {
            return labels;
        }
        for (final String l : labelExpression.split("&&")) {
            if (!l.trim().isEmpty()) {
                labels.add(l.trim());
            }
        }
        return labels;
    }
    
    @Override
    public synchronized CSAssignment assignContainers(final Resource clusterResource, final FiCaSchedulerNode node, final boolean needToUnreserve) {
        if (LeafQueue.LOG.isDebugEnabled()) {
            LeafQueue.LOG.debug("assignContainers: node=" + node.getNodeName() + " #applications=" + this.activeApplications.size());
        }
        if (!SchedulerUtils.checkQueueAccessToNode(this.accessibleLabels, this.labelManager.getLabelsOnNode(node.getNodeID()))) {
            return LeafQueue.NULL_ASSIGNMENT;
        }
        final RMContainer reservedContainer = node.getReservedContainer();
        if (reservedContainer != null) {
            final FiCaSchedulerApp application = this.getApplication(reservedContainer.getApplicationAttemptId());
            synchronized (application) {
                return this.assignReservedContainer(application, node, reservedContainer, clusterResource);
            }
        }
        for (final FiCaSchedulerApp application2 : this.activeApplications) {
            if (LeafQueue.LOG.isDebugEnabled()) {
                LeafQueue.LOG.debug("pre-assignContainers for application " + application2.getApplicationId());
                application2.showRequests();
            }
            synchronized (application2) {
                if (SchedulerAppUtils.isBlacklisted(application2, node, LeafQueue.LOG)) {
                    continue;
                }
                for (final Priority priority : application2.getPriorities()) {
                    final ResourceRequest anyRequest = application2.getResourceRequest(priority, "*");
                    if (null == anyRequest) {
                        continue;
                    }
                    final Resource required = anyRequest.getCapability();
                    if (application2.getTotalRequiredResources(priority) <= 0) {
                        continue;
                    }
                    if (!this.reservationsContinueLooking && !this.needContainers(application2, priority, required)) {
                        if (!LeafQueue.LOG.isDebugEnabled()) {
                            continue;
                        }
                        LeafQueue.LOG.debug("doesn't need containers based on reservation algo!");
                    }
                    else {
                        final Set<String> requestedNodeLabels = getRequestLabelSetByExpression(anyRequest.getNodeLabelExpression());
                        final Resource userLimit = this.computeUserLimitAndSetHeadroom(application2, clusterResource, required, requestedNodeLabels);
                        if (!this.canAssignToThisQueue(clusterResource, required, this.labelManager.getLabelsOnNode(node.getNodeID()), application2, true)) {
                            return LeafQueue.NULL_ASSIGNMENT;
                        }
                        if (!this.assignToUser(clusterResource, application2.getUser(), userLimit, application2, true, requestedNodeLabels)) {
                            break;
                        }
                        application2.addSchedulingOpportunity(priority);
                        final CSAssignment assignment = this.assignContainersOnNode(clusterResource, node, application2, priority, null, needToUnreserve);
                        if (assignment.getSkipped()) {
                            application2.subtractSchedulingOpportunity(priority);
                        }
                        else {
                            final Resource assigned = assignment.getResource();
                            if (Resources.greaterThan(this.resourceCalculator, clusterResource, assigned, Resources.none())) {
                                this.allocateResource(clusterResource, application2, assigned, this.labelManager.getLabelsOnNode(node.getNodeID()));
                                if (assignment.getType() != NodeType.OFF_SWITCH) {
                                    if (LeafQueue.LOG.isDebugEnabled()) {
                                        LeafQueue.LOG.debug("Resetting scheduling opportunities");
                                    }
                                    application2.resetSchedulingOpportunities(priority);
                                }
                                return assignment;
                            }
                            break;
                        }
                    }
                }
            }
            if (LeafQueue.LOG.isDebugEnabled()) {
                LeafQueue.LOG.debug("post-assignContainers for application " + application2.getApplicationId());
            }
            application2.showRequests();
        }
        return LeafQueue.NULL_ASSIGNMENT;
    }
    
    private synchronized CSAssignment assignReservedContainer(final FiCaSchedulerApp application, final FiCaSchedulerNode node, final RMContainer rmContainer, final Resource clusterResource) {
        final Priority priority = rmContainer.getReservedPriority();
        if (application.getTotalRequiredResources(priority) == 0) {
            return new CSAssignment(application, rmContainer);
        }
        this.assignContainersOnNode(clusterResource, node, application, priority, rmContainer, false);
        return new CSAssignment(Resources.none(), NodeType.NODE_LOCAL);
    }
    
    protected Resource getHeadroom(final User user, final Resource queueMaxCap, final Resource clusterResource, final FiCaSchedulerApp application, final Resource required) {
        return this.getHeadroom(user, queueMaxCap, clusterResource, this.computeUserLimit(application, clusterResource, required, user, null));
    }
    
    private Resource getHeadroom(final User user, final Resource queueMaxCap, final Resource clusterResource, final Resource userLimit) {
        final Resource headroom = Resources.min(this.resourceCalculator, clusterResource, Resources.subtract(userLimit, user.getTotalConsumedResources()), Resources.subtract(queueMaxCap, this.usedResources));
        return headroom;
    }
    
    synchronized boolean canAssignToThisQueue(final Resource clusterResource, final Resource required, final Set<String> nodeLabels, final FiCaSchedulerApp application, final boolean checkReservations) {
        Set<String> labelCanAccess;
        if (null == nodeLabels || nodeLabels.isEmpty()) {
            labelCanAccess = new HashSet<String>();
            labelCanAccess.add("");
        }
        else {
            labelCanAccess = new HashSet<String>(Sets.intersection(this.accessibleLabels, nodeLabels));
        }
        boolean canAssign = true;
        for (final String label : labelCanAccess) {
            if (!this.usedResourcesByNodeLabels.containsKey(label)) {
                this.usedResourcesByNodeLabels.put(label, Resources.createResource(0));
            }
            final Resource potentialTotalCapacity = Resources.add(this.usedResourcesByNodeLabels.get(label), required);
            final float potentialNewCapacity = Resources.divide(this.resourceCalculator, clusterResource, potentialTotalCapacity, this.labelManager.getResourceByLabel(label, clusterResource));
            if (this.reservationsContinueLooking && checkReservations && label.equals("")) {
                final float potentialNewWithoutReservedCapacity = Resources.divide(this.resourceCalculator, clusterResource, Resources.subtract(potentialTotalCapacity, application.getCurrentReservation()), this.labelManager.getResourceByLabel(label, clusterResource));
                if (potentialNewWithoutReservedCapacity <= this.absoluteMaxCapacity) {
                    if (LeafQueue.LOG.isDebugEnabled()) {
                        LeafQueue.LOG.debug("try to use reserved: " + this.getQueueName() + " usedResources: " + this.usedResources + " clusterResources: " + clusterResource + " reservedResources: " + application.getCurrentReservation() + " currentCapacity " + Resources.divide(this.resourceCalculator, clusterResource, this.usedResources, clusterResource) + " required " + required + " potentialNewWithoutReservedCapacity: " + potentialNewWithoutReservedCapacity + " ( " + " max-capacity: " + this.absoluteMaxCapacity + ")");
                    }
                    return true;
                }
            }
            if (potentialNewCapacity > this.getAbsoluteMaximumCapacityByNodeLabel(label) + 1.0E-4) {
                canAssign = false;
                break;
            }
            if (!LeafQueue.LOG.isDebugEnabled()) {
                continue;
            }
            LeafQueue.LOG.debug(this.getQueueName() + "Check assign to queue, label=" + label + " usedResources: " + this.usedResourcesByNodeLabels.get(label) + " clusterResources: " + clusterResource + " currentCapacity " + Resources.divide(this.resourceCalculator, clusterResource, this.usedResourcesByNodeLabels.get(label), this.labelManager.getResourceByLabel(label, clusterResource)) + " potentialNewCapacity: " + potentialNewCapacity + " ( " + " max-capacity: " + this.absoluteMaxCapacity + ")");
        }
        return canAssign;
    }
    
    @Lock({ LeafQueue.class, FiCaSchedulerApp.class })
    Resource computeUserLimitAndSetHeadroom(final FiCaSchedulerApp application, final Resource clusterResource, final Resource required, final Set<String> requestedLabels) {
        final String user = application.getUser();
        final User queueUser = this.getUser(user);
        final Resource userLimit = this.computeUserLimit(application, clusterResource, required, queueUser, requestedLabels);
        final float absoluteMaxAvailCapacity = CSQueueUtils.getAbsoluteMaxAvailCapacity(this.resourceCalculator, clusterResource, this);
        final Resource queueMaxCap = Resources.multiplyAndNormalizeDown(this.resourceCalculator, clusterResource, absoluteMaxAvailCapacity, this.minimumAllocation);
        synchronized (this.queueHeadroomInfo) {
            this.queueHeadroomInfo.setQueueMaxCap(queueMaxCap);
            this.queueHeadroomInfo.setClusterResource(clusterResource);
        }
        final Resource headroom = this.getHeadroom(queueUser, queueMaxCap, clusterResource, userLimit);
        if (LeafQueue.LOG.isDebugEnabled()) {
            LeafQueue.LOG.debug("Headroom calculation for user " + user + ": " + " userLimit=" + userLimit + " queueMaxCap=" + queueMaxCap + " consumed=" + queueUser.getTotalConsumedResources() + " headroom=" + headroom);
        }
        final CapacityHeadroomProvider headroomProvider = new CapacityHeadroomProvider(queueUser, this, application, required, this.queueHeadroomInfo);
        application.setHeadroomProvider(headroomProvider);
        this.metrics.setAvailableResourcesToUser(user, headroom);
        return userLimit;
    }
    
    @Lock({ Lock.NoLock.class })
    private Resource computeUserLimit(final FiCaSchedulerApp application, final Resource clusterResource, final Resource required, final User user, final Set<String> requestedLabels) {
        Resource queueCapacity = Resource.newInstance(0, 0);
        if (requestedLabels != null && !requestedLabels.isEmpty()) {
            final String firstLabel = requestedLabels.iterator().next();
            queueCapacity = Resources.max(this.resourceCalculator, clusterResource, queueCapacity, Resources.multiplyAndNormalizeUp(this.resourceCalculator, this.labelManager.getResourceByLabel(firstLabel, clusterResource), this.getAbsoluteCapacityByNodeLabel(firstLabel), this.minimumAllocation));
        }
        else {
            queueCapacity = Resources.multiplyAndNormalizeUp(this.resourceCalculator, this.labelManager.getResourceByLabel("", clusterResource), this.absoluteCapacity, this.minimumAllocation);
        }
        queueCapacity = Resources.max(this.resourceCalculator, clusterResource, queueCapacity, required);
        final Resource currentCapacity = Resources.lessThan(this.resourceCalculator, clusterResource, this.usedResources, queueCapacity) ? queueCapacity : Resources.add(this.usedResources, required);
        final int activeUsers = this.activeUsersManager.getNumActiveUsers();
        final Resource limit = Resources.roundUp(this.resourceCalculator, Resources.min(this.resourceCalculator, clusterResource, Resources.max(this.resourceCalculator, clusterResource, Resources.divideAndCeil(this.resourceCalculator, currentCapacity, activeUsers), Resources.divideAndCeil(this.resourceCalculator, Resources.multiplyAndRoundDown(currentCapacity, this.userLimit), 100)), Resources.multiplyAndRoundDown(queueCapacity, this.userLimitFactor)), this.minimumAllocation);
        if (LeafQueue.LOG.isDebugEnabled()) {
            final String userName = application.getUser();
            LeafQueue.LOG.debug("User limit computation for " + userName + " in queue " + this.getQueueName() + " userLimit=" + this.userLimit + " userLimitFactor=" + this.userLimitFactor + " required: " + required + " consumed: " + user.getTotalConsumedResources() + " limit: " + limit + " queueCapacity: " + queueCapacity + " qconsumed: " + this.usedResources + " currentCapacity: " + currentCapacity + " activeUsers: " + activeUsers + " clusterCapacity: " + clusterResource);
        }
        return limit;
    }
    
    @InterfaceAudience.Private
    protected synchronized boolean assignToUser(final Resource clusterResource, final String userName, final Resource limit, final FiCaSchedulerApp application, final boolean checkReservations, final Set<String> requestLabels) {
        final User user = this.getUser(userName);
        String label = "";
        if (requestLabels != null && !requestLabels.isEmpty()) {
            label = requestLabels.iterator().next();
        }
        if (!Resources.greaterThan(this.resourceCalculator, clusterResource, user.getConsumedResourceByLabel(label), limit)) {
            return true;
        }
        if (this.reservationsContinueLooking && checkReservations && Resources.lessThanOrEqual(this.resourceCalculator, clusterResource, Resources.subtract(user.getTotalConsumedResources(), application.getCurrentReservation()), limit)) {
            if (LeafQueue.LOG.isDebugEnabled()) {
                LeafQueue.LOG.debug("User " + userName + " in queue " + this.getQueueName() + " will exceed limit based on reservations - " + " consumed: " + user.getTotalConsumedResources() + " reserved: " + application.getCurrentReservation() + " limit: " + limit);
            }
            return true;
        }
        if (LeafQueue.LOG.isDebugEnabled()) {
            LeafQueue.LOG.debug("User " + userName + " in queue " + this.getQueueName() + " will exceed limit - " + " consumed: " + user.getTotalConsumedResources() + " limit: " + limit);
        }
        return false;
    }
    
    boolean needContainers(final FiCaSchedulerApp application, final Priority priority, final Resource required) {
        final int requiredContainers = application.getTotalRequiredResources(priority);
        final int reservedContainers = application.getNumReservedContainers(priority);
        int starvation = 0;
        if (reservedContainers > 0) {
            final float nodeFactor = Resources.ratio(this.resourceCalculator, required, this.getMaximumAllocation());
            starvation = (int)(application.getReReservations(priority) / (float)reservedContainers * (1.0f - Math.min(nodeFactor, this.getMinimumAllocationFactor())));
            if (LeafQueue.LOG.isDebugEnabled()) {
                LeafQueue.LOG.debug("needsContainers: app.#re-reserve=" + application.getReReservations(priority) + " reserved=" + reservedContainers + " nodeFactor=" + nodeFactor + " minAllocFactor=" + this.getMinimumAllocationFactor() + " starvation=" + starvation);
            }
        }
        return starvation + requiredContainers - reservedContainers > 0;
    }
    
    private CSAssignment assignContainersOnNode(final Resource clusterResource, final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority, final RMContainer reservedContainer, final boolean needToUnreserve) {
        Resource assigned = Resources.none();
        final ResourceRequest nodeLocalResourceRequest = application.getResourceRequest(priority, node.getNodeName());
        if (nodeLocalResourceRequest != null) {
            assigned = this.assignNodeLocalContainers(clusterResource, nodeLocalResourceRequest, node, application, priority, reservedContainer, needToUnreserve);
            if (Resources.greaterThan(this.resourceCalculator, clusterResource, assigned, Resources.none())) {
                return new CSAssignment(assigned, NodeType.NODE_LOCAL);
            }
        }
        final ResourceRequest rackLocalResourceRequest = application.getResourceRequest(priority, node.getRackName());
        if (rackLocalResourceRequest != null) {
            if (!rackLocalResourceRequest.getRelaxLocality()) {
                return LeafQueue.SKIP_ASSIGNMENT;
            }
            assigned = this.assignRackLocalContainers(clusterResource, rackLocalResourceRequest, node, application, priority, reservedContainer, needToUnreserve);
            if (Resources.greaterThan(this.resourceCalculator, clusterResource, assigned, Resources.none())) {
                return new CSAssignment(assigned, NodeType.RACK_LOCAL);
            }
        }
        final ResourceRequest offSwitchResourceRequest = application.getResourceRequest(priority, "*");
        if (offSwitchResourceRequest == null) {
            return LeafQueue.SKIP_ASSIGNMENT;
        }
        if (!offSwitchResourceRequest.getRelaxLocality()) {
            return LeafQueue.SKIP_ASSIGNMENT;
        }
        return new CSAssignment(this.assignOffSwitchContainers(clusterResource, offSwitchResourceRequest, node, application, priority, reservedContainer, needToUnreserve), NodeType.OFF_SWITCH);
    }
    
    @InterfaceAudience.Private
    protected boolean findNodeToUnreserve(final Resource clusterResource, final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority, final Resource capability) {
        final NodeId idToUnreserve = application.getNodeIdToUnreserve(priority, capability);
        if (idToUnreserve == null) {
            if (LeafQueue.LOG.isDebugEnabled()) {
                LeafQueue.LOG.debug("checked to see if could unreserve for app but nothing reserved that matches for this app");
            }
            return false;
        }
        final FiCaSchedulerNode nodeToUnreserve = this.scheduler.getNode(idToUnreserve);
        if (nodeToUnreserve == null) {
            LeafQueue.LOG.error("node to unreserve doesn't exist, nodeid: " + idToUnreserve);
            return false;
        }
        if (LeafQueue.LOG.isDebugEnabled()) {
            LeafQueue.LOG.debug("unreserving for app: " + application.getApplicationId() + " on nodeId: " + idToUnreserve + " in order to replace reserved application and place it on node: " + node.getNodeID() + " needing: " + capability);
        }
        Resources.addTo(application.getHeadroom(), nodeToUnreserve.getReservedContainer().getReservedResource());
        this.completedContainer(clusterResource, application, nodeToUnreserve, nodeToUnreserve.getReservedContainer(), SchedulerUtils.createAbnormalContainerStatus(nodeToUnreserve.getReservedContainer().getContainerId(), "Container reservation no longer required."), RMContainerEventType.RELEASED, null, false);
        return true;
    }
    
    @InterfaceAudience.Private
    protected boolean checkLimitsToReserve(final Resource clusterResource, final FiCaSchedulerApp application, final Resource capability, final boolean needToUnreserve) {
        if (needToUnreserve) {
            if (LeafQueue.LOG.isDebugEnabled()) {
                LeafQueue.LOG.debug("we needed to unreserve to be able to allocate");
            }
            return false;
        }
        final Resource userLimit = this.computeUserLimitAndSetHeadroom(application, clusterResource, capability, null);
        if (!this.canAssignToThisQueue(clusterResource, capability, null, application, false)) {
            if (LeafQueue.LOG.isDebugEnabled()) {
                LeafQueue.LOG.debug("was going to reserve but hit queue limit");
            }
            return false;
        }
        if (!this.assignToUser(clusterResource, application.getUser(), userLimit, application, false, null)) {
            if (LeafQueue.LOG.isDebugEnabled()) {
                LeafQueue.LOG.debug("was going to reserve but hit user limit");
            }
            return false;
        }
        return true;
    }
    
    private Resource assignNodeLocalContainers(final Resource clusterResource, final ResourceRequest nodeLocalResourceRequest, final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority, final RMContainer reservedContainer, final boolean needToUnreserve) {
        if (this.canAssign(application, priority, node, NodeType.NODE_LOCAL, reservedContainer)) {
            return this.assignContainer(clusterResource, node, application, priority, nodeLocalResourceRequest, NodeType.NODE_LOCAL, reservedContainer, needToUnreserve);
        }
        return Resources.none();
    }
    
    private Resource assignRackLocalContainers(final Resource clusterResource, final ResourceRequest rackLocalResourceRequest, final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority, final RMContainer reservedContainer, final boolean needToUnreserve) {
        if (this.canAssign(application, priority, node, NodeType.RACK_LOCAL, reservedContainer)) {
            return this.assignContainer(clusterResource, node, application, priority, rackLocalResourceRequest, NodeType.RACK_LOCAL, reservedContainer, needToUnreserve);
        }
        return Resources.none();
    }
    
    private Resource assignOffSwitchContainers(final Resource clusterResource, final ResourceRequest offSwitchResourceRequest, final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority, final RMContainer reservedContainer, final boolean needToUnreserve) {
        if (this.canAssign(application, priority, node, NodeType.OFF_SWITCH, reservedContainer)) {
            return this.assignContainer(clusterResource, node, application, priority, offSwitchResourceRequest, NodeType.OFF_SWITCH, reservedContainer, needToUnreserve);
        }
        return Resources.none();
    }
    
    boolean canAssign(final FiCaSchedulerApp application, final Priority priority, final FiCaSchedulerNode node, final NodeType type, final RMContainer reservedContainer) {
        if (type == NodeType.OFF_SWITCH) {
            if (reservedContainer != null) {
                return true;
            }
            final ResourceRequest offSwitchRequest = application.getResourceRequest(priority, "*");
            final long missedOpportunities = application.getSchedulingOpportunities(priority);
            final long requiredContainers = offSwitchRequest.getNumContainers();
            final float localityWaitFactor = application.getLocalityWaitFactor(priority, this.scheduler.getNumClusterNodes());
            return requiredContainers * localityWaitFactor < missedOpportunities;
        }
        else {
            final ResourceRequest rackLocalRequest = application.getResourceRequest(priority, node.getRackName());
            if (rackLocalRequest == null || rackLocalRequest.getNumContainers() <= 0) {
                return false;
            }
            if (type == NodeType.RACK_LOCAL) {
                final long missedOpportunities = application.getSchedulingOpportunities(priority);
                return Math.min(this.scheduler.getNumClusterNodes(), this.getNodeLocalityDelay()) < missedOpportunities;
            }
            if (type == NodeType.NODE_LOCAL) {
                final ResourceRequest nodeLocalRequest = application.getResourceRequest(priority, node.getNodeName());
                if (nodeLocalRequest != null) {
                    return nodeLocalRequest.getNumContainers() > 0;
                }
            }
            return false;
        }
    }
    
    private Container getContainer(final RMContainer rmContainer, final FiCaSchedulerApp application, final FiCaSchedulerNode node, final Resource capability, final Priority priority) {
        return (rmContainer != null) ? rmContainer.getContainer() : this.createContainer(application, node, capability, priority);
    }
    
    Container createContainer(final FiCaSchedulerApp application, final FiCaSchedulerNode node, final Resource capability, final Priority priority) {
        final NodeId nodeId = node.getRMNode().getNodeID();
        final ContainerId containerId = BuilderUtils.newContainerId(application.getApplicationAttemptId(), application.getNewContainerId());
        final Container container = BuilderUtils.newContainer(containerId, nodeId, node.getRMNode().getHttpAddress(), capability, priority, null);
        return container;
    }
    
    private Resource assignContainer(final Resource clusterResource, final FiCaSchedulerNode node, final FiCaSchedulerApp application, final Priority priority, final ResourceRequest request, final NodeType type, final RMContainer rmContainer, final boolean needToUnreserve) {
        if (LeafQueue.LOG.isDebugEnabled()) {
            LeafQueue.LOG.debug("assignContainers: node=" + node.getNodeName() + " application=" + application.getApplicationId() + " priority=" + priority.getPriority() + " request=" + request + " type=" + type + " needToUnreserve= " + needToUnreserve);
        }
        if (!SchedulerUtils.checkNodeLabelExpression(this.labelManager.getLabelsOnNode(node.getNodeID()), request.getNodeLabelExpression())) {
            if (rmContainer != null) {
                this.unreserve(application, priority, node, rmContainer);
            }
            return Resources.none();
        }
        final Resource capability = request.getCapability();
        final Resource available = node.getAvailableResource();
        final Resource totalResource = node.getTotalResource();
        if (!Resources.fitsIn(capability, totalResource)) {
            LeafQueue.LOG.warn("Node : " + node.getNodeID() + " does not have sufficient resource for request : " + request + " node total capability : " + node.getTotalResource());
            return Resources.none();
        }
        assert Resources.greaterThan(this.resourceCalculator, clusterResource, available, Resources.none());
        final Container container = this.getContainer(rmContainer, application, node, capability, priority);
        if (container == null) {
            LeafQueue.LOG.warn("Couldn't get container for allocation!");
            return Resources.none();
        }
        boolean canAllocContainer = true;
        if (this.reservationsContinueLooking) {
            canAllocContainer = this.needContainers(application, priority, capability);
            if (LeafQueue.LOG.isDebugEnabled()) {
                LeafQueue.LOG.debug("can alloc container is: " + canAllocContainer);
            }
        }
        final int availableContainers = this.resourceCalculator.computeAvailableContainers(available, capability);
        if (availableContainers > 0) {
            if (rmContainer != null) {
                this.unreserve(application, priority, node, rmContainer);
            }
            else if (this.reservationsContinueLooking && (!canAllocContainer || needToUnreserve)) {
                final boolean res = this.findNodeToUnreserve(clusterResource, node, application, priority, capability);
                if (!res) {
                    return Resources.none();
                }
            }
            else if (needToUnreserve) {
                if (LeafQueue.LOG.isDebugEnabled()) {
                    LeafQueue.LOG.debug("we needed to unreserve to be able to allocate, skipping");
                }
                return Resources.none();
            }
            final RMContainer allocatedContainer = application.allocate(type, node, priority, request, container);
            if (allocatedContainer == null) {
                return Resources.none();
            }
            node.allocateContainer(allocatedContainer);
            LeafQueue.LOG.info("assignedContainer application attempt=" + application.getApplicationAttemptId() + " container=" + container + " queue=" + this + " clusterResource=" + clusterResource);
            return container.getResource();
        }
        else {
            if (canAllocContainer || rmContainer != null) {
                if (this.reservationsContinueLooking) {
                    final boolean res = this.checkLimitsToReserve(clusterResource, application, capability, needToUnreserve);
                    if (!res) {
                        return Resources.none();
                    }
                }
                this.reserve(application, priority, node, rmContainer, container);
                LeafQueue.LOG.info("Reserved container  application=" + application.getApplicationId() + " resource=" + request.getCapability() + " queue=" + this.toString() + " usedCapacity=" + this.getUsedCapacity() + " absoluteUsedCapacity=" + this.getAbsoluteUsedCapacity() + " used=" + this.usedResources + " cluster=" + clusterResource);
                return request.getCapability();
            }
            return Resources.none();
        }
    }
    
    private void reserve(final FiCaSchedulerApp application, final Priority priority, final FiCaSchedulerNode node, RMContainer rmContainer, final Container container) {
        if (rmContainer == null) {
            this.getMetrics().reserveResource(application.getUser(), container.getResource());
        }
        rmContainer = application.reserve(node, priority, rmContainer, container);
        node.reserveResource(application, priority, rmContainer);
    }
    
    private boolean unreserve(final FiCaSchedulerApp application, final Priority priority, final FiCaSchedulerNode node, final RMContainer rmContainer) {
        if (application.unreserve(node, priority)) {
            node.unreserveResource(application);
            this.getMetrics().unreserveResource(application.getUser(), rmContainer.getContainer().getResource());
            return true;
        }
        return false;
    }
    
    @Override
    public void completedContainer(final Resource clusterResource, final FiCaSchedulerApp application, final FiCaSchedulerNode node, final RMContainer rmContainer, final ContainerStatus containerStatus, final RMContainerEventType event, final CSQueue childQueue, final boolean sortQueues) {
        if (application != null) {
            boolean removed = false;
            synchronized (this) {
                final Container container = rmContainer.getContainer();
                if (rmContainer.getState() == RMContainerState.RESERVED) {
                    removed = this.unreserve(application, rmContainer.getReservedPriority(), node, rmContainer);
                }
                else {
                    removed = application.containerCompleted(rmContainer, containerStatus, event);
                    node.releaseContainer(container);
                }
                if (removed) {
                    this.releaseResource(clusterResource, application, container.getResource(), this.labelManager.getLabelsOnNode(node.getNodeID()));
                    LeafQueue.LOG.info("completedContainer container=" + container + " queue=" + this + " cluster=" + clusterResource);
                }
            }
            if (removed) {
                this.getParent().completedContainer(clusterResource, application, node, rmContainer, null, event, this, sortQueues);
            }
        }
    }
    
    synchronized void allocateResource(final Resource clusterResource, final SchedulerApplicationAttempt application, final Resource resource, final Set<String> nodeLabels) {
        super.allocateResource(clusterResource, resource, nodeLabels);
        final String userName = application.getUser();
        final User user = this.getUser(userName);
        user.assignContainer(resource, nodeLabels);
        Resources.subtractFrom(application.getHeadroom(), resource);
        this.metrics.setAvailableResourcesToUser(userName, application.getHeadroom());
        if (LeafQueue.LOG.isDebugEnabled()) {
            LeafQueue.LOG.info(this.getQueueName() + " user=" + userName + " used=" + this.usedResources + " numContainers=" + this.numContainers + " headroom = " + application.getHeadroom() + " user-resources=" + user.getTotalConsumedResources());
        }
    }
    
    synchronized void releaseResource(final Resource clusterResource, final FiCaSchedulerApp application, final Resource resource, final Set<String> nodeLabels) {
        super.releaseResource(clusterResource, resource, nodeLabels);
        final String userName = application.getUser();
        final User user = this.getUser(userName);
        user.releaseContainer(resource, nodeLabels);
        this.metrics.setAvailableResourcesToUser(userName, application.getHeadroom());
        LeafQueue.LOG.info(this.getQueueName() + " used=" + this.usedResources + " numContainers=" + this.numContainers + " user=" + userName + " user-resources=" + user.getTotalConsumedResources());
    }
    
    @Override
    public synchronized void updateClusterResource(final Resource clusterResource) {
        this.lastClusterResource = clusterResource;
        this.maxActiveApplications = CSQueueUtils.computeMaxActiveApplications(this.resourceCalculator, clusterResource, this.minimumAllocation, this.maxAMResourcePerQueuePercent, this.absoluteMaxCapacity);
        this.maxActiveAppsUsingAbsCap = CSQueueUtils.computeMaxActiveApplications(this.resourceCalculator, clusterResource, this.minimumAllocation, this.maxAMResourcePerQueuePercent, this.absoluteCapacity);
        this.maxActiveApplicationsPerUser = CSQueueUtils.computeMaxActiveApplicationsPerUser(this.maxActiveAppsUsingAbsCap, this.userLimit, this.userLimitFactor);
        CSQueueUtils.updateQueueStatistics(this.resourceCalculator, this, this.getParent(), clusterResource, this.minimumAllocation);
        this.activateApplications();
        for (final FiCaSchedulerApp application : this.activeApplications) {
            synchronized (application) {
                this.computeUserLimitAndSetHeadroom(application, clusterResource, Resources.none(), null);
            }
        }
    }
    
    @Override
    public void recoverContainer(final Resource clusterResource, final SchedulerApplicationAttempt attempt, final RMContainer rmContainer) {
        if (rmContainer.getState().equals(RMContainerState.COMPLETED)) {
            return;
        }
        synchronized (this) {
            this.allocateResource(clusterResource, attempt, rmContainer.getContainer().getResource(), this.labelManager.getLabelsOnNode(rmContainer.getContainer().getNodeId()));
        }
        this.getParent().recoverContainer(clusterResource, attempt, rmContainer);
    }
    
    public Set<FiCaSchedulerApp> getApplications() {
        return this.activeApplications;
    }
    
    public Resource getTotalResourcePending() {
        final Resource ret = BuilderUtils.newResource(0, 0);
        for (final FiCaSchedulerApp f : this.activeApplications) {
            Resources.addTo(ret, f.getTotalPendingRequests());
        }
        return ret;
    }
    
    @Override
    public void collectSchedulerApplications(final Collection<ApplicationAttemptId> apps) {
        for (final FiCaSchedulerApp pendingApp : this.pendingApplications) {
            apps.add(pendingApp.getApplicationAttemptId());
        }
        for (final FiCaSchedulerApp app : this.activeApplications) {
            apps.add(app.getApplicationAttemptId());
        }
    }
    
    @Override
    public void attachContainer(final Resource clusterResource, final FiCaSchedulerApp application, final RMContainer rmContainer) {
        if (application != null) {
            this.allocateResource(clusterResource, application, rmContainer.getContainer().getResource(), this.labelManager.getLabelsOnNode(rmContainer.getContainer().getNodeId()));
            LeafQueue.LOG.info("movedContainer container=" + rmContainer.getContainer() + " resource=" + rmContainer.getContainer().getResource() + " queueMoveIn=" + this + " usedCapacity=" + this.getUsedCapacity() + " absoluteUsedCapacity=" + this.getAbsoluteUsedCapacity() + " used=" + this.usedResources + " cluster=" + clusterResource);
            this.getParent().attachContainer(clusterResource, application, rmContainer);
        }
    }
    
    @Override
    public void detachContainer(final Resource clusterResource, final FiCaSchedulerApp application, final RMContainer rmContainer) {
        if (application != null) {
            this.releaseResource(clusterResource, application, rmContainer.getContainer().getResource(), this.labelManager.getLabelsOnNode(rmContainer.getContainer().getNodeId()));
            LeafQueue.LOG.info("movedContainer container=" + rmContainer.getContainer() + " resource=" + rmContainer.getContainer().getResource() + " queueMoveOut=" + this + " usedCapacity=" + this.getUsedCapacity() + " absoluteUsedCapacity=" + this.getAbsoluteUsedCapacity() + " used=" + this.usedResources + " cluster=" + clusterResource);
            this.getParent().detachContainer(clusterResource, application, rmContainer);
        }
    }
    
    @Override
    public float getAbsActualCapacity() {
        if (Resources.lessThanOrEqual(this.resourceCalculator, this.lastClusterResource, this.lastClusterResource, Resources.none())) {
            return this.absoluteCapacity;
        }
        final Resource resourceRespectLabels = (this.labelManager == null) ? this.lastClusterResource : this.labelManager.getQueueResource(this.queueName, this.accessibleLabels, this.lastClusterResource);
        final float absActualCapacity = Resources.divide(this.resourceCalculator, this.lastClusterResource, resourceRespectLabels, this.lastClusterResource);
        return (absActualCapacity > this.absoluteCapacity) ? this.absoluteCapacity : absActualCapacity;
    }
    
    public void setCapacity(final float capacity) {
        this.capacity = capacity;
    }
    
    public void setAbsoluteCapacity(final float absoluteCapacity) {
        this.absoluteCapacity = absoluteCapacity;
    }
    
    public void setMaxApplications(final int maxApplications) {
        this.maxApplications = maxApplications;
    }
    
    static {
        LOG = LogFactory.getLog(LeafQueue.class);
        NULL_ASSIGNMENT = new CSAssignment(Resources.createResource(0, 0), NodeType.NODE_LOCAL);
        SKIP_ASSIGNMENT = new CSAssignment(true);
    }
    
    @VisibleForTesting
    public static class User
    {
        Resource consumed;
        Map<String, Resource> consumedByLabel;
        int pendingApplications;
        int activeApplications;
        
        public User() {
            this.consumed = Resources.createResource(0, 0);
            this.consumedByLabel = new HashMap<String, Resource>();
            this.pendingApplications = 0;
            this.activeApplications = 0;
        }
        
        public Resource getTotalConsumedResources() {
            return this.consumed;
        }
        
        public Resource getConsumedResourceByLabel(final String label) {
            final Resource r = this.consumedByLabel.get(label);
            if (null != r) {
                return r;
            }
            return Resources.none();
        }
        
        public int getPendingApplications() {
            return this.pendingApplications;
        }
        
        public int getActiveApplications() {
            return this.activeApplications;
        }
        
        public int getTotalApplications() {
            return this.getPendingApplications() + this.getActiveApplications();
        }
        
        public synchronized void submitApplication() {
            ++this.pendingApplications;
        }
        
        public synchronized void activateApplication() {
            --this.pendingApplications;
            ++this.activeApplications;
        }
        
        public synchronized void finishApplication(final boolean wasActive) {
            if (wasActive) {
                --this.activeApplications;
            }
            else {
                --this.pendingApplications;
            }
        }
        
        public synchronized void assignContainer(final Resource resource, final Set<String> nodeLabels) {
            Resources.addTo(this.consumed, resource);
            if (nodeLabels == null || nodeLabels.isEmpty()) {
                if (!this.consumedByLabel.containsKey("")) {
                    this.consumedByLabel.put("", Resources.createResource(0));
                }
                Resources.addTo(this.consumedByLabel.get(""), resource);
            }
            else {
                for (final String label : nodeLabels) {
                    if (!this.consumedByLabel.containsKey(label)) {
                        this.consumedByLabel.put(label, Resources.createResource(0));
                    }
                    Resources.addTo(this.consumedByLabel.get(label), resource);
                }
            }
        }
        
        public synchronized void releaseContainer(final Resource resource, final Set<String> nodeLabels) {
            Resources.subtractFrom(this.consumed, resource);
            if (nodeLabels == null || nodeLabels.isEmpty()) {
                if (!this.consumedByLabel.containsKey("")) {
                    this.consumedByLabel.put("", Resources.createResource(0));
                }
                Resources.subtractFrom(this.consumedByLabel.get(""), resource);
            }
            else {
                for (final String label : nodeLabels) {
                    if (!this.consumedByLabel.containsKey(label)) {
                        this.consumedByLabel.put(label, Resources.createResource(0));
                    }
                    Resources.subtractFrom(this.consumedByLabel.get(label), resource);
                }
            }
        }
    }
    
    static class QueueHeadroomInfo
    {
        private Resource queueMaxCap;
        private Resource clusterResource;
        
        public void setQueueMaxCap(final Resource queueMaxCap) {
            this.queueMaxCap = queueMaxCap;
        }
        
        public Resource getQueueMaxCap() {
            return this.queueMaxCap;
        }
        
        public void setClusterResource(final Resource clusterResource) {
            this.clusterResource = clusterResource;
        }
        
        public Resource getClusterResource() {
            return this.clusterResource;
        }
    }
}
