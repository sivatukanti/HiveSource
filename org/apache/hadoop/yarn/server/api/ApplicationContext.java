// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api;

import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Map;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;

public interface ApplicationContext
{
    ApplicationReport getApplication(final ApplicationId p0) throws YarnException, IOException;
    
    Map<ApplicationId, ApplicationReport> getAllApplications() throws YarnException, IOException;
    
    Map<ApplicationAttemptId, ApplicationAttemptReport> getApplicationAttempts(final ApplicationId p0) throws YarnException, IOException;
    
    ApplicationAttemptReport getApplicationAttempt(final ApplicationAttemptId p0) throws YarnException, IOException;
    
    ContainerReport getContainer(final ContainerId p0) throws YarnException, IOException;
    
    ContainerReport getAMContainer(final ApplicationAttemptId p0) throws YarnException, IOException;
    
    Map<ContainerId, ContainerReport> getContainers(final ApplicationAttemptId p0) throws YarnException, IOException;
}
