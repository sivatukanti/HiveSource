// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class SchedulerApplication<T extends SchedulerApplicationAttempt>
{
    private Queue queue;
    private final String user;
    private T currentAttempt;
    
    public SchedulerApplication(final Queue queue, final String user) {
        this.queue = queue;
        this.user = user;
    }
    
    public Queue getQueue() {
        return this.queue;
    }
    
    public void setQueue(final Queue queue) {
        this.queue = queue;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public T getCurrentAppAttempt() {
        return this.currentAttempt;
    }
    
    public void setCurrentAppAttempt(final T currentAttempt) {
        this.currentAttempt = currentAttempt;
    }
    
    public void stop(final RMAppState rmAppFinalState) {
        this.queue.getMetrics().finishApp(this.user, rmAppFinalState);
    }
}
