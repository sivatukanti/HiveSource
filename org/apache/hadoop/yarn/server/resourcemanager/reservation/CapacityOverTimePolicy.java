// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.Date;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningQuotaException;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.ResourceOverCommitException;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.MismatchedUserException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "yarn" })
@InterfaceStability.Unstable
public class CapacityOverTimePolicy implements SharingPolicy
{
    private CapacitySchedulerConfiguration conf;
    private long validWindow;
    private float maxInst;
    private float maxAvg;
    
    @Override
    public void init(final String reservationQueuePath, final Configuration conf) {
        if (!(conf instanceof CapacitySchedulerConfiguration)) {
            throw new IllegalArgumentException("Unexpected conf type: " + conf.getClass().getSimpleName() + " only supported conf is: " + CapacitySchedulerConfiguration.class.getSimpleName());
        }
        this.conf = (CapacitySchedulerConfiguration)conf;
        this.validWindow = this.conf.getReservationWindow(reservationQueuePath);
        this.maxInst = this.conf.getInstantaneousMaxCapacity(reservationQueuePath) / 100.0f;
        this.maxAvg = this.conf.getAverageCapacity(reservationQueuePath) / 100.0f;
    }
    
    @Override
    public void validate(final Plan plan, final ReservationAllocation reservation) throws PlanningException {
        final ReservationAllocation oldReservation = plan.getReservationById(reservation.getReservationId());
        if (oldReservation != null && !oldReservation.getUser().equals(reservation.getUser())) {
            throw new MismatchedUserException("Updating an existing reservation with mismatched user:" + oldReservation.getUser() + " != " + reservation.getUser());
        }
        final long startTime = reservation.getStartTime();
        final long endTime = reservation.getEndTime();
        final long step = plan.getStep();
        final Resource planTotalCapacity = plan.getTotalCapacity();
        final Resource maxAvgRes = Resources.multiply(planTotalCapacity, this.maxAvg);
        final Resource maxInsRes = Resources.multiply(planTotalCapacity, this.maxInst);
        final IntegralResource runningTot = new IntegralResource(0L, 0L);
        final IntegralResource maxAllowed = new IntegralResource(maxAvgRes);
        maxAllowed.multiplyBy(this.validWindow / step);
        for (long t = startTime - this.validWindow; t < endTime + this.validWindow; t += step) {
            final Resource currExistingAllocTot = plan.getTotalCommittedResources(t);
            final Resource currExistingAllocForUser = plan.getConsumptionForUser(reservation.getUser(), t);
            final Resource currNewAlloc = reservation.getResourcesAtTime(t);
            Resource currOldAlloc = Resources.none();
            if (oldReservation != null) {
                currOldAlloc = oldReservation.getResourcesAtTime(t);
            }
            final Resource inst = Resources.subtract(Resources.add(currExistingAllocTot, currNewAlloc), currOldAlloc);
            if (Resources.greaterThan(plan.getResourceCalculator(), planTotalCapacity, inst, planTotalCapacity)) {
                throw new ResourceOverCommitException(" Resources at time " + t + " would be overcommitted (" + inst + " over " + plan.getTotalCapacity() + ") by accepting reservation: " + reservation.getReservationId());
            }
            if (Resources.greaterThan(plan.getResourceCalculator(), planTotalCapacity, Resources.subtract(Resources.add(currExistingAllocForUser, currNewAlloc), currOldAlloc), maxInsRes)) {
                throw new PlanningQuotaException("Instantaneous quota capacity " + this.maxInst + " would be passed at time " + t + " by accepting reservation: " + reservation.getReservationId());
            }
            runningTot.add(currExistingAllocForUser);
            runningTot.add(currNewAlloc);
            runningTot.subtract(currOldAlloc);
            if (t > startTime) {
                final Resource pastOldAlloc = plan.getConsumptionForUser(reservation.getUser(), t - this.validWindow);
                final Resource pastNewAlloc = reservation.getResourcesAtTime(t - this.validWindow);
                runningTot.subtract(pastOldAlloc);
                runningTot.subtract(pastNewAlloc);
            }
            if (maxAllowed.compareTo(runningTot) < 0L) {
                throw new PlanningQuotaException("Integral (avg over time) quota capacity " + this.maxAvg + " over a window of " + this.validWindow / 1000L + " seconds, " + " would be passed at time " + t + "(" + new Date(t) + ") by accepting reservation: " + reservation.getReservationId());
            }
        }
    }
    
    @Override
    public long getValidWindow() {
        return this.validWindow;
    }
    
    private static class IntegralResource
    {
        long memory;
        long vcores;
        
        public IntegralResource(final Resource resource) {
            this.memory = resource.getMemory();
            this.vcores = resource.getVirtualCores();
        }
        
        public IntegralResource(final long mem, final long vcores) {
            this.memory = mem;
            this.vcores = vcores;
        }
        
        public void add(final Resource r) {
            this.memory += r.getMemory();
            this.vcores += r.getVirtualCores();
        }
        
        public void subtract(final Resource r) {
            this.memory -= r.getMemory();
            this.vcores -= r.getVirtualCores();
        }
        
        public void multiplyBy(final long window) {
            this.memory *= window;
            this.vcores *= window;
        }
        
        public long compareTo(final IntegralResource other) {
            long diff = this.memory - other.memory;
            if (diff == 0L) {
                diff = this.vcores - other.vcores;
            }
            return diff;
        }
        
        @Override
        public String toString() {
            return "<memory:" + this.memory + ", vCores:" + this.vcores + ">";
        }
    }
}
