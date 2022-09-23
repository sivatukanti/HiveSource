// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.concurrent;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

public interface AsyncGet<R, E extends Throwable>
{
    R get(final long p0, final TimeUnit p1) throws E, TimeoutException, InterruptedException, Throwable;
    
    boolean isDone();
    
    public static class Util
    {
        public static void wait(final Object obj, final long timeout, final TimeUnit unit) throws InterruptedException {
            if (timeout < 0L) {
                obj.wait();
            }
            else if (timeout > 0L) {
                obj.wait(unit.toMillis(timeout));
            }
        }
    }
}
