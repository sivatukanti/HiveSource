// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetQueueInfoResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static GetQueueInfoResponse newInstance(final QueueInfo queueInfo) {
        final GetQueueInfoResponse response = Records.newRecord(GetQueueInfoResponse.class);
        response.setQueueInfo(queueInfo);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract QueueInfo getQueueInfo();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setQueueInfo(final QueueInfo p0);
}
