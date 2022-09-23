// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.utils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.FutureTask;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class CloseableScheduledExecutorService extends CloseableExecutorService
{
    private final ScheduledExecutorService scheduledExecutorService;
    
    public CloseableScheduledExecutorService(final ScheduledExecutorService scheduledExecutorService) {
        super(scheduledExecutorService, false);
        this.scheduledExecutorService = scheduledExecutorService;
    }
    
    public CloseableScheduledExecutorService(final ScheduledExecutorService scheduledExecutorService, final boolean shutdownOnClose) {
        super(scheduledExecutorService, shutdownOnClose);
        this.scheduledExecutorService = scheduledExecutorService;
    }
    
    public Future<?> schedule(final Runnable task, final long delay, final TimeUnit unit) {
        Preconditions.checkState(this.isOpen.get(), (Object)"CloseableExecutorService is closed");
        final InternalFutureTask<Void> futureTask = new InternalFutureTask<Void>(new FutureTask<Void>(task, null));
        this.scheduledExecutorService.schedule(futureTask, delay, unit);
        return futureTask;
    }
    
    public Future<?> scheduleWithFixedDelay(final Runnable task, final long initialDelay, final long delay, final TimeUnit unit) {
        Preconditions.checkState(this.isOpen.get(), (Object)"CloseableExecutorService is closed");
        final ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(task, initialDelay, delay, unit);
        return new InternalScheduledFutureTask(scheduledFuture);
    }
}
