// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.thread;

public interface ThreadPool
{
    boolean dispatch(final Runnable p0);
    
    void join() throws InterruptedException;
    
    int getThreads();
    
    int getIdleThreads();
    
    boolean isLowOnThreads();
}
