// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Stable
public abstract class RefreshServiceAclsResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public static RefreshServiceAclsResponse newInstance() {
        final RefreshServiceAclsResponse response = Records.newRecord(RefreshServiceAclsResponse.class);
        return response;
    }
}
