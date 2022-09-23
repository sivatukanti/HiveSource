// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import java.util.concurrent.TimeUnit;

public interface InterProcessLock
{
    void acquire() throws Exception;
    
    boolean acquire(final long p0, final TimeUnit p1) throws Exception;
    
    void release() throws Exception;
    
    boolean isAcquiredInThisProcess();
}
