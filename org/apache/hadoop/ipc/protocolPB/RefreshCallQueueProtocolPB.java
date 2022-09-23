// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.protocolPB;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.ipc.proto.RefreshCallQueueProtocolProtos;

@KerberosInfo(serverPrincipal = "hadoop.security.service.user.name.key")
@ProtocolInfo(protocolName = "org.apache.hadoop.ipc.RefreshCallQueueProtocol", protocolVersion = 1L)
@InterfaceAudience.LimitedPrivate({ "HDFS" })
@InterfaceStability.Evolving
public interface RefreshCallQueueProtocolPB extends RefreshCallQueueProtocolProtos.RefreshCallQueueProtocolService.BlockingInterface
{
}
