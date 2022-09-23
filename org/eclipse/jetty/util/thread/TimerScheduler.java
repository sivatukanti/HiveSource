// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import org.eclipse.jetty.util.log.Log;
import java.util.TimerTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class TimerScheduler extends AbstractLifeCycle implements Scheduler, Runnable
{
    private static final Logger LOG;
    private final String _name;
    private final boolean _daemon;
    private Timer _timer;
    
    public TimerScheduler() {
        this(null, false);
    }
    
    public TimerScheduler(final String name, final boolean daemon) {
        this._name = name;
        this._daemon = daemon;
    }
    
    @Override
    protected void doStart() throws Exception {
        this._timer = ((this._name == null) ? new Timer() : new Timer(this._name, this._daemon));
        this.run();
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        this._timer.cancel();
        super.doStop();
        this._timer = null;
    }
    
    @Override
    public Task schedule(final Runnable task, final long delay, final TimeUnit units) {
        final Timer timer = this._timer;
        if (timer == null) {
            throw new RejectedExecutionException("STOPPED: " + this);
        }
        final SimpleTask t = new SimpleTask(task);
        timer.schedule(t, units.toMillis(delay));
        return t;
    }
    
    @Override
    public void run() {
        final Timer timer = this._timer;
        if (timer != null) {
            timer.purge();
            this.schedule(this, 1L, TimeUnit.SECONDS);
        }
    }
    
    static {
        LOG = Log.getLogger(TimerScheduler.class);
    }
    
    private static class SimpleTask extends TimerTask implements Task
    {
        private final Runnable _task;
        
        private SimpleTask(final Runnable runnable) {
            this._task = runnable;
        }
        
        @Override
        public void run() {
            try {
                this._task.run();
            }
            catch (Throwable x) {
                TimerScheduler.LOG.warn("Exception while executing task " + this._task, x);
            }
        }
        
        @Override
        public String toString() {
            return String.format("%s.%s@%x", TimerScheduler.class.getSimpleName(), SimpleTask.class.getSimpleName(), this.hashCode());
        }
    }
}
