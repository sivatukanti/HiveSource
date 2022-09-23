// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class AddToClusterNodeLabelsResponse
{
    public static AddToClusterNodeLabelsResponse newInstance() {
        return Records.newRecord(AddToClusterNodeLabelsResponse.class);
    }
}
