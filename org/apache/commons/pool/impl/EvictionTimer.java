// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.pool.impl;

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
            EvictionTimer._timer = new Timer(true);
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
}
