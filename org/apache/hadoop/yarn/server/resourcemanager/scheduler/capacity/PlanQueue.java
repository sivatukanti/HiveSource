// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerDynamicEditException;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.Resource;
import java.io.IOException;
import org.slf4j.Logger;

public class PlanQueue extends ParentQueue
{
    public static final String DEFAULT_QUEUE_SUFFIX = "-default";
    private static final Logger LOG;
    private int maxAppsForReservation;
    private int maxAppsPerUserForReservation;
    private int userLimit;
    private float userLimitFactor;
    protected CapacitySchedulerContext schedulerContext;
    private boolean showReservationsAsQueues;
    
    public PlanQueue(final CapacitySchedulerContext cs, final String queueName, final CSQueue parent, final CSQueue old) throws IOException {
        super(cs, queueName, parent, old);
        this.schedulerContext = cs;
        final CapacitySchedulerConfiguration conf = cs.getConfiguration();
        final String queuePath = super.getQueuePath();
        int maxAppsForReservation = conf.getMaximumApplicationsPerQueue(queuePath);
        this.showReservationsAsQueues = conf.getShowReservationAsQueues(queuePath);
        if (maxAppsForReservation < 0) {
            maxAppsForReservation = (int)(10000.0f * super.getAbsoluteCapacity());
        }
        final int userLimit = conf.getUserLimit(queuePath);
        final float userLimitFactor = conf.getUserLimitFactor(queuePath);
        final int maxAppsPerUserForReservation = (int)(maxAppsForReservation * (userLimit / 100.0f) * userLimitFactor);
        this.updateQuotas(userLimit, userLimitFactor, maxAppsForReservation, maxAppsPerUserForReservation);
        final StringBuffer queueInfo = new StringBuffer();
        queueInfo.append("Created Plan Queue: ").append(queueName).append("\nwith capacity: [").append(super.getCapacity()).append("]\nwith max capacity: [").append(super.getMaximumCapacity()).append("\nwith max reservation apps: [").append(maxAppsForReservation).append("]\nwith max reservation apps per user: [").append(maxAppsPerUserForReservation).append("]\nwith user limit: [").append(userLimit).append("]\nwith user limit factor: [").append(userLimitFactor).append("].");
        PlanQueue.LOG.info(queueInfo.toString());
    }
    
    @Override
    public synchronized void reinitialize(final CSQueue newlyParsedQueue, final Resource clusterResource) throws IOException {
        if (!(newlyParsedQueue instanceof PlanQueue) || !newlyParsedQueue.getQueuePath().equals(this.getQueuePath())) {
            throw new IOException("Trying to reinitialize " + this.getQueuePath() + " from " + newlyParsedQueue.getQueuePath());
        }
        final PlanQueue newlyParsedParentQueue = (PlanQueue)newlyParsedQueue;
        if (newlyParsedParentQueue.getChildQueues().size() > 0) {
            throw new IOException("Reservable Queue should not have sub-queues in theconfiguration");
        }
        this.setupQueueConfigs(clusterResource, newlyParsedParentQueue.getCapacity(), newlyParsedParentQueue.getAbsoluteCapacity(), newlyParsedParentQueue.getMaximumCapacity(), newlyParsedParentQueue.getAbsoluteMaximumCapacity(), newlyParsedParentQueue.getState(), newlyParsedParentQueue.getACLs(), newlyParsedParentQueue.accessibleLabels, newlyParsedParentQueue.defaultLabelExpression, newlyParsedParentQueue.capacitiyByNodeLabels, newlyParsedParentQueue.maxCapacityByNodeLabels, newlyParsedParentQueue.getReservationContinueLooking());
        this.updateQuotas(newlyParsedParentQueue.userLimit, newlyParsedParentQueue.userLimitFactor, newlyParsedParentQueue.maxAppsForReservation, newlyParsedParentQueue.maxAppsPerUserForReservation);
        for (final CSQueue res : this.getChildQueues()) {
            res.reinitialize(res, clusterResource);
        }
        this.showReservationsAsQueues = newlyParsedParentQueue.showReservationsAsQueues;
    }
    
    synchronized void addChildQueue(final CSQueue newQueue) throws SchedulerDynamicEditException {
        if (newQueue.getCapacity() > 0.0f) {
            throw new SchedulerDynamicEditException("Queue " + newQueue + " being added has non zero capacity.");
        }
        final boolean added = this.childQueues.add(newQueue);
        if (PlanQueue.LOG.isDebugEnabled()) {
            PlanQueue.LOG.debug("updateChildQueues (action: add queue): " + added + " " + this.getChildQueuesToPrint());
        }
    }
    
    synchronized void removeChildQueue(final CSQueue remQueue) throws SchedulerDynamicEditException {
        if (remQueue.getCapacity() > 0.0f) {
            throw new SchedulerDynamicEditException("Queue " + remQueue + " being removed has non zero capacity.");
        }
        final Iterator<CSQueue> qiter = this.childQueues.iterator();
        while (qiter.hasNext()) {
            final CSQueue cs = qiter.next();
            if (cs.equals(remQueue)) {
                qiter.remove();
                if (!PlanQueue.LOG.isDebugEnabled()) {
                    continue;
                }
                PlanQueue.LOG.debug("Removed child queue: {}", cs.getQueueName());
            }
        }
    }
    
    protected synchronized float sumOfChildCapacities() {
        float ret = 0.0f;
        for (final CSQueue l : this.childQueues) {
            ret += l.getCapacity();
        }
        return ret;
    }
    
    private void updateQuotas(final int userLimit, final float userLimitFactor, final int maxAppsForReservation, final int maxAppsPerUserForReservation) {
        this.userLimit = userLimit;
        this.userLimitFactor = userLimitFactor;
        this.maxAppsForReservation = maxAppsForReservation;
        this.maxAppsPerUserForReservation = maxAppsPerUserForReservation;
    }
    
    public int getMaxApplicationsForReservations() {
        return this.maxAppsForReservation;
    }
    
    public int getMaxApplicationsPerUserForReservation() {
        return this.maxAppsPerUserForReservation;
    }
    
    public int getUserLimitForReservation() {
        return this.userLimit;
    }
    
    public float getUserLimitFactor() {
        return this.userLimitFactor;
    }
    
    public boolean showReservationsAsQueues() {
        return this.showReservationsAsQueues;
    }
    
    static {
        LOG = LoggerFactory.getLogger(PlanQueue.class);
    }
}
