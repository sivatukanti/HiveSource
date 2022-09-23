// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.apache.hadoop.io.retry.Idempotent;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@KerberosInfo(serverPrincipal = "hadoop.security.service.user.name.key")
@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public interface RefreshUserMappingsProtocol
{
    public static final long versionID = 1L;
    
    @Idempotent
    void refreshUserToGroupsMappings() throws IOException;
    
    @Idempotent
    void refreshSuperUserGroupsConfiguration() throws IOException;
}
