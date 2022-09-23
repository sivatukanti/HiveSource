// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.slf4j.LoggerFactory;
import java.util.Comparator;
import java.util.Collections;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Set;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.QueueEntitlement;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CSQueue;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import java.util.List;
import java.io.IOException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerDynamicEditException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.Queue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerContext;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.ReservationQueue;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.HashSet;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.PlanQueue;
import java.util.Iterator;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.util.Clock;
import java.util.Collection;
import org.slf4j.Logger;

public class CapacitySchedulerPlanFollower implements PlanFollower
{
    private static final Logger LOG;
    private Collection<Plan> plans;
    private Clock clock;
    private CapacityScheduler scheduler;
    
    public CapacitySchedulerPlanFollower() {
        this.plans = new ArrayList<Plan>();
    }
    
    @Override
    public void init(final Clock clock, final ResourceScheduler sched, final Collection<Plan> plans) {
        CapacitySchedulerPlanFollower.LOG.info("Initializing Plan Follower Policy:" + this.getClass().getCanonicalName());
        if (!(sched instanceof CapacityScheduler)) {
            throw new YarnRuntimeException("CapacitySchedulerPlanFollower can only work with CapacityScheduler");
        }
        this.clock = clock;
        this.scheduler = (CapacityScheduler)sched;
        this.plans.addAll(plans);
    }
    
    @Override
    public synchronized void run() {
        for (final Plan plan : this.plans) {
            this.synchronizePlan(plan);
        }
    }
    
    @Override
    public synchronized void synchronizePlan(final Plan plan) {
        final String planQueueName = plan.getQueueName();
        if (CapacitySchedulerPlanFollower.LOG.isDebugEnabled()) {
            CapacitySchedulerPlanFollower.LOG.debug("Running plan follower edit policy for plan: " + planQueueName);
        }
        final long step = plan.getStep();
        long now = this.clock.getTime();
        if (now % step != 0L) {
            now += step - now % step;
        }
        final CSQueue queue = this.scheduler.getQueue(planQueueName);
        if (!(queue instanceof PlanQueue)) {
            CapacitySchedulerPlanFollower.LOG.error("The Plan is not an PlanQueue!");
            return;
        }
        final PlanQueue planQueue = (PlanQueue)queue;
        final Resource clusterResources = this.scheduler.getClusterResource();
        final float planAbsCap = planQueue.getAbsoluteCapacity();
        final Resource planResources = Resources.multiply(clusterResources, planAbsCap);
        plan.setTotalCapacity(planResources);
        final Set<ReservationAllocation> currentReservations = plan.getReservationsAtTime(now);
        final Set<String> curReservationNames = new HashSet<String>();
        final Resource reservedResources = Resource.newInstance(0, 0);
        int numRes = 0;
        if (currentReservations != null) {
            numRes = currentReservations.size();
            for (final ReservationAllocation reservation : currentReservations) {
                curReservationNames.add(reservation.getReservationId().toString());
                Resources.addTo(reservedResources, reservation.getResourcesAtTime(now));
            }
        }
        final String defReservationQueue = planQueueName + "-default";
        if (this.scheduler.getQueue(defReservationQueue) == null) {
            try {
                final ReservationQueue defQueue = new ReservationQueue(this.scheduler, defReservationQueue, planQueue);
                this.scheduler.addQueue(defQueue);
            }
            catch (SchedulerDynamicEditException e) {
                CapacitySchedulerPlanFollower.LOG.warn("Exception while trying to create default reservation queue for plan: {}", planQueueName, e);
            }
            catch (IOException e2) {
                CapacitySchedulerPlanFollower.LOG.warn("Exception while trying to create default reservation queue for plan: {}", planQueueName, e2);
            }
        }
        curReservationNames.add(defReservationQueue);
        if (Resources.greaterThan(this.scheduler.getResourceCalculator(), clusterResources, reservedResources, planResources)) {
            try {
                plan.getReplanner().plan(plan, null);
            }
            catch (PlanningException e3) {
                CapacitySchedulerPlanFollower.LOG.warn("Exception while trying to replan: {}", planQueueName, e3);
            }
        }
        final List<CSQueue> resQueues = planQueue.getChildQueues();
        final Set<String> expired = new HashSet<String>();
        for (final CSQueue resQueue : resQueues) {
            final String resQueueName = resQueue.getQueueName();
            if (curReservationNames.contains(resQueueName)) {
                curReservationNames.remove(resQueueName);
            }
            else {
                expired.add(resQueueName);
            }
        }
        this.cleanupExpiredQueues(plan.getMoveOnExpiry(), expired, defReservationQueue);
        float totalAssignedCapacity = 0.0f;
        if (currentReservations != null) {
            try {
                this.scheduler.setEntitlement(defReservationQueue, new QueueEntitlement(0.0f, 1.0f));
            }
            catch (YarnException e4) {
                CapacitySchedulerPlanFollower.LOG.warn("Exception while trying to release default queue capacity for plan: {}", planQueueName, e4);
            }
            final List<ReservationAllocation> sortedAllocations = this.sortByDelta(new ArrayList<ReservationAllocation>(currentReservations), now);
            for (final ReservationAllocation res : sortedAllocations) {
                final String currResId = res.getReservationId().toString();
                if (curReservationNames.contains(currResId)) {
                    try {
                        final ReservationQueue resQueue2 = new ReservationQueue(this.scheduler, currResId, planQueue);
                        this.scheduler.addQueue(resQueue2);
                    }
                    catch (SchedulerDynamicEditException e5) {
                        CapacitySchedulerPlanFollower.LOG.warn("Exception while trying to activate reservation: {} for plan: {}", currResId, planQueueName, e5);
                    }
                    catch (IOException e6) {
                        CapacitySchedulerPlanFollower.LOG.warn("Exception while trying to activate reservation: {} for plan: {}", currResId, planQueueName, e6);
                    }
                }
                final Resource capToAssign = res.getResourcesAtTime(now);
                float targetCapacity = 0.0f;
                if (planResources.getMemory() > 0 && planResources.getVirtualCores() > 0) {
                    targetCapacity = Resources.divide(this.scheduler.getResourceCalculator(), clusterResources, capToAssign, planResources);
                }
                if (CapacitySchedulerPlanFollower.LOG.isDebugEnabled()) {
                    CapacitySchedulerPlanFollower.LOG.debug("Assigning capacity of {} to queue {} with target capacity {}", capToAssign, currResId, targetCapacity);
                }
                float maxCapacity = 1.0f;
                if (res.containsGangs()) {
                    maxCapacity = targetCapacity;
                }
                try {
                    this.scheduler.setEntitlement(currResId, new QueueEntitlement(targetCapacity, maxCapacity));
                }
                catch (YarnException e7) {
                    CapacitySchedulerPlanFollower.LOG.warn("Exception while trying to size reservation for plan: {}", currResId, planQueueName, e7);
                }
                totalAssignedCapacity += targetCapacity;
            }
        }
        final float defQCap = 1.0f - totalAssignedCapacity;
        if (CapacitySchedulerPlanFollower.LOG.isDebugEnabled()) {
            CapacitySchedulerPlanFollower.LOG.debug("PlanFollowerEditPolicyTask: total Plan Capacity: {} currReservation: {} default-queue capacity: {}", planResources, numRes, defQCap);
        }
        try {
            this.scheduler.setEntitlement(defReservationQueue, new QueueEntitlement(defQCap, 1.0f));
        }
        catch (YarnException e8) {
            CapacitySchedulerPlanFollower.LOG.warn("Exception while trying to reclaim default queue capacity for plan: {}", planQueueName, e8);
        }
        try {
            plan.archiveCompletedReservations(now);
        }
        catch (PlanningException e9) {
            CapacitySchedulerPlanFollower.LOG.error("Exception in archiving completed reservations: ", e9);
        }
        CapacitySchedulerPlanFollower.LOG.info("Finished iteration of plan follower edit policy for plan: " + planQueueName);
    }
    
