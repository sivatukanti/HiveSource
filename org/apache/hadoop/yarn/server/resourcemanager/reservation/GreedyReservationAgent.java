// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.Iterator;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.ContractValidationException;
import java.util.ListIterator;
import java.util.List;
import org.apache.hadoop.yarn.api.records.Resource;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ReservationRequestInterpreter;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import java.util.HashMap;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.slf4j.Logger;

public class GreedyReservationAgent implements ReservationAgent
{
    private static final Logger LOG;
    
    @Override
    public boolean createReservation(final ReservationId reservationId, final String user, final Plan plan, final ReservationDefinition contract) throws PlanningException {
        return this.computeAllocation(reservationId, user, plan, contract, null);
    }
    
    @Override
    public boolean updateReservation(final ReservationId reservationId, final String user, final Plan plan, final ReservationDefinition contract) throws PlanningException {
        return this.computeAllocation(reservationId, user, plan, contract, plan.getReservationById(reservationId));
    }
    
    @Override
    public boolean deleteReservation(final ReservationId reservationId, final String user, final Plan plan) throws PlanningException {
        return plan.deleteReservation(reservationId);
    }
    
    private boolean computeAllocation(final ReservationId reservationId, final String user, final Plan plan, final ReservationDefinition contract, final ReservationAllocation oldReservation) throws PlanningException, ContractValidationException {
        GreedyReservationAgent.LOG.info("placing the following ReservationRequest: " + contract);
        final Resource totalCapacity = plan.getTotalCapacity();
        long earliestStart = contract.getArrival();
        final long step = plan.getStep();
        if (earliestStart % step != 0L) {
            earliestStart += step - earliestStart % step;
        }
        long curDeadline;
        final long deadline = curDeadline = contract.getDeadline() - contract.getDeadline() % plan.getStep();
        long oldDeadline = -1L;
        final Map<ReservationInterval, ReservationRequest> allocations = new HashMap<ReservationInterval, ReservationRequest>();
        final RLESparseResourceAllocation tempAssigned = new RLESparseResourceAllocation(plan.getResourceCalculator(), plan.getMinimumAllocation());
        final List<ReservationRequest> stages = contract.getReservationRequests().getReservationResources();
        final ReservationRequestInterpreter type = contract.getReservationRequests().getInterpreter();
        final ListIterator<ReservationRequest> li = stages.listIterator(stages.size());
        while (li.hasPrevious()) {
            final ReservationRequest currentReservationStage = li.previous();
            this.validateInput(plan, currentReservationStage, totalCapacity);
            final Map<ReservationInterval, ReservationRequest> curAlloc = this.placeSingleStage(plan, tempAssigned, currentReservationStage, earliestStart, curDeadline, oldReservation, totalCapacity);
            if (curAlloc == null) {
                if (type != ReservationRequestInterpreter.R_ANY) {
                    throw new PlanningException("The GreedyAgent couldn't find a valid allocation for your request");
                }
                continue;
            }
            else {
                allocations.putAll(curAlloc);
                if (type == ReservationRequestInterpreter.R_ANY) {
                    break;
                }
                if (type != ReservationRequestInterpreter.R_ORDER && type != ReservationRequestInterpreter.R_ORDER_NO_GAP) {
                    continue;
                }
                curDeadline = this.findEarliestTime(curAlloc.keySet());
                if (type == ReservationRequestInterpreter.R_ORDER_NO_GAP && oldDeadline > 0L && oldDeadline - this.findLatestTime(curAlloc.keySet()) > plan.getStep()) {
                    throw new PlanningException("The GreedyAgent couldn't find a valid allocation for your request");
                }
                oldDeadline = curDeadline;
            }
        }
        if (allocations.isEmpty()) {
            throw new PlanningException("The GreedyAgent couldn't find a valid allocation for your request");
        }
        final ReservationRequest ZERO_RES = ReservationRequest.newInstance(Resource.newInstance(0, 0), 0);
        long firstStartTime = this.findEarliestTime(allocations.keySet());
        if (firstStartTime > earliestStart) {
            allocations.put(new ReservationInterval(earliestStart, firstStartTime), ZERO_RES);
            firstStartTime = earliestStart;
        }
        final ReservationAllocation capReservation = new InMemoryReservationAllocation(reservationId, contract, user, plan.getQueueName(), firstStartTime, this.findLatestTime(allocations.keySet()), allocations, plan.getResourceCalculator(), plan.getMinimumAllocation());
        if (oldReservation != null) {
            return plan.updateReservation(capReservation);
        }
        return plan.addReservation(capReservation);
    }
    
