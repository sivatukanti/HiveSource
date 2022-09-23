// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.yarn.api.records.QueueACL;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import java.util.Collections;
import java.util.HashSet;
import com.google.common.collect.ImmutableSet;
import org.apache.hadoop.util.StringUtils;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.QueueState;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.commons.logging.Log;
import org.apache.hadoop.conf.Configuration;

public class CapacitySchedulerConfiguration extends Configuration
{
    private static final Log LOG;
    private static final String CS_CONFIGURATION_FILE = "capacity-scheduler.xml";
    @InterfaceAudience.Private
    public static final String PREFIX = "yarn.scheduler.capacity.";
    @InterfaceAudience.Private
    public static final String DOT = ".";
    @InterfaceAudience.Private
    public static final String MAXIMUM_APPLICATIONS_SUFFIX = "maximum-applications";
    @InterfaceAudience.Private
    public static final String MAXIMUM_SYSTEM_APPLICATIONS = "yarn.scheduler.capacity.maximum-applications";
    @InterfaceAudience.Private
    public static final String MAXIMUM_AM_RESOURCE_SUFFIX = "maximum-am-resource-percent";
    @InterfaceAudience.Private
    public static final String MAXIMUM_APPLICATION_MASTERS_RESOURCE_PERCENT = "yarn.scheduler.capacity.maximum-am-resource-percent";
    @InterfaceAudience.Private
    public static final String QUEUES = "queues";
    @InterfaceAudience.Private
    public static final String CAPACITY = "capacity";
    @InterfaceAudience.Private
    public static final String MAXIMUM_CAPACITY = "maximum-capacity";
    @InterfaceAudience.Private
    public static final String USER_LIMIT = "minimum-user-limit-percent";
    @InterfaceAudience.Private
    public static final String USER_LIMIT_FACTOR = "user-limit-factor";
    @InterfaceAudience.Private
    public static final String STATE = "state";
    @InterfaceAudience.Private
    public static final String ACCESSIBLE_NODE_LABELS = "accessible-node-labels";
    @InterfaceAudience.Private
    public static final String DEFAULT_NODE_LABEL_EXPRESSION = "default-node-label-expression";
    public static final String RESERVE_CONT_LOOK_ALL_NODES = "yarn.scheduler.capacity.reservations-continue-look-all-nodes";
    @InterfaceAudience.Private
    public static final boolean DEFAULT_RESERVE_CONT_LOOK_ALL_NODES = true;
    @InterfaceAudience.Private
    public static final int DEFAULT_MAXIMUM_SYSTEM_APPLICATIIONS = 10000;
    @InterfaceAudience.Private
    public static final float DEFAULT_MAXIMUM_APPLICATIONMASTERS_RESOURCE_PERCENT = 0.1f;
    @InterfaceAudience.Private
    public static final float UNDEFINED = -1.0f;
    @InterfaceAudience.Private
    public static final float MINIMUM_CAPACITY_VALUE = 0.0f;
    @InterfaceAudience.Private
    public static final float MAXIMUM_CAPACITY_VALUE = 100.0f;
    @InterfaceAudience.Private
    public static final float DEFAULT_MAXIMUM_CAPACITY_VALUE = -1.0f;
    @InterfaceAudience.Private
    public static final int DEFAULT_USER_LIMIT = 100;
    @InterfaceAudience.Private
    public static final float DEFAULT_USER_LIMIT_FACTOR = 1.0f;
    @InterfaceAudience.Private
    public static final String ALL_ACL = "*";
    @InterfaceAudience.Private
    public static final String NONE_ACL = " ";
    @InterfaceAudience.Private
    public static final String ENABLE_USER_METRICS = "yarn.scheduler.capacity.user-metrics.enable";
    @InterfaceAudience.Private
    public static final boolean DEFAULT_ENABLE_USER_METRICS = false;
    @InterfaceAudience.Private
    public static final String RESOURCE_CALCULATOR_CLASS = "yarn.scheduler.capacity.resource-calculator";
    @InterfaceAudience.Private
    public static final Class<? extends ResourceCalculator> DEFAULT_RESOURCE_CALCULATOR_CLASS;
    @InterfaceAudience.Private
    public static final String ROOT = "root";
    @InterfaceAudience.Private
    public static final String NODE_LOCALITY_DELAY = "yarn.scheduler.capacity.node-locality-delay";
    @InterfaceAudience.Private
    public static final int DEFAULT_NODE_LOCALITY_DELAY = -1;
    @InterfaceAudience.Private
    public static final String SCHEDULE_ASYNCHRONOUSLY_PREFIX = "yarn.scheduler.capacity.schedule-asynchronously";
    @InterfaceAudience.Private
    public static final String SCHEDULE_ASYNCHRONOUSLY_ENABLE = "yarn.scheduler.capacity.schedule-asynchronously.enable";
    @InterfaceAudience.Private
    public static final boolean DEFAULT_SCHEDULE_ASYNCHRONOUSLY_ENABLE = false;
    @InterfaceAudience.Private
    public static final String QUEUE_MAPPING = "yarn.scheduler.capacity.queue-mappings";
    @InterfaceAudience.Private
    public static final String ENABLE_QUEUE_MAPPING_OVERRIDE = "yarn.scheduler.capacity.queue-mappings-override.enable";
    @InterfaceAudience.Private
    public static final boolean DEFAULT_ENABLE_QUEUE_MAPPING_OVERRIDE = false;
    @InterfaceAudience.Private
    public static final String AVERAGE_CAPACITY = "average-capacity";
    @InterfaceAudience.Private
    public static final String IS_RESERVABLE = "reservable";
    @InterfaceAudience.Private
    public static final String RESERVATION_WINDOW = "reservation-window";
    @InterfaceAudience.Private
    public static final String INSTANTANEOUS_MAX_CAPACITY = "instantaneous-max-capacity";
    @InterfaceAudience.Private
    public static final long DEFAULT_RESERVATION_WINDOW = 86400000L;
    @InterfaceAudience.Private
    public static final String RESERVATION_ADMISSION_POLICY = "reservation-policy";
    @InterfaceAudience.Private
    public static final String RESERVATION_AGENT_NAME = "reservation-agent";
    @InterfaceAudience.Private
    public static final String RESERVATION_SHOW_RESERVATION_AS_QUEUE = "show-reservations-as-queues";
    @InterfaceAudience.Private
    public static final String DEFAULT_RESERVATION_ADMISSION_POLICY = "org.apache.hadoop.yarn.server.resourcemanager.reservation.CapacityOverTimePolicy";
    @InterfaceAudience.Private
    public static final String DEFAULT_RESERVATION_AGENT_NAME = "org.apache.hadoop.yarn.server.resourcemanager.reservation.GreedyReservationAgent";
    @InterfaceAudience.Private
    public static final String RESERVATION_PLANNER_NAME = "reservation-planner";
    @InterfaceAudience.Private
    public static final String DEFAULT_RESERVATION_PLANNER_NAME = "org.apache.hadoop.yarn.server.resourcemanager.reservation.SimpleCapacityReplanner";
    @InterfaceAudience.Private
    public static final String RESERVATION_MOVE_ON_EXPIRY = "reservation-move-on-expiry";
    @InterfaceAudience.Private
    public static final boolean DEFAULT_RESERVATION_MOVE_ON_EXPIRY = true;
    @InterfaceAudience.Private
    public static final String RESERVATION_ENFORCEMENT_WINDOW = "reservation-enforcement-window";
    @InterfaceAudience.Private
    public static final long DEFAULT_RESERVATION_ENFORCEMENT_WINDOW = 3600000L;
    
