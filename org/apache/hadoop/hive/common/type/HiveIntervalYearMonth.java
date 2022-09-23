// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.type;

import java.util.regex.Pattern;
import org.apache.hive.common.util.DateUtils;
import java.util.regex.Matcher;

public class HiveIntervalYearMonth implements Comparable<HiveIntervalYearMonth>
{
    protected int totalMonths;
    protected static final int MONTHS_PER_YEAR = 12;
    private static final String PARSE_PATTERN = "([+|-])?(\\d+)-(\\d+)";
    private static final ThreadLocal<Matcher> PATTERN_MATCHER;
    
    public HiveIntervalYearMonth() {
    }
    
    public HiveIntervalYearMonth(final int years, final int months) {
        this.set(years, months);
    }
    
    public HiveIntervalYearMonth(final int totalMonths) {
        this.set(totalMonths);
    }
    
    public HiveIntervalYearMonth(final HiveIntervalYearMonth hiveInterval) {
        this.set(hiveInterval.getTotalMonths());
    }
    
    public int getYears() {
        return this.totalMonths / 12;
    }
    
    public int getMonths() {
        return this.totalMonths % 12;
    }
    
    public int getTotalMonths() {
        return this.totalMonths;
    }
    
    public void set(final int years, final int months) {
        this.totalMonths = months;
        this.totalMonths += years * 12;
    }
    
    public void set(final int totalMonths) {
        this.totalMonths = totalMonths;
    }
    
    public void set(final HiveIntervalYearMonth other) {
        this.set(other.getTotalMonths());
    }
    
    public HiveIntervalYearMonth negate() {
        return new HiveIntervalYearMonth(-this.getTotalMonths());
    }
    
    @Override
    public int compareTo(final HiveIntervalYearMonth other) {
        int cmp = this.getTotalMonths() - other.getTotalMonths();
        if (cmp != 0) {
            cmp = ((cmp > 0) ? 1 : -1);
        }
        return cmp;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof HiveIntervalYearMonth && 0 == this.compareTo((HiveIntervalYearMonth)obj));
    }
    
    @Override
    public int hashCode() {
        return this.totalMonths;
    }
    
    @Override
    public String toString() {
        final String yearMonthSignStr = (this.totalMonths >= 0) ? "" : "-";
        return String.format("%s%d-%d", yearMonthSignStr, Math.abs(this.getYears()), Math.abs(this.getMonths()));
    }
    
    public static HiveIntervalYearMonth valueOf(final String strVal) {
        HiveIntervalYearMonth result = null;
        if (strVal == null) {
            throw new IllegalArgumentException("Interval year-month string was null");
        }
        final Matcher patternMatcher = HiveIntervalYearMonth.PATTERN_MATCHER.get();
        patternMatcher.reset(strVal);
        if (patternMatcher.matches()) {
            try {
                int sign = 1;
                final String field = patternMatcher.group(1);
                if (field != null && field.equals("-")) {
                    sign = -1;
                }
                final int years = sign * DateUtils.parseNumericValueWithRange("year", patternMatcher.group(2), 0, Integer.MAX_VALUE);
                final byte months = (byte)(sign * DateUtils.parseNumericValueWithRange("month", patternMatcher.group(3), 0, 11));
                result = new HiveIntervalYearMonth(years, months);
                return result;
            }
            catch (Exception err) {
                throw new IllegalArgumentException("Error parsing interval year-month string: " + strVal, err);
            }
            throw new IllegalArgumentException("Interval string does not match year-month format of 'y-m': " + strVal);
        }
        throw new IllegalArgumentException("Interval string does not match year-month format of 'y-m': " + strVal);
    }
    
    static {
        PATTERN_MATCHER = new ThreadLocal<Matcher>() {
            @Override
            protected Matcher initialValue() {
                return Pattern.compile("([+|-])?(\\d+)-(\\d+)").matcher("");
            }
        };
    }
}
