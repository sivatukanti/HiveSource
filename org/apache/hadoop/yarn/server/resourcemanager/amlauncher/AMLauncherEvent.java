// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.amlauncher;

import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.event.AbstractEvent;

public class AMLauncherEvent extends AbstractEvent<AMLauncherEventType>
{
    private final RMAppAttempt appAttempt;
    
    public AMLauncherEvent(final AMLauncherEventType type, final RMAppAttempt appAttempt) {
        super(type);
        this.appAttempt = appAttempt;
    }
    
    public RMAppAttempt getAppAttempt() {
        return this.appAttempt;
    }
}
