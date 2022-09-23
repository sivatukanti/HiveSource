// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.io.retry.Idempotent;
import java.io.IOException;
import java.util.Collection;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.KerberosInfo;

@KerberosInfo(serverPrincipal = "hadoop.security.service.user.name.key")
@InterfaceAudience.Private
@InterfaceStability.Evolving
public interface GenericRefreshProtocol
{
    public static final long versionID = 1L;
    
    @Idempotent
    Collection<RefreshResponse> refresh(final String p0, final String[] p1) throws IOException;
}
