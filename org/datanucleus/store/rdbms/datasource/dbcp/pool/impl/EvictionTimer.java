// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.datasource.dbcp.pool.impl;

import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.TimerTask;
import java.util.Timer;

class EvictionTimer
{
    private static Timer _timer;
    private static int _usageCount;
    
    private EvictionTimer() {
    }
    
    static synchronized void schedule(final TimerTask task, final long delay, final long period) {
        if (null == EvictionTimer._timer) {
            final ClassLoader ccl = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedGetTccl());
            try {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedSetTccl(EvictionTimer.class.getClassLoader()));
                EvictionTimer._timer = new Timer(true);
            }
            finally {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedSetTccl(ccl));
            }
        }
        ++EvictionTimer._usageCount;
        EvictionTimer._timer.schedule(task, delay, period);
    }
    
    static synchronized void cancel(final TimerTask task) {
        task.cancel();
        --EvictionTimer._usageCount;
        if (EvictionTimer._usageCount == 0) {
            EvictionTimer._timer.cancel();
            EvictionTimer._timer = null;
        }
    }
    
    private static class PrivilegedGetTccl implements PrivilegedAction
    {
        @Override
        public Object run() {
            return Thread.currentThread().getContextClassLoader();
        }
    }
    
    private static class PrivilegedSetTccl implements PrivilegedAction
    {
        private final ClassLoader cl;
        
        PrivilegedSetTccl(final ClassLoader cl) {
            this.cl = cl;
        }
        
        @Override
        public Object run() {
            Thread.currentThread().setContextClassLoader(this.cl);
            return null;
        }
    }
}
