// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tools.protocolPB;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.security.KerberosInfo;
import org.apache.hadoop.tools.proto.GetUserMappingsProtocolProtos;

@KerberosInfo(serverPrincipal = "hadoop.security.service.user.name.key")
@ProtocolInfo(protocolName = "org.apache.hadoop.tools.GetUserMappingsProtocol", protocolVersion = 1L)
@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public interface GetUserMappingsProtocolPB extends GetUserMappingsProtocolProtos.GetUserMappingsProtocolService.BlockingInterface
{
}
