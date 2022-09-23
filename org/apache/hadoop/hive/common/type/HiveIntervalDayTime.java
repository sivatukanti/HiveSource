// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.type;

import java.util.regex.Pattern;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.hive.common.util.DateUtils;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.util.regex.Matcher;

public class HiveIntervalDayTime implements Comparable<HiveIntervalDayTime>
{
    protected long totalSeconds;
    protected int nanos;
    private static final String PARSE_PATTERN = "([+|-])?(\\d+) (\\d+):(\\d+):((\\d+)(\\.(\\d+))?)";
    private static final ThreadLocal<Matcher> PATTERN_MATCHER;
    
    public HiveIntervalDayTime() {
    }
    
    public HiveIntervalDayTime(final int days, final int hours, final int minutes, final int seconds, final int nanos) {
        this.set(days, hours, minutes, seconds, nanos);
    }
    
    public HiveIntervalDayTime(final long seconds, final int nanos) {
        this.set(seconds, nanos);
    }
    
    public HiveIntervalDayTime(final BigDecimal seconds) {
        this.set(seconds);
    }
    
    public HiveIntervalDayTime(final HiveIntervalDayTime other) {
        this.set(other.totalSeconds, other.nanos);
    }
    
    public int getDays() {
        return (int)TimeUnit.SECONDS.toDays(this.totalSeconds);
    }
    
    public int getHours() {
        return (int)(TimeUnit.SECONDS.toHours(this.totalSeconds) % TimeUnit.DAYS.toHours(1L));
    }
    
    public int getMinutes() {
        return (int)(TimeUnit.SECONDS.toMinutes(this.totalSeconds) % TimeUnit.HOURS.toMinutes(1L));
    }
    
    public int getSeconds() {
        return (int)(this.totalSeconds % TimeUnit.MINUTES.toSeconds(1L));
    }
    
    public int getNanos() {
        return this.nanos;
    }
    
    public long getTotalSeconds() {
        return this.totalSeconds;
    }
    
    protected void normalizeSecondsAndNanos() {
        if (this.totalSeconds > 0L && this.nanos < 0) {
            --this.totalSeconds;
            this.nanos += 1000000000;
        }
        else if (this.totalSeconds < 0L && this.nanos > 0) {
            ++this.totalSeconds;
            this.nanos -= 1000000000;
        }
    }
    
    public void set(final int days, final int hours, final int minutes, final int seconds, int nanos) {
        long totalSeconds = seconds;
        totalSeconds += TimeUnit.DAYS.toSeconds(days);
        totalSeconds += TimeUnit.HOURS.toSeconds(hours);
        totalSeconds += TimeUnit.MINUTES.toSeconds(minutes);
        totalSeconds += TimeUnit.NANOSECONDS.toSeconds(nanos);
        nanos %= 1000000000;
        this.totalSeconds = totalSeconds;
        this.nanos = nanos;
        this.normalizeSecondsAndNanos();
    }
    
    public void set(final long seconds, final int nanos) {
        this.totalSeconds = seconds;
        this.nanos = nanos;
        this.normalizeSecondsAndNanos();
    }
    
    public void set(final BigDecimal totalSecondsBd) {
        final long totalSeconds = totalSecondsBd.longValue();
        final BigDecimal fractionalSecs = totalSecondsBd.remainder(BigDecimal.ONE);
        final int nanos = fractionalSecs.multiply(DateUtils.NANOS_PER_SEC_BD).intValue();
        this.set(totalSeconds, nanos);
    }
    
    public void set(final HiveIntervalDayTime other) {
        this.set(other.getTotalSeconds(), other.getNanos());
    }
    
    public HiveIntervalDayTime negate() {
        return new HiveIntervalDayTime(-this.getTotalSeconds(), -this.getNanos());
    }
    
    @Override
    public int compareTo(final HiveIntervalDayTime other) {
        long cmp = this.totalSeconds - other.totalSeconds;
        if (cmp == 0L) {
            cmp = this.nanos - other.nanos;
        }
        if (cmp != 0L) {
            cmp = ((cmp > 0L) ? 1L : -1L);
        }
        return (int)cmp;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof HiveIntervalDayTime && 0 == this.compareTo((HiveIntervalDayTime)obj));
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.totalSeconds).append(this.nanos).toHashCode();
    }
    
    @Override
    public String toString() {
        final boolean isNegative = this.totalSeconds < 0L || this.nanos < 0;
        final String daySecondSignStr = isNegative ? "-" : "";
        return String.format("%s%d %02d:%02d:%02d.%09d", daySecondSignStr, Math.abs(this.getDays()), Math.abs(this.getHours()), Math.abs(this.getMinutes()), Math.abs(this.getSeconds()), Math.abs(this.getNanos()));
    }
    
    public static HiveIntervalDayTime valueOf(final String strVal) {
        HiveIntervalDayTime result = null;
        if (strVal == null) {
            throw new IllegalArgumentException("Interval day-time string was null");
        }
        final Matcher patternMatcher = HiveIntervalDayTime.PATTERN_MATCHER.get();
        patternMatcher.reset(strVal);
        if (patternMatcher.matches()) {
            try {
                int sign = 1;
                String field = patternMatcher.group(1);
                if (field != null && field.equals("-")) {
                    sign = -1;
                }
                final int days = sign * DateUtils.parseNumericValueWithRange("day", patternMatcher.group(2), 0, Integer.MAX_VALUE);
                final byte hours = (byte)(sign * DateUtils.parseNumericValueWithRange("hour", patternMatcher.group(3), 0, 23));
                final byte minutes = (byte)(sign * DateUtils.parseNumericValueWithRange("minute", patternMatcher.group(4), 0, 59));
                int seconds = 0;
                int nanos = 0;
                field = patternMatcher.group(5);
                if (field != null) {
                    final BigDecimal bdSeconds = new BigDecimal(field);
                    if (bdSeconds.compareTo(DateUtils.MAX_INT_BD) > 0) {
                        throw new IllegalArgumentException("seconds value of " + bdSeconds + " too large");
                    }
                    seconds = sign * bdSeconds.intValue();
                    nanos = sign * bdSeconds.subtract(new BigDecimal(bdSeconds.toBigInteger())).multiply(DateUtils.NANOS_PER_SEC_BD).intValue();
                }
                result = new HiveIntervalDayTime(days, hours, minutes, seconds, nanos);
                return result;
            }
            catch (Exception err) {
                throw new IllegalArgumentException("Error parsing interval day-time string: " + strVal, err);
            }
            throw new IllegalArgumentException("Interval string does not match day-time format of 'd h:m:s.n': " + strVal);
        }
        throw new IllegalArgumentException("Interval string does not match day-time format of 'd h:m:s.n': " + strVal);
    }
    
    static {
        PATTERN_MATCHER = new ThreadLocal<Matcher>() {
            @Override
            protected Matcher initialValue() {
                return Pattern.compile("([+|-])?(\\d+) (\\d+):(\\d+):((\\d+)(\\.(\\d+))?)").matcher("");
            }
        };
    }
}
