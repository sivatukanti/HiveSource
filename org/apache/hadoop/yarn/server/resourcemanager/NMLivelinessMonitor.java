// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEventType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.SystemClock;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.util.AbstractLivelinessMonitor;

public class NMLivelinessMonitor extends AbstractLivelinessMonitor<NodeId>
{
    private EventHandler dispatcher;
    
    public NMLivelinessMonitor(final Dispatcher d) {
        super("NMLivelinessMonitor", new SystemClock());
        this.dispatcher = d.getEventHandler();
    }
    
    public void serviceInit(final Configuration conf) throws Exception {
        final int expireIntvl = conf.getInt("yarn.nm.liveness-monitor.expiry-interval-ms", 600000);
        this.setExpireInterval(expireIntvl);
        this.setMonitorInterval(expireIntvl / 3);
        super.serviceInit(conf);
    }
    
    @Override
    protected void expire(final NodeId id) {
        this.dispatcher.handle(new RMNodeEvent(id, RMNodeEventType.EXPIRE));
    }
}
