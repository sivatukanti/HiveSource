// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator;

public interface RetryPolicy
{
    boolean allowRetry(final int p0, final long p1, final RetrySleeper p2);
}
