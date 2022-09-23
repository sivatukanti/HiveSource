// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.util.Date;

@Deprecated
public class DateUtils
{
    public static long toSecondsSinceEpoch(final Date date) {
        return date.getTime() / 1000L;
    }
    
    public static Date fromSecondsSinceEpoch(final long time) {
        return new Date(time * 1000L);
    }
    
    public static boolean isAfter(final Date date, final Date reference, final long maxClockSkewSeconds) {
        return new Date(date.getTime() + maxClockSkewSeconds * 1000L).after(reference);
    }
    
    public static boolean isBefore(final Date date, final Date reference, final long maxClockSkewSeconds) {
        return new Date(date.getTime() - maxClockSkewSeconds * 1000L).before(reference);
    }
    
    private DateUtils() {
    }
}
