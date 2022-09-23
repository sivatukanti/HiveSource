// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ReservationSubmissionRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static ReservationSubmissionRequest newInstance(final ReservationDefinition reservationDefinition, final String queueName) {
        final ReservationSubmissionRequest request = Records.newRecord(ReservationSubmissionRequest.class);
        request.setReservationDefinition(reservationDefinition);
        request.setQueue(queueName);
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
    public abstract String getQueue();
    
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public abstract void setQueue(final String p0);
}