    public CapacitySchedulerConfiguration() {
        this(new Configuration());
    }
    
    public CapacitySchedulerConfiguration(final Configuration configuration) {
        this(configuration, true);
    }
    
    public CapacitySchedulerConfiguration(final Configuration configuration, final boolean useLocalConfigurationProvider) {
        super(configuration);
        if (useLocalConfigurationProvider) {
            this.addResource("capacity-scheduler.xml");
        }
    }
    
    private String getQueuePrefix(final String queue) {
        final String queueName = "yarn.scheduler.capacity." + queue + ".";
        return queueName;
    }
    
    private String getNodeLabelPrefix(final String queue, final String label) {
        return this.getQueuePrefix(queue) + "accessible-node-labels" + "." + label + ".";
    }
    
    public int getMaximumSystemApplications() {
        final int maxApplications = this.getInt("yarn.scheduler.capacity.maximum-applications", 10000);
        return maxApplications;
    }
    
    public float getMaximumApplicationMasterResourcePercent() {
        return this.getFloat("yarn.scheduler.capacity.maximum-am-resource-percent", 0.1f);
    }
    
    public int getMaximumApplicationsPerQueue(final String queue) {
        final int maxApplicationsPerQueue = this.getInt(this.getQueuePrefix(queue) + "maximum-applications", -1);
        return maxApplicationsPerQueue;
    }
    
