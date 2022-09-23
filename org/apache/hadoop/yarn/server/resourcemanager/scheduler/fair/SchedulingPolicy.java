// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import java.util.Collection;
import java.util.Comparator;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies.DominantResourceFairnessPolicy;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies.FifoPolicy;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies.FairSharePolicy;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ReflectionUtils;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class SchedulingPolicy
{
    private static final ConcurrentHashMap<Class<? extends SchedulingPolicy>, SchedulingPolicy> instances;
    public static final SchedulingPolicy DEFAULT_POLICY;
    public static final byte DEPTH_LEAF = 1;
    public static final byte DEPTH_INTERMEDIATE = 2;
    public static final byte DEPTH_ROOT = 4;
    public static final byte DEPTH_PARENT = 6;
    public static final byte DEPTH_ANY = 7;
    
    public static SchedulingPolicy getInstance(final Class<? extends SchedulingPolicy> clazz) {
        SchedulingPolicy policy = SchedulingPolicy.instances.get(clazz);
        if (policy == null) {
            policy = ReflectionUtils.newInstance(clazz, null);
            SchedulingPolicy.instances.put(clazz, policy);
        }
        return policy;
    }
    
    public static SchedulingPolicy parse(final String policy) throws AllocationConfigurationException {
        final String text = policy.toLowerCase();
        Class clazz;
        if (text.equalsIgnoreCase("fair")) {
            clazz = FairSharePolicy.class;
        }
        else if (text.equalsIgnoreCase("FIFO")) {
            clazz = FifoPolicy.class;
        }
        else if (text.equalsIgnoreCase("DRF")) {
            clazz = DominantResourceFairnessPolicy.class;
        }
        else {
            try {
                clazz = Class.forName(policy);
            }
            catch (ClassNotFoundException cnfe) {
                throw new AllocationConfigurationException(policy + " SchedulingPolicy class not found!");
            }
        }
        if (!SchedulingPolicy.class.isAssignableFrom(clazz)) {
            throw new AllocationConfigurationException(policy + " does not extend SchedulingPolicy");
        }
        return getInstance(clazz);
    }
    
    public void initialize(final Resource clusterCapacity) {
    }
    
    public abstract String getName();
    
    public abstract byte getApplicableDepth();
    
    public static boolean isApplicableTo(final SchedulingPolicy policy, final byte depth) {
        return (policy.getApplicableDepth() & depth) == depth;
    }
    
    public abstract Comparator<Schedulable> getComparator();
    
    public abstract void computeShares(final Collection<? extends Schedulable> p0, final Resource p1);
    
    public abstract void computeSteadyShares(final Collection<? extends FSQueue> p0, final Resource p1);
    
    public abstract boolean checkIfUsageOverFairShare(final Resource p0, final Resource p1);
    
    public abstract boolean checkIfAMResourceUsageOverLimit(final Resource p0, final Resource p1);
    
    public abstract Resource getHeadroom(final Resource p0, final Resource p1, final Resource p2);
    
    static {
        instances = new ConcurrentHashMap<Class<? extends SchedulingPolicy>, SchedulingPolicy>();
        DEFAULT_POLICY = getInstance(FairSharePolicy.class);
    }
}
