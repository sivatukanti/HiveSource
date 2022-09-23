// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public final class Time
{
    private static final long NANOSECONDS_PER_MILLISECOND = 1000000L;
    private static final TimeZone UTC_ZONE;
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT;
    
    public static long now() {
        return System.currentTimeMillis();
    }
    
    public static long monotonicNow() {
        return System.nanoTime() / 1000000L;
    }
    
    public static long monotonicNowNanos() {
        return System.nanoTime();
    }
    
    public static String formatTime(final long millis) {
        return Time.DATE_FORMAT.get().format(millis);
    }
    
    public static long getUtcTime() {
        return Calendar.getInstance(Time.UTC_ZONE).getTimeInMillis();
    }
    
    static {
        UTC_ZONE = TimeZone.getTimeZone("UTC");
        DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSSZ");
            }
        };
    }
}
