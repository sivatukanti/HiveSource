// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.Chronology;

abstract class BasicGJChronology extends BasicChronology
{
    private static final long serialVersionUID = 538276888268L;
    private static final int[] MIN_DAYS_PER_MONTH_ARRAY;
    private static final int[] MAX_DAYS_PER_MONTH_ARRAY;
    private static final long[] MIN_TOTAL_MILLIS_BY_MONTH_ARRAY;
    private static final long[] MAX_TOTAL_MILLIS_BY_MONTH_ARRAY;
    private static final long FEB_29 = 5097600000L;
    
    BasicGJChronology(final Chronology chronology, final Object o, final int n) {
        super(chronology, o, n);
    }
    
    @Override
    boolean isLeapDay(final long n) {
        return this.dayOfMonth().get(n) == 29 && this.monthOfYear().isLeap(n);
    }
    
    @Override
    int getMonthOfYear(final long n, final int n2) {
        final int n3 = (int)(n - this.getYearMillis(n2) >> 10);
        return this.isLeapYear(n2) ? ((n3 < 15356250) ? ((n3 < 7678125) ? ((n3 < 2615625) ? 1 : ((n3 < 5062500) ? 2 : 3)) : ((n3 < 10209375) ? 4 : ((n3 < 12825000) ? 5 : 6))) : ((n3 < 23118750) ? ((n3 < 17971875) ? 7 : ((n3 < 20587500) ? 8 : 9)) : ((n3 < 25734375) ? 10 : ((n3 < 28265625) ? 11 : 12)))) : ((n3 < 15271875) ? ((n3 < 7593750) ? ((n3 < 2615625) ? 1 : ((n3 < 4978125) ? 2 : 3)) : ((n3 < 10125000) ? 4 : ((n3 < 12740625) ? 5 : 6))) : ((n3 < 23034375) ? ((n3 < 17887500) ? 7 : ((n3 < 20503125) ? 8 : 9)) : ((n3 < 25650000) ? 10 : ((n3 < 28181250) ? 11 : 12))));
    }
    
    @Override
    int getDaysInYearMonth(final int n, final int n2) {
        if (this.isLeapYear(n)) {
            return BasicGJChronology.MAX_DAYS_PER_MONTH_ARRAY[n2 - 1];
        }
        return BasicGJChronology.MIN_DAYS_PER_MONTH_ARRAY[n2 - 1];
    }
    
    @Override
    int getDaysInMonthMax(final int n) {
        return BasicGJChronology.MAX_DAYS_PER_MONTH_ARRAY[n - 1];
    }
    
    @Override
    int getDaysInMonthMaxForSet(final long n, final int n2) {
        return (n2 > 28 || n2 < 1) ? this.getDaysInMonthMax(n) : 28;
    }
    
    @Override
    long getTotalMillisByYearMonth(final int n, final int n2) {
        if (this.isLeapYear(n)) {
            return BasicGJChronology.MAX_TOTAL_MILLIS_BY_MONTH_ARRAY[n2 - 1];
        }
        return BasicGJChronology.MIN_TOTAL_MILLIS_BY_MONTH_ARRAY[n2 - 1];
    }
    
    @Override
    long getYearDifference(final long n, final long n2) {
        final int year = this.getYear(n);
        final int year2 = this.getYear(n2);
        long n3 = n - this.getYearMillis(year);
        long n4 = n2 - this.getYearMillis(year2);
        if (n4 >= 5097600000L) {
            if (this.isLeapYear(year2)) {
                if (!this.isLeapYear(year)) {
                    n4 -= 86400000L;
                }
            }
            else if (n3 >= 5097600000L && this.isLeapYear(year)) {
                n3 -= 86400000L;
            }
        }
        int n5 = year - year2;
        if (n3 < n4) {
            --n5;
        }
        return n5;
    }
    
    @Override
    long setYear(long yearMonthDayMillis, final int n) {
        final int year = this.getYear(yearMonthDayMillis);
        int dayOfYear = this.getDayOfYear(yearMonthDayMillis, year);
        final int millisOfDay = this.getMillisOfDay(yearMonthDayMillis);
        if (dayOfYear > 59) {
            if (this.isLeapYear(year)) {
                if (!this.isLeapYear(n)) {
                    --dayOfYear;
                }
            }
            else if (this.isLeapYear(n)) {
                ++dayOfYear;
            }
        }
        yearMonthDayMillis = this.getYearMonthDayMillis(n, 1, dayOfYear);
        yearMonthDayMillis += millisOfDay;
        return yearMonthDayMillis;
    }
    
    static {
        MIN_DAYS_PER_MONTH_ARRAY = new int[] { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        MAX_DAYS_PER_MONTH_ARRAY = new int[] { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        MIN_TOTAL_MILLIS_BY_MONTH_ARRAY = new long[12];
        MAX_TOTAL_MILLIS_BY_MONTH_ARRAY = new long[12];
        long n = 0L;
        long n2 = 0L;
        for (int i = 0; i < 11; ++i) {
            n += BasicGJChronology.MIN_DAYS_PER_MONTH_ARRAY[i] * 86400000L;
            BasicGJChronology.MIN_TOTAL_MILLIS_BY_MONTH_ARRAY[i + 1] = n;
            n2 += BasicGJChronology.MAX_DAYS_PER_MONTH_ARRAY[i] * 86400000L;
            BasicGJChronology.MAX_TOTAL_MILLIS_BY_MONTH_ARRAY[i + 1] = n2;
        }
    }
}
