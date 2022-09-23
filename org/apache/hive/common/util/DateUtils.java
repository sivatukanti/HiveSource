// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class DateUtils
{
    private static final ThreadLocal<SimpleDateFormat> dateFormatLocal;
    public static final int NANOS_PER_SEC = 1000000000;
    public static final BigDecimal MAX_INT_BD;
    public static final BigDecimal NANOS_PER_SEC_BD;
    
    public static SimpleDateFormat getDateFormat() {
        return DateUtils.dateFormatLocal.get();
    }
    
    public static int parseNumericValueWithRange(final String fieldName, final String strVal, final int minValue, final int maxValue) throws IllegalArgumentException {
        int result = 0;
        if (strVal != null) {
            result = Integer.parseInt(strVal);
            if (result < minValue || result > maxValue) {
                throw new IllegalArgumentException(String.format("%s value %d outside range [%d, %d]", fieldName, result, minValue, maxValue));
            }
        }
        return result;
    }
    
    public static long getIntervalDayTimeTotalNanos(final HiveIntervalDayTime intervalDayTime) {
        return intervalDayTime.getTotalSeconds() * 1000000000L + intervalDayTime.getNanos();
    }
    
    public static void setIntervalDayTimeTotalNanos(final HiveIntervalDayTime intervalDayTime, final long totalNanos) {
        intervalDayTime.set(totalNanos / 1000000000L, (int)(totalNanos % 1000000000L));
    }
    
    public static long getIntervalDayTimeTotalSecondsFromTotalNanos(final long totalNanos) {
        return totalNanos / 1000000000L;
    }
    
    public static int getIntervalDayTimeNanosFromTotalNanos(final long totalNanos) {
        return (int)(totalNanos % 1000000000L);
    }
    
    static {
        dateFormatLocal = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd");
            }
        };
        MAX_INT_BD = new BigDecimal(Integer.MAX_VALUE);
        NANOS_PER_SEC_BD = new BigDecimal(1000000000);
    }
}
