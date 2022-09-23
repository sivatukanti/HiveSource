// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.api.records.ReservationId;

public interface ReservationAllocation extends Comparable<ReservationAllocation>
{
    ReservationId getReservationId();
    
    ReservationDefinition getReservationDefinition();
    
    long getStartTime();
    
    long getEndTime();
    
    Map<ReservationInterval, ReservationRequest> getAllocationRequests();
    
    String getPlanName();
    
    String getUser();
    
    boolean containsGangs();
    
    void setAcceptanceTimestamp(final long p0);
    
    long getAcceptanceTime();
    
    Resource getResourcesAtTime(final long p0);
}
