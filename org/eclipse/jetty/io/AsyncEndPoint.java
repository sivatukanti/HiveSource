// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.thread.Timeout;

public interface AsyncEndPoint extends ConnectedEndPoint
{
    void asyncDispatch();
    
    void scheduleWrite();
    
    void onIdleExpired(final long p0);
    
    void setCheckForIdle(final boolean p0);
    
    boolean isCheckForIdle();
    
    boolean isWritable();
    
    boolean hasProgressed();
    
    void scheduleTimeout(final Timeout.Task p0, final long p1);
    
    void cancelTimeout(final Timeout.Task p0);
}
