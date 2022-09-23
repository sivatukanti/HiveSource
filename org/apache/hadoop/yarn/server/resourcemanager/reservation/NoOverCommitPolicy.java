// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.ResourceOverCommitException;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.MismatchedUserException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "yarn" })
@InterfaceStability.Unstable
public class NoOverCommitPolicy implements SharingPolicy
{
    @Override
    public void validate(final Plan plan, final ReservationAllocation reservation) throws PlanningException {
        final ReservationAllocation oldReservation = plan.getReservationById(reservation.getReservationId());
        if (oldReservation != null && !oldReservation.getUser().equals(reservation.getUser())) {
            throw new MismatchedUserException("Updating an existing reservation with mismatching user:" + oldReservation.getUser() + " != " + reservation.getUser());
        }
        final long startTime = reservation.getStartTime();
        for (long endTime = reservation.getEndTime(), step = plan.getStep(), t = startTime; t < endTime; t += step) {
            final Resource currExistingAllocTot = plan.getTotalCommittedResources(t);
            final Resource currNewAlloc = reservation.getResourcesAtTime(t);
            final Resource currOldAlloc = Resource.newInstance(0, 0);
            if (oldReservation != null) {
                oldReservation.getResourcesAtTime(t);
            }
            if (Resources.greaterThan(plan.getResourceCalculator(), plan.getTotalCapacity(), Resources.subtract(Resources.add(currExistingAllocTot, currNewAlloc), currOldAlloc), plan.getTotalCapacity())) {
                throw new ResourceOverCommitException("Resources at time " + t + " would be overcommitted by " + "accepting reservation: " + reservation.getReservationId());
            }
        }
    }
    
    @Override
    public long getValidWindow() {
        return 0L;
    }
    
    @Override
    public void init(final String planQueuePath, final Configuration conf) {
    }
}
