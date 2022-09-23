// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;

public interface PlanEdit extends PlanContext, PlanView
{
    boolean addReservation(final ReservationAllocation p0) throws PlanningException;
    
    boolean updateReservation(final ReservationAllocation p0) throws PlanningException;
    
    boolean deleteReservation(final ReservationId p0) throws PlanningException;
    
    void archiveCompletedReservations(final long p0) throws PlanningException;
    
    void setTotalCapacity(final Resource p0);
}
