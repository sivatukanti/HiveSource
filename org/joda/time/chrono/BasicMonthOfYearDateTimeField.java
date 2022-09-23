// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.chrono;

import org.joda.time.DurationField;
import org.joda.time.DateTimeField;
import org.joda.time.field.FieldUtils;
import org.joda.time.Chronology;
import org.joda.time.DateTimeUtils;
import org.joda.time.ReadablePartial;
import org.joda.time.DateTimeFieldType;
import org.joda.time.field.ImpreciseDateTimeField;

class BasicMonthOfYearDateTimeField extends ImpreciseDateTimeField
{
    private static final long serialVersionUID = -8258715387168736L;
    private static final int MIN = 1;
    private final BasicChronology iChronology;
    private final int iMax;
    private final int iLeapMonth;
    
    BasicMonthOfYearDateTimeField(final BasicChronology iChronology, final int iLeapMonth) {
        super(DateTimeFieldType.monthOfYear(), iChronology.getAverageMillisPerMonth());
        this.iChronology = iChronology;
        this.iMax = this.iChronology.getMaxMonth();
        this.iLeapMonth = iLeapMonth;
    }
    
    @Override
    public boolean isLenient() {
        return false;
    }
    
    @Override
    public int get(final long n) {
        return this.iChronology.getMonthOfYear(n);
    }
    
    @Override
    public long add(final long n, final int n2) {
        if (n2 == 0) {
            return n;
        }
        final long n3 = this.iChronology.getMillisOfDay(n);
        final int year = this.iChronology.getYear(n);
        final int monthOfYear = this.iChronology.getMonthOfYear(n, year);
        final int a = monthOfYear - 1 + n2;
        int n4;
        int n5;
        if (a >= 0) {
            n4 = year + a / this.iMax;
            n5 = a % this.iMax + 1;
        }
        else {
            n4 = year + a / this.iMax - 1;
            int iMax = Math.abs(a) % this.iMax;
            if (iMax == 0) {
                iMax = this.iMax;
            }
            n5 = this.iMax - iMax + 1;
            if (n5 == 1) {
                ++n4;
            }
        }
        int dayOfMonth = this.iChronology.getDayOfMonth(n, year, monthOfYear);
        final int daysInYearMonth = this.iChronology.getDaysInYearMonth(n4, n5);
        if (dayOfMonth > daysInYearMonth) {
            dayOfMonth = daysInYearMonth;
        }
        return this.iChronology.getYearMonthDayMillis(n4, n5, dayOfMonth) + n3;
    }
    
    @Override
    public long add(final long n, final long lng) {
        final int n2 = (int)lng;
        if (n2 == lng) {
            return this.add(n, n2);
        }
        final long n3 = this.iChronology.getMillisOfDay(n);
        final int year = this.iChronology.getYear(n);
        final int monthOfYear = this.iChronology.getMonthOfYear(n, year);
        final long a = monthOfYear - 1 + lng;
        long n4;
        long n5;
        if (a >= 0L) {
            n4 = year + a / this.iMax;
            n5 = a % this.iMax + 1L;
        }
        else {
            n4 = year + a / this.iMax - 1L;
            int iMax = (int)(Math.abs(a) % this.iMax);
            if (iMax == 0) {
                iMax = this.iMax;
            }
            n5 = this.iMax - iMax + 1;
            if (n5 == 1L) {
                ++n4;
            }
        }
        if (n4 < this.iChronology.getMinYear() || n4 > this.iChronology.getMaxYear()) {
            throw new IllegalArgumentException("Magnitude of add amount is too large: " + lng);
        }
        final int n6 = (int)n4;
        final int n7 = (int)n5;
        int dayOfMonth = this.iChronology.getDayOfMonth(n, year, monthOfYear);
        final int daysInYearMonth = this.iChronology.getDaysInYearMonth(n6, n7);
        if (dayOfMonth > daysInYearMonth) {
            dayOfMonth = daysInYearMonth;
        }
        return this.iChronology.getYearMonthDayMillis(n6, n7, dayOfMonth) + n3;
    }
    
