// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.monitor;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ContainerPreemptEvent;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.PreemptableResourceScheduler;
import org.apache.hadoop.conf.Configuration;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.commons.logging.Log;
import org.apache.hadoop.service.AbstractService;

public class SchedulingMonitor extends AbstractService
{
    private final SchedulingEditPolicy scheduleEditPolicy;
    private static final Log LOG;
    private Thread checkerThread;
    private volatile boolean stopped;
    private long monitorInterval;
    private RMContext rmContext;
    
    public SchedulingMonitor(final RMContext rmContext, final SchedulingEditPolicy scheduleEditPolicy) {
        super("SchedulingMonitor (" + scheduleEditPolicy.getPolicyName() + ")");
        this.scheduleEditPolicy = scheduleEditPolicy;
        this.rmContext = rmContext;
    }
    
    public long getMonitorInterval() {
        return this.monitorInterval;
    }
    
    @VisibleForTesting
    public synchronized SchedulingEditPolicy getSchedulingEditPolicy() {
        return this.scheduleEditPolicy;
    }
    
    public void serviceInit(final Configuration conf) throws Exception {
        this.scheduleEditPolicy.init(conf, this.rmContext.getDispatcher().getEventHandler(), (PreemptableResourceScheduler)this.rmContext.getScheduler());
        this.monitorInterval = this.scheduleEditPolicy.getMonitoringInterval();
        super.serviceInit(conf);
    }
    
    public void serviceStart() throws Exception {
        assert !this.stopped : "starting when already stopped";
        (this.checkerThread = new Thread(new PreemptionChecker())).setName(this.getName());
        this.checkerThread.start();
        super.serviceStart();
    }
    
    public void serviceStop() throws Exception {
        this.stopped = true;
        if (this.checkerThread != null) {
            this.checkerThread.interrupt();
        }
        super.serviceStop();
    }
    
    @VisibleForTesting
    public void invokePolicy() {
        this.scheduleEditPolicy.editSchedule();
    }
    
    static {
        LOG = LogFactory.getLog(SchedulingMonitor.class);
    }
    
    private class PreemptionChecker implements Runnable
    {
        @Override
        public void run() {
            while (!SchedulingMonitor.this.stopped && !Thread.currentThread().isInterrupted()) {
                SchedulingMonitor.this.invokePolicy();
                try {
                    Thread.sleep(SchedulingMonitor.this.monitorInterval);
                    continue;
                }
                catch (InterruptedException e) {
                    SchedulingMonitor.LOG.info(SchedulingMonitor.this.getName() + " thread interrupted");
                }
                break;
            }
        }
    }
}