    private void moveAppsInQueueSync(final String expiredReservation, final String defReservationQueue) {
        final List<ApplicationAttemptId> activeApps = this.scheduler.getAppsInQueue(expiredReservation);
        if (activeApps.isEmpty()) {
            return;
        }
        for (final ApplicationAttemptId app : activeApps) {
            try {
                this.scheduler.moveApplication(app.getApplicationId(), defReservationQueue);
            }
            catch (YarnException e) {
                CapacitySchedulerPlanFollower.LOG.warn("Encountered unexpected error during migration of application: {} from reservation: {}", app, expiredReservation, e);
            }
        }
    }
    
    private void cleanupExpiredQueues(final boolean shouldMove, final Set<String> toRemove, final String defReservationQueue) {
        for (final String expiredReservation : toRemove) {
            try {
                this.scheduler.setEntitlement(expiredReservation, new QueueEntitlement(0.0f, 0.0f));
                if (shouldMove) {
                    this.moveAppsInQueueSync(expiredReservation, defReservationQueue);
                }
                if (this.scheduler.getAppsInQueue(expiredReservation).size() > 0) {
                    this.scheduler.killAllAppsInQueue(expiredReservation);
                    CapacitySchedulerPlanFollower.LOG.info("Killing applications in queue: {}", expiredReservation);
                }
                else {
                    this.scheduler.removeQueue(expiredReservation);
                    CapacitySchedulerPlanFollower.LOG.info("Queue: " + expiredReservation + " removed");
                }
            }
            catch (YarnException e) {
                CapacitySchedulerPlanFollower.LOG.warn("Exception while trying to expire reservation: {}", expiredReservation, e);
            }
        }
    }
    
    @Override
    public synchronized void setPlans(final Collection<Plan> plans) {
        this.plans.clear();
        this.plans.addAll(plans);
    }
    
    private List<ReservationAllocation> sortByDelta(final List<ReservationAllocation> currentReservations, final long now) {
        Collections.sort(currentReservations, new ReservationAllocationComparator(this.scheduler, now));
        return currentReservations;
    }
    
    static {
        LOG = LoggerFactory.getLogger(CapacitySchedulerPlanFollower.class);
    }
    
    private static class ReservationAllocationComparator implements Comparator<ReservationAllocation>
    {
        CapacityScheduler scheduler;
        long now;
        
        ReservationAllocationComparator(final CapacityScheduler scheduler, final long now) {
            this.scheduler = scheduler;
            this.now = now;
        }
        
        private Resource getUnallocatedReservedResources(final ReservationAllocation reservation) {
            final CSQueue resQueue = this.scheduler.getQueue(reservation.getReservationId().toString());
            Resource resResource;
            if (resQueue != null) {
                resResource = Resources.subtract(reservation.getResourcesAtTime(this.now), Resources.multiply(this.scheduler.getClusterResource(), resQueue.getAbsoluteCapacity()));
            }
            else {
                resResource = reservation.getResourcesAtTime(this.now);
            }
            return resResource;
        }
        
        @Override
        public int compare(final ReservationAllocation lhs, final ReservationAllocation rhs) {
            final Resource lhsRes = this.getUnallocatedReservedResources(lhs);
            final Resource rhsRes = this.getUnallocatedReservedResources(rhs);
            return lhsRes.compareTo(rhsRes);
        }
    }
}
