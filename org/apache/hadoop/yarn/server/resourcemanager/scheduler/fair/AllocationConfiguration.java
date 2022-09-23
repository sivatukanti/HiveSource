// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.util.resource.Resources;
import java.util.HashSet;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.server.resourcemanager.resource.ResourceWeights;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.Map;
import org.apache.hadoop.security.authorize.AccessControlList;

public class AllocationConfiguration
{
    private static final AccessControlList EVERYBODY_ACL;
    private static final AccessControlList NOBODY_ACL;
    private final Map<String, Resource> minQueueResources;
    @VisibleForTesting
    final Map<String, Resource> maxQueueResources;
    private final Map<String, ResourceWeights> queueWeights;
    @VisibleForTesting
    final Map<String, Integer> queueMaxApps;
    @VisibleForTesting
    final Map<String, Integer> userMaxApps;
    private final int userMaxAppsDefault;
    private final int queueMaxAppsDefault;
    final Map<String, Float> queueMaxAMShares;
    private final float queueMaxAMShareDefault;
    private final Map<String, Map<QueueACL, AccessControlList>> queueAcls;
    private final Map<String, Long> minSharePreemptionTimeouts;
    private final Map<String, Long> fairSharePreemptionTimeouts;
    private final Map<String, Float> fairSharePreemptionThresholds;
    private final Map<String, SchedulingPolicy> schedulingPolicies;
    private final SchedulingPolicy defaultSchedulingPolicy;
    @VisibleForTesting
    QueuePlacementPolicy placementPolicy;
    @VisibleForTesting
    Map<FSQueueType, Set<String>> configuredQueues;
    
    public AllocationConfiguration(final Map<String, Resource> minQueueResources, final Map<String, Resource> maxQueueResources, final Map<String, Integer> queueMaxApps, final Map<String, Integer> userMaxApps, final Map<String, ResourceWeights> queueWeights, final Map<String, Float> queueMaxAMShares, final int userMaxAppsDefault, final int queueMaxAppsDefault, final float queueMaxAMShareDefault, final Map<String, SchedulingPolicy> schedulingPolicies, final SchedulingPolicy defaultSchedulingPolicy, final Map<String, Long> minSharePreemptionTimeouts, final Map<String, Long> fairSharePreemptionTimeouts, final Map<String, Float> fairSharePreemptionThresholds, final Map<String, Map<QueueACL, AccessControlList>> queueAcls, final QueuePlacementPolicy placementPolicy, final Map<FSQueueType, Set<String>> configuredQueues) {
        this.minQueueResources = minQueueResources;
        this.maxQueueResources = maxQueueResources;
        this.queueMaxApps = queueMaxApps;
        this.userMaxApps = userMaxApps;
        this.queueMaxAMShares = queueMaxAMShares;
        this.queueWeights = queueWeights;
        this.userMaxAppsDefault = userMaxAppsDefault;
        this.queueMaxAppsDefault = queueMaxAppsDefault;
        this.queueMaxAMShareDefault = queueMaxAMShareDefault;
        this.defaultSchedulingPolicy = defaultSchedulingPolicy;
        this.schedulingPolicies = schedulingPolicies;
        this.minSharePreemptionTimeouts = minSharePreemptionTimeouts;
        this.fairSharePreemptionTimeouts = fairSharePreemptionTimeouts;
        this.fairSharePreemptionThresholds = fairSharePreemptionThresholds;
        this.queueAcls = queueAcls;
        this.placementPolicy = placementPolicy;
        this.configuredQueues = configuredQueues;
    }
    
    public AllocationConfiguration(final Configuration conf) {
        this.minQueueResources = new HashMap<String, Resource>();
        this.maxQueueResources = new HashMap<String, Resource>();
        this.queueWeights = new HashMap<String, ResourceWeights>();
        this.queueMaxApps = new HashMap<String, Integer>();
        this.userMaxApps = new HashMap<String, Integer>();
        this.queueMaxAMShares = new HashMap<String, Float>();
        this.userMaxAppsDefault = Integer.MAX_VALUE;
        this.queueMaxAppsDefault = Integer.MAX_VALUE;
        this.queueMaxAMShareDefault = 0.5f;
        this.queueAcls = new HashMap<String, Map<QueueACL, AccessControlList>>();
        this.minSharePreemptionTimeouts = new HashMap<String, Long>();
        this.fairSharePreemptionTimeouts = new HashMap<String, Long>();
        this.fairSharePreemptionThresholds = new HashMap<String, Float>();
        this.schedulingPolicies = new HashMap<String, SchedulingPolicy>();
        this.defaultSchedulingPolicy = SchedulingPolicy.DEFAULT_POLICY;
        this.configuredQueues = new HashMap<FSQueueType, Set<String>>();
        for (final FSQueueType queueType : FSQueueType.values()) {
            this.configuredQueues.put(queueType, new HashSet<String>());
        }
        this.placementPolicy = QueuePlacementPolicy.fromConfiguration(conf, this.configuredQueues);
    }
    
