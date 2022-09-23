// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies;

import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FSQueue;
import org.apache.hadoop.yarn.server.resourcemanager.resource.ResourceType;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.Schedulable;
import java.util.Collection;

public class ComputeFairShares
{
    private static final int COMPUTE_FAIR_SHARES_ITERATIONS = 25;
    
    public static void computeShares(final Collection<? extends Schedulable> schedulables, final Resource totalResources, final ResourceType type) {
        computeSharesInternal(schedulables, totalResources, type, false);
    }
    
    public static void computeSteadyShares(final Collection<? extends FSQueue> queues, final Resource totalResources, final ResourceType type) {
        computeSharesInternal(queues, totalResources, type, true);
    }
    
    private static void computeSharesInternal(final Collection<? extends Schedulable> allSchedulables, final Resource totalResources, final ResourceType type, final boolean isSteadyShare) {
        final Collection<Schedulable> schedulables = new ArrayList<Schedulable>();
        final int takenResources = handleFixedFairShares(allSchedulables, schedulables, isSteadyShare, type);
        if (schedulables.isEmpty()) {
            return;
        }
        int totalMaxShare = 0;
        for (final Schedulable sched : schedulables) {
            final int maxShare = getResourceValue(sched.getMaxShare(), type);
            totalMaxShare = (int)Math.min(maxShare + (long)totalMaxShare, 2147483647L);
            if (totalMaxShare == Integer.MAX_VALUE) {
                break;
            }
        }
        int totalResource;
        double rMax;
        for (totalResource = Math.max(getResourceValue(totalResources, type) - takenResources, 0), totalResource = Math.min(totalMaxShare, totalResource), rMax = 1.0; resourceUsedWithWeightToResourceRatio(rMax, schedulables, type) < totalResource; rMax *= 2.0) {}
        double left = 0.0;
        double right = rMax;
        for (int i = 0; i < 25; ++i) {
            final double mid = (left + right) / 2.0;
            final int plannedResourceUsed = resourceUsedWithWeightToResourceRatio(mid, schedulables, type);
            if (plannedResourceUsed == totalResource) {
                right = mid;
                break;
            }
            if (plannedResourceUsed < totalResource) {
                left = mid;
            }
            else {
                right = mid;
            }
        }
        for (final Schedulable sched2 : schedulables) {
            if (isSteadyShare) {
                setResourceValue(computeShare(sched2, right, type), ((FSQueue)sched2).getSteadyFairShare(), type);
            }
            else {
                setResourceValue(computeShare(sched2, right, type), sched2.getFairShare(), type);
            }
        }
    }
    
    private static int resourceUsedWithWeightToResourceRatio(final double w2rRatio, final Collection<? extends Schedulable> schedulables, final ResourceType type) {
        int resourcesTaken = 0;
        for (final Schedulable sched : schedulables) {
            final int share = computeShare(sched, w2rRatio, type);
            resourcesTaken += share;
        }
        return resourcesTaken;
    }
    
    private static int computeShare(final Schedulable sched, final double w2rRatio, final ResourceType type) {
        double share = sched.getWeights().getWeight(type) * w2rRatio;
        share = Math.max(share, getResourceValue(sched.getMinShare(), type));
        share = Math.min(share, getResourceValue(sched.getMaxShare(), type));
        return (int)share;
    }
    
    private static int handleFixedFairShares(final Collection<? extends Schedulable> schedulables, final Collection<Schedulable> nonFixedSchedulables, final boolean isSteadyShare, final ResourceType type) {
        int totalResource = 0;
        for (final Schedulable sched : schedulables) {
            final int fixedShare = getFairShareIfFixed(sched, isSteadyShare, type);
            if (fixedShare < 0) {
                nonFixedSchedulables.add(sched);
            }
            else {
                setResourceValue(fixedShare, isSteadyShare ? ((FSQueue)sched).getSteadyFairShare() : sched.getFairShare(), type);
                totalResource = (int)Math.min(totalResource + (long)fixedShare, 2147483647L);
            }
        }
        return totalResource;
    }
    
    private static int getFairShareIfFixed(final Schedulable sched, final boolean isSteadyShare, final ResourceType type) {
        if (getResourceValue(sched.getMaxShare(), type) <= 0) {
            return 0;
        }
        if (!isSteadyShare && sched instanceof FSQueue && !((FSQueue)sched).isActive()) {
            return 0;
        }
        if (sched.getWeights().getWeight(type) <= 0.0f) {
            final int minShare = getResourceValue(sched.getMinShare(), type);
            return (minShare <= 0) ? 0 : minShare;
        }
        return -1;
    }
    
    private static int getResourceValue(final Resource resource, final ResourceType type) {
        switch (type) {
            case MEMORY: {
                return resource.getMemory();
            }
            case CPU: {
                return resource.getVirtualCores();
            }
            default: {
                throw new IllegalArgumentException("Invalid resource");
            }
        }
    }
    
    private static void setResourceValue(final int val, final Resource resource, final ResourceType type) {
        switch (type) {
            case MEMORY: {
                resource.setMemory(val);
                break;
            }
            case CPU: {
                resource.setVirtualCores(val);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid resource");
            }
        }
    }
}
