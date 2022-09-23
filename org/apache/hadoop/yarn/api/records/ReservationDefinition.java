// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ReservationDefinition
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ReservationDefinition newInstance(final long arrival, final long deadline, final ReservationRequests reservationRequests, final String name) {
        final ReservationDefinition rDefinition = Records.newRecord(ReservationDefinition.class);
        rDefinition.setArrival(arrival);
        rDefinition.setDeadline(deadline);
        rDefinition.setReservationRequests(reservationRequests);
        rDefinition.setReservationName(name);
        return rDefinition;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getArrival();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setArrival(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract long getDeadline();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setDeadline(final long p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ReservationRequests getReservationRequests();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setReservationRequests(final ReservationRequests p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract String getReservationName();
    
    @InterfaceAudience.Public
    @InterfaceStability.Evolving
    public abstract void setReservationName(final String p0);
}
