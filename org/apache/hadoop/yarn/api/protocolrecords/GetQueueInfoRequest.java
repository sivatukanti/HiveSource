// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetQueueInfoRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetQueueInfoRequest newInstance(final String queueName, final boolean includeApplications, final boolean includeChildQueues, final boolean recursive) {
        final GetQueueInfoRequest request = Records.newRecord(GetQueueInfoRequest.class);
        request.setQueueName(queueName);
        request.setIncludeApplications(includeApplications);
        request.setIncludeChildQueues(includeChildQueues);
        request.setRecursive(recursive);
        return request;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getQueueName();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setQueueName(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract boolean getIncludeApplications();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setIncludeApplications(final boolean p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract boolean getIncludeChildQueues();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setIncludeChildQueues(final boolean p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract boolean getRecursive();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setRecursive(final boolean p0);
}
