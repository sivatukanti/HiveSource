// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api;

import org.apache.hadoop.ipc.ProtocolInfo;
import org.apache.hadoop.yarn.proto.ResourceTracker;

@ProtocolInfo(protocolName = "org.apache.hadoop.yarn.server.api.ResourceTrackerPB", protocolVersion = 1L)
public interface ResourceTrackerPB extends ResourceTracker.ResourceTrackerService.BlockingInterface
{
}
