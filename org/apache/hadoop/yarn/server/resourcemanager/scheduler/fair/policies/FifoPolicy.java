// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies;

import java.io.Serializable;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSQueue;
import java.util.Iterator;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.Collection;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.Schedulable;
import java.util.Comparator;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.SchedulingPolicy;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FifoPolicy extends SchedulingPolicy
{
    @VisibleForTesting
    public static final String NAME = "FIFO";
    private FifoComparator comparator;
    
    public FifoPolicy() {
        this.comparator = new FifoComparator();
    }
    
    @Override
    public String getName() {
        return "FIFO";
    }
    
    @Override
    public Comparator<Schedulable> getComparator() {
        return this.comparator;
    }
    
    @Override
    public void computeShares(final Collection<? extends Schedulable> schedulables, final Resource totalResources) {
        if (schedulables.isEmpty()) {
            return;
        }
        Schedulable earliest = null;
        for (final Schedulable schedulable : schedulables) {
            if (earliest == null || schedulable.getStartTime() < earliest.getStartTime()) {
                earliest = schedulable;
            }
        }
        earliest.setFairShare(Resources.clone(totalResources));
    }
    
    @Override
    public void computeSteadyShares(final Collection<? extends FSQueue> queues, final Resource totalResources) {
    }
    
    @Override
    public boolean checkIfUsageOverFairShare(final Resource usage, final Resource fairShare) {
        throw new UnsupportedOperationException("FifoPolicy doesn't support checkIfUsageOverFairshare operation, as FifoPolicy only works for FSLeafQueue.");
    }
    
    @Override
    public boolean checkIfAMResourceUsageOverLimit(final Resource usage, final Resource maxAMResource) {
        return usage.getMemory() > maxAMResource.getMemory();
    }
    
    @Override
    public Resource getHeadroom(final Resource queueFairShare, final Resource queueUsage, final Resource clusterAvailable) {
        final int queueAvailableMemory = Math.max(queueFairShare.getMemory() - queueUsage.getMemory(), 0);
        final Resource headroom = Resources.createResource(Math.min(clusterAvailable.getMemory(), queueAvailableMemory), clusterAvailable.getVirtualCores());
        return headroom;
    }
    
    @Override
    public byte getApplicableDepth() {
        return 1;
    }
    
    static class FifoComparator implements Comparator<Schedulable>, Serializable
    {
        private static final long serialVersionUID = -5905036205491177060L;
        
        @Override
        public int compare(final Schedulable s1, final Schedulable s2) {
            int res = s1.getPriority().compareTo(s2.getPriority());
            if (res == 0) {
                res = (int)Math.signum((float)(s1.getStartTime() - s2.getStartTime()));
            }
            if (res == 0) {
                res = s1.getName().compareTo(s2.getName());
            }
            return res;
        }
    }
}