    public float getMaximumApplicationMasterResourcePerQueuePercent(final String queue) {
        return this.getFloat(this.getQueuePrefix(queue) + "maximum-am-resource-percent", this.getMaximumApplicationMasterResourcePercent());
    }
    
    public float getCapacity(final String queue) {
        final float capacity = queue.equals("root") ? 100.0f : this.getFloat(this.getQueuePrefix(queue) + "capacity", -1.0f);
        if (capacity < 0.0f || capacity > 100.0f) {
            throw new IllegalArgumentException("Illegal capacity of " + capacity + " for queue " + queue);
        }
        CapacitySchedulerConfiguration.LOG.debug("CSConf - getCapacity: queuePrefix=" + this.getQueuePrefix(queue) + ", capacity=" + capacity);
        return capacity;
    }
    
    public void setCapacity(final String queue, final float capacity) {
        if (queue.equals("root")) {
            throw new IllegalArgumentException("Cannot set capacity, root queue has a fixed capacity of 100.0f");
        }
        this.setFloat(this.getQueuePrefix(queue) + "capacity", capacity);
        CapacitySchedulerConfiguration.LOG.debug("CSConf - setCapacity: queuePrefix=" + this.getQueuePrefix(queue) + ", capacity=" + capacity);
    }
    
    public float getMaximumCapacity(final String queue) {
        float maxCapacity = this.getFloat(this.getQueuePrefix(queue) + "maximum-capacity", 100.0f);
        maxCapacity = ((maxCapacity == -1.0f) ? 100.0f : maxCapacity);
        return maxCapacity;
    }
    
    public void setMaximumCapacity(final String queue, final float maxCapacity) {
        if (maxCapacity > 100.0f) {
            throw new IllegalArgumentException("Illegal maximum-capacity of " + maxCapacity + " for queue " + queue);
        }
        this.setFloat(this.getQueuePrefix(queue) + "maximum-capacity", maxCapacity);
        CapacitySchedulerConfiguration.LOG.debug("CSConf - setMaxCapacity: queuePrefix=" + this.getQueuePrefix(queue) + ", maxCapacity=" + maxCapacity);
    }
    
    public void setCapacityByLabel(final String queue, final String label, final float capacity) {
        this.setFloat(this.getNodeLabelPrefix(queue, label) + "capacity", capacity);
    }
    
    public void setMaximumCapacityByLabel(final String queue, final String label, final float capacity) {
        this.setFloat(this.getNodeLabelPrefix(queue, label) + "maximum-capacity", capacity);
    }
    
    public int getUserLimit(final String queue) {
        final int userLimit = this.getInt(this.getQueuePrefix(queue) + "minimum-user-limit-percent", 100);
        return userLimit;
    }
    
