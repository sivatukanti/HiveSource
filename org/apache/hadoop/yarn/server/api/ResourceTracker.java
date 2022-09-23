// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api;

import org.apache.hadoop.io.retry.AtMostOnce;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.io.retry.Idempotent;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;

public interface ResourceTracker
{
    @Idempotent
    RegisterNodeManagerResponse registerNodeManager(final RegisterNodeManagerRequest p0) throws YarnException, IOException;
    
    @AtMostOnce
    NodeHeartbeatResponse nodeHeartbeat(final NodeHeartbeatRequest p0) throws YarnException, IOException;
}
