// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tracing;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.ipc.VersionedProtocol;

@KerberosInfo(serverPrincipal = "hadoop.security.service.user.name.key")
@ProtocolInfo(protocolName = "org.apache.hadoop.tracing.TraceAdminPB.TraceAdminService", protocolVersion = 1L)
@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface TraceAdminProtocolPB extends TraceAdminPB.TraceAdminService.BlockingInterface, VersionedProtocol
{
}
