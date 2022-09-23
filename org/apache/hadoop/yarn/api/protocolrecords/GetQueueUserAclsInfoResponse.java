// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetQueueUserAclsInfoResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static GetQueueUserAclsInfoResponse newInstance(final List<QueueUserACLInfo> queueUserAclsList) {
        final GetQueueUserAclsInfoResponse response = Records.newRecord(GetQueueUserAclsInfoResponse.class);
        response.setUserAclsInfoList(queueUserAclsList);
        return response;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract List<QueueUserACLInfo> getUserAclsInfoList();
    
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public abstract void setUserAclsInfoList(final List<QueueUserACLInfo> p0);
}
