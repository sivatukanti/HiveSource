// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.yarn.api.records.Resource;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.ReservationId;

public interface PlanView extends PlanContext
{
    ReservationAllocation getReservationById(final ReservationId p0);
    
    Set<ReservationAllocation> getReservationsAtTime(final long p0);
    
    Set<ReservationAllocation> getAllReservations();
    
    Resource getTotalCommittedResources(final long p0);
    
    Resource getConsumptionForUser(final String p0, final long p1);
    
    Resource getTotalCapacity();
    
    long getEarliestStartTime();
    
    long getLastEndTime();
}
