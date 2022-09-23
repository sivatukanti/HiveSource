// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ReservationDeleteResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ReservationDeleteResponse newInstance() {
        final ReservationDeleteResponse response = Records.newRecord(ReservationDeleteResponse.class);
        return response;
    }
}
