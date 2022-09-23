// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;

public interface ReservationAgent
{
    boolean createReservation(final ReservationId p0, final String p1, final Plan p2, final ReservationDefinition p3) throws PlanningException;
    
    boolean updateReservation(final ReservationId p0, final String p1, final Plan p2, final ReservationDefinition p3) throws PlanningException;
    
    boolean deleteReservation(final ReservationId p0, final String p1, final Plan p2) throws PlanningException;
}
