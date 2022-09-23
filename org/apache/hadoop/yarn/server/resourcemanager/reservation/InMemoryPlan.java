// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.yarn.util.resource.Resources;
import java.util.Collections;
import java.util.SortedMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import java.util.HashMap;
import org.apache.hadoop.yarn.util.UTCClock;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.yarn.api.records.ReservationId;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.hadoop.yarn.api.records.Resource;
import org.slf4j.Logger;

class InMemoryPlan implements Plan
{
    private static final Logger LOG;
    private static final Resource ZERO_RESOURCE;
    private TreeMap<ReservationInterval, Set<InMemoryReservationAllocation>> currentReservations;
    private RLESparseResourceAllocation rleSparseVector;
    private Map<String, RLESparseResourceAllocation> userResourceAlloc;
    private Map<ReservationId, InMemoryReservationAllocation> reservationTable;
    private final ReentrantReadWriteLock readWriteLock;
    private final Lock readLock;
    private final Lock writeLock;
    private final SharingPolicy policy;
    private final ReservationAgent agent;
    private final long step;
    private final ResourceCalculator resCalc;
    private final Resource minAlloc;
    private final Resource maxAlloc;
    private final String queueName;
    private final QueueMetrics queueMetrics;
    private final Planner replanner;
    private final boolean getMoveOnExpiry;
    private final Clock clock;
    private Resource totalCapacity;
    
    InMemoryPlan(final QueueMetrics queueMetrics, final SharingPolicy policy, final ReservationAgent agent, final Resource totalCapacity, final long step, final ResourceCalculator resCalc, final Resource minAlloc, final Resource maxAlloc, final String queueName, final Planner replanner, final boolean getMoveOnExpiry) {
        this(queueMetrics, policy, agent, totalCapacity, step, resCalc, minAlloc, maxAlloc, queueName, replanner, getMoveOnExpiry, new UTCClock());
    }
    
    InMemoryPlan(final QueueMetrics queueMetrics, final SharingPolicy policy, final ReservationAgent agent, final Resource totalCapacity, final long step, final ResourceCalculator resCalc, final Resource minAlloc, final Resource maxAlloc, final String queueName, final Planner replanner, final boolean getMoveOnExpiry, final Clock clock) {
        this.currentReservations = new TreeMap<ReservationInterval, Set<InMemoryReservationAllocation>>();
        this.userResourceAlloc = new HashMap<String, RLESparseResourceAllocation>();
        this.reservationTable = new HashMap<ReservationId, InMemoryReservationAllocation>();
        this.readWriteLock = new ReentrantReadWriteLock();
        this.readLock = this.readWriteLock.readLock();
        this.writeLock = this.readWriteLock.writeLock();
        this.queueMetrics = queueMetrics;
        this.policy = policy;
        this.agent = agent;
        this.step = step;
        this.totalCapacity = totalCapacity;
        this.resCalc = resCalc;
        this.minAlloc = minAlloc;
        this.maxAlloc = maxAlloc;
        this.rleSparseVector = new RLESparseResourceAllocation(resCalc, minAlloc);
        this.queueName = queueName;
        this.replanner = replanner;
        this.getMoveOnExpiry = getMoveOnExpiry;
        this.clock = clock;
    }
    
    @Override
    public QueueMetrics getQueueMetrics() {
        return this.queueMetrics;
    }
    
    private void incrementAllocation(final ReservationAllocation reservation) {
        assert this.readWriteLock.isWriteLockedByCurrentThread();
        final Map<ReservationInterval, ReservationRequest> allocationRequests = reservation.getAllocationRequests();
        final String user = reservation.getUser();
        RLESparseResourceAllocation resAlloc = this.userResourceAlloc.get(user);
        if (resAlloc == null) {
            resAlloc = new RLESparseResourceAllocation(this.resCalc, this.minAlloc);
            this.userResourceAlloc.put(user, resAlloc);
        }
        for (final Map.Entry<ReservationInterval, ReservationRequest> r : allocationRequests.entrySet()) {
            resAlloc.addInterval(r.getKey(), r.getValue());
            this.rleSparseVector.addInterval(r.getKey(), r.getValue());
        }
    }
    
    private void decrementAllocation(final ReservationAllocation reservation) {
        assert this.readWriteLock.isWriteLockedByCurrentThread();
        final Map<ReservationInterval, ReservationRequest> allocationRequests = reservation.getAllocationRequests();
        final String user = reservation.getUser();
        final RLESparseResourceAllocation resAlloc = this.userResourceAlloc.get(user);
        for (final Map.Entry<ReservationInterval, ReservationRequest> r : allocationRequests.entrySet()) {
            resAlloc.removeInterval(r.getKey(), r.getValue());
            this.rleSparseVector.removeInterval(r.getKey(), r.getValue());
        }
        if (resAlloc.isEmpty()) {
            this.userResourceAlloc.remove(user);
        }
    }
    
