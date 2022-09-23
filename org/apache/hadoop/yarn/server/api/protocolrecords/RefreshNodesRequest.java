// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Stable
public abstract class RefreshNodesRequest
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static RefreshNodesRequest newInstance() {
        final RefreshNodesRequest request = Records.newRecord(RefreshNodesRequest.class);
        return request;
    }
}