    public void setUserLimit(final String queue, final int userLimit) {
        this.setInt(this.getQueuePrefix(queue) + "minimum-user-limit-percent", userLimit);
        CapacitySchedulerConfiguration.LOG.debug("here setUserLimit: queuePrefix=" + this.getQueuePrefix(queue) + ", userLimit=" + this.getUserLimit(queue));
    }
    
    public float getUserLimitFactor(final String queue) {
        final float userLimitFactor = this.getFloat(this.getQueuePrefix(queue) + "user-limit-factor", 1.0f);
        return userLimitFactor;
    }
    
    public void setUserLimitFactor(final String queue, final float userLimitFactor) {
        this.setFloat(this.getQueuePrefix(queue) + "user-limit-factor", userLimitFactor);
    }
    
    public QueueState getState(final String queue) {
        final String state = this.get(this.getQueuePrefix(queue) + "state");
        return (state != null) ? QueueState.valueOf(state.toUpperCase()) : QueueState.RUNNING;
    }
    
    public void setAccessibleNodeLabels(final String queue, final Set<String> labels) {
        if (labels == null) {
            return;
        }
        final String str = StringUtils.join(",", labels);
        this.set(this.getQueuePrefix(queue) + "accessible-node-labels", str);
    }
    
    public Set<String> getAccessibleNodeLabels(final String queue) {
        final String accessibleLabelStr = this.get(this.getQueuePrefix(queue) + "accessible-node-labels");
        if (accessibleLabelStr == null) {
            if (!queue.equals("root")) {
                return null;
            }
        }
        else if (queue.equals("root")) {
            CapacitySchedulerConfiguration.LOG.warn("Accessible node labels for root queue will be ignored, it will be automatically set to \"*\".");
        }
        if (queue.equals("root")) {
            return ImmutableSet.of("*");
        }
        final Set<String> set = new HashSet<String>();
        for (final String str : accessibleLabelStr.split(",")) {
            if (!str.trim().isEmpty()) {
                set.add(str.trim());
            }
        }
        if (set.contains("*")) {
            set.clear();
            set.add("*");
        }
        return Collections.unmodifiableSet((Set<? extends String>)set);
    }
    
    public Map<String, Float> getNodeLabelCapacities(final String queue, final Set<String> labels, final RMNodeLabelsManager mgr) {
        final Map<String, Float> nodeLabelCapacities = new HashMap<String, Float>();
        if (labels == null) {
            return nodeLabelCapacities;
        }
        for (final String label : labels.contains("*") ? mgr.getClusterNodeLabels() : labels) {
            final String capacityPropertyName = this.getNodeLabelPrefix(queue, label) + "capacity";
            final float capacity = this.getFloat(capacityPropertyName, 0.0f);
            if (capacity < 0.0f || capacity > 100.0f) {
                throw new IllegalArgumentException("Illegal capacity of " + capacity + " for node-label=" + label + " in queue=" + queue + ", valid capacity should in range of [0, 100].");
            }
            if (CapacitySchedulerConfiguration.LOG.isDebugEnabled()) {
                CapacitySchedulerConfiguration.LOG.debug("CSConf - getCapacityOfLabel: prefix=" + this.getNodeLabelPrefix(queue, label) + ", capacity=" + capacity);
            }
            nodeLabelCapacities.put(label, capacity / 100.0f);
        }
        return nodeLabelCapacities;
    }
    
