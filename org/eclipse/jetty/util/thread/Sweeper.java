// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import org.eclipse.jetty.util.log.Log;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class Sweeper extends AbstractLifeCycle implements Runnable
{
    private static final Logger LOG;
    private final AtomicReference<List<Sweepable>> items;
    private final AtomicReference<Scheduler.Task> task;
    private final Scheduler scheduler;
    private final long period;
    
    public Sweeper(final Scheduler scheduler, final long period) {
        this.items = new AtomicReference<List<Sweepable>>();
        this.task = new AtomicReference<Scheduler.Task>();
        this.scheduler = scheduler;
        this.period = period;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this.items.set(new CopyOnWriteArrayList<Sweepable>());
        this.activate();
    }
    
    @Override
    protected void doStop() throws Exception {
        this.deactivate();
        this.items.set(null);
        super.doStop();
    }
    
    public int getSize() {
        final List<Sweepable> refs = this.items.get();
        return (refs == null) ? 0 : refs.size();
    }
    
    public boolean offer(final Sweepable sweepable) {
        final List<Sweepable> refs = this.items.get();
        if (refs == null) {
            return false;
        }
        refs.add(sweepable);
        if (Sweeper.LOG.isDebugEnabled()) {
            Sweeper.LOG.debug("Resource offered {}", sweepable);
        }
        return true;
    }
    
    public boolean remove(final Sweepable sweepable) {
        final List<Sweepable> refs = this.items.get();
        return refs != null && refs.remove(sweepable);
    }
    
    @Override
    public void run() {
        final List<Sweepable> refs = this.items.get();
        if (refs == null) {
            return;
        }
        for (final Sweepable sweepable : refs) {
            try {
                if (!sweepable.sweep()) {
                    continue;
                }
                refs.remove(sweepable);
                if (!Sweeper.LOG.isDebugEnabled()) {
                    continue;
                }
                Sweeper.LOG.debug("Resource swept {}", sweepable);
            }
            catch (Throwable x) {
                Sweeper.LOG.info("Exception while sweeping " + sweepable, x);
            }
        }
        this.activate();
    }
    
    private void activate() {
        if (this.isRunning()) {
            final Scheduler.Task t = this.scheduler.schedule(this, this.period, TimeUnit.MILLISECONDS);
            if (Sweeper.LOG.isDebugEnabled()) {
                Sweeper.LOG.debug("Scheduled in {} ms sweep task {}", this.period, t);
            }
            this.task.set(t);
        }
        else if (Sweeper.LOG.isDebugEnabled()) {
            Sweeper.LOG.debug("Skipping sweep task scheduling", new Object[0]);
        }
    }
    
    private void deactivate() {
        final Scheduler.Task t = this.task.getAndSet(null);
        if (t != null) {
            final boolean cancelled = t.cancel();
            if (Sweeper.LOG.isDebugEnabled()) {
                Sweeper.LOG.debug("Cancelled ({}) sweep task {}", cancelled, t);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(Sweeper.class);
    }
    
    public interface Sweepable
    {
        boolean sweep();
    }
}
