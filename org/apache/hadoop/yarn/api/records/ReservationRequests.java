// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ReservationRequests
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ReservationRequests newInstance(final List<ReservationRequest> reservationResources, final ReservationRequestInterpreter type) {
        final ReservationRequests reservationRequests = Records.newRecord(ReservationRequests.class);
        reservationRequests.setReservationResources(reservationResources);
        reservationRequests.setInterpreter(type);
        return reservationRequests;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract List<ReservationRequest> getReservationResources();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setReservationResources(final List<ReservationRequest> p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ReservationRequestInterpreter getInterpreter();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setInterpreter(final ReservationRequestInterpreter p0);
}
