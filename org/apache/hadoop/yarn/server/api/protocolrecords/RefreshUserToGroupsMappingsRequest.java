// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Stable
public abstract class RefreshUserToGroupsMappingsRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static RefreshUserToGroupsMappingsRequest newInstance() {
        final RefreshUserToGroupsMappingsRequest request = Records.newRecord(RefreshUserToGroupsMappingsRequest.class);
        return request;
    }
}