    public Map<String, Float> getMaximumNodeLabelCapacities(final String queue, final Set<String> labels, final RMNodeLabelsManager mgr) {
        final Map<String, Float> maximumNodeLabelCapacities = new HashMap<String, Float>();
        if (labels == null) {
            return maximumNodeLabelCapacities;
        }
        for (final String label : labels.contains("*") ? mgr.getClusterNodeLabels() : labels) {
            final float maxCapacity = this.getFloat(this.getNodeLabelPrefix(queue, label) + "maximum-capacity", 100.0f);
            if (maxCapacity < 0.0f || maxCapacity > 100.0f) {
                throw new IllegalArgumentException("Illegal capacity of " + maxCapacity + " for label=" + label + " in queue=" + queue);
            }
            CapacitySchedulerConfiguration.LOG.debug("CSConf - getCapacityOfLabel: prefix=" + this.getNodeLabelPrefix(queue, label) + ", capacity=" + maxCapacity);
            maximumNodeLabelCapacities.put(label, maxCapacity / 100.0f);
        }
        return maximumNodeLabelCapacities;
    }
    
    public String getDefaultNodeLabelExpression(final String queue) {
        return this.get(this.getQueuePrefix(queue) + "default-node-label-expression");
    }
    
    public void setDefaultNodeLabelExpression(final String queue, final String exp) {
        this.set(this.getQueuePrefix(queue) + "default-node-label-expression", exp);
    }
    
    public boolean getReservationContinueLook() {
        return this.getBoolean("yarn.scheduler.capacity.reservations-continue-look-all-nodes", true);
    }
    
    private static String getAclKey(final QueueACL acl) {
        return "acl_" + acl.toString().toLowerCase();
    }
    
    public AccessControlList getAcl(final String queue, final QueueACL acl) {
        final String queuePrefix = this.getQueuePrefix(queue);
        final String defaultAcl = queue.equals("root") ? "*" : " ";
        final String aclString = this.get(queuePrefix + getAclKey(acl), defaultAcl);
        return new AccessControlList(aclString);
    }
    
    public void setAcl(final String queue, final QueueACL acl, final String aclString) {
        final String queuePrefix = this.getQueuePrefix(queue);
        this.set(queuePrefix + getAclKey(acl), aclString);
    }
    
    public Map<QueueACL, AccessControlList> getAcls(final String queue) {
        final Map<QueueACL, AccessControlList> acls = new HashMap<QueueACL, AccessControlList>();
        for (final QueueACL acl : QueueACL.values()) {
            acls.put(acl, this.getAcl(queue, acl));
        }
        return acls;
    }
    
    public void setAcls(final String queue, final Map<QueueACL, AccessControlList> acls) {
        for (final Map.Entry<QueueACL, AccessControlList> e : acls.entrySet()) {
            this.setAcl(queue, e.getKey(), e.getValue().getAclString());
        }
    }
    
    public String[] getQueues(final String queue) {
        CapacitySchedulerConfiguration.LOG.debug("CSConf - getQueues called for: queuePrefix=" + this.getQueuePrefix(queue));
        final String[] queues = this.getStrings(this.getQueuePrefix(queue) + "queues");
        CapacitySchedulerConfiguration.LOG.debug("CSConf - getQueues: queuePrefix=" + this.getQueuePrefix(queue) + ", queues=" + ((queues == null) ? "" : StringUtils.arrayToString(queues)));
        return queues;
    }
    
    public void setQueues(final String queue, final String[] subQueues) {
        this.set(this.getQueuePrefix(queue) + "queues", StringUtils.arrayToString(subQueues));
        CapacitySchedulerConfiguration.LOG.debug("CSConf - setQueues: qPrefix=" + this.getQueuePrefix(queue) + ", queues=" + StringUtils.arrayToString(subQueues));
    }
    
    public org.apache.hadoop.yarn.api.records.Resource getMinimumAllocation() {
        final int minimumMemory = this.getInt("yarn.scheduler.minimum-allocation-mb", 1024);
        final int minimumCores = this.getInt("yarn.scheduler.minimum-allocation-vcores", 1);
        return Resources.createResource(minimumMemory, minimumCores);
    }
    
    public org.apache.hadoop.yarn.api.records.Resource getMaximumAllocation() {
        final int maximumMemory = this.getInt("yarn.scheduler.maximum-allocation-mb", 8192);
        final int maximumCores = this.getInt("yarn.scheduler.maximum-allocation-vcores", 4);
        return Resources.createResource(maximumMemory, maximumCores);
    }
    
