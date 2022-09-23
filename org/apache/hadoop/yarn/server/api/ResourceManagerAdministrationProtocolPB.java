// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api;

import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.proto.ResourceManagerAdministrationProtocol;

@InterfaceAudience.Private
@InterfaceStability.Unstable
@ProtocolInfo(protocolName = "org.apache.hadoop.yarn.server.api.ResourceManagerAdministrationProtocolPB", protocolVersion = 1L)
public interface ResourceManagerAdministrationProtocolPB extends ResourceManagerAdministrationProtocol.ResourceManagerAdministrationProtocolService.BlockingInterface
{
}
