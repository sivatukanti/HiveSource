// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.apache.hadoop.io.retry.Idempotent;
import org.apache.hadoop.security.AccessControlException;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.KerberosInfo;

@KerberosInfo(serverPrincipal = "hadoop.security.service.user.name.key")
@InterfaceAudience.Private
@InterfaceStability.Evolving
public interface ZKFCProtocol
{
    public static final long versionID = 1L;
    
    @Idempotent
    void cedeActive(final int p0) throws IOException, AccessControlException;
    
    @Idempotent
    void gracefulFailover() throws IOException, AccessControlException;
}
