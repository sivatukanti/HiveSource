// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ReservationDeleteRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ReservationDeleteRequest newInstance(final ReservationId reservationId) {
        final ReservationDeleteRequest request = Records.newRecord(ReservationDeleteRequest.class);
        request.setReservationId(reservationId);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ReservationId getReservationId();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setReservationId(final ReservationId p0);
}
