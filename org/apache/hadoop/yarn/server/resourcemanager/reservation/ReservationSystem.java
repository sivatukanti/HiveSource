// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.yarn.api.records.ReservationId;
import java.util.Map;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "yarn" })
@InterfaceStability.Unstable
public interface ReservationSystem
{
    void setRMContext(final RMContext p0);
    
    void reinitialize(final Configuration p0, final RMContext p1) throws YarnException;
    
    Plan getPlan(final String p0);
    
    Map<String, Plan> getAllPlans();
    
    void synchronizePlan(final String p0);
    
    long getPlanFollowerTimeStep();
    
    ReservationId getNewReservationId();
    
    String getQueueForReservation(final ReservationId p0);
    
    void setQueueForReservation(final ReservationId p0, final String p1);
}
