// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.io.retry.Idempotent;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.KerberosInfo;

@KerberosInfo(serverPrincipal = "hadoop.security.service.user.name.key")
@InterfaceAudience.LimitedPrivate({ "HDFS" })
@InterfaceStability.Evolving
public interface RefreshCallQueueProtocol
{
    public static final long versionID = 1L;
    
    @Idempotent
    void refreshCallQueue() throws IOException;
}
