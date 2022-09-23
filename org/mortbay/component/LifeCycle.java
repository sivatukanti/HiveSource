// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.component;

import java.util.EventListener;

public interface LifeCycle
{
    void start() throws Exception;
    
    void stop() throws Exception;
    
    boolean isRunning();
    
    boolean isStarted();
    
    boolean isStarting();
    
    boolean isStopping();
    
    boolean isStopped();
    
    boolean isFailed();
    
    void addLifeCycleListener(final Listener p0);
    
    void removeLifeCycleListener(final Listener p0);
    
    public interface Listener extends EventListener
    {
        void lifeCycleStarting(final LifeCycle p0);
        
        void lifeCycleStarted(final LifeCycle p0);
        
        void lifeCycleFailure(final LifeCycle p0, final Throwable p1);
        
        void lifeCycleStopping(final LifeCycle p0);
        
        void lifeCycleStopped(final LifeCycle p0);
    }
}
