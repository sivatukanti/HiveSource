// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class ReservationUpdateResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static ReservationUpdateResponse newInstance() {
        final ReservationUpdateResponse response = Records.newRecord(ReservationUpdateResponse.class);
        return response;
    }
}
