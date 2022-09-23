// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import java.util.List;
import org.apache.hadoop.yarn.server.api.records.NodeAction;
import org.apache.hadoop.yarn.factories.RecordFactory;

public class YarnServerBuilderUtils
{
    private static final RecordFactory recordFactory;
    
    public static NodeHeartbeatResponse newNodeHeartbeatResponse(final int responseId, final NodeAction action, final List<ContainerId> containersToCleanUp, final List<ApplicationId> applicationsToCleanUp, final MasterKey containerTokenMasterKey, final MasterKey nmTokenMasterKey, final long nextHeartbeatInterval) {
        final NodeHeartbeatResponse response = YarnServerBuilderUtils.recordFactory.newRecordInstance(NodeHeartbeatResponse.class);
        response.setResponseId(responseId);
        response.setNodeAction(action);
        response.setContainerTokenMasterKey(containerTokenMasterKey);
        response.setNMTokenMasterKey(nmTokenMasterKey);
        response.setNextHeartBeatInterval(nextHeartbeatInterval);
        if (containersToCleanUp != null) {
            response.addAllContainersToCleanup(containersToCleanUp);
        }
        if (applicationsToCleanUp != null) {
            response.addAllApplicationsToCleanup(applicationsToCleanUp);
        }
        return response;
    }
    
    static {
        recordFactory = RecordFactoryProvider.getRecordFactory(null);
    }
}
