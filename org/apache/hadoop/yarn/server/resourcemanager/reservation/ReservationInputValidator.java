// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionRequest;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ReservationRequests;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.ReservationRequestInterpreter;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.ipc.RPCUtil;
import org.apache.hadoop.yarn.server.resourcemanager.RMAuditLogger;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.util.Clock;

public class ReservationInputValidator
{
    private final Clock clock;
    
    public ReservationInputValidator(final Clock clock) {
        this.clock = clock;
    }
    
    private Plan validateReservation(final ReservationSystem reservationSystem, final ReservationId reservationId, final String auditConstant) throws YarnException {
        String message = "";
        if (reservationId == null) {
            message = "Missing reservation id. Please try again by specifying a reservation id.";
            RMAuditLogger.logFailure("UNKNOWN", auditConstant, "validate reservation input", "ClientRMService", message);
            throw RPCUtil.getRemoteException(message);
        }
        final String queueName = reservationSystem.getQueueForReservation(reservationId);
        if (queueName == null) {
            message = "The specified reservation with ID: " + reservationId + " is unknown. Please try again with a valid reservation.";
            RMAuditLogger.logFailure("UNKNOWN", auditConstant, "validate reservation input", "ClientRMService", message);
            throw RPCUtil.getRemoteException(message);
        }
        final Plan plan = reservationSystem.getPlan(queueName);
        if (plan == null) {
            message = "The specified reservation: " + reservationId + " is not associated with any valid plan." + " Please try again with a valid reservation.";
            RMAuditLogger.logFailure("UNKNOWN", auditConstant, "validate reservation input", "ClientRMService", message);
            throw RPCUtil.getRemoteException(message);
        }
        return plan;
    }
    
    private void validateReservationDefinition(final ReservationId reservationId, final ReservationDefinition contract, final Plan plan, final String auditConstant) throws YarnException {
        String message = "";
        if (contract == null) {
            message = "Missing reservation definition. Please try again by specifying a reservation definition.";
            RMAuditLogger.logFailure("UNKNOWN", auditConstant, "validate reservation input definition", "ClientRMService", message);
            throw RPCUtil.getRemoteException(message);
        }
        if (contract.getDeadline() <= this.clock.getTime()) {
            message = "The specified deadline: " + contract.getDeadline() + " is the past. Please try again with deadline in the future.";
            RMAuditLogger.logFailure("UNKNOWN", auditConstant, "validate reservation input definition", "ClientRMService", message);
            throw RPCUtil.getRemoteException(message);
        }
        final ReservationRequests resReqs = contract.getReservationRequests();
        if (resReqs == null) {
            message = "No resources have been specified to reserve.Please try again by specifying the resources to reserve.";
            RMAuditLogger.logFailure("UNKNOWN", auditConstant, "validate reservation input definition", "ClientRMService", message);
            throw RPCUtil.getRemoteException(message);
        }
        final List<ReservationRequest> resReq = resReqs.getReservationResources();
        if (resReq == null || resReq.isEmpty()) {
            message = "No resources have been specified to reserve. Please try again by specifying the resources to reserve.";
            RMAuditLogger.logFailure("UNKNOWN", auditConstant, "validate reservation input definition", "ClientRMService", message);
            throw RPCUtil.getRemoteException(message);
        }
        long minDuration = 0L;
        Resource maxGangSize = Resource.newInstance(0, 0);
        final ReservationRequestInterpreter type = contract.getReservationRequests().getInterpreter();
        for (final ReservationRequest rr : resReq) {
            if (type == ReservationRequestInterpreter.R_ALL || type == ReservationRequestInterpreter.R_ANY) {
                minDuration = Math.max(minDuration, rr.getDuration());
            }
            else {
                minDuration += rr.getDuration();
            }
            maxGangSize = Resources.max(plan.getResourceCalculator(), plan.getTotalCapacity(), maxGangSize, Resources.multiply(rr.getCapability(), rr.getConcurrency()));
        }
        if (contract.getDeadline() - contract.getArrival() < minDuration && type != ReservationRequestInterpreter.R_ANY) {
            message = "The time difference (" + (contract.getDeadline() - contract.getArrival()) + ") between arrival (" + contract.getArrival() + ") " + "and deadline (" + contract.getDeadline() + ") must " + " be greater or equal to the minimum resource duration (" + minDuration + ")";
            RMAuditLogger.logFailure("UNKNOWN", auditConstant, "validate reservation input definition", "ClientRMService", message);
            throw RPCUtil.getRemoteException(message);
        }
        if (Resources.greaterThan(plan.getResourceCalculator(), plan.getTotalCapacity(), maxGangSize, plan.getTotalCapacity()) && type != ReservationRequestInterpreter.R_ANY) {
            message = "The size of the largest gang in the reservation refinition (" + maxGangSize + ") exceed the capacity available (" + plan.getTotalCapacity() + " )";
            RMAuditLogger.logFailure("UNKNOWN", auditConstant, "validate reservation input definition", "ClientRMService", message);
            throw RPCUtil.getRemoteException(message);
        }
    }
    
    public Plan validateReservationSubmissionRequest(final ReservationSystem reservationSystem, final ReservationSubmissionRequest request, final ReservationId reservationId) throws YarnException {
        final String queueName = request.getQueue();
        if (queueName == null || queueName.isEmpty()) {
            final String errMsg = "The queue to submit is not specified. Please try again with a valid reservable queue.";
            RMAuditLogger.logFailure("UNKNOWN", "Submit Reservation Request", "validate reservation input", "ClientRMService", errMsg);
            throw RPCUtil.getRemoteException(errMsg);
        }
        final Plan plan = reservationSystem.getPlan(queueName);
        if (plan == null) {
            final String errMsg2 = "The specified queue: " + queueName + " is not managed by reservation system." + " Please try again with a valid reservable queue.";
            RMAuditLogger.logFailure("UNKNOWN", "Submit Reservation Request", "validate reservation input", "ClientRMService", errMsg2);
            throw RPCUtil.getRemoteException(errMsg2);
        }
        this.validateReservationDefinition(reservationId, request.getReservationDefinition(), plan, "Submit Reservation Request");
        return plan;
    }
    
    public Plan validateReservationUpdateRequest(final ReservationSystem reservationSystem, final ReservationUpdateRequest request) throws YarnException {
        final ReservationId reservationId = request.getReservationId();
        final Plan plan = this.validateReservation(reservationSystem, reservationId, "Update Reservation Request");
        this.validateReservationDefinition(reservationId, request.getReservationDefinition(), plan, "Update Reservation Request");
        return plan;
    }
    
    public Plan validateReservationDeleteRequest(final ReservationSystem reservationSystem, final ReservationDeleteRequest request) throws YarnException {
        return this.validateReservation(reservationSystem, request.getReservationId(), "Delete Reservation Request");
    }
}