    private void validateInput(final Plan plan, final ReservationRequest rr, final Resource totalCapacity) throws ContractValidationException {
        if (rr.getConcurrency() < 1) {
            throw new ContractValidationException("Gang Size should be >= 1");
        }
        if (rr.getNumContainers() <= 0) {
            throw new ContractValidationException("Num containers should be >= 0");
        }
        if (rr.getNumContainers() % rr.getConcurrency() != 0) {
            throw new ContractValidationException("Parallelism must be an exact multiple of gang size");
        }
        if (Resources.greaterThan(plan.getResourceCalculator(), totalCapacity, rr.getCapability(), plan.getMaximumAllocation())) {
            throw new ContractValidationException("Individual capability requests should not exceed cluster's maxAlloc");
        }
    }
    
    private Map<ReservationInterval, ReservationRequest> placeSingleStage(final Plan plan, final RLESparseResourceAllocation tempAssigned, final ReservationRequest rr, final long earliestStart, long curDeadline, final ReservationAllocation oldResAllocation, final Resource totalCapacity) {
        final Map<ReservationInterval, ReservationRequest> allocationRequests = new HashMap<ReservationInterval, ReservationRequest>();
        final Resource gang = Resources.multiply(rr.getCapability(), rr.getConcurrency());
        long dur = rr.getDuration();
        final long step = plan.getStep();
        if (dur % step != 0L) {
            dur += step - dur % step;
        }
        int gangsToPlace = rr.getNumContainers() / rr.getConcurrency();
        int maxGang = 0;
        while (gangsToPlace > 0 && curDeadline - dur >= earliestStart) {
            maxGang = gangsToPlace;
            long minPoint = curDeadline;
            int curMaxGang = maxGang;
            for (long t = curDeadline - plan.getStep(); t >= curDeadline - dur && maxGang > 0; t -= plan.getStep()) {
                Resource oldResCap = Resource.newInstance(0, 0);
                if (oldResAllocation != null) {
                    oldResCap = oldResAllocation.getResourcesAtTime(t);
                }
                final Resource netAvailableRes = Resources.clone(totalCapacity);
                Resources.addTo(netAvailableRes, oldResCap);
                Resources.subtractFrom(netAvailableRes, plan.getTotalCommittedResources(t));
                Resources.subtractFrom(netAvailableRes, tempAssigned.getCapacityAtTime(t));
                curMaxGang = (int)Math.floor(Resources.divide(plan.getResourceCalculator(), totalCapacity, netAvailableRes, gang));
                curMaxGang = Math.min(gangsToPlace, curMaxGang);
                if (curMaxGang <= maxGang) {
                    maxGang = curMaxGang;
                    minPoint = t;
                }
            }
            if (maxGang > 0) {
                gangsToPlace -= maxGang;
                final ReservationInterval reservationInt = new ReservationInterval(curDeadline - dur, curDeadline);
                final ReservationRequest reservationRes = ReservationRequest.newInstance(rr.getCapability(), rr.getConcurrency() * maxGang, rr.getConcurrency(), rr.getDuration());
                tempAssigned.addInterval(reservationInt, reservationRes);
                allocationRequests.put(reservationInt, reservationRes);
            }
            curDeadline = minPoint;
        }
        if (gangsToPlace == 0) {
            return allocationRequests;
        }
        for (final Map.Entry<ReservationInterval, ReservationRequest> tempAllocation : allocationRequests.entrySet()) {
            tempAssigned.removeInterval(tempAllocation.getKey(), tempAllocation.getValue());
        }
        return null;
    }
    
    private long findEarliestTime(final Set<ReservationInterval> resInt) {
        long ret = Long.MAX_VALUE;
        for (final ReservationInterval s : resInt) {
            if (s.getStartTime() < ret) {
                ret = s.getStartTime();
            }
        }
        return ret;
    }
    
    private long findLatestTime(final Set<ReservationInterval> resInt) {
        long ret = Long.MIN_VALUE;
        for (final ReservationInterval s : resInt) {
            if (s.getEndTime() > ret) {
                ret = s.getEndTime();
            }
        }
        return ret;
    }
    
    static {
        LOG = LoggerFactory.getLogger(GreedyReservationAgent.class);
    }
}
