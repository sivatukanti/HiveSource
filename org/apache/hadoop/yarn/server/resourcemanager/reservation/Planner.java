// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.exceptions.PlanningException;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import java.util.List;

public interface Planner
{
    void plan(final Plan p0, final List<ReservationDefinition> p1) throws PlanningException;
    
    void init(final String p0, final Configuration p1);
}
