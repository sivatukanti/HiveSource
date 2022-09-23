// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tracing;

import org.apache.hadoop.io.retry.AtMostOnce;
import org.apache.hadoop.io.retry.Idempotent;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.security.KerberosInfo;

@KerberosInfo(serverPrincipal = "hadoop.security.service.user.name.key")
@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface TraceAdminProtocol
{
    public static final long versionID = 1L;
    
    @Idempotent
    SpanReceiverInfo[] listSpanReceivers() throws IOException;
    
    @AtMostOnce
    long addSpanReceiver(final SpanReceiverInfo p0) throws IOException;
    
    @AtMostOnce
    void removeSpanReceiver(final long p0) throws IOException;
}
