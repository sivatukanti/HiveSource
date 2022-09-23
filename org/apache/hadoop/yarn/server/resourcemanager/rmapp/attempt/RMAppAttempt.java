// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;

import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.security.client.ClientToAMTokenIdentifier;
import org.apache.hadoop.classification.InterfaceAudience;
import javax.crypto.SecretKey;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import java.util.List;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.event.EventHandler;

public interface RMAppAttempt extends EventHandler<RMAppAttemptEvent>
{
    ApplicationAttemptId getAppAttemptId();
    
    RMAppAttemptState getAppAttemptState();
    
    String getHost();
    
    int getRpcPort();
    
    String getTrackingUrl();
    
    String getOriginalTrackingUrl();
    
    String getWebProxyBase();
    
    String getDiagnostics();
    
    float getProgress();
    
    FinalApplicationStatus getFinalApplicationStatus();
    
    List<ContainerStatus> pullJustFinishedContainers();
    
    ConcurrentMap<NodeId, List<ContainerStatus>> getJustFinishedContainersReference();
    
    List<ContainerStatus> getJustFinishedContainers();
    
    ConcurrentMap<NodeId, List<ContainerStatus>> getFinishedContainersSentToAMReference();
    
    Container getMasterContainer();
    
    ApplicationSubmissionContext getSubmissionContext();
    
    Token<AMRMTokenIdentifier> getAMRMToken();
    
    @InterfaceAudience.LimitedPrivate({ "RMStateStore" })
    SecretKey getClientTokenMasterKey();
    
    Token<ClientToAMTokenIdentifier> createClientToken(final String p0);
    
    ApplicationResourceUsageReport getApplicationResourceUsageReport();
    
    long getStartTime();
    
    RMAppAttemptState getState();
    
    YarnApplicationAttemptState createApplicationAttemptState();
    
    ApplicationAttemptReport createApplicationAttemptReport();
    
    boolean shouldCountTowardsMaxAttemptRetry();
    
    RMAppAttemptMetrics getRMAppAttemptMetrics();
    
    long getFinishTime();
}
