// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.timer;

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Java5SingletonTimerFactory extends SingletonTimerFactory
{
    private final AtomicInteger cancelCount;
    
    public Java5SingletonTimerFactory() {
        this.cancelCount = new AtomicInteger();
    }
    
    @Override
    public void cancel(final TimerTask timerTask) {
        super.cancel(timerTask);
        if (this.cancelCount.incrementAndGet() % 1000 == 0) {
            this.getCancellationTimer().purge();
        }
    }
}
