// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import org.eclipse.jetty.util.component.Destroyable;
import java.util.Collection;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.component.LifeCycle;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;

public class ShutdownThread extends Thread
{
    private static final Logger LOG;
    private static final ShutdownThread _thread;
    private boolean _hooked;
    private final List<LifeCycle> _lifeCycles;
    
    private ShutdownThread() {
        this._lifeCycles = new CopyOnWriteArrayList<LifeCycle>();
    }
    
    private synchronized void hook() {
        try {
            if (!this._hooked) {
                Runtime.getRuntime().addShutdownHook(this);
            }
            this._hooked = true;
        }
        catch (Exception e) {
            ShutdownThread.LOG.ignore(e);
            ShutdownThread.LOG.info("shutdown already commenced", new Object[0]);
        }
    }
    
    private synchronized void unhook() {
        try {
            this._hooked = false;
            Runtime.getRuntime().removeShutdownHook(this);
        }
        catch (Exception e) {
            ShutdownThread.LOG.ignore(e);
            ShutdownThread.LOG.debug("shutdown already commenced", new Object[0]);
        }
    }
    
    public static ShutdownThread getInstance() {
        return ShutdownThread._thread;
    }
    
    public static synchronized void register(final LifeCycle... lifeCycles) {
        ShutdownThread._thread._lifeCycles.addAll(Arrays.asList(lifeCycles));
        if (ShutdownThread._thread._lifeCycles.size() > 0) {
            ShutdownThread._thread.hook();
        }
    }
    
    public static synchronized void register(final int index, final LifeCycle... lifeCycles) {
        ShutdownThread._thread._lifeCycles.addAll(index, Arrays.asList(lifeCycles));
        if (ShutdownThread._thread._lifeCycles.size() > 0) {
            ShutdownThread._thread.hook();
        }
    }
    
    public static synchronized void deregister(final LifeCycle lifeCycle) {
        ShutdownThread._thread._lifeCycles.remove(lifeCycle);
        if (ShutdownThread._thread._lifeCycles.size() == 0) {
            ShutdownThread._thread.unhook();
        }
    }
    
    public static synchronized boolean isRegistered(final LifeCycle lifeCycle) {
        return ShutdownThread._thread._lifeCycles.contains(lifeCycle);
    }
    
    @Override
    public void run() {
        for (final LifeCycle lifeCycle : ShutdownThread._thread._lifeCycles) {
            try {
                if (lifeCycle.isStarted()) {
                    lifeCycle.stop();
                    ShutdownThread.LOG.debug("Stopped {}", lifeCycle);
                }
                if (!(lifeCycle instanceof Destroyable)) {
                    continue;
                }
                ((Destroyable)lifeCycle).destroy();
                ShutdownThread.LOG.debug("Destroyed {}", lifeCycle);
            }
            catch (Exception ex) {
                ShutdownThread.LOG.debug(ex);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(ShutdownThread.class);
        _thread = new ShutdownThread();
    }
}
