// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api;

import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.proto.ApplicationClientProtocol;

@InterfaceAudience.Private
@InterfaceStability.Unstable
@ProtocolInfo(protocolName = "org.apache.hadoop.yarn.api.ApplicationClientProtocolPB", protocolVersion = 1L)
public interface ApplicationClientProtocolPB extends ApplicationClientProtocol.ApplicationClientProtocolService.BlockingInterface
{
}
