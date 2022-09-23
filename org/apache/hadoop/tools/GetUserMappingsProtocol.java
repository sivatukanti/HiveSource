// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tools;

import org.apache.hadoop.io.retry.Idempotent;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public interface GetUserMappingsProtocol
{
    public static final long versionID = 1L;
    
    @Idempotent
    String[] getGroupsForUser(final String p0) throws IOException;
}