    public AccessControlList getQueueAcl(final String queue, final QueueACL operation) {
        final Map<QueueACL, AccessControlList> queueAcls = this.queueAcls.get(queue);
        if (queueAcls != null) {
            final AccessControlList operationAcl = queueAcls.get(operation);
            if (operationAcl != null) {
                return operationAcl;
            }
        }
        return queue.equals("root") ? AllocationConfiguration.EVERYBODY_ACL : AllocationConfiguration.NOBODY_ACL;
    }
    
    public long getMinSharePreemptionTimeout(final String queueName) {
        final Long minSharePreemptionTimeout = this.minSharePreemptionTimeouts.get(queueName);
        return (minSharePreemptionTimeout == null) ? -1L : minSharePreemptionTimeout;
    }
    
    public long getFairSharePreemptionTimeout(final String queueName) {
        final Long fairSharePreemptionTimeout = this.fairSharePreemptionTimeouts.get(queueName);
        return (fairSharePreemptionTimeout == null) ? -1L : fairSharePreemptionTimeout;
    }
    
    public float getFairSharePreemptionThreshold(final String queueName) {
        final Float fairSharePreemptionThreshold = this.fairSharePreemptionThresholds.get(queueName);
        return (fairSharePreemptionThreshold == null) ? -1.0f : fairSharePreemptionThreshold;
    }
    
    public ResourceWeights getQueueWeight(final String queue) {
        final ResourceWeights weight = this.queueWeights.get(queue);
        return (weight == null) ? ResourceWeights.NEUTRAL : weight;
    }
    
    public int getUserMaxApps(final String user) {
        final Integer maxApps = this.userMaxApps.get(user);
        return (maxApps == null) ? this.userMaxAppsDefault : maxApps;
    }
    
    public int getQueueMaxApps(final String queue) {
        final Integer maxApps = this.queueMaxApps.get(queue);
        return (maxApps == null) ? this.queueMaxAppsDefault : maxApps;
    }
    
    public float getQueueMaxAMShare(final String queue) {
        final Float maxAMShare = this.queueMaxAMShares.get(queue);
        return (maxAMShare == null) ? this.queueMaxAMShareDefault : maxAMShare;
    }
    
    public Resource getMinResources(final String queue) {
        final Resource minQueueResource = this.minQueueResources.get(queue);
        return (minQueueResource == null) ? Resources.none() : minQueueResource;
    }
    
    public Resource getMaxResources(final String queueName) {
        final Resource maxQueueResource = this.maxQueueResources.get(queueName);
        return (maxQueueResource == null) ? Resources.unbounded() : maxQueueResource;
    }
    
    public boolean hasAccess(final String queueName, final QueueACL acl, final UserGroupInformation user) {
        for (int lastPeriodIndex = queueName.length(); lastPeriodIndex != -1; lastPeriodIndex = queueName.lastIndexOf(46, lastPeriodIndex - 1)) {
            final String queue = queueName.substring(0, lastPeriodIndex);
            if (this.getQueueAcl(queue, acl).isUserAllowed(user)) {
                return true;
            }
        }
        return false;
    }
    
    public SchedulingPolicy getSchedulingPolicy(final String queueName) {
        final SchedulingPolicy policy = this.schedulingPolicies.get(queueName);
        return (policy == null) ? this.defaultSchedulingPolicy : policy;
    }
    
    public SchedulingPolicy getDefaultSchedulingPolicy() {
        return this.defaultSchedulingPolicy;
    }
    
    public Map<FSQueueType, Set<String>> getConfiguredQueues() {
        return this.configuredQueues;
    }
    
    public QueuePlacementPolicy getPlacementPolicy() {
        return this.placementPolicy;
    }
    
    static {
        EVERYBODY_ACL = new AccessControlList("*");
        NOBODY_ACL = new AccessControlList(" ");
    }
}
