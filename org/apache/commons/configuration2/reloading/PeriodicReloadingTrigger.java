// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.reloading;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

public class PeriodicReloadingTrigger
{
    private final ScheduledExecutorService executorService;
    private final ReloadingController controller;
    private final Object controllerParam;
    private final long period;
    private final TimeUnit timeUnit;
    private ScheduledFuture<?> triggerTask;
    
    public PeriodicReloadingTrigger(final ReloadingController ctrl, final Object ctrlParam, final long triggerPeriod, final TimeUnit unit, final ScheduledExecutorService exec) {
        if (ctrl == null) {
            throw new IllegalArgumentException("ReloadingController must not be null!");
        }
        this.controller = ctrl;
        this.controllerParam = ctrlParam;
        this.period = triggerPeriod;
        this.timeUnit = unit;
        this.executorService = ((exec != null) ? exec : createDefaultExecutorService());
    }
    
    public PeriodicReloadingTrigger(final ReloadingController ctrl, final Object ctrlParam, final long triggerPeriod, final TimeUnit unit) {
        this(ctrl, ctrlParam, triggerPeriod, unit, null);
    }
    
    public synchronized void start() {
        if (!this.isRunning()) {
            this.triggerTask = this.getExecutorService().scheduleAtFixedRate(this.createTriggerTaskCommand(), this.period, this.period, this.timeUnit);
        }
    }
    
    public synchronized void stop() {
        if (this.isRunning()) {
            this.triggerTask.cancel(false);
            this.triggerTask = null;
        }
    }
    
    public synchronized boolean isRunning() {
        return this.triggerTask != null;
    }
    
    public void shutdown(final boolean shutdownExecutor) {
        this.stop();
        if (shutdownExecutor) {
            this.getExecutorService().shutdown();
        }
    }
    
    public void shutdown() {
        this.shutdown(true);
    }
    
    ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }
    
    private Runnable createTriggerTaskCommand() {
        return new Runnable() {
            @Override
            public void run() {
                PeriodicReloadingTrigger.this.controller.checkForReloading(PeriodicReloadingTrigger.this.controllerParam);
            }
        };
    }
    
    private static ScheduledExecutorService createDefaultExecutorService() {
        final ThreadFactory factory = new BasicThreadFactory.Builder().namingPattern("ReloadingTrigger-%s").daemon(true).build();
        return Executors.newScheduledThreadPool(1, factory);
    }
}
