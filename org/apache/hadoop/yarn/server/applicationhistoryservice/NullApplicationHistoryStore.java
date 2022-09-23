// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice;

import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerHistoryData;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptHistoryData;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import java.util.Collections;
import java.util.Map;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationHistoryData;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerFinishData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerStartData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptFinishData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptStartData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationFinishData;
import java.io.IOException;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationStartData;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.service.AbstractService;

@InterfaceStability.Unstable
@InterfaceAudience.Private
public class NullApplicationHistoryStore extends AbstractService implements ApplicationHistoryStore
{
    public NullApplicationHistoryStore() {
        super(NullApplicationHistoryStore.class.getName());
    }
    
    @Override
    public void applicationStarted(final ApplicationStartData appStart) throws IOException {
    }
    
    @Override
    public void applicationFinished(final ApplicationFinishData appFinish) throws IOException {
    }
    
    @Override
    public void applicationAttemptStarted(final ApplicationAttemptStartData appAttemptStart) throws IOException {
    }
    
    @Override
    public void applicationAttemptFinished(final ApplicationAttemptFinishData appAttemptFinish) throws IOException {
    }
    
    @Override
    public void containerStarted(final ContainerStartData containerStart) throws IOException {
    }
    
    @Override
    public void containerFinished(final ContainerFinishData containerFinish) throws IOException {
    }
    
    @Override
    public ApplicationHistoryData getApplication(final ApplicationId appId) throws IOException {
        return null;
    }
    
    @Override
    public Map<ApplicationId, ApplicationHistoryData> getAllApplications() throws IOException {
        return Collections.emptyMap();
    }
    
    @Override
    public Map<ApplicationAttemptId, ApplicationAttemptHistoryData> getApplicationAttempts(final ApplicationId appId) throws IOException {
        return Collections.emptyMap();
    }
    
    @Override
    public ApplicationAttemptHistoryData getApplicationAttempt(final ApplicationAttemptId appAttemptId) throws IOException {
        return null;
    }
    
    @Override
    public ContainerHistoryData getContainer(final ContainerId containerId) throws IOException {
        return null;
    }
    
    @Override
    public ContainerHistoryData getAMContainer(final ApplicationAttemptId appAttemptId) throws IOException {
        return null;
    }
    
    @Override
    public Map<ContainerId, ContainerHistoryData> getContainers(final ApplicationAttemptId appAttemptId) throws IOException {
        return Collections.emptyMap();
    }
}
