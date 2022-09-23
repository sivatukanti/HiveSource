// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.commons.logging.Log;

public class RMAppAttemptMetrics
{
    private static final Log LOG;
    private ApplicationAttemptId attemptId;
    private Resource resourcePreempted;
    private AtomicInteger numNonAMContainersPreempted;
    private AtomicBoolean isPreempted;
    private ReentrantReadWriteLock.ReadLock readLock;
    private ReentrantReadWriteLock.WriteLock writeLock;
    private AtomicLong finishedMemorySeconds;
    private AtomicLong finishedVcoreSeconds;
    private RMContext rmContext;
    
    public RMAppAttemptMetrics(final ApplicationAttemptId attemptId, final RMContext rmContext) {
        this.attemptId = null;
        this.resourcePreempted = Resource.newInstance(0, 0);
        this.numNonAMContainersPreempted = new AtomicInteger(0);
        this.isPreempted = new AtomicBoolean(false);
        this.finishedMemorySeconds = new AtomicLong(0L);
        this.finishedVcoreSeconds = new AtomicLong(0L);
        this.attemptId = attemptId;
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
        this.rmContext = rmContext;
    }
    
    public void updatePreemptionInfo(final Resource resource, final RMContainer container) {
        try {
            this.writeLock.lock();
            this.resourcePreempted = Resources.addTo(this.resourcePreempted, resource);
        }
        finally {
            this.writeLock.unlock();
        }
        if (!container.isAMContainer()) {
            RMAppAttemptMetrics.LOG.info(String.format("Non-AM container preempted, current appAttemptId=%s, containerId=%s, resource=%s", this.attemptId, container.getContainerId(), resource));
            this.numNonAMContainersPreempted.incrementAndGet();
        }
        else {
            RMAppAttemptMetrics.LOG.info(String.format("AM container preempted, current appAttemptId=%s, containerId=%s, resource=%s", this.attemptId, container.getContainerId(), resource));
            this.isPreempted.set(true);
        }
    }
    
    public Resource getResourcePreempted() {
        try {
            this.readLock.lock();
            return this.resourcePreempted;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public int getNumNonAMContainersPreempted() {
        return this.numNonAMContainersPreempted.get();
    }
    
    public void setIsPreempted() {
        this.isPreempted.set(true);
    }
    
    public boolean getIsPreempted() {
        return this.isPreempted.get();
    }
    
    public AggregateAppResourceUsage getAggregateAppResourceUsage() {
        long memorySeconds = this.finishedMemorySeconds.get();
        long vcoreSeconds = this.finishedVcoreSeconds.get();
        final RMAppAttempt currentAttempt = this.rmContext.getRMApps().get(this.attemptId.getApplicationId()).getCurrentAppAttempt();
        if (currentAttempt.getAppAttemptId().equals(this.attemptId)) {
            final ApplicationResourceUsageReport appResUsageReport = this.rmContext.getScheduler().getAppResourceUsageReport(this.attemptId);
            if (appResUsageReport != null) {
                memorySeconds += appResUsageReport.getMemorySeconds();
                vcoreSeconds += appResUsageReport.getVcoreSeconds();
            }
        }
        return new AggregateAppResourceUsage(memorySeconds, vcoreSeconds);
    }
    
    public void updateAggregateAppResourceUsage(final long finishedMemorySeconds, final long finishedVcoreSeconds) {
        this.finishedMemorySeconds.addAndGet(finishedMemorySeconds);
        this.finishedVcoreSeconds.addAndGet(finishedVcoreSeconds);
    }
    
    static {
        LOG = LogFactory.getLog(RMAppAttemptMetrics.class);
    }
}
