// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import java.util.Iterator;
import java.util.Set;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import java.util.Collection;
import java.util.TreeSet;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import java.util.List;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.conf.Configuration;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.util.UTCClock;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.commons.logging.Log;

public class SimpleCapacityReplanner implements Planner
{
    private static final Log LOG;
    private static final Resource ZERO_RESOURCE;
    private final Clock clock;
    private long lengthOfCheckZone;
    
    public SimpleCapacityReplanner() {
        this(new UTCClock());
    }
    
    @VisibleForTesting
    SimpleCapacityReplanner(final Clock clock) {
        this.clock = clock;
    }
    
    @Override
    public void init(final String planQueueName, final Configuration conf) {
        if (!(conf instanceof CapacitySchedulerConfiguration)) {
            throw new IllegalArgumentException("Unexpected conf type: " + conf.getClass().getSimpleName() + " only supported conf is: " + CapacitySchedulerConfiguration.class.getSimpleName());
        }
        this.lengthOfCheckZone = ((CapacitySchedulerConfiguration)conf).getEnforcementWindow(planQueueName);
    }
    
    @Override
    public void plan(final Plan plan, final List<ReservationDefinition> contracts) throws PlanningException {
        if (contracts != null) {
            throw new RuntimeException("SimpleCapacityReplanner cannot handle new reservation contracts");
        }
        final ResourceCalculator resCalc = plan.getResourceCalculator();
        final Resource totCap = plan.getTotalCapacity();
        long t;
        for (long now = t = this.clock.getTime(); t < plan.getLastEndTime() && t < now + this.lengthOfCheckZone; t += plan.getStep()) {
            Resource excessCap = Resources.subtract(plan.getTotalCommittedResources(t), totCap);
            if (Resources.greaterThan(resCalc, totCap, excessCap, SimpleCapacityReplanner.ZERO_RESOURCE)) {
                final Set<ReservationAllocation> curReservations = new TreeSet<ReservationAllocation>(plan.getReservationsAtTime(t));
                final Iterator<ReservationAllocation> resIter = curReservations.iterator();
                while (resIter.hasNext() && Resources.greaterThan(resCalc, totCap, excessCap, SimpleCapacityReplanner.ZERO_RESOURCE)) {
                    final ReservationAllocation reservation = resIter.next();
                    plan.deleteReservation(reservation.getReservationId());
                    excessCap = Resources.subtract(excessCap, reservation.getResourcesAtTime(t));
                    SimpleCapacityReplanner.LOG.info("Removing reservation " + reservation.getReservationId() + " to repair physical-resource constraints in the plan: " + plan.getQueueName());
                }
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(SimpleCapacityReplanner.class);
        ZERO_RESOURCE = Resource.newInstance(0, 0);
    }
}
