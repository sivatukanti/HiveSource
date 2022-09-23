// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class MoveApplicationAcrossQueuesResponse
{
    @InterfaceAudience.Private
    @InterfaceStability.Unstable
    public MoveApplicationAcrossQueuesResponse newInstance() {
        final MoveApplicationAcrossQueuesResponse response = Records.newRecord(MoveApplicationAcrossQueuesResponse.class);
        return response;
    }
}
