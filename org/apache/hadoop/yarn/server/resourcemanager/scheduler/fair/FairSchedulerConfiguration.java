// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.commons.logging.LogFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import java.io.File;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configuration;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class FairSchedulerConfiguration extends Configuration
{
    public static final Log LOG;
    public static final String RM_SCHEDULER_INCREMENT_ALLOCATION_MB = "yarn.scheduler.increment-allocation-mb";
    public static final int DEFAULT_RM_SCHEDULER_INCREMENT_ALLOCATION_MB = 1024;
    public static final String RM_SCHEDULER_INCREMENT_ALLOCATION_VCORES = "yarn.scheduler.increment-allocation-vcores";
    public static final int DEFAULT_RM_SCHEDULER_INCREMENT_ALLOCATION_VCORES = 1;
    private static final String CONF_PREFIX = "yarn.scheduler.fair.";
    public static final String ALLOCATION_FILE = "yarn.scheduler.fair.allocation.file";
    protected static final String DEFAULT_ALLOCATION_FILE = "fair-scheduler.xml";
    public static final String EVENT_LOG_ENABLED = "yarn.scheduler.fair.event-log-enabled";
    public static final boolean DEFAULT_EVENT_LOG_ENABLED = false;
    protected static final String EVENT_LOG_DIR = "eventlog.dir";
    protected static final String ALLOW_UNDECLARED_POOLS = "yarn.scheduler.fair.allow-undeclared-pools";
    protected static final boolean DEFAULT_ALLOW_UNDECLARED_POOLS = true;
    protected static final String USER_AS_DEFAULT_QUEUE = "yarn.scheduler.fair.user-as-default-queue";
    protected static final boolean DEFAULT_USER_AS_DEFAULT_QUEUE = true;
    protected static final float DEFAULT_LOCALITY_THRESHOLD = -1.0f;
    protected static final String LOCALITY_THRESHOLD_NODE = "yarn.scheduler.fair.locality.threshold.node";
    protected static final float DEFAULT_LOCALITY_THRESHOLD_NODE = -1.0f;
    protected static final String LOCALITY_THRESHOLD_RACK = "yarn.scheduler.fair.locality.threshold.rack";
    protected static final float DEFAULT_LOCALITY_THRESHOLD_RACK = -1.0f;
    protected static final String LOCALITY_DELAY_NODE_MS = "yarn.scheduler.fair.locality-delay-node-ms";
    protected static final long DEFAULT_LOCALITY_DELAY_NODE_MS = -1L;
    protected static final String LOCALITY_DELAY_RACK_MS = "yarn.scheduler.fair.locality-delay-rack-ms";
    protected static final long DEFAULT_LOCALITY_DELAY_RACK_MS = -1L;
    protected static final String CONTINUOUS_SCHEDULING_ENABLED = "yarn.scheduler.fair.continuous-scheduling-enabled";
    protected static final boolean DEFAULT_CONTINUOUS_SCHEDULING_ENABLED = false;
    protected static final String CONTINUOUS_SCHEDULING_SLEEP_MS = "yarn.scheduler.fair.continuous-scheduling-sleep-ms";
    protected static final int DEFAULT_CONTINUOUS_SCHEDULING_SLEEP_MS = 5;
    protected static final String PREEMPTION = "yarn.scheduler.fair.preemption";
    protected static final boolean DEFAULT_PREEMPTION = false;
    protected static final String PREEMPTION_THRESHOLD = "yarn.scheduler.fair.preemption.cluster-utilization-threshold";
    protected static final float DEFAULT_PREEMPTION_THRESHOLD = 0.8f;
    protected static final String PREEMPTION_INTERVAL = "yarn.scheduler.fair.preemptionInterval";
    protected static final int DEFAULT_PREEMPTION_INTERVAL = 5000;
    protected static final String WAIT_TIME_BEFORE_KILL = "yarn.scheduler.fair.waitTimeBeforeKill";
    protected static final int DEFAULT_WAIT_TIME_BEFORE_KILL = 15000;
    public static final String ASSIGN_MULTIPLE = "yarn.scheduler.fair.assignmultiple";
    protected static final boolean DEFAULT_ASSIGN_MULTIPLE = false;
    protected static final String SIZE_BASED_WEIGHT = "yarn.scheduler.fair.sizebasedweight";
    protected static final boolean DEFAULT_SIZE_BASED_WEIGHT = false;
    protected static final String MAX_ASSIGN = "yarn.scheduler.fair.max.assign";
    protected static final int DEFAULT_MAX_ASSIGN = -1;
    public static final String UPDATE_INTERVAL_MS = "yarn.scheduler.fair.update-interval-ms";
    public static final int DEFAULT_UPDATE_INTERVAL_MS = 500;
    
    public FairSchedulerConfiguration() {
    }
    
    public FairSchedulerConfiguration(final Configuration conf) {
        super(conf);
    }
    
    public org.apache.hadoop.yarn.api.records.Resource getMinimumAllocation() {
        final int mem = this.getInt("yarn.scheduler.minimum-allocation-mb", 1024);
        final int cpu = this.getInt("yarn.scheduler.minimum-allocation-vcores", 1);
        return Resources.createResource(mem, cpu);
    }
    
    public org.apache.hadoop.yarn.api.records.Resource getMaximumAllocation() {
        final int mem = this.getInt("yarn.scheduler.maximum-allocation-mb", 8192);
        final int cpu = this.getInt("yarn.scheduler.maximum-allocation-vcores", 4);
        return Resources.createResource(mem, cpu);
    }
    
    public org.apache.hadoop.yarn.api.records.Resource getIncrementAllocation() {
        final int incrementMemory = this.getInt("yarn.scheduler.increment-allocation-mb", 1024);
        final int incrementCores = this.getInt("yarn.scheduler.increment-allocation-vcores", 1);
        return Resources.createResource(incrementMemory, incrementCores);
    }
    
    public float getLocalityThresholdNode() {
        return this.getFloat("yarn.scheduler.fair.locality.threshold.node", -1.0f);
    }
    
    public float getLocalityThresholdRack() {
        return this.getFloat("yarn.scheduler.fair.locality.threshold.rack", -1.0f);
    }
    
    public boolean isContinuousSchedulingEnabled() {
        return this.getBoolean("yarn.scheduler.fair.continuous-scheduling-enabled", false);
    }
    
    public int getContinuousSchedulingSleepMs() {
        return this.getInt("yarn.scheduler.fair.continuous-scheduling-sleep-ms", 5);
    }
    
    public long getLocalityDelayNodeMs() {
        return this.getLong("yarn.scheduler.fair.locality-delay-node-ms", -1L);
    }
    
    public long getLocalityDelayRackMs() {
        return this.getLong("yarn.scheduler.fair.locality-delay-rack-ms", -1L);
    }
    
    public boolean getPreemptionEnabled() {
        return this.getBoolean("yarn.scheduler.fair.preemption", false);
    }
    
    public float getPreemptionUtilizationThreshold() {
        return this.getFloat("yarn.scheduler.fair.preemption.cluster-utilization-threshold", 0.8f);
    }
    
    public boolean getAssignMultiple() {
        return this.getBoolean("yarn.scheduler.fair.assignmultiple", false);
    }
    
    public int getMaxAssign() {
        return this.getInt("yarn.scheduler.fair.max.assign", -1);
    }
    
    public boolean getSizeBasedWeight() {
        return this.getBoolean("yarn.scheduler.fair.sizebasedweight", false);
    }
    
    public boolean isEventLogEnabled() {
        return this.getBoolean("yarn.scheduler.fair.event-log-enabled", false);
    }
    
    public String getEventlogDir() {
        return this.get("eventlog.dir", new File(System.getProperty("hadoop.log.dir", "/tmp/")).getAbsolutePath() + File.separator + "fairscheduler");
    }
    
    public int getPreemptionInterval() {
        return this.getInt("yarn.scheduler.fair.preemptionInterval", 5000);
    }
    
    public int getWaitTimeBeforeKill() {
        return this.getInt("yarn.scheduler.fair.waitTimeBeforeKill", 15000);
    }
    
    public boolean getUsePortForNodeName() {
        return this.getBoolean("yarn.scheduler.include-port-in-node-name", false);
    }
    
    public static org.apache.hadoop.yarn.api.records.Resource parseResourceConfigValue(final String val) throws AllocationConfigurationException {
        try {
            final int memory = findResource(val, "mb");
            final int vcores = findResource(val, "vcores");
            return BuilderUtils.newResource(memory, vcores);
        }
        catch (AllocationConfigurationException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new AllocationConfigurationException("Error reading resource config", ex2);
        }
    }
    
    public long getUpdateInterval() {
        return this.getLong("yarn.scheduler.fair.update-interval-ms", 500L);
    }
    
    private static int findResource(final String val, final String units) throws AllocationConfigurationException {
        final Pattern pattern = Pattern.compile("(\\d+) ?" + units);
        final Matcher matcher = pattern.matcher(val);
        if (!matcher.find()) {
            throw new AllocationConfigurationException("Missing resource: " + units);
        }
        return Integer.parseInt(matcher.group(1));
    }
    
    static {
        LOG = LogFactory.getLog(FairSchedulerConfiguration.class.getName());
    }
}
