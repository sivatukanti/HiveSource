// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice;

import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerHistoryData;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptHistoryData;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Map;
import java.io.IOException;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationHistoryData;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public interface ApplicationHistoryReader
{
    ApplicationHistoryData getApplication(final ApplicationId p0) throws IOException;
    
    Map<ApplicationId, ApplicationHistoryData> getAllApplications() throws IOException;
    
    Map<ApplicationAttemptId, ApplicationAttemptHistoryData> getApplicationAttempts(final ApplicationId p0) throws IOException;
    
    ApplicationAttemptHistoryData getApplicationAttempt(final ApplicationAttemptId p0) throws IOException;
    
    ContainerHistoryData getContainer(final ContainerId p0) throws IOException;
    
    ContainerHistoryData getAMContainer(final ApplicationAttemptId p0) throws IOException;
    
    Map<ContainerId, ContainerHistoryData> getContainers(final ApplicationAttemptId p0) throws IOException;
}
