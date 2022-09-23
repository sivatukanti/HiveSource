// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Stable
public abstract class RefreshUserToGroupsMappingsResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static RefreshUserToGroupsMappingsResponse newInstance() {
        final RefreshUserToGroupsMappingsResponse response = Records.newRecord(RefreshUserToGroupsMappingsResponse.class);
        return response;
    }
}
