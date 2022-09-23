// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies;

import java.io.Serializable;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSQueue;
import org.apache.hadoop.yarn.server.resourcemanager.resource.ResourceType;
import java.util.Collection;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.Schedulable;
import java.util.Comparator;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.SchedulingPolicy;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FairSharePolicy extends SchedulingPolicy
{
    @VisibleForTesting
    public static final String NAME = "fair";
    private static final DefaultResourceCalculator RESOURCE_CALCULATOR;
    private FairShareComparator comparator;
    
    public FairSharePolicy() {
        this.comparator = new FairShareComparator();
    }
    
    @Override
    public String getName() {
        return "fair";
    }
    
    @Override
    public Comparator<Schedulable> getComparator() {
        return this.comparator;
    }
    
    @Override
    public Resource getHeadroom(final Resource queueFairShare, final Resource queueUsage, final Resource clusterAvailable) {
        final int queueAvailableMemory = Math.max(queueFairShare.getMemory() - queueUsage.getMemory(), 0);
        final Resource headroom = Resources.createResource(Math.min(clusterAvailable.getMemory(), queueAvailableMemory), clusterAvailable.getVirtualCores());
        return headroom;
    }
    
    @Override
    public void computeShares(final Collection<? extends Schedulable> schedulables, final Resource totalResources) {
        ComputeFairShares.computeShares(schedulables, totalResources, ResourceType.MEMORY);
    }
    
    @Override
    public void computeSteadyShares(final Collection<? extends FSQueue> queues, final Resource totalResources) {
        ComputeFairShares.computeSteadyShares(queues, totalResources, ResourceType.MEMORY);
    }
    
    @Override
    public boolean checkIfUsageOverFairShare(final Resource usage, final Resource fairShare) {
        return Resources.greaterThan(FairSharePolicy.RESOURCE_CALCULATOR, null, usage, fairShare);
    }
    
    @Override
    public boolean checkIfAMResourceUsageOverLimit(final Resource usage, final Resource maxAMResource) {
        return usage.getMemory() > maxAMResource.getMemory();
    }
    
    @Override
    public byte getApplicableDepth() {
        return 7;
    }
    
    static {
        RESOURCE_CALCULATOR = new DefaultResourceCalculator();
    }
    
    private static class FairShareComparator implements Comparator<Schedulable>, Serializable
    {
        private static final long serialVersionUID = 5564969375856699313L;
        private static final Resource ONE;
        
        @Override
        public int compare(final Schedulable s1, final Schedulable s2) {
            final Resource minShare1 = Resources.min(FairSharePolicy.RESOURCE_CALCULATOR, null, s1.getMinShare(), s1.getDemand());
            final Resource minShare2 = Resources.min(FairSharePolicy.RESOURCE_CALCULATOR, null, s2.getMinShare(), s2.getDemand());
            final boolean s1Needy = Resources.lessThan(FairSharePolicy.RESOURCE_CALCULATOR, null, s1.getResourceUsage(), minShare1);
            final boolean s2Needy = Resources.lessThan(FairSharePolicy.RESOURCE_CALCULATOR, null, s2.getResourceUsage(), minShare2);
            final double minShareRatio1 = s1.getResourceUsage().getMemory() / (double)Resources.max(FairSharePolicy.RESOURCE_CALCULATOR, null, minShare1, FairShareComparator.ONE).getMemory();
            final double minShareRatio2 = s2.getResourceUsage().getMemory() / (double)Resources.max(FairSharePolicy.RESOURCE_CALCULATOR, null, minShare2, FairShareComparator.ONE).getMemory();
            final double useToWeightRatio1 = s1.getResourceUsage().getMemory() / s1.getWeights().getWeight(ResourceType.MEMORY);
            final double useToWeightRatio2 = s2.getResourceUsage().getMemory() / s2.getWeights().getWeight(ResourceType.MEMORY);
            int res = 0;
            if (s1Needy && !s2Needy) {
                res = -1;
            }
            else if (s2Needy && !s1Needy) {
                res = 1;
            }
            else if (s1Needy && s2Needy) {
                res = (int)Math.signum(minShareRatio1 - minShareRatio2);
            }
            else {
                res = (int)Math.signum(useToWeightRatio1 - useToWeightRatio2);
            }
            if (res == 0) {
                res = (int)Math.signum((float)(s1.getStartTime() - s2.getStartTime()));
                if (res == 0) {
                    res = s1.getName().compareTo(s2.getName());
                }
            }
            return res;
        }
        
        static {
            ONE = Resources.createResource(1);
        }
    }
}
