// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread;

import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.component.LifeCycle;

public interface Scheduler extends LifeCycle
{
    Task schedule(final Runnable p0, final long p1, final TimeUnit p2);
    
    public interface Task
    {
        boolean cancel();
    }
}
