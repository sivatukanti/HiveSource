// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator;

import java.util.concurrent.TimeUnit;

public interface RetrySleeper
{
    void sleepFor(final long p0, final TimeUnit p1) throws InterruptedException;
}
