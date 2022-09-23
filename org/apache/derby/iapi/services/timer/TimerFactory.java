// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.timer;

import java.util.TimerTask;

public interface TimerFactory
{
    void schedule(final TimerTask p0, final long p1);
    
    void cancel(final TimerTask p0);
}
