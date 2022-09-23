// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmcontainer;

import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.ContainerExpiredSchedulerEvent;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.SystemClock;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.util.AbstractLivelinessMonitor;

public class ContainerAllocationExpirer extends AbstractLivelinessMonitor<ContainerId>
{
    private EventHandler dispatcher;
    
    public ContainerAllocationExpirer(final Dispatcher d) {
        super(ContainerAllocationExpirer.class.getName(), new SystemClock());
        this.dispatcher = d.getEventHandler();
    }
    
    public void serviceInit(final Configuration conf) throws Exception {
        final int expireIntvl = conf.getInt("yarn.resourcemanager.rm.container-allocation.expiry-interval-ms", 600000);
        this.setExpireInterval(expireIntvl);
        this.setMonitorInterval(expireIntvl / 3);
        super.serviceInit(conf);
    }
    
    @Override
    protected void expire(final ContainerId containerId) {
        this.dispatcher.handle(new ContainerExpiredSchedulerEvent(containerId));
    }
}
