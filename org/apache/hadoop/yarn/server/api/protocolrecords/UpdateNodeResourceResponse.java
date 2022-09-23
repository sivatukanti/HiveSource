// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class UpdateNodeResourceResponse
{
    public static UpdateNodeResourceResponse newInstance() {
        final UpdateNodeResourceResponse response = Records.newRecord(UpdateNodeResourceResponse.class);
        return response;
    }
}
