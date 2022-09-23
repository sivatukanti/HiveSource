// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class GetQueueUserAclsInfoRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static GetQueueUserAclsInfoRequest newInstance() {
        final GetQueueUserAclsInfoRequest request = Records.newRecord(GetQueueUserAclsInfoRequest.class);
        return request;
    }
}
