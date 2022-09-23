// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "yarn" })
@InterfaceStability.Unstable
public interface SharingPolicy
{
    void init(final String p0, final Configuration p1);
    
    void validate(final Plan p0, final ReservationAllocation p1) throws PlanningException;
    
    long getValidWindow();
}