    public boolean getEnableUserMetrics() {
        return this.getBoolean("yarn.scheduler.capacity.user-metrics.enable", false);
    }
    
    public int getNodeLocalityDelay() {
        final int delay = this.getInt("yarn.scheduler.capacity.node-locality-delay", -1);
        return (delay == -1) ? 0 : delay;
    }
    
    public ResourceCalculator getResourceCalculator() {
        return ReflectionUtils.newInstance(this.getClass("yarn.scheduler.capacity.resource-calculator", CapacitySchedulerConfiguration.DEFAULT_RESOURCE_CALCULATOR_CLASS, ResourceCalculator.class), this);
    }
    
    public boolean getUsePortForNodeName() {
        return this.getBoolean("yarn.scheduler.include-port-in-node-name", false);
    }
    
    public void setResourceComparator(final Class<? extends ResourceCalculator> resourceCalculatorClass) {
        this.setClass("yarn.scheduler.capacity.resource-calculator", resourceCalculatorClass, ResourceCalculator.class);
    }
    
    public boolean getScheduleAynschronously() {
        return this.getBoolean("yarn.scheduler.capacity.schedule-asynchronously.enable", false);
    }
    
    public void setScheduleAynschronously(final boolean async) {
        this.setBoolean("yarn.scheduler.capacity.schedule-asynchronously.enable", async);
    }
    
    public boolean getOverrideWithQueueMappings() {
        return this.getBoolean("yarn.scheduler.capacity.queue-mappings-override.enable", false);
    }
    
    private static Collection<String> getTrimmedStringCollection(final String str, final String delim) {
        final List<String> values = new ArrayList<String>();
        if (str == null) {
            return values;
        }
        final StringTokenizer tokenizer = new StringTokenizer(str, delim);
        while (tokenizer.hasMoreTokens()) {
            final String next = tokenizer.nextToken();
            if (next != null) {
                if (next.trim().isEmpty()) {
                    continue;
                }
                values.add(next.trim());
            }
        }
        return values;
    }
    
    public List<QueueMapping> getQueueMappings() {
        final List<QueueMapping> mappings = new ArrayList<QueueMapping>();
        final Collection<String> mappingsString = this.getTrimmedStringCollection("yarn.scheduler.capacity.queue-mappings");
        for (final String mappingValue : mappingsString) {
            final String[] mapping = getTrimmedStringCollection(mappingValue, ":").toArray(new String[0]);
            if (mapping.length != 3 || mapping[1].length() == 0 || mapping[2].length() == 0) {
                throw new IllegalArgumentException("Illegal queue mapping " + mappingValue);
            }
            QueueMapping m;
            try {
                QueueMapping.MappingType mappingType;
                if (mapping[0].equals("u")) {
                    mappingType = QueueMapping.MappingType.USER;
                }
                else {
                    if (!mapping[0].equals("g")) {
                        throw new IllegalArgumentException("unknown mapping prefix " + mapping[0]);
                    }
                    mappingType = QueueMapping.MappingType.GROUP;
                }
                m = new QueueMapping(mappingType, mapping[1], mapping[2]);
            }
            catch (Throwable t) {
                throw new IllegalArgumentException("Illegal queue mapping " + mappingValue);
            }
            if (m == null) {
                continue;
            }
            mappings.add(m);
        }
        return mappings;
    }
    
    public boolean isReservable(final String queue) {
        final boolean isReservable = this.getBoolean(this.getQueuePrefix(queue) + "reservable", false);
        return isReservable;
    }
    
    public void setReservable(final String queue, final boolean isReservable) {
        this.setBoolean(this.getQueuePrefix(queue) + "reservable", isReservable);
        CapacitySchedulerConfiguration.LOG.debug("here setReservableQueue: queuePrefix=" + this.getQueuePrefix(queue) + ", isReservableQueue=" + this.isReservable(queue));
    }
    
