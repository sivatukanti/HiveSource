// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public abstract class MoveApplicationAcrossQueuesRequest
{
    public static MoveApplicationAcrossQueuesRequest newInstance(final ApplicationId appId, final String queue) {
        final MoveApplicationAcrossQueuesRequest request = Records.newRecord(MoveApplicationAcrossQueuesRequest.class);
        request.setApplicationId(appId);
        request.setTargetQueue(queue);
        return request;
    }
    
    public abstract ApplicationId getApplicationId();
    
    public abstract void setApplicationId(final ApplicationId p0);
    
    public abstract String getTargetQueue();
    
    public abstract void setTargetQueue(final String p0);
}
