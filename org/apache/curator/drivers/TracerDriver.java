// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.drivers;

import java.util.concurrent.TimeUnit;

public interface TracerDriver
{
    void addTrace(final String p0, final long p1, final TimeUnit p2);
    
    void addCount(final String p0, final int p1);
}
