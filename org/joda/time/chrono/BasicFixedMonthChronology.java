// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.Chronology;

abstract class BasicFixedMonthChronology extends BasicChronology
{
    private static final long serialVersionUID = 261387371998L;
    static final int MONTH_LENGTH = 30;
    static final long MILLIS_PER_YEAR = 31557600000L;
    static final long MILLIS_PER_MONTH = 2592000000L;
    
    BasicFixedMonthChronology(final Chronology chronology, final Object o, final int n) {
        super(chronology, o, n);
    }
    
    @Override
    long setYear(long yearMonthDayMillis, final int n) {
        int dayOfYear = this.getDayOfYear(yearMonthDayMillis, this.getYear(yearMonthDayMillis));
        final int millisOfDay = this.getMillisOfDay(yearMonthDayMillis);
        if (dayOfYear > 365 && !this.isLeapYear(n)) {
            --dayOfYear;
        }
        yearMonthDayMillis = this.getYearMonthDayMillis(n, 1, dayOfYear);
        yearMonthDayMillis += millisOfDay;
        return yearMonthDayMillis;
    }
    
    @Override
    long getYearDifference(final long n, final long n2) {
        final int year = this.getYear(n);
        final int year2 = this.getYear(n2);
        final long n3 = n - this.getYearMillis(year);
        final long n4 = n2 - this.getYearMillis(year2);
        int n5 = year - year2;
        if (n3 < n4) {
            --n5;
        }
        return n5;
    }
    
    @Override
    long getTotalMillisByYearMonth(final int n, final int n2) {
        return (n2 - 1) * 2592000000L;
    }
    
    @Override
    int getDayOfMonth(final long n) {
        return (this.getDayOfYear(n) - 1) % 30 + 1;
    }
    
    @Override
    boolean isLeapYear(final int n) {
        return (n & 0x3) == 0x3;
    }
    
    @Override
    int getDaysInYearMonth(final int n, final int n2) {
        return (n2 != 13) ? 30 : (this.isLeapYear(n) ? 6 : 5);
    }
    
    @Override
    int getDaysInMonthMax() {
        return 30;
    }
    
    @Override
    int getDaysInMonthMax(final int n) {
        return (n != 13) ? 30 : 6;
    }
    
    @Override
    int getMonthOfYear(final long n) {
        return (this.getDayOfYear(n) - 1) / 30 + 1;
    }
    
    @Override
    int getMonthOfYear(final long n, final int n2) {
        return (int)((n - this.getYearMillis(n2)) / 2592000000L) + 1;
    }
    
    @Override
    int getMaxMonth() {
        return 13;
    }
    
    @Override
    long getAverageMillisPerYear() {
        return 31557600000L;
    }
    
    @Override
    long getAverageMillisPerYearDividedByTwo() {
        return 15778800000L;
    }
    
    @Override
    long getAverageMillisPerMonth() {
        return 2592000000L;
    }
}
