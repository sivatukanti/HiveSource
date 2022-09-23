// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords;

import org.apache.hadoop.yarn.util.Records;

public abstract class GetNodesToLabelsRequest
{
    public static GetNodesToLabelsRequest newInstance() {
        return Records.newRecord(GetNodesToLabelsRequest.class);
    }
}
