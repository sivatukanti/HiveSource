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
public abstract class ReservationSubmissionResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ReservationSubmissionResponse newInstance(final ReservationId reservationId) {
        final ReservationSubmissionResponse response = Records.newRecord(ReservationSubmissionResponse.class);
        response.setReservationId(reservationId);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract ReservationId getReservationId();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setReservationId(final ReservationId p0);
}
