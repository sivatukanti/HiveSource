// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util;

public interface Timeout
{
    Timer getTimer();
    
    TimerTask getTask();
    
    boolean isExpired();
    
    boolean isCancelled();
    
    void cancel();
}