    public long getReservationWindow(final String queue) {
        final long reservationWindow = this.getLong(this.getQueuePrefix(queue) + "reservation-window", 86400000L);
        return reservationWindow;
    }
    
    public float getAverageCapacity(final String queue) {
        final float avgCapacity = this.getFloat(this.getQueuePrefix(queue) + "average-capacity", 100.0f);
        return avgCapacity;
    }
    
    public float getInstantaneousMaxCapacity(final String queue) {
        final float instMaxCapacity = this.getFloat(this.getQueuePrefix(queue) + "instantaneous-max-capacity", 100.0f);
        return instMaxCapacity;
    }
    
    public void setInstantaneousMaxCapacity(final String queue, final float instMaxCapacity) {
        this.setFloat(this.getQueuePrefix(queue) + "instantaneous-max-capacity", instMaxCapacity);
    }
    
    public void setReservationWindow(final String queue, final long reservationWindow) {
        this.setLong(this.getQueuePrefix(queue) + "reservation-window", reservationWindow);
    }
    
    public void setAverageCapacity(final String queue, final float avgCapacity) {
        this.setFloat(this.getQueuePrefix(queue) + "average-capacity", avgCapacity);
    }
    
    public String getReservationAdmissionPolicy(final String queue) {
        final String reservationPolicy = this.get(this.getQueuePrefix(queue) + "reservation-policy", "org.apache.hadoop.yarn.server.resourcemanager.reservation.CapacityOverTimePolicy");
        return reservationPolicy;
    }
    
    public void setReservationAdmissionPolicy(final String queue, final String reservationPolicy) {
        this.set(this.getQueuePrefix(queue) + "reservation-policy", reservationPolicy);
    }
    
    public String getReservationAgent(final String queue) {
        final String reservationAgent = this.get(this.getQueuePrefix(queue) + "reservation-agent", "org.apache.hadoop.yarn.server.resourcemanager.reservation.GreedyReservationAgent");
        return reservationAgent;
    }
    
    public void setReservationAgent(final String queue, final String reservationPolicy) {
        this.set(this.getQueuePrefix(queue) + "reservation-agent", reservationPolicy);
    }
    
    public boolean getShowReservationAsQueues(final String queuePath) {
        final boolean showReservationAsQueues = this.getBoolean(this.getQueuePrefix(queuePath) + "show-reservations-as-queues", false);
        return showReservationAsQueues;
    }
    
    public String getReplanner(final String queue) {
        final String replanner = this.get(this.getQueuePrefix(queue) + "reservation-planner", "org.apache.hadoop.yarn.server.resourcemanager.reservation.SimpleCapacityReplanner");
        return replanner;
    }
    
    public boolean getMoveOnExpiry(final String queue) {
        final boolean killOnExpiry = this.getBoolean(this.getQueuePrefix(queue) + "reservation-move-on-expiry", true);
        return killOnExpiry;
    }
    
    public long getEnforcementWindow(final String queue) {
        final long enforcementWindow = this.getLong(this.getQueuePrefix(queue) + "reservation-enforcement-window", 3600000L);
        return enforcementWindow;
    }
    
    static {
        LOG = LogFactory.getLog(CapacitySchedulerConfiguration.class);
        DEFAULT_RESOURCE_CALCULATOR_CLASS = DefaultResourceCalculator.class;
    }
    
    @InterfaceAudience.Private
    public static class QueueMapping
    {
        MappingType type;
        String source;
        String queue;
        
        public QueueMapping(final MappingType type, final String source, final String queue) {
            this.type = type;
            this.source = source;
            this.queue = queue;
        }
        
        public enum MappingType
        {
            USER("u"), 
            GROUP("g");
            
            private final String type;
            
            private MappingType(final String type) {
                this.type = type;
            }
            
            @Override
            public String toString() {
                return this.type;
            }
        }
    }
}
