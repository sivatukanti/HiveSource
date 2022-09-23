// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha.protocolPB;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.hadoop.ha.proto.ZKFCProtocolProtos;

@KerberosInfo(serverPrincipal = "hadoop.security.service.user.name.key")
@ProtocolInfo(protocolName = "org.apache.hadoop.ha.ZKFCProtocol", protocolVersion = 1L)
@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface ZKFCProtocolPB extends ZKFCProtocolProtos.ZKFCProtocolService.BlockingInterface, VersionedProtocol
{
}
