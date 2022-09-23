// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.daemon;

public interface DaemonService
{
    public static final int TIMER_DELAY = 10000;
    public static final String DaemonTrace = null;
    public static final String DaemonOff = null;
    
    int subscribe(final Serviceable p0, final boolean p1);
    
    void unsubscribe(final int p0);
    
    void serviceNow(final int p0);
    
    boolean enqueue(final Serviceable p0, final boolean p1);
    
    void pause();
    
    void resume();
    
    void stop();
    
    void clear();
    
    void waitUntilQueueIsEmpty();
}
