// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.common;

import java.util.Date;

public class Time
{
    public static long currentElapsedTime() {
        return System.nanoTime() / 1000000L;
    }
    
    public static long currentWallTime() {
        return System.currentTimeMillis();
    }
    
    public static Date elapsedTimeToDate(final long elapsedTime) {
        final long wallTime = currentWallTime() + elapsedTime - currentElapsedTime();
        return new Date(wallTime);
    }
}
