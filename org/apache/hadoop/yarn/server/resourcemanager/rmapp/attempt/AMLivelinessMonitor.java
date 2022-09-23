// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.SystemClock;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.util.AbstractLivelinessMonitor;

public class AMLivelinessMonitor extends AbstractLivelinessMonitor<ApplicationAttemptId>
{
    private EventHandler dispatcher;
    
    public AMLivelinessMonitor(final Dispatcher d) {
        super("AMLivelinessMonitor", new SystemClock());
        this.dispatcher = d.getEventHandler();
    }
    
    public void serviceInit(final Configuration conf) throws Exception {
        super.serviceInit(conf);
        final int expireIntvl = conf.getInt("yarn.am.liveness-monitor.expiry-interval-ms", 600000);
        this.setExpireInterval(expireIntvl);
        this.setMonitorInterval(expireIntvl / 3);
    }
    
    @Override
    protected void expire(final ApplicationAttemptId id) {
        this.dispatcher.handle(new RMAppAttemptEvent(id, RMAppAttemptEventType.EXPIRE));
    }
}
