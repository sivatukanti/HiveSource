// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Stable
public abstract class RefreshAdminAclsRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static RefreshAdminAclsRequest newInstance() {
        final RefreshAdminAclsRequest request = Records.newRecord(RefreshAdminAclsRequest.class);
        return request;
    }
}
