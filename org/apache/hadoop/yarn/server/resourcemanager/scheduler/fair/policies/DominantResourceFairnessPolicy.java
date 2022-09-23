// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies;

import org.apache.hadoop.yarn.server.resourcemanager.resource.ResourceWeights;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSQueue;
import org.apache.hadoop.yarn.server.resourcemanager.resource.ResourceType;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.Schedulable;
import java.util.Comparator;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.SchedulingPolicy;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class DominantResourceFairnessPolicy extends SchedulingPolicy
{
    public static final String NAME = "DRF";
    private DominantResourceFairnessComparator comparator;
    
    public DominantResourceFairnessPolicy() {
        this.comparator = new DominantResourceFairnessComparator();
    }
    
    @Override
    public String getName() {
        return "DRF";
    }
    
    @Override
    public byte getApplicableDepth() {
        return 7;
    }
    
    @Override
    public Comparator<Schedulable> getComparator() {
        return this.comparator;
    }
    
    @Override
    public void computeShares(final Collection<? extends Schedulable> schedulables, final Resource totalResources) {
        for (final ResourceType type : ResourceType.values()) {
            ComputeFairShares.computeShares(schedulables, totalResources, type);
        }
    }
    
    @Override
    public void computeSteadyShares(final Collection<? extends FSQueue> queues, final Resource totalResources) {
        for (final ResourceType type : ResourceType.values()) {
            ComputeFairShares.computeSteadyShares(queues, totalResources, type);
        }
    }
    
    @Override
    public boolean checkIfUsageOverFairShare(final Resource usage, final Resource fairShare) {
        return !Resources.fitsIn(usage, fairShare);
    }
    
    @Override
    public boolean checkIfAMResourceUsageOverLimit(final Resource usage, final Resource maxAMResource) {
        return !Resources.fitsIn(usage, maxAMResource);
    }
    
    @Override
    public Resource getHeadroom(final Resource queueFairShare, final Resource queueUsage, final Resource clusterAvailable) {
        final int queueAvailableMemory = Math.max(queueFairShare.getMemory() - queueUsage.getMemory(), 0);
        final int queueAvailableCPU = Math.max(queueFairShare.getVirtualCores() - queueUsage.getVirtualCores(), 0);
        final Resource headroom = Resources.createResource(Math.min(clusterAvailable.getMemory(), queueAvailableMemory), Math.min(clusterAvailable.getVirtualCores(), queueAvailableCPU));
        return headroom;
    }
    
    @Override
    public void initialize(final Resource clusterCapacity) {
        this.comparator.setClusterCapacity(clusterCapacity);
    }
    
    public static class DominantResourceFairnessComparator implements Comparator<Schedulable>
    {
        private static final int NUM_RESOURCES;
        private Resource clusterCapacity;
        
        public void setClusterCapacity(final Resource clusterCapacity) {
            this.clusterCapacity = clusterCapacity;
        }
        
        @Override
        public int compare(final Schedulable s1, final Schedulable s2) {
            final ResourceWeights sharesOfCluster1 = new ResourceWeights();
            final ResourceWeights sharesOfCluster2 = new ResourceWeights();
            final ResourceWeights sharesOfMinShare1 = new ResourceWeights();
            final ResourceWeights sharesOfMinShare2 = new ResourceWeights();
            final ResourceType[] resourceOrder1 = new ResourceType[DominantResourceFairnessComparator.NUM_RESOURCES];
            final ResourceType[] resourceOrder2 = new ResourceType[DominantResourceFairnessComparator.NUM_RESOURCES];
            this.calculateShares(s1.getResourceUsage(), this.clusterCapacity, sharesOfCluster1, resourceOrder1, s1.getWeights());
            this.calculateShares(s1.getResourceUsage(), s1.getMinShare(), sharesOfMinShare1, null, ResourceWeights.NEUTRAL);
            this.calculateShares(s2.getResourceUsage(), this.clusterCapacity, sharesOfCluster2, resourceOrder2, s2.getWeights());
            this.calculateShares(s2.getResourceUsage(), s2.getMinShare(), sharesOfMinShare2, null, ResourceWeights.NEUTRAL);
            final boolean s1Needy = sharesOfMinShare1.getWeight(resourceOrder1[0]) < 1.0f;
            final boolean s2Needy = sharesOfMinShare2.getWeight(resourceOrder2[0]) < 1.0f;
            int res = 0;
            if (!s2Needy && !s1Needy) {
                res = this.compareShares(sharesOfCluster1, sharesOfCluster2, resourceOrder1, resourceOrder2);
            }
            else if (s1Needy && !s2Needy) {
                res = -1;
            }
            else if (s2Needy && !s1Needy) {
                res = 1;
            }
            else {
                res = this.compareShares(sharesOfMinShare1, sharesOfMinShare2, resourceOrder1, resourceOrder2);
            }
            if (res == 0) {
                res = (int)(s1.getStartTime() - s2.getStartTime());
            }
            return res;
        }
        
        void calculateShares(final Resource resource, final Resource pool, final ResourceWeights shares, final ResourceType[] resourceOrder, final ResourceWeights weights) {
            shares.setWeight(ResourceType.MEMORY, resource.getMemory() / (pool.getMemory() * weights.getWeight(ResourceType.MEMORY)));
            shares.setWeight(ResourceType.CPU, resource.getVirtualCores() / (pool.getVirtualCores() * weights.getWeight(ResourceType.CPU)));
            if (resourceOrder != null) {
                if (shares.getWeight(ResourceType.MEMORY) > shares.getWeight(ResourceType.CPU)) {
                    resourceOrder[0] = ResourceType.MEMORY;
                    resourceOrder[1] = ResourceType.CPU;
                }
                else {
                    resourceOrder[0] = ResourceType.CPU;
                    resourceOrder[1] = ResourceType.MEMORY;
                }
            }
        }
        
        private int compareShares(final ResourceWeights shares1, final ResourceWeights shares2, final ResourceType[] resourceOrder1, final ResourceType[] resourceOrder2) {
            for (int i = 0; i < resourceOrder1.length; ++i) {
                final int ret = (int)Math.signum(shares1.getWeight(resourceOrder1[i]) - shares2.getWeight(resourceOrder2[i]));
                if (ret != 0) {
                    return ret;
                }
            }
            return 0;
        }
        
        static {
            NUM_RESOURCES = ResourceType.values().length;
        }
    }
}
