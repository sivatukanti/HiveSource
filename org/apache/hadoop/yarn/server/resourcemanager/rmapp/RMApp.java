// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import java.util.Collection;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import java.util.Map;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.event.EventHandler;

public interface RMApp extends EventHandler<RMAppEvent>
{
    ApplicationId getApplicationId();
    
    ApplicationSubmissionContext getApplicationSubmissionContext();
    
    RMAppState getState();
    
    String getUser();
    
    float getProgress();
    
    RMAppAttempt getRMAppAttempt(final ApplicationAttemptId p0);
    
    String getQueue();
    
    void setQueue(final String p0);
    
    String getName();
    
    RMAppAttempt getCurrentAppAttempt();
    
    Map<ApplicationAttemptId, RMAppAttempt> getAppAttempts();
    
    ApplicationReport createAndGetApplicationReport(final String p0, final boolean p1);
    
    int pullRMNodeUpdates(final Collection<RMNode> p0);
    
    long getFinishTime();
    
    long getStartTime();
    
    long getSubmitTime();
    
    String getTrackingUrl();
    
    String getOriginalTrackingUrl();
    
    StringBuilder getDiagnostics();
    
    FinalApplicationStatus getFinalApplicationStatus();
    
    int getMaxAppAttempts();
    
    String getApplicationType();
    
    Set<String> getApplicationTags();
    
    boolean isAppFinalStateStored();
    
    Set<NodeId> getRanNodes();
    
    YarnApplicationState createApplicationState();
    
    RMAppMetrics getRMAppMetrics();
    
    ReservationId getReservationId();
}
