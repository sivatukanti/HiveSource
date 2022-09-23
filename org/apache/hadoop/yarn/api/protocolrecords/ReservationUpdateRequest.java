// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ReservationUpdateRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ReservationUpdateRequest newInstance(final ReservationDefinition reservationDefinition, final ReservationId reservationId) {
        final ReservationUpdateRequest request = Records.newRecord(ReservationUpdateRequest.class);
        request.setReservationDefinition(reservationDefinition);
        request.setReservationId(reservationId);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ReservationDefinition getReservationDefinition();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setReservationDefinition(final ReservationDefinition p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ReservationId getReservationId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setReservationId(final ReservationId p0);
}
