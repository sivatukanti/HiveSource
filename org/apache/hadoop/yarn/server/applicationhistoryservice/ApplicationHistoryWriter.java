// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice;

import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerFinishData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerStartData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptFinishData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptStartData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationFinishData;
import java.io.IOException;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationStartData;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public interface ApplicationHistoryWriter
{
    void applicationStarted(final ApplicationStartData p0) throws IOException;
    
    void applicationFinished(final ApplicationFinishData p0) throws IOException;
    
    void applicationAttemptStarted(final ApplicationAttemptStartData p0) throws IOException;
    
    void applicationAttemptFinished(final ApplicationAttemptFinishData p0) throws IOException;
    
    void containerStarted(final ContainerStartData p0) throws IOException;
    
    void containerFinished(final ContainerFinishData p0) throws IOException;
}
