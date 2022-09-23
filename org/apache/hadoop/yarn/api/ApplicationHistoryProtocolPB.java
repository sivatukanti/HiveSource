// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api;

import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.proto.ApplicationHistoryProtocol;

@InterfaceAudience.Private
@InterfaceStability.Unstable
@ProtocolInfo(protocolName = "org.apache.hadoop.yarn.api.ApplicationHistoryProtocolPB", protocolVersion = 1L)
public interface ApplicationHistoryProtocolPB extends ApplicationHistoryProtocol.ApplicationHistoryProtocolService.BlockingInterface
{
}