    @Override
    public Set<ReservationAllocation> getAllReservations() {
        this.readLock.lock();
        try {
            if (this.currentReservations != null) {
                final Set<ReservationAllocation> flattenedReservations = new HashSet<ReservationAllocation>();
                for (final Set<InMemoryReservationAllocation> reservationEntries : this.currentReservations.values()) {
                    flattenedReservations.addAll(reservationEntries);
                }
                return flattenedReservations;
            }
            return null;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public boolean addReservation(final ReservationAllocation reservation) throws PlanningException {
        final InMemoryReservationAllocation inMemReservation = (InMemoryReservationAllocation)reservation;
        if (inMemReservation.getUser() == null) {
            final String errMsg = "The specified Reservation with ID " + inMemReservation.getReservationId() + " is not mapped to any user";
            InMemoryPlan.LOG.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        this.writeLock.lock();
        try {
            if (this.reservationTable.containsKey(inMemReservation.getReservationId())) {
                final String errMsg = "The specified Reservation with ID " + inMemReservation.getReservationId() + " already exists";
                InMemoryPlan.LOG.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            }
            this.policy.validate(this, inMemReservation);
            reservation.setAcceptanceTimestamp(this.clock.getTime());
            final ReservationInterval searchInterval = new ReservationInterval(inMemReservation.getStartTime(), inMemReservation.getEndTime());
            Set<InMemoryReservationAllocation> reservations = this.currentReservations.get(searchInterval);
            if (reservations == null) {
                reservations = new HashSet<InMemoryReservationAllocation>();
            }
            if (!reservations.add(inMemReservation)) {
                InMemoryPlan.LOG.error("Unable to add reservation: {} to plan.", inMemReservation.getReservationId());
                return false;
            }
            this.currentReservations.put(searchInterval, reservations);
            this.reservationTable.put(inMemReservation.getReservationId(), inMemReservation);
            this.incrementAllocation(inMemReservation);
            InMemoryPlan.LOG.info("Sucessfully added reservation: {} to plan.", inMemReservation.getReservationId());
            return true;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public boolean updateReservation(final ReservationAllocation reservation) throws PlanningException {
        this.writeLock.lock();
        boolean result = false;
        try {
            final ReservationId resId = reservation.getReservationId();
            final ReservationAllocation currReservation = this.getReservationById(resId);
            if (currReservation == null) {
                final String errMsg = "The specified Reservation with ID " + resId + " does not exist in the plan";
                InMemoryPlan.LOG.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            }
            this.policy.validate(this, reservation);
            if (!this.removeReservation(currReservation)) {
                InMemoryPlan.LOG.error("Unable to replace reservation: {} from plan.", reservation.getReservationId());
                return result;
            }
            try {
                result = this.addReservation(reservation);
            }
            catch (PlanningException e) {
                InMemoryPlan.LOG.error("Unable to update reservation: {} from plan due to {}.", reservation.getReservationId(), e.getMessage());
            }
            if (result) {
                InMemoryPlan.LOG.info("Sucessfully updated reservation: {} in plan.", reservation.getReservationId());
                return result;
            }
            this.addReservation(currReservation);
            InMemoryPlan.LOG.info("Rollbacked update reservation: {} from plan.", reservation.getReservationId());
            return result;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    private boolean removeReservation(final ReservationAllocation reservation) {
        assert this.readWriteLock.isWriteLockedByCurrentThread();
        final ReservationInterval searchInterval = new ReservationInterval(reservation.getStartTime(), reservation.getEndTime());
        final Set<InMemoryReservationAllocation> reservations = this.currentReservations.get(searchInterval);
        if (reservations == null) {
            final String errMsg = "The specified Reservation with ID " + reservation.getReservationId() + " does not exist in the plan";
            InMemoryPlan.LOG.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        if (!reservations.remove(reservation)) {
            InMemoryPlan.LOG.error("Unable to remove reservation: {} from plan.", reservation.getReservationId());
            return false;
        }
        if (reservations.isEmpty()) {
            this.currentReservations.remove(searchInterval);
        }
        this.reservationTable.remove(reservation.getReservationId());
        this.decrementAllocation(reservation);
        InMemoryPlan.LOG.info("Sucessfully deleted reservation: {} in plan.", reservation.getReservationId());
        return true;
    }
    
    @Override
    public boolean deleteReservation(final ReservationId reservationID) {
        this.writeLock.lock();
        try {
            final ReservationAllocation reservation = this.getReservationById(reservationID);
            if (reservation == null) {
                final String errMsg = "The specified Reservation with ID " + reservationID + " does not exist in the plan";
                InMemoryPlan.LOG.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            }
            return this.removeReservation(reservation);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void archiveCompletedReservations(final long tick) {
        InMemoryPlan.LOG.debug("Running archival at time: {}", (Object)tick);
        final List<InMemoryReservationAllocation> expiredReservations = new ArrayList<InMemoryReservationAllocation>();
        this.readLock.lock();
        try {
            final long archivalTime = tick - this.policy.getValidWindow();
            final ReservationInterval searchInterval = new ReservationInterval(archivalTime, archivalTime);
            final SortedMap<ReservationInterval, Set<InMemoryReservationAllocation>> reservations = this.currentReservations.headMap(searchInterval, true);
            if (!reservations.isEmpty()) {
                for (final Set<InMemoryReservationAllocation> reservationEntries : reservations.values()) {
                    for (final InMemoryReservationAllocation reservation : reservationEntries) {
                        if (reservation.getEndTime() <= archivalTime) {
                            expiredReservations.add(reservation);
                        }
                    }
                }
            }
        }
        finally {
            this.readLock.unlock();
        }
        if (expiredReservations.isEmpty()) {
            return;
        }
        this.writeLock.lock();
        try {
            for (final InMemoryReservationAllocation expiredReservation : expiredReservations) {
                this.removeReservation(expiredReservation);
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public Set<ReservationAllocation> getReservationsAtTime(final long tick) {
        final ReservationInterval searchInterval = new ReservationInterval(tick, Long.MAX_VALUE);
        this.readLock.lock();
        try {
            final SortedMap<ReservationInterval, Set<InMemoryReservationAllocation>> reservations = this.currentReservations.headMap(searchInterval, true);
            if (!reservations.isEmpty()) {
                final Set<ReservationAllocation> flattenedReservations = new HashSet<ReservationAllocation>();
                for (final Set<InMemoryReservationAllocation> reservationEntries : reservations.values()) {
                    for (final InMemoryReservationAllocation reservation : reservationEntries) {
                        if (reservation.getEndTime() > tick) {
                            flattenedReservations.add(reservation);
                        }
                    }
                }
                return Collections.unmodifiableSet((Set<? extends ReservationAllocation>)flattenedReservations);
            }
            return Collections.emptySet();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public long getStep() {
        return this.step;
    }
    
    @Override
    public SharingPolicy getSharingPolicy() {
        return this.policy;
    }
    
    @Override
    public ReservationAgent getReservationAgent() {
        return this.agent;
    }
    
    @Override
    public Resource getConsumptionForUser(final String user, final long t) {
        this.readLock.lock();
        try {
            final RLESparseResourceAllocation userResAlloc = this.userResourceAlloc.get(user);
            if (userResAlloc != null) {
                return userResAlloc.getCapacityAtTime(t);
            }
            return Resources.clone(InMemoryPlan.ZERO_RESOURCE);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Resource getTotalCommittedResources(final long t) {
        this.readLock.lock();
        try {
            return this.rleSparseVector.getCapacityAtTime(t);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public ReservationAllocation getReservationById(final ReservationId reservationID) {
        if (reservationID == null) {
            return null;
        }
        this.readLock.lock();
        try {
            return this.reservationTable.get(reservationID);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Resource getTotalCapacity() {
        this.readLock.lock();
        try {
            return Resources.clone(this.totalCapacity);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Resource getMinimumAllocation() {
        return Resources.clone(this.minAlloc);
    }
    
    @Override
    public void setTotalCapacity(final Resource cap) {
        this.writeLock.lock();
        try {
            this.totalCapacity = Resources.clone(cap);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public long getEarliestStartTime() {
        this.readLock.lock();
        try {
            return this.rleSparseVector.getEarliestStartTime();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public long getLastEndTime() {
        this.readLock.lock();
        try {
            return this.rleSparseVector.getLatestEndTime();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public ResourceCalculator getResourceCalculator() {
        return this.resCalc;
    }
    
    @Override
    public String getQueueName() {
        return this.queueName;
    }
    
    @Override
    public Resource getMaximumAllocation() {
        return Resources.clone(this.maxAlloc);
    }
    
    public String toCumulativeString() {
        this.readLock.lock();
        try {
            return this.rleSparseVector.toString();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public Planner getReplanner() {
        return this.replanner;
    }
    
    @Override
    public boolean getMoveOnExpiry() {
        return this.getMoveOnExpiry;
    }
    
    @Override
    public String toString() {
        this.readLock.lock();
        try {
            final StringBuffer planStr = new StringBuffer("In-memory Plan: ");
            planStr.append("Parent Queue: ").append(this.queueName).append("Total Capacity: ").append(this.totalCapacity).append("Step: ").append(this.step);
            for (final ReservationAllocation reservation : this.getAllReservations()) {
                planStr.append(reservation);
            }
            return planStr.toString();
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(InMemoryPlan.class);
        ZERO_RESOURCE = Resource.newInstance(0, 0);
    }
}