    @Override
    public int[] add(final ReadablePartial readablePartial, final int n, final int[] array, final int n2) {
        if (n2 == 0) {
            return array;
        }
        if (readablePartial.size() > 0 && readablePartial.getFieldType(0).equals(DateTimeFieldType.monthOfYear()) && n == 0) {
            return this.set(readablePartial, 0, array, (readablePartial.getValue(0) - 1 + n2 % 12 + 12) % 12 + 1);
        }
        if (DateTimeUtils.isContiguous(readablePartial)) {
            long set = 0L;
            for (int i = 0; i < readablePartial.size(); ++i) {
                set = readablePartial.getFieldType(i).getField(this.iChronology).set(set, array[i]);
            }
            return this.iChronology.get(readablePartial, this.add(set, n2));
        }
        return super.add(readablePartial, n, array, n2);
    }
    
    @Override
    public long addWrapField(final long n, final int n2) {
        return this.set(n, FieldUtils.getWrappedValue(this.get(n), n2, 1, this.iMax));
    }
    
    @Override
    public long getDifferenceAsLong(final long n, long set) {
        if (n < set) {
            return -this.getDifference(set, n);
        }
        final int year = this.iChronology.getYear(n);
        final int monthOfYear = this.iChronology.getMonthOfYear(n, year);
        final int year2 = this.iChronology.getYear(set);
        final int monthOfYear2 = this.iChronology.getMonthOfYear(set, year2);
        long n2 = (year - year2) * (long)this.iMax + monthOfYear - monthOfYear2;
        final int dayOfMonth = this.iChronology.getDayOfMonth(n, year, monthOfYear);
        if (dayOfMonth == this.iChronology.getDaysInYearMonth(year, monthOfYear) && this.iChronology.getDayOfMonth(set, year2, monthOfYear2) > dayOfMonth) {
            set = this.iChronology.dayOfMonth().set(set, dayOfMonth);
        }
        if (n - this.iChronology.getYearMonthMillis(year, monthOfYear) < set - this.iChronology.getYearMonthMillis(year2, monthOfYear2)) {
            --n2;
        }
        return n2;
    }
    
    @Override
    public long set(final long n, final int n2) {
        FieldUtils.verifyValueBounds(this, n2, 1, this.iMax);
        final int year = this.iChronology.getYear(n);
        int dayOfMonth = this.iChronology.getDayOfMonth(n, year);
        final int daysInYearMonth = this.iChronology.getDaysInYearMonth(year, n2);
        if (dayOfMonth > daysInYearMonth) {
            dayOfMonth = daysInYearMonth;
        }
        return this.iChronology.getYearMonthDayMillis(year, n2, dayOfMonth) + this.iChronology.getMillisOfDay(n);
    }
    
    @Override
    public DurationField getRangeDurationField() {
        return this.iChronology.years();
    }
    
    @Override
    public boolean isLeap(final long n) {
        final int year = this.iChronology.getYear(n);
        return this.iChronology.isLeapYear(year) && this.iChronology.getMonthOfYear(n, year) == this.iLeapMonth;
    }
    
    @Override
    public int getLeapAmount(final long n) {
        return this.isLeap(n) ? 1 : 0;
    }
    
    @Override
    public DurationField getLeapDurationField() {
        return this.iChronology.days();
    }
    
    @Override
    public int getMinimumValue() {
        return 1;
    }
    
    @Override
    public int getMaximumValue() {
        return this.iMax;
    }
    
    @Override
    public long roundFloor(final long n) {
        final int year = this.iChronology.getYear(n);
        return this.iChronology.getYearMonthMillis(year, this.iChronology.getMonthOfYear(n, year));
    }
    
    @Override
    public long remainder(final long n) {
        return n - this.roundFloor(n);
    }
    
    private Object readResolve() {
        return this.iChronology.monthOfYear();
    }
}
