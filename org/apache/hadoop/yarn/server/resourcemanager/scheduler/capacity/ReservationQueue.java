// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerDynamicEditException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.QueueEntitlement;
import org.apache.hadoop.yarn.api.records.Resource;
import java.io.IOException;
import org.slf4j.Logger;

public class ReservationQueue extends LeafQueue
{
    private static final Logger LOG;
    private PlanQueue parent;
    private int maxSystemApps;
    
    public ReservationQueue(final CapacitySchedulerContext cs, final String queueName, final PlanQueue parent) throws IOException {
        super(cs, queueName, parent, null);
        this.maxSystemApps = cs.getConfiguration().getMaximumSystemApplications();
        this.updateQuotas(parent.getUserLimitForReservation(), parent.getUserLimitFactor(), parent.getMaxApplicationsForReservations(), parent.getMaxApplicationsPerUserForReservation());
        this.parent = parent;
    }
    
    @Override
    public synchronized void reinitialize(final CSQueue newlyParsedQueue, final Resource clusterResource) throws IOException {
        if (!(newlyParsedQueue instanceof ReservationQueue) || !newlyParsedQueue.getQueuePath().equals(this.getQueuePath())) {
            throw new IOException("Trying to reinitialize " + this.getQueuePath() + " from " + newlyParsedQueue.getQueuePath());
        }
        CSQueueUtils.updateQueueStatistics(this.parent.schedulerContext.getResourceCalculator(), newlyParsedQueue, this.parent, this.parent.schedulerContext.getClusterResource(), this.parent.schedulerContext.getMinimumResourceCapability());
        super.reinitialize(newlyParsedQueue, clusterResource);
        this.updateQuotas(this.parent.getUserLimitForReservation(), this.parent.getUserLimitFactor(), this.parent.getMaxApplicationsForReservations(), this.parent.getMaxApplicationsPerUserForReservation());
    }
    
    public synchronized void setEntitlement(final QueueEntitlement entitlement) throws SchedulerDynamicEditException {
        final float capacity = entitlement.getCapacity();
        if (capacity < 0.0f || capacity > 1.0f) {
            throw new SchedulerDynamicEditException("Capacity demand is not in the [0,1] range: " + capacity);
        }
        this.setCapacity(capacity);
        this.setAbsoluteCapacity(this.getParent().getAbsoluteCapacity() * this.getCapacity());
        this.setMaxApplications((int)(this.maxSystemApps * this.getAbsoluteCapacity()));
        this.setMaxCapacity(entitlement.getMaxCapacity());
        if (ReservationQueue.LOG.isDebugEnabled()) {
            ReservationQueue.LOG.debug("successfully changed to " + capacity + " for queue " + this.getQueueName());
        }
    }
    
    private void updateQuotas(final int userLimit, final float userLimitFactor, final int maxAppsForReservation, final int maxAppsPerUserForReservation) {
        this.setUserLimit(userLimit);
        this.setUserLimitFactor(userLimitFactor);
        this.setMaxApplications(maxAppsForReservation);
        this.maxApplicationsPerUser = maxAppsPerUserForReservation;
    }
    
    @Override
    protected float getCapacityFromConf() {
        return 0.0f;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ReservationQueue.class);
    }
}
